
package gvs.tree;

public interface GVSBinaryTreeNode extends GVSTreeNode{
	
	/**
	 * Returns the Leftchild from the Treenode
	 * @return TreeNode
	 */
	public GVSBinaryTreeNode getGVSLeftChild();
	
	/**
	 * Returns the Rigthchild from the Treenode
	 * @return TreeNode
	 */
	public GVSBinaryTreeNode getGVSRigthChild();
}
