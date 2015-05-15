package com.raytheon.datamodeler.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import com.raytheon.datamodeler.classes.SeniorDomain;

import com.raytheon.datamodeler.classes.SeniorModel;

import com.raytheon.datamodeler.views.TreeView;

public class DomainEditor extends EditorPart {
	public static final String ID = "com.raytheon.datamodeler.editors.domaineditor";
	
	private Text nameText;
	private Label nameLabel;
	private Combo domainCombo;
	private Label domainLabel;
	private ModelEditorInput input;
	private TreeViewer treeViewer;
	private boolean isPageModified;
	private SeniorDomain domain;

	private IWorkbench wb = PlatformUI.getWorkbench();
	private IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
	private IWorkbenchPage page = win.getActivePage();
	
	public DomainEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		this.setPartName(nameText.getText());
		domain.setName(domainCombo.getText());
		domainLabel.setText(domain.getName());
		nameText.setText(domain.getName());
		treeViewer.refresh();
		isPageModified = false;
		firePropertyChange(IEditorPart.PROP_DIRTY);

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO Auto-generated method stub
		if(!(input instanceof ModelEditorInput))
			throw new RuntimeException("Invalid Input: Must be ModelEditorInput");
		this.input = (ModelEditorInput) input;
		treeViewer = this.input.getTree();
		setSite(site);
		setInput(input);

	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return isPageModified;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		Image background = new Image(Display.getCurrent(), ModelEditor.class.getResourceAsStream("raytheon2.jpg"));
		parent.setBackgroundImage(background);
		parent.setLayout(null);
		final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		
		domain = (SeniorDomain) selection.getFirstElement();
		if(domain == null)
			this.getEditorSite().getPage().closeEditor(this, false);
			
		setPartName("Domain Editor");
		
		nameLabel = new Label(parent, SWT.NONE);
		nameLabel.setText("Name: ");
		nameLabel.setBounds(20,35,40,20);
					
		nameText = new Text(parent, SWT.BORDER);
		nameText.setText(domain.getName());
		nameText.setBounds(60,35,250,20);
		nameText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				// TODO Auto-generated method stub
				boolean wasDirty = isDirty();
				isPageModified = true;
				if(!wasDirty){
					firePropertyChange(IEditorPart.PROP_DIRTY);
				}
			}
		});
		nameText.addKeyListener(new KeyListener(){
			
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.character == 13)
					doSave(null);					
			}
			public void keyReleased(KeyEvent e) {
			}
			
		});
		
		nameText.setEditable(false);
		domainLabel = new Label(parent,SWT.NONE);
		domainLabel.setText(domain.getName());
		domainLabel.setBounds(20, 90, 190, 20);
		
		domainCombo = new Combo(parent, SWT.READ_ONLY);
		domainCombo.setItems(new String[]{"Avionics Messages"});
		domainCombo.setBounds(20,110,150,30);
		domainCombo.setText(domain.getName());
		domainCombo.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.character == 13)
					doSave(null);					
			}
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub	
			}
			
		});
		
		domainCombo.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				// TODO Auto-generated method stub
				boolean wasDirty = isDirty();
				isPageModified = true;
				if(!wasDirty){
					firePropertyChange(IEditorPart.PROP_DIRTY);
				}
			}
		});
		
		Button saveButton = new Button(parent,SWT.PUSH);
		saveButton.setText("Save");
		saveButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				doSave(null);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		saveButton.setBounds(20,300,50,30);
		saveButton.setToolTipText("Save changes.");
		
		Button deleteButton = new Button(parent,SWT.PUSH);
		deleteButton.setText("Delete");
		deleteButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				try {
					((TreeView) page.showView(TreeView.ID)).closeEditors(domain);
				} catch (PartInitException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}							
				((SeniorModel[])treeViewer.getInput())[0].getParentNode(domain).removeChild(domain);
				treeViewer.setExpandedState(domain,true);
        		treeViewer.refresh(true);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		deleteButton.setBounds(90,300,50,30);
		deleteButton.setToolTipText("Delete this node and all descendants");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}