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

public class LayerVectorStrokeParser implements LayerAdditionalInformationParser {

	/*
	 * Vector Stroke Data (Photoshop CS6)
	 * Key is 'vstk'
	 */
	
	public static final String TAG = "vstk";
	private final LayerVectorStrokeHandler handler;
	
	public LayerVectorStrokeParser(LayerVectorStrokeHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void parse(PsdInputStream stream, String tag, int size) throws IOException {
		
		/*
		 * parsing vector stroke here
		 */
		HashMap<String, String> data = new HashMap<String, String>();
		
		if (handler != null) {
			handler.layerVectorStrokeParsed(data);
		}
	}
	
}