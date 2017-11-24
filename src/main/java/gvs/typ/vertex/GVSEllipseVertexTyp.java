
package gvs.typ.vertex;

import gvs.typ.GVSDefaultTyp;
import gvs.typ.GVSDefaultTyp.LineColor;
import gvs.typ.GVSDefaultTyp.LineStyle;
import gvs.typ.GVSDefaultTyp.LineThickness;

/**
 * 
 * @author mkoller
 * 
 *         Represents a vertex which will be drawn as a Ellipse.
 *         Linethickness,linecolor,linestyle and fillcolor can be set.
 * 
 */
public class GVSEllipseVertexTyp extends GVSVertexTyp {

  public enum FillColor {
    standard, gray, ligthGray, red, ligthRed, blue, darkBlue, ligthBlue, green, ligthGreen, darkGreen, turqoise, yellow, brown, orange, pink, violet
  }

  private LineColor lineColor = null;
  private LineStyle lineStyle = null;
  private LineThickness lineThickness = null;
  private FillColor fillColor = null;

  public GVSEllipseVertexTyp(LineColor pLineColor, LineStyle pLineStyle,
      LineThickness pLineThickness, FillColor pFillColor) {
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
   * Return the fillcolor
   * 
   * @return fillcolor
   */
  public FillColor getFillColor() {
    return fillColor;
  }

}
