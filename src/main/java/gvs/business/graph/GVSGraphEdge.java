
package gvs.business.graph;

import gvs.business.styles.GVSStyle;

/**
 * 
 * Super class for Edges. Do not use
 * 
 * @author mkoller
 */

public interface GVSGraphEdge {

  /**
   * Returns the edgelabel.
   * 
   * @return the edgelabel. If it is null the label will be set to empty
   */
  public abstract String getGVSEdgeLabel();

  /**
   * Returns the edgetyp
   * 
   * @return the edgetyp. If it is null the default typ will be set
   */
  public abstract GVSStyle getStyle();

}
