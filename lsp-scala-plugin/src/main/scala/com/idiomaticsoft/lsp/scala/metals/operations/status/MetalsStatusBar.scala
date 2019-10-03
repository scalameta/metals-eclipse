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
    val layout = new FillLayout()
    layout.`type` = SWT.HORIZONTAL
    composite.setLayout(layout)
    label = new Label(composite, SWT.NONE)
    label.setText(" " * 100)
    label
  }
}
