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

import org.eclipse.debug.core.IProcessFactory
import java.{util => ju}
import org.eclipse.debug.core.ILaunch
import org.eclipse.debug.core.model.IProcess

class MetalsProcessFactory extends IProcessFactory {

  override def newProcess(
      launch: ILaunch,
      process: Process,
      label: String,
      attributes: ju.Map[String, String]
  ): IProcess = {
    new MetalsProcess(launch, process, label, attributes)
  }

}
