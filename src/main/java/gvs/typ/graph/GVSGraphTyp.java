
package gvs.typ.graph;
/**
 * 
 * @author mkoller
 *
 * Represents a Graphtyp.
 * A backgroundimage can be set.
 *   
 */
public class GVSGraphTyp {
		
	public enum Background{standard,background1,background2,backgrond3,background4,
							background5,background6,background7,background8,background9}
	
	private Background background=null;
	
	public GVSGraphTyp(Background pBackground){
		this.background=pBackground;
	}

	/**
	 * Returns the backgroundtyp
	 * 
	 * @return backgorund
	 */
	public Background getBackground() {
		return background;
	}
	
}
