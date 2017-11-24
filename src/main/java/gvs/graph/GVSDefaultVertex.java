package gvs.graph;

import gvs.styles.GVSStyle;

/**
 * This interface is needed for the realization of a DefaultVertex
 * 
 * @author mkoller
 */
public interface GVSDefaultVertex {

  /**
   * Returns the label of the vertex
   * 
   * @return the label. If it is null empty string will be set
   */
  public abstract String getGVSVertexLabel();

  /**
   * Returns the typ of the vertex
   * 
   * @return the typ. If it is null the default typ will be set
   */
  public abstract GVSStyle getStyle();
}
