package htmlcode;

import psd.model.Layer;

public class HTMLImage {

	public static void generate(Layer layer) {
		
		int width = layer.getWidth(),
			height = layer.getHeight();
		
		/**
		 * we treat this as a background css image
		 */
		/*if (width >= 100 && height >= 100) {
			
			HTMLShape.generate(layer);
			
		} else {
			
			
			
		}*/
		
		String imagePath = layer.getFilePath();
		
		/**
		 * to make image paths relatives we remove the prefixed /
		 */
		imagePath = imagePath.substring(imagePath.indexOf("/resources") + 1);
		
		layer.setHtmlTagName("img");
		layer.setHtmlClassName(layer.fileSavingName());
		layer.setHtmlImageSrc(imagePath);
	}

}
