package controllers;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.daisy.validation.epubcheck.EpubcheckBackend;
import org.daisy.validation.epubcheck.Issue;
import org.daisy.validation.epubcheck.Issue.Type;

import play.Logger;
import play.data.FileUpload;
import play.libs.Codec;
import play.mvc.Controller;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Application extends Controller {
	private static final String RESULTS = "results";
	private static final String VERSION = "version";
	private static final String FILENAME = "filename";
	private static final String FILEERROR = "fileError";

	private static final Function<Issue, Map<String, String>> issueToMap = new Function<Issue, Map<String, String>>() {
		public Map<String, String> apply(Issue issue) {
			Map<String, String> map = Maps.newHashMapWithExpectedSize(5);
			map.put("type", issue.type.toString());
			map.put("file", issue.file);
			map.put("lineNr", Integer.toString(issue.lineNo));
			map.put("position", Integer.toString(issue.colNo));
			map.put("message", issue.txt);
			return Collections.unmodifiableMap(map);
		}
	};

	public static void index() {
		render();
	}

	public static void validate(final FileUpload input_file) {
		Iterable<Map<String, String>> results = null;
		String filename = null;
		boolean fileError = input_file == null;
		if (!fileError) {
			final String origFileName = input_file.getFileName();
			filename = new File(origFileName).getName();
			final File newFile = input_file.asFile(new File(play.Play.tmpDir,
					Codec.UUID() + ".epub"));
			results = runEpubcheck(newFile.getPath());
			final boolean deleted = newFile.delete();
			if (!deleted) {
				Logger.warn("deletion of file failed: %s", newFile);
			}
		}
		renderArgs.put(FILEERROR, fileError);
		renderArgs.put(RESULTS, results);
		renderArgs.put(FILENAME, filename);
		render();
	}

	private static List<Map<String, String>> runEpubcheck(final String file) {
		List<Issue> results = EpubcheckBackend.run(file);
		if (results.get(0).type == Type.VERSION) {
			renderArgs.put(VERSION, results.get(0).txt);
		}
		return Collections.unmodifiableList(Lists.newArrayList(Collections2
				.transform(Collections2.filter(results, new Predicate<Issue>() {
					public boolean apply(Issue issue) {
						return issue.type != Type.VERSION;
					}
				}), issueToMap)));
	}
}