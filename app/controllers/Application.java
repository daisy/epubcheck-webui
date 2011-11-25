package controllers;

import play.*;
import play.mvc.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import models.*;

public class Application extends Controller {

	public static void index() {
		
		String cmd = Play.applicationPath+"/bin/epubcheck/epubcheck "+Play.applicationPath+"/example-epub/9780316000000_MobyDick_r6.epub";
		List<String> out = new ArrayList<String>();
		try {
			String line;
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((line = bri.readLine()) != null) {
				out.add(line);
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				out.add(line);
			}
			bre.close();
			p.waitFor();
		}
		catch (Exception err) {
			err.printStackTrace();
		}

		List<Map<String,String>> results = new ArrayList<Map<String,String>>();
		for (String line : out) {
			String[] parts = line.split(":");
			if (parts.length >= 3) {
				String type = parts[0];
				String file = parts[1];
				String lineNr = "";
				String position = "";
				if (file.matches(".*\\(\\d*,\\d*\\)$")) {
					lineNr = file.replaceAll("^.*\\((\\d*),\\d*\\)$", "$1");
					position = file.replaceAll("^.*\\(\\d*,(\\d*)\\)$", "$1");
					file = file.replaceAll("\\(\\d*,\\d*\\)$","");
				}
				String message = line.replaceAll("^[^:]*:[^:]*:", "");
				
				Map<String,String> result = new HashMap<String,String>();
				result.put("type", type);
				result.put("file", file);
				result.put("lineNr", lineNr);
				result.put("position", position);
				result.put("message", message);
				results.add(result);
			}
		}

		renderArgs.put("results", results);

		render();
	}

}