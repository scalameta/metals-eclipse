package com.idiomaticsoft.lsp.scala.preferences
import org.eclipse.jface.preference.FieldEditorPreferencePage
import org.eclipse.ui.IWorkbenchPreferencePage
import org.eclipse.ui.IWorkbench
import org.eclipse.ui.preferences.ScopedPreferenceStore
import org.eclipse.core.runtime.preferences.InstanceScope
import org.eclipse.jface.preference.StringFieldEditor


object MetalsPreference {
	val METALS_PREFERENCE = "com.idiomaticsoft.lsp.scala.pref.page"
	
	val METALS_SEVER_VERSION = "com.idiomaticsoft.lsp.scala.server.version"
}

class MetalsPreference extends FieldEditorPreferencePage with IWorkbenchPreferencePage {

	import MetalsPreference._
	

	def createFieldEditors(): Unit = {
		val versionField = new StringFieldEditor(METALS_SEVER_VERSION, "Server version", getFieldEditorParent())
		versionField.loadDefault()
		addField(versionField)
	}

	def init(workbench: IWorkbench): Unit = {
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE,
			METALS_PREFERENCE))
		val store = getPreferenceStore()
		store.setDefault(METALS_SEVER_VERSION, "latest.release")
		setDescription("Preferences for the Metals Language Server")
	}
	
	


}