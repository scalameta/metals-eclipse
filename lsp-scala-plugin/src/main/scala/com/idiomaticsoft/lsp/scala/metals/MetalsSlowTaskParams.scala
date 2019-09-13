package com.idiomaticsoft.lsp.scala.metals

class MetalsSlowTaskParams(
			var id: String,
			var message: String) {
			
		def getId() = id
		
		def setId(id: String) = {
			this.id = id
		}			
			
		def getMessage() = message
		
		def setMessage(message: String) = {
			this.message = message
		}			
}
 