
package com.idiomaticsoft.lsp.scala.metals.operations.treeview.mvc
import org.eclipse.jface.viewers.ILazyTreeContentProvider
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.TreeViewNode
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.TreeViewChildrenParams
import com.idiomaticsoft.lsp.scala.metals.MetaslServerInterface
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.TreeViewVisibilityDidChangeParams

class MetalsTreeContentProvider(
	private val controller: MetalsTreeViewController,
	private val languageServer: MetaslServerInterface,
	private val view: MetalsTreeView,
	private val viewId: String
	) extends ILazyTreeContentProvider {

	override def getParent(x: Any): Object = {
		(for {
			ls <- Option(languageServer)
		} yield {
			(for {
				nodeUri <- Option(x.asInstanceOf[TreeViewNode].getNodeUri())
			} yield {
				val treeViewParentParams = new TreeViewChildrenParams(x.asInstanceOf[TreeViewNode].getViewId(), nodeUri)
				val treeViewNode = ls.treeViewParent(treeViewParentParams).get()
				(for (treeNodeUri <- Option(treeViewNode.getUri())) 
				yield controller.uriTochildNodes(treeNodeUri)).getOrElse(controller.childToParent(nodeUri))
			}).getOrElse{
				null
			}
		}).getOrElse(null)
	}
	
	override def updateChildCount(obj: Any, currentChildCount: Int): Unit = {
		if (obj.isInstanceOf[TreeViewNode]) {
			val treeViewNode = obj.asInstanceOf[TreeViewNode]
			val count = controller.getChildren(treeViewNode).size
			if (count != currentChildCount) {
				view.viewer(viewId).map(_.setChildCount(obj, count))
			}
		}
	}
	override def updateElement(parent: Any, index: Int): Unit = {
		if (parent.isInstanceOf[TreeViewNode]) {
			val treeViewNode = parent.asInstanceOf[TreeViewNode]
			val children = controller.getChildren(treeViewNode)
			val someElement = if (index < children.length) {
				Some(children(index))	
			} else {
				None
			}
			someElement.map {x => 
				view.viewer(viewId).map(_.replace(parent, index, x))
				updateChildCount(x, -1)
			}	
		} else {
			val treeViewNodes = parent.asInstanceOf[Array[TreeViewNode]]
			view.viewer(viewId).map(_.replace(parent, index, treeViewNodes(index)))
			updateChildCount(treeViewNodes(index), -1)
		}
	}
}