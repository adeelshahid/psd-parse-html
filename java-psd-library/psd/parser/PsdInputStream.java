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

package psd.parser;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import psd.parser.object.PsdBoolean;
import psd.parser.object.PsdDescriptor;
import psd.parser.object.PsdDouble;
import psd.parser.object.PsdEnum;
import psd.parser.object.PsdList;
import psd.parser.object.PsdLong;
import psd.parser.object.PsdObject;
import psd.parser.object.PsdObjectFactory;
import psd.parser.object.PsdText;
import psd.parser.object.PsdTextData;
import psd.parser.object.PsdUnitFloat;

public class PsdInputStream extends InputStream {

    private int pos;
    private int markPos;
    private final InputStream in;

    public PsdInputStream(InputStream in) {
        this.in = in;
        pos = 0;
        markPos = 0;
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
        markPos = pos;
    }

    @Override
    public synchronized void reset() throws IOException {
        in.reset();
        pos = markPos;
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }
    
    public void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    public void readFully(byte b[], int off, int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int res = in.read(b, off, len);
        if (res != -1) {
            pos += res;
        }
        return res;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int res = in.read(b);
        if (res != -1) {
            pos += res;
        }
        return res;
    }

    @Override
    public int read() throws IOException {
        int res = in.read();
        if (res != -1) {
            pos++;
        }
        return res;
    }

    @Override
    public long skip(long n) throws IOException {
        long skip = in.skip(n);
        pos += skip;
        return skip;
    }

    public String readString(int len) throws IOException {
        // read string of specified length
        byte[] bytes = new byte[len];
        read(bytes);
        return new String(bytes, "ISO-8859-1");
    }

    public String readPsdString() throws IOException {
        int size = readInt();
        if (size == 0) {
            size = 4;
        }
        return readString(size);
    }

    public int readBytes(byte[] bytes, int n) throws IOException {
        // read multiple bytes from input
        if (bytes == null)
            return 0;
        int r = 0;
        r = read(bytes, 0, n);
        if (r < n) {
            throw new IOException("format error. readed=" + r + " needed=" + n);
        }
        return r;
    }

    public byte readByte() throws IOException {
        int ch = read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte) (ch);
    }

    public int readUnsignedByte() throws IOException {
    	int ch = read();
        if (ch < 0) {
            throw new EOFException();
        }
        
        return (byte) (ch) & 0xff;
    }
    
    public short readShort() throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (short) ((ch1 << 8) + (ch2 << 0));
    }
    
    public int readInt() throws IOException {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    public boolean readBoolean() throws IOException {
        int ch = read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (ch != 0);
    }

    public final long readLong() throws IOException {
        int c1 = read();
        int c2 = read();
        int c3 = read();
        int c4 = read();
        int c5 = read();
        int c6 = read();
        int c7 = read();
        int c8 = read();
        return (((long) c1 << 56) + ((long) (c2 & 255) << 48) + ((long) (c3 & 255) << 40) + ((long) (c4 & 255) << 32)
                + ((long) (c5 & 255) << 24) + ((c6 & 255) << 16) + ((c7 & 255) << 8) + (c8 & 255));
    }

    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public int skipBytes(int n) throws IOException {
        int total = 0;
        int cur;
        while ((total < n) && ((cur = (int) skip(n - total)) > 0)) {
            total += cur;
        }
        return total;
    }

    public int getPos() {
        return pos;
    }

	public void readNullOfType(PsdInputStream stream, String type) throws IOException {
		if (type.equals("Objc")) {
			new PsdDescriptor(stream);
		} else if (type.equals("VlLs")) {
			new PsdList(stream);
		} else if (type.equals("doub")) {
			new PsdDouble(stream);
		} else if (type.equals("long")) {
			new PsdLong(stream);
		} else if (type.equals("bool")) {
			new PsdBoolean(stream);
		} else if (type.equals("UntF")) {
			new PsdUnitFloat(stream);
		} else if (type.equals("enum")) {
			new PsdEnum(stream);
		} else if (type.equals("TEXT")) {
			new PsdText(stream);
		} else if (type.equals("tdta")) {
			new PsdTextData(stream);
		} else {
			throw new IOException("UNKNOWN TYPE <" + type + ">");
		}
	}

	public void readObjectPoint() throws IOException {
		
		int length, number_item;
		String key, rootkey, type;
		
		// Unicode string: name from classID
		length = readInt() * 2;
		skipBytes(length);

		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = readInt();
		key = readString(4);
		
		// Number of items in descriptor
		number_item = readInt();
		
		while (number_item > 0) {
			length = readInt();
			rootkey = readString(4);
			
			// Type: OSType key
			type = readString(4);
			
			switch(rootkey)
			{
				// horizontal
				case "Hrzn":
					if (type.equals("UntF")) {
						key = readString(4);
					}
					
					// horizontal
					readDouble();
					break;
					
					// vertical
				case "Vrtc":
					if (type.equals("UntF")) {
						key = readString(4);
					}
					
					// vertical
					readDouble();
					break;
				
				default:
					readNullOfType((PsdInputStream) in, type);
					break;
			}
			
			number_item--;
		}
		
	}

	public Object readObjectColor() throws IOException {
		String key;
		int length, number_colors;
		int red, green, blue;
		
		// Unicode string: name from classID
		length = readInt() * 2;
		skipBytes(length);

		// classID: 4 bytes (length), followed either by string or (if length is zero) 4-
		// byte classID
		length = readInt();
		assert(length == 0);
		key = readString(4);
		// rgb color
		assert(key.equals("RGBC"));
		
		// Number of color component
		number_colors = readInt();
		// must be 3
		assert(number_colors == 3);

		// Key: 4 bytes ( length) followed either by string or (if length is zero) 4-byte key
		length = readInt();
		assert(length == 0);
		key = readString(4);
		// red component
		assert(key.equals("Rd  "));
		// Type: OSType key
		key = readString(4);
		// Double
		assert(key.equals("doub"));
		// Actual value (double)
		red = (int)readDouble();

		// Key: 4 bytes ( length) followed either by string or (if length is zero) 4-byte key
		length = readInt();
		assert(length == 0);
		key = readString(4);
		// green component
		assert(key.equals("Grn "));
		// Type: OSType key
		key = readString(4);
		// Double
		assert(key.equals("doub"));
		// Actual value (double)
		green = (int)readDouble();

		// Key: 4 bytes ( length) followed either by string or (if length is zero) 4-byte key
		length = readInt();
		assert(length == 0);
		key = readString(4);
		// blue component
		assert(key.equals("Bl  "));
		// Type: OSType key
		key = readString(4);
		// Double
		assert(key.equals("doub"));
		// Actual value (double)
		blue = (int)readDouble();
		
		return RGBtoColor(red, green, blue);
	}

	private String RGBtoColor(int red, int green, int blue) {
		String r = Integer.toHexString((int) Math.round((double) red)),
				g = Integer.toHexString((int) Math.round((double) green)),
				b = Integer.toHexString((int) Math.round((double) blue));
			
		r = r.equals("0") ? "00" : r;
		g = g.equals("0") ? "00" : g;
		b = b.equals("0") ? "00" : b;
		
		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;
		
		String rgbValue = r + g + b;
		
		return "#" + rgbValue;
	}

	public short[] getSpaceColor() throws IOException {
		short color_space;	// 2 bytes for color space
		short[] color_component = new short[4];		// 4 * 2 byte color component
		int i;
		//psd_argb_color color;

		// Color: 2 bytes for space followed by 4 * 2 byte color component
		color_space = readShort();
		for(i = 0; i < 4; i ++)
			color_component[i] = (short) (readShort() >> 8);
		
		return color_component;
		/*if(psd_color_space_to_argb(&color, color_space, color_component) != psd_status_done)
			return psd_color_clear;

		return color;*/
	}

}
