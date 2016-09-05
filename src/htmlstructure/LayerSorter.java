package htmlstructure;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import psd.model.Layer;

public class LayerSorter {

	/**
	 * sort structure inside out
	 * sort by keys, sort by layers inside keys
	 */
	public static ArrayList<Integer> sort(
			HashMap<Integer,ArrayList<Layer>> hashMap) {
		ArrayList<PSDRectangle> rectangles;
		
		rectangles = LayerSorter.getRectangles(hashMap);
		
		Collections.sort(rectangles, new PSDRectangleComparator());
		
		return PSDRectangle.getKeys(rectangles);
	}
	
	public static ArrayList<Integer> sort(ArrayList<Layer> layers) {

		ArrayList<PSDRectangle> rectangles;
		
		rectangles = LayerSorter.getRectangles(layers);
		
		Collections.sort(rectangles, new PSDRectangleComparator());
		
		return PSDRectangle.getKeys(rectangles);
	}
	
	private static ArrayList<Integer> ExtractKeys(ArrayList<PSDRectangle> rectangles) {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		
		for (PSDRectangle rect : rectangles) {
			keys.add(rect.getKey());
		}
		
		return keys;
	}

	public static ArrayList<PSDRectangle> getRectangles(HashMap<Integer, ArrayList<Layer>> hashMap)
	{
		PSDRectangle rect;
		ArrayList<Layer> layers;
		
//		TODO we have to think about groups and find out their width and height
		
		ArrayList<PSDRectangle> rectangles = new ArrayList<PSDRectangle>();
		
		for (Integer key : hashMap.keySet()) {
			layers = hashMap.get(key);
			
			rect = LayerSorter.getRectangle(layers);
			rect.setKey(key);
			
			rectangles.add(rect);
		}
		
		return rectangles;
	}
	
	public static ArrayList<PSDRectangle> getRectangles(ArrayList<Layer> layers)
	{
		PSDRectangle psdRect;
		Rectangle rect;
		ArrayList<PSDRectangle> rectangles = new ArrayList<PSDRectangle>();
		
		for (Layer layer : layers) {
			rect = layer.getOverlayedRectangle();
			
			psdRect = new PSDRectangle(rect);
			psdRect.setKey(layer.getUniqueLayerId());
			
			rectangles.add(psdRect);
		}
		
		return rectangles;
	}
	
	private static PSDRectangle getRectangle(Layer layer) {
		PSDRectangle rect = new PSDRectangle(layer.getRectangle());
		
		rect.setKey(layer.getLayerId());
		
		return rect;
	}

	public static PSDRectangle getRectangle(ArrayList<Layer> layers)
	{
		int minX, maxX, minY, maxY;
		
		minX = minY = Integer.MAX_VALUE; 
		maxX = maxY = Integer.MIN_VALUE;
		
		Rectangle rect;
		
		if (layers.size() == 1) {
			
			return new PSDRectangle(layers.get(0).getOverlayedRectangle());
			
		} else if (layers.size() > 1) {
			
			int layerX1, layerX2, layerY1, layerY2;
			
			minX = minY = Integer.MAX_VALUE;
			maxX = maxY = Integer.MIN_VALUE;
			
			for (Layer layer : layers) {
				
				rect = layer.getOverlayedRectangle();
				
				layerX1 = rect.x;
				layerY1 = rect.y;
				
				layerX2 = layerX1 + rect.width;
				layerY2 = layerY1 + rect.height;
				
				minX = minX > layerX1 ? layerX1 : minX;
				minY = minY > layerY1 ? layerY1 : minY;
				
				maxX = maxX < layerX2 ? layerX2 : maxX;
				maxY = maxY < layerY2 ? layerY2 : maxY;
			}
			
		}
		
		return new PSDRectangle(minX, minY, maxX - minX, maxY - minY);
	}

	public static ArrayList<Layer> sortByIds(ArrayList<Integer> layerIds,
			ArrayList<Layer> layers) {
		
		ArrayList<Layer> sortedList = new ArrayList<Layer>();
		
		for (Integer layerId: layerIds) {
			sortedList.add(LayerSorter.getLayer(layerId, layers));
		}
		
		return sortedList;
	}

	private static Layer getLayer(Integer layerId, ArrayList<Layer> layers) {
		Layer layer = null;
		
		for (Layer l : layers) {
			if (layerId == l.getUniqueLayerId()) {
				layer = l;
				break;
			}
		}
		
		return layer;
	}
	
}
