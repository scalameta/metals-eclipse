package com.idiomaticsoft.lsp.scala.metals

class MetalsStatusParams(
			var text: String,
			var show: Boolean,
			var hide: Boolean,
			var tooltip: String, 
			var command: String) {
			
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
 