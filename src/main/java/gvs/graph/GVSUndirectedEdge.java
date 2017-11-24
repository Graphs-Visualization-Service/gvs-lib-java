
package gvs.graph;

/**
 * 
 * Represents a Undirectededge. The Edge could be drawn with an Arrow
 * 
 * @author mkoller
 * 
 */

public interface GVSUndirectedEdge extends GVSGraphEdge {

  /**
   * Returns the connected nodes
   * 
   * @return the 2 Nodes which are connected
   */
  public GVSDefaultVertex[] getGVSVertizes();

  /**
   * Returns the Position of the Arrow. 1 and 2 are allowed. Values greater or
   * lower than 1 or 2 takes no effect
   * 
   * @return the Arrow position
   */
  public int hasArrow();
}
