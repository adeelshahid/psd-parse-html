package psd.parser.layer.additional;

import java.io.IOException;

import psd.parser.PsdInputStream;

public class LayerUtilities {

	public static String ReadColor(PsdInputStream stream)
	{
		// alpha, red, green, blue
		int a = 0, r = 0, g = 0, b = 0;
		
		try {
			
			stream.skipBytes(2);
			
			r = stream.readByte() & 0xff;
			stream.skipBytes(1);
			
			g = stream.readByte() & 0xff;
			stream.skipBytes(1);
			
			b = stream.readByte() & 0xff;
			stream.skipBytes(1);
			
			a = stream.readByte() & 0xff;
			stream.skipBytes(1);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringBuilder hexString = new StringBuilder();
		
		if (Integer.toHexString(r).length() == 1) {
			hexString.append("0").append(Integer.toHexString(r));
		} else {
			hexString.append(Integer.toHexString(r));
		}
		
		if (Integer.toHexString(g).length() == 1) {
			hexString.append("0").append(Integer.toHexString(g));
		} else {
			hexString.append(Integer.toHexString(g));
		}
		
		if (Integer.toHexString(b).length() == 1) {
			hexString.append("0").append(Integer.toHexString(b));
		} else {
			hexString.append(Integer.toHexString(b));
		}
		
		return hexString.toString();
	}
	
}
