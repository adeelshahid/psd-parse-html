package layerstructure;

import htmlstructure.LayerSorter;
import htmlstructure.PSDRectangle;
import htmlstructure.PSDRectangleComparator;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import psd.model.Layer;
import psdprocessing.LayerProcessor;

public class LayerContainmentCase {

	public static HashMap<Integer, ArrayList<Layer>> process(HashMap<Integer, ArrayList<Layer>> hashMap) {
		
		ArrayList<Layer> layers;
		HashMap<Integer, ArrayList<Layer>> containmentLayers = new HashMap<Integer, ArrayList<Layer>>(), temp;
		
		Integer layerId;
		
		for (Integer key : hashMap.keySet()) {
			layers = hashMap.get(key);
			
			if (LayerContainmentCase.hasContainmentCase(layers)) {
				temp = LayerContainmentCase.extractContainmentCases(layers);
				
				/**
				 * do something with containment layers you have found
				 */
				if (!temp.isEmpty()) {
					
					for (Integer tempHashKey: temp.keySet()) {
						containmentLayers.put(tempHashKey, temp.get(tempHashKey));
					}
				}
				
				temp = null;
			}
		}
		
		return containmentLayers;
	}

	private static boolean hasContainmentCase(ArrayList<Layer> layers) {
		
		/**
		 * sort layers by left, top
		 * see if layer 1 contains layer 2 etc.
		 */
		ArrayList<Integer> layerIds = LayerSorter.sort(layers);
		layers = LayerSorter.sortByIds(layerIds, layers);
		
		/**
		 * we don't care about single or multiple containment case here just that 
		 * there is a containment that is present. such that Layer 1 contains Layer 2 etc. 
		 */
		Rectangle r1, r2;
		int numberOfLayers = layers.size();
		
		for (int i = 0; i < numberOfLayers -1 ; i++) {
			r1 = layers.get(i).getRectangle();
			
			for (int j = 0; j < numberOfLayers; j++) {
				r2 = layers.get(j).getRectangle();
				
				if (r1.contains(r2)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static HashMap<Integer, ArrayList<Layer>> extractContainmentCases(ArrayList<Layer> layers)
	{
		/**
		 * sort layers by left, top
		 * see if layer 1 contains layer 2 etc.
		 */
		ArrayList<Integer> layerIds = LayerSorter.sort(layers);
		layers = LayerSorter.sortByIds(layerIds, layers);
		
		/**
		 * we don't care about single or multiple containment case here just that 
		 * there is a containment that is present. such that Layer 1 contains Layer 2 etc. 
		 */
		Layer l1, l2;
		Rectangle r1, r2;
		
		int numberOfLayers = layers.size();
		
		Boolean containmentCaseFound;
		HashMap<Integer, ArrayList<Layer>> containmentLayers = new HashMap<Integer, ArrayList<Layer>>();
		
		int tempCounter = -1;
		
		for (int i = 0; i < numberOfLayers -1 ; i++) {
			containmentCaseFound = false;
			
			l1 = layers.get(i);
			r1 = l1.getRectangle();
			
			for (int j = i + 1; j < numberOfLayers; j++) {
				
				l2 = layers.get(j);
				r2 = l2.getRectangle();
				
				if (r1.contains(r2)) {
					containmentCaseFound = true;
					
					if (!containmentLayers.containsKey(l2.getUniqueLayerId())) {
						containmentLayers.put(l1.getUniqueLayerId(), new ArrayList<Layer>());
						containmentLayers.get(l1.getUniqueLayerId()).add(l1);
					}
					
					containmentLayers.get(l1.getUniqueLayerId()).add(l2);
					tempCounter = j;
					
				} else if (containmentCaseFound) {
					tempCounter = j;
					break;
				}
			}
			
			if (containmentCaseFound) {
				containmentCaseFound = false;
				i = tempCounter;
			}
		}
		
		return containmentLayers;
	}

	public static void insertItems(int depth,
			HashMap<Integer, ArrayList<Layer>> containedLayers,
			ArrayList<HashMap<Integer, ArrayList<Layer>>> layersByDepth) {
		
		HashMap<Integer, ArrayList<Layer>> removeLayerHashMap = new HashMap<Integer, ArrayList<Layer>>();
		ArrayList<Layer> layersToRemove = new ArrayList<Layer>();
		
		/**
		 * get all layers we would need to remove, basically it's all layers in ArrayList with index (0) + 1
		 */
		for (Integer k : containedLayers.keySet()) {
			for (int i = 1; i < containedLayers.get(k).size(); i++) {
				layersToRemove.add(containedLayers.get(k).get(i));
			}
		}
		
		ArrayList<Layer> layers;
		
		/**
		 * find groupIds => and corresponding layers to remove from layersByDepth
		 */
		for (Integer k : layersByDepth.get(depth).keySet()) {
			layers = layersByDepth.get(depth).get(k);
			
			for (int i = 0; i < layers.size(); i++) {
				if (layersToRemove.contains(layers.get(i))) {
					
					if (removeLayerHashMap.get(k) == null) {
						removeLayerHashMap.put(k, new ArrayList<Layer>());
					}
					
					removeLayerHashMap.get(k).add(layers.get(i));
				}
			}
		}
		
		/**
		 * let's remove layers inside the hashMap based on : removeLayerHashMap
		 */
		Integer newDepth = 0;
		for (Integer k : removeLayerHashMap.keySet()) {
			layers = removeLayerHashMap.get(k);
			newDepth = layers.get(0).getDepth();
			layersByDepth.get(depth).get(k).removeAll(layers);
		}
		
		newDepth++;
		
		if (newDepth >= layersByDepth.size()) {
			/**
			 * because of the layersByDepth sort order we have to use `depth` here
			 */
			layersByDepth.add(depth, new HashMap<Integer, ArrayList<Layer>>());
		}
		
		/**
		 * find relationship between removedLayerHashMapKey and containedLayers HashMap keys
		 */
		HashMap<Integer, Integer> relationship = new HashMap<Integer, Integer>();
		for (Integer k : removeLayerHashMap.keySet()) {
			layers = removeLayerHashMap.get(k);
			
			for (Integer k1 : containedLayers.keySet()) {
				
				for (int i = 0; i < containedLayers.get(k1).size(); i++) {
					if (layers.contains(containedLayers.get(k1).get(i))) {
						relationship.put(k, k1);
					}
				}
			}
		}
		
		for (Integer k : removeLayerHashMap.keySet()) {
			layers = removeLayerHashMap.get(k);
			
			for (int i = 0; i < layers.size(); i++) {
				layers.get(i).setDepth( layers.get(i).getDepth() + 1 );
				layers.get(i).setGroupLayerId(relationship.get(k));
			}
			
			layersByDepth.get(depth).put(relationship.get(k), layers);
		}
	}

}
