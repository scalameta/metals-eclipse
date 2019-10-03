package com.idiomaticsoft.lsp.scala.metals

class ExperimentalFeatures {
  private var treeViewProvider: Boolean = _

  def getTreeViewProvider(): Boolean = {
    this.treeViewProvider
  }

  def setTreeViewProvider(treeViewProvider: Boolean): Unit = {
    this.treeViewProvider = treeViewProvider
  }
}
