package com.idiomaticsoft.lsp.scala.metals.operations.status
import scala.beans.BeanProperty

class MetalsStatusParams (
			@BeanProperty var text: String = null,
			@BeanProperty var show: Boolean = false,
			@BeanProperty var hide: Boolean = false,
			@BeanProperty var tooltip: String = null, 
			@BeanProperty var command: String = null)