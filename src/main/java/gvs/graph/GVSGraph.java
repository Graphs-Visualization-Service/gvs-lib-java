
package gvs.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.connection.XMLConnection;
import gvs.typ.edge.GVSEdgeTyp;
import gvs.typ.graph.GVSGraphTyp;
import gvs.typ.graph.GVSGraphTyp.Background;
import gvs.typ.vertex.GVSEllipseVertexTyp;
import gvs.typ.vertex.GVSIconVertexTyp;
import gvs.typ.vertex.GVSVertexTyp;

/**
 * 
 * @author mkoller
 *
 *         This class represents the graph. Null values are translated on
 *         standard or empty strings. The class works over references. It does
 *         not play a role, if values are doubly added or removed. The
 *         connectioninformation have to be set over Properties. -DGVSPortFile
 *         or -DGVSHost and -DGVSPort are supported.
 * 
 **/

public class GVSGraph {

  // Connection
  private String host = null;
  private int port = 0;
  private Document document = null;
  private XMLConnection xmlConnection = null;

  // Datas
  private long gvsGraphId = 0;
  private String gvsGraphName = "";
  private GVSGraphTyp gvsGraphTyp = null;
  private int maxLabelLength = 0;

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
  private final String STANDARD = "standard";

  // Graph
  private final String GRAPH = "Graph";
  private final String BACKGROUND = "Background";
  private final String MAXLABELLENGTH = "MaxLabelLength";
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
  private HashSet<GVSDefaultVertex> gvsGraphVertizes = null;
  private HashSet<GVSGraphEdge> gvsGraphEdges = null;

  // Deafaultgraphtyp
  private static GVSGraphTyp defaultGraphTyp = new GVSGraphTyp(
      Background.standard);

  private static final Logger logger = LoggerFactory.getLogger(GVSGraph.class);

  /**
   * Creates a Graph with default background
   * 
   * @param pGVSGraphName
   */
  public GVSGraph(String pGVSGraphName) {
    this(pGVSGraphName, null);
  }

  /**
   * Creates the Graph-Object. Id will be set to System.currentTimeMillis() If
   * no properties are set, the default port 3000 and localhost will be applied.
   * 
   * @param pGVSGraphName
   * @param pGVSGraphTyp
   */
  public GVSGraph(String pGVSGraphName, GVSGraphTyp pGVSGraphTyp) {
    this.gvsGraphId = System.currentTimeMillis();
    this.gvsGraphName = pGVSGraphName;
    if (this.gvsGraphName == null) {
      this.gvsGraphName = "";
      logger.debug("GraphName null. Set it to empty");
    }
    this.gvsGraphTyp = pGVSGraphTyp;
    if (this.gvsGraphTyp == null) {
      this.gvsGraphTyp = defaultGraphTyp;
      logger.debug("GraphTyp null. Set it to default");
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
  public void add(Collection pGVSComponent) {
    logger.debug("Beginn to add a Collection");
    Iterator componentIterator = pGVSComponent.iterator();
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
    Vector<GVSGraphEdge> toRemove = new Vector<GVSGraphEdge>();
    Iterator edgeIt = gvsGraphEdges.iterator();
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
    Iterator removeIt = toRemove.iterator();
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
    Vector<GVSGraphEdge> toRemove = new Vector<GVSGraphEdge>();
    Iterator edgeIt = gvsGraphEdges.iterator();
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
    Iterator removeIt = toRemove.iterator();
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
  public void remove(Collection pGVSComponent) {
    logger.debug("Beginn to remove a Collection");
    Iterator componentIterator = pGVSComponent.iterator();
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
        }

        else if (interfaces[count] == GVSDefaultVertex.class) {
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
   * Set the maxLabelLength.
   * 
   * @param pMaxLength
   */
  public void setMaxLabelLength(int pMaxLength) {
    this.maxLabelLength = pMaxLength;
    logger.debug("MaxLabelLength set to " + maxLabelLength);
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
    Element graphBackground = graph.addElement(BACKGROUND);
    graphBackground.addText(this.gvsGraphTyp.getBackground().name());
    Element maxLabelLength = graph.addElement(MAXLABELLENGTH);
    maxLabelLength.addText(String.valueOf(this.maxLabelLength));

    Element vertizes = docRoot.addElement(VERTIZES);
    logger.debug("Build Vertizes-Elements");
    Iterator vertexIterator = gvsGraphVertizes.iterator();
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
    Iterator edgeIterator = gvsGraphEdges.iterator();
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
    GVSVertexTyp vertexTypNull = pVertex.getGVSVertexTyp();
    if (vertexTypNull != null) {
      if (pVertex.getGVSVertexTyp().getClass() == GVSEllipseVertexTyp.class) {
        logger.debug("Create DefaultVertex with ELLIPSE");
        GVSEllipseVertexTyp vertexTyp = ((GVSEllipseVertexTyp) (pVertex
            .getGVSVertexTyp()));
        Element label = defaultVertex.addElement(LABEL);
        String vertexLabel = pVertex.getGVSVertexLabel();
        if (vertexLabel == null) {
          vertexLabel = "";
        }
        label.addText(vertexLabel);
        Element lineColor = defaultVertex.addElement(LINECOLOR);
        lineColor.addText(vertexTyp.getLineColor().name());
        Element lineStyle = defaultVertex.addElement(LINESTYLE);
        lineStyle.addText(vertexTyp.getLineStyle().name());
        Element lineThick = defaultVertex.addElement(LINETHICKNESS);
        lineThick.addText(vertexTyp.getLineThickness().name());
        Element fillColor = defaultVertex.addElement(FILLCOLOR);
        fillColor.addText(vertexTyp.getFillColor().name());
      } else if (pVertex.getGVSVertexTyp()
          .getClass() == GVSIconVertexTyp.class) {
        logger.debug("Create DefaultVertex with ICON");
        GVSIconVertexTyp vertexTyp = ((GVSIconVertexTyp) (pVertex
            .getGVSVertexTyp()));
        Element label = defaultVertex.addElement(LABEL);
        String vertexLabel = pVertex.getGVSVertexLabel();
        if (vertexLabel == null) {
          vertexLabel = "";
        }
        label.addText(vertexLabel);
        Element lineColor = defaultVertex.addElement(LINECOLOR);
        lineColor.addText(vertexTyp.getLineColor().name());
        Element lineStyle = defaultVertex.addElement(LINESTYLE);
        lineStyle.addText(vertexTyp.getLineStyle().name());
        Element lineThick = defaultVertex.addElement(LINETHICKNESS);
        lineThick.addText(vertexTyp.getLineThickness().name());
        Element icon = defaultVertex.addElement(ICON);
        icon.addText(vertexTyp.getIcon().name());
      } else {
        logger.error("VertexTyp isn't a ELLIPSE or ICON");
      }
    } else {
      logger.info("No Typ. Standard will be set");
      Element label = defaultVertex.addElement(LABEL);
      String vertexLabel = pVertex.getGVSVertexLabel();
      if (vertexLabel == null) {
        vertexLabel = "";
      }
      // TRACE
      label.addText(vertexLabel);
      Element lineColor = defaultVertex.addElement(LINECOLOR);
      lineColor.addText(STANDARD);
      Element lineStyle = defaultVertex.addElement(LINESTYLE);
      lineStyle.addText(STANDARD);
      Element lineThick = defaultVertex.addElement(LINETHICKNESS);
      lineThick.addText(STANDARD);
      Element fillColor = defaultVertex.addElement(FILLCOLOR);
      fillColor.addText(STANDARD);
    }
    logger.debug("Finish create DefaultVertex-->XML");
  }

  private void buildRelativVertex(Element pParent, GVSRelativeVertex pVertex) {
    logger.debug("Create RealtivVertex-->XML");
    Element relativeVertex = pParent.addElement(RELATIVVERTEX);
    relativeVertex.addAttribute(ATTRIBUTEID,
        String.valueOf(pVertex.hashCode()));
    GVSVertexTyp vertexTypNull = pVertex.getGVSVertexTyp();
    if (vertexTypNull != null) {
      if (pVertex.getGVSVertexTyp().getClass() == GVSEllipseVertexTyp.class) {
        logger.debug("Create DefaultVertex with ELLIPSE");
        GVSEllipseVertexTyp vertexTyp = ((GVSEllipseVertexTyp) (pVertex
            .getGVSVertexTyp()));
        Element label = relativeVertex.addElement(LABEL);
        String vertexLabel = pVertex.getGVSVertexLabel();
        if (vertexLabel == null) {
          vertexLabel = "";
        }
        label.addText(vertexLabel);
        Element lineColor = relativeVertex.addElement(LINECOLOR);
        lineColor.addText(vertexTyp.getLineColor().name());
        Element lineStyle = relativeVertex.addElement(LINESTYLE);
        lineStyle.addText(vertexTyp.getLineStyle().name());
        Element lineThick = relativeVertex.addElement(LINETHICKNESS);
        lineThick.addText(vertexTyp.getLineThickness().name());
        Element fillColor = relativeVertex.addElement(FILLCOLOR);
        fillColor.addText(vertexTyp.getFillColor().name());
        Element xPos = relativeVertex.addElement(XPOS);
        xPos.addText(String.valueOf(pVertex.getX()));
        Element yPos = relativeVertex.addElement(YPOS);
        yPos.addText(String.valueOf(pVertex.getY()));
      } else if (pVertex.getGVSVertexTyp()
          .getClass() == GVSIconVertexTyp.class) {
        logger.debug("Create DefaultVertex with ICON");
        GVSIconVertexTyp vertexTyp = ((GVSIconVertexTyp) (pVertex
            .getGVSVertexTyp()));
        Element label = relativeVertex.addElement("Label");
        String vertexLabel = pVertex.getGVSVertexLabel();
        if (vertexLabel == null) {
          vertexLabel = "";
        }
        label.addText(vertexLabel);
        Element lineColor = relativeVertex.addElement(LINECOLOR);
        lineColor.addText(vertexTyp.getLineColor().name());
        Element lineStyle = relativeVertex.addElement(LINESTYLE);
        lineStyle.addText(vertexTyp.getLineStyle().name());
        Element lineThick = relativeVertex.addElement(LINETHICKNESS);
        lineThick.addText(vertexTyp.getLineThickness().name());
        Element icon = relativeVertex.addElement(ICON);
        icon.addText(vertexTyp.getIcon().name());
        Element xPos = relativeVertex.addElement(XPOS);
        xPos.addText(String.valueOf(pVertex.getX()));
        Element yPos = relativeVertex.addElement(YPOS);
        yPos.addText(String.valueOf(pVertex.getY()));
      } else {
        logger.error("VertexTyp isnt a ELLIPSE or ICON");
      }
    } else {
      Element label = relativeVertex.addElement(LABEL);
      String vertexLabel = pVertex.getGVSVertexLabel();
      if (vertexLabel == null) {
        vertexLabel = "";
      }
      logger.info("No Typ. Standard will be set");
      label.addText(vertexLabel);
      Element lineColor = relativeVertex.addElement(LINECOLOR);
      lineColor.addText(STANDARD);
      Element lineStyle = relativeVertex.addElement(LINESTYLE);
      lineStyle.addText(STANDARD);
      Element lineThick = relativeVertex.addElement(LINETHICKNESS);
      lineThick.addText(STANDARD);
      Element fillColor = relativeVertex.addElement(FILLCOLOR);
      fillColor.addText(STANDARD);
      Element xPos = relativeVertex.addElement(XPOS);
      xPos.addText(String.valueOf(pVertex.getX()));
      Element yPos = relativeVertex.addElement(YPOS);
      yPos.addText(String.valueOf(pVertex.getY()));
    }
    logger.debug("Finish create RealtivVertex-->XML");
  }

  private void buildDirectedEdge(Element pParent, GVSDirectedEdge pEdge) {
    logger.debug("Create DirectedEdge-->XML");
    GVSDefaultVertex vertex1 = pEdge.getGVSStartVertex();
    GVSDefaultVertex vertex2 = pEdge.getGVSEndVertex();
    boolean vertex1Exist = false;
    boolean vertex2Exist = false;

    Iterator vertexIt = gvsGraphVertizes.iterator();
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
      GVSEdgeTyp edgeTyp = pEdge.getGVSEdgeTyp();
      Element directedEdge = pParent.addElement(EDGE);
      directedEdge.addAttribute(ATTRIBUTEID, String.valueOf(pEdge.hashCode()));
      directedEdge.addAttribute(ISDIRECTED, "true");
      if (edgeTyp != null) {
        Element label = directedEdge.addElement(LABEL);
        String edgeLabel = pEdge.getGVSEdgeLabel();
        if (edgeLabel == null) {
          edgeLabel = "";
        }
        label.addText(edgeLabel);
        Element lineColor = directedEdge.addElement(LINECOLOR);
        lineColor.addText(edgeTyp.getLineColor().name());
        Element lineStyle = directedEdge.addElement(LINESTYLE);
        lineStyle.addText(edgeTyp.getLineStyle().name());
        Element lineThick = directedEdge.addElement(LINETHICKNESS);
        lineThick.addText(edgeTyp.getLineThickness().name());
        Element fromVertex = directedEdge.addElement(FROMVERTEX);
        fromVertex
            .addText(String.valueOf(pEdge.getGVSStartVertex().hashCode()));
        Element toVertex = directedEdge.addElement(TOVERTEX);
        toVertex.addText(String.valueOf(pEdge.getGVSEndVertex().hashCode()));
      } else {
        Element label = directedEdge.addElement(LABEL);
        String edgeLabel = pEdge.getGVSEdgeLabel();
        if (edgeLabel == null) {
          edgeLabel = "";
        }
        logger.debug("No Typ. Standard will be set");
        label.addText(edgeLabel);
        Element lineColor = directedEdge.addElement(LINECOLOR);
        lineColor.addText(STANDARD);
        Element lineStyle = directedEdge.addElement(LINESTYLE);
        lineStyle.addText(STANDARD);
        Element lineThick = directedEdge.addElement(LINETHICKNESS);
        lineThick.addText(STANDARD);
        Element fromVertex = directedEdge.addElement(FROMVERTEX);
        fromVertex
            .addText(String.valueOf(pEdge.getGVSStartVertex().hashCode()));
        Element toVertex = directedEdge.addElement(TOVERTEX);
        toVertex.addText(String.valueOf(pEdge.getGVSEndVertex().hashCode()));
      }
      logger.debug("Finish create DirectedEdge-->XML");
    } else {
      logger.warn("Start- or endvertex isn't in the Collection or null.");
    }
  }

  private void buildUndirectedEdge(Element pParent, GVSUndirectedEdge pEdge) {
    logger.debug("Create UnirectedEdge-->XML");
    GVSDefaultVertex vertex1 = pEdge.getGVSVertizes()[0];
    GVSDefaultVertex vertex2 = pEdge.getGVSVertizes()[1];
    boolean vertex1Exist = false;
    boolean vertex2Exist = false;

    Iterator vertexIt = gvsGraphVertizes.iterator();
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
      GVSEdgeTyp edgeTyp = pEdge.getGVSEdgeTyp();
      Element undirectedEdge = pParent.addElement(EDGE);
      undirectedEdge.addAttribute(ATTRIBUTEID,
          String.valueOf(pEdge.hashCode()));
      int arrowPos = pEdge.hasArrow();
      undirectedEdge.addAttribute(ISDIRECTED, "false");
      undirectedEdge.addAttribute(ARROWPOS, String.valueOf(arrowPos));

      if (edgeTyp != null) {
        Element label = undirectedEdge.addElement(LABEL);
        String edgeLabel = pEdge.getGVSEdgeLabel();
        if (edgeLabel == null) {
          edgeLabel = "";
        }
        label.addText(edgeLabel);
        Element lineColor = undirectedEdge.addElement(LINECOLOR);
        lineColor.addText(edgeTyp.getLineColor().name());
        Element lineStyle = undirectedEdge.addElement(LINESTYLE);
        lineStyle.addText(edgeTyp.getLineStyle().name());
        Element lineThick = undirectedEdge.addElement(LINETHICKNESS);
        lineThick.addText(edgeTyp.getLineThickness().name());

        Element fromVertex = undirectedEdge.addElement(FROMVERTEX);
        fromVertex
            .addText(String.valueOf(pEdge.getGVSVertizes()[0].hashCode()));
        Element toVertex = undirectedEdge.addElement(TOVERTEX);
        toVertex.addText(String.valueOf(pEdge.getGVSVertizes()[1].hashCode()));
      } else {
        Element label = undirectedEdge.addElement(LABEL);
        String edgeLabel = pEdge.getGVSEdgeLabel();
        if (edgeLabel == null) {
          edgeLabel = "";
        }
        logger.debug("No Typ. Standard will be set");
        label.addText(edgeLabel);
        Element lineColor = undirectedEdge.addElement(LINECOLOR);
        lineColor.addText(STANDARD);
        Element lineStyle = undirectedEdge.addElement(LINESTYLE);
        lineStyle.addText(STANDARD);
        Element lineThick = undirectedEdge.addElement(LINETHICKNESS);
        lineThick.addText(STANDARD);
        Element fromVertex = undirectedEdge.addElement(FROMVERTEX);
        fromVertex
            .addText(String.valueOf(pEdge.getGVSVertizes()[0].hashCode()));
        Element toVertex = undirectedEdge.addElement(TOVERTEX);
        toVertex.addText(String.valueOf(pEdge.getGVSVertizes()[1].hashCode()));
      }
      logger.debug("Finish create UnirectedEdge-->XML");
    } else {
      logger.warn("Start- or endvertex isn't in the Collection or null.");
    }
  }
}
