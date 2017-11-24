
package gvs.tree;

public interface GVSBinaryTreeNode extends GVSTreeNode {

  /**
   * Returns the left child from the Treenode
   * 
   * @return TreeNode
   */
  public GVSBinaryTreeNode getGVSLeftChild();
  

  /**
   * Returns the right child from the Treenode
   * 
   * @return TreeNode
   */
  public GVSBinaryTreeNode getGVSRightChild();
}
