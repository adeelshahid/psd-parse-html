package htmlstructure;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import psd.model.Layer;

public class HTMLStructure {
	
	/**
	 * groups / layers divided by depth
	 */
	private ArrayList<HashMap<Integer, ArrayList<Layer>>> layersByDepth;
	
	/**
	 * groups / layers sorted by key, which can be used to access
	 * keys inside layersByDepth
	 */
	private ArrayList<ArrayList<Integer>> sortedGroupIds;
	
	/**
	 * HTML code for each group / layers at each depth
	 */
	private String html;
	
	/**
	 * CSS code for HTML
	 */
	private String css;
	
	/**
	 * 
	 * @param layersByDepth
	 * @param sortedLayersByDepth
	 */
	public HTMLStructure(
			ArrayList<HashMap<Integer, ArrayList<Layer>>> layersByDepth,
			ArrayList<ArrayList<Integer>> sortedLayersByDepth) {
				
		this.layersByDepth = layersByDepth;
		this.sortedGroupIds = sortedLayersByDepth;
	}

	public void process() {
		
		ArrayList<Integer> layerKeys;
		HashMap<Integer, ArrayList<Layer>> layersAtDepth;
		
		Integer key;
		ArrayList<Layer> layers;
		
		for (int i = 0; i < this.sortedGroupIds.size(); i++) {
			
			layersAtDepth = this.layersByDepth.get(i);
			layerKeys = this.sortedGroupIds.get(i);
			
			for (int j = 0; j < layerKeys.size(); j++) {
				key = layerKeys.get(j);
				layers = layersAtDepth.get(key);
				
				/**
				 * generate code for each layer
				 */
				this.generateCodeInLayers(key, layers);
			}
		}
	}

	private void generateCodeInLayers(Integer key, ArrayList<Layer> layers) {
		HTMLCode.generate(layers);
	}
	
	public String combinedHTMLCode() {
		
		ArrayList<Integer> layerKeys;
		HashMap<Integer, ArrayList<Layer>> layersAtDepth;
		
		Integer key;
		ArrayList<Layer> layers;
		
		StringBuilder html = new StringBuilder();
		
		/**
		 * we don't need to loop over all the depths as 
		 * recursion takes care of iterating through depth
		 */
		Integer index = this.layersByDepth.size() - 1;
		
		layersAtDepth = this.layersByDepth.get(index);
		layerKeys = this.sortedGroupIds.get(index);
		
		for (int j = 0; j < layerKeys.size(); j++) {
			key = layerKeys.get(j);
			layers = layersAtDepth.get(key);
			
			html.append(this.getHTMLCodeForLayers(layers));
		}
		
		return html.toString();
	}

	private String getHTMLCodeForLayers(ArrayList<Layer> layers) {
		Layer layer;
		StringBuilder code = new StringBuilder();
		
		ArrayList<Integer> sortedLayersIds = LayerSorter.sort(layers);
		ArrayList<Layer> sortedLayers = LayerSorter.sortByIds(sortedLayersIds, layers);
		
		for (int i = 0; i < sortedLayers.size(); i++) {
			layer = sortedLayers.get(i);
			code.append(this.getHTMLCodeForLayer(layer));
		}
		
		return code.toString();
	}

	private String getHTMLCodeForLayer(Layer layer) {
		
		/**
		 * find the id of the layer
		 * if it has a matching id for a group somewhere fetch code for that and include it in
		 */
		String code = "";
		ArrayList<Layer> layers;
		
		if (this.hasCodeInChildren(layer.getUniqueLayerId())) {
			layers = this.getChildrenLayers(layer.getUniqueLayerId());
			code = layer.getFillableCode();
			code = String.format(code, this.getHTMLCodeForLayers(layers));
		} else {
			code = layer.getCode();
		}
		
		return code;
	}
	
	/**
	 * retrieve the layers referred to by uniqueLayerId
	 * @param uniqueLayerId
	 * @return
	 */
	private ArrayList<Layer> getChildrenLayers(int uniqueLayerId) {

		ArrayList<Integer> layerKeys;
		
		HashMap<Integer, ArrayList<Layer>> layersAtDepth;
		
		Integer key;
		ArrayList<Layer> layers = null;
		
		/**
		 * if uniqueLayerId represents a group
		 */
		for (int i = this.sortedGroupIds.size() - 1; i >= 0; i--) {
			
			layersAtDepth = this.layersByDepth.get(i);
			layerKeys = this.sortedGroupIds.get(i);
			
			if (layerKeys.contains(uniqueLayerId)) {
				layers = layersAtDepth.get(uniqueLayerId);
				break;
			}
		}
		
		return layers;
	}

	/**
	 * check if the layerId has children
	 * @param uniqueLayerId
	 * @return
	 */
	private boolean hasCodeInChildren(int uniqueLayerId) {
		
		/**
		 * if uniqueLayerId represents a group
		 */
		for (int i = this.sortedGroupIds.size() - 1; i >= 0; i--) {
			if (this.sortedGroupIds.get(i).contains(uniqueLayerId)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @return the html
	 */
	public String getHtml() {
		return html;
	}

	/**
	 * @param html the html to set
	 */
	public void setHtml(String html) {
		this.html = html;
	}

	public String getCSS() {
		return css;
	}

	/**
	 * @param set CSS used for the HTML
	 */
	public void setCSS(String css) {
		this.css = css;
	}

	public String combinedCSSCode() {
		ArrayList<Integer> layerKeys;
		HashMap<Integer, ArrayList<Layer>> layersAtDepth;
		
		Integer key;
		ArrayList<Layer> layers;
		
		StringBuilder css = new StringBuilder();
		
		/**
		 * we don't need to loop over all the depths as 
		 * recursion takes care of iterating through depth
		 */
		Integer index = this.layersByDepth.size() - 1;
		
		layersAtDepth = this.layersByDepth.get(index);
		layerKeys = this.sortedGroupIds.get(index);
		
		for (int j = 0; j < layerKeys.size(); j++) {
			key = layerKeys.get(j);
			layers = layersAtDepth.get(key);
			
			css.append(this.getCSSCodeForLayers(layers));
		}
		
		return css.toString();
	}

	private String getCSSCodeForLayers(ArrayList<Layer> layers) {
		Layer layer;
		StringBuilder code = new StringBuilder();
		
		ArrayList<Integer> sortedLayersIds = LayerSorter.sort(layers);
		ArrayList<Layer> sortedLayers = LayerSorter.sortByIds(sortedLayersIds, layers);
		
		for (int i = 0; i < sortedLayers.size(); i++) {
			layer = sortedLayers.get(i);
			code.append(this.getCSSCodeForLayer(layer));
		}
		
		return code.toString();
	}

	private String getCSSCodeForLayer(Layer layer) {
		
		/**
		 * find the id of the layer
		 * if it has a matching id for a group somewhere fetch code for that and include it in
		 */
		String code = "";
		ArrayList<Layer> layers;
		
		code += layer.getCSSCode();
		
		if (this.hasCodeInChildren(layer.getUniqueLayerId())) {
			layers = this.getChildrenLayers(layer.getUniqueLayerId());
			code += this.getCSSCodeForLayers(layers);
		}
		
		return code;
	}
}