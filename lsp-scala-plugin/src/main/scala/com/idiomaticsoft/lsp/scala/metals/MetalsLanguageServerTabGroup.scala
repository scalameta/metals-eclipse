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

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup
import org.eclipse.debug.ui.ILaunchConfigurationDialog
import org.eclipse.debug.ui.DebugUITools
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab
import org.eclipse.debug.ui.EnvironmentTab
import org.eclipse.debug.ui.ILaunchConfigurationTab

class MetalsLanguageServerTabGroup extends AbstractLaunchConfigurationTabGroup {

  override def createTabs(
      dialog: ILaunchConfigurationDialog,
      mode: String
  ): Unit = {
    val configuration = DebugUITools.getLaunchConfiguration(dialog)
    val tabs = Array[ILaunchConfigurationTab](
      new JavaArgumentsTab(),
      new JavaJRETab(true),
      new EnvironmentTab()
    )
    import collection.JavaConverters._
    setTabs(tabs)
  }
}
