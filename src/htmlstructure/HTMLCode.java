package htmlstructure;

import java.util.ArrayList;

import layerstructure.LayerStructure;
import psd.model.Layer;

public class HTMLCode {

	public static void generate(ArrayList<Layer> layers) {
		LayerStructure.process(layers);
	}

}
