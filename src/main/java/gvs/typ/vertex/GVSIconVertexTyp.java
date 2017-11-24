
package gvs.typ.vertex;

import gvs.typ.GVSDefaultTyp;
import gvs.typ.GVSDefaultTyp.LineColor;
import gvs.typ.GVSDefaultTyp.LineStyle;
import gvs.typ.GVSDefaultTyp.LineThickness;

/**
 * 
 * @author mkoller
 *	
 * Represents a vertex which will be drawn with a image.
 * Linethickness,linecolor,linestyle and icon can be set.*
 */
public class GVSIconVertexTyp extends GVSVertexTyp{

	public enum Icon{COG, PLAY, GLOBE, BELL }
	
	private LineColor lineColor=null;
	private LineStyle lineStyle=null;
	private LineThickness lineThickness=null;
	private Icon icon=null;
	
	public GVSIconVertexTyp(LineColor pLineColor, LineStyle pLineStyle,
								LineThickness pLineThickness, Icon pIcon){
		this.lineColor=pLineColor;
		this.lineStyle=pLineStyle;
		this.lineThickness=pLineThickness;
		this.icon=pIcon;
	}

	/**
	 * Return the linceolor
	 * 
	 * @return linecolor
	 */
	public GVSDefaultTyp.LineColor getLineColor() {
		return lineColor;
	}

	/**
	 * Return the linestyle
	 * 
	 * @return linestyle
	 */
	public GVSDefaultTyp.LineStyle getLineStyle() {
		return lineStyle;
	}

	/**
	 * Return the linethickness
	 * 
	 * @return linethickness
	 */
	public GVSDefaultTyp.LineThickness getLineThickness() {
		return lineThickness;
	}

	/**
	 * Return the image
	 *  
	 * @return icon
	 */
	public Icon getIcon() {
		return icon;
	}
	
}
