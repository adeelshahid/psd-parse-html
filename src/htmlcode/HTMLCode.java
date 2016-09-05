package htmlcode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;

public class HTMLCode {

	public static void saveHTML(String html, String projectPath) {
		
		PrintWriter writer;
		
		try {
			
			String htmlTemplate = HTMLCode.getHTMLTemplate(),
					headTemplate = HTMLCode.getHeadTemplate(),
					stylesheetTemplate = HTMLCode.getStylesheetTemplate();
			
			htmlTemplate = htmlTemplate.replace("{{HEAD}}", headTemplate);
			
			htmlTemplate = htmlTemplate.replace("{{TITLE}}", "My Website");
			htmlTemplate = htmlTemplate.replace("{{STYLESHEETS}}", String.format(stylesheetTemplate, "resources/css/style.css"));
			
			htmlTemplate = htmlTemplate.replace("{{META_TAGS}}", "");
			htmlTemplate = htmlTemplate.replace("{{SCRIPTS}}", "");
			
			htmlTemplate = htmlTemplate.replace("{{BODY}}", html);
			
			Document doc = Jsoup.parse(htmlTemplate);
			
			Document.OutputSettings outputSettings = new OutputSettings();
			outputSettings.indentAmount(4);
			outputSettings.prettyPrint(true);
			
			doc.outputSettings(outputSettings);
			
			String indexHTML = projectPath + "/index.html";
			
			writer = new PrintWriter(indexHTML, "UTF-8");			
			
			writer.println(doc.html());
			writer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

	private static String getHTMLTemplate() {
		return HTMLResources.get("/resources/snippets/html");
	}
	
	private static String getHeadTemplate() {
		return HTMLResources.get("/resources/head/head");
	}
	
	private static String getStylesheetTemplate() {
		return HTMLResources.get("/resources/snippets/stylesheet");
	}

	public static void saveCSS(String css, String projectPath) {

		PrintWriter writer;
		
		try {
			
			String cssTemplate = HTMLCode.getCSSTemplate();
			
			String normalizationStyles = HTMLResources.get("/resources/snippets/normalization-styles");
			
			cssTemplate = cssTemplate.replace("{{NORMALIZATION}}", normalizationStyles);
			cssTemplate = cssTemplate.replace("{{WEBPAGE_STYLES}}", css);
			
			String cssFile = projectPath + "/resources/css/style.css";
			
			writer = new PrintWriter(cssFile, "UTF-8");
			writer.println(cssTemplate);
			writer.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getCSSTemplate() {
		return HTMLResources.get("/resources/snippets/css");
	}

}
