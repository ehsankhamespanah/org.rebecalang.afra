package org.rebecalang.ide.afra.views.counterexample.rebeca;

import java.awt.Frame;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.rebecalang.ide.afra.general.RebecaUIPlugin;
import org.rebecalang.ide.afra.graphs.SystemStateGraph;
/*import org.rebecalang.afra.plugin.actions.SimulatorSocket;*/
import org.rebecalang.ide.afra.modelcheckreport.Message;
import org.rebecalang.ide.afra.modelcheckreport.Param;
import org.rebecalang.ide.afra.modelcheckreport.Parameters;
import org.rebecalang.ide.afra.modelcheckreport.Queue;
import org.rebecalang.ide.afra.modelcheckreport.Rebec;
import org.rebecalang.ide.afra.modelcheckreport.StateVar;
import org.rebecalang.ide.afra.modelcheckreport.StateVars;
import org.rebecalang.ide.afra.modelcheckreport.SystemState;
import org.rebecalang.ide.afra.modelcheckreport.Value;
import org.rebecalang.ide.afra.views.counterexample.QueueContent;
import org.rebecalang.ide.afra.views.counterexample.RebecItem;
import org.rebecalang.ide.afra.views.counterexample.RebecStatevarItem;
import org.rebecalang.ide.afra.views.counterexample.SystemStateItem;

public class SystemStateView extends ViewPart{
		
	public static StateContentProvider stateContentProvider = new StateContentProvider();
	
	public static StateLabelProvider stateLabelProvider = new StateLabelProvider();
	
	public SystemStateGraph systemStateGraph = new SystemStateGraph();
	
	private TreeViewer viewer;
	
	private Action actionPrev;

	private Action actionNext;

	private Action actionFirst;

	private Action actionLast;
	
	private Action actionTerminate;
	
	public SystemStateView(){
		CSystemState cst = new CSystemState();
    	setCSystemState(cst);
	}
	
	public void setSystemStateGraph(SystemStateGraph ssg)
	{
		systemStateGraph = ssg;
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
	    Frame frame = SWT_AWT.new_Frame(composite);
	    frame.add(systemStateGraph);
	    frame.setVisible(true);
		
		Tree tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		TreeColumn[] cols = new TreeColumn[2];
		cols[0] = new TreeColumn(tree, SWT.LEFT);
		cols[0].setText("Rebec");
		cols[0].setWidth(200);

		cols[1] = new TreeColumn(tree, SWT.LEFT);
		cols[1].setText("Value");
		cols[1].setWidth(500);
		
		CounterExampleView cev = new CounterExampleView();
		
		viewer = new TreeViewer(tree);
		viewer.setContentProvider(stateContentProvider);
		viewer.setLabelProvider(stateLabelProvider);
		viewer.setSorter(cev.new NameSorter());
		viewer.setInput(getViewSite());
		
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		this.viewer.expandAll();
		checkState();
		
	}
	

	private void checkState() {
		if (stateContentProvider.systemState != null) {
			
			actionTerminate.setEnabled(true);
			
			if (stateContentProvider.systemState.hasNextState()) {
				actionNext.setEnabled(true);
				actionLast.setEnabled(true);
			}
			else {
				actionNext.setEnabled(false);
				actionLast.setEnabled(false);
			}
			if (stateContentProvider.systemState.hasPrevState()) {
				actionPrev.setEnabled(true);
				actionFirst.setEnabled(true);
			}
			else {
				actionPrev.setEnabled(false);
				actionFirst.setEnabled(false);
			}
		}
		else {
			actionTerminate.setEnabled(false);
			actionNext.setEnabled(false);
			actionPrev.setEnabled(false);
			actionLast.setEnabled(false);
			actionFirst.setEnabled(false);
		}
	}
	
	private void prevState() {
		stateContentProvider.systemState.goToPrevSystemStateItem();
		checkState();
		this.viewer.refresh();
		this.viewer.expandAll();
	}
	
	private void nextState() {
		stateContentProvider.systemState.goToNextSystemStateItem();
		checkState();
		this.viewer.refresh();
		this.viewer.expandAll();
	}
	
	private void firstState() {
		stateContentProvider.systemState.goToFirstSystemStateItem();
		checkState();
		this.viewer.refresh();
		this.viewer.expandAll();
	}

	private void lastState() {
		stateContentProvider.systemState.goToLastSystemStateItem();
		checkState();
		this.viewer.refresh();
		this.viewer.expandAll();
	}
	
	
	public void terminate() {
		actionTerminate.setEnabled(false);
		if(systemStateGraph.NumOfARebecs!=0){}
			//SimulatorSocket.sendSelectedRebec(Integer.toString(-1));
	/*	String perspId = "org.afra.tools.core.perspective.rebeca";
		IAdaptable input = ResourcesPlugin.getWorkspace();
		IPreferenceStore store = RebecaUIPlugin.getStore();
        String pref = store.getString(IWorkbenchPreferenceConstants.OPEN_NEW_PERSPECTIVE);            
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbench workbench = PlatformUI.getWorkbench();
        // Implement open behavior.
        try {
                if (pref.equals(IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_WINDOW))
                        workbench.openWorkbenchWindow(perspId, input);
                else if (pref.equals(IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_PAGE))
                        window.openPage(perspId, input);
                else if (pref.equals(IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_REPLACE)) {
                        IPerspectiveRegistry reg = workbench.getPerspectiveRegistry();
                        window.getActivePage().setPerspective(reg.findPerspectiveWithId(perspId));
                }
        } catch (WorkbenchException e) {
                e.printStackTrace();
        }*/
	}
	
	private void makeActions() {
		String iconPath;
		actionPrev = new Action() {
			public void run() {
				prevState();
			}
		};
		actionPrev.setText("Previous State");
		actionPrev.setToolTipText("Previous State");
		actionPrev.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
		// ///////////////////////////
		actionNext = new Action() {
			public void run() {
				nextState();
			}
		};
		actionNext.setText("Next State");
		actionNext.setToolTipText("Next State");
		actionNext.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
		// ///////////////////////////
		actionLast = new Action() {
			public void run() {
				lastState();
			}
		};
		actionLast.setText("Last State");
		actionLast.setToolTipText("Last State");
		iconPath = "icons/last.png";
		ImageDescriptor imageDescriptor = RebecaUIPlugin.getImageDescriptor(iconPath);
		actionLast.setImageDescriptor(imageDescriptor);
		// ///////////////////////////
		actionFirst = new Action() {
			public void run() {
				firstState();
			}
		};
		actionFirst.setText("First State");
		actionFirst.setToolTipText("First State");
		iconPath = "icons/stop.gif";
		ImageDescriptor imageDescriptorFirst = RebecaUIPlugin.getImageDescriptor(iconPath);
		actionFirst.setImageDescriptor(imageDescriptorFirst);
		// ///////////////////////////
		actionTerminate = new Action() {
			public void run() {
				terminate();
			}
		};
		actionTerminate.setText("Terminate");
		actionTerminate.setToolTipText("Terminate");
		iconPath = "org/rebecalang/afra/icons/stop.gif";
		ImageDescriptor imageDescriptorTerminate;
//		try {
//			imageDescriptorTerminate = ImageDescriptor.createFromURL(new URL(PDEPlugin.getDefault().getInstallURL(), iconPath));
//			;
//			actionTerminate.setImageDescriptor(imageDescriptorTerminate);
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
			
		URL installUrl;
		try {
//			installUrl = FileLocator.toFileURL(url);//Platform.toFileURL();Platform.find(Platform.getBundle(RebecaUIPlugin.PLUGIN_ID), new Path(".")));
			   URL imageUrl = new URL("file:icons/stop.gif");
			   System.out.println(ImageDescriptor.createFromURL(imageUrl));
			   imageUrl = new URL("file:/icons/stop.gif");
			   System.out.println(ImageDescriptor.createFromURL(imageUrl));
			   imageUrl = new URL("file:org.rebecalang.afra/icons/stop.gif");
			   System.out.println(ImageDescriptor.createFromURL(imageUrl));
			   imageUrl = new URL("file:org/rebecalang/afra/icons/stop.gif");
			   System.out.println(ImageDescriptor.createFromURL(imageUrl));
			   imageUrl = new URL("file:/org/rebecalang/afra/icons/stop.gif");
			   System.out.println(ImageDescriptor.createFromURL(imageUrl));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println(RebecaUIPlugin.getImageDescriptor("/icons/stop.png"));
		System.out.println(RebecaUIPlugin.getImageDescriptor("icons/stop.png"));
		System.out.println(RebecaUIPlugin.getImageDescriptor("org.rebecalang.afra.plugin/icons/stop.png"));
		System.out.println(RebecaUIPlugin.getImageDescriptor("org/rebecalang/afra/plugin/icons/stop.png"));
		System.out.println(RebecaUIPlugin.getImageDescriptor("/org/rebecalang/afra/plugin/icons/stop.png"));
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SystemStateView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionTerminate);
		manager.add(actionFirst);
		manager.add(actionPrev);
		manager.add(actionNext);
		manager.add(actionLast);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionTerminate);
		manager.add(actionFirst);
		manager.add(actionPrev);
		manager.add(actionNext);
		manager.add(actionLast);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionTerminate);
		manager.add(actionFirst);
		manager.add(actionPrev);
		manager.add(actionNext);
		manager.add(actionLast);
	}

	public void checkRefreshAll() {
		checkState();
		this.viewer.getControl().setFocus();
		this.viewer.refresh();
		this.viewer.expandAll();
	}
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	public void setSystemState(String sysState) {
		SystemState systemState = new SystemState();
		int n, n2;
		n = sysState.indexOf("<rebec");
		n2 = sysState.indexOf("</rebec>");
        while(n!=-1)
        {
        	Rebec rebec = new Rebec();
        	Queue queue = new Queue();
        	StateVars stateVars = new StateVars();
        	
        	String r = sysState.substring(n+23,n2);
        	sysState = sysState.substring(n2+8);
        	n = r.indexOf("\"");
        	rebec.setReactiveClass(r.substring(0,n));
        	r = r.substring(n+8);
        	n = r.indexOf("\"");
        	rebec.setName(r.substring(0,n));
        	
        	n = r.indexOf("<message");
    		n2 = r.indexOf("</message>");
    		while(n!=-1)	//filling Messages
    		{
    			Message msg = new Message();
    			Parameters parameters = new Parameters();
    			
    			String m = r.substring(n+15,n2);
    			r = r.substring(n2+9);
    			
    			n = m.indexOf("\"");
    			msg.setName(m.substring(0,n));
    			n = m.indexOf("<sender>");
    			n2 = m.indexOf("</sender>");
    			msg.setSender(m.substring(n+8,n2));
    			
    			n = m.indexOf("<parameters>");
    			n2 = m.indexOf("</parameters>");
    			m = m.substring(n+12,n2);
    			
    			n = m.indexOf("<param");
    			n2 = m.indexOf("</param>");
    			while(n!=-1)
    			{
    				Param param = new Param();
    				
    				String p = m.substring(n+13,n2);
    				m = m.substring(n2+7);
    				n = p.indexOf("\"");
        			param.setName(p.substring(0,n));
        			
        			
        			n = p.indexOf("<value>");
                	n2 = p.indexOf("</value>");
        			while(n!=-1)
        			{
        				Value val = new Value();
        				
        				val.setvalue(p.substring(n+7,n2));
        				p = p.substring(n2+8);
        				
        				param.getValue().add(val);
        				n = p.indexOf("<value>");
                    	n2 = p.indexOf("</value>");
        			}
        			
    				parameters.getParam().add(param);
    				n = m.indexOf("<param");
        			n2 = m.indexOf("</param>");
    			}
    			msg.setParameters(parameters);
    			
    			queue.getMessage().add(msg);
    			n = r.indexOf("<message");
        		n2 = r.indexOf("</message>");
    		}
    		
    		rebec.setQueue(queue);
        	
        	n = r.indexOf("<state-vars>");
        	n2 = r.indexOf("</state-vars>");
        	r = r.substring(n+13,n2);
        	
        	n = r.indexOf("<var");
        	n2 = r.indexOf("</var>");
    		while(n!=-1) 	//filling Vars
    		{
    			StateVar var = new StateVar();
    			
    			String v = r.substring(n+11,n2);
    			r = r.substring(n2+6);
    			n = v.indexOf("\"");
    			var.setName(v.substring(0,n));
    			
    			n = v.indexOf("<value>");
            	n2 = v.indexOf("</value>");
    			while(n!=-1)
    			{
    				Value val = new Value();
    				
    				val.setvalue(v.substring(n+7,n2));
    				v = v.substring(n2+8);
    				
    				var.getValue().add(val);
    				n = v.indexOf("<value>");
                	n2 = v.indexOf("</value>");
    			}
    			
    			stateVars.getStateVar().add(var);
    			n = r.indexOf("<var");
            	n2 = r.indexOf("</var>");
    		}
    		
    		n = sysState.indexOf("<rebec");
    		n2 = sysState.indexOf("</rebec>");
    		
    		rebec.setStateVars(stateVars);
        	systemState.getRebec().add(rebec);
        }
    	System.out.println("State is set!");
    	parse(systemState);
	}

	private void parse(SystemState systemState)
	{
		System.out.println("parse");
		SystemStateItem systemStateItem = new SystemStateItem();//stateContentProvider.getSystemStateItem();;
		List<Rebec> rebecs = systemState.getRebec();
		for (Iterator<Rebec> iterator = rebecs.iterator(); iterator.hasNext();) {
			Rebec r = (Rebec) iterator.next();
			RebecItem rItem = stateContentProvider.systemState.processRebec(r);
			systemStateItem.addRebecItem(rItem);
		}
		stateContentProvider.systemState.addSystemStateItem(systemStateItem);
	}

	public void setCSystemState(CSystemState st) {
		SystemStateView.stateContentProvider.systemState = st;
	}
	
	public static CSystemState getCSystemState()
	{
		return SystemStateView.stateContentProvider.systemState;
	}
}

class StateContentProvider implements ITreeContentProvider{
	
	public CSystemState systemState = null;
	
	public StateContentProvider() {
	}

	public SystemStateItem getCurrentSystemStateItem() {
		return systemState.getCurrentSystemStateItem();
	}

	public Object[] getElements(Object parent) {
		if (systemState != null) {
			SystemStateItem s = systemState.getCurrentSystemStateItem();
			List<Object> l = new ArrayList<Object>();
			l.addAll(s.getRebecItems());
			return l.toArray();
		}
		else {
			return new Object[0];
		}
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof RebecItem) {
			List<Object> l = new ArrayList<Object>();
			l.addAll(((RebecItem) parentElement).getStatevars());
			l.add(((RebecItem) parentElement).getMsgsrvQueue().getMessageQueue());
			l.add(((RebecItem) parentElement).getMsgsrvQueue().getSenderQueue());
			return l.toArray();
		}
		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof RebecStatevarItem) {
			((RebecStatevarItem) element).getRebec();
		}
		else if (element instanceof QueueContent) {
			((QueueContent) element).getMsgsrvQueue().getRebec();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		return element instanceof RebecItem;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}

class StateLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider {
	public String getColumnText(Object obj, int index) {
		if (obj instanceof RebecItem) {
			return index == 0 ? ((RebecItem) obj).getName() + ":" + ((RebecItem) obj).getType()
					: "";
		}
		else if (obj instanceof RebecStatevarItem) {
			return index == 0 ? ((RebecStatevarItem) obj).getName() : ((RebecStatevarItem) obj)
					.getValue();
		}
		else if (obj instanceof QueueContent) {
			return index == 0 ? ((QueueContent) obj).getName() : ((QueueContent) obj)
					.getContent();
		}
		return null;
	}

	public Image getColumnImage(Object obj, int index) {
		if (index > 0)
			return null;
		return getImage(obj);
	}

	public Image getImage(Object obj) {
		if (obj instanceof RebecStatevarItem || obj instanceof QueueContent)
			return null;
		return PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_OBJ_ELEMENT);
	}

	@Override
	public Color getForeground(Object element) {
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		if(!SystemStateView.stateContentProvider.systemState.hasPrevState())
			return null;
		if (SystemStateView.stateContentProvider.systemState.hasChanged(element)) 
			return new Color(Display.getCurrent(), 255, 255 ,0);
		return null;
	}
}



