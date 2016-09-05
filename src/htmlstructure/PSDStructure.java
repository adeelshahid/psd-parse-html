package htmlstructure;

import java.util.ArrayList;
import java.util.HashMap;

import psd.model.Layer;
import psdprocessing.LayerProcessor;

public class PSDStructure {
	
	private LayerProcessor layerProcessor;
	
	private HashMap<String, String> html;

	public PSDStructure(LayerProcessor layerProcessor) {
		
		this.layerProcessor = layerProcessor;
		
	}

	public ArrayList<HashMap<Integer,ArrayList<Layer>>> get() {
		ArrayList<Layer> layers = this.layerProcessor.getLayers();
		ArrayList<HashMap<Integer,ArrayList<Layer>>> layersByDepth = new ArrayList<HashMap<Integer,ArrayList<Layer>>>();
		
		/**
		 * we should accumulate all the HTML code into top layers HashMap
		 */
		if (layers.size() > 0) {
			int maxDepth = layerProcessor.discoverMaximumLayerDepth();
			
			for (int i = maxDepth; i >= 0; i--) {
				layersByDepth.add(layerProcessor.getLayersInGroupsAtDepth(i));
			}
		}
		
		return layersByDepth;
	}
}
