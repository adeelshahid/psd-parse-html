package psd.parser.imageresource;

import java.io.IOException;

import psd.parser.PsdInputStream;
import psd.parser.object.PsdDescriptor;

public class ImageResourceSectionParser {
	private static final String PSD_TAG = "8BIM";
	private ImageResourceSectionHandler handler;

	public void setHandler(ImageResourceSectionHandler handler) {
		this.handler = handler;
	}

	public void parse(PsdInputStream stream) throws IOException {
		int length, i, size;
		short ID;
		String tag;
		byte sizeofname;
		int sizeofdata, prev_stream_pos;
		String buffer;
		
		// Length of image resource section
		length = stream.readInt();
		if(length <= 0)
			return;
		
		// default
		int global_angle = 30;
		int global_altitude = 30;
		
		while(length > 0)
		{
			// Signature: '8BIM'
			tag = stream.readString(4);
			if (!tag.equals(PSD_TAG) && !tag.equals("MeSa")) {
				throw new IOException("Format error: Invalid image resources section.: " + tag);
			}
			
			length -= 4;
			// Unique identifier for the resource
			ID = stream.readShort();
			length -= 2;
			
			// Name: Pascal string, padded to make the size even (a null name consists of two bytes of 0)
			sizeofname = stream.readByte();
			if((sizeofname & 0x01) == 0)
				sizeofname++;
			stream.skipBytes(sizeofname);
			length -= sizeofname + 1;
			
			// Actual size of resource data that follows
			sizeofdata = stream.readInt();
			length -= 4;
			
			// resource data must be even
			if((sizeofdata & 0x01) == 1)
				sizeofdata ++;
			length -= sizeofdata;
			int storePos = stream.getPos();
			
			if(sizeofdata > 0)
			{
				switch(ID)
				{
					// ResolutionInfo structure
					case 1005:
						// Horizontal resolution in pixels per inch.
						float resolution_info_hres = stream.readInt() / 65536.0f;
						// 1=display horitzontal resolution in pixels per inch; 2=display horitzontal resolution in pixels per cm.
						short resolution_info_hres_unit = stream.readShort();
						// Display width as 1=inches; 2=cm; 3=points; 4=picas; 5=columns.
						short resolution_info_width_unit = stream.readShort();
						// Vertial resolution in pixels per inch.
						float resolution_info_vres = stream.readInt() / 65536.0f;
						// 1=display vertical resolution in pixels per inch; 2=display vertical resolution in pixels per cm.
						short resolution_info_vres_unit = stream.readShort();
						// Display height as 1=inches; 2=cm; 3=points; 4=picas; 5=columns.
						short resolution_info_height_unit = stream.readShort();
						boolean fill_resolution_info = true;
						
						break;
					
						// Names of the alpha channels as a series of Pascal strings.
					case 1006:
						buffer = stream.readString(sizeofdata);
						break;
					
					// DisplayInfo structure
					case 1007:
						stream.getSpaceColor();
						// 0..100
						short display_info_opacity = stream.readShort();
						assert(display_info_opacity >= 0 && display_info_opacity <= 100);
						// selected = 0, protected = 1
						byte display_info_kind = stream.readByte();
						// maybe be 2 when color mode is multichannel
						//psd_assert(context->display_info.kind == 0 || context->display_info.kind == 1);
						// padding
						stream.readByte();
						boolean fill_display_info = true;
						break;
					
						// The caption as a Pascal string.
					case 1008:
						size = stream.readByte();
						stream.skipBytes(size);
						break;

					// Layer state information
					// 2 bytes containing the index of target layer (0 = bottom layer).
					case 1024:
						short target_layer_index = stream.readShort();
						break;

					// Layers group information
					// 2 bytes per layer containing a group ID for the dragging groups. Layers in
					// a group have the same group ID.
					case 1026:
						int layer_group_count = sizeofdata / 2;
						
						for(i = 0; i < layer_group_count; i ++)
							stream.readShort();
						break;
					
						// (Photoshop 4.0) Thumbnail resource for Photoshop 4.0 only
					case 1033:
					// (Photoshop 5.0) Thumbnail resource (supersedes resource 1033)
					case 1036:
						stream.skipBytes(sizeofdata);
						break;

					// (Photoshop 4.0) Copyright flag
					// Boolean indicating whether image is copyrighted. Can be set via
					// Property suite or by user in File Info...
					case 1034:
						short copyright_flag = stream.readShort();
						assert(copyright_flag == 0 || copyright_flag == 1);
						break;

					// (Photoshop 5.0) Global Angle
					// 4 bytes that contain an integer between 0 and 359, which is the global
					// lighting angle for effects layer. If not present, assumed to be 30.
					case 1037:
						global_angle = stream.readInt();
						break;

					// (Photoshop 5.0) Effects visible
					// 1-byte global flag to show/hide all the effects layer. Only present when
					// they are hidden.
					case 1042:
						short effects_visible = stream.readShort();
						assert(effects_visible == 0 || effects_visible == 1);
						break;
					
					// (Photoshop 5.0) Unicode Alpha Names
					// Unicode string (4 bytes length followed by string).
					case 1045:
						stream.skipBytes(sizeofdata);
						break;
					
					// (Photoshop 6.0) Indexed Color Table Count
					// 2 bytes for the number of colors in table that are actually defined
					case 1046:
						short indexed_color_table_count = stream.readShort();
						break;

					// (Photoshop 6.0) Transparency Index.
					// 2 bytes for the index of transparent color, if any.
					case 1047:
						short transparency_index = stream.readShort();
						break;

					// (Photoshop 6.0) Global Altitude
					// 4 byte entry for altitude
					case 1049:
						global_altitude = stream.readInt();
						break;

					// (Photoshop 6.0) Alpha Identifiers
					// 4 bytes of length, followed by 4 bytes each for every alpha identifier.
					case 1053:
						stream.skipBytes(sizeofdata);
						break;
					
					default:
						
						if (sizeofdata > 0 && tag.equals(PSD_TAG) && ID >= 4000 && ID < 5000) {
							String key = stream.readString(4);
							if (key.equals("mani")) {
								stream.skipBytes(12 + 12); // unknown data
								PsdDescriptor descriptor = new PsdDescriptor(stream);
								if (handler != null) {
									handler.imageResourceManiSectionParsed(descriptor);
								}
							}
						}
						
						stream.skipBytes(sizeofdata - (stream.getPos() - storePos));
						
						break;
				}
			}
		}
		
		/*int length = stream.readInt();
		int pos = stream.getPos();
		while (length > 0) {
			String tag = stream.readString(4);
			if (!tag.equals(PSD_TAG) && !tag.equals("MeSa")) {
				throw new IOException("Format error: Invalid image resources section.: " + tag);
			}
			length -= 4;
			int id = stream.readShort();
			length -= 2;

			int sizeOfName = stream.readByte() & 0xFF;
			if ((sizeOfName & 0x01) == 0)
				sizeOfName++;
			@SuppressWarnings("unused")
			String name = stream.readString(sizeOfName);
			length -= sizeOfName + 1;

			int sizeOfData = stream.readInt();
			length -= 4;
			if ((sizeOfData & 0x01) == 1)
				sizeOfData++;
			length -= sizeOfData;
			int storePos = stream.getPos();

			// TODO FIXME Is id correct?
			if (sizeOfData > 0 && tag.equals(PSD_TAG) && id >= 4000 && id < 5000) {
				String key = stream.readString(4);
				if (key.equals("mani")) {
					stream.skipBytes(12 + 12); // unknown data
					PsdDescriptor descriptor = new PsdDescriptor(stream);
					if (handler != null) {
						handler.imageResourceManiSectionParsed(descriptor);
					}
				}
			}
			stream.skipBytes(sizeOfData - (stream.getPos() - storePos));
		}
		stream.skipBytes(length - (stream.getPos() - pos));*/
	}
}
