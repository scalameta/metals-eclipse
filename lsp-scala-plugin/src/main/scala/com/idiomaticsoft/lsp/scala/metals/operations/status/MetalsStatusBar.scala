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

package com.idiomaticsoft.lsp.scala.metals.operations.status

import org.eclipse.ui.menus.WorkbenchWindowControlContribution
import org.eclipse.swt.widgets.{Composite, Control}
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.SWT
import com.idiomaticsoft.lsp.scala.ScalaLSPPlugin
import org.eclipse.swt.layout.FillLayout
import org.eclipse.jface.resource.FontDescriptor
import org.eclipse.swt.graphics.Font
import org.eclipse.swt.graphics.FontData

class MetalsStatusBar extends WorkbenchWindowControlContribution {

  ScalaLSPPlugin.registerStatusBar(this)

  var label: Label = _

  def createControl(composite: Composite): Control = {
	composite.getParent().setRedraw(true)
    val layout = new FillLayout()
    layout.`type` = SWT.HORIZONTAL
    composite.setLayout(layout)
    label = new Label(composite, SWT.NONE)
    label.setText(" " * 100)
    label
  }

  override def isDynamic() = true

	
}
