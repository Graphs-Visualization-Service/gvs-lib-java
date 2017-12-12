package gvs.business.styles;

public class GVSStyle {

  private final GVSColor lineColor;
  private final GVSLineStyle lineStyle;
  private final GVSLineThickness lineThickness;
  private final GVSColor fillColor;
  private final GVSIcon icon;

  public GVSStyle() {
    this(null, null, null, null, null);
  }

  public GVSStyle(GVSColor lineColor, GVSLineStyle lineStyle,
      GVSLineThickness lineThickness) {
    this(lineColor, lineStyle, lineThickness, null, null);
  }

  public GVSStyle(GVSColor lineColor, GVSLineStyle lineStyle,
      GVSLineThickness lineThickness, GVSColor fillColor) {
    this(lineColor, lineStyle, lineThickness, fillColor, null);
  }

  public GVSStyle(GVSColor lineColor, GVSLineStyle lineStyle,
      GVSLineThickness lineThickness, GVSColor fillColor, GVSIcon icon) {

    if (lineColor != null) {
      this.lineColor = lineColor;
    } else {
      this.lineColor = GVSColor.STANDARD;
    }

    if (lineStyle != null) {
      this.lineStyle = lineStyle;
    } else {
      this.lineStyle = GVSLineStyle.THROUGH;
    }

    if (lineThickness != null) {
      this.lineThickness = lineThickness;
    } else {
      this.lineThickness = GVSLineThickness.STANDARD;
    }

    if (fillColor != null) {
      this.fillColor = fillColor;
    } else {
      this.fillColor = GVSColor.STANDARD;
    }

    this.icon = icon;
  }

  public GVSColor getFillColor() {
    return fillColor;
  }

  public GVSColor getLineColor() {
    return lineColor;
  }

  public GVSLineStyle getLineStyle() {
    return lineStyle;
  }

  public GVSLineThickness getLineThickness() {
    return lineThickness;
  }

  public GVSIcon getIcon() {
    return icon;
  }
}
