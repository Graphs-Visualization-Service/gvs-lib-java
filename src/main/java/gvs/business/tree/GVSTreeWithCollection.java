
package gvs.business.tree;

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

import gvs.access.XMLConnection;
import gvs.business.styles.GVSStyle;

/**
 * 
 * This class takes up those nodes to a Collection and transfers it to the
 * server. It is to be made certain that the tree does not contain cycles. It
 * does not play a role, if values are doubly added or removed. Null values are
 * translated on standard or empty strings. The connection information have to
 * be set over Properties:
 * 
 * -DGVSPortFile or -DGVSHost and -DGVSPort are supported.
 * 
 * 
 * @author mkoller
 */
public class GVSTreeWithCollection {

  // datas
  private Document document = null;
  private String host = null;
  private int port = 0;
  private XMLConnection xmlConnection = null;
  private long gvsTreeId = 0;
  private String gvsTreeName = "";
  private HashSet<GVSTreeNode> gvsTreeNodes = null;

  // Config
  private final String GVSPORTFILE = "GVSPortFile";
  private final String GVSHOST = "GVSHost";
  private final String GVSPORT = "GVSPort";
  private final String NO_GVS = "NoGVS";

  // Generaly
  private final String ROOT = "GVS";
  private final String ATTRIBUTEID = "Id";
  private final String LABEL = "Label";
  private final String FILLCOLOR = "Fillcolor";
  private final String LINECOLOR = "Linecolor";
  private final String LINESTYLE = "Linestyle";
  private final String LINETHICKNESS = "Linethickness";
  private final String STANDARD = "standard";

  // Tree
  private final String TREE = "Tree";
  private final String NODES = "Nodes";
  private final String DEFAULTNODE = "DefaultNode";
  private final String BINARYNODE = "BinaryNode";
  private final String CHILD = "Child";
  private final String RIGHT_CHILD = "Rigthchild";
  private final String LEFT_CHILD = "Leftchild";

  // Logger
  private static final Logger logger = LoggerFactory
      .getLogger(GVSTreeWithCollection.class);

  // If Connection to Server shall be used:
  private boolean connectToServer = false;
  private boolean connected = false;

  /**
   * Init the tree and the connection
   * 
   * @param pGVSTreeName
   */
  public GVSTreeWithCollection(String pGVSTreeName) {
    this.gvsTreeId = System.currentTimeMillis();
    this.gvsTreeName = pGVSTreeName;
    gvsTreeNodes = new HashSet<>();

    if (gvsTreeName.equals(null)) {
      gvsTreeName = "";
    }

    String propPortfile = System.getProperty(GVSPORTFILE);
    String propHost = System.getProperty(GVSHOST);
    String propPort = System.getProperty(GVSPORT);
    connectToServer = System.getProperty(NO_GVS) != null ? false : true;

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
          logger.error("Error while loading Portfile. System exit");
          System.exit(0);
        }

      } catch (DocumentException e) {
        logger.error("Error while loading Portfile. System exit");
        System.exit(0);
      }
    }

    // Set Host and Port from VM
    else if (propHost != null && propPort != null) {
      logger.info("Load Communication from Host and Port");
      try {
        this.host = propHost;
        this.port = Integer.parseInt(propPort);
      } catch (Exception ex) {
        logger.error("Error Port or Host. System exit");
        System.exit(0);
      }

    }

    // Set Defaultvalues
    else {
      logger.info("Set default for host and Port");
      this.host = "127.0.0.1";
      this.port = 3000;
      logger.info("Host: " + host + " Port: " + port);
    }
    xmlConnection = new XMLConnection(host, port);
    if (connectToServer) {
      String result = xmlConnection.connectToServer();
      if (!result.equals("")) {
        connected = true;
      }
    } else {
      logger.warn("Connection to Server is disabled by Property \"-DNoGVS\"!");
    }

  }

  /**
   * Add a Binarynode
   * 
   * @param pGVSTreeNode
   */
  public void add(GVSBinaryTreeNode pGVSTreeNode) {
    this.gvsTreeNodes.add(pGVSTreeNode);
    logger.debug("BinaryNode added");
  }

  /**
   * Add a Defaultnode
   * 
   * @param pGVSTreeNode
   */
  public void add(GVSDefaultTreeNode pGVSTreeNode) {
    this.gvsTreeNodes.add(pGVSTreeNode);
    logger.debug("DefaultNode added");
  }

  /**
   * Add a BinaryNode-Array
   * 
   * @param pGVSTreeNodes
   */
  public void add(GVSBinaryTreeNode[] pGVSTreeNodes) {
    for (int count = 0; count < pGVSTreeNodes.length; count++) {
      this.add(pGVSTreeNodes[count]);
    }
    logger.debug("BinaryNode[] added");
  }

  /**
   * Add a DefaultNode-Array
   * 
   * @param pGVSTreeNodes
   */
  public void add(GVSDefaultTreeNode[] pGVSTreeNodes) {
    for (int count = 0; count < pGVSTreeNodes.length; count++) {
      this.add(pGVSTreeNodes[count]);
    }
    logger.debug("DefaultNode[] added");
  }

  /**
   * Add a Collection of TreeNodes
   * 
   * @param pGVSTreeNodes
   */
  public void add(Collection<? extends GVSTreeNode> pGVSTreeNodes) {
    logger.debug("Start add a Collection");
    Iterator<? extends GVSTreeNode> componentIterator = pGVSTreeNodes
        .iterator();
    while (componentIterator.hasNext()) {
      Object tmp = componentIterator.next();
      Class<?>[] interfaces = tmp.getClass().getInterfaces();
      for (int count = 0; count < interfaces.length; count++) {
        System.out.println(interfaces[count].toString());
        if (interfaces[count] == GVSBinaryTreeNode.class) {
          logger.debug("BinaryNode found");
          this.add((GVSBinaryTreeNode) tmp);
        } else if (interfaces[count] == GVSDefaultTreeNode.class) {
          logger.debug("DefaultNode found");
          this.add((GVSDefaultTreeNode) tmp);
        }
      }

    }
    logger.debug("Finish add a Collection");
  }

  /**
   * Remove a DefaultNode
   * 
   * @param pGVSTreeNode
   */
  public void remove(GVSDefaultTreeNode pGVSTreeNode) {
    this.gvsTreeNodes.remove(pGVSTreeNode);
    logger.debug("remove DefaultNode");
  }

  /**
   * Remove a BinaryNode
   * 
   * @param pGVSTreeNode
   */
  public void remove(GVSBinaryTreeNode pGVSTreeNode) {
    this.gvsTreeNodes.remove(pGVSTreeNode);
    logger.debug("remove BinaryNode");
  }

  /**
   * Remove a DefaultNode-Array
   * 
   * @param pGVSTreeNodes
   */
  public void remove(GVSDefaultTreeNode[] pGVSTreeNodes) {
    for (int count = 0; count < pGVSTreeNodes.length; count++) {
      this.remove(pGVSTreeNodes[count]);
    }
    logger.debug("remove DefaultNode[]");
  }

  /**
   * Remove a BinaryNode-Array
   * 
   * @param pGVSTreeNodes
   */
  public void remove(GVSBinaryTreeNode[] pGVSTreeNodes) {
    for (int count = 0; count < pGVSTreeNodes.length; count++) {
      this.remove(pGVSTreeNodes[count]);
    }
    logger.debug("remove BinaryNode[]");
  }

  /**
   * Remove a Collection of TreeNodes
   * 
   * @param pGVSTreeNodes
   */
  public void remove(Collection<GVSTreeNode> pGVSTreeNodes) {
    logger.debug("Start remove a Collection");
    Iterator<GVSTreeNode> componentIterator = pGVSTreeNodes.iterator();
    while (componentIterator.hasNext()) {
      Object tmp = componentIterator.next();
      Class<?>[] interfaces = tmp.getClass().getInterfaces();
      for (int count = 0; count < interfaces.length; count++) {
        if (interfaces[count] == GVSBinaryTreeNode.class) {
          logger.debug("BinaryNode found");
          this.remove((GVSBinaryTreeNode) tmp);

        } else if (interfaces[count] == GVSDefaultTreeNode.class) {
          logger.debug("DefaultNode found");
          this.remove((GVSDefaultTreeNode) tmp);
        }
      }
    }
    logger.debug("Finish remove a Collection");
  }

  /**
   * Build the Xml and send it. It examined whether the tree cycles contains. If
   * the Client terminated, since this is not permitted
   */
  public void display() {

    if (checkForCycles()) {
      System.exit(0);
    }
    logger.info("Start building XML...");
    document = DocumentHelper.createDocument();
    Element docRoot = document.addElement(ROOT);

    logger.debug("build Tree-Elements");
    Element tree = docRoot.addElement(TREE);
    tree.addAttribute(ATTRIBUTEID, String.valueOf(this.gvsTreeId));
    Element treeLabel = tree.addElement(LABEL);
    treeLabel.addText(this.gvsTreeName);

    Element nodes = docRoot.addElement(NODES);
    logger.debug("build Node-Elements");
    Iterator<GVSTreeNode> nodeIterator = gvsTreeNodes.iterator();
    while (nodeIterator.hasNext()) {
      Object tmp = nodeIterator.next();
      Class<?>[] interfaces = tmp.getClass().getInterfaces();
      for (int count = 0; count < interfaces.length; count++) {
        if (interfaces[count] == GVSBinaryTreeNode.class) {
          GVSBinaryTreeNode node = (GVSBinaryTreeNode) tmp;
          if (node != null) {
            logger.debug("BinaryNode found");
            buildBinaryNode(nodes, node);
          } else {
            logger.warn("BinaryNode null");
          }
          break;
        } else if (interfaces[count] == GVSDefaultTreeNode.class) {
          GVSDefaultTreeNode node = (GVSDefaultTreeNode) tmp;
          if (node != null) {
            buildDefaultNode(nodes, node);
          } else {
            System.err.println("Node null");
          }
          break;
        }

      }
    }

    logger.info("Finish building XML...");
    if (connectToServer) {
      logger.info("Call send");
      xmlConnection.sendFile(document);
    }
  }

  /**
   * Disconnect from the Server
   *
   */
  public void disconnect() {
    if (connectToServer) {
      logger.info("Call disconnect");
      xmlConnection.disconnectFromServer();
    }
  }

  protected void finalize() throws Throwable {
    this.disconnect();
    super.finalize();

  }

  // ****************************XML-BUILDER*********************************

  private void buildDefaultNode(Element pParent, GVSDefaultTreeNode pNode) {
    Element defaultNode = pParent.addElement(DEFAULTNODE);
    defaultNode.addAttribute(ATTRIBUTEID, String.valueOf(pNode.hashCode()));
    GVSStyle style = pNode.getStyle();

    Element label = defaultNode.addElement(LABEL);
    String theLabel = pNode.getNodeLabel();

    Element lineColor = defaultNode.addElement(LINECOLOR);
    Element lineStyle = defaultNode.addElement(LINESTYLE);

    Element lineThick = defaultNode.addElement(LINETHICKNESS);
    Element fillColor = defaultNode.addElement(FILLCOLOR);

    if (theLabel == null) {
      theLabel = "";
    }
    label.addText(theLabel);

    if (style != null) {

      lineColor.addText(style.getLineColor().name());
      lineStyle.addText(style.getLineStyle().name());
      lineThick.addText(style.getLineThickness().name());
      fillColor.addText(style.getFillColor().name());
    } else {
      lineColor.addText(STANDARD);
      lineStyle.addText(STANDARD);
      lineThick.addText(STANDARD);
      fillColor.addText(STANDARD);
    }
    GVSDefaultTreeNode children[] = pNode.getGVSChildNodes();
    if (children != null) {
      for (int index = 0; index < children.length; index++) {
        GVSDefaultTreeNode childNode = children[index];
        if (childNode != null) {
          Element child = defaultNode.addElement(CHILD);
          child.addText(String.valueOf(childNode.hashCode()));
        }
      }
    }
  }

  private void buildBinaryNode(Element pParent, GVSBinaryTreeNode pNode) {
    logger.info("CreateBinaryNode -->XML");
    Element binaryNode = pParent.addElement(BINARYNODE);
    binaryNode.addAttribute(ATTRIBUTEID, String.valueOf(pNode.hashCode()));

    GVSStyle nodeStyle = pNode.getStyle();

    Element label = binaryNode.addElement(LABEL);
    String theLabel = pNode.getNodeLabel();

    Element lineColor = binaryNode.addElement(LINECOLOR);
    Element lineStyle = binaryNode.addElement(LINESTYLE);

    Element lineThick = binaryNode.addElement(LINETHICKNESS);
    Element fillColor = binaryNode.addElement(FILLCOLOR);

    if (theLabel == null) {
      theLabel = "";
    }
    label.addText(theLabel);

    if (nodeStyle != null) {

      lineColor.addText(nodeStyle.getLineColor().name());
      lineStyle.addText(nodeStyle.getLineStyle().name());
      lineThick.addText(nodeStyle.getLineThickness().name());
      fillColor.addText(nodeStyle.getFillColor().name());
    } else {
      logger.info("No Style. Standard will be set");
      lineColor.addText(STANDARD);
      lineStyle.addText(STANDARD);
      lineThick.addText(STANDARD);
      fillColor.addText(STANDARD);
    }

    GVSBinaryTreeNode leftNode = pNode.getGVSLeftChild();
    GVSBinaryTreeNode rightNode = pNode.getGVSRightChild();
    if (leftNode != null) {
      if (this.gvsTreeNodes.contains(leftNode)) {
        Element leftChild = binaryNode.addElement(LEFT_CHILD);
        leftChild.addText(String.valueOf(leftNode.hashCode()));
        logger.info("Leftchild found");
      } else {
        logger.warn("Leftchild " + leftNode.getNodeLabel()
            + " not contained in collection");
      }
    }
    if (rightNode != null) {
      if (this.gvsTreeNodes.contains(rightNode)) {
        Element rightChild = binaryNode.addElement(RIGHT_CHILD);
        rightChild.addText(String.valueOf(rightNode.hashCode()));
        logger.info("Right child found");
      } else {
        logger.warn("Right child " + rightNode.getNodeLabel()
            + " not contained in collection");
      }
    }
    logger.info("Finish Create BinaryNode -->XML");
  }

  private boolean checkForCycles() {
    logger.info("Check for Cycles");
    boolean hasCycle = false;
    Vector<GVSTreeNode> toCheck = new Vector<GVSTreeNode>(gvsTreeNodes);
    Iterator<GVSTreeNode> checkerIt = toCheck.iterator();
    while (checkerIt.hasNext()) {
      int counter = 0;
      GVSTreeNode actualNode = checkerIt.next();
      Iterator<GVSTreeNode> checkToOrigIt = gvsTreeNodes.iterator();
      while (checkToOrigIt.hasNext()) {
        GVSTreeNode nodeToCheck = checkToOrigIt.next();
        GVSTreeNode[] children = children(nodeToCheck);
        if (children != null) {
          for (int i = 0; i < children.length; i++) {
            if (children[i] == actualNode) {
              counter++;
            }
          }
        }
      }
      if (counter >= 2) {
        hasCycle = true;
        logger.error("CYCLE in the tree!!!! System Exit");
        break;
      }
    }
    return hasCycle;
  }

  private GVSTreeNode[] children(GVSTreeNode nodeToCheck) {
    if (nodeToCheck instanceof GVSBinaryTreeNode) {
      GVSBinaryTreeNode node = (GVSBinaryTreeNode) nodeToCheck;
      return new GVSTreeNode[] { node.getGVSLeftChild(),
          node.getGVSRightChild() };
    } else {
      GVSDefaultTreeNode node = (GVSDefaultTreeNode) nodeToCheck;
      return node.getGVSChildNodes();
    }
  }

  public boolean isConnected() {
    return connected;
  }
}
