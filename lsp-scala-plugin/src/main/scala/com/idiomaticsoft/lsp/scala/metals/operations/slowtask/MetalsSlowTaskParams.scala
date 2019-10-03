package com.idiomaticsoft.lsp.scala.metals.operations.slowtask

import scala.beans.BeanProperty

class MetalsSlowTaskParams(
    @BeanProperty var id: String = null,
    @BeanProperty var message: String = null
)

class MetalsSlowTaskResult(@BeanProperty var cancel: Boolean = false)
