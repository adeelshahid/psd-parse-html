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

public class LayerGradientMapParser implements LayerAdditionalInformationParser {

	public static final String TAG = "grdm";
	private final LayerGradientMapHandler handler;
	
	public LayerGradientMapParser(LayerGradientMapHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void parse(PsdInputStream stream, String tag, int size) throws IOException {
		
		short version = stream.readShort();
		
		if (handler != null) {
			handler.layerGradientMapParsed();
		}
	}
	
}