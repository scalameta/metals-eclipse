package com.idiomaticsoft.lsp.scala.metals

import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification

trait MetalsLanguageClient extends LanguageClient {
	
	
	@JsonNotification("metals/status")
	def metalsStatus(status: MetalsStatusParams): Unit

}