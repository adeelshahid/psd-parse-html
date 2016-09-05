package htmlstructure;

import java.awt.Rectangle;
import java.util.ArrayList;

public class PSDRectangle extends Rectangle {
	
	private Integer key;
	
	public int minX;
	public int minY;
	
	public int maxX;
	public int maxY;

	public PSDRectangle(int x, int y, int width, int height) {
		super(x, y, width, height);
		
		this.fillPublics();
	}

	public PSDRectangle(Rectangle rectangle) {
		super(rectangle);
		
		this.fillPublics();
	}
	
	public void fillPublics()
	{
		this.minX = this.x;
		this.minY = this.y;
		
		this.maxX = this.x + this.width;
		this.maxY = this.y + this.height;
	}

	/**
	 * @return the key
	 */
	public Integer getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(Integer key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return String.format("[key => %s, x: %d, y: %d, width: %d, height: %d]", key, x, y, width, height);
	}

	public static ArrayList<Integer> getKeys(ArrayList<PSDRectangle> rectangles) {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		
		for (PSDRectangle rect : rectangles) {
			keys.add(rect.getKey());
		}
		
		return keys;
	}

}
