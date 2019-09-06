package com.idiomaticsoft.lsp.scala.metals

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup
import org.eclipse.debug.ui.ILaunchConfigurationDialog
import org.eclipse.debug.ui.DebugUITools
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab 
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab
import org.eclipse.debug.ui.EnvironmentTab
import org.eclipse.debug.ui.ILaunchConfigurationTab

class MetalsLanguageServerTabGroup extends AbstractLaunchConfigurationTabGroup {

	override def createTabs(dialog: ILaunchConfigurationDialog, mode: String): Unit = {
		val configuration = DebugUITools.getLaunchConfiguration(dialog)
		val tabs = Array[ILaunchConfigurationTab](
			new JavaArgumentsTab(),
			new JavaJRETab(true),
			new EnvironmentTab(),
			)
		import collection.JavaConverters._
		setTabs(tabs)
	}
}