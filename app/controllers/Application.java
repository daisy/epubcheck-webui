package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.daisy.validation.epubcheck.EpubcheckBackend;
import org.daisy.validation.epubcheck.EpubcheckBackend.Issue;

import play.data.Upload;
import play.mvc.Controller;

public class Application extends Controller {
	
	private static String TMPDIR = "/tmp/epubcheck/";

	public static void index() {
		render();
	}
	public static void validate(File input_file) {
		List<Map<String, String>> results = runEpubcheck(input_file.getPath());
		renderArgs.put("results", results);
		render();
	}
	
	private static List<Map<String, String>> runEpubcheck(String file) {
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