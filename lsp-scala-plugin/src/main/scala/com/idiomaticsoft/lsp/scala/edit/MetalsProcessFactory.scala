package com.idiomaticsoft.lsp.scala.edit

import org.eclipse.debug.core.IProcessFactory
import java.{util => ju}
import org.eclipse.debug.core.ILaunch
import org.eclipse.debug.core.model.IProcess

class MetalsProcessFactory extends IProcessFactory {

	override def newProcess(launch: ILaunch,
							process: Process, 
							label: String, 
							attributes: ju.Map[String,String]): IProcess = {
		new	MetalsProcess(launch, process, label, attributes)	
							}

}