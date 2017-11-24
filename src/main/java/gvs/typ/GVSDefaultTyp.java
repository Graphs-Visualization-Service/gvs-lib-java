
package gvs.typ;

/**
 * 
 * @author mkoller
 *
 *         Represents the general typdefinition for all vertex and nodes. That
 *         includes LineColor,LineStyle and LineThickness
 */
public abstract class GVSDefaultTyp {
  public enum LineColor {
    standard, black, gray, ligthGray, red, ligthRed, blue, darkBlue, ligthBlue, green, ligthGreen, darkGreen, turqoise, yellow, brown, orange, pink, violet
  }

  public enum LineStyle {
    standard, dashed, dotted, through
  }

  public enum LineThickness {
    standard, slight, bold, fat
  }
}
