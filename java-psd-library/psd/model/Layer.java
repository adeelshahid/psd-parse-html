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

package psd.model;

import htmlstructure.LayerSorter;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import com.sun.xml.internal.ws.util.StringUtils;

import psd.parser.BlendMode;
import psd.parser.layer.BlendingRanges;
import psd.parser.layer.Channel;
import psd.parser.layer.LayerHandler;
import psd.parser.layer.LayerParser;
import psd.parser.layer.LayerType;
import psd.parser.layer.Mask;
import psd.parser.layer.additional.LayerEffectsHandler;
import psd.parser.layer.additional.LayerEffectsObjectBasedHandler;
import psd.parser.layer.additional.LayerEffectsObjectBasedParser;
import psd.parser.layer.additional.LayerEffectsParser;
import psd.parser.layer.additional.LayerGradientMapHandler;
import psd.parser.layer.additional.LayerGradientMapParser;
import psd.parser.layer.additional.LayerIdHandler;
import psd.parser.layer.additional.LayerIdParser;
import psd.parser.layer.additional.LayerMetaDataHandler;
import psd.parser.layer.additional.LayerMetaDataParser;
import psd.parser.layer.additional.LayerSectionDividerHandler;
import psd.parser.layer.additional.LayerSectionDividerParser;
import psd.parser.layer.additional.LayerTypeToolHandler;
import psd.parser.layer.additional.LayerTypeToolParser;
import psd.parser.layer.additional.LayerUnicodeNameHandler;
import psd.parser.layer.additional.LayerUnicodeNameParser;
import psd.parser.layer.additional.LayerVectorMaskHandler;
import psd.parser.layer.additional.LayerVectorMaskParser;
import psd.parser.layer.additional.Matrix;
import psd.parser.layer.additional.effects.PSDEffect;
import psd.parser.layer.additional.effects.SolidFillEffect;
import psd.parser.object.PsdDescriptor;
import psd.parser.object.PsdObject;
import psd.util.BufferedImageBuilder;

public class Layer implements LayersContainer {
	
	/*
	 * static variable for layer that holds names of all layer fileSavingNames
	 */
	public static ArrayList<String> fileSavingNameList = new ArrayList<String>();
	
    private int top = 0;
    private int left = 0;
    private int bottom = 0;
    private int right = 0;
    
    private int oldTop, oldLeft, oldBottom, oldRight;
    
    private int alpha = 255;

    private boolean visible = true;
    
    private Matrix textTransformMatrix;
    private int textAngle = 0;

    private String name;
    
    private String md5Hash = null;
    
    private String fileSavingName = null;
    
    private boolean hasImagePath = false;
    private String filePath = null;

    private BufferedImage image;
    private LayerType type = LayerType.NORMAL;

    private ArrayList<Layer> layers = new ArrayList<Layer>();
    
    private Map<String, PsdObject> textInformation = new HashMap<String, PsdObject>();
    
    private List<PSDEffect> effects = null;
    
    /*
     * these are object based effects newly added in Photoshop
     */
    private HashMap<String, Object> objectBasedEffects = null;
    
    private boolean isTransparent = false;
    private boolean transparencyProcessed = false;
    
    private int layerId;
    
    private ArrayList<Point> points = null;

	private int uniqueLayerId;

	private int layerGroupId;

	private int depth;
	
	/**
	 * html code / information
	 * 
	 */
	private String htmlTagName = null;
	private String htmlClassName = null;
	private String htmlImageSrc = null;
	private String htmlText = null;
	
	/**
	 * html styling information
	 * 
	 */
	private HashMap<String, String> cssStyles = new HashMap<String, String>();

	/**
	 * css styles in a string
	 */
	private String styles;

    public boolean isHasImagePath() {
		return hasImagePath;
	}

	public void setHasImagePath(boolean hasImagePath) {
		this.hasImagePath = hasImagePath;
	}

	public String getHtmlTagName() {
		return htmlTagName;
	}

	public void setHtmlTagName(String htmlTagName) {
		this.htmlTagName = htmlTagName;
	}

	public String getHtmlClassName() {
		return htmlClassName;
	}

	public void setHtmlClassName(String htmlClassName) {
		this.htmlClassName = htmlClassName;
	}

	public String getHtmlImageSrc() {
		return htmlImageSrc;
	}

	public void setHtmlImageSrc(String htmlSrc) {
		this.htmlImageSrc = htmlSrc;
	}

	public String getHtmlText() {
		return htmlText;
	}

	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
	}

	public HashMap<String, String> getCssStyles() {
		return cssStyles;
	}
	
	public void addCssStyle(String key, String value)
	{
		this.cssStyles.put(key, value);
	}

	public void setCssStyles(HashMap<String, String> cssStyles) {
		this.cssStyles = cssStyles;
		this.generateStyles();
	}

	private void generateStyles() {
		StringBuilder css = new StringBuilder();
		
		for (String key : this.cssStyles.keySet()) {
			css.append(String.format("%s: %s;", key, this.cssStyles.get(key)));
		}
		
		this.setStyles(css.toString());
	}

	public Layer(LayerParser parser) {
    	
    	parser.setHandler(new LayerHandler() {
            @Override
            public void boundsLoaded(int left, int top, int right, int bottom) {
                Layer.this.left = left;
                Layer.this.top = top;
                Layer.this.right = right;
                Layer.this.bottom = bottom;
                
                Layer.this.setOldLeft(left);
                Layer.this.setOldTop(top);
                Layer.this.setOldRight(right);
                Layer.this.setOldBottom(bottom);
            }

            @Override
            public void blendModeLoaded(BlendMode blendMode) {
            		
            }

            @Override
            public void blendingRangesLoaded(BlendingRanges ranges) {
            	
            }

            @Override
            public void opacityLoaded(int opacity) {
                Layer.this.alpha = opacity;
            }

            @Override
            public void clippingLoaded(boolean clipping) {
            }

            @Override
            public void flagsLoaded(boolean transparencyProtected, boolean visible, boolean obsolete, boolean isPixelDataIrrelevantValueUseful, boolean pixelDataIrrelevant) {
                Layer.this.visible = visible;
            }

            @Override
            public void nameLoaded(String name) {
                Layer.this.name = name;
            }

            @Override
            public void channelsLoaded(List<Channel> channels) {
                BufferedImageBuilder imageBuilder = new BufferedImageBuilder(channels, getWidth(), getHeight());
                image = imageBuilder.makeImage();
            }

            @Override
            public void maskLoaded(Mask mask) {
            }

        });
        
        parser.putAdditionalInformationParser(LayerIdParser.TAG, new LayerIdParser(new LayerIdHandler() {
			
			@Override
			public void layerIdParsed(int id) {
				setLayerId(id);
			}
		}));
        
        parser.putAdditionalInformationParser(LayerVectorMaskParser.TAG, new LayerVectorMaskParser(new LayerVectorMaskHandler() {
			
			@Override
			public void layerVectorMaskParsed(ArrayList<Point> points) {
				setPoints(points);
			}
		}));
        
        parser.putAdditionalInformationParser(LayerEffectsObjectBasedParser.TAG, new LayerEffectsObjectBasedParser(new LayerEffectsObjectBasedHandler() {
			
			@Override
			public void handleEffects(HashMap<String, Object> data) {
				
				if (data.size() > 0) {
					objectBasedEffects = data;
				}
			}
        }));
        
        parser.putAdditionalInformationParser(LayerVectorMaskParser.ALTERNATE_TAG, new LayerVectorMaskParser(new LayerVectorMaskHandler() {
			
			@Override
			public void layerVectorMaskParsed(ArrayList<Point> points) {
				setPoints(points);
			}
		}));
        
        // gradient map parser
        parser.putAdditionalInformationParser(LayerGradientMapParser.TAG, new LayerGradientMapParser(new LayerGradientMapHandler() {
			
			@Override
			public void layerGradientMapParsed() {
				
			}
		}));

        parser.putAdditionalInformationParser(LayerEffectsParser.TAG, new LayerEffectsParser(new LayerEffectsHandler() {
			
			@Override
			public void handleEffects(List<PSDEffect> layerEffects) {
				effects = layerEffects;
			}
		}));

        parser.putAdditionalInformationParser(LayerSectionDividerParser.TAG, new LayerSectionDividerParser(new LayerSectionDividerHandler() {
            @Override
            public void sectionDividerParsed(LayerType type) {
                Layer.this.type = type;
            }
        }));
        
        parser.putAdditionalInformationParser(LayerMetaDataParser.TAG, new LayerMetaDataParser(new LayerMetaDataHandler() {
			
        	public void metaDataMlstSectionParsed(PsdDescriptor descriptor) {
				
			}
        	
		}));

        parser.putAdditionalInformationParser(LayerUnicodeNameParser.TAG, new LayerUnicodeNameParser(new LayerUnicodeNameHandler() {
            @Override
            public void layerUnicodeNameParsed(String unicodeName) {
                name = unicodeName;
            }
        }));
        
        parser.putAdditionalInformationParser(LayerTypeToolParser.TAG, new LayerTypeToolParser(new LayerTypeToolHandler() {
			
			@Override
			public void typeToolTransformParsed(Matrix transform) {
				AffineTransform tx = new AffineTransform();
				tx.setTransform(transform.m11(), transform.m12(), transform.m13(), transform.m21(), transform.m22(), transform.m23());
				
				setTextAngle((int) Math.toDegrees(Layer.extractAngle(tx)));
				setTextTransformMatrix(transform);
			}
			
			@Override
			public void typeToolDescriptorParsed(int version, PsdDescriptor descriptor) {
				/*
				 * e.g. descriptor.get("you can use any key from below here to get the data")
				 * AFSt, FrIn, FrID, FrDl, FrGA, FSts, FsID, AFrm, FsFr, LCnt, Txt, textGridding, Ornt, AntA, TextIndex, EngineData
				 */
				
				textInformation.put("Text", descriptor.get("Txt"));
				textInformation.put("Orientation", descriptor.get("Ornt"));
				textInformation.put("EngineData", descriptor.get("EngineData"));
			}
			
		}));
    }
    
    private static double extractAngle(AffineTransform at)
    {
        Point2D p0 = new Point();
        Point2D p1 = new Point(1,0);
        Point2D pp0 = at.transform(p0, null);
        Point2D pp1 = at.transform(p1, null);
        double dx = pp1.getX() - pp0.getX();
        double dy = pp1.getY() - pp0.getY();
        double angle = Math.atan2(dy, dx);
        return angle;
    }

    public void addLayer(Layer layer) {
    	layers.add(layer);
    }

    @Override
    public Layer getLayer(int index) {
        return layers.get(index);
    }

    @Override
    public int indexOfLayer(Layer layer) {
        return layers.indexOf(layer);
    }

    @Override
    public int getLayersCount() {
        return layers.size();
    }
    
    public ArrayList<Layer> getChildren()
    {
    	return layers;
    }

    public BufferedImage getImage() {
        return image;
    }
    
    public void setImage(BufferedImage img)
    {
    	this.image = img;
    }
    
    /*
     * returns the layer visible within visual bounds
     */
    public Rectangle getVisualRectangle(Psd psdFile)
    {
    	if (this.points != null && this.points.size() > 0) {
    		return new Rectangle(this.left, this.top, this.getWidth(), this.getHeight());
    	}
    	
    	int x1 = left < 0 ? 0 : left,
    		x2 = right > psdFile.getWidth() ? psdFile.getWidth() : right;
    	
    	int y1 = top < 0 ? 0 : top,
    		y2 = bottom > psdFile.getHeight() ? psdFile.getHeight() : bottom;
    	
    	return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    public int getX() {
    	return left;
    }
    
    public void setX(int x) {
        this.left = x;
    }
    
    public void setX2(int x2) {
        this.right = x2;
    }

    public int getY() {
    	return top;
    }
    
    public void setY(int y) {
        this.top = y;
    }
    
    public void setY2(int y2) {
        this.bottom = y2;
    }

    public int getWidth() {
    	int width = Math.abs(right - left);
    	
    	/*if (left < 0) {
    		if (right < Psd.header.getWidth()) {
    			width = right;
    		} else {
    			width = Psd.header.getWidth();
    		}
    	}*/
    	
    	return width;
    }
    
    public void updateWidth(int newWidth)
    {
    	this.right = this.left + newWidth - 1;
    }

    public int getHeight() {
    	int height = Math.abs(bottom - top);
    	
    	/*if (top < 0) {
    		if (bottom < Psd.header.getHeight()) {
    			height = bottom;
    		} else {
    			height = Psd.header.getHeight();
    		}
    	}*/
    	
    	return height;
    }
    
    public void adjustPositionAndSizeInformation()
    {
    	if (this.points != null && this.points.size() > 0) {
    		
    		if (this.left < 0) {
    			this.left = 0;
        	} else {
        		this.left = this.points.get(0).x;
        	}
    		
    		if (this.top < 0) {
    			this.top = 0;
    		} else {
    			this.top = this.points.get(0).y;
    		}
    		
    		if (this.right > Psd.header.getWidth()) {
    			this.right = Psd.header.getWidth();
    		} else {
    			if (this.points.size() == 2) {
        			this.right = this.points.get(1).x;
        		} else if (this.points.size() == 4) {
        			this.right = this.points.get(2).x;
        		}
    		}
    		
    		if (this.bottom > Psd.header.getHeight()) {
    			this.bottom = Psd.header.getHeight();
    		} else {
    			if (this.points.size() == 2) {
        			this.bottom = this.points.get(1).y;
        		} else if (this.points.size() == 4) {
        			this.bottom = this.points.get(2).y;
        		}
    		}
    	}
    }

    public LayerType getType() {
        return type;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public String toString() {
    	return String.format("%s#%d-%s", name, getUniqueLayerId(), this.type);
    }
    
    public void setName(String name)
    {
    	this.name = name;
    }

    public int getAlpha() {
        return alpha;
    }
    
    public double getOpacity()
    {
    	return this.alpha / 2.55;
    }
    
    /*
     * custom functions below
     */
    public boolean isTextLayer()
    {
    	if (this.textInformation != null && this.textInformation.containsKey("Text")) {
    		return true;
    	}
    	
    	return false;
    }
    
    @SuppressWarnings("deprecation")
	public String getText()
    {
    	return this.textInformation.get("Text").toString();
    }
    
    public Map<String, PsdObject> getTextInformation()
    {
    	return textInformation;
    }
    
    public List<PSDEffect> getEffects()
    {
    	return effects;
    }
    
    public HashMap<String, Object> getObjectBasedEffects()
    {
    	return objectBasedEffects;
    }
    
    public boolean hasEffects()
    {
    	if (this.effects == null && this.objectBasedEffects == null) {
    		return false;
    	}
    	
    	return true;
    }

	public boolean isShapeLayer() {
		
		if (
			
			this.points != null && 
			this.points.size() > 0
			
			||
			
			(
				this.getWidth() == 1 || this.getHeight() == 1
			)
		) {
			return true;
		}
		
		/*
		 * basic case if there is a keyword shape inside a layer name then it must be a shape
		 */
		/*String tmp = this.toString().toLowerCase();
		if (
				tmp.indexOf("shape") > -1 ||
				tmp.indexOf("rectangle") > -1 ||
				tmp.indexOf("ellipse") > -1 ||
				tmp.indexOf("polygon") > -1 ||
				tmp.indexOf("line") > -1
			) {
			return true;
		}*/
		
		return false;
	}
	
	public void setTransparency()
	{
		this.transparencyProcessed = true;
		
		int transparency = this.image.getTransparency();
		
		/*
		 * we assume transparency to be false otherwise proven
		 */
		this.isTransparent = false;
		
		int pixel;
		
		switch (transparency)
		{
			case Transparency.BITMASK:
				pixel = this.image.getRGB(0, 0);
				
				if ((pixel >> 24) == 0x00) {
					this.isTransparent = true;
				}
				break;
				
			case Transparency.OPAQUE:
				this.isTransparent = false;
				break;
				
			case Transparency.TRANSLUCENT:
				
				pixel = 0;
				
				for (int x = 0; x < this.image.getWidth(); x++) {
					for (int y = 0; y < this.image.getHeight(); y++) {
						pixel = this.image.getRGB(x, y);
						if ((pixel >> 24) == 0x00) {
							this.isTransparent = true;
						}
					}
				}
				
				break;
		}
	}
	
	public boolean checkTransparencyInRegion(Layer layer)
	{
		int offsetX = layer.getX() - this.getX(),
			offsetY = layer.getY() - this.getY();
		
		Rectangle imageRectangle = new Rectangle(this.image.getMinX(), this.image.getMinY(), this.image.getWidth(), this.image.getHeight());
		
		if (!imageRectangle.contains(layer.getRectangle())) {
			return true;
		}
		
		return checkLayerTransparencyInRegion(offsetX, offsetY, layer.getWidth(), layer.getHeight());
	}
	
	/*
	 * we check against a region inside layer
	 * if that part of layer is transparent or not
	 */
	private boolean checkLayerTransparencyInRegion(int x1, int y1, int x2, int y2)
	{
		/*
		 * assuming image is not transparent
		 */
		boolean imageIsTransparent = false;
		
		int pixel,
			transparency = this.image.getTransparency();
		
		switch (transparency)
		{
			case Transparency.BITMASK:
				pixel = this.image.getRGB(0, 0);
				
				if ((pixel >> 24) == 0x00) {
					imageIsTransparent = true;
				}
				break;
				
			case Transparency.OPAQUE:
				imageIsTransparent = false;
				break;
				
			case Transparency.TRANSLUCENT:
				
				OuterLoop:
				for (int x = x1; x < x2; x++) {
					for (int y = y1; y < y2; y++) {
						pixel = this.image.getRGB(x, y);
						if ((pixel >> 24) == 0x00) {
							imageIsTransparent = true;
							break OuterLoop;
						}
					}
				}
				
				break;
		}
		
		return imageIsTransparent;
	}
	
	public boolean hasTransparency()
	{
		if (!this.transparencyProcessed) {
			this.setTransparency();
		}
		
		return this.isTransparent;
	}
	
	public String fileSavingName()
	{
		if (this.fileSavingName != null) {
			return this.fileSavingName;
		}
		
		return Layer.generateFileSavingName(this.name, this);
	}
	
	public static String generateFileSavingName(String nameSent)
	{
		return Layer.generateFileSavingName(nameSent, null);
	}
	
	public static String generateFileSavingName(String nameSent, Layer layer)
	{
		if (layer.fileSavingName != null) {
			return layer.fileSavingName;
		}
		
		String name = nameSent;
		
		name = name.replaceAll("[^a-zA-Z0-9\\s]", "");
		name = name.replaceAll("( )+", " ");
		
		if (name.length() > 25) {
			name = name.substring(0, 25);
		}
		
		name = name.trim();
		name = name.replaceAll("[\\s]", "-");
		
		if (name.length() == 0 || Layer.fileSavingNameList.contains(name)) {
			if (layer.hasImage()) {
				name = "cimage-" + layer.getLayerId();
			} else if (layer.isShapeLayer()) {
				name = "cshape-" + layer.getLayerId();
			} else if (layer.isTextLayer()) {
				name = "ctext-" + layer.getLayerId();
			} else {
				name = "clayer-" + layer.getLayerId();
			}
		}
		
		if (Layer.isNumeric(name.substring(0, 1))) {
			name = "cnumeric" + "-" + name;
		}
		
		name = name.toLowerCase();
		
		layer.setFileSavingName(name);
		
		Layer.fileSavingNameList.add(name);
		
		return name;
	}
	
	public static boolean isNumeric(String str)  
	{
		try {
			double d = Double.parseDouble(str);  
		} catch (NumberFormatException nfe) {
			return false;  
		}
		
		return true;  
	}
	
	public String getImagePath()
	{
		StringBuilder path = new StringBuilder();
		
		path.append(this.fileSavingName());
		
		if (this.hasTransparency()) {
			path.append(".png");
		} else {
			path.append(".jpg");
		}
		
		return path.toString();
	}

	public Rectangle getRectangle() {
		
		return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		
	}
	
	public Rectangle getOverlayedRectangle()
	{
		if (this.type == LayerType.NORMAL) {
			return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		}
		
		/**
		 * otherwise we need to dig deep into layer children to find out the true rectangle width and height of the rectangle
		 */
		int minX, maxX, minY, maxY;
		
		minX = minY = Integer.MAX_VALUE; 
		maxX = maxY = Integer.MIN_VALUE;
		
		if (this.layers.size() > 0) {
			
			Layer l;
			Rectangle r;
			
			for (int i = 0; i < this.layers.size(); i++) {
				
				l = this.layers.get(i);
				
				r = l.getOverlayedRectangle();
				
				minX = minX > r.x ? r.x : minX;
				minY = minY > r.y ? r.y : minY;
				
				maxX = maxX < (r.x + r.width) ? (r.x + r.width) : maxX;
				maxY = maxY < (r.y + r.height) ? (r.y + r.height) : maxY;
			}
			
		}
		
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	public int getLayerId() {
		return layerId;
	}

	public void setLayerId(int layerId) {
		this.layerId = layerId;
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	public boolean hasSolidFill() {
		
		if (this.hasEffects()) {
			List<PSDEffect>effects = this.getEffects();
			
			PSDEffect effect = null;
			
			for (int i = 0; i < effects.size(); i++) {
				effect = effects.get(i);
				
				if (!effect.isEnabled()) {
					continue;
				}
				
				if (effect.getName().equals("sofi")) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public String getSolidFillCSS() {
		
		List<PSDEffect>effects = this.getEffects();
		
		PSDEffect effect = null;
		
		for (int i = 0; i < effects.size(); i++) {
			effect = effects.get(i);
			
			if (!effect.isEnabled()) {
				continue;
			}
			
			if (effect.getName().equals("sofi")) {
				SolidFillEffect sofi = (SolidFillEffect) effect;
				
				if (this.getOpacity() < 100) {
					return String.format("background-color: rgba(%d, %d, %d, %f);", sofi.getNativeColor().getRed(), sofi.getNativeColor().getGreen(), sofi.getNativeColor().getBlue(), this.getOpacity() / 100.0);
				} else {
					return String.format("background-color: #%02x%02x%02x;", sofi.getNativeColor().getRed(), sofi.getNativeColor().getGreen(), sofi.getNativeColor().getBlue());
				}
				
			}
		}
		return "";
	}

	public int getTextAngle() {
		return textAngle;
	}

	public void setTextAngle(int textAngle) {
		this.textAngle = textAngle;
	}

	public boolean hasImage() {
		return this.filePath != null;
	}

	public Matrix getTextTransformMatrix() {
		return textTransformMatrix;
	}

	public void setTextTransformMatrix(Matrix textTransformMatrix) {
		this.textTransformMatrix = textTransformMatrix;
	}

	public int getOldTop() {
		return oldTop;
	}

	public void setOldTop(int oldTop) {
		this.oldTop = oldTop;
	}

	public int getOldLeft() {
		return oldLeft;
	}

	public void setOldLeft(int oldLeft) {
		this.oldLeft = oldLeft;
	}

	public int getOldBottom() {
		return oldBottom;
	}

	public void setOldBottom(int oldBottom) {
		this.oldBottom = oldBottom;
	}

	public int getOldRight() {
		return oldRight;
	}

	public void setOldRight(int oldRight) {
		this.oldRight = oldRight;
	}

	public Rectangle getOldBounds() {
		return new Rectangle(this.oldLeft, this.oldTop, this.oldRight - this.oldLeft + 1, this.oldBottom - this.oldTop + 1);
	}
	
	public String getFileSavingName() {
		return fileSavingName;
	}

	public void setFileSavingName(String fileSavingName) {
		this.fileSavingName = fileSavingName;
	}

	public void setUniqueLayerId(int layerId) {
		this.uniqueLayerId = layerId;
	}
	
	public int getUniqueLayerId()
	{
		return this.uniqueLayerId;
	}

	public void setGroupLayerId(int layerGroupId) {
		this.layerGroupId = layerGroupId;
	}
	
	public int getGroupLayerId()
	{
		return this.layerGroupId;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public int getDepth()
	{
		return this.depth;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.hasImagePath = true;
		this.filePath = filePath;
	}
	
	public boolean isImage()
	{
		return this.hasImagePath;
	}

	/**
	 * @return the md5Hash
	 */
	public String getMd5Hash() {
		return md5Hash;
	}

	/**
	 * @param md5Hash the md5Hash to set
	 */
	public void setMd5Hash(String md5Hash) {
		this.md5Hash = md5Hash;
	}
	
	public boolean isImageSingleColored()
    {
		if (this.hasTransparency()) {
			return false;
		}
		
    	BufferedImage image = this.getImage();
    	ArrayList<String> colors = new ArrayList<String>();
    	
    	int imageWidth = image.getWidth(),
    		imageHeight = image.getHeight();
    	
    	int incrementX = 1,
    		incrementY = 1;
    	
    	if (imageWidth > 5) {
    		incrementX = imageWidth / 5;
    	}
    	
    	if (imageHeight > 5) {
    		incrementY = imageHeight / 5;
    	}
    	
    	String color;
    	
    	boolean isShapeSingleColored = false;
    	
    	SingleColorCheckLoop: {
    		for (int x = 0; x < imageWidth; x += incrementX) {
        		for (int y = 0; y < imageHeight; y += incrementY) {
        			color = String.format("#%06X", (0xFFFFFF & image.getRGB(x, y)));
        			
        			if (!colors.contains(color)) {
        				colors.add(color);
        			}
        			
        			if (colors.size() > 1) {
        				isShapeSingleColored = false;
        				break SingleColorCheckLoop;
        			}
        		}
        	}
    	}
    	
    	/*if (colors.size() > 1) {
    		System.out.println(this.fileSavingName() + " > " + colors);
    	}*/
    	
    	if (colors.size() == 1) {
    		isShapeSingleColored = true;
    	}
    	
    	return isShapeSingleColored;
    }

	public String getCode() {
		
		return LayerHTMLCode.get(this);
		
	}

	public String getFillableCode() {
		
		return LayerHTMLCode.getFillable(this);
		
	}

	/**
	 * @return the styles
	 */
	public String getStyles() {
		return styles;
	}

	/**
	 * @param styles the styles to set
	 */
	public void setStyles(String styles) {
		this.styles = styles;
	}
	
	public String getCSSCode()
	{
		return "." + this.fileSavingName + "{" + this.getStyles() + "}";
	}
}