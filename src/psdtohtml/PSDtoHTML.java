package psdtohtml;

import htmlcode.HTMLCode;
import htmlstructure.HTMLStructure;
import htmlstructure.LayerSorter;
import htmlstructure.PSDStructure;

import java.util.ArrayList;
import java.util.HashMap;

import layerstructure.LayerContainmentCase;
import psd.model.Layer;
import psdprocessing.LayerImages;
import psdprocessing.LayerProcessor;
import psdprocessing.PSDProcessor;

public class PSDtoHTML {
	
	/**
	 * deals with psd processing only nothing to do with HTML or anything just
	 * a psd processor
	 */
	private static PSDProcessor psdProcessor;
	
	/**
	 * layer processor
	 */
	private static LayerProcessor layerProcessor;
	
	/**
	 * psd structure generator
	 */
	private static PSDStructure psdStructure;
	
	/**
	 * html structure
	 */
	private static HTMLStructure htmlStructure;
	
	/**
	 * get layers and groups in a depth first (bottom towards top [level 3, 2, 1]) format
	 * if we have layers / groups till depth 3,
	 * 	the array will contain a structure such as,
	 * 		
	 * 		-> level 3 [ layer / group items ]
	 * 		-> level 2 [ layer / group items ]
	 * 		-> level 1 [ layer / group items ]
	 * 		-> level 0 [ layer / group items ]
	 */
	private static ArrayList<HashMap<Integer,ArrayList<Layer>>> layersByDepth;
	
	/**
	 * groups sorted by depth
	 * we only store the keys which we can use to access `layersByDepth`
	 */
	private static ArrayList<ArrayList<Integer>> sortedLayers;
	
	/**
	 * path to the PSD file which we would like to process
	 */
	private static String psdPath;
	
	/**
	 * project path, where our images should be saved
	 */
	private static String projectPath;
	
	/**
	 * images path, path were images would be saved
	 */
	private static String imagesPath;
	
	
	public static void main(String[] args) {
		
		/**
		 * basic setup just so we know which psd file we are going to process
		 */
		PSDtoHTML.setup();
		
		/**
		 * process the PSD itself, 
		 * discover usable layers and other useful information
		 */
		PSDtoHTML.discoverPSDInformation();
		
		/**
		 * 
		 */
		
		/**
		 * generate images
		 */
		PSDtoHTML.generateImages();
		
		/**
		 * divide up the problem by levels
		 * go into groups, deepest and then come up 
		 * processing it step by step
		 */
		PSDtoHTML.generateStructure();
		
		/**
		 * find containment case for layers
		 * when layers contain other layers
		 * 
		 * e.g. a text button is inside a button but both of them are in the same group 
		 */
//		PSDtoHTML.layersContainmentCase();
		
		/**
		 * sort structure generated in previous step
		 * go into the structure at each level and 
		 * sort based upon (x,y) point of reference
		 * with respect to each item in the structure
		 */
		PSDtoHTML.sortStructure();
		
		/**
		 * generate code structure
		 * - go over sorted list structure and process each group one by one
		 */
		PSDtoHTML.generateCodeStructure();
		
		/**
		 * generate stylesheet code
		 */
		PSDtoHTML.generateStylesheetCode();
		

		/**
		 * save code inside the project folder
		 */
		PSDtoHTML.saveCode();
		
		
		/**
		 * 
		 * generate CSS code
		 * 
		 */
		
		/**
		 * when the work is finished ;)
		 */
		System.out.println("finished");
	}

	private static void generateStylesheetCode() {
		
		htmlStructure.setCSS( htmlStructure.combinedCSSCode() );
		
	}

	private static void saveCode() {
		HTMLCode.saveHTML(htmlStructure.getHtml(), PSDtoHTML.projectPath);
		HTMLCode.saveCSS(htmlStructure.getCSS(), PSDtoHTML.projectPath);
	}

	private static void layersContainmentCase() {
		HashMap<Integer, ArrayList<Layer>> layers;
		HashMap<Integer, ArrayList<Layer>> containedLayers;
		
		int depthBySize = layersByDepth.size();
		
		for (int i = 0; i < depthBySize; i++) {
			layers = layersByDepth.get(i);
			containedLayers = LayerContainmentCase.process(layers);
			LayerContainmentCase.insertItems(i, containedLayers, layersByDepth);
			
			if (layersByDepth.size() > depthBySize) {
				i++;
			}
		}
	}

	private static void generateCodeStructure() {
		
		htmlStructure = new HTMLStructure(layersByDepth, sortedLayers);
		
		htmlStructure.process();
		
		htmlStructure.setHtml( htmlStructure.combinedHTMLCode() );
		
	}

	private static void sortStructure() {
		HashMap<Integer, ArrayList<Layer>> layers;
		
		sortedLayers = new ArrayList<ArrayList<Integer>>();
		
		for (int i = 0; i < layersByDepth.size(); i++) {
			/**
			 * sort structure inside out
			 * sort by keys, sort by layers inside keys
			 */
			layers = layersByDepth.get(i);
			
			sortedLayers.add(LayerSorter.sort(layers));
		}
	}
	
	private static void generateStructure() {
		psdStructure = new PSDStructure(layerProcessor);

		/**
		 * here we fetch all layers in a level structure a
		 * problem custom designed by end user
		 */
		layersByDepth = psdStructure.get();
	}

	private static void generateImages() {
		/**
		 * all layers which would be used to generate images
		 */
		LayerImages.generateImages(layerProcessor.getImageLayers(), PSDtoHTML.imagesPath);
	}

	private static void discoverPSDInformation() {
		
		/**
		 * send the psd file for processing
		 */
		psdProcessor = new PSDProcessor(PSDtoHTML.psdPath);
		layerProcessor = psdProcessor.processLayers();
	}

	private static void setup() {
		
		/**
		 * some dummy psd files we can use to validate
		 */
		ArrayList<String> psdFiles = new ArrayList<String>();
		
		psdFiles.add("meguairs.psd");		// 0
		psdFiles.add("slippers.psd");		// 1
		psdFiles.add("index.psd");			// 2
		psdFiles.add("dutchbullion.psd");	// 3
		psdFiles.add("stuff2have.psd");		// 4
		psdFiles.add("foto-producten.psd");	// 5
		
		int psdIndex = 2;
		
		PSDtoHTML.psdPath = "/home/muhammad/websites/readpsdfile1/psd/" + psdFiles.get(psdIndex);
		
		/**
		 * folder for psd to html project
		 */
		PSDtoHTML.projectPath = "/home/muhammad/websites/readpsdfile1/output/";
		PSDtoHTML.imagesPath = PSDtoHTML.projectPath + "resources/images/";
	}

}
