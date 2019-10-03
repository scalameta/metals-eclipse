package com.idiomaticsoft.lsp.scala.metals

import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import java.util.concurrent.CompletableFuture
import com.idiomaticsoft.lsp.scala.metals.operations.status.MetalsStatusParams
import com.idiomaticsoft.lsp.scala.metals.operations.slowtask.MetalsSlowTaskParams
import com.idiomaticsoft.lsp.scala.metals.operations.slowtask.MetalsSlowTaskResult
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.TreeViewDidChangeParams
import org.eclipse.lsp4j.ExecuteCommandParams
import com.idiomaticsoft.lsp.scala.metals.operations.inputbox.MetalsInputBoxParams
import com.idiomaticsoft.lsp.scala.metals.operations.inputbox.MetalsInputBoxResult

trait MetalsLanguageClient extends LanguageClient {

  @JsonNotification("metals/status")
  def metalsStatus(status: MetalsStatusParams): Unit

  @JsonRequest("metals/slowTask")
  def metalsSlowTask(
      status: MetalsSlowTaskParams
  ): CompletableFuture[MetalsSlowTaskResult]

  @JsonNotification("metals/treeViewDidChange")
  def treeViewDidChange(treeViewDidChangeParam: TreeViewDidChangeParams)
  @JsonNotification("metals/executeClientCommand")
  def executeClientCommand(executeCommandParams: ExecuteCommandParams)

  @JsonNotification("metals/inputBox")
  def inputBox(
      metalsInputBoxParams: MetalsInputBoxParams
  ): CompletableFuture[MetalsInputBoxResult]

}
