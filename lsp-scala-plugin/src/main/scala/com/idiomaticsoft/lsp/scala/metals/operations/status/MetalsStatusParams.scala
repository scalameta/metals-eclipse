/*******************************************************************************
 * Copyright (c) 2019 Idiomaticsoft S.R.L. and others.
 * This program and the accompanying materials are made
 * available under the terms of the APACHE LICENSE, VERSION 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *  Edmundo Lopez B. (Idiomaticsoft S.R.L.) - initial implementation
 *******************************************************************************/

package com.idiomaticsoft.lsp.scala.metals.operations.status
import scala.beans.BeanProperty

class MetalsStatusParams(
    @BeanProperty var text: String = null,
    @BeanProperty var show: Boolean = false,
    @BeanProperty var hide: Boolean = false,
    @BeanProperty var tooltip: String = null,
    @BeanProperty var command: String = null
)
