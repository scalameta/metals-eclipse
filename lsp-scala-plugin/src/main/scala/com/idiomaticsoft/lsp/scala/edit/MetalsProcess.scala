package com.idiomaticsoft.lsp.scala.edit

import org.eclipse.debug.core.model.RuntimeProcess
import org.eclipse.debug.core.ILaunch
import org.eclipse.debug.core.model.IStreamsProxy

class MetalsProcess(
    launch: ILaunch,
    process: Process,
    name: String,
    attributes: java.util.Map[String, String]
) extends RuntimeProcess(launch, process, name, attributes) {

  def inputStream() = {
    getSystemProcess().getInputStream()
  }

  def outputStream() = {
    getSystemProcess().getOutputStream()
  }

  def errorStream() = {
    getSystemProcess().getErrorStream()
  }

  // Streams will be handled by LSP
  override def createStreamsProxy(): IStreamsProxy = null

}
