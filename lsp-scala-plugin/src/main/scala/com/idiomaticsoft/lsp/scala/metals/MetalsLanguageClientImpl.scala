package com.idiomaticsoft.lsp.scala.metals

import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification
import org.eclipse.lsp4e.LanguageClientImpl
import org.eclipse.lsp4e.LanguageServiceAccessor
import com.idiomaticsoft.lsp.scala.ScalaLSPPlugin
import java.util.concurrent.CompletableFuture
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.ICoreRunnable
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.Status
import com.idiomaticsoft.lsp.scala.metals.operations.status.MetalsStatusParams
import com.idiomaticsoft.lsp.scala.metals.operations.slowtask.MetalsSlowTaskParams
import com.idiomaticsoft.lsp.scala.metals.operations.slowtask.MetalsSlowTaskResult
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.TreeViewDidChangeParams
import scala.concurrent.Future
import scala.concurrent.forkjoin._
import scala.concurrent.ExecutionContext.Implicits.global


class MetalsLanguageClientImpl extends LanguageClientImpl with MetalsLanguageClient {

	override def metalsStatus(status: MetalsStatusParams) = {
		ScalaLSPPlugin.setStatusBar(status)
	}

	override def metalsSlowTask(param: MetalsSlowTaskParams): CompletableFuture[MetalsSlowTaskResult] = {
		val f = new CompletableFuture[MetalsSlowTaskResult]
		val startTime = System.currentTimeMillis()
		val metalsJob = new Job(param.getMessage()) {
				override def run(monitor: IProgressMonitor) = {
					var isCanceled = false
					var isFinished = false
					while (!isCanceled && !f.isCancelled()) {
						if (monitor.isCanceled()) {
							f.complete(new MetalsSlowTaskResult(true))		
							isCanceled = true
						} else {
							val currentTime = System.currentTimeMillis()
							val elapsedTime = (currentTime - startTime)/1000
							monitor.setTaskName(param.getMessage() + " (" + elapsedTime   + "s)")
							Thread.sleep(500)
						}
					}
					if (isCanceled) {
						Status.CANCEL_STATUS
					} else {
						Status.OK_STATUS
					}
				}
		}
		metalsJob.schedule()
		f
	}
	
	override def treeViewDidChange(treeViewDidChangeParam: TreeViewDidChangeParams) = {
		println("Setting nodes")
		val controller = ScalaLSPPlugin().getTreeViewController()
		Future.apply(controller.parentNodes = treeViewDidChangeParam.nodes)
	}
	
	
}