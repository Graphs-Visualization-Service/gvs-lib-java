
package gvs.typ.node;

import gvs.typ.GVSDefaultTyp.LineColor;
import gvs.typ.GVSDefaultTyp.LineStyle;
import gvs.typ.GVSDefaultTyp.LineThickness;
import gvs.typ.vertex.GVSEllipseVertexTyp;

/**
 * 
 * @author mkoller
 *
 * Represents a node which will be drawn as a Ellipse.
 * Linethickness,linecolor,linestyle and fillcolor can be set.
 *   
 */
public class GVSNodeTyp extends GVSEllipseVertexTyp{

	public GVSNodeTyp(LineColor pLineColor, LineStyle pLineStyle, LineThickness pLineThickness, FillColor pFillColor) {
		super(pLineColor, pLineStyle, pLineThickness, pFillColor);
	}

}
