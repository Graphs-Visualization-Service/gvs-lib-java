
package gvs.business.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.access.XMLConnection;
import gvs.business.styles.GVSStyle;

/**
 * This class represents the graph. Null values are translated on standard or
 * empty strings. The class works over references. It does not play a role, if
 * values are doubly added or removed. The connection information have to be set
 * over Properties. -DGVSPortFile or -DGVSHost and -DGVSPort are supported.
 * 
 * @author mkoller
 */
public class GVSGraph {

  // Connection
  private String host = null;
  private int port = 0;
  private Document document = null;
  private XMLConnection xmlConnection = null;

  // Datas
  private long gvsGraphId = 0;
  private String gvsGraphName = "";

  // Config
  private final String GVSPORTFILE = "GVSPortFile";
  private final String GVSHOST = "GVSHost";
  private final String GVSPORT = "GVSPort";

  // Generally
  private final String ROOT = "GVS";
  private final String ATTRIBUTEID = "Id";
  private final String LABEL = "Label";
  private final String FILLCOLOR = "Fillcolor";
  private final String ICON = "Icon";
  private final String LINECOLOR = "Linecolor";
  private final String LINESTYLE = "Linestyle";
  private final String LINETHICKNESS = "Linethickness";

  // Graph
  private final String GRAPH = "Graph";
  private final String VERTIZES = "Vertizes";
  private final String RELATIVVERTEX = "RelativVertex";
  private final String DEFAULTVERTEX = "DefaultVertex";
  private final String XPOS = "XPos";
  private final String YPOS = "YPos";
  private final String EDGES = "Edges";
  private final String EDGE = "Edge";
  private final String ISDIRECTED = "IsDirected";
  private final String FROMVERTEX = "FromVertex";
  private final String TOVERTEX = "ToVertex";
  private final String ARROWPOS = "DrawArrowOnPosition";

  // Values to send
  private Set<GVSDefaultVertex> gvsGraphVertizes = null;
  private Set<GVSGraphEdge> gvsGraphEdges = null;

  private static final Logger logger = LoggerFactory.getLogger(GVSGraph.class);

  /**
   * Creates the Graph-Object. Id will be set to System.currentTimeMillis() If
   * no properties are set, the default port 3000 and localhost will be applied.
   * 
   * @param pGVSGraphName
   * @param pGVSGraphTyp
   */
  public GVSGraph(String pGVSGraphName) {
    this.gvsGraphId = System.currentTimeMillis();
    this.gvsGraphName = pGVSGraphName;
    if (this.gvsGraphName == null) {
      this.gvsGraphName = "";
      logger.debug("GraphName null. Set it to empty");
    }

    this.gvsGraphEdges = new HashSet<GVSGraphEdge>();
    this.gvsGraphVertizes = new HashSet<GVSDefaultVertex>();

    String propPortfile = System.getProperty(GVSPORTFILE);
    String propHost = System.getProperty(GVSHOST);
    String propPort = System.getProperty(GVSPORT);

    // Set Portfilepath from VM
    if (propPortfile != null) {
      SAXReader reader = new SAXReader();
      logger.info("Load Communication from Portfile");
      try {
        // Read Portfile
        Document document = reader.read(propPortfile);
        Element docRoot = document.getRootElement();
        Element eHost = docRoot.element("Host");
        Element ePort = docRoot.element("Port");
        if (eHost != null && ePort != null) {
          host = eHost.getText();
          port = Integer.parseInt(ePort.getText());
        } else {
          logger.error("Error while Portfile loading. System exit");
          System.exit(0);
        }

      } catch (DocumentException e) {
        logger.error("Error while Portfile loading. System exit", e);
        System.exit(0);
      }
    }

    // Set Host and Port from VM
    else if (propHost != null && propPort != null) {
      logger.info("Load Communication from Host and Port");
      try {
        this.host = propHost;
        this.port = Integer.parseInt(propPort);
        logger.info("Host: " + host + " Port: " + port);
      } catch (Exception ex) {
        logger.error("Error port or host. System exit", ex);
        System.exit(0);
      }

    }

    // Set Defaultvalues
    else {
      logger.info("Set default for host and port");
      this.host = "127.0.0.1";
      this.port = 3000;
      logger.info("host: " + host + " port: " + port);
    }

    xmlConnection = new XMLConnection(host, port);
    xmlConnection.connectToServer();
  }

  /**
   * Add a DefaultVertex
   * 
   * @param pGVSVertex
   */
  public void add(GVSDefaultVertex pGVSVertex) {
    this.gvsGraphVertizes.add(pGVSVertex);
    logger.debug("DefaultVertex added");
  }

  /**
   * Add a RelativVertex
   * 
   * @param pGVSVertex
   */
  public void add(GVSRelativeVertex pGVSVertex) {
    this.gvsGraphVertizes.add(pGVSVertex);
    logger.debug("RelativVertex added");
  }

  /**
   * Add a DirectedEdge
   * 
   * @param pGVSEdge
   */
  public void add(GVSUndirectedEdge pGVSEdge) {
    this.gvsGraphEdges.add(pGVSEdge);
    logger.debug("UndiectedEdge added");
  }

  /**
   * Add a UndirectedEdge
   * 
   * @param pGVSEdge
   */
  public void add(GVSDirectedEdge pGVSEdge) {
    this.gvsGraphEdges.add(pGVSEdge);
    logger.debug("DirectedEdge added");
  }

  /**
   * Add a Collection of GVSComponents.
   * 
   * @param pGVSComponent
   */
  public void add(Collection<?> pGVSComponent) {
    logger.debug("Beginn to add a Collection");
    Iterator<?> componentIterator = pGVSComponent.iterator();
    while (componentIterator.hasNext()) {
      Object tmp = componentIterator.next();
      Class<?>[] interfaces = tmp.getClass().getInterfaces();
      for (int count = 0; count < interfaces.length; count++) {
        if (interfaces[count] == GVSDirectedEdge.class) {
          this.add((GVSDirectedEdge) tmp);
        } else if (interfaces[count] == GVSUndirectedEdge.class) {
          this.add((GVSUndirectedEdge) tmp);
        } else if (interfaces[count] == GVSDefaultVertex.class) {
          this.add((GVSDefaultVertex) tmp);
        } else if (interfaces[count] == GVSRelativeVertex.class) {
          this.add((GVSRelativeVertex) tmp);
        }
      }
    }
    logger.debug("Finish add a Collection");
  }

  /**
   * Add a Array of DirectedEdges
   * 
   * @param pGVSEdge
   */
  public void add(GVSDirectedEdge[] pGVSEdge) {
    for (int count = 0; count < pGVSEdge.length; count++) {
      this.gvsGraphEdges.add(pGVSEdge[count]);
    }
    logger.debug("DirectedEdge[] added");
  }

  /**
   * Add a Array of UndirectedEdges
   * 
   * @param pGVSEdge
   */
  public void add(GVSUndirectedEdge[] pGVSEdge) {
    for (int count = 0; count < pGVSEdge.length; count++) {
      this.gvsGraphEdges.add(pGVSEdge[count]);
    }
    logger.debug("UndirectedEdge[] added");
  }

  /**
   * Add a Array of DefaultVertizes
   * 
   * @param pGVSVertex
   */
  public void add(GVSDefaultVertex[] pGVSVertex) {
    for (int count = 0; count < pGVSVertex.length; count++) {
      this.gvsGraphVertizes.add(pGVSVertex[count]);
    }
    logger.debug("DefaultVertex[] added");
  }

  /**
   * Add a Array of RelativVertizes
   * 
   * @param pGVSVertex
   */
  public void add(GVSRelativeVertex[] pGVSVertex) {
    for (int count = 0; count < pGVSVertex.length; count++) {
      this.gvsGraphVertizes.add(pGVSVertex[count]);
    }
    logger.debug("RealtivVertex[] added");
  }

  /**
   * Remove a DefaultVertex. Connected edges will be removed to
   * 
   * @param pGVSVertex
   */
  public void remove(GVSDefaultVertex pGVSVertex) {
    logger.debug("Begin to remove DefaultVertex");
    Vector<GVSGraphEdge> toRemove = new Vector<>();
    Iterator<GVSGraphEdge> edgeIt = gvsGraphEdges.iterator();
    while (edgeIt.hasNext()) {
      Object tmp = edgeIt.next();
      Class<?>[] interfaces = tmp.getClass().getInterfaces();
      for (int count = 0; count < interfaces.length; count++) {
        if (interfaces[count] == GVSDirectedEdge.class) {
          GVSDirectedEdge de = (GVSDirectedEdge) tmp;
          if (de.getGVSStartVertex() == pGVSVertex) {
            toRemove.add(de);
            logger.debug("DirectedEdge to remove found");
          }
          if (de.getGVSEndVertex() == pGVSVertex) {
            toRemove.add(de);
            logger.debug("DirectedEdge to remove found");
          }
        } else if (interfaces[count] == GVSUndirectedEdge.class) {
          GVSUndirectedEdge ue = (GVSUndirectedEdge) tmp;
          GVSDefaultVertex[] vertizes = ue.getGVSVertizes();
          for (int counter = 0; counter < vertizes.length; counter++) {
            if (vertizes[counter] == pGVSVertex) {
              toRemove.add(ue);
              logger.debug("UndiectedEdge to remove found");
            }
          }
        }
      }
    }
    Iterator<GVSGraphEdge> removeIt = toRemove.iterator();
    while (removeIt.hasNext()) {
      gvsGraphEdges.remove(removeIt.next());
      logger.debug("Remove founded Edges");
    }
    this.gvsGraphVertizes.remove(pGVSVertex);
    logger.debug("Finish remove DefaultVertex");
  }

  /**
   * Remove a RealtivVertex. Connected edges will be removed to
   * 
   * @param pGVSVertex
   */
  public void remove(GVSRelativeVertex pGVSVertex) {
    logger.debug("Begin to remove relativtVertex");
    Vector<GVSGraphEdge> toRemove = new Vector<>();
    Iterator<GVSGraphEdge> edgeIt = gvsGraphEdges.iterator();
    while (edgeIt.hasNext()) {
      Object tmp = edgeIt.next();
      Class<?>[] interfaces = tmp.getClass().getInterfaces();
      for (int count = 0; count < interfaces.length; count++) {
        if (interfaces[count] == GVSDirectedEdge.class) {
          GVSDirectedEdge de = (GVSDirectedEdge) tmp;
          if (de.getGVSStartVertex() == pGVSVertex) {
            toRemove.add(de);
            logger.debug("DirectedEdge to remove found");
          }
          if (de.getGVSEndVertex() == pGVSVertex) {
            toRemove.add(de);
            logger.debug("DirectedEdge to remove found");
          }
        } else if (interfaces[count] == GVSUndirectedEdge.class) {
          GVSUndirectedEdge ue = (GVSUndirectedEdge) tmp;
          GVSDefaultVertex[] vertizes = ue.getGVSVertizes();
          for (int counter = 0; counter < vertizes.length; counter++) {
            if (vertizes[counter] == pGVSVertex) {
              toRemove.add(ue);
              logger.debug("UndiectedEdge to remove found");
            }
          }
        }
      }
    }
    Iterator<GVSGraphEdge> removeIt = toRemove.iterator();
    while (removeIt.hasNext()) {
      gvsGraphEdges.remove(removeIt.next());
      logger.debug("remove founded Edges");
    }
    this.gvsGraphVertizes.remove(pGVSVertex);
    logger.debug("Finish remove RealtivVertex");
  }

  /**
   * Remove a DirectedEdge
   * 
   * @param pGVSEdge
   */
  public void remove(GVSDirectedEdge pGVSEdge) {
    this.gvsGraphEdges.remove(pGVSEdge);
    logger.debug("Remove DirectedEdge");
  }

  /**
   * Remove a UndirectedEdge
   * 
   * @param pGVSEdge
   */
  public void remove(GVSUndirectedEdge pGVSEdge) {
    this.gvsGraphEdges.remove(pGVSEdge);
    logger.debug("Remove UndirectedEdge");
  }

  /**
   * Remove a Collection of GVSComponents.
   * 
   * @param pGVSComponent
   */
  public void remove(Collection<Object> pGVSComponent) {
    logger.debug("Beginn to remove a Collection");
    Iterator<Object> componentIterator = pGVSComponent.iterator();
    while (componentIterator.hasNext()) {
      Object tmp = componentIterator.next();
      Class<?>[] interfaces = tmp.getClass().getInterfaces();
      for (int count = 0; count < interfaces.length; count++) {
        if (interfaces[count] == GVSDirectedEdge.class) {
          logger.debug("DirectedEdge found");
          this.remove((GVSDirectedEdge) tmp);
        } else if (interfaces[count] == GVSUndirectedEdge.class) {
          logger.debug("UndirectedEdge found");
          this.remove((GVSUndirectedEdge) tmp);
        } else if (interfaces[count] == GVSDefaultVertex.class) {
          logger.debug("DefaultVertex found");
          this.remove((GVSDefaultVertex) tmp);
        } else if (interfaces[count] == GVSRelativeVertex.class) {
          logger.debug("RelativVertex found");
          this.remove((GVSRelativeVertex) tmp);
        }
      }
    }
    logger.debug("Finish remove Collection");
  }

  /**
   * Remove a Array of DirectedEdges
   * 
   * @param pGVSEdge
   */
  public void remove(GVSDirectedEdge[] pGVSEdge) {
    for (int count = 0; count < pGVSEdge.length; count++) {
      this.gvsGraphEdges.remove(pGVSEdge[count]);
    }
    logger.debug("DirectedEdge[] removed");
  }

  /**
   * Remove a Array of UndirectedEdges
   * 
   * @param pGVSEdge
   */
  public void remove(GVSUndirectedEdge[] pGVSEdge) {
    for (int count = 0; count < pGVSEdge.length; count++) {
      this.gvsGraphEdges.remove(pGVSEdge[count]);
    }
    logger.debug("UnirectedEdge[] removed");
  }

  /**
   * Remove a Array of DefaultVertizes
   * 
   * @param pGVSVertex
   */
  public void remove(GVSDefaultVertex[] pGVSVertex) {
    for (int count = 0; count < pGVSVertex.length; count++) {
      this.remove(pGVSVertex[count]);
    }
    logger.debug("DefaultVertex[] removed");
  }

  /**
   * Remove a Array of RealtiveVertizes
   * 
   * @param pGVSVertex
   */
  public void remove(GVSRelativeVertex[] pGVSVertex) {
    for (int count = 0; count < pGVSVertex.length; count++) {
      this.remove(pGVSVertex[count]);
    }
    logger.debug("RelativVertex[] removed");
  }

  /**
   * Build the Xml and send it to the GVSServer
   *
   */
  public void display() {

    logger.info("Start bilding XML.....");
    document = DocumentHelper.createDocument();

    logger.debug("Build GraphElements");
    Element docRoot = document.addElement(ROOT);

    Element graph = docRoot.addElement(GRAPH);
    graph.addAttribute(ATTRIBUTEID, String.valueOf(this.gvsGraphId));
    Element graphLabel = graph.addElement(LABEL);
    graphLabel.addText(this.gvsGraphName);

    Element vertizes = docRoot.addElement(VERTIZES);
    logger.debug("Build Vertizes-Elements");
    Iterator<GVSDefaultVertex> vertexIterator = gvsGraphVertizes.iterator();
    while (vertexIterator.hasNext()) {
      Object tmp = vertexIterator.next();
      Class<?>[] interfaces = tmp.getClass().getInterfaces();
      for (int count = 0; count < interfaces.length; count++) {
        if (interfaces[count] == GVSDefaultVertex.class) {
          GVSDefaultVertex vertex = (GVSDefaultVertex) tmp;
          if (vertex != null) {
            logger.debug("Build DefaultVertex");
            buildDefaultVertex(vertizes, vertex);
          } else {
            logger.warn("DefaultVertex is null");
          }
          break;
        } else if (interfaces[count] == GVSRelativeVertex.class) {
          GVSRelativeVertex vertex = (GVSRelativeVertex) tmp;
          if (vertex != null) {
            logger.debug("Build RealtivVertex");
            buildRelativVertex(vertizes, vertex);
          } else {
            logger.warn("RealtivVertex is null");
          }
          break;
        }
      }

    }

    Element edges = docRoot.addElement(EDGES);
    logger.debug("Build Edge-Elements");
    Iterator<GVSGraphEdge> edgeIterator = gvsGraphEdges.iterator();
    while (edgeIterator.hasNext()) {
      Object tmp = edgeIterator.next();
      Class<?>[] interfaces = tmp.getClass().getInterfaces();
      for (int count = 0; count < interfaces.length; count++) {
        if (interfaces[count] == GVSDirectedEdge.class) {
          GVSDirectedEdge edge = (GVSDirectedEdge) tmp;
          if (edge != null) {
            logger.debug("Build DirectedEdge");
            buildDirectedEdge(edges, edge);
          } else {
            logger.warn("DirectedEdge null");
          }
          break;
        } else if (interfaces[count] == GVSUndirectedEdge.class) {
          GVSUndirectedEdge edge = (GVSUndirectedEdge) tmp;
          if (edge != null) {
            logger.debug("Build UndirectedEdge");
            buildUndirectedEdge(edges, edge);
          } else {
            logger.warn("UndirectedEdge null");
          }
          break;
        }
      }
    }

    logger.info("Finish building XML");
    xmlConnection.sendFile(document);

  }

  /**
   * Disconnect from the Server. It have to be called for proper datatransfer
   *
   */
  public void disconnect() {
    xmlConnection.disconnectFromServer();
  }

  protected void finalize() throws Throwable {
    super.finalize();
    this.disconnect();
  }

  // ***********************************XML
  // Builders*************************************

  private void buildDefaultVertex(Element pParent, GVSDefaultVertex pVertex) {
    logger.debug("Create DefaultVertex-->XML");
    Element defaultVertex = pParent.addElement(DEFAULTVERTEX);
    defaultVertex.addAttribute(ATTRIBUTEID, String.valueOf(pVertex.hashCode()));

    GVSStyle nodeStyle = pVertex.getStyle();
    if (nodeStyle == null) {
      nodeStyle = new GVSStyle();
    }

    Element label = defaultVertex.addElement(LABEL);
    String vertexLabel = pVertex.getGVSVertexLabel();
    if (vertexLabel == null) {
      vertexLabel = "";
    }

    // TODO check if style.name() is okay -> or better get color string
    label.addText(vertexLabel);
    Element lineColor = defaultVertex.addElement(LINECOLOR);
    lineColor.addText(nodeStyle.getLineColor().name());
    Element lineStyle = defaultVertex.addElement(LINESTYLE);
    lineStyle.addText(nodeStyle.getLineStyle().name());
    Element lineThick = defaultVertex.addElement(LINETHICKNESS);
    lineThick.addText(nodeStyle.getLineThickness().name());
    Element fillColor = defaultVertex.addElement(FILLCOLOR);
    fillColor.addText(nodeStyle.getFillColor().name());

    if (nodeStyle.getIcon() != null) {
      Element icon = defaultVertex.addElement(ICON);
      icon.addText(nodeStyle.getIcon().name());
    }
  }

  private void buildRelativVertex(Element pParent, GVSRelativeVertex pVertex) {
    logger.debug("Create RealtivVertex-->XML");
    Element relativeVertex = pParent.addElement(RELATIVVERTEX);
    relativeVertex.addAttribute(ATTRIBUTEID,
        String.valueOf(pVertex.hashCode()));

    GVSStyle nodeStyle = pVertex.getStyle();
    if (nodeStyle == null) {
      nodeStyle = new GVSStyle();
    }

    Element label = relativeVertex.addElement(LABEL);
    String vertexLabel = pVertex.getGVSVertexLabel();
    if (vertexLabel == null) {
      vertexLabel = "";
    }
    label.addText(vertexLabel);
    Element lineColor = relativeVertex.addElement(LINECOLOR);
    lineColor.addText(nodeStyle.getLineColor().name());
    Element lineStyle = relativeVertex.addElement(LINESTYLE);
    lineStyle.addText(nodeStyle.getLineStyle().name());
    Element lineThick = relativeVertex.addElement(LINETHICKNESS);
    lineThick.addText(nodeStyle.getLineThickness().name());
    Element fillColor = relativeVertex.addElement(FILLCOLOR);
    fillColor.addText(nodeStyle.getFillColor().name());
    Element xPos = relativeVertex.addElement(XPOS);
    xPos.addText(String.valueOf(pVertex.getX()));
    Element yPos = relativeVertex.addElement(YPOS);
    yPos.addText(String.valueOf(pVertex.getY()));

    if (nodeStyle.getIcon() != null) {
      Element icon = relativeVertex.addElement(ICON);
      icon.addText(nodeStyle.getIcon().name());
    }
  }

  private void buildDirectedEdge(Element pParent, GVSDirectedEdge pEdge) {
    logger.debug("Create DirectedEdge-->XML");
    GVSDefaultVertex vertex1 = pEdge.getGVSStartVertex();
    GVSDefaultVertex vertex2 = pEdge.getGVSEndVertex();
    boolean vertex1Exist = false;
    boolean vertex2Exist = false;

    Iterator<GVSDefaultVertex> vertexIt = gvsGraphVertizes.iterator();
    while (vertexIt.hasNext()) {
      GVSDefaultVertex tmp = (GVSDefaultVertex) vertexIt.next();
      if (tmp == vertex1) {
        vertex1Exist = true;
      }
      if (tmp == vertex2) {
        vertex2Exist = true;
      }
    }
    if (vertex1Exist == true && vertex2Exist == true && vertex1 != null
        && vertex2 != null) {

      Element directedEdge = pParent.addElement(EDGE);
      directedEdge.addAttribute(ATTRIBUTEID, String.valueOf(pEdge.hashCode()));
      directedEdge.addAttribute(ISDIRECTED, "true");

      GVSStyle style = pEdge.getStyle();
      if (style == null) {
        style = new GVSStyle();
      }

      Element label = directedEdge.addElement(LABEL);
      String edgeLabel = pEdge.getGVSEdgeLabel();
      if (edgeLabel == null) {
        edgeLabel = "";
      }
      label.addText(edgeLabel);
      Element lineColor = directedEdge.addElement(LINECOLOR);
      lineColor.addText(style.getLineColor().name());
      Element lineStyle = directedEdge.addElement(LINESTYLE);
      lineStyle.addText(style.getLineStyle().name());
      Element lineThick = directedEdge.addElement(LINETHICKNESS);
      lineThick.addText(style.getLineThickness().name());
      Element fromVertex = directedEdge.addElement(FROMVERTEX);
      fromVertex.addText(String.valueOf(pEdge.getGVSStartVertex().hashCode()));
      Element toVertex = directedEdge.addElement(TOVERTEX);
      toVertex.addText(String.valueOf(pEdge.getGVSEndVertex().hashCode()));
    }
  }

  private void buildUndirectedEdge(Element pParent, GVSUndirectedEdge pEdge) {
    logger.debug("Create UnirectedEdge-->XML");
    GVSDefaultVertex vertex1 = pEdge.getGVSVertizes()[0];
    GVSDefaultVertex vertex2 = pEdge.getGVSVertizes()[1];
    boolean vertex1Exist = false;
    boolean vertex2Exist = false;

    Iterator<GVSDefaultVertex> vertexIt = gvsGraphVertizes.iterator();
    while (vertexIt.hasNext()) {
      GVSDefaultVertex tmp = (GVSDefaultVertex) vertexIt.next();
      if (tmp == vertex1) {
        vertex1Exist = true;
      }
      if (tmp == vertex2) {
        vertex2Exist = true;
      }
    }
    if (vertex1Exist == true && vertex2Exist == true && vertex1 != null
        && vertex2 != null) {
      Element undirectedEdge = pParent.addElement(EDGE);
      undirectedEdge.addAttribute(ATTRIBUTEID,
          String.valueOf(pEdge.hashCode()));
      int arrowPos = pEdge.hasArrow();
      undirectedEdge.addAttribute(ISDIRECTED, "false");
      undirectedEdge.addAttribute(ARROWPOS, String.valueOf(arrowPos));

      GVSStyle nodeStyle = pEdge.getStyle();
      if (nodeStyle == null) {
        nodeStyle = new GVSStyle();
      }
      Element label = undirectedEdge.addElement(LABEL);
      String edgeLabel = pEdge.getGVSEdgeLabel();
      if (edgeLabel == null) {
        edgeLabel = "";
      }
      label.addText(edgeLabel);
      Element lineColor = undirectedEdge.addElement(LINECOLOR);
      lineColor.addText(nodeStyle.getLineColor().name());
      Element lineStyle = undirectedEdge.addElement(LINESTYLE);
      lineStyle.addText(nodeStyle.getLineStyle().name());
      Element lineThick = undirectedEdge.addElement(LINETHICKNESS);
      lineThick.addText(nodeStyle.getLineThickness().name());

      Element fromVertex = undirectedEdge.addElement(FROMVERTEX);
      fromVertex.addText(String.valueOf(pEdge.getGVSVertizes()[0].hashCode()));
      Element toVertex = undirectedEdge.addElement(TOVERTEX);
      toVertex.addText(String.valueOf(pEdge.getGVSVertizes()[1].hashCode()));
    }
  }
}
