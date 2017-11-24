
package gvs.typ.node;

import gvs.typ.styles.GVSColor;
import gvs.typ.styles.GVSLineStyle;
import gvs.typ.styles.GVSLineThickness;
import gvs.typ.vertex.GVSEllipseVertexTyp;

/**
 * 
 * Represents a node which will be drawn as a Ellipse.
 * Linethickness,linecolor,linestyle and fillcolor can be set.
 * 
 * @author mkoller
 */
public class GVSNodeTyp extends GVSEllipseVertexTyp {

  public GVSNodeTyp(GVSColor pLineColor, GVSLineStyle pLineStyle,
      GVSLineThickness pLineThickness, GVSColor pFillColor) {
    super(pLineColor, pLineStyle, pLineThickness, pFillColor);
  }

}
