package com.idiomaticsoft.lsp.scala.metals.operations.treeview.mvc
import org.eclipse.ui.part.ViewPart
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Text
import org.eclipse.swt.SWT
import com.idiomaticsoft.lsp.scala.ScalaLSPPlugin
import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.jface.layout.GridLayoutFactory
import org.eclipse.jface.viewers.ITreeViewerListener
import org.eclipse.jface.viewers.TreeExpansionEvent

class MetalsTreeView extends ViewPart {

	val controller = ScalaLSPPlugin().getTreeViewController()
	
	var viewer: TreeViewer = _
	 
	controller.view = this
	
	

	override def createPartControl(parent: Composite) = {
		viewer = new TreeViewer(parent, SWT.VIRTUAL)
		viewer.setContentProvider(controller)
		viewer.setUseHashlookup(true)
		GridLayoutFactory.fillDefaults().generateLayout(parent)
		viewer.addTreeListener(new ITreeViewerListener {
			def treeCollapsed(event: TreeExpansionEvent): Unit = {
				controller.elementCollapsedOrExpanded(event.getElement(), true)
			}
			
			def treeExpanded(event: TreeExpansionEvent): Unit = {
				controller.elementCollapsedOrExpanded(event.getElement(), false)
			}
		})
		viewer.setInput(controller.parentNodes.toArray)
		viewer.getTree().setItemCount(controller.parentNodes.size)
	}
	
	override def setFocus(): Unit = {}

}