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
import org.eclipse.lsp4j.TextDocumentPositionParams
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.MetalsTreeRevealResult
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.eclipse.jface.viewers.ISelection
import org.eclipse.jface.viewers.StructuredSelection
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.TreeViewVisibilityDidChangeParams
import org.eclipse.jface.viewers.TreeNode
import com.idiomaticsoft.lsp.scala.metals.MetalsLanguageClient

trait MetalsTreeViewController  {
	
	def parentNodes(viewId: String): List[TreeViewNode]
	
	def setParentNode(nodes: Array[TreeViewNode]): Unit
	
	def view: MetalsTreeView
	
	def view_=(view: MetalsTreeView): Unit
	
	def contentProvider(viewId: String): MetalsTreeContentProvider
	
	def elementCollapsed(element: Any, collapsed: Boolean): Unit
	
	def reveal(textDocumentPositionParams: TextDocumentPositionParams): Unit

	def parentNodesMap: scala.collection.mutable.HashMap[String, Array[TreeViewNode]]
	
	def uriTochildNodes: scala.collection.mutable.HashMap[String, TreeViewNode]
	
	def childToParent: scala.collection.mutable.HashMap[String, TreeViewNode]
	
	def getChildren(treeViewNode: TreeViewNode): Array[TreeViewNode]
	
	def languageServer(): MetaslServerInterface
}

class MetalsTreeViewControllerImpl extends MetalsTreeViewController {
	
	val parentNodesMap = new scala.collection.mutable.HashMap[String, Array[TreeViewNode]]
	val uriTochildNodes = new scala.collection.mutable.HashMap[String, TreeViewNode]

	val childToParent = new scala.collection.mutable.HashMap[String, TreeViewNode]

	
	  
	private var metalsTreeView: MetalsTreeView = _  
	
	override def parentNodes(viewId: String): List[TreeViewNode] = parentNodesMap(viewId).toList// parentNodesMap.keys.toList.sorted.map(parentNodesMap(_))

	private def createUriChain(node: TreeViewNode, list: List[String]): List[String] = {
		Option(node.getNodeUri()) match {
			case Some(nodeUri) => 
		 		childToParent.get(nodeUri) match {
					case Some(parentNode) =>
						 createUriChain(parentNode, nodeUri::list)
					case None =>
						nodeUri::list
				}
			case None =>
				list
		}
	}

	override def setParentNode(nodes: Array[TreeViewNode]): Unit = {
		Display.getDefault().asyncExec(() =>
			for {
				theView <- Option(view)
			} {
				for {
					node <- nodes
				} yield {
					(parentNodesMap.get(node.getViewId()), Option(node.getNodeUri()))  match {
						case (None, None) => 
							// this only happens with the top level values
							view.createExpandBar(node.getViewId(), node.toString(), node)
							val children = getChildren(node)
							parentNodesMap.put(node.getViewId(), children)
							view.fillExpandBar(node.getViewId(), children)
						case (Some(treeViewNodes), None) => 
							// we have an expand tab that already exists, nothing to do, this should not happen
						case(Some(treeViewNodes), Some(nodeUri)) =>
							// we always take the viewId of the parent
							// because sometimes they are commands
							childToParent.get(nodeUri).map(_.getViewId()).map { viewId =>
								view.viewer(viewId).map(_.refresh(node))
							}
						case (None, Some(nodeUri)) =>
							// should not happen
					}
				}
			}
		)
	}	
	
	def contentProvider(viewId: String): MetalsTreeContentProvider = {
		new MetalsTreeContentProvider(this, languageServer(), metalsTreeView, viewId)
	}
	
	private def addOrReplaceNodeIfNotThere(l: List[TreeViewNode], node: TreeViewNode): List[TreeViewNode] = {
		l match {
			case h::tail =>
				if (node.getNodeUri() == h.getNodeUri()) {
					node::tail
				} else {
					h::addOrReplaceNodeIfNotThere(tail, node)
				}
			case Nil =>
				node::Nil
		}
	}
	
	def languageServer() = {
		val res = ScalaLSPPlugin().getLanguageServer()
		if (Option(res).isEmpty) {
			println("Ooops! No language server")
		}
		res
	}
	

		
	override def view_=(view: MetalsTreeView): Unit = {
		this.metalsTreeView = view
	}
	
	override def view = metalsTreeView
	
	override def elementCollapsed(element: Any, collapsed: Boolean): Unit = {
		ScalaLSPPlugin().getLanguageServer().treeViewNodeCollapseDidChange(new TreeViewNodeCollapseDidChangeParams(
			element.asInstanceOf[TreeViewNode].getViewId(),
			element.asInstanceOf[TreeViewNode].getNodeUri(),
			collapsed
		))
		if (!collapsed) {
			val childrenAndIdx = getChildren(element.asInstanceOf[TreeViewNode]).zipWithIndex
			childrenAndIdx.map{e =>
				val (child, idx) = e
			}
		}
	}


	def getChildren(treeViewNode: TreeViewNode) = {
		val children = Option(languageServer).map(_.treeViewChildren(new TreeViewChildrenParams(
								treeViewNode.getViewId(), 
								treeViewNode.getNodeUri()))
								.get().getNodes()).getOrElse(Array())
		children.foreach(child => {
			if (Option(child.getNodeUri()).isDefined) {
				uriTochildNodes.put(child.getNodeUri(),child)
				childToParent.put(child.getNodeUri(),treeViewNode)
			}
		})			
		children
	}
	
	def visitPath(viewId: String, uriChain: List[String], elements: List[TreeViewNode], someParent: Option[TreeViewNode], f: TreeViewNode => Unit): Option[TreeViewNode] = {
		uriChain match {
			case h::tail => 
				val someElement = elements.find(_.getNodeUri() == h)
				someElement match {
					case Some(treeViewNode) =>
						val children = getChildren(treeViewNode).toList
						f(treeViewNode)
						visitPath(viewId, tail, children, Some(treeViewNode), f)
					case None => 
						None
				}
			case Nil => 
				someParent
		}
	}
	
	private def expandTreeViewNode(viewId: String, treeViewNode: TreeViewNode) = {
		view.viewer(viewId).map(_.setExpandedState(treeViewNode, true))
		elementCollapsed(treeViewNode, false)
	}
	
	override def reveal(textDocumentPositionParams: TextDocumentPositionParams): Unit = {
		val f = languageServer().treeViewReveal(textDocumentPositionParams)
		val result = f.get
		if (Option(result).isDefined) {	
			view.expandBar("metalsBuild").map(_.setExpanded(true))
			languageServer().treeViewVisibilityDidChange(new TreeViewVisibilityDidChangeParams("metalsBuild", true))
			Display.getCurrent().timerExec(1, () => {
				val foundNode = visitPath("metalsBuild", result.getUriChain().toList.reverse, parentNodes("metalsBuild"), None, expandTreeViewNode("metalsBuild", _))
				foundNode.map(y => view.viewer("metalsBuild").map(_.setSelection(new StructuredSelection(y), true)))
			})
		}
	}	

}