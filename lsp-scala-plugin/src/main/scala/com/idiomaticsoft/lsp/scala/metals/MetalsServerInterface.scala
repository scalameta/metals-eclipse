/*******************************************************************************
 * Copyright (c) 2019 Idiomaticsoft S.R.L. and others.
 * This program and the accompanying materials are made
 * available under the terms of the APACHE LICENSE, VERSION 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *  Edmundo Lopez B. (Idiomaticsoft S.R.L.) - initial implementation
 *******************************************************************************/

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

  @JsonRequest(value = "metals/treeViewChildren")
  def treeViewChildren(
      treeViewChildrenParams: TreeViewChildrenParams
  ): CompletableFuture[TreeViewChildrenResult]

  @JsonRequest(value = "metals/treeViewParent")
  def treeViewParent(
      treeViewChildrenParams: TreeViewChildrenParams
  ): CompletableFuture[TreeViewParentResult]

  @JsonNotification(value = "metals/treeViewNodeCollapseDidChange")
  def treeViewNodeCollapseDidChange(
      treeViewVisibilityDidChangeParams: TreeViewNodeCollapseDidChangeParams
  )

  @JsonNotification(value = "metals/treeViewVisibilityDidChange")
  def treeViewVisibilityDidChange(
      treeViewVisibilityDidChangeParams: TreeViewVisibilityDidChangeParams
  )

  @JsonRequest(value = "metals/treeViewReveal")
  def treeViewReveal(
      textDocumentPositionParams: TextDocumentPositionParams
  ): CompletableFuture[MetalsTreeRevealResult]

}
