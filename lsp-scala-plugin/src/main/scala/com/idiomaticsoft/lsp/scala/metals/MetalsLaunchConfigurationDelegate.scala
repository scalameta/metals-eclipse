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

package com.idiomaticsoft.lsp.scala.metals

import org.eclipse.jdt.launching.JavaLaunchDelegate
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.debug.core.{ILaunch, ILaunchConfiguration}
import org.eclipse.jdt.launching.JavaRuntime
import org.eclipse.core.runtime.Path
import coursier._
import org.eclipse.core.resources.WorkspaceJob
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants
import org.eclipse.jdt.launching.IRuntimeClasspathEntry
import org.eclipse.core.runtime.Platform
import com.idiomaticsoft.lsp.scala.preferences.MetalsPreference

class MetalsLaunchConfigurationDelegate extends JavaLaunchDelegate {

  // we don't need to save before launch, thus we return always true
  override def saveBeforeLaunch(
      configuration: ILaunchConfiguration,
      mode: String,
      monitor: IProgressMonitor
  ): Boolean = true

  override def launch(
      configuration: ILaunchConfiguration,
      mode: String,
      launch: ILaunch,
      pMonitor: IProgressMonitor
  ): Unit = {
    val monitor =
      if (Option(pMonitor).isEmpty) new NullProgressMonitor else pMonitor
    try {
      monitor.worked(1)
      val wc = configuration.getWorkingCopy()
      val metalsVersion = Platform.getPreferencesService().getString(MetalsPreference.METALS_PREFERENCE, MetalsPreference.METALS_SEVER_VERSION, "latest.release", null)
      import coursier._
      val fetch = Fetch()
        .addDependencies(
			Dependency(
			 mod"org.scalameta:metals_2.12",
			 metalsVersion
			)).run()
      import collection.JavaConverters._
      val classPathElements = fetch
        .map(x => new Path(x.toPath.toString()))
        .map(x => {
          val entry = JavaRuntime.newArchiveRuntimeClasspathEntry(x)
          entry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES)
          entry.getMemento()
        })
        .toList
        .asJava

      wc.setAttribute(
        IJavaLaunchConfigurationConstants.ATTR_CLASSPATH,
        classPathElements
      )
      wc.doSave()
      super.launch(configuration, mode, launch, monitor)
    } finally {
      monitor.done()
    }
  }
}
