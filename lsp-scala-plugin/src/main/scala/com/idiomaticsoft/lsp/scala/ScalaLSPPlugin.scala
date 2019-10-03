package com.idiomaticsoft.lsp.scala

import org.osgi.framework.BundleContext
import org.eclipse.ui.plugin.AbstractUIPlugin
import java.io.File
import org.eclipse.debug.core.DebugPlugin
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants
import org.eclipse.debug.core.ILaunchManager
import org.eclipse.debug.core.model.IProcess
import org.eclipse.debug.core.model.RuntimeProcess
import com.idiomaticsoft.lsp.scala.edit.MetalsProcess
import org.eclipse.jdt.launching.JavaRuntime
import org.eclipse.core.runtime.Path
import org.eclipse.jdt.launching.IRuntimeClasspathEntry
import org.eclipse.debug.core.model.IStreamsProxy2
import org.eclipse.debug.internal.core.StreamsProxy
import org.eclipse.debug.core.ILaunch
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy
import org.eclipse.debug.core.ILaunchConfiguration
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.ICoreRunnable
import org.eclipse.core.runtime.IProgressMonitor
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.CountDownLatch
import org.eclipse.core.runtime.NullProgressMonitor
import com.idiomaticsoft.lsp.scala.metals.MetalsLanguageClientImpl
import com.idiomaticsoft.lsp.scala.metals.operations.status.MetalsStatusBar
import com.idiomaticsoft.lsp.scala.metals.operations.status.MetalsStatusParams
import org.eclipse.swt.widgets.Display
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.mvc.MetalsTreeViewController
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.mvc.MetalsTreeViewControllerImpl
import com.idiomaticsoft.lsp.scala.metals.MetaslServerInterface
import org.eclipse.lsp4e.LanguageServiceAccessor

object ScalaLSPPlugin {

  @volatile private var plugin: ScalaLSPPlugin = _

  @volatile private var statusBar: MetalsStatusBar = _

  def apply(): ScalaLSPPlugin = plugin

  def registerStatusBar(statusBar: MetalsStatusBar): Unit = {
    this.statusBar = statusBar
  }

  def setStatusBar(status: MetalsStatusParams) = {
    Display
      .getDefault()
      .asyncExec(() => {
        if (Option(statusBar.label).isDefined) {
          statusBar.label.setText(status.text)
          statusBar.label.setToolTipText(status.tooltip)
        }
      })
  }
}

class ScalaLSPPlugin extends AbstractUIPlugin {

  @volatile private var treeViewController: MetalsTreeViewController = _

  val launchedJob = new AtomicBoolean(false)

  var scheduledJob = new CountDownLatch(1)

  @volatile var launch: ILaunch = null

  override def start(context: BundleContext) = {
    ScalaLSPPlugin.plugin = this
    super.start(context)
  }

  def getTreeViewController(): MetalsTreeViewController = {
    synchronized {
      if (Option(treeViewController).isEmpty) {
        treeViewController = new MetalsTreeViewControllerImpl
      }
      treeViewController
    }
  }

  def getLanguageServer(): MetaslServerInterface = {
    val ls = LanguageServiceAccessor.getActiveLanguageServers(_ => true)
    if (!ls.isEmpty()) {
      ls.get(0).asInstanceOf[MetaslServerInterface]
    } else {
      null
    }
  }

  def launchJob(): Unit = {
    val manager = DebugPlugin.getDefault().getLaunchManager()
    val typeConfiguration = manager.getLaunchConfigurationType(
      "com.idiomaticsoft.lsp.scala.languageServer.launchConfType"
    )
    val wc = typeConfiguration.newInstance(null, "Metals config")
    wc.setAttribute(
      IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
      "scala.meta.metals.Main"
    )
    wc.setAttribute(
      IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH,
      false
    )
    wc.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, false)
    val vmParams =
      "-XX:+UseG1GC -XX:+UseStringDeduplication -Xss4m -Xms100m -Xmx2G -Dmetals.status-bar=on -Dmetals.icons=unicode -Dmetals.slow-task=on -Dmetals.execute-client-command=on -Dmetals.input-box=on"
    wc.setAttribute(
      IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
      wc.getAttribute(
        IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
        vmParams
      )
    )
    wc.setAttribute(
      DebugPlugin.ATTR_PROCESS_FACTORY_ID,
      "com.idiomaticsoft.lsp.scala.metalsprocess"
    )
    val config = wc.doSave()
    launch = config.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor())
  }

  def processForCommand(): IProcess = {
    if (Option(launch).isDefined && launch
          .getProcesses()
          .toList
          .find(x => !x.isTerminated())
          .isEmpty) {
      if (launchedJob.compareAndSet(true, false)) {
        scheduledJob = new CountDownLatch(1)
      }
    }
    if (!launchedJob.getAndSet(true)) {
      launchJob()
      scheduledJob.countDown()
    }
    scheduledJob.await()
    val someProcess = launch.getProcesses().toList.find(x => !x.isTerminated())
    someProcess.getOrElse(null)

  }
}
