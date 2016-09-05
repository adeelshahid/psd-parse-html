package psdprocessing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import psd.model.Layer;

public class LayerImages {
	
	/**
	 * use layers to generate images
	 * @param imageLayers, layers to be used as images
	 * @param imagesPath, path where to save images
	 */
	public static void generateImages(ArrayList<Layer> imageLayers, String imagesPath) {
		
		LayerImages.preprocessImageGeneration(imageLayers);
		
		Layer layer;
		String filePath,
			fileSavingName;
		
		LayerImages.cleanImagesDirectory(imagesPath);
		
		HashMap<String, String> md5Hashes = new HashMap<String, String>();
		String layerMD5Hash;
		
		for (int i = 0; i < imageLayers.size(); i++) {
			
			layer = imageLayers.get(i);
			layerMD5Hash = layer.getMd5Hash();
			
			if (layerMD5Hash != null && md5Hashes.containsKey(layerMD5Hash)) {
				layer.setFilePath(md5Hashes.get(layerMD5Hash));
				continue;
			}
			
			fileSavingName = layer.fileSavingName();
			filePath = imagesPath + fileSavingName;
			
			if (layer.hasTransparency() || (layer.getWidth() <= 50 || layer.getHeight() <= 50)) {
				
				filePath += ".png";
				LayerImage.SavePNG(filePath, layer.getImage());
				
    		} else {
    			filePath += ".jpg";
    			LayerImage.SaveJPEG(filePath, layer.getImage());
    		}
			
			layer.setFilePath(filePath);
			
			// layer image md5 hash code, file path to which this key corresponds to
			md5Hashes.put(layerMD5Hash, filePath);
		}
	}

	private static void preprocessImageGeneration(ArrayList<Layer> imageLayers) {
		
		Layer layer;
		String key;
		
		/**
		 * based on WIDTHxHEIGHT hash key, make a list of layers that might have the same image
		 */
		HashMap<String, ArrayList<Layer>> list = new HashMap<String, ArrayList<Layer>>();
		
		for (int i = 0; i < imageLayers.size(); i++) {
			layer = imageLayers.get(i);
			
			key = layer.getWidth() + "x" + layer.getHeight();
			
			if (!list.containsKey(key)) {
				list.put(key, new ArrayList<Layer>());
			}
			
			list.get(key).add(layer);
		}
		
//		System.out.println(list);
		
		/**
		 * if list HashMap has just one key then this size does not have a duplicate image
		 */
		ArrayList<String> deleteKeys = new ArrayList<String>();
		
		for (String mapKey : list.keySet()) {
			if (list.get(mapKey).size() <= 1) {
				deleteKeys.add(mapKey);
			}
		}
		
		/**
		 * delete keys from HashMap based on size key
		 */
		if (deleteKeys.size() > 0) {
			for (String deleteKey : deleteKeys) {
				list.remove(deleteKey);
			}
		}
		
//		System.out.println(list);
		
		/**
		 * now iterate over HashMap key, and check images in it.
		 * if they are duplicate in the list
		 */
		ArrayList<Layer> layersToCompare;
		
		for (String mapKey: list.keySet()) {
			layersToCompare = list.get(mapKey);
			
			for (int i = 0; i < layersToCompare.size(); i++) {
				layersToCompare.get(i).setMd5Hash(LayerImage.getHash(layersToCompare.get(i)));
			}
		}
		
	}

	public static void cleanImagesDirectory(String imagesPath) {
		File targetFolder = new File(imagesPath);
		FileUtilities.deleteFolder(targetFolder);
		
		if (!targetFolder.exists()) {
			try {
				boolean success = targetFolder.mkdirs();
				
				if (!success) {
					throw new Exception("Directory creation failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("");
				System.out.println("Image directory creation failed");
			}
		}
	}
	
}
