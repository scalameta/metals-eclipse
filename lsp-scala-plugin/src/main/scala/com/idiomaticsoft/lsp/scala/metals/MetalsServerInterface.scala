package com.idiomaticsoft.lsp.scala.metals

import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import java.util.concurrent.CompletableFuture
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.TreeViewChildrenParams
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.TreeViewNodeCollapseDidChangeParams
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.TreeViewChildrenResult
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.TreeViewParentResult
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.TreeViewDidChangeParams
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification
import org.eclipse.lsp4j.TextDocumentPositionParams
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.MetalsTreeRevealResult
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.TreeViewVisibilityDidChangeParams
import org.eclipse.lsp4j.ExecuteCommandParams

trait MetaslServerInterface extends LanguageServer {

	@JsonRequest(value="metals/treeViewChildren")
	def treeViewChildren(treeViewChildrenParams: TreeViewChildrenParams):CompletableFuture[TreeViewChildrenResult]

	@JsonRequest(value="metals/treeViewParent")
	def treeViewParent(treeViewChildrenParams: TreeViewChildrenParams):CompletableFuture[TreeViewParentResult]

	@JsonNotification(value="metals/treeViewNodeCollapseDidChange")
	def treeViewNodeCollapseDidChange(treeViewVisibilityDidChangeParams: TreeViewNodeCollapseDidChangeParams)
	
	@JsonNotification(value="metals/treeViewVisibilityDidChange")
	def treeViewVisibilityDidChange(treeViewVisibilityDidChangeParams: TreeViewVisibilityDidChangeParams)
	
	@JsonRequest(value="metals/treeViewReveal")
	def treeViewReveal(textDocumentPositionParams: TextDocumentPositionParams): CompletableFuture[MetalsTreeRevealResult]

}