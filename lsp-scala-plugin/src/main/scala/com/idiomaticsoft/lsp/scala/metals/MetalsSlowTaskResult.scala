package com.idiomaticsoft.lsp.scala.metals

class MetalsSlowTaskResult(
			var cancel: Boolean) {
			
		def getCancel() = cancel
		
		def setCancel(cancel: Boolean) = {
			this.cancel = cancel
		}			
}
 