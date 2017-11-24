
package gvs.graph;

/**
 * Represents a DirectedEdge. Start and end must be set. The Arrow will be drawn
 * at the endvertex.
 * 
 * @author mkoller
 * 
 */
public interface GVSDirectedEdge extends GVSGraphEdge {

  /**
   * Returns the startevertex
   * 
   * @return the startvertex
   */
  public abstract GVSDefaultVertex getGVSStartVertex();

  /**
   * Returns the endvertex
   * 
   * @return the endvertex
   */
  public abstract GVSDefaultVertex getGVSEndVertex();

}
