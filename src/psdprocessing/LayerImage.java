package psdprocessing;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import psd.model.Layer;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi;

public class LayerImage {
	
	public static void SavePNG(String filePath, BufferedImage image)
	{
		try {
			FileUtilities.saveBufferedImage(image, filePath, "PNG");
			
			PngOptimizer optimizer = new PngOptimizer();
	    	PngImage pngImage = new PngImage(filePath);
	    	
			optimizer.optimize(pngImage, filePath, 9);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void SaveJPEG(String filePath, BufferedImage image)
	{
		try {
			JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
			
			jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			
			/*if (image.getWidth() > 50 && image.getHeight() > 50) {
				jpegParams.setCompressionQuality(0.88f);
			} else {
				jpegParams.setCompressionQuality(1.0f);
			}*/
			
			jpegParams.setCompressionQuality(1.0f);
			
			ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(new File(filePath));
			
			ImageWriter writer = new JPEGImageWriterSpi().createWriterInstance();
			writer.setOutput(imageOutputStream);
			
			BufferedImage bi2 = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics big = bi2.getGraphics();
	        big.drawImage(image, 0, 0, null);
	        
			writer.write(null, new IIOImage(bi2, null, null), jpegParams);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getHash(Layer layer)
	{
		byte[] hash;
		byte[] imageData;
		
		MessageDigest md;
		String md5Hash = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		BufferedImage image = layer.getImage();
		
		try {
			ImageIO.write(image, "png", outputStream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		imageData = outputStream.toByteArray();
		
		md = null;

		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		md.update(imageData);
		hash = md.digest();
		
		try {
			md5Hash = FileUtilities.getHexString(hash);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5Hash;
	}
}
