package com.idiomaticsoft.lsp.scala.metals

import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification
import org.eclipse.lsp4e.LanguageClientImpl
import org.eclipse.lsp4e.LanguageServiceAccessor
import com.idiomaticsoft.lsp.scala.ScalaLSPPlugin

class MetalsLanguageClientImpl extends LanguageClientImpl with MetalsLanguageClient {

	
	override def metalsStatus(status: MetalsStatusParams) = {
		ScalaLSPPlugin.setStatusBar(status)
	}
}