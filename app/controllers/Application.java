package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.daisy.validation.epubcheck.EpubcheckBackend;
import org.daisy.validation.epubcheck.EpubcheckBackend.Issue;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import play.Logger;
import play.data.FileUpload;
import play.libs.Codec;
import play.mvc.Controller;

public class Application extends Controller {

	private static final String RESULTS = "results";
	private static final String FILENAME = "filename";
	private static final String FILEERROR = "fileError";

	private static final Function<Issue, Map<String, String>> issueToMap = new Function<EpubcheckBackend.Issue, Map<String, String>>() {
		public Map<String, String> apply(Issue issue) {
			Map map = Maps.newHashMapWithExpectedSize(5);
			map.put("type", issue.type);
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
		List<Map<String, String>> results = null;
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
		return Lists.transform(EpubcheckBackend.run(file), issueToMap);
	}

}