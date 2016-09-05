package htmlstructure;

import java.util.Comparator;

public class PSDRectangleComparator implements Comparator<PSDRectangle> {
	
	@Override
	public int compare(PSDRectangle r1, PSDRectangle r2) {
		int diff = 0;
		
		diff = (r1.minY > r2.minY) ? (r1.minY - r2.minY) : (r2.minY - r1.minY);
		
		/*
		 * first need to a differential check
		 * if y1 - y2 difference is about 5 px
		 */
		if (diff <= 5) {
			
			/*
			 * if the width's are equal for both of the rectangles
			 * and they lie at an approximate distance of about 1 - 5 pixels
			 * then we need to sort on the y-axis
			 */
			if (r1.width == r2.width) {
				
				/*
				 * determine by y-axis sorting
				 */
				return r1.minY - r2.minY;
				
			}
			
			if (r1.maxX < r2.minX) {
				return -1;
			} else if (r2.maxX < r1.minX) {
				return 1;
			}
		} else if (r1.minY != r2.minY) {
			return r1.minY - r2.minY;
		}
		
		return r1.minX - r2.minX;
	}

}
