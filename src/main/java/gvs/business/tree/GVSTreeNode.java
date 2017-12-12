
package gvs.business.tree;

import java.util.Collection;

import gvs.business.styles.GVSStyle;

/**
 * 
 * @author mkoller Superclass for nodes. Do not use
 */
public abstract interface GVSTreeNode {

  /**
   * Returns the label of the node
   * 
   * @return nodeLabel. If it is null empty string will be set
   */
  public String getNodeLabel();

  /**
   * Returns the typ of the node
   * 
   * @return nodeTyp. If it is null the default typ will be set
   */
  public GVSStyle getStyle();
  
  /**
   * Returns all the children of the node
   * 
   * @return children
   */
  public GVSTreeNode[] children();
}
