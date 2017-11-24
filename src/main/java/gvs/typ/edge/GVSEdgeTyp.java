
package gvs.typ.edge;

import gvs.typ.GVSDefaultTyp;

/**
 * 
 * @author Koller Michael
 *
 * Represents a EdgeTyp. Linecolor, linestyle and linethickness
 * can be set
 */
public class GVSEdgeTyp extends GVSDefaultTyp{
	
	private LineColor lineColor=null;
	private LineStyle lineStyle=null;
	private LineThickness lineThickness=null;
	
	public GVSEdgeTyp(LineColor pLineColor, 
						LineStyle pLineStyle,
							LineThickness pLineThickness){
		this.lineColor=pLineColor;
		this.lineStyle=pLineStyle;
		this.lineThickness=pLineThickness;
		
	}
	/**
	 * Returns the linecolor
	 * @return linecolor
	 */
	
	public GVSDefaultTyp.LineColor getLineColor() {
		return lineColor;
	}

	/**
	 * Returns the linestyle
	 * @return linestyle
	 */
	public GVSDefaultTyp.LineStyle getLineStyle() {
		return lineStyle;
	}

	/**
	 * Retunrns the linethickness
	 * @return lineThickness
	 */
	public GVSDefaultTyp.LineThickness getLineThickness() {
		return lineThickness;
	}
		
}
