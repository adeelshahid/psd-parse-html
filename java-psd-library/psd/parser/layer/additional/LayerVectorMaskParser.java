package psd.parser.layer.additional;

import java.awt.Point;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;


import psd.model.Psd;
import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;
import psd.parser.header.Header;

public class LayerVectorMaskParser implements LayerAdditionalInformationParser {


	// Key is 'vmsk' or 'vsms'. If key is 'vsms' then we are writing for (Photoshop CS6) and the document will have a 'vscg' key
	private static int pointNumber;
	
	public static final String TAG = "vmsk";
	public static final String ALTERNATE_TAG = "vsms";
	private final LayerVectorMaskHandler handler;
	
	public LayerVectorMaskParser(LayerVectorMaskHandler handler) {
		this.handler = handler;

		pointNumber = 0;
	}
	
	@Override
	public void parse(PsdInputStream stream, String tag, int size) throws IOException {
		
		/*
		 * parsing vector mask here
		 */
		int version = stream.readInt();
		stream.skipBytes(4);
		
		int records = (size - 8) / 26;
		
		ArrayList<Point> points = new ArrayList<Point>();
		ArrayList<Point> newPoints = new ArrayList<Point>();
		
		for (int i = 0; i < records; i++) {
			/*
			 * 0 - Closed subpath length record
			 * 1 - Closed subpath Bezier knot, linked
			 * 2 - Closed subpath Bezier knot, unlinked
			 * 3 - Open subpath length record
			 * 4 - Open subpath Bezier knot, linked
			 * 5 - Open subpath Bezier knot, unlinked
			 * 6 - Path fill rule record
			 * 7 - Clipboard record
			 * 8 - Initial fill rule record
			 */
			short record_type = stream.readShort();
			
			switch(record_type)
			{
				case 0:		// Closed subpath length record
				case 3:		// Open subpath length record
					
					// contain the number of Bezier knot records in bytes 2 and 3
					short number_of_points = stream.readShort();
					boolean closed = record_type == 0 ? true : false;
					
					stream.skipBytes(22);
					
					break;
				
				case 1:		// Closed subpath Bezier knot, linked
				case 2:		// Closed subpath Bezier knot, unlinked
				case 4:		// Open subpath Bezier knot, linked
				case 5:		// Open subpath Bezier knot, unlinked	
					
					/*
					 * simple new calculation method found 
					 */
					
					stream.mark(8);
					
					ByteBuffer bf;
					int q;
					byte[] r = new byte[4];
					
					stream.readBytes(r, 4);
					
					bf = ByteBuffer.wrap(r);
					bf.order(ByteOrder.BIG_ENDIAN);
					
					q = bf.getInt();
					double yDouble = ((q / 16777216.0) * Psd.header.getHeight());
					int y = (int) Math.round(yDouble);
					
					stream.readBytes(r, 4);
					
					bf = ByteBuffer.wrap(r);
					bf.order(ByteOrder.BIG_ENDIAN);
					
					q = bf.getInt();
					double xDouble = ((q / 16777216.0) * Psd.header.getWidth());
					int x = (int) Math.round(xDouble);
					
					newPoints.add(new Point(x, y));
					
					stream.reset();
					
					/*
					 * second method which we are leaving as it has errors
					 */
					
					int num = stream.readInt();
					num = stream.readInt();
					
					boolean [] bits      = new boolean[32];	
					boolean [] readyBits = new boolean[24] ;
					boolean num1 = false, 
							num2 = false;
					double result1    = 0.0, 
						   result2    = 0.0, 
						   powerOfTwo = 0.0;
					///////////////////////////////////
					////////////////////////////////////
					/// knot
					bits = new boolean[32];
					num = stream.readInt();
					if(num >= 0)
						num1 = true;
					
					for(int ii = 0 ; ii < bits.length ; ii++)
						bits[ii] = false;

					for(int ii = 0 ; ii < bits.length && num != 0; ii++){
						if(num % 2 == 1){
							bits[bits.length - 1 - ii] = true;	// if bits.length < 32 than we add 0 at the first positions						
						}
						else{
							bits[bits.length - 1 - ii] = false;	// if bits.length < 32 than we add 0 at the first positions											
						}
						num >>= 1;					
					}						
					
					// Reverse bits
					for(int ii = 0 ; ii <= bits.length/2 ; ii++){
						boolean tmp = bits[ii];
						bits[ii] = bits[bits.length - 1 - ii];
						bits[bits.length - 1 - ii] = tmp;					
						
					}

					for(int ii = 0 ; ii < 24 ; ii++){
						readyBits[ii] = bits[ii];
					}
						
					powerOfTwo = 0.5;
					result1 = 0.0;
					
					for(int ii = readyBits.length - 1 ; ii >= 1 ; ii--){
						if(readyBits[ii] == true)
							result1 += powerOfTwo;
						powerOfTwo /= 2.0;
					}
					
					
					///////////////////////////////////
					bits = new boolean[32];			
					num = stream.readInt();
					if(num >= 0)
						num2 = true;

					
					for(int ii = 0 ; ii < bits.length ; ii++)
						bits[ii] = false;
					
					for(int ii = 0 ; ii < bits.length && num != 0; ii++){
						if(num % 2 == 1){
							bits[bits.length - 1 - ii] = true;	// if bits.length < 32 than we add 0 at the first positions						
						}
						else{
							bits[bits.length - 1 - ii] = false;	// if bits.length < 32 than we add 0 at the first positions											
						}
						num >>= 1;					
					}						
					
					// Reverse bits
					for(int ii = 0 ; ii <= bits.length/2 ; ii++){
						boolean tmp = bits[ii];
						bits[ii] = bits[bits.length - 1 - ii];
						bits[bits.length - 1 - ii] = tmp;					
						
					}

					for(int ii = 0 ; ii < 24 ; ii++){
						readyBits[ii] = bits[ii];
					}
						
					powerOfTwo = 0.5;
					result2 = 0.0;
					
					for(int ii = readyBits.length - 1 ; ii >= 1 ; ii--){
						if(readyBits[ii] == true)
							result2 += powerOfTwo;
						powerOfTwo /= 2.0;
					}
					
					int X = (int)(Psd.header.getWidth() * result2);
					int Y = (int)(Psd.header.getHeight() * result1);
					
					if((Psd.header.getWidth() * result2) - (int)(Psd.header.getWidth() * result2) >= 0.5){
						X += 1;
					}
					
					if((Psd.header.getHeight() * result1) - (int)(Psd.header.getHeight() * result1) >= 0.5){
						Y += 1;
					}
					
					points.add(new Point(X, Y));
					
					num = stream.readInt();
					num = stream.readInt();
		
					break;
				
				case 6:		// Path fill rule record
					stream.skipBytes(24);
					break;
				
				case 7:		// Clipboard record
					// contain four fixed-point numbers for the bounding rectangle (top, left, bottom, right)
					int top = stream.readInt(),
						left = stream.readInt(),
						bottom = stream.readInt(),
						right = stream.readInt();
					
					// a single fixed-point number indicating the resolution
					int resolution = stream.readInt();
					
					stream.skipBytes(4);
					
					break;

				case 8:		// Initial fill rule record
					int initial_fill = stream.readShort();
					stream.skipBytes(22);
					break;
					
				default:
					stream.skipBytes(24);
					break;
			}
		}
		
		if (handler != null) {
			handler.layerVectorMaskParsed(newPoints);
			//handler.layerVectorMaskParsed(points);
		}
	}
	
}