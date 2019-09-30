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
import org.eclipse.swt.widgets.Menu
import org.eclipse.swt.widgets.ToolBar
import org.eclipse.core.runtime.Platform
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.ToolItem
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.jface.text.ITextOperationTarget
import org.eclipse.jface.text.ITextViewer
import org.eclipse.lsp4e.LSPEclipseUtils
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.custom.StyledText
import org.eclipse.jface.viewers.LabelProvider
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.TreeViewNode
import org.eclipse.gmf.runtime.draw2d.ui.render.awt.internal.svg.SVGImageConverter
import org.eclipse.gmf.runtime.draw2d.ui.render.RenderInfo
import org.eclipse.gmf.runtime.draw2d.ui.render.factory.RenderedImageFactory
import org.apache.commons.io.IOUtils
import org.eclipse.swt.widgets.Display
import java.io.ByteArrayInputStream
import org.eclipse.jface.viewers.IDoubleClickListener
import org.eclipse.jface.viewers.DoubleClickEvent
import org.eclipse.swt.widgets.ExpandBar
import org.eclipse.swt.widgets.ExpandItem
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.custom.StackLayout
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.events.ExpandListener
import org.eclipse.swt.events.ExpandEvent
import com.idiomaticsoft.lsp.scala.metals.operations.treeview.server.TreeViewVisibilityDidChangeParams
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.lsp4j.ExecuteCommandParams

class MetalsTreeView extends ViewPart {

	val controller = ScalaLSPPlugin().getTreeViewController()
	 
	controller.view = this
	
	private var viewsTo:scala.collection.mutable.HashMap[String, (ExpandItem, TreeViewer)] = new scala.collection.mutable.HashMap()
	
	var expandBar: ExpandBar = _
	
	var parentView: Composite = _

	override def createPartControl(parent: Composite) = {
		val parentLayout = new GridLayout(1, false)
		parent.setLayout(parentLayout)
		parent.setSize(parent.getShell().getSize())
		val bar = new ToolBar(parent, SWT.HORIZONTAL)
		bar.setLocation(0,0)
		val resourceStream = getClass().getClassLoader.getResourceAsStream("icons/full/etools/crosshair.png")
		val openIcon = new Image(parent.getDisplay(), resourceStream)
		val openToolItem = new ToolItem(bar, SWT.PUSH | SWT.BORDER)
		openToolItem.setImage(openIcon)
		openToolItem.setToolTipText("Go to tree location")
		openToolItem.addSelectionListener(new SelectionListener {
			override def widgetSelected(e: SelectionEvent) = {
				val editorPart = getSite().getPage().getActiveEditor()
				if (Option(editorPart).isDefined) {
					val target = editorPart.getAdapter(classOf[ITextOperationTarget])
					if (target.isInstanceOf[ITextViewer]) {
						val textViewer = target.asInstanceOf[ITextViewer]
						val text = editorPart.getAdapter(classOf[Control]).asInstanceOf[StyledText]
						val offset = text.getCaretOffset()
						val activeEditor = LSPEclipseUtils.getActiveTextEditor()
						val document = activeEditor.getDocumentProvider().getDocument(activeEditor.getEditorInput())
						val textDocumentPositionParams = LSPEclipseUtils.toTextDocumentPosistionParams(offset, document)
						controller.reveal(textDocumentPositionParams)
					}
				}
			}
			
			override def widgetDefaultSelected(e: SelectionEvent) = {
			}
		})
		expandBar = new ExpandBar(parent, SWT.V_SCROLL | SWT.BORDER)
		val barGridData = new GridData
		barGridData.horizontalAlignment = GridData.FILL
		barGridData.verticalAlignment = GridData.FILL
		barGridData.grabExcessHorizontalSpace = true
		barGridData.grabExcessVerticalSpace = true
		expandBar.setLayoutData(barGridData)
		expandBar.addExpandListener(new ExpandListener {
			def itemCollapsed(evt: ExpandEvent): Unit = {
				Option(evt.item.getData()).map({ data =>
					controller.elementCollapsed(data, true)
					controller.languageServer().treeViewVisibilityDidChange(new TreeViewVisibilityDidChangeParams(data.asInstanceOf[TreeViewNode].getViewId(), false))
				})
			}
  			def itemExpanded(evt: ExpandEvent): Unit = {
				Option(evt.item.getData()).map({ data => 
					controller.elementCollapsed(data, false)
					controller.languageServer().treeViewVisibilityDidChange(new TreeViewVisibilityDidChangeParams(data.asInstanceOf[TreeViewNode].getViewId(), true))
				})
			}
		})
		parentView = parent
		parentView.pack()
		parentView.update()
	}
	
	def fillExpandBar(viewId: String, treeViewNodes: Array[TreeViewNode]) = {
		val (_, treeViewer) = viewsTo(viewId)
		treeViewer.setInput(treeViewNodes)
		treeViewer.getTree().setItemCount(treeViewNodes.size)
	}
	
	def createExpandBar(viewId: String, label: String, treeViewNode: TreeViewNode) = {
		val expandItem = new ExpandItem(expandBar, SWT.NONE, 0)
	 	val composite = new Composite (expandBar, SWT.NONE)
		val treeViewer = createViewer(composite, viewId)
		expandItem.setText(label);
		expandItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y)
		expandItem.setControl(composite)
		expandItem.setData(treeViewNode)
		viewsTo.put(viewId, (expandItem, treeViewer))	
	}
	
	def udpdateViewNode(viewId: String, treeViewNode: TreeViewNode) = {
		viewsTo.get(viewId) match {
			case Some((_, treeViewer)) => {
				treeViewer.update(treeViewNode, null)
			}
			case None =>
				// no node to update
				println("No node to update")
		}
	}
	
	def createViewer(parent: Composite, viewId: String) = {
		val viewer = new TreeViewer(parent, SWT.VIRTUAL)
		viewer.setContentProvider(controller.contentProvider(viewId))
		viewer.setLabelProvider(new LabelProvider {
			override def getImage(obj: Any): Image = {
				obj match {
					case tvn :TreeViewNode =>
						Option(tvn.getIcon()).map({ icon =>
							val someResourceStream = Option(getClass().getClassLoader.getResourceAsStream(s"icons/metals/$icon.png"))
							someResourceStream.map( { resourceStream =>
								val bytes = IOUtils.toByteArray(resourceStream)
								new Image(Display.getCurrent, new ByteArrayInputStream(bytes))
							}).getOrElse(null)
						}).getOrElse(null)
					case _ =>
						null
				}
			}
			override def getText(obj: Any): String = {
				obj match {
					case tvn :TreeViewNode =>
						tvn.toString()
				}
			}
		})
		viewer.setUseHashlookup(true)
		GridLayoutFactory.fillDefaults().generateLayout(parent)
		viewer.addTreeListener(new ITreeViewerListener {
			def treeCollapsed(event: TreeExpansionEvent): Unit = {
				controller.elementCollapsed(event.getElement(), true)
			}
			
			def treeExpanded(event: TreeExpansionEvent): Unit = {
				controller.elementCollapsed(event.getElement(), false)
			}
		})
		viewer.addDoubleClickListener(new IDoubleClickListener {
			def doubleClick(evt: DoubleClickEvent): Unit = {
				for {
					selection <- Option(evt.getSelection().asInstanceOf[IStructuredSelection])
					obj <- if (!selection.isEmpty()) Option(selection.getFirstElement()) else None
					val treeViewNode = obj.asInstanceOf[TreeViewNode]
					command <- Option(treeViewNode.getCommand())
				} yield {
					val lsp4jcommand = new ExecuteCommandParams()
					lsp4jcommand.setCommand(command.getCommand())
					lsp4jcommand.setArguments(java.util.Arrays.asList(command.getArguments()))
					controller.languageServer().getWorkspaceService().executeCommand(lsp4jcommand)
				}
			}
		})
		viewer
	}
	
	def viewer(viewId:String) = viewsTo.get(viewId).map(_._2)
	
	def viewers = viewsTo.keySet.toList
	
	def expandBar(viewId:String) = viewsTo.get(viewId).map(_._1)
	
	override def setFocus(): Unit = {}

}