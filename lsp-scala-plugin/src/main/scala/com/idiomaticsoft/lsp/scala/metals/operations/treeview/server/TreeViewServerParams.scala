package com.idiomaticsoft.lsp.scala.metals.operations.treeview.server
import scala.beans.BeanProperty
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.TreeViewNode

class TreeViewChildrenParams (
	@BeanProperty var viewId: String = null,
	@BeanProperty var nodeUri: String = null
		)
		
class TreeViewChildrenResult(
	@BeanProperty var nodes: Array[TreeViewNode] = null)
	
class TreeViewParentResult(
	@BeanProperty var uri: String = null)
	
class TreeViewNodeCollapseDidChangeParams(	
	@BeanProperty var viewId: String = null,
	@BeanProperty var nodeUri: String = null,
	@BeanProperty var collapsed: Boolean = false)