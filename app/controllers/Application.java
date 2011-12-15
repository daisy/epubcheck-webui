package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.daisy.validation.epubcheck.EpubcheckBackend;
import org.daisy.validation.epubcheck.EpubcheckBackend.Issue;

import play.Logger;
import play.data.FileUpload;
import play.libs.Codec;
import play.mvc.Controller;

public class Application extends Controller {
	public static void index() {
		render();
	}

	private static final String RESULTS = "results";
	private static final String FILENAME = "filename";
	private static final String FILEERROR = "fileError";
	
	public static void validate(final FileUpload input_file) {
		List<Map<String, String>> results = null;
		String filename = null;
		boolean fileError = false;
		if (input_file == null) {
			fileError = true;
		}
		else {
			final String origFileName = input_file.getFileName();
			filename = new File(origFileName).getName();
			final File newFile = input_file.asFile(new File(play.Play.tmpDir, Codec
					.UUID() + ".epub"));
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
		final List<Issue> issues = EpubcheckBackend.run(file);
		final List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		for (final Issue issue : issues) {
			final Map<String, String> result = new HashMap<String, String>();
			result.put("type", issue.type);
			result.put("file", issue.file);
			result.put("lineNr", Integer.toString(issue.lineNo));
			result.put("position", Integer.toString(issue.colNo));
			result.put("message", issue.txt);
			results.add(result);
		}
		return results;
	}
}