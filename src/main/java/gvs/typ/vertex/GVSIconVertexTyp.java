
package gvs.typ.vertex;

import gvs.typ.styles.GVSColor;
import gvs.typ.styles.GVSIcon;
import gvs.typ.styles.GVSLineStyle;
import gvs.typ.styles.GVSLineThickness;

/**
 * 
 * Represents a vertex which will be drawn with a image.
 * Linethickness,linecolor,linestyle and icon can be set.
 * 
 * @author mkoller
 */
public class GVSIconVertexTyp extends GVSVertexTyp {

  private final GVSColor lineColor;
  private final GVSLineStyle lineStyle;
  private final GVSLineThickness lineThickness;
  private final GVSIcon icon;

  public GVSIconVertexTyp(GVSColor pLineColor, GVSLineStyle pLineStyle,
      GVSLineThickness pLineThickness, GVSIcon pIcon) {

    this.lineColor = pLineColor;
    this.lineStyle = pLineStyle;
    this.lineThickness = pLineThickness;
    this.icon = pIcon;
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
   * Return the image
   * 
   * @return icon
   */
  public GVSIcon getIcon() {
    return icon;
  }

}
