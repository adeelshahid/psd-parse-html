package layerstructure;

import htmlcode.HTMLCSS;
import htmlcode.HTMLFolder;
import htmlcode.HTMLImage;
import htmlcode.HTMLShape;
import htmlcode.HTMLText;

import java.util.ArrayList;

import psd.model.Layer;
import psd.parser.layer.LayerType;

public class LayerCode {

	public static void generate(Layer layer) {
		
		// TODO, change this method to generate as well to accumulate code for generation of Image 
		
		if (layer.isTextLayer()) {
			
			HTMLText.generate(layer);
			
		} else if (layer.isShapeLayer()) {
			
			HTMLShape.generate(layer);
			
		} else if (layer.hasImage()) {
			
			HTMLImage.generate(layer);
			
		} else if (layer.getType() == LayerType.OPEN_FOLDER || layer.getType() == LayerType.CLOSED_FOLDER) {
			
			HTMLFolder.generate(layer);
			
		} else if (layer.isImageSingleColored()) {
			
			HTMLShape.generate(layer);
			
		} else {
		
			System.out.println("unknown layer");
			System.out.println(layer.getWidth() + " x " + layer.getHeight());
			System.out.println(layer);
			System.exit(0);
			
		}
		
		HTMLCSS.generate(layer);
	}

	public static void generate(ArrayList<Layer> layers) {
		
		/**
		 * we need to iterate over layers and process them one by one 
		 */
		
		for (Layer layer : layers) {
			LayerCode.generate(layer);
		}
	}

}
