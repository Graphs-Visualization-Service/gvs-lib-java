
package gvs.graph;

/**
 * 
 * @author mkoller
 * Represents a RelativeVertex. 
 */
public interface GVSRelativeVertex extends GVSDefaultVertex{
	
	/**
	 * Returns the xPosition. Values between 0-100 are allowed
	 * @return	the xPosition
	 */
	public double getX();
	
	/**
	 * Returns the yPosition. Values between 0-100 are allowed
	 * @return	the yPosition
	 */
	public double getY();
}
