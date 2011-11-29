package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.daisy.validation.epubcheck.EpubcheckBackend;
import org.daisy.validation.epubcheck.EpubcheckBackend.Issue;

import play.mvc.Controller;

public class Application extends Controller {

	public static void index() {
		final List<Issue> issues = EpubcheckBackend
				.run("example-epub/9780316000000_MobyDick_r6.epub");

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
		renderArgs.put("results", results);
		render();
	}
}