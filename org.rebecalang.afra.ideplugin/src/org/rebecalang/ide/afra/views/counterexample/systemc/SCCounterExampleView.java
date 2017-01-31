package org.rebecalang.ide.afra.views.counterexample.systemc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.rebecalang.ide.afra.general.RebecaUIPlugin;
import org.rebecalang.ide.afra.views.counterexample.GlobalStatevarItem;
import org.rebecalang.ide.afra.views.counterexample.GlobalVariablesItem;
import org.rebecalang.ide.afra.views.counterexample.InfoItem;
import org.rebecalang.ide.afra.views.counterexample.Item;
import org.rebecalang.ide.afra.views.counterexample.ItemComparator;
import org.rebecalang.ide.afra.views.counterexample.PropertyItem;
import org.rebecalang.ide.afra.views.counterexample.QueueContent;
import org.rebecalang.ide.afra.views.counterexample.RebecItem;
import org.rebecalang.ide.afra.views.counterexample.RebecStatevarItem;
import org.rebecalang.ide.afra.views.counterexample.SystemStateItem;
import org.rebecalang.ide.afra.modelcheckreport.ModelCheckingReport;

public class SCCounterExampleView extends ViewPart {
	private static Logger logger = Logger
			.getLogger("org.afra.tools.core.views.SCCounterExampleView");

	private TreeViewer viewer;

	private Action actionGotoState;

	private Action actionPrev;

	private Action actionNext;

	private Action actionFirst;

	private Action actionLast;

	private Action actionLoad;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	public void setCounterExample(CounterExample ce) {
		this.viewContentProvider.counterExample = ce;
	}

	class ViewContentProvider implements ITreeContentProvider {

		CounterExample counterExample = null;

		public ViewContentProvider() {

		}

		public Object[] getElements(Object parent) {
			if (counterExample != null) {
				SystemStateItem s = counterExample.getCurrentSystemStateItem();
				List<Object> l = new ArrayList<Object>();
				l.addAll(s.getInfoItems());
				if (s.getGlobalVariablesItem() != null) {
					l.add(s.getGlobalVariablesItem());
				}
				l.addAll(s.getRebecItems());
				l.addAll(s.getPropertyItems());
				return l.toArray();
			}
			else {
				return new Object[0];
			}
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof GlobalVariablesItem) {
				return ((GlobalVariablesItem) parentElement).getGlobalStatevarItems().toArray();
			}
			else if (parentElement instanceof RebecItem) {
				List<Object> l = new ArrayList<Object>();
				l.addAll(((RebecItem) parentElement).getStatevars());
				l.add(((RebecItem) parentElement).getMsgsrvQueue().getMessageQueue());
				l.add(((RebecItem) parentElement).getMsgsrvQueue().getSenderQueue());
				return l.toArray();
			}
			else if (parentElement instanceof InfoItem) {
				return ((InfoItem) parentElement).getInfoItems().toArray();
			}
			return null;
		}

		public Object getParent(Object element) {
			if (element instanceof GlobalStatevarItem) {
				((GlobalStatevarItem) element).getGlobalVariables();
			}
			else if (element instanceof RebecStatevarItem) {
				((RebecStatevarItem) element).getRebec();
			}
			else if (element instanceof QueueContent) {
				((QueueContent) element).getMsgsrvQueue().getRebec();
			}
			else if (element instanceof PropertyItem) {
				((PropertyItem) element).getParent();
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			return element instanceof GlobalVariablesItem || element instanceof RebecItem
					|| element instanceof InfoItem;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			if (obj instanceof GlobalVariablesItem) {
				return index == 0 ? "global variables" : "";

			}
			else if (obj instanceof GlobalStatevarItem) {
				return index == 0 ? ((GlobalStatevarItem) obj).getName()
						: ((GlobalStatevarItem) obj).getValue();
			}
			else if (obj instanceof RebecItem) {
				return index == 0 ? ((RebecItem) obj).getName() + ":" + ((RebecItem) obj).getType()
						: "";
			}
			else if (obj instanceof InfoItem) {
				return index == 0 ? ((InfoItem) obj).getName() : "";
			}
			else if (obj instanceof RebecStatevarItem) {
				return index == 0 ? ((RebecStatevarItem) obj).getName() : ((RebecStatevarItem) obj)
						.getValue();
			}
			else if (obj instanceof QueueContent) {
				return index == 0 ? ((QueueContent) obj).getName() : ((QueueContent) obj)
						.getContent();
			}
			else if (obj instanceof PropertyItem) {
				return index == 0 ? ((PropertyItem) obj).getName() : ((PropertyItem) obj)
						.getValue();
			}
			return null;
		}

		public Image getColumnImage(Object obj, int index) {
			if (index > 0)
				return null;
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			if (obj instanceof GlobalStatevarItem || obj instanceof RebecStatevarItem
					|| obj instanceof QueueContent || obj instanceof PropertyItem)
				return null;
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class NameSorter extends ViewerSorter {

		ItemComparator itemComparator = new ItemComparator();

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			return itemComparator.compare((Item) e1, (Item) e2);
		}

	}

	/**
	 * The constructor.
	 */
	public SCCounterExampleView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		Tree tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		// GridData gd = new GridData(GridData.FILL_BOTH);
		// gd.verticalSpan = 1;
		// table.setLayoutData(gd);
		// init state
		TreeColumn[] cols = new TreeColumn[2];
		cols[0] = new TreeColumn(tree, SWT.LEFT);
		cols[0].setText("Name");
		cols[0].setWidth(200);

		cols[1] = new TreeColumn(tree, SWT.LEFT);
		cols[1].setText("Value");
		cols[1].setWidth(200);

		viewer = new TreeViewer(tree);
		viewer.setContentProvider(viewContentProvider);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		makeActions();
		hookContextMenu();
		contributeToActionBars();
		this.viewer.expandAll();
		checkState();
	}

	private ViewContentProvider viewContentProvider = new ViewContentProvider();

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SCCounterExampleView.this.fillContextMenu(manager);
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
		manager.add(actionLoad);
		manager.add(actionGotoState);
		manager.add(actionPrev);
		manager.add(actionNext);
		manager.add(actionFirst);
		manager.add(actionLast);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionLoad);
		manager.add(actionGotoState);
		manager.add(actionFirst);
		manager.add(actionPrev);
		manager.add(actionNext);
		manager.add(actionLast);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionLoad);
		manager.add(actionGotoState);
		manager.add(actionFirst);
		manager.add(actionPrev);
		manager.add(actionNext);
		manager.add(actionLast);
	}

	private void prevState() {
		viewContentProvider.counterExample.goToPrevSystemStateItem();
		checkState();
		this.viewer.refresh();
		this.viewer.expandAll();
	}

	class StateNumberValidator implements IInputValidator {

		public String isValid(String newText) {
			try {
				int s = Integer.parseInt(newText);
				if (!viewContentProvider.counterExample.isValidStateNumber(s)) {
					return "Invalid state ID";
				}
			}
			catch (NumberFormatException e) {
				return e.getMessage();
			}
			return null;
		}

	}

	private void gotoState() {
		InputDialog inputDialog = new InputDialog(this.getSite().getShell(), "Input for state ID",
				"Enter the state ID: ", "0", new StateNumberValidator());
		inputDialog.open();
		int retCode = inputDialog.getReturnCode();

		if (retCode == Window.OK) {
			int stateId = Integer.parseInt(inputDialog.getValue());

			viewContentProvider.counterExample.gotoStateId(stateId);
			checkState();
			this.viewer.refresh();
			this.viewer.expandAll();
		}
	}

	private void loadResult() {
		FileDialog fileDialog = new FileDialog(this.getSite().getShell());
		fileDialog.setFilterExtensions(new String[] { "*.xml" });
		String selectedFile = fileDialog.open();
		if (selectedFile != null) {
			File resultFile = new File(selectedFile);
			CounterExample counterExample = null;
			JAXBContext jaxbContext;
			try {
				jaxbContext = JAXBContext.newInstance("org.rebecalang.afra.plugin.internal.view.counterexample");
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				ModelCheckingReport report = (ModelCheckingReport) unmarshaller
						.unmarshal(resultFile);
				counterExample = new CounterExample("", report);
				counterExample.parse();
				this.setCounterExample(null);
				this.checkRefreshAll();
				this.setCounterExample(counterExample);
				this.checkRefreshAll();
			}
			catch (JAXBException e) {
				logger.throwing(this.getClass().getName(), "run", e);
				e.printStackTrace();
			}
			checkState();
			this.viewer.refresh();
			this.viewer.expandAll();
		}
	}

	private void nextState() {
		viewContentProvider.counterExample.goToNextSystemStateItem();
		checkState();
		this.viewer.refresh();
		this.viewer.expandAll();
	}

	public void checkRefreshAll() {
		checkState();
		this.viewer.refresh();
		this.viewer.expandAll();
	}

	private void checkState() {
		if (viewContentProvider.counterExample != null) {
			if (viewContentProvider.counterExample.hasNextState()) {
				actionNext.setEnabled(true);
				actionLast.setEnabled(true);
			}
			else {
				actionNext.setEnabled(false);
				actionLast.setEnabled(false);
			}
			if (viewContentProvider.counterExample.hasPrevState()) {
				actionPrev.setEnabled(true);
				actionFirst.setEnabled(true);
			}
			else {
				actionPrev.setEnabled(false);
				actionFirst.setEnabled(false);
			}
			if (!viewContentProvider.counterExample.hasPrevState()
					&& !viewContentProvider.counterExample.hasNextState()) {
				actionGotoState.setEnabled(false);
			}
			else {
				actionGotoState.setEnabled(true);
			}
		}
		else {
			actionGotoState.setEnabled(false);
			actionNext.setEnabled(false);
			actionPrev.setEnabled(false);
			actionLast.setEnabled(false);
			actionFirst.setEnabled(false);
		}
	}

	private void makeActions() {
		actionGotoState = new Action() {
			public void run() {
				gotoState();
			}
		};
		actionGotoState.setText("Go to State");
		actionGotoState.setToolTipText("Go to State");
		String iconPath = "/icons/goto.gif";
		ImageDescriptor gotoImageDescriptor = RebecaUIPlugin.getImageDescriptor(iconPath);
		actionGotoState.setImageDescriptor(gotoImageDescriptor);
		// ///////////////////////////
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
		iconPath = "/icons/last.gif";
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
		iconPath = "/icons/first.gif";
		ImageDescriptor imageDescriptorFirst = RebecaUIPlugin.getImageDescriptor(iconPath);
		actionFirst.setImageDescriptor(imageDescriptorFirst);
		// ///////////////////////////
		actionLoad = new Action() {
			public void run() {
				loadResult();
			}
		};
		actionLoad.setText("Load Result File");
		actionLoad.setToolTipText("Load Result File");
		actionLoad.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
	}

	private void firstState() {
		viewContentProvider.counterExample.goToFirstSystemStateItem();
		checkState();
		this.viewer.refresh();
		this.viewer.expandAll();
	}

	private void lastState() {
		viewContentProvider.counterExample.goToLastSystemStateItem();
		checkState();
		this.viewer.refresh();
		this.viewer.expandAll();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
