package psdprocessing;

import java.util.ArrayList;
import java.util.HashMap;

import psd.model.Layer;
import psd.model.Psd;
import psd.parser.layer.LayerType;

public class LayerProcessor {

	/**
	 * psd file itself
	 */
	private Psd psd;
	
	/**
	 * list of all layers we will be processing in our document
	 */
	private ArrayList<Layer> layers = null;
	
	/**
	 * list of layers to remove from psd
	 */
	private ArrayList<Layer> layersToRemove = null;
	
	/**
	 * unique id counter, so we can assign a unique layer id to every layer
	 */
	private int layerId = 0;
	
	public int getLayerId() {
		return layerId;
	}

	public void setLayerId(int layerId) {
		this.layerId = layerId;
	}
	
	public int nextLayerId() {
		this.layerId++;
		return this.layerId;
	}

	/**
	 * maximum layer depth
	 */
	private int maximumLayerDepth = 0;
	
	/**
	 * 
	 * @param psd PSD file to process
	 */
	public LayerProcessor(Psd psd) {
		this.psd = psd;
		this.process();
	}
	
	public void process()
	{
		/**
		 * Array list of all layers
		 */
		this.layers = new ArrayList<Layer>();
		
		/**
		 * list of layers to remove
		 */
		this.layersToRemove = new ArrayList<Layer>();
		
		/**
		 * find all layers we need to process in the document
		 */
		this.allLayers();
		
		/**
		 * we'll remove all layers which are unused in psd
		 */
		this.removeUnusedLayers();
		
//		this.discoverMaximumLayerDepth();
		
		/*this.showLayersInformation(this.layers);
		System.exit(0)*/;
	}

	private void removeUnusedLayers() {
		
		if (this.layersToRemove.size() > 0) {
			for (Layer layer : this.layersToRemove) {
				this.psd.removeLayer(layer);
			}
		}
		
	}

	/**
	 * get all layers that are images
	 * @return ArrayList<Layer> list of layers that will be used to generate images
	 */
	public ArrayList<Layer> getImageLayers() {
		ArrayList<Layer> layers = new ArrayList<Layer>();
		
		Layer layer;
		
		for (int i = 0; i < this.layers.size(); i++) {
			layer = this.layers.get(i);
			
			if (
					(
						layer.getType() == LayerType.OPEN_FOLDER ||
						layer.getType() == LayerType.CLOSED_FOLDER
					) 
					
					||
					
					layer.isTextLayer() ||
					
					(
						layer.getWidth() == 1 ||
						layer.getHeight() == 1
					)
					
					||
					
					layer.isShapeLayer()
					
					||
					
					layer.isImageSingleColored()
			) {
				continue;
			}
			
			layers.add(layer);
		}
		
		return layers;
	}

	public ArrayList<Layer> getLayersForGroup(int groupId)
	{
		Layer layer;
		ArrayList<Layer> layers = new ArrayList<Layer>();
		
		for (int i = 0; i < this.layers.size(); i++) {
			layer = this.layers.get(i);
			
			if (layer.getGroupLayerId() == groupId) {
				layers.add(layer);
			}
		}
		
		return layers;
	}
	
	public void showLayersInformation(ArrayList<Layer> layers)
	{
		Layer l;
		
		for (int i = 0; i < layers.size(); i++) {
			l = layers.get(i);
			
			for (int j = 0; j < l.getDepth(); j++) {
				System.out.print("--");
			}
			
			System.out.print("#" + l.getUniqueLayerId() +
							" > " + l.toString() +
							" ; Type = " + l.getType() +
							" ; Group id = " + l.getGroupLayerId() + 
							" ; Children count = "+ l.getLayersCount() +
							" ; Depth = " + l.getDepth() +
							" ; ID name = " + l.fileSavingName());
			System.out.println();
		}
	}
	
	/**
	 * discover how deep the layer goes, what's the maximum depth of the layer in a group
	 */
	public int discoverMaximumLayerDepth() {
		if (this.layers.size() > 0) {
			int layerDepth;
			
			for (int i = 0; i < this.layers.size(); i++) {
				layerDepth = this.layers.get(i).getDepth();
				this.maximumLayerDepth = layerDepth > this.maximumLayerDepth ? layerDepth : this.maximumLayerDepth;
			}
		}
		
		return this.maximumLayerDepth;
	}

	public void allLayers()
	{
		/**
		 * 
		 * There are two types of layers,
		 * 	- layers
		 * 	- layers in groups
		 * 
		 * we will process them both, but we will not process
		 * layers that are in-visible as those layers will not be used
		 * for psd to html conversion
		 */
		int totalLayers = this.psd.getLayersCount();
		
		Layer layer;
		
		for (int i = totalLayers - 1; i >= 0; i--) {
			layer = this.psd.getLayer(i);
			
			if (!layer.isVisible() || (layer.getType() == LayerType.NORMAL && layer.getWidth() == 0)) {
				this.layersToRemove.add(layer);
				continue;
			}
			
			layer.adjustPositionAndSizeInformation();
			this.layerId++;
			
			if (LayerType.NORMAL == layer.getType()) {
				
				layer.setUniqueLayerId(this.layerId);
				layer.setGroupLayerId(0);
				layer.setDepth(0);
				
				this.layers.add(layer);
				
			} else if (LayerType.OPEN_FOLDER == layer.getType() || LayerType.CLOSED_FOLDER == layer.getType()) {
				
				layer.setUniqueLayerId(this.layerId);
				layer.setGroupLayerId(0);
				layer.setDepth(0);
				
				this.layers.add(layer);
				
				if (layer.getLayersCount() > 0) {
					this.subLayers(layer, this.layerId, 1);
				}
			}
		}
	}
	
	/*
	 * process layers inside a group
	 */
	private void subLayers(Layer layer, int layerGroupId, int depth)
	{
		Layer subLayer;
		int totalLayers = layer.getLayersCount();
		
		for (int i = totalLayers - 1; i >= 0; i--) {
			subLayer = layer.getLayer(i);
			
			if (!subLayer.isVisible() || (subLayer.getType() == LayerType.NORMAL && subLayer.getWidth() == 0)) {
				this.layersToRemove.add(subLayer);
				continue;
			}
			
			subLayer.adjustPositionAndSizeInformation();
			this.layerId++;
			
			if (LayerType.NORMAL == subLayer.getType()) {
				
				subLayer.setUniqueLayerId(this.layerId);
				subLayer.setGroupLayerId(layerGroupId);
				subLayer.setDepth(depth);
				
				this.layers.add(subLayer);
				
			} else if (LayerType.OPEN_FOLDER == subLayer.getType() || LayerType.CLOSED_FOLDER == subLayer.getType()) {
				
				subLayer.setUniqueLayerId(this.layerId);
				subLayer.setGroupLayerId(layerGroupId);
				subLayer.setDepth(depth);
				
				this.layers.add(subLayer);
				
				if (subLayer.getLayersCount() > 0) {
					this.subLayers(subLayer, this.layerId, depth + 1);
				}
				
			}
		}
	}
	
	public ArrayList<Layer> getLayers() {
		return layers;
	}

	public HashMap<Integer,ArrayList<Layer>> getLayersInGroupsAtDepth(int queryLayerDepth) {
		/**
		 * we will return layers as group
		 * 
		 * if depth == 0 we can return all layers just as an array at depth == 0
		 * 
		 * if depth > 0 then we must return all layers as 
		 * 	layer id (parent id of layer) -> group of layers inside layer id
		 */
		HashMap<Integer, ArrayList<Layer>> layerInGroups = new HashMap<Integer, ArrayList<Layer>>();
		
		if (this.layers.size() > 0) {
			
			int layerDepth, groupId;
			Layer layer;
			
			
			for (int i = 0; i < this.layers.size(); i++) {
				layer = this.layers.get(i);
				
				groupId = layer.getGroupLayerId();
				layerDepth = layer.getDepth();
				
				if (layerDepth == queryLayerDepth) {
					if (!layerInGroups.containsKey(groupId)) {
						layerInGroups.put(groupId, new ArrayList<Layer>());
					}
					
					layerInGroups.get(groupId).add(layer);
				}
			}
		}
		
		/*System.out.println("Layer/Group Depth:" + queryLayerDepth);
		System.out.println(layerInGroups);
		System.out.println(String.format(String.format("%%%ds", layerInGroups.toString().length()), " ").replace(" ","-"));*/
		
		return layerInGroups;
	}

}
