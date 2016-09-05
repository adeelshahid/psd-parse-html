package htmlcode;

import psd.model.Layer;

public class HTMLShape {

	public static void generate(Layer layer) {
		
		layer.setHtmlTagName("div");
		layer.setHtmlClassName(layer.fileSavingName());
		
	}

}
