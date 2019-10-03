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

package com.idiomaticsoft.lsp.scala.edit

import org.eclipse.lsp4e.server.StreamConnectionProvider
import com.idiomaticsoft.lsp.scala.ScalaLSPPlugin
import org.eclipse.debug.core.model.IProcess
import com.idiomaticsoft.lsp.scala.metals.ExperimentalFeatures

class MetalConnectionProvider extends StreamConnectionProvider {

  var process: Option[MetalsProcess] = None

  def getErrorStream() = process.map(_.errorStream()).getOrElse(null)

  def getInputStream() = process.map(_.inputStream()).getOrElse(null)

  def getOutputStream() = process.map(_.outputStream()).getOrElse(null)

  def start(): Unit = {
    process = Option(
      ScalaLSPPlugin().processForCommand().asInstanceOf[MetalsProcess]
    )
  }
  def stop(): Unit = process.map(_.terminate())

  override def getExperimentalFeaturesPOJO(): Object = {
    val experimentalFeatures = new ExperimentalFeatures
    experimentalFeatures.setTreeViewProvider(true)
    experimentalFeatures
  }

}
