
package gvs.typ.vertex;

import gvs.typ.styles.GVSColor;
import gvs.typ.styles.GVSLineStyle;
import gvs.typ.styles.GVSLineThickness;

/**
 * 
 * Represents a vertex which will be drawn as a Ellipse.
 * Linethickness,linecolor,linestyle and fillcolor can be set.
 * 
 * @author mkoller
 */
public class GVSEllipseVertexTyp extends GVSVertexTyp {

  private final GVSColor lineColor;
  private final GVSLineStyle lineStyle;
  private final GVSLineThickness lineThickness;
  private final GVSColor fillColor;

  public GVSEllipseVertexTyp(GVSColor pLineColor, GVSLineStyle pLineStyle,
      GVSLineThickness pLineThickness, GVSColor pFillColor) {

    this.lineColor = pLineColor;
    this.lineStyle = pLineStyle;
    this.lineThickness = pLineThickness;
    this.fillColor = pFillColor;
  }

  /**
   * Return the linceolor
   * 
   * @return linecolor
   */
  public GVSColor getLineColor() {
    return lineColor;
  }

  /**
   * Return the linestyle
   * 
   * @return linestyle
   */
  public GVSLineStyle getLineStyle() {
    return lineStyle;
  }

  /**
   * Return the linethickness
   * 
   * @return linethickness
   */
  public GVSLineThickness getLineThickness() {
    return lineThickness;
  }

  /**
   * Return the fillcolor
   * 
   * @return fillcolor
   */
  public GVSColor getFillColor() {
    return fillColor;
  }
}
