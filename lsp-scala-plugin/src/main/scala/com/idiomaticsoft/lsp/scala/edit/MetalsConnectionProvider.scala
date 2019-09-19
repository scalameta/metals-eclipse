package com.idiomaticsoft.lsp.scala.edit


import org.eclipse.lsp4e.server.StreamConnectionProvider
import com.idiomaticsoft.lsp.scala.ScalaLSPPlugin
import org.eclipse.debug.core.model.IProcess
import com.idiomaticsoft.lsp.scala.metals.ExperimentalFeatures




class MetalConnectionProvider extends StreamConnectionProvider {


  var process: Option[MetalsProcess] = None

  def getErrorStream()  =  process.map(_.errorStream()).getOrElse(null)
 
  def getInputStream() = process.map(_.inputStream()).getOrElse(null)
 
  def getOutputStream() = process.map(_.outputStream()).getOrElse(null)
 
  def start(): Unit = {
    process = Option(ScalaLSPPlugin().processForCommand().asInstanceOf[MetalsProcess])
  }
  def stop(): Unit = process.map(_.terminate())

  override def getExperimentalFeaturesPOJO(): Object = {
	val experimentalFeatures = new ExperimentalFeatures
	experimentalFeatures.setTreeViewProvider(true)
	experimentalFeatures
  }

}