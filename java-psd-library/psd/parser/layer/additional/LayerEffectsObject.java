package psd.parser.layer.additional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import psd.parser.PsdInputStream;
import psd.parser.object.PsdObjectFactory;

public class LayerEffectsObject {

	public static void parsePatternOverlay(PsdInputStream stream) throws IOException {
		
		int length, number_items;
		String rootkey, type, key;
		String keychar;
		
		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);
		
		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		
		if (length == 0) {
			stream.readInt();
		} else {
			stream.skipBytes(length);
		}
		
		// Number of items in descriptor
		number_items = stream.readInt();
		
		keychar = null;
		
		while (number_items > 0) {
			
			length = stream.readInt();
			if (length == 0) {
				rootkey = stream.readString(4);
			} else {
				rootkey = "0";
				keychar = stream.readString(length);
			}
			
			// Type: OSType key
			type = stream.readString(4);
			
			switch(rootkey)
			{
				case "0":
					
					if (keychar.equals("phase")) {
						stream.readObjectPoint();
					} else {
						stream.readNullOfType(stream, type);
					}
					
					break;
				
					// effect enable
				case "enab":
					boolean enableEffect = stream.readBoolean();
					break;
				
				// blend mode
				case "Md  ":
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					PatternModeObjectBlendMode(stream);
					
					break;
				
				// opacity
				case "Opct":
					
					// percent
					key = stream.readString(4);
				
					// Actual value (double)
					double opacity = (stream.readDouble() * 2.55 + 0.5);
					break;
				
					// pattern
				case "Ptrn":
					PatternObjectInfo(stream);
					break;
				
				// scale
				case "Scl ":
					key = stream.readString(4);
					double scale = stream.readDouble();
					break;
				
				// link with layer
				case "Algn":
					boolean link_with_layer = stream.readBoolean();
					break;
				
				default:
					stream.readNullOfType(stream, type);
					break;
			}
			
			number_items--;
		}
	}

	private static void PatternObjectInfo(PsdInputStream stream) throws IOException {
		int i, length, number_items, identifier_length;
		String key, rootkey, type;
		String keychar;
		
		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);
		
		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		
		key = stream.readString(4);
		
		// Number of items in descriptor
		number_items = stream.readInt();
		
		while(number_items > 0)
		{
			length = stream.readInt();
			
			if(length == 0) {
				rootkey = stream.readString(4);
			} else {
				rootkey = "0";
				keychar = stream.readString(length);
			}
			
			// Type: OSType key
			type = stream.readString(4);
			
			switch(rootkey)
			{
				case "Nm  ":
					// String
					
					// String value as Unicode string
					int name_length = stream.readInt();
					String name = stream.readString(name_length * 2);
					break;
					
				case "Idnt":
					// String
					// String value as Unicode string
					identifier_length = stream.readInt();
					
					for(i = 0; i < identifier_length; i++) {
						//pattern_info->identifier[i] = psd_stream_get_short(context) & 0x00FF;
						stream.readShort();
					}
					break;

				default:
					stream.readNullOfType(stream, type);
					break;
			}
			
			number_items--;
		}
	}

	private static String PatternModeObjectBlendMode(PsdInputStream stream) throws IOException {
		int length;
		String tag;
		String keychar;
		String blend_mode = null;

		length = stream.readInt(); 
		
		if (length == 0) {
			tag = stream.readString(4);
			
			switch (tag)
			{
				case "Nrml":
					blend_mode = "normal";
					break;
				case "Dslv":
					blend_mode = "dissolve";
					break;
				case "Drkn":
					blend_mode = "darken";
					break;
				case "Mltp":
					blend_mode = "multiply";
					break;
				case "CBrn":
					blend_mode = "color_burn";
					break;
				case "Lghn":
					blend_mode = "lighten";
					break;
				case "Scrn":
					blend_mode = "screen";
					break;
				case "CDdg":
					blend_mode = "color_dodge";
					break;
				case "Ovrl":
					blend_mode = "overlay";
					break;
				case "SftL":
					blend_mode = "soft_light";
					break;
				case "HrdL":
					blend_mode = "hard_light";
					break;
				case "Dfrn":
					blend_mode = "difference";
					break;
				case "Xclu":
					blend_mode = "exclusion";
					break;
				case "H   ":
					blend_mode = "hue";
					break;
				case "Strt":
					blend_mode = "saturation";
					break;
				case "Clr ":
					blend_mode = "color";
					break;
				case "Lmns":
					blend_mode = "luminosity";
					break;
				default:
					break;
			}
		} else {
			keychar = stream.readString(length);
			
			if(keychar.equals("linearBurn")) {
			    blend_mode = "linear_burn";
			} else if(keychar.equals("linearDodge")) {
			    blend_mode = "linear_dodge";
			} else if(keychar.equals("vividLight")) {
			    blend_mode = "vivid_light";
			} else if(keychar.equals("linearLight")) {
			    blend_mode = "linear_light";
			} else if(keychar.equals("pinLight")) {
			    blend_mode = "pin_light";
			} else if(keychar.equals("hardMix")) {
			    blend_mode = "hard_mix";
			} else {
			    //psd_assert(0);
			}
		}
		
		return blend_mode;
	}

	public static void parseLayerDropShadow(PsdInputStream stream) throws IOException {
		int length, number_items;
		String rootkey, type, key;
		String keychar = null;
		
		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);
		
		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		
		if(length == 0)
			stream.readInt();
		else
			stream.skipBytes(length);
		
		// Number of items in descriptor
		number_items = stream.readInt();
		
		while(number_items > 0) {
			
			length = stream.readInt();
			if(length == 0) {
				rootkey = stream.readString(4);
			} else {
				rootkey = "0";
				keychar = stream.readString(length);
			}
			
			// Type: OSType key
			type = stream.readString(4);
			
			switch(rootkey)
			{
				case "0":
					// layer knocks out drop shadow
					if(keychar.equals("layerConceals")) {
						boolean knocks_out = stream.readBoolean();
					} else {
						//psd_assert(0);
					}
					
					break;
				
				// effect enable
				case "enab":
					boolean effect_enable = stream.readBoolean();
					break;
				
				// blend mode
				case "Md  ":
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					
					key = stream.readString(4);

					// blend mode
					DropShadowBlendMode(stream);
					break;
				
					// color or native color
				case "Clr ":
					// Descriptor
					//drop_shadow->color = drop_shadow->native_color = psd_stream_get_object_color(context);
					stream.readObjectColor();
					break;
				
					// opacity
				case "Opct":
					// percent
					key = stream.readString(4);
					
					// Actual value (double)
					int opacity = (int) (stream.readDouble() * 2.55 + 0.5);
					break;
				
				// use global light
				case "uglg":
					boolean use_global_light = stream.readBoolean();
					break;
				
				// angle
				case "lagl":
					// angle: base degrees
					key = stream.readString(4);
					int angle = (int) stream.readDouble();
					break;
				
				// distance
				case "Dstn":
					// pixels: tagged unit value
					key = stream.readString(4);
					int distance = (int) stream.readDouble();
					break;
				
				// spread
				case "Ckmt":
					// pixels: tagged unit value
					key = stream.readString(4);
					int spread = (int) stream.readDouble();
					break;
				
				// size
				case "blur":
					// pixels: tagged unit value
					key = stream.readString(4);
					int size = (int) stream.readDouble();
					break;
				
					// noise
				case "Nose":
					// percent
					key = stream.readString(4);
					// Actual value (double)
					int noise = (int) stream.readDouble();
					break;
				
				// anti-aliased
				case "AntA":
					boolean anti_aliased = stream.readBoolean();
					break;
				
				// contour
				case "TrnS":
					DropShadowObjectContour(stream);
					break;
				
				default:
					break;
			}
			
			number_items--;
		}
	}

	private static void DropShadowObjectContour(PsdInputStream stream) throws IOException {
		char[] input_value = new char[256], output_value = new char[256];
		boolean[] corner = new boolean[256];
		String key, type;
		int i, length, number_item, number_point, number_param;

		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);
		
		/***************************************************************************/
		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		key = stream.readString(4);
		
		// shape contour
		// Number of items in descriptor
		number_item = stream.readInt();
		
		if(number_item == 2) {
			length = stream.readInt();
			key = stream.readString(4);
			
			// name of contour
			type = stream.readString(4);

			length = stream.readInt() * 2;
			stream.skipBytes(length);
			
			length = stream.readInt();
			key = stream.readString(4);
			
			// curve
			type = stream.readString(4);
			
			// list
			// Number of items in the list
			number_point = stream.readInt();
			
			for (i = 0; i < number_point; i++) {
				type = stream.readString(4);
				
				length = stream.readInt() * 2;
				stream.skipBytes(length);
				
				length = stream.readInt();
				
				key = stream.readString(4);
				
				// curve point
				
				// set default value
				input_value[i] = output_value[i] = 0;
				corner[i] = false;

				number_param = stream.readInt();
				
				while(number_param > 0) {
					
					length = stream.readInt();
					key = stream.readString(4);
					type = stream.readString(4);
					
					switch(key)
					{
						// horizontal coordinate
						case "Hrzn":
							input_value[i] = (char) stream.readDouble();
							break;
							
							// vertical coordinate
						case "Vrtc":
							output_value[i] = (char) stream.readDouble();
							break;
							
						// point corner
						case "Cnty":
							stream.readBoolean();
							//corner[i] = true - psd_stream_get_bool(context);
							break;
							
						default:
							stream.readNullOfType(stream, type);
							break;
							
					}
					
					number_param--;
				}
			}
		}
		
		//psd_contour_calculate_table(lookup_table, number_point, input_value, output_value, corner);
	}

	private static String ObjectBlendMode(PsdInputStream stream) throws IOException {
		int length;
		String tag;
		String keychar;
		String blend_mode = null;

		length = stream.readInt(); 
		
		if (length == 0) {
			tag = stream.readString(4);
			
			switch (tag)
			{
				case "Nrml":
					blend_mode = "normal";
					break;
				case "Dslv":
					blend_mode = "dissolve";
					break;
				case "Drkn":
					blend_mode = "darken";
					break;
				case "Mltp":
					blend_mode = "multiply";
					break;
				case "CBrn":
					blend_mode = "color_burn";
					break;
				case "Lghn":
					blend_mode = "lighten";
					break;
				case "Scrn":
					blend_mode = "screen";
					break;
				case "CDdg":
					blend_mode = "color_dodge";
					break;
				case "Ovrl":
					blend_mode = "overlay";
					break;
				case "SftL":
					blend_mode = "soft_light";
					break;
				case "HrdL":
					blend_mode = "hard_light";
					break;
				case "Dfrn":
					blend_mode = "difference";
					break;
				case "Xclu":
					blend_mode = "exclusion";
					break;
				case "H   ":
					blend_mode = "hue";
					break;
				case "Strt":
					blend_mode = "saturation";
					break;
				case "Clr ":
					blend_mode = "color";
					break;
				case "Lmns":
					blend_mode = "luminosity";
					break;
				default:
					break;
			}
		} else {
			keychar = stream.readString(length);
			
			if(keychar.equals("linearBurn")) {
			    blend_mode = "linear_burn";
			} else if(keychar.equals("linearDodge")) {
			    blend_mode = "linear_dodge";
			} else if(keychar.equals("vividLight")) {
			    blend_mode = "vivid_light";
			} else if(keychar.equals("linearLight")) {
			    blend_mode = "linear_light";
			} else if(keychar.equals("pinLight")) {
			    blend_mode = "pin_light";
			} else if(keychar.equals("hardMix")) {
			    blend_mode = "hard_mix";
			} else {
			    //psd_assert(0);
			}
		}
		
		return blend_mode;
	}
	
	private static String DropShadowBlendMode(PsdInputStream stream) throws IOException {
		return ObjectBlendMode(stream);
	}

	public static void parseLayerInnerShadow(PsdInputStream stream) throws IOException {
		int length, number_items;
		String rootkey, type, key;
		String keychar = null;
		
		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);
		
		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		
		if(length == 0)
			stream.readInt();
		else
			stream.skipBytes(length);
		
		// Number of items in descriptor
		number_items = stream.readInt();
		
		while(number_items > 0) {
			
			length = stream.readInt();
			if(length == 0) {
				rootkey = stream.readString(4);
			} else {
				rootkey = "0";
				keychar = stream.readString(length);
			}
			
			// Type: OSType key
			type = stream.readString(4);
			
			switch(rootkey)
			{
				// effect enable
				case "enab":
					boolean effect_enable = stream.readBoolean();
					break;
				
				// blend mode
				case "Md  ":
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					
					key = stream.readString(4);

					// blend mode
					InnerShadowBlendMode(stream);
					break;
				
					// color or native color
				case "Clr ":
					// Descriptor
					//drop_shadow->color = drop_shadow->native_color = psd_stream_get_object_color(context);
					stream.readObjectColor();
					break;
				
					// opacity
				case "Opct":
					// percent
					key = stream.readString(4);
					
					// Actual value (double)
					int opacity = (int) (stream.readDouble() * 2.55 + 0.5);
					break;
				
				// use global light
				case "uglg":
					boolean use_global_light = stream.readBoolean();
					break;
				
				// angle
				case "lagl":
					// angle: base degrees
					key = stream.readString(4);
					int angle = (int) stream.readDouble();
					break;
				
				// distance
				case "Dstn":
					// pixels: tagged unit value
					key = stream.readString(4);
					int distance = (int) stream.readDouble();
					break;
				
				// spread
				case "Ckmt":
					// pixels: tagged unit value
					key = stream.readString(4);
					int spread = (int) stream.readDouble();
					break;
				
				// size
				case "blur":
					// pixels: tagged unit value
					key = stream.readString(4);
					int size = (int) stream.readDouble();
					break;
				
					// noise
				case "Nose":
					// percent
					key = stream.readString(4);
					// Actual value (double)
					int noise = (int) stream.readDouble();
					break;
				
				// anti-aliased
				case "AntA":
					boolean anti_aliased = stream.readBoolean();
					break;
				
				// contour
				case "TrnS":
					DropShadowObjectContour(stream);
					break;
				
				default:
					break;
			}
			
			number_items--;
		}
	}

	private static String InnerShadowBlendMode(PsdInputStream stream) throws IOException {
		return ObjectBlendMode(stream);
	}

	public static void parseLayerOuterGlow(PsdInputStream stream) throws IOException {
		int length, number_items;
		String rootkey, type, key;
		String keychar;

		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);
		
		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		
		if(length == 0)
			stream.readInt();
		else
			stream.skipBytes(length);
		
		// Number of items in descriptor
		number_items = stream.readInt();
		
		while (number_items > 0) {
			length = stream.readInt();
			assert(length == 0);
			
			if(length == 0) {
				rootkey = stream.readString(4);
			} else {
				rootkey = "0";
				keychar = stream.readString(length);
			}
			
			// Type: OSType key
			type = stream.readString(4);
			
			switch(rootkey)
			{
				// effect enable
				case "enab":
					assert(type.equals("bool"));
					boolean effect_enable = stream.readBoolean();
					break;

				// blend mode
				case "Md  ":
					assert(type.equals("enum"));
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					assert(length == 0);
					key = stream.readString(4);
					// blend mode
					assert(key.equals("BlnM"));
					ObjectBlendMode(stream);
					break;

				// color or native color
				case "Clr ":
					// Descriptor
					assert(type.equals("Objc"));
					stream.readObjectColor();
					//outer_glow->fill_type = psd_fill_solid_color;
					break;

				// gradient color
				case "Grad":
					// Descriptor
					assert(type.equals("Objc"));
					ObjectGradientColor(stream);
					//outer_glow->fill_type = psd_fill_gradient;
					break;

				// opacity
				case "Opct":
					assert(type.equals("UntF"));
					// percent
					key = stream.readString(4);
					assert(key.equals("#Prc"));
					// Actual value (double)
					int opacity = (int)(stream.readDouble() * 2.55 + 0.5);
					break;

				// technique
				case "GlwT":
					assert(type.equals("enum"));
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					assert(length == 0);
					key = stream.readString(4);
					// Matte Technique
					assert(key.equals("BETE"));
					GetObjectTechnique(stream);
					break;

				// choke
				case "Ckmt":
					assert(type.equals("UntF"));
					// pixels: tagged unit value
					key = stream.readString(4);
					assert(key.equals("#Pxl"));
					int spread = (int)stream.readDouble();
					break;

				// size
				case "blur":
					assert(type.equals("UntF"));
					// pixels: tagged unit value
					key = stream.readString(4);
					assert(key.equals("#Pxl"));
					int size = (int)stream.readDouble();
					break;

				// noise
				case "Nose":
					assert(type.equals("UntF"));
					// percent
					key = stream.readString(4);
					assert(key.equals("#Prc"));
					// Actual value (double)
					int noise = (int)stream.readDouble();
					break;

				// jitter
				case "ShdN":
					assert(type.equals("UntF"));
					// percent
					key = stream.readString(4);
					assert(key.equals("#Prc"));
					// Actual value (double)
					int jitter = (int)stream.readDouble();
					break;

				// anti-aliased
				case "AntA":
					assert(type.equals("bool"));
					boolean anti_aliased = stream.readBoolean();
					break;

				// contour
				case "TrnS":
					assert(type.equals("Objc"));
					GetObjectContour(stream);
					break;

				// range
				case "Inpr":
					assert(type.equals("UntF"));
					// percent
					key = stream.readString(4);
					assert(key.equals("#Prc"));
					// Actual value (double)
					int range = (int)stream.readDouble();
					break;

				default:
					stream.readNullOfType(stream, type);
					break;
			}
			
			
			number_items--;
		}
	}

	private static void GetObjectContour(PsdInputStream stream) throws IOException {
		char[] input_value = new char[256], output_value = new char[256];
		boolean[] corner = new boolean[256];
		String key, type;
		int i, length, number_item, number_point, number_param;
		
		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);
		
		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		key = stream.readString(4);
		
		// shape contour
		assert(key.equals("ShpC"));
		
		// Number of items in descriptor
		number_item = stream.readInt();
		
		if(number_item == 2)
		{
			length = stream.readInt();
			key = stream.readString(4);
			
			// name of contour
			assert(key.equals("Nm  "));
			
			type = stream.readString(4);
			
			assert(type.equals("TEXT"));

			length = stream.readInt() * 2;
			stream.skipBytes(length);
		}
		
		length = stream.readInt();
		assert(length == 0);
		
		key = stream.readString(4);
		
		// curve
		assert(key.equals("Crv "));
		type = stream.readString(4);
		
		// list
		assert(type.equals("VlLs"));
		
		// Number of items in the list
		number_point = stream.readInt();
		
		for(i = 0; i < number_point; i++) {
			type = stream.readString(4);
			assert(type.equals("Objc"));

			length = stream.readInt() * 2;
			stream.skipBytes(length);

			length = stream.readInt();
			assert(length == 0);
			key = stream.readString(4);
			
			// curve point
			assert(key.equals("CrPt"));
			
			// set default value
			input_value[i] = output_value[i] = 0;
			corner[i] = false;

			number_param = stream.readInt();
			
			while (number_param > 0) {
				length = stream.readInt();
				assert(length == 0);
				key = stream.readString(4);
				type = stream.readString(4);
				
				switch(key)
				{
					// horizontal coordinate
					case "Hrzn":
						assert(type.equals("doub"));
						input_value[i] = (char)stream.readDouble();
						break;
						
					// vertical coordinate
					case "Vrtc":
						output_value[i] =(char)stream.readDouble();
						break;
						
					// point corner
					case "Cnty":
						//corner[i] = psd_true - psd_stream_get_bool(context);
						stream.readBoolean();
						break;
						
					default:
						stream.readNullOfType(stream, type);
						break;
				}
				
				number_param--;
			}
		}
		
		//psd_contour_calculate_table(lookup_table, number_point, input_value, output_value, corner);
	}

	private static String GetObjectTechnique(PsdInputStream stream) throws IOException {
		String technique_type = "softer";
		String tag;
		int length;

		length = stream.readInt();
		
		if(length == 0)
		{
			tag = stream.readString(4);
			
			switch(tag)
			{
				case "SfBL":
					technique_type = "softer";
					break;
					
				case "PrBL":
					technique_type = "precise";
					break;
					
				case "Slmt":
					technique_type = "slope_limit";
					break;
					
				default:
					break;
			}
		}

		return technique_type;
	}

	private static void ObjectGradientColor(PsdInputStream stream) throws IOException {
		int i, length, number_items, number_lists, number_data;
		String key, rootkey, type;
		String keychar;
		
		String color_stop_type;
		String transparency_stop;
		
		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);
		
		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		
		key = stream.readString(4);
		
		// Gradient
		// Number of items in descriptor
		number_items = stream.readInt();
		
		while (number_items > 0) {
			
			length = stream.readInt();
			
			if(length == 0)
				rootkey = stream.readString(4);
			else
			{
				rootkey = "0";
				keychar = stream.readString(length);
			}
			
			// Type: OSType key
			type = stream.readString(4);
			
			switch(rootkey)
			{
				case "Nm  ":	// name
					// String
					
					// String value as Unicode string
					int name_length = stream.readInt();
					stream.skipBytes(2 * name_length);
					//psd_stream_get(context, (psd_uchar *)gradient_color->name, 2 * gradient_color->name_length);
					break;
				
                case "GrdF":	// gradient custom
                    // Enumerated
                    
                    // TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
                    // byte typeID
                    length = stream.readInt();
                    
                    key = stream.readString(4);
                    
                    // Gradient Type
    
                    // enum: 4 bytes (length), followed either by string or (if length is zero) 4-
                    // byte enum
                    length = stream.readInt();
                    
                    key = stream.readString(4);
                    
                    break;
               
                case "Intr":	// smoothness
    				// double

    				// for Interpolation ?
    				int smoothness = (int)stream.readDouble();
    				break;
    			
                case "Clrs": // color stop
    				// value list

    				number_lists = stream.readInt();
    				
    				// Number of color stops
    				int number_color_stops = number_lists;
    				
    				for(i = 0; i < number_color_stops; i++)
    				{
    					// Type: OSType key
    					type = stream.readString(4);
    					
    					// Descriptor

    					// Unicode string: name from classID
    					length = stream.readInt() * 2;
    					stream.skipBytes(length);

    					// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
    					// byte classID
    					length = stream.readInt();
    					
    					key = stream.readString(4);
    					
    					// color stop
    					
    					// Number of items in descriptor
    					number_data = stream.readInt();
    					
    					// maybe be 4, include color, color stop type, location and midpoint
    					
    					length = stream.readInt();
    					key = stream.readString(4);

    					// Type: OSType key
    					type = stream.readString(4);
    					
    					// Descriptor
    					Object actual_color = stream.readObjectColor();

    					// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
    					// byte classID
    					length = stream.readInt();
    					key = stream.readString(4);
    					
    					// Type
    					// Type: OSType key
    					type = stream.readString(4);
    					
    					// Enumerated

    					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
    					// byte typeID
    					length = stream.readInt();
    					key = stream.readString(4);
    					
    					// Gradient Type

    					// enum: 4 bytes (length), followed either by string or (if length is zero) 4-
    					// byte enum
    					length = stream.readInt();
    					key = stream.readString(4);
    					
    					switch(key)
    					{
    						case "FrgC":
    							color_stop_type = "foreground_color";
    							break;
    							
    						case "BckC":
    							color_stop_type = "background_color";
    							break;
    							
    						case "UsrS":
    							color_stop_type = "user_stop";
    							break;
    							
    						default:
    							break;
    					}

    					// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
    					// byte classID
    					length = stream.readInt();
    					key = stream.readString(4);
    					
    					// Location
    					
    					// Type: OSType key
    					type = stream.readString(4);
    					
    					// Integer

    					int location = stream.readInt();

    					// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
    					// byte classID
    					length = stream.readInt();
    					key = stream.readString(4);
    					
    					// midpoint
    					
    					// Type: OSType key
    					type = stream.readString(4);
    					
    					// Integer
    					int midpoint = stream.readInt();
    				}
    				break;
    			
                case "Trns":	// tranparency stop
    				// value list

    				number_lists = stream.readInt();
    				
    				// Number of color stops
    				int number_transparency_stops = number_lists;
    				transparency_stop = null;

    				for(i = 0; i < number_transparency_stops; i++)
    				{
    					// Type: OSType key
    					type = stream.readString(4);
    					
    					// Descriptor

    					// Unicode string: name from classID
    					length = stream.readInt() * 2;
    					stream.skipBytes(length);

    					// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
    					// byte classID
    					length = stream.readInt();
    					key = stream.readString(4);
    					
    					// transparency stop

    					// Number of items in descriptor
    					number_data = stream.readInt();
    					
    					// maybe be 3, include opacity, location and midpoint
    					
    					// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
    					// byte classID
    					length = stream.readInt();
    					key = stream.readString(4);
    					
    					// opacity
    					// Type: OSType key
    					type = stream.readString(4);
    					
    					// Unit psd_float
    					// '#Prc' = percent:
    					key = stream.readString(4);

    					// Actual value (double)
    					int opacity = (int)stream.readDouble();

    					// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
    					// byte classID
    					length = stream.readInt();
    					key = stream.readString(4);
    					
    					// Location
    					
    					// Type: OSType key
    					type = stream.readString(4);
    					
    					// Integer

    					int location = stream.readInt();

    					// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
    					// byte classID
    					length = stream.readInt();
    					key = stream.readString(4);
    							
    					// midpoint
    					// Type: OSType key
    					type = stream.readString(4);
    					
    					// Integer

    					int midpoint = stream.readInt();
    				}
    				break;
    			
                default:
                	stream.readNullOfType(stream, type);
    				break;

			}
			
			number_items--;
		}
	}

	public static void parseLayerInnerGlow(PsdInputStream stream) throws IOException {
		int length, number_items;
		String rootkey, type, key;
		String keychar;

		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);
		
		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		
		if(length == 0)
			stream.readInt();
		else
			stream.skipBytes(length);
		
		// Number of items in descriptor
		number_items = stream.readInt();
		
		while (number_items > 0) {
			length = stream.readInt();
			
			if(length == 0) {
				rootkey = stream.readString(4);
			} else {
				rootkey = "0";
				keychar = stream.readString(length);
			}
			
			// Type: OSType key
			type = stream.readString(4);
			
			switch(rootkey)
			{
				// effect enable
				case "enab":
					boolean effect_enable = stream.readBoolean();
					break;

				// blend mode
				case "Md  ":
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					// blend mode
					ObjectBlendMode(stream);
					break;

				// color or native color
				case "Clr ":
					// Descriptor
					stream.readObjectColor();
					//outer_glow->fill_type = psd_fill_solid_color;
					break;

				// gradient color
				case "Grad":
					// Descriptor
					ObjectGradientColor(stream);
					//outer_glow->fill_type = psd_fill_gradient;
					break;

				// opacity
				case "Opct":
					// percent
					key = stream.readString(4);
					// Actual value (double)
					int opacity = (int)(stream.readDouble() * 2.55 + 0.5);
					break;

				// technique
				case "GlwT":
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					// Matte Technique
					GetObjectTechnique(stream);
					break;

				// choke
				case "Ckmt":
					// pixels: tagged unit value
					key = stream.readString(4);
					int spread = (int)stream.readDouble();
					break;

				// size
				case "blur":
					// pixels: tagged unit value
					key = stream.readString(4);
					int size = (int)stream.readDouble();
					break;

				// jitter
				case "ShdN":
					// percent
					key = stream.readString(4);
					// Actual value (double)
					int jitter = (int)stream.readDouble();
					break;
				
				// noise
				case "Nose":
					// percent
					key = stream.readString(4);
					// Actual value (double)
					int noise = (int)stream.readDouble();
					break;
					
					// anti-aliased
				case "AntA":
					boolean anti_aliased = stream.readBoolean();
					break;
				
					// anti-aliased
				case "glwS":
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// inner glow source

					length = stream.readInt();
					
					if(length == 0)
						key = stream.readString(4);
					else
					{
						key = "0";
						keychar = stream.readString(length);
					}
					
					String source;
					
					switch(key)
					{
						case "SrcC":
							source = "center";
							break;
							
						case "SrcE":
							source = "edge";
							break;
						default:
							
							break;
					}
					break;

				// contour
				case "TrnS":
					GetObjectContour(stream);
					break;

				// range
				case "Inpr":
					// percent
					key = stream.readString(4);
					// Actual value (double)
					int range = (int)stream.readDouble();
					break;

				default:
					stream.readNullOfType(stream, type);
					break;
			}
			
			
			number_items--;
		}
	}

	public static void parseLayerBevelEmboss(PsdInputStream stream) throws IOException {
		int length, number_items;
		String rootkey, type, key;
		String keychar = null;
		
		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);
		
		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		
		if(length == 0)
			stream.readInt();
		else
			stream.skipBytes(length);

		// Number of items in descriptor
		number_items = stream.readInt();
		
		String style;
		
		while (number_items > 0)
		{
			length = stream.readInt();
			if(length == 0)
				rootkey = stream.readString(4);
			else
			{
				rootkey = "0";
				keychar = stream.readString(length);
			}
			// Type: OSType key
			type = stream.readString(4);
			
			switch(rootkey)
			{
				case "0":
					// gloss anti-aliased
					if(keychar.equals("antialiasGloss"))
					{
						boolean gloss_anti_aliased = stream.readBoolean();
					}
					// coutour enable
					else if(keychar.equals("useShape"))
					{
						boolean contour_enable = stream.readBoolean();
					}
					// texture enable
					else if(keychar.equals("useTexture"))
					{
						boolean texture_enable = stream.readBoolean();
					}
					// texture depth
					else if(keychar.equals("textureDepth"))
					{
						// percent
						key = stream.readString(4);
						
						// Actual value (double)
						int texture_depth = (int) stream.readDouble();
					}
					else if(keychar.equals("phase"))
					{
						stream.readObjectPoint();						
					}
					else
					{
						stream.readNullOfType(stream, type);
					}
					
					break;
				
				// effect enable
				case "enab":
					boolean effect_enable = stream.readBoolean();
					break;
				
				// highlight blend mode
				case "hglM":
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// blend mode
					ObjectBlendMode(stream);
					//bevel_emboss->highlight_blend_mode = psd_stream_get_object_blend_mode(context);
					break;
					// highlight color
					
				case "hglC":
					// Descriptor
					stream.readObjectColor();
					//bevel_emboss->highlight_color = bevel_emboss->real_highlight_color = psd_stream_get_object_color(context);
					break;
				
				// highlight opacity
				case "hglO":
					// percent
					key = stream.readString(4);
					
					// Actual value (double)
					int highlight_opacity = (int)(stream.readDouble() * 2.55 + 0.5);
					break;
				
				// shadow blend mode
				case "sdwM":
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// blend mode
					ObjectBlendMode(stream);
					//LayerEffectsObject.bevel_emboss->shadow_blend_mode = psd_stream_get_object_blend_mode(context);
					break;
				
				// shadow color
				case "sdwC":
					// Descriptor
					stream.readObjectColor();
					//bevel_emboss->shadow_color = bevel_emboss->real_shadow_color = psd_stream_get_object_color(context);
					break;
				
				// shadow opacity
				case "sdwO":
					// percent
					key = stream.readString(4);
					
					// Actual value (double)
					int shadow_opacity = (int)(stream.readDouble() * 2.55 + 0.5);
					break;
				
				// technique
				case "bvlT":
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// bevel technique
					GetObjectTechnique(stream);
					//bevel_emboss->technique = psd_stream_get_object_technique(context);
					break;
				
				// style
				case "bvlS":
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// bevel style

					length = stream.readInt();
					
					if(length == 0)
						key = stream.readString(4);
					else
					{
						key = "0";
						keychar = stream.readString(length);
					}
					
					switch(key)
					{
						case "OtrB":
							style = "outer_bevel";
							break;
							
						case "InrB":
							style = "inner_bevel";
							break;
							
						case "Embs":
							style = "emboss";
							break;
							
						case "PlEb":
							style = "pillow_emboss";
							break;
							
						case "0":
							if(keychar.equals("strokeEmboss")) {
								 style = "stroke_emboss";
							} else {
								//psd_assert(0);
							}
							
							break;
							
						default:
							//psd_assert(0);
							break;
					}
					break;
				
				// use global light
				case "uglg":
					boolean use_global_light = stream.readBoolean();
					break;
				
				// angle
				case "lagl":
					// angle: base degrees
					key = stream.readString(4);
					int angle = (int)stream.readDouble();
					break;
				
				// altitude
				case "Lald":
					// angle: base degrees
					key = stream.readString(4);
					int altitude = (int)stream.readDouble();
					break;
				
				// depth
				case "srgR":
					key = stream.readString(4);
					int depth = (int)stream.readDouble();
					break;
				
				// size
				case "blur":
					// pixels: tagged unit value
					key = stream.readString(4);
					int size = (int)stream.readDouble();
					break;
				
				// direction
				case "bvlD":
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// emboss stamp style

					length = stream.readInt();
					
					if(length == 0)
						key = stream.readString(4);
					else
					{
						key = "0";
						keychar = stream.readString(length);
					}
					
					String direction;
					
					switch(key)
					{
						case "In  ":
							direction = "up";
							break;
							
						case "Out ":
							direction = "down";
							break;
							
						default:
							//psd_assert(0);
							break;
					}
					break;
					
				// gloss contour
				case "TrnS":
					assert(type.equals("Objc"));
					GetObjectContour(stream);
					//psd_stream_get_object_contour(bevel_emboss->gloss_contour_lookup_table, context);
					break;
				
				// soften
				case "Sftn":
					// pixels: tagged unit value
					key = stream.readString(4);
					int soften = (int)stream.readDouble();
					break;

				// contour
				case "MpgS":
					GetObjectContour(stream);
					//psd_stream_get_object_contour(bevel_emboss->contour_lookup_table, context);
					break;
					// contour anti-aliased
					
				case "AntA":
					boolean contour_anti_aliased = stream.readBoolean();
					break;

				// contour range
				case "Inpr":
					// percent
					key = stream.readString(4);
					
					// Actual value (double)
					int contour_range = (int) stream.readDouble();
					break;
				
				// invert
				case "InvT":
					boolean texture_invert = stream.readBoolean();
					break;

				// link with layer
				case "Algn":
					boolean texture_link = stream.readBoolean();
					break;
				
				// scale
				case "Scl ":
					// percent
					key = stream.readString(4);
					// Actual value (double)
					int texture_scale = (int)stream.readDouble();
					break;
				
				// texture pattern
				case "Ptrn":
					PatternObjectInfo(stream);
					//psd_stream_get_object_pattern_info(&bevel_emboss->texture_pattern_info, context);
					break;

				default:
					stream.readNullOfType(stream, type);
					break;
			}
			
			
			number_items--;
		}
	}

	public static void parseSatin(PsdInputStream stream) throws IOException {
		int length, number_items;
		String rootkey, type, key;
		String keychar;
		
		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);

		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		if(length == 0)
			stream.readInt();
		else
			stream.skipBytes(length);

		// Number of items in descriptor
		number_items = stream.readInt();
		
		while(number_items > 0)
		{
			length = stream.readInt();
			
			if(length == 0)
				rootkey = stream.readString(4);
			else
			{
				rootkey = "0";
				keychar = stream.readString(length);
			}
			
			// Type: OSType key
			type = stream.readString(4);
			
			switch(rootkey)
			{
				// effect enable
				case "enab":
					boolean effect_enable = stream.readBoolean();
					break;

				// blend mode
				case "Md  ":
					
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// blend mode
					ObjectBlendMode(stream);
					//satin->blend_mode = psd_stream_get_object_blend_mode(context);
					break;

				// color
				case "Clr ":
					// Descriptor
					stream.readObjectColor();
					//satin->color = psd_stream_get_object_color(context);
					break;
					
				// anti-aliased
				case "AntA":
					boolean anti_aliased = stream.readBoolean();
					break;

				// invert
				case "Invr":
					boolean invert = stream.readBoolean();
					break;
					
				// opacity
				case "Opct":
					// percent
					key = stream.readString(4);
					// Actual value (double)
					int opacity = (int)(stream.readDouble()  * 2.55 + 0.5);
					break;

				// angle
				case "lagl":
					// angle: base degrees
					key = stream.readString(4);
					int angle = (int)stream.readDouble();
					break;

				// distance
				case "Dstn":
					// pixels: tagged unit value
					key = stream.readString(4);
					int distance = (int)stream.readDouble();
					break;

				// size
				case "blur":
					// pixels: tagged unit value
					key = stream.readString(4);
					int size = (int)stream.readDouble();
					break;

				// contour
				case "MpgS":
					GetObjectContour(stream);
					//psd_stream_get_object_contour(satin->contour_lookup_table, context);
					break;

				default:
					stream.readNullOfType(stream, type);
					//psd_stream_get_object_null(type, context);
					break;
			}
			
			number_items--;
		}
	}

	public static ArrayList<HashMap<String, Object>> parseColorOverlay(PsdInputStream stream) throws IOException {
		
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		int length, number_items;
		String rootkey, type, key;
		String keychar;
		
		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);

		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		if(length == 0)
			stream.readInt();
		else
			stream.skipBytes(length);
		
		// Number of items in descriptor
		number_items = stream.readInt();
		
		boolean enabled = false;

		while(number_items > 0)
		{
			length = stream.readInt();
			
			if(length == 0)
				rootkey = stream.readString(4);
			else
			{
				rootkey = "0";
				keychar = stream.readString(length);
			}
			
			// Type: OSType key
			type = stream.readString(4);
			
			switch(rootkey)
			{
				// effect enable
				case "enab":
					boolean effect_enable = stream.readBoolean();
					
					enabled = effect_enable;
					data.put("enabled", effect_enable);
					
					break;

				// blend mode
				case "Md  ":
					
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// blend mode
					ObjectBlendMode(stream);
					//color_overlay->blend_mode = psd_stream_get_object_blend_mode(context);
					break;

				// opacity
				case "Opct":
					// percent
					key = stream.readString(4);
					
					// Actual value (double)
					int opacity = (int)(stream.readDouble() * 2.55 + 0.5);
					
					data.put("opacity", opacity);
					
					break;
					
				// color or native color
				case "Clr ":
					// Descriptor
					Object color = stream.readObjectColor();
					data.put("color", color);
					//color_overlay->color = color_overlay->native_color = psd_stream_get_object_color(context);
					break;

				default:
					//psd_assert(0);
					break;
			}
			
			list.add(data);
			
			number_items--;
		}
		
		if (!enabled) {
			list.clear();
		}
		
		return list;
	}

	public static HashMap<String,Object> parseGradientOverlay(PsdInputStream stream) throws IOException {
		int length, number_items;
		String rootkey, type, key;
		String keychar;
		
		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);

		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		
		if(length == 0)
			stream.readInt();
		else
			stream.skipBytes(length);

		// Number of items in descriptor
		number_items = stream.readInt();
		
		boolean enabled = false;
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		while(number_items > 0) {

			length = stream.readInt();
			if(length == 0)
				rootkey = stream.readString(4);
			else
			{
				rootkey = "0";
				keychar = stream.readString(length);
			}
			// Type: OSType key
			type = stream.readString(4);
			
			switch(rootkey)
			{
				// effect enable
				case "enab":
					boolean effect_enable = stream.readBoolean();
					enabled = effect_enable;
					data.put("enabled", effect_enable);
					break;

				// blend mode
				case "Md  ":
					
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// blend mode
					ObjectBlendMode(stream);
					//gradient_overlay->blend_mode = psd_stream_get_object_blend_mode(context);
					break;

				// opacity
				case "Opct":
					// percent
					key = stream.readString(4);
					// Actual value (double)
					int opacity = (int)(stream.readDouble() * 2.55 + 0.5);
					data.put("opacity", opacity);
					break;
					
				// gradient color
				case "Grad":
					// Descriptor
					HashMap<String, Object> colors = GetObjectGradientColor(stream);
					data.put("colors", colors);
					break;

				case "Angl":	// angle
					// Unit psd_float
					
					// '#Ang' = angle: base degrees
					key = stream.readString(4);

					// Actual value (double)
					int angle = (int)stream.readDouble();
					data.put("angle", angle);
					break;

				case "Type":	// gradient style
					// Enumerated

					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// Gradient Type
					String style = GetObjectGradientStyle(stream);
					data.put("style", style);
					break;

				case "Rvrs":	// reverse
					// boolean
					boolean reverse = stream.readBoolean();
					data.put("reverse", reverse);
					break;

				case "Algn":	// align with layer
					// boolean
					boolean align_width_layer = stream.readBoolean();
					data.put("align_width_layer", align_width_layer);
					break;

				case "Scl ":	// scale
					// '#Prc' = percent:
					key = stream.readString(4);
									
					// Actual value (double)
					int scale = (int)stream.readDouble();
					data.put("scale", scale);
					break;

				// offset, not documented
				case "Ofst":
					stream.readObjectPoint();
					/*psd_stream_get_object_point(&gradient_overlay->horz_offset, 
						&gradient_overlay->vert_offset, context);*/
					break;

				default:
					stream.readNullOfType(stream, type);
					break;
			}
			
			
			number_items--;
		}
		
		if (!enabled) {
			data.clear();
		}
		
		return data;
	}

	private static String GetObjectGradientStyle(PsdInputStream stream) throws IOException {
		String style = "linear";
		int length;
		String tag;
		
		// enum: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte enum
		length = stream.readInt();
		tag = stream.readString(4);
		
		switch(tag)
		{
			case "Lnr ":
				style = "linear";
				break;
			case "Rdl ":
				style = "radial";
				break;
			case "Angl":
				style = "angle";
				break;
			case "Rflc":
				style = "reflected";
				break;
			case "Dmnd":
				style = "diamond";
				break;
			default:
				//psd_assert(0);
				break;
		}

		return style;
	}

	private static HashMap<String, Object> GetObjectGradientColor(PsdInputStream stream) throws IOException {
		
		ArrayList<HashMap<String, Object>> color_stop = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> transparency_stop = new ArrayList<HashMap<String, Object>>();
		
		HashMap<String, Object> returnValues = new HashMap<String, Object>();
		
		int i, length, number_items, number_lists, number_data;
		String key, rootkey, type;
		String keychar;
		
		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);

		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		key = stream.readString(4);
		
		// Gradient
		
		// Number of items in descriptor
		number_items = stream.readInt();
		
		while (number_items > 0) {
			
			length = stream.readInt();
			
			if(length == 0) {
				rootkey = stream.readString(4);
			} else {
				rootkey = "0";
				keychar = stream.readString(length);
			}
			
			// Type: OSType key
			type = stream.readString(4);
			
			
			switch(rootkey)
			{
				case "Nm  ":	// name
					// String

					// String value as Unicode string
					int name_length = stream.readInt();
					String name = stream.readString(name_length * 2);
					
					break;
				
				case "GrdF":	// gradient custom
					// Enumerated

					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// Gradient Type

					// enum: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte enum
					length = stream.readInt();
					key = stream.readString(4);
					
					break;
				
				case "Intr":	// smoothness
					// double

					// for Interpolation ?
					int smoothness = (int)stream.readDouble();
					break;
				
				case "Clrs":	// color stop
					// value list
					number_lists = stream.readInt();
					
					// Number of color stops
					int number_color_stops = number_lists;
					HashMap<String, Object> tempColorStop; 
					
					for(i = 0; i < number_color_stops; i++)
					{
						tempColorStop = new HashMap<String, Object>();
						
						// Type: OSType key
						type = stream.readString(4);
						
						// Descriptor
						assert(type.equals("Objc"));
						
						// Unicode string: name from classID
						length = stream.readInt() * 2;
						stream.skipBytes(length);

						// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
						// byte classID
						length = stream.readInt();
						assert(length == 0);
						
						key = stream.readString(4);
						
						// color stop
						assert(key.equals("Clrt"));
						
						// Number of items in descriptor
						number_data = stream.readInt();
						assert(number_data == 4);
						
						// maybe be 4, include color, color stop type, location and midpoint
						length = stream.readInt();
						assert(length == 0);
						
						key = stream.readString(4);
						assert(key.equals("Clr "));

						// Type: OSType key
						type = stream.readString(4);
						assert(type.equals("Objc"));
						
						// Descriptor
						tempColorStop.put("actual_color", stream.readObjectColor());
						
						// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
						// byte classID
						
						length = stream.readInt();
						assert(length == 0);
						key = stream.readString(4);
						assert(key.equals("Type"));
						
						// Type
						// Type: OSType key
						type = stream.readString(4);
						// Enumerated
						assert(type.equals("enum"));
						
						// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
						// byte typeID
						length = stream.readInt();
						assert(length == 0);
						
						key = stream.readString(4);
						
						// Gradient Type
						// enum: 4 bytes (length), followed either by string or (if length is zero) 4-
						// byte enum
						length = stream.readInt();
						key = stream.readString(4);
						
						switch(key)
						{
							case "FrgC":
								tempColorStop.put("color_stop_type", "foreground_color");
								break;
								
							case "BckC":
								tempColorStop.put("color_stop_type", "background_color");
								break;
								
							case "UsrS":
								tempColorStop.put("color_stop_type", "user_stop");
								break;
								
							default:
								//psd_assert(0);
								break;
						}

						// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
						// byte classID
						length = stream.readInt();
						key = stream.readString(4);
						
						// Location
						// Type: OSType key
						type = stream.readString(4);
						
						// Integer
						tempColorStop.put("location", stream.readInt());

						// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
						// byte classID
						length = stream.readInt();
						key = stream.readString(4);
						
						// midpoint
						// Type: OSType key
						type = stream.readString(4);
						
						// Integer
						tempColorStop.put("midpoint", stream.readInt());
						
						color_stop.add(tempColorStop);
					}
					break;
				
				case "Trns":	// tranparency stop
					// value list
					number_lists = stream.readInt();
					
					// Number of color stops
					int number_transparency_stops = number_lists;

					HashMap<String, Object> tempTransparencyStop;
					
					for(i = 0; i < number_transparency_stops; i ++)
					{
						tempTransparencyStop = new HashMap<String, Object>();
						
						// Type: OSType key
						type = stream.readString(4);
						
						// Descriptor
						// Unicode string: name from classID
						length = stream.readInt() * 2;
						stream.skipBytes(length);

						// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
						// byte classID
						length = stream.readInt();
						key = stream.readString(4);
						
						// transparency stop
						// Number of items in descriptor
						number_data = stream.readInt();
						
						// maybe be 3, include opacity, location and midpoint
						
						/***************************************************************************/
						// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
						// byte classID
						length = stream.readInt();
						key = stream.readString(4);
						
						// opacity
						// Type: OSType key
						type = stream.readString(4);
						
						// Unit psd_float
						// '#Prc' = percent:
						key = stream.readString(4);

						// Actual value (double)
						int opacity = (int) stream.readDouble();
						
						tempTransparencyStop.put("opacity", opacity);

						// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
						// byte classID
						length = stream.readInt();
						key = stream.readString(4);
						
						// Location
						// Type: OSType key
						type = stream.readString(4);
						
						// Integer
						int location = stream.readInt();
						tempTransparencyStop.put("location", location);
						
						// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
						// byte classID
						length = stream.readInt();
						key = stream.readString(4);
						
						// midpoint
						// Type: OSType key
						type = stream.readString(4);
						
						// Integer
						int midpoint = stream.readInt();
						tempTransparencyStop.put("midpoint", midpoint);
						
						transparency_stop.add(tempTransparencyStop);
					}
					
					break;
				
				default:
					//psd_assert(0);
					stream.readNullOfType(stream, type);
					break;
			}
			
			number_items--;
		}
		
		returnValues.put("color_stop", color_stop);
		returnValues.put("transparency_stop", transparency_stop);
		
		return returnValues;
	}

	public static HashMap<String, Object> parseLayerStroke(PsdInputStream stream) throws IOException {
		
		int length, number_items;
		String rootkey, type, key;
		String keychar = null;
		
		// Unicode string: name from classID
		length = stream.readInt() * 2;
		stream.skipBytes(length);

		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = stream.readInt();
		if(length == 0)
			stream.readInt();
		else
			stream.skipBytes(length);

		// Number of items in descriptor
		number_items = stream.readInt();
		
		String fill_type = null;
		
		int gradient_scale, pattern_scale;
		int pattern_horz_phase, pattern_vert_phase;
		String position = null;
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		boolean enabled = false;
		
		while(number_items > 0)
		{
			length = stream.readInt();
			
			if(length == 0)
				rootkey = stream.readString(4);
			else
			{
				rootkey = "0";
				keychar = stream.readString(length);
			}
			
			// Type: OSType key
			type = stream.readString(4);
			
			switch(rootkey)
			{
				case "0":
					if(keychar.equals("phase"))
					{
						stream.readObjectPoint();
						pattern_horz_phase = 0;
						pattern_vert_phase = 0;
					}
					else
					{
						stream.readNullOfType(stream, type);
					}
					break;
					
				// effect enable
				case "enab":
					boolean effect_enable = stream.readBoolean();
					enabled = effect_enable;
					data.put("enabled", effect_enable);
					break;

				// position
				case "Styl":
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// fill style
					length = stream.readInt();
					
					if(length == 0)
						key = stream.readString(4);
					else
					{
						key = "0";
						keychar =  stream.readString(length);
					}
					
					switch(key)
					{
						case "OutF":
							position = "outside";
							break;
						case "Out":
						case "InsF":
							position = "inside";
							break;
						case "CtrF":
							position = "center";
							break;
						default:
							position = "";
							//psd_assert(0);
							break;
					}
					
					data.put("position", position);
					
					break;

				// fill type
				case "PntT":
					
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// fill style
					length = stream.readInt();
					
					if(length == 0)
						key = stream.readString(4);
					else
					{
						key = "0";
						keychar = stream.readString(length);
					}
					switch(key)
					{
						case "SClr":
							fill_type = "solid_color";
							break;
						case "GrFl":
							fill_type = "gradient";
							break;
						case "Ptrn":
							fill_type = "pattern";
							break;
						default:
							//psd_assert(0);
							break;
					}
					
					data.put("fill_type", fill_type);
					
					break;

				// blend mode
				case "Md  ":
					
					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// blend mode
					ObjectBlendMode(stream);
					//blend_mode = psd_stream_get_object_blend_mode(context);
					break;

				// opacity
				case "Opct":
					// percent
					key = stream.readString(4);
					
					// Actual value (double)
					int opacity = (int)(stream.readDouble() * 2.55 + 0.5);
					
					data.put("opacity", opacity);
					
					break;

				// size
				case "Sz  ":
					// pixels: tagged unit value
					key = stream.readString(4);
					int size = (int)stream.readDouble();
					
					data.put("size", size);
					
					break;

				// color
				case "Clr ":
					// Descriptor
					Object fill_color = stream.readObjectColor();
					
					data.put("color", fill_color);
					break;

				// gradient color
				case "Grad":
					// Descriptor
					ObjectGradientColor(stream);
					// psd_stream_get_object_gradient_color(&stroke->gradient_color, context);
					break;

				case "Angl":	// angle
					// Unit psd_float
					// '#Ang' = angle: base degrees
					key = stream.readString(4);

					// Actual value (double)
					int gradient_angle = (int)stream.readDouble();
					
					data.put("gradient_angle", gradient_angle);
					
					break;

				case "Type":	// gradient style
					// Enumerated

					// TypeID: 4 bytes (length), followed either by string or (if length is zero) 4-
					// byte typeID
					length = stream.readInt();
					key = stream.readString(4);
					
					// Gradient Type
					GetObjectGradientStyle(stream);
					// stroke->gradient_style = psd_stream_get_object_gradient_style(context);
					break;
					
				case "Rvrs":	// reverse
					// boolean
					boolean gradient_reverse = stream.readBoolean();
					break;

				case "Scl ":	// scale
					// Unit psd_float
					// '#Prc' = percent:
					key = stream.readString(4);
									
					// Actual value (double)
					if(fill_type.equals("gradient"))
						gradient_scale = (int)stream.readDouble();
					else if(fill_type.equals("pattern"))
						pattern_scale = (int)stream.readDouble();
					break;

				case "Algn":	// align with layer
					// boolean
					boolean gradient_align = stream.readBoolean();
					break;

				// offset, not documented
				case "Ofst":
					stream.readObjectPoint();
					/*psd_stream_get_object_point(&stroke->gradient_horz_offset, 
						&stroke->gradient_vert_offset, context);*/
					break;

				// pattern
				case "Ptrn":
					PatternObjectInfo(stream);
					break;

				// link with layer
				case "Lnkd":
					boolean pattern_link = stream.readBoolean();
					break;

				default:
					stream.readNullOfType(stream, type);
					break;
			}

			number_items--;
		}
		
		if (!enabled) {
			data.clear();
		}
		
		return data;
	}
}
