package htmlcode;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Random;

import psd.model.Layer;
import psd.parser.layer.LayerType;

public class HTMLCSS {

	public static void generate(Layer layer) {
		HTMLCSS.generateDebuggableCSS(layer);
	}
	
	public static void generateDebuggableCSS(Layer layer)
	{
		Rectangle rect = layer.getOverlayedRectangle();
		
		int left = rect.x, top = rect.y, width = rect.width, height = rect.height;
		
		HashMap<String, String> cssMap = new HashMap<String, String>();
		
		cssMap.put("position", "fixed");
		
		cssMap.put("left", left + "px");
		cssMap.put("top", top + "px");
		cssMap.put("width", width + "px");
		cssMap.put("height", height + "px");
		
		// Java 'Color' class takes 3 floats, from 0 to 1.
		Random rand = new Random();
		
		Color randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
		
		cssMap.put("background-color", "" + Integer.toHexString(randomColor.getRGB()).substring(2));
		cssMap.put("background-color", String.format("rgba(%d, %d, %d, %.2f)", 
					randomColor.getRed(), randomColor.getGreen(), randomColor.getBlue(), randomColor.getAlpha() / 255.0 ));
		
		randomColor = randomColor.darker();
		
		cssMap.put("border", String.format("1px solid #%s", Integer.toHexString(randomColor.getRGB()).substring(2)));
		
		if (layer.fileSavingName().contains("group")) {
			layer.setCssStyles(cssMap);	
		}
	}

}
