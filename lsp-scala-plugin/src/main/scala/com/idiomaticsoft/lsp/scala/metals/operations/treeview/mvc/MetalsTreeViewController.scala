package com.idiomaticsoft.lsp.scala.metals.operations.treeview.mvc
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.TreeViewNode
import org.eclipse.swt.widgets.Display
import com.idiomaticsoft.lsp.scala.metals.MetaslServerInterface
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.TreeViewChildrenParams
import com.idiomaticsoft.lsp.scala.ScalaLSPPlugin
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.TreeViewNodeCollapseDidChangeParams
import org.eclipse.jface.viewers.ILazyTreeContentProvider

trait MetalsTreeViewController extends ILazyTreeContentProvider {
	
	def parentNodes: List[TreeViewNode]
	
	def parentNodes_=(nodes: Array[TreeViewNode]): Unit
	
	def view: MetalsTreeView
	
	def view_=(view: MetalsTreeView): Unit
	
	def elementCollapsedOrExpanded(element: Any, collapsed: Boolean): Unit 
	
}

class MetalsTreeViewControllerImpl extends MetalsTreeViewController {
	
	private var parentNodesMap = new scala.collection.mutable.HashMap[String, TreeViewNode]
	
	private var uriTochildNodes = new scala.collection.mutable.HashMap[String, TreeViewNode]
	  
	private var metalsTreeView: MetalsTreeView = _  
	
	override def parentNodes: List[TreeViewNode] = parentNodesMap.keys.toList.map(parentNodesMap(_))
	
	override def parentNodes_=(nodes: Array[TreeViewNode]): Unit = {
		// we reset everything by default
		parentNodesMap = new scala.collection.mutable.HashMap[String, TreeViewNode]
		nodes.foreach(node => parentNodesMap.put(node.getViewId(), node))
		Display.getDefault().asyncExec(() =>
			for {
				theView <- Option(view)
				theViewer <- Option(theView.viewer)
			} {
				theViewer.setInput(parentNodes.toArray)
				theViewer.getTree().setItemCount(parentNodes.size)
				ScalaLSPPlugin().getLanguageServer()
			}
		)
	}	
	
	private def languageServer() = {
		val res = ScalaLSPPlugin().getLanguageServer()
		if (Option(res).isEmpty) {
			println("Ooops! No language server")
		}
		res
	}
	
	override def getParent(x: Any): Object = {
		(for {
			ls <- Option(languageServer)
		} yield {
			val uri = ls.treeViewParent(new TreeViewChildrenParams(x.asInstanceOf[TreeViewNode].getViewId(), x.asInstanceOf[TreeViewNode].getNodeUri())).get().getUri()
			uriTochildNodes(uri)
		}).getOrElse(null)
	}
		
	override def view_=(view: MetalsTreeView): Unit = {
		this.metalsTreeView = view
	}
	
	override def view = metalsTreeView
	
	override def elementCollapsedOrExpanded(element: Any, collapsed: Boolean): Unit = {
		ScalaLSPPlugin().getLanguageServer().treeViewNodeCollapseDidChange(new TreeViewNodeCollapseDidChangeParams(
			element.asInstanceOf[TreeViewNode].getViewId(),
			element.asInstanceOf[TreeViewNode].getNodeUri(),
			collapsed
		))
	}

	override def updateChildCount(obj: Any, currentChildCount: Int): Unit = {
		if (obj.isInstanceOf[TreeViewNode]) {
			val treeViewNode = obj.asInstanceOf[TreeViewNode]
			val count = Option(languageServer).map(_.treeViewChildren(new TreeViewChildrenParams(
						treeViewNode.getViewId(), 
						treeViewNode.getNodeUri()))
						.get().getNodes().size).getOrElse(0)
			if (count != currentChildCount) {
				view.viewer.setChildCount(obj, count)
			}
		} else {
			val treeViewNodes = obj.asInstanceOf[Array[TreeViewNode]]
			val count = parentNodes.size
			println("Count: " + count)
			if (count != currentChildCount) {
				view.viewer.setChildCount(obj, count)
			}
		}
	}
	
	def updateElement(parent: Any, index: Int): Unit = {
		if (parent.isInstanceOf[TreeViewNode]) {
			val treeViewNode = parent.asInstanceOf[TreeViewNode]
			val children = Option(languageServer).map(_.treeViewChildren(new TreeViewChildrenParams(
				treeViewNode.getViewId(), 
				treeViewNode.getNodeUri()))
				.get().getNodes()).getOrElse(Array())
			val someElement = if (index < children.length) {
				Some(children(index))	
			} else {
				None
			}
			someElement.map {x => 
				view.viewer.replace(parent, index, x)
				updateChildCount(x, -1)
			}	
		} else {
			val treeViewNodes = parent.asInstanceOf[Array[TreeViewNode]]
			view.viewer.replace(parent, index, treeViewNodes(index))
			updateChildCount(treeViewNodes(index), -1)
		}
	}	
}