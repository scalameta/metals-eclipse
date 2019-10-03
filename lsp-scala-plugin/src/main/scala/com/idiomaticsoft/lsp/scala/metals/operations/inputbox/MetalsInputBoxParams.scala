package com.idiomaticsoft.lsp.scala.metals.operations.inputbox
import scala.beans.BeanProperty


class MetalsInputBoxParams(
	@BeanProperty var value: String,
  	@BeanProperty var prompt: String,
    @BeanProperty var placeHolder: String,
  	@BeanProperty var password: Boolean,
  	@BeanProperty var ignoreFocusOut: Boolean
)

class MetalsInputBoxResult(
  @BeanProperty var value: String,
  @BeanProperty var cancelled: Boolean
)