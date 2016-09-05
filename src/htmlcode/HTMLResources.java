package htmlcode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class HTMLResources {

	public static String get(String resource) {
		
		try {
			
			File filePath = FileUtils.getFile(HTMLResources.class.getResource(resource).getFile());
			BufferedReader reader = new BufferedReader(new FileReader(filePath));                                                                                   
 
			String line = null;
			StringBuilder text = new StringBuilder();
			
			while ((line = reader.readLine()) != null) {
				text.append(line);
			}
			 
			 reader.close();
			 
			 return text.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
