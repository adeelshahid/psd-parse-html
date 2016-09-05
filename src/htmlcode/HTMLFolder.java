package htmlcode;

import psd.model.Layer;

public class HTMLFolder {

	public static void generate(Layer layer) {
		
		layer.setHtmlTagName("div");
		layer.setHtmlClassName(layer.fileSavingName());
	}

}
