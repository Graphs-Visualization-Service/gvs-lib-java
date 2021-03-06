
package gvs.business.tree;

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
 * This class takes up only a rootnode. For transfer, the class builds the tree
 * recursivly and adds the nodes to a collection. Null values are translated to
 * standard or empty strings. It is to be made certain that the tree does not
 * contain cycles. The connection information has to be set over Properties:
 * 
 * -DGVSPortFile or -DGVSHost and -DGVSPort are supported.
 * 
 * Currently only BinaryTrees are supported, because there is no layout
 * algorithm for general trees
 * 
 * @author mkoller
 * 
 */
public class GVSTreeWithRoot {

  // Data
  private Document document = null;
  private XMLConnection xmlConnection = null;
  private String host = null;
  private int port = 0;
  private long gvsTreeId = 0;
  private String gvsTreeName = "";
  private GVSTreeNode gvsTreeRoot = null;

  // Config
  private final String GVSPORTFILE = "GVSPortFile";
  private final String GVSHOST = "GVSHost";
  private final String GVSPORT = "GVSPort";
  private final String NO_GVS = "NoGVS";

  // General
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
  private final String TREEROOTID = "TreeRootId";
  private final String CHILD = "Child";
  private final String RIGHT_CHILD = "Rigthchild";
  private final String LEFT_CHILD = "Leftchild";

  private static final Logger logger = LoggerFactory
      .getLogger(GVSTreeWithRoot.class);

  private Vector<GVSTreeNode> gvsTreeNodes = null;

  // If Connection to Server shall be used:
  private boolean connectToServer = false;
  private boolean connected = false;

  /**
   * Init tree and connection
   * 
   * @param pGVSTreeName
   */
  public GVSTreeWithRoot(String pGVSTreeName) {
    this.gvsTreeId = System.currentTimeMillis();
    this.gvsTreeName = pGVSTreeName;

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
   * Set the rootnode for the tree
   * 
   * @param pGVSRootTreeNode
   */
  public void setRoot(GVSTreeNode pGVSRootTreeNode) {
    logger.debug("Set Rootnode");
    this.gvsTreeRoot = pGVSRootTreeNode;
  }

  /**
   * Build the tree and check for cycles. If the tree is ok, it will be send to
   * the server
   *
   */
  public void display() {

    logger.info("Start building XML...");
    document = DocumentHelper.createDocument();
    Element docRoot = document.addElement(ROOT);

    logger.debug("build Tree-Elements");
    Element tree = docRoot.addElement(TREE);
    tree.addAttribute(ATTRIBUTEID, String.valueOf(this.gvsTreeId));
    Element treeLabel = tree.addElement(LABEL);
    treeLabel.addText(this.gvsTreeName);

    if (this.gvsTreeRoot != null) {
      logger.debug("build Node-Elements");
      Element treeRoot = tree.addElement(TREEROOTID);
      treeRoot.addText(String.valueOf(this.gvsTreeRoot.hashCode()));
    }
    Element nodes = docRoot.addElement(NODES);

    this.gvsTreeNodes = new Vector<GVSTreeNode>();
    if (this.gvsTreeRoot != null) {
      buildNode(nodes, this.gvsTreeRoot);
      if (checkForCycles()) {
        System.exit(0);
      }
    }
    logger.info("Finish building XML");
    if (connectToServer) {
      logger.info("Call send");
      xmlConnection.sendFile(document);
    }
  }

  /**
   * Disconnect from the server
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

  // ***********************BUILDERS**********************************
  private void buildNode(Element pParent, GVSTreeNode pNode) {
    logger.debug("build Node");
    Class<?>[] interfaces = pNode.getClass().getInterfaces();
    for (int count = 0; count < interfaces.length; count++) {
      if (interfaces[count] == GVSBinaryTreeNode.class) {
        logger.debug("BinaryNode found");
        GVSBinaryTreeNode theNode = (GVSBinaryTreeNode) pNode;
        if (theNode != null) {
          gvsTreeNodes.add(pNode);
          buildBinaryNode(pParent, theNode);
          GVSBinaryTreeNode tmpNode = theNode.getGVSLeftChild();
          if (tmpNode != null) {
            buildNode(pParent, tmpNode);
          } else {
            logger.debug("LeftChild null");
          }
          tmpNode = ((GVSBinaryTreeNode) pNode).getGVSRightChild();
          if (tmpNode != null) {
            buildNode(pParent, tmpNode);
          } else {
            logger.debug("RigthChild null");
          }
        } else {
          logger.warn("BinaryNode null");
        }
        break;
      } else if (interfaces[count] == GVSDefaultTreeNode.class) {
        GVSDefaultTreeNode theNode = (GVSDefaultTreeNode) pNode;
        if (theNode != null) {
          gvsTreeNodes.add(pNode);
          buildDefaultNode(pParent, theNode);
          GVSDefaultTreeNode children[] = theNode.getGVSChildNodes();
          if (children != null) {
            for (int index = 0; index < children.length; index++) {
              if (children[index] != null) {
                buildNode(pParent, children[index]);
              }
            }
          }
          break;
        }
      }

    }
  }

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
      logger.info("No Tyo. Standard will be set");
      lineColor.addText(STANDARD);
      lineStyle.addText(STANDARD);
      lineThick.addText(STANDARD);
      fillColor.addText(STANDARD);
    }

    GVSBinaryTreeNode leftNode = pNode.getGVSLeftChild();
    GVSBinaryTreeNode rigthNode = pNode.getGVSRightChild();
    if (leftNode != null) {
      Element leftChild = binaryNode.addElement(LEFT_CHILD);
      leftChild.addText(String.valueOf(leftNode.hashCode()));
    } else {
      logger.debug("Leftchild null");
    }
    if (rigthNode != null) {
      Element rigthChild = binaryNode.addElement(RIGHT_CHILD);
      rigthChild.addText(String.valueOf(rigthNode.hashCode()));
    } else {
      logger.debug("Rigthchild null");
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

  public boolean isConnected() {
    return connected;
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

}
