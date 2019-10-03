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

package com.idiomaticsoft.lsp.scala.metals.operations.treeview.server
import scala.beans.BeanProperty
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.TreeViewNode

class TreeViewChildrenParams(
    @BeanProperty var viewId: String = null,
    @BeanProperty var nodeUri: String = null
)

class TreeViewChildrenResult(
    @BeanProperty var nodes: Array[TreeViewNode] = null
)

class TreeViewParentResult(@BeanProperty var uri: String = null)

class TreeViewNodeCollapseDidChangeParams(
    @BeanProperty var viewId: String = null,
    @BeanProperty var nodeUri: String = null,
    @BeanProperty var collapsed: Boolean = false
)

class TreeViewVisibilityDidChangeParams(
    @BeanProperty var viewId: String = null,
    @BeanProperty var visible: Boolean = false
)

class MetalsTreeRevealResult(
    @BeanProperty var viewId: String = null,
    @BeanProperty var uriChain: Array[String] = null
)
