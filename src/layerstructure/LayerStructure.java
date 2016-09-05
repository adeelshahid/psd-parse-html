package layerstructure;

import htmlstructure.LayerSorter;

import java.util.ArrayList;

import psd.model.Layer;

public class LayerStructure {

	public static void process(ArrayList<Layer> layers) {
		
		if (layers.size() > 1) {
			ArrayList<Integer> layerIds = LayerSorter.sort(layers);
			layers = LayerSorter.sortByIds(layerIds, layers);
		}
		
		/**
		 * process layers one by one
		 */
		
		LayerCode.generate(layers);
	}

}
