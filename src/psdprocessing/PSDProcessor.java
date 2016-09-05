package psdprocessing;

import java.io.File;
import java.io.IOException;

import psd.model.Psd;

/**
 * 
 * @author muhammad
 * 
 * This class is basically an abstraction over the psd file itself
 * It's purpose would be to find out just enough information about
 * the psd itself that we need for our psd to html work 
 *
 */
public class PSDProcessor {
	
	/**
	 * path to the psd file
	 */
	private String psdFilePath = null;
	
	/**
	 * psd file with all the meta data attached to it
	 */
	private Psd psd = null;
	
	/**
	 * document width and height
	 */
	private int width;
	private int height;
	
	/**
	 * 
	 * @param psdFilePath PSD file path which we need to process
	 */
	public PSDProcessor(String psdFilePath) {
		this.psdFilePath = psdFilePath;
		
		this.load();
	}

	public LayerProcessor processLayers() {
		return new LayerProcessor(this.psd);
	}

	/**
	 * load the psd into memory for processing
	 * so we can get all the meta information about the psd
	 */
	private void load() {
		try {
			
			this.psd = new Psd(new File(this.psdFilePath));
			
			this.width = this.psd.getWidth();
			this.height = this.psd.getHeight();
			
		} catch (IOException e) {
			
			System.out.println("Failed to load PSD. Read error below relating to psd: " + this.psdFilePath);
			e.printStackTrace();
			System.exit(0);
			
		}
	}

	/**
	 * @return return the psd file used by the processor
	 */
	public String getFilePath() {
		return psdFilePath;
	}

	/**
	 * @param set psd file path
	 */
	public void setFilePath(String filePath) {
		this.psdFilePath = filePath;
	}

}
