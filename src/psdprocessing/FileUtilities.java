package psdprocessing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;


public final class FileUtilities {

	public static String getHexString(byte[] b) throws Exception
	{
	  String result = "";
	  for (int i=0; i < b.length; i++) {
	    result +=
	          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
	  }
	  return result;
	}
	
	public static void saveBufferedImage(BufferedImage img, String filePath, String imageType)
	{
		if (img != null) {
			File outputFile = new File(filePath);
			
			try {
				ImageIO.write(img, imageType, outputFile);
			} catch (IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public static void deleteFolder(File folder)
	{
	    File[] files = folder.listFiles();
	    
	    if(files != null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	            	deleteFolder(f);
	            } else {
	            	if (
	            		f.getName().equals("index.html") ||
	            		f.getName().equals("Guardfile") ||
	            		f.getName().equals("Gemfile.lock") ||
	            		f.getName().equals("Gemfile")
	            	) {
	            		// files we don't delete
	            	} else {
	            		f.delete();
	            	}
	            }
	        }
	    }
	    
	    folder.delete();
	}
	
	public static void copyFolder(File src, File dest) {
 
    	if(src.isDirectory()){
 
    		//if directory not exists, create it
    		if(!dest.exists()){
    		   dest.mkdir();
    		}
 
    		//list all the directory contents
    		String files[] = src.list();
 
    		for (String file : files) {
    		   //construct the src and dest file structure
    		   File srcFile = new File(src, file);
    		   File destFile = new File(dest, file);
    		   //recursive copy
    		   FileUtilities.copyFolder(srcFile, destFile);
    		}
 
    	}else{
    		//if file, then copy it
    		//Use bytes stream to support all file types
    		try {
    			InputStream in = new FileInputStream(src);
    	        OutputStream out = new FileOutputStream(dest); 
 
    	        byte[] buffer = new byte[1024];
 
    	        int length;
    	        //copy the file content in bytes 
    	        while ((length = in.read(buffer)) > 0){
    	    	   out.write(buffer, 0, length);
    	        }
 
    	        in.close();
    	        out.close();
    	        //System.out.println("File copied from " + src + " to " + dest);
    		} catch (IOException e) {
    			
    		}
    	}
    }
	
}
