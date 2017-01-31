package org.rebecalang.ide.afra.graphs;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;

import javax.swing.JInternalFrame;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import org.eclipse.swt.widgets.Display;

//import com.mxgraph.layout.mxCircleLayout;
//import com.mxgraph.model.mxCell;
//import com.mxgraph.swing.mxGraphComponent;
//import com.mxgraph.view.mxGraph;

//import com.mxgraph.swing.util.mxMorphing;

//import com.mxgraph.util.mxEvent; 
//import com.mxgraph.util.mxEventSource.mxIEventListener;
//import com.mxgraph.util.mxEventObject;

public class SystemStateGraph extends JInternalFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	//private mxGraph systemState;
	
	private Object parent;
	
	//private mxGraphComponent graphComponent;
	
	//private mxCell[] Rebecs;
	//private mxCell[] ARebecs;
	
	private int NumOfRebecs = 0;
	public int NumOfARebecs = 0;

	public SystemStateGraph()
	{
		super("System State");
		// systemState = new mxGraph();
		// systemState.setAutoSizeCells(true);
		// BasicInternalFrameUI ui = (BasicInternalFrameUI)this.getUI();
		// ui.getNorthPane().setPreferredSize(new Dimension(0,0));
		
		// parent = systemState.getDefaultParent();
		// graphComponent = new mxGraphComponent(systemState);
	}
	
	//public mxCell addRebec(String rebecName, int x)
	//{
	//	return null;
		// mxCell newRebec;
		// systemState.getModel().beginUpdate();
		// try
		// {
		// 	newRebec = (mxCell) systemState.insertVertex(parent, null, rebecName, 0,0,80,30, "shape=ellipse;fontColor=#000000;spacingLeft=4;spacingRight=4;");
		// 	systemState.updateCellSize(newRebec);
		// }
		// finally
		// {
		// 	systemState.getModel().endUpdate();
		// }
		// return newRebec;
	//}
	
	//public void addKnownRebec(mxCell rebec1, mxCell rebec2)
	//{
		// System.out.println("insert edge: "+systemState.getLabel(rebec1)+"+"+systemState.getLabel(rebec2));
		// systemState.getModel().beginUpdate();
		// try
		// {
		// 	systemState.insertEdge(parent, null, "", rebec1, rebec2);
		// }
		// finally
		// {
		// 	systemState.getModel().endUpdate();
		// }
	//}
	
	public void showGraph()
	{
		// getContentPane().add(graphComponent);
		// graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		// {
		
		// 	public void mousePressed(MouseEvent e)
		// 	{
		// 		final Object cell = graphComponent.getCellAt(e.getX(), e.getY());
				
		// 		if (cell != null && isActive(cell))
		// 		{
		// 			Display.getDefault().asyncExec(new Runnable() {
		// 			    public void run() {
		// 			    	/*SimulatorSocket.sendSelectedRebec(Integer.toString(getRebecIndex(cell)));
		// 			    	SimulatorSocket.getState();*/
		// 			    }
		// 			});
		// 		}
		// 	}
		// });
		// layoutGraph();
		// systemState.setCellsLocked(true);
		// this.setSize(600, 320);
		// this.setVisible(true);
	}
	
	protected boolean isActive(Object cell) {
		// for(int i=0; i<NumOfARebecs;i++)
		// {
		// 	if(ARebecs[i].equals(cell)) return true;
		// }
		return false;
	}
	
	protected int getRebecIndex(Object cell)
	{
		// for(int i=0; i<NumOfRebecs;i++)
		// {
		// 	if(Rebecs[i].equals(cell)) return i;
		// }
		return 0;
	}

	public void addKnownRebecs(String knownRebecs)
	{	
		// this.remove(graphComponent);
		// systemState = new mxGraph();
		// systemState.setAutoSizeCells(true);
		
		// parent = systemState.getDefaultParent();
		// graphComponent = new mxGraphComponent(systemState);
		
		// StringTokenizer stk = new StringTokenizer(knownRebecs,"+",false);
  //       NumOfRebecs = stk.countTokens() - 1;
  //       if(NumOfRebecs==0) return;
  //       Rebecs = new mxCell[NumOfRebecs];
  //       for(int i=0; i<NumOfRebecs; i++)
  //       {
  //       	String RebecName = stk.nextToken();
  //       	Rebecs[i] = addRebec(RebecName,i);
  //       	System.out.println("Adding Rebecs: "+RebecName);
  //       }	
  //       String kStr = stk.nextToken();
  //       StringTokenizer stk2 = new StringTokenizer(kStr,"R",false);
  //       for(int i=0; i<NumOfRebecs; i++)
  //       {
  //       	String str = stk2.nextToken();
  //       	StringTokenizer stk3 = new StringTokenizer(str,"-",false);
  //           int NumOfKRebecs = stk3.countTokens();
  //           for(int j=0; j<NumOfKRebecs; j++)
  //           {
  //           	System.out.println("Adding Known Rebecs: "+i+"+"+j);
  //           	addKnownRebec(Rebecs[i], Rebecs[Integer.parseInt(stk3.nextToken())]);
  //           }
  //       }
	}

	public void setActiveRebecs(String rebecs) {
			
		// StringTokenizer stk = new StringTokenizer(rebecs,"-",false);
		
  //       NumOfARebecs = stk.countTokens();
  //       System.out.println(rebecs + "	NumOfARebecs: "+NumOfARebecs);
  //       systemState.getModel().beginUpdate();
		// try
		// {
	 //        systemState.setCellStyle("shape=ellipse;fontColor=#000000;spacingLeft=4;spacingRight=4;",Rebecs);
	        
	 //        ARebecs = new mxCell[NumOfARebecs];
	 //        for(int i=0; i<NumOfARebecs; i++)
	 //        {
	 //        	int j = Integer.parseInt(stk.nextToken());
	 //        	ARebecs[i] = Rebecs[j];
	 //        }
	 //        systemState.setCellStyle("shape=ellipse;fillColor=#6699CC;fontColor=#FFFFFF;spacingLeft=4;spacingRight=4;",ARebecs);
		// }
		// finally
		// {
		// 	systemState.getModel().endUpdate();
		// }
	}
	
	 private void layoutGraph()
	 {
	     // mxCircleLayout layout = new mxCircleLayout(systemState);
	     // Object cell = systemState.getSelectionCell();
	     // if (cell == null || systemState.getModel().getChildCount(cell) == 0)
	     // {
	     //     cell = systemState.getDefaultParent();
	     // }

	     // systemState.getModel().beginUpdate();

	     //     try
	     //     {
	     //         layout.execute(cell);
	     //     } finally
	     //     {
	     //         mxMorphing morph = new mxMorphing(graphComponent,
	     //                 20,
	     //                 1.2,
	     //                 20);
	     //         morph.addListener(mxEvent.DONE,
	     //                 new mxIEventListener()
	     //                 {

	     //                     @Override
	     //                     public void invoke(Object sender,
	     //                             mxEventObject evt)
	     //                     {
	     //                    	 systemState.getModel().endUpdate();
	     //                     }
	     //                 });
	     //         morph.startAnimation();
	     //     }

	 }

}
