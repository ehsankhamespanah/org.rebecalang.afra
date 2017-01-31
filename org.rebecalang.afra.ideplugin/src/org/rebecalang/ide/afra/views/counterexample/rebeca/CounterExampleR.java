package org.rebecalang.ide.afra.views.counterexample.rebeca;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.rebecalang.ide.afra.modelcheckreport.GlobalVars;
import org.rebecalang.ide.afra.modelcheckreport.Message;
import org.rebecalang.ide.afra.modelcheckreport.ModelCheckingReport;
import org.rebecalang.ide.afra.modelcheckreport.Option;
import org.rebecalang.ide.afra.modelcheckreport.Rebec;
import org.rebecalang.ide.afra.modelcheckreport.StateVar;
import org.rebecalang.ide.afra.modelcheckreport.SystemState;
import org.rebecalang.ide.afra.modelcheckreport.Value;
import org.rebecalang.ide.afra.views.counterexample.GlobalStatevarItem;
import org.rebecalang.ide.afra.views.counterexample.GlobalVariablesItem;
import org.rebecalang.ide.afra.views.counterexample.IdComparator;
import org.rebecalang.ide.afra.views.counterexample.InfoItem;
import org.rebecalang.ide.afra.views.counterexample.MsgsrvQueue;
import org.rebecalang.ide.afra.views.counterexample.PropertyItem;
import org.rebecalang.ide.afra.views.counterexample.QueueContent;
import org.rebecalang.ide.afra.views.counterexample.RebecItem;
import org.rebecalang.ide.afra.views.counterexample.RebecStatevarItem;
import org.rebecalang.ide.afra.views.counterexample.SystemStateItem;

public class CounterExampleR {
	private int maxDepth;

	private int maxReachedState;

	private List<SystemStateItem> systemStateItems = new ArrayList<SystemStateItem>();

	private List<SystemStateItem> systemStateItemsSorted;

	public int getMaxDepth() {
		return maxDepth;
	}

	private int currentSystemStateItemIndx = 0;

	public SystemStateItem getCurrentSystemStateItem() {
		if (currentSystemStateItemIndx <= -1) {
			currentSystemStateItemIndx = 0;
		}
		return systemStateItems.get(currentSystemStateItemIndx);
	}

	/**
	 * 
	 * @param stateNumber
	 * @return Returns false if the stateNumber is invalid
	 */
	public boolean gotoStateId(int stateId) {
		if (isValidStateNumber(stateId)) {
			int i = 0;
			for (Iterator<SystemStateItem> iter = systemStateItems.iterator(); iter.hasNext();) {
				SystemStateItem element = iter.next();
				if (element.getId() == stateId) {
					currentSystemStateItemIndx = i;
					return true;
				}
				i++;
			}
		}
		return false;
	}

	private SystemStateItem findStateById(int id) {
		SystemStateItem s = new SystemStateItem();
		s.setId(id);
		int indx = Collections.binarySearch(systemStateItemsSorted, s, new IdComparator());
		return (indx < 0) ? null : systemStateItemsSorted.get(indx);
	}

	public boolean isValidStateNumber(int stateId) {
		if (findStateById(stateId) != null) {
			return true;
		}
		return false;
	}

	public void goToNextSystemStateItem() {
		if (currentSystemStateItemIndx < systemStateItems.size() - 1) {
			currentSystemStateItemIndx++;
		}
	}

	public void goToPrevSystemStateItem() {
		if (currentSystemStateItemIndx > 0) {
			currentSystemStateItemIndx--;
		}
	}

	public boolean hasNextState() {
		if (currentSystemStateItemIndx < systemStateItems.size() - 1) {
			return true;
		}
		return false;
	}

	public boolean hasPrevState() {
		if (currentSystemStateItemIndx > 0) {
			return true;
		}
		return false;
	}

	public List<SystemStateItem> getSystemStateItems() {
		return systemStateItems;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public int getMaxReachedState() {
		return maxReachedState;
	}

	public void setMaxReachedState(int maxReachedState) {
		this.maxReachedState = maxReachedState;
	}

	private ModelCheckingReport modelCheckingReport;

	public static int toInt(String s) {
		return Integer.parseInt(s);
	}

	private void addBasicInfo() {
		SystemStateItem systemStateItem = new SystemStateItem();
		InfoItem infoItem = new InfoItem("Checked Property");
		infoItem.addInfoItem(new PropertyItem("Type", modelCheckingReport.getCheckedProperty().getType(), infoItem));
		infoItem.addInfoItem(new PropertyItem("Name", modelCheckingReport.getCheckedProperty().getName(), infoItem));
		infoItem.addInfoItem(new PropertyItem("Algorithm", modelCheckingReport.getCheckedProperty().getAlgorithm(), infoItem));
		infoItem.addInfoItem(new PropertyItem("Result", modelCheckingReport.getCheckedProperty().getResult(), infoItem));
		systemStateItem.addInfoItem(infoItem);

		infoItem = new InfoItem("Model Checking Information");
		infoItem.addInfoItem(new PropertyItem("Total Spent Time", modelCheckingReport.getSystemInfo().getTotalSpentTime(), infoItem));
		infoItem.addInfoItem(new PropertyItem("Reached States", modelCheckingReport.getSystemInfo().getReachedStates(), infoItem));
		infoItem.addInfoItem(new PropertyItem("Reached Transitions", modelCheckingReport.getSystemInfo().getReachedTransitions(), infoItem));
		infoItem.addInfoItem(new PropertyItem("Hashtable Size", modelCheckingReport.getSystemInfo().getHashtableSize(), infoItem));
		infoItem.addInfoItem(new PropertyItem("Consumed Memory", modelCheckingReport.getSystemInfo().getConsumedMem(), infoItem));
		String options = "";
		for (Option option : modelCheckingReport.getCheckedProperty().getOptions().getOption()) {
			options += option.getvalue() + ", ";
		}
		infoItem.addInfoItem(new PropertyItem("Options", options.substring(0, options.length() - 1), infoItem));
		systemStateItem.addInfoItem(infoItem);		
		systemStateItem.setId(-2);
		
		systemStateItems.add(systemStateItem);
	}
		
	public void parse() {
		this.maxDepth = toInt((modelCheckingReport.getSystemInfo().getMaxDepth()!= null) ? modelCheckingReport.getSystemInfo().getMaxDepth() : "0" );
		this.maxReachedState = toInt(modelCheckingReport.getSystemInfo().getReachedStates());
		String result = modelCheckingReport.getCheckedProperty().getResult();
		
		if (result.equals("satisfied") || result.equals("not satisfied")) {
			addBasicInfo();
		}
		else {
			//parsing CouterExampleTrace
			addBasicInfo();
			if (modelCheckingReport.getCounterExampleTrace() != null) {
				List<SystemState> syssl = modelCheckingReport.getCounterExampleTrace().getSystemState();
				for (SystemState element : syssl) {
					SystemStateItem systemStateItem = new SystemStateItem();
					systemStateItem.setId(Integer.parseInt(element.getId()));
					InfoItem infoItem = new InfoItem("State Information");
					infoItem.addInfoItem(new PropertyItem("system.id", element.getId(), infoItem));
					infoItem.addInfoItem(new PropertyItem("system.next_state.id", element
							.getNextSystemState().replaceAll("\n", ""), infoItem));
					infoItem.addInfoItem(new PropertyItem("system.rebec_to_execute", element
							.getExecutingRebec().getName(), infoItem));
					systemStateItem.addInfoItem(infoItem);
					List<Rebec> rebecs = element.getRebec();
					for (Iterator<Rebec> iterator = rebecs.iterator(); iterator.hasNext();) {
						Rebec r = (Rebec) iterator.next();
						RebecItem rItem = processRebec(r);
						systemStateItem.addRebecItem(rItem);
					}
					systemStateItems.add(systemStateItem);
				}
			}
		}
		systemStateItemsSorted = new ArrayList<SystemStateItem>(systemStateItems);
		Collections.sort(systemStateItemsSorted, new IdComparator());
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
		StringBuilder senderContent = new StringBuilder();
		StringBuilder msgContent = new StringBuilder();
		for (Iterator<Message> iter = messages.iterator(); iter.hasNext();) {
			Message m = iter.next();
			senderContent.append(m.getSender()).append(";");
			msgContent.append(m.getName()).append(";");
		}
		msgsrvQueue.setSenderQueue(new QueueContent("sender", senderContent.toString().replaceAll(
				"\n", ""), msgsrvQueue));
		msgsrvQueue.setMessageQueue(new QueueContent("message", msgContent.toString().replaceAll(
				"\n", ""), msgsrvQueue));
		rebecItem.setMsgsrvQueue(msgsrvQueue);
		return rebecItem;
	}

	public GlobalVariablesItem processGlobalVariables(SystemState element) {
		GlobalVars globalVars = (GlobalVars) element.getGlobalVars();
		GlobalVariablesItem globalVariablesItem = new GlobalVariablesItem();
		if (globalVars != null) {
			List<StateVar> gvars = globalVars.getStateVar();
			for (Iterator<StateVar> iter = gvars.iterator(); iter.hasNext();) {
				StateVar v = iter.next();
				globalVariablesItem.addVariable(new GlobalStatevarItem(v.getName(), valueToString(v
						.getValue()), globalVariablesItem));
			}
		}
		return globalVariablesItem;
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

	public CounterExampleR(ModelCheckingReport report) {
		this("", report);
	}

	public CounterExampleR(String modelName, ModelCheckingReport report) {
		super();
		this.modelCheckingReport = report;
	}


	@Override
	public String toString() {
		return maxDepth + "; " + maxReachedState;
	}

	public void goToFirstSystemStateItem() {
		currentSystemStateItemIndx = 0;
	}

	public void goToLastSystemStateItem() {
		currentSystemStateItemIndx = systemStateItems.size() - 1;
	}

}
