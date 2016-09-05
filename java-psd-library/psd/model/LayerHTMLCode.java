package psd.model;

public class LayerHTMLCode {

	public static String get(Layer layer) {
		
		String htmlTagName = layer.getHtmlTagName(),
				htmlClassName = layer.getHtmlClassName(),
				htmlImageSrc = layer.getHtmlImageSrc(),
				htmlText = layer.getHtmlText();
		
		StringBuilder html = new StringBuilder();
		
		switch (htmlTagName)
		{
			case "img":
				html.append(String.format("<%s class=\"%s\" src=\"%s\" />", htmlTagName, htmlClassName, htmlImageSrc));
				break;
			
			case "div":
				
				html.append(String.format("<div class=\"%s\">", htmlClassName));
				
				if (layer.isTextLayer()) {
					html.append(htmlText);
				}
				
				html.append("</div>");
				
				break;
		}
		
		return html.toString();
	}

	public static String getFillable(Layer layer) {
		String htmlTagName = layer.getHtmlTagName(),
				htmlClassName = layer.getHtmlClassName(),
				htmlImageSrc = layer.getHtmlImageSrc(),
				htmlText = layer.getHtmlText();
		
		StringBuilder html = new StringBuilder();
		
		switch (htmlTagName)
		{
			case "div":
				html.append(String.format("<%s class=\"%s\">%%s</div>", htmlTagName, htmlClassName, htmlImageSrc));
				break;
		}
		
		return html.toString();
	}
	
}
