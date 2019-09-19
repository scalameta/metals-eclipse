package com.idiomaticsoft.lsp.scala.metals.operations.status

class MetalsStatusParams (
			var text: String = null,
			var show: Boolean = false,
			var hide: Boolean = false,
			var tooltip: String = null, 
			var command: String = null) {
			
		def getText() = text
		
		def getShow()= show
		
		def getHide() = hide
		
		def getTooltip() = tooltip
		
		def getCommand() = command
		
		def setText(text: String) = {
			this.text = text
		}
		
		def setShow(show: Boolean) = {
			this.show = show
		}
		
		def setHide(hide: Boolean) = {
			this.hide = hide
		}
		
		def setTooltip(tooltip: String) = {
			this.tooltip = tooltip
		}
		
		def setCommand(command: String) = {
			this.command = command
		}
			
}
 