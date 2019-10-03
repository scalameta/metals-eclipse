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
