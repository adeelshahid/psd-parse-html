/*
 * This file is part of java-psd-library.
 * 
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package psd.parser.layer.additional;

import psd.parser.BlendMode;
import psd.parser.PsdInputStream;
import psd.parser.layer.LayerAdditionalInformationParser;
import psd.parser.layer.additional.effects.*;
import psd.parser.object.PsdObject;
import psd.parser.object.PsdObjectFactory;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Contains the effects applied in this layer.
 * For now it only supports some effects.
 */
public class LayerEffectsObjectBasedParser implements LayerAdditionalInformationParser {

    /**
     * Tag that represents the layer effects section.
     */
    public static final String TAG = "lfx2";

    /**
     * Layer effects handler (will receive the parsed effects).
     */
    private final LayerEffectsObjectBasedHandler handler;

    public LayerEffectsObjectBasedParser(LayerEffectsObjectBasedHandler handler) {
        this.handler = handler;
    }

    public void parse(PsdInputStream stream, String tag, int size) throws IOException {

    	HashMap<String, Object> data = new HashMap<String, Object>();
        
        int objectEffectsVersion = stream.readInt();
        
        if (objectEffectsVersion == 0) {
        	
        	int descriptorVersion = stream.readInt();
            
            if (descriptorVersion == 16) {
            	
            	//String unicodeString = stream.readPsdString();
            	stream.skip(stream.readInt() * 2);
            	
            	// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
            	// byte classID
            	int length = stream.readInt();
            	
            	if(length == 0) {
            		stream.readInt();
            	} else {
            		stream.skip(length);
            	}

            	// number of items in descriptor
            	int numOfItems = stream.readInt();
            	
            	String rootKey, type;
            	String keychar = "";
            	
            	String subRootKey, subType, subKeyChar;
            	
            	PsdObject obj;
            	
            	while (numOfItems > 0) {
            		
            		length = stream.readInt();
            		
            		if(length == 0) {
            			rootKey = stream.readString(4);
            		} else {
            			rootKey = "0";
            			keychar = stream.readString(length);
            		}
            		
            		type = stream.readString(4);
            		
            		switch (rootKey)
            		{
	            		case "0":
	            			
	            			if (keychar.equals("patternFill")) {
	            				LayerEffectsObject.parsePatternOverlay(stream);
	            			} else {
	            				stream.readNullOfType(stream, type);
	            			}
	            			
	            			break;
	            		
	            		case "Scl ":
	            			// percent
	            			int percent = stream.readInt();
	            			
	            			// Actual value (double)
	            			double actualValue = stream.readDouble();
	            			
	            			numOfItems--;
	            			
	            			length = stream.readInt();
	                		
	                		if(length == 0) {
	                			stream.readInt();
	                		} else {
	                			stream.skipBytes(length);
	                		}
	            			
	                		// visible
	                		type = stream.readString(4);
	                		boolean visible = stream.readBoolean();
	                		
	                		data.put("visible", visible);
	                		
	            			break;
	            		
	            		// drop shadow
	        			case "DrSh":
	        				// Descriptor
	        				LayerEffectsObject.parseLayerDropShadow(stream);
	        				break;
	        			
	        			// inner shadow
	        			case "IrSh":
	        				// Descriptor
	        				LayerEffectsObject.parseLayerInnerShadow(stream);
	        				break;
	        				
	        			// outer glow
	        			case "OrGl":
	        				assert(type.equals("Objc"));
	        				// Descriptor
	        				LayerEffectsObject.parseLayerOuterGlow(stream);
	        				break;

        				// inner glow
        				case "IrGl":
        					// Descriptor
        					LayerEffectsObject.parseLayerInnerGlow(stream);
        					break;
        				
        				// bevel and emboss
        				case "ebbl":
        					// Descriptor
        					LayerEffectsObject.parseLayerBevelEmboss(stream);
        					break;
        				
        				// satin
        				case "ChFX":
        					// Descriptor
        					LayerEffectsObject.parseSatin(stream);
        					break;
        				
        				// color overlay
        				case "SoFi":
        					// Descriptor
        					//psd_get_layer_color_overlay2(context, &data->color_overlay);
        					ArrayList<HashMap<String, Object>> solidFill = LayerEffectsObject.parseColorOverlay(stream);
        					
        					if (solidFill.size() > 0) {
        						data.put("SolidFill", solidFill);
        					}
        					
        					break;
        				
        				// gradient overlay
        				case "GrFl":
        					// Descriptor
        					//psd_get_layer_gradient_overlay2(context, &data->gradient_overlay);
        					HashMap<String, Object> gradient = LayerEffectsObject.parseGradientOverlay(stream);
        					
        					if (gradient.size() > 0) {
        						data.put("Gradient", gradient);
        					}
        					
        					break;
        					
        				// stroke
        				case "FrFX":
        					// Descriptor
        					//psd_get_layer_stroke2(context, &data->stroke);
        					
        					HashMap<String, Object> stroke = LayerEffectsObject.parseLayerStroke(stream);
        					
        					if (stroke.size() > 0) {
        						data.put("Stroke", stroke);
        					}
        					
        					break;
        					
        				default:
        					stream.readNullOfType(stream, type);
        					break;
            		}
            		
            		numOfItems--;
            	}
            	
            }
        }
        
        if (handler != null) {
            handler.handleEffects(data);
        }
    }

    
}
