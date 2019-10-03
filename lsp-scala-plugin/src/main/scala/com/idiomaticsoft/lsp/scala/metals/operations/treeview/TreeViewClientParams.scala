package com.idiomaticsoft.lsp.scala.metals.operations.treeview

import scala.beans.BeanProperty

class TreeViewCommand(
    @BeanProperty var title: String = null,
    @BeanProperty var command: String = null,
    @BeanProperty var toolTip: String = null,
    @BeanProperty var arguments: Array[Any] = null
) {

  override def equals(obj: Any): Boolean = {
    if (!obj.isInstanceOf[TreeViewCommand]) {
      false
    } else {
      val that = obj.asInstanceOf[TreeViewCommand]
      return (title == that.title) &&
        (command == that.command) &&
        (toolTip == that.toolTip) &&
        Option(arguments)
          .map(
            _.zipWithIndex
              .map({ case (arg, idx) => arg == that.arguments(idx) })
              .reduce(_ && _)
          )
          .getOrElse(false)
    }
  }

  override def hashCode(): Int = {
    var hash = 7
    hash = 31 * hash + (if (Option(title).isEmpty) 0 else title.hashCode)
    hash = 31 * hash + (if (Option(command).isEmpty) 0 else command.hashCode)
    hash = 31 * hash + (if (Option(toolTip).isEmpty) 0 else toolTip.hashCode)
    hash = 31 * hash + (if (Option(arguments).isEmpty) 0
                        else arguments.hashCode)
    hash
  }

}

class TreeViewNode(
    @BeanProperty var viewId: String = null,
    @BeanProperty var nodeUri: String = null,
    @BeanProperty var label: String = null,
    @BeanProperty var command: TreeViewCommand = null,
    @BeanProperty var icon: String = null,
    @BeanProperty var toolTip: String = null,
    @BeanProperty var collapseState: String = null
) {

  override def equals(obj: Any): Boolean = {
    if (!obj.isInstanceOf[TreeViewNode]) {
      false
    } else {
      val that = obj.asInstanceOf[TreeViewNode]
      return nodeUri == that.nodeUri
    }
  }

  override def hashCode(): Int = {
    var hash = 7
    hash = 31 * hash + (if (Option(viewId).isEmpty) 0 else viewId.hashCode)
    hash = 31 * hash + (if (Option(nodeUri).isEmpty) 0 else nodeUri.hashCode)
    hash
  }

  override def toString() = {
    if (Option(label).isEmpty) {
      viewId
    } else {
      label
    }
  }
}

class TreeViewDidChangeParams(
    @BeanProperty var nodes: Array[TreeViewNode] = Array[TreeViewNode]()
)
