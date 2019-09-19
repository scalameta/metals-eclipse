package com.idiomaticsoft.lsp.scala.metals.operations.treeview

import scala.beans.BeanProperty

class TreeViewCommand (
	@BeanProperty var title: String = null,
	@BeanProperty var command: String = null,
	@BeanProperty var toolTip: String = null,
	@BeanProperty var arguments: Array[Object] = null)

class TreeViewNode(
	@BeanProperty var viewId: String = null,
	@BeanProperty var nodeUri: String = null,
	@BeanProperty var label: String = null,
	@BeanProperty var command: TreeViewCommand = null,
	@BeanProperty var icon: String = null,
	@BeanProperty var toolTip: String = null,
	@BeanProperty var collapseState: String = null) {

	
	override def toString() = {
		if (Option(label).isEmpty) {
			viewId
		} else {
			label
		}
	}
}

class TreeViewDidChangeParams(
	@BeanProperty var nodes: Array[TreeViewNode] = Array[TreeViewNode]())