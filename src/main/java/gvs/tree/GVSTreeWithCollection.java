
package gvs.tree;

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
import gvs.typ.node.GVSNodeTyp;

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
 * Actually only BinaryTrees are supported, because the layout algorithm are
 * missing
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
  private int maxLabelLength = 0;
  private HashSet<GVSTreeNode> gvsTreeNodes = null;

  // Config
  private final String GVSPORTFILE = "GVSPortFile";
  private final String GVSHOST = "GVSHost";
  private final String GVSPORT = "GVSPort";

  // Generaly
  private final String ROOT = "GVS";
  private final String ATTRIBUTEID = "Id";
  private final String LABEL = "Label";
  private final String FILLCOLOR = "Fillcolor";
  private final String LINECOLOR = "Linecolor";
  private final String LINESTYLE = "Linestyle";
  private final String LINETHICKNESS = "Linethickness";
  private final String STANDARD = "standard";
  private final String MAXLABELLENGTH = "MaxLabelLength";

  // Tree
  private final String TREE = "Tree";
  private final String NODES = "Nodes";
  // private final String DEFAULTNODE="DefaultNode";
  private final String BINARYNODE = "BinaryNode";
  // private final String CHILDID="Childid";
  private final String RIGHT_CHILD = "Rigthchild";
  private final String LEFT_CHILD = "Leftchild";

  // Logger
  private static final Logger logger = LoggerFactory
      .getLogger(GVSTreeWithCollection.class);

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
    xmlConnection.connectToServer();
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
  public void add(Collection<GVSTreeNode> pGVSTreeNodes) {
    logger.debug("Start add a Collection");
    Iterator<GVSTreeNode> componentIterator = pGVSTreeNodes.iterator();
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
   * Set the MaxLabelLength
   * 
   * @param pMaxLabelLength
   */
  public void setMaxLabelLength(int pMaxLabelLength) {
    this.maxLabelLength = pMaxLabelLength;
    logger.debug("MaxLabelLength set: " + pMaxLabelLength);
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
    // output = new File("test.xml");
    document = DocumentHelper.createDocument();
    Element docRoot = document.addElement(ROOT);

    logger.debug("build Tree-Elements");
    Element tree = docRoot.addElement(TREE);
    tree.addAttribute(ATTRIBUTEID, String.valueOf(this.gvsTreeId));
    Element treeLabel = tree.addElement(LABEL);
    treeLabel.addText(this.gvsTreeName);

    Element maxLabelLength = tree.addElement(MAXLABELLENGTH);
    maxLabelLength.addText(String.valueOf(this.maxLabelLength));

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
        }
        /*
         * else if(interfaces[count]==GVSDefaultTreeNode.class){
         * GVSDefaultTreeNode node=(GVSDefaultTreeNode)tmp; if(node!=null){
         * buildDefaultNode(nodes,node); } else{
         * System.err.println("Node null"); } break; }
         */
      }
    }

    /*
     * OutputFormat format = OutputFormat.createPrettyPrint(); try { XMLWriter
     * writer = new XMLWriter( new FileOutputStream(output), format );
     * writer.write( document ); } catch (UnsupportedEncodingException e) {
     * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
     */
    logger.info("Finish building XML...");
    logger.info("Call send");
    xmlConnection.sendFile(document);
  }

  /**
   * Disconnect from the Server
   *
   */
  public void disconnect() {
    logger.info("Call disconnect");
    xmlConnection.disconnectFromServer();
  }

  protected void finalize() throws Throwable {
    this.disconnect();
    super.finalize();

  }

  // ****************************XML-BUILDER*********************************

  /*
   * private void buildDefaultNode(Element pParent, GVSDefaultTreeNode pNode){
   * logger.info("Create DefaultNode -->XML"); Element defaultNode =
   * pParent.addElement(DEFAULTNODE);
   * defaultNode.addAttribute(ATTRIBUTEID,String.valueOf(pNode.hashCode()));
   * GVSNodeTyp nodeTyp =pNode.getNodeTyp(); Element label =
   * defaultNode.addElement(LABEL); String theLabel=pNode.getNodeLabel();
   * 
   * Element lineColor = defaultNode.addElement(LINECOLOR); Element lineStyle =
   * defaultNode.addElement(LINESTYLE);
   * 
   * Element lineThick = defaultNode.addElement(LINETHICKNESS); Element
   * fillColor = defaultNode.addElement(FILLCOLOR);
   * 
   * if(theLabel==null){ theLabel=""; } label.addText(theLabel);
   * 
   * GVSNodeTyp nodeTypNull=pNode.getNodeTyp(); if(nodeTypNull!=null){
   * 
   * lineColor.addText(nodeTyp.getLineColor().name());
   * lineStyle.addText(nodeTyp.getLineStyle().name());
   * lineThick.addText(nodeTyp.getLineThickness().name());
   * fillColor.addText(nodeTyp.getFillColor().name()); } else{
   * lineColor.addText(STANDARD); lineStyle.addText(STANDARD);
   * lineThick.addText(STANDARD); fillColor.addText(STANDARD); }
   * GVSDefaultTreeNode childs[]=pNode.getChildNodes(); if(childs!=null){
   * for(int size=0;size<childs.length;size++){
   * if(gvsTreeNodes.contains(childs[size])&& childs[size]!=null){ Element child
   * = defaultNode.addElement(CHILDID);
   * child.addText(String.valueOf(childs[size].hashCode()));
   * logger.info("child found"); } else{ logger.warn("Child "+
   * childs[size].getNodeLabel()+" " +
   * "nicht in der Collection vorhanden oder null"); } } }
   * logger.info("Finish Create DefaultNode -->XML"); }
   */

  private void buildBinaryNode(Element pParent, GVSBinaryTreeNode pNode) {
    logger.info("CreateBinaryNode -->XML");
    Element binaryNode = pParent.addElement(BINARYNODE);
    binaryNode.addAttribute(ATTRIBUTEID, String.valueOf(pNode.hashCode()));

    GVSNodeTyp nodeTyp = pNode.getNodeTyp();

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

    if (nodeTyp != null) {

      lineColor.addText(nodeTyp.getLineColor().name());
      lineStyle.addText(nodeTyp.getLineStyle().name());
      lineThick.addText(nodeTyp.getLineThickness().name());
      fillColor.addText(nodeTyp.getFillColor().name());
    } else {
      logger.info("No Tyo. Standard will be set");
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
            + " existiert nicht in der Collection");
      }
    }
    if (rightNode != null) {
      if (this.gvsTreeNodes.contains(rightNode)) {
        Element rightChild = binaryNode.addElement(RIGHT_CHILD);
        rightChild.addText(String.valueOf(rightNode.hashCode()));
        logger.info("Right child found");
      } else {
        logger.warn("Right child " + rightNode.getNodeLabel()
            + " existiert nicht in der Collection");
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
      GVSBinaryTreeNode actualNode = (GVSBinaryTreeNode) checkerIt.next();
      Iterator<GVSTreeNode> checkToOrigIt = gvsTreeNodes.iterator();
      while (checkToOrigIt.hasNext()) {
        GVSBinaryTreeNode nodeToCheck = (GVSBinaryTreeNode) checkToOrigIt
            .next();
        if (nodeToCheck.getGVSLeftChild() == actualNode
            || nodeToCheck.getGVSRightChild() == actualNode) {
          counter++;
        }
      }
      if (counter >= 2) {
        hasCycle = true;
        logger.error("CICLE in the tree!!!! System Exit");
        break;
      }
    }
    return hasCycle;

  }

}
