
package gvs.typ.edge;

import gvs.typ.styles.GVSColor;
import gvs.typ.styles.GVSLineStyle;
import gvs.typ.styles.GVSLineThickness;

/**
 * Represents a EdgeTyp. Linecolor, linestyle and linethickness can be set.
 * 
 * @author Koller Michael
 */
public class GVSEdgeTyp {

  private GVSColor lineColor;
  private GVSLineStyle lineStyle;
  private GVSLineThickness lineThickness;

  public GVSEdgeTyp(GVSColor pLineColor, GVSLineStyle pLineStyle,
      GVSLineThickness pLineThickness) {

    this.lineColor = pLineColor;
    this.lineStyle = pLineStyle;
    this.lineThickness = pLineThickness;

  }

  /**
   * Returns the linecolor
   * 
   * @return linecolor
   */
  public GVSColor getLineColor() {
    return lineColor;
  }

  /**
   * Returns the linestyle
   * 
   * @return linestyle
   */
  public GVSLineStyle getLineStyle() {
    return lineStyle;
  }

  /**
   * Retunrns the linethickness
   * 
   * @return lineThickness
   */
  public GVSLineThickness getLineThickness() {
    return lineThickness;
  }

}
