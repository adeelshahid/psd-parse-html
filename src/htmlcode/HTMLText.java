package htmlcode;

import psd.model.Layer;

public class HTMLText {

	public static void generate(Layer layer) {
		
		layer.setHtmlTagName("div");
		layer.setHtmlClassName(layer.fileSavingName());
		layer.setHtmlText(layer.getText());
		
	}

}
