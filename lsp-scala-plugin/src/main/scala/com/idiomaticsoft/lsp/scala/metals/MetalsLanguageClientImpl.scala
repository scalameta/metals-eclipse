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
import org.eclipse.lsp4j.ExecuteCommandParams
import org.eclipse.swt.browser.Browser
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.SWT
import org.eclipse.ui.console.IConsoleConstants
import org.eclipse.ui.PlatformUI
import org.eclipse.ui.IPageLayout
import org.eclipse.lsp4e.LSPEclipseUtils
import org.eclipse.lsp4j.Location
import com.idiomaticsoft.lsp.scala.metals.operations.inputbox.MetalsInputBoxParams
import org.eclipse.jface.dialogs.InputDialog
import org.eclipse.jface.window.Window
import com.idiomaticsoft.lsp.scala.metals.operations.inputbox.MetalsInputBoxResult
import org.eclipse.swt.layout.FillLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser


class MetalsLanguageClientImpl extends LanguageClientImpl with MetalsLanguageClient {

	var shell: Shell = _
	var browser:Browser = _
	
	val parser = new JsonParser

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
		val controller = ScalaLSPPlugin().getTreeViewController()
		Future.apply(controller.setParentNode(treeViewDidChangeParam.nodes))
	}
	
	override def executeClientCommand(executeCommandParams: ExecuteCommandParams) = {
		if (executeCommandParams.getCommand == "metals-doctor-run") {
			Display.getDefault().asyncExec(() => {
				shell = new Shell(Display.getCurrent())
				shell.setLayout(new FillLayout)
				browser = new Browser(shell, SWT.NONE);
				browser.setText(
				"""
					<html>
					<head>
						<title>Metals Doctor</title>
					</head>
					<body>
				""" + 
				parser.parse(executeCommandParams.getArguments().get(0).toString()).getAsString() +
				"""
					</body>
					</html>
				""")
				shell.pack()
				shell.setSize(400, 300)
				shell.open()
			})
		} else if (executeCommandParams.getCommand == "metals-doctor-reload") {
			if (Option(shell).map(_.isEnabled()).getOrElse(false)) {
				browser.setText(
					"""
						<html>
						<head>
							<title>Metals Doctor</title>
						</head>
						<body>
					""" + 
					parser.parse(executeCommandParams.getArguments().get(0).toString()).getAsString() +
					"""
						</body>
						</html>
					""")
				browser.refresh()
			}
		} else if (executeCommandParams.getCommand == "metals-logs-toggle") {
			Display.getDefault().asyncExec(() => {
				val id = IConsoleConstants.ID_CONSOLE_VIEW
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(id)
			})
		} else if (executeCommandParams.getCommand == "metals-diagnostics-focus") {
			Display.getDefault().asyncExec(() => {
				val id = IPageLayout.ID_PROBLEM_VIEW
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(id)
			})
		} else if (executeCommandParams.getCommand == "metals-goto-location") {
			val location = executeCommandParams.getArguments().get(0).asInstanceOf[Location]
			Display.getDefault().asyncExec(() => {
				LSPEclipseUtils.openInEditor(location, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage())
			})
		} else if (executeCommandParams.getCommand == "metals-echo-command") {
			val commandId = executeCommandParams.getArguments().get(0).asInstanceOf[String]
			val commandParams = new ExecuteCommandParams
			commandParams.setCommand(commandId)
			getLanguageServer().getWorkspaceService().executeCommand(commandParams)
		} else {
			println("Unknnown client command: " + executeCommandParams.getCommand)
		} 
	}
	
	override def inputBox(metalsInputBoxParams: MetalsInputBoxParams) = {
		val f = new CompletableFuture[MetalsInputBoxResult]
		Display.getDefault().asyncExec(() => {
			println("Hello!")
			val dialog = if (metalsInputBoxParams.getPassword()) {
				new InputDialog(Display.getCurrent().getActiveShell(),
					"Metals LSP",
					metalsInputBoxParams.getPrompt(),
					metalsInputBoxParams.getPlaceHolder(),
					null) {
						override def getInputTextStyle(): Int = {
							super.getInputTextStyle() | SWT.PASSWORD
						}
					}
			} else {
				new InputDialog(Display.getCurrent().getActiveShell(),
					"Metals LSP",
					metalsInputBoxParams.getPrompt(),
					metalsInputBoxParams.getPlaceHolder(),
					null)
			}
			val result = dialog.open()
			if (result == Window.OK) {
				f.complete(new MetalsInputBoxResult(dialog.getValue(), false))
			} else {
				f.complete(new MetalsInputBoxResult(dialog.getValue(), true))
			}
		})
		f
	}
	
}