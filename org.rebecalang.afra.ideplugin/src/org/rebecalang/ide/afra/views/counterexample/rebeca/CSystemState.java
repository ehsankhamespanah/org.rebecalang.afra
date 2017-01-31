package org.rebecalang.ide.afra.views.counterexample.rebeca;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rebecalang.ide.afra.modelcheckreport.Message;
import org.rebecalang.ide.afra.modelcheckreport.Rebec;
import org.rebecalang.ide.afra.modelcheckreport.StateVar;
import org.rebecalang.ide.afra.modelcheckreport.SystemState;
import org.rebecalang.ide.afra.modelcheckreport.Value;
import org.rebecalang.ide.afra.views.counterexample.MsgsrvQueue;
import org.rebecalang.ide.afra.views.counterexample.QueueContent;
import org.rebecalang.ide.afra.views.counterexample.RebecItem;
import org.rebecalang.ide.afra.views.counterexample.RebecStatevarItem;
import org.rebecalang.ide.afra.views.counterexample.StatevarItem;
import org.rebecalang.ide.afra.views.counterexample.SystemStateItem;

public class CSystemState {
	private int Depth;
	
	private List<SystemStateItem> systemStateItems = new ArrayList<SystemStateItem>();
	
	private int currentSystemStateItemIndex = 0;

	public int getMaxDepth() {
		return Depth;
	}
	
	public void goToNextSystemStateItem() {
		if (currentSystemStateItemIndex < systemStateItems.size() - 1) {
			currentSystemStateItemIndex++;
		}
	}

	public void goToPrevSystemStateItem() {
		if (currentSystemStateItemIndex > 0) {
			currentSystemStateItemIndex--;
		}
	}
	
	public void goToFirstSystemStateItem() {
		currentSystemStateItemIndex = 0;
	}

	public void goToLastSystemStateItem() {
		currentSystemStateItemIndex = systemStateItems.size() - 1;
	}

	public SystemStateItem getCurrentSystemStateItem() {
		if(systemStateItems.size()== 0) return new SystemStateItem();
		if (currentSystemStateItemIndex <= -1) {
			currentSystemStateItemIndex = 0;
		}
		return systemStateItems.get(currentSystemStateItemIndex);
	}

	public void setMaxDepth(int maxDepth) {
		this.Depth = maxDepth;
	}

	public static int toInt(String s) {
		return Integer.parseInt(s);
	}

	public RebecItem processRebec(Rebec r) {
		RebecItem rebecItem = new RebecItem(r.getName(), r.getReactiveClass());
		List<StateVar> vars = r.getStateVars().getStateVar();
		for (Iterator<StateVar> iter = vars.iterator(); iter.hasNext();) {
			StateVar v = iter.next();
			RebecStatevarItem rsv = new RebecStatevarItem(v.getName(), valueToString(v.getValue()),
					rebecItem);
			rebecItem.addStatevar(rsv);
		}
		List<Message> messages = r.getQueue().getMessage();
		MsgsrvQueue msgsrvQueue = new MsgsrvQueue(rebecItem);
		StringBuilder msgContent = new StringBuilder();
		StringBuilder senderContent = new StringBuilder();
		senderContent.append("");
		for (Iterator<Message> iter = messages.iterator(); iter.hasNext();) {
			Message m = iter.next();
			msgContent.append(m.getName()).append(":").append(m.getSender()).append(";");
		}
		msgsrvQueue.setSenderQueue(new QueueContent("", senderContent.toString().replaceAll(
				"\n", ""), msgsrvQueue));
		msgsrvQueue.setMessageQueue(new QueueContent("message:sender", msgContent.toString().replaceAll(
				"\n", ""), msgsrvQueue));
		rebecItem.setMsgsrvQueue(msgsrvQueue);
		return rebecItem;
	}

	public String valueToString(List<Value> value) {
		StringBuffer stringBuffer = new StringBuffer();
		for (Iterator<Value> iter = value.iterator(); iter.hasNext();) {
			String element = iter.next().getvalue();
			element.trim();
			element = element.replaceAll("\n", "");
			stringBuffer.append(element).append(";");
		}
		return stringBuffer.toString();
	}

	public CSystemState() {
		super();
	}

	@Override
	public String toString() {
		return Depth + "";
	}

	public void setSystemState(SystemState sysState) {
		
	}

	public void addSystemStateItem(SystemStateItem sysStateItem) {
		systemStateItems.add(sysStateItem);
		goToLastSystemStateItem();
	}

	public boolean hasNextState() {
		if (currentSystemStateItemIndex < systemStateItems.size() - 1) return true;
		return false;
	}

	public boolean hasPrevState() {
		if (currentSystemStateItemIndex > 0) return true;
		return false;
	}

	public boolean hasChanged(Object element) {
		if (element instanceof RebecStatevarItem) {
			RebecStatevarItem r = findPreRebecStatevarItem(
					((RebecStatevarItem) element).getRebec(),
					((RebecStatevarItem) element).getName());
			return !((RebecStatevarItem) element).equal(r);
		}
		else if (element instanceof QueueContent) {
			if(((QueueContent) element).getName().equals("")) 
				return false; //if it's SenderQueue	
			QueueContent q = finPrevQueueContent(
					((QueueContent) element).getMsgsrvQueue().getRebec(),
					((QueueContent) element).getName());
			return !((QueueContent) element).equal(q);
		}
		
		return false;
	}
	
	private QueueContent finPrevQueueContent(RebecItem parent, String name) {
		RebecItem prevRebecItem = findPrevRebecItem(parent.getType(), parent.getName());
		QueueContent a = prevRebecItem.getMsgsrvQueue().getMessageQueue();
		return a;
	}

	private RebecStatevarItem findPreRebecStatevarItem(RebecItem parent, String name)
	{
		RebecItem prevRebecItem = findPrevRebecItem(parent.getType(), parent.getName());
		List<StatevarItem> a = prevRebecItem.getStatevars();
		for (Iterator<StatevarItem> iter = a.iterator(); iter.hasNext();) {
			StatevarItem r = iter.next();
			String n = r.getName();
			if(name.equals(n)) return (RebecStatevarItem) r;
		}
		return null;
	}

	private RebecItem findPrevRebecItem(String type, String name)
	{
		SystemStateItem s = systemStateItems.get(currentSystemStateItemIndex-1);
		List<RebecItem> a = s.getRebecItems();
		for (Iterator<RebecItem> iter = a.iterator(); iter.hasNext();) {
			RebecItem r = iter.next();
			String n = r.getName();
			String t = r.getType();
			if(type.equals(t)&&name.equals(n)) return r;
		}
		return null;
	}

}
