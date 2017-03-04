package org.rebecalang.afra.ideplugin.view.counterexample.systemc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.rebecalang.afra.ideplugin.modelcheckreport.GlobalVars;
import org.rebecalang.afra.ideplugin.modelcheckreport.ModelCheckingReport;
import org.rebecalang.afra.ideplugin.modelcheckreport.Option;
import org.rebecalang.afra.ideplugin.modelcheckreport.StateVar;
import org.rebecalang.afra.ideplugin.modelcheckreport.SystemState;
import org.rebecalang.afra.ideplugin.modelcheckreport.Value;
import org.rebecalang.afra.ideplugin.view.counterexample.GlobalStatevarItem;
import org.rebecalang.afra.ideplugin.view.counterexample.GlobalVariablesItem;
import org.rebecalang.afra.ideplugin.view.counterexample.IdComparator;
import org.rebecalang.afra.ideplugin.view.counterexample.InfoItem;
import org.rebecalang.afra.ideplugin.view.counterexample.PropertyItem;
import org.rebecalang.afra.ideplugin.view.counterexample.SystemStateItem;

public class CounterExample {
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
		InfoItem infoItem = new InfoItem("Model Checking Information");
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
		systemStateItems.add(systemStateItem);		
		
		infoItem = new InfoItem("Checked Property");
		infoItem.addInfoItem(new PropertyItem("Type", modelCheckingReport.getCheckedProperty().getType(), infoItem));
		infoItem.addInfoItem(new PropertyItem("Name", modelCheckingReport.getCheckedProperty().getName(), infoItem));
		infoItem.addInfoItem(new PropertyItem("Algorithm", modelCheckingReport.getCheckedProperty().getAlgorithm(), infoItem));
		infoItem.addInfoItem(new PropertyItem("Result", modelCheckingReport.getCheckedProperty().getResult(), infoItem));
		systemStateItems.add(systemStateItem);		
	}
	
	public void parse() {
		this.maxDepth = toInt(modelCheckingReport.getSystemInfo().getMaxDepth());
		this.maxReachedState = toInt(modelCheckingReport.getSystemInfo().getReachedStates());
		String result = modelCheckingReport.getCheckedProperty().getResult();
		if (result.equals("satisfied")) {
			addBasicInfo();
			/*SystemStateItem systemStateItem = new SystemStateItem();
			InfoItem infoItem = new InfoItem("System Information");
			infoItem.addInfoItem(new PropertyItem("result", modelCheckingReport
					.getCheckedProperty().getResult(), infoItem));
			infoItem.addInfoItem(new PropertyItem(modelCheckingReport.getCheckedProperty()
					.getName(), modelCheckingReport.getCheckedProperty().getType(), infoItem));
			infoItem.addInfoItem(new PropertyItem("Total time spent", modelCheckingReport
					.getSystemInfo().getTotalSpentTime(), infoItem));
			infoItem.addInfoItem(new PropertyItem("Reached Depth", modelCheckingReport
					.getSystemInfo().getMaxDepth(), infoItem));
			infoItem.addInfoItem(new PropertyItem("Reached States", modelCheckingReport
					.getSystemInfo().getReachedStates(), infoItem));
			infoItem.addInfoItem(new PropertyItem("Model Name", modelName, infoItem));
			systemStateItem.addInfoItem(infoItem);
			systemStateItems.add(systemStateItem);*/
		}
		else if (result.equals("not satisfied")) {
			addBasicInfo();
			/*SystemStateItem systemStateItem = new SystemStateItem();
			InfoItem infoItem = new InfoItem("System Information");
			infoItem.addInfoItem(new PropertyItem("result", modelCheckingReport
					.getCheckedProperty().getResult(), infoItem));
			infoItem.addInfoItem(new PropertyItem(modelCheckingReport.getCheckedProperty()
					.getName(), modelCheckingReport.getCheckedProperty().getType(), infoItem));
			infoItem.addInfoItem(new PropertyItem("Total time spent", modelCheckingReport
					.getSystemInfo().getTotalSpentTime(), infoItem));
			infoItem.addInfoItem(new PropertyItem("Max Reached Depth", modelCheckingReport
					.getSystemInfo().getMaxDepth(), infoItem));
			infoItem.addInfoItem(new PropertyItem("Max Reached State", modelCheckingReport
					.getSystemInfo().getReachedStates(), infoItem));
			infoItem.addInfoItem(new PropertyItem("Model Name", modelName, infoItem));
			systemStateItem.addInfoItem(infoItem);
			systemStateItems.add(systemStateItem);*/
		}
		else {
			List<SystemState> syssl = modelCheckingReport.getCounterExampleTrace().getSystemState();
			Iterator<SystemState> iterator = syssl.iterator();
			boolean synchTurn = false;
			while (iterator.hasNext()) {
				SystemState element = iterator.next();
				if (synchTurn && iterator.hasNext()) {
					if (element.getExecutingRebec().getName().equals("synchronizer")) {
						synchTurn = true;
						continue;
					}
					else {
						synchTurn = false;
					}
				}
				SystemStateItem systemStateItem = new SystemStateItem();
				systemStateItem.setId(Integer.parseInt(element.getId()));
				InfoItem infoItem = new InfoItem("System Information");
				infoItem.addInfoItem(new PropertyItem("result", modelCheckingReport
						.getCheckedProperty().getResult(), infoItem));
				infoItem.addInfoItem((new PropertyItem(modelCheckingReport.getCheckedProperty()
						.getName(), modelCheckingReport.getCheckedProperty().getType(), infoItem)));
				infoItem.addInfoItem(new PropertyItem("state id", element.getId(), infoItem));
				infoItem.addInfoItem(new PropertyItem("Max Reached Depth", modelCheckingReport
						.getSystemInfo().getMaxDepth(), infoItem));
				infoItem.addInfoItem(new PropertyItem("Max Reached State", modelCheckingReport
						.getSystemInfo().getReachedStates(), infoItem));
				infoItem.addInfoItem(new PropertyItem("Model Name", modelName, infoItem));

				if (element.getExecutingRebec().getName().equals("synchronizer")) {
					synchTurn = true;
					infoItem.addInfoItem(new PropertyItem("next process to execute",
							"input change", infoItem));
				}
				else {
					synchTurn = false;
					System.out.println("element.getExecutingRebec().getType(): "
							+ element.getExecutingRebec().getType());
					ModuleProcessName mpname = getModuleProcessName(element.getExecutingRebec()
							.getName(), element.getExecutingRebec().getType());

					infoItem.addInfoItem(new PropertyItem("next module to execute",
							mpname.moduleName, infoItem));
					infoItem.addInfoItem(new PropertyItem("next process to execute",
							mpname.processName, infoItem));
				}
				systemStateItem.addInfoItem(infoItem);
//				systemStateItem.setGlobalVariablesItem(processGlobalVariables(element));
				systemStateItems.add(systemStateItem);
			}
		}
		systemStateItemsSorted = new ArrayList<SystemStateItem>(systemStateItems);
		Collections.sort(systemStateItemsSorted, new IdComparator());
	}

	class ModuleProcessName {
		String moduleName;

		String processName;
	}

	private ModuleProcessName getModuleProcessName(String rebecaMName, String rebecType) {
		ModuleProcessName moduleProcessName = new ModuleProcessName();
		rebecaMName = rebecaMName.substring(rebecType.length());
		int first_Index = rebecaMName.indexOf("_");
		int first__Index = rebecaMName.indexOf("__");
		String processName = rebecaMName.substring(first_Index + 1, first__Index);
		int last_Index = rebecaMName.lastIndexOf("_");
		String modulesNames = rebecaMName.substring(first__Index + 2, last_Index);

		String scModuleName = "";
		first__Index = modulesNames.indexOf("__");
		while (first__Index != -1) {
			scModuleName += modulesNames.substring(0, first__Index) + ".";
			modulesNames = modulesNames.substring(first__Index + 2);
			first__Index = modulesNames.indexOf("__");
		}
		scModuleName += modulesNames;

		moduleProcessName.moduleName = scModuleName;
		System.out.println("scModuleName: " + scModuleName);
		moduleProcessName.processName = processName;
		System.out.println("processName: " + processName);
		return moduleProcessName;
	}

	private GlobalVariablesItem processGlobalVariables(SystemState element) {
		GlobalVars globalVars = (GlobalVars) element.getGlobalVars();
		List<StateVar> gvars = globalVars.getStateVar();
		GlobalVariablesItem globalVariablesItem = new GlobalVariablesItem();
		for (StateVar v : gvars) {
			if (v.getName().startsWith("main_") && !v.getName().endsWith("_Post")) {
				globalVariablesItem.addVariable(new GlobalStatevarItem(v.getName().substring(5),
						valueToString(v.getValue()), globalVariablesItem));
			}
		}
		return globalVariablesItem;
	}

	private String valueToString(List<Value> value) {
		StringBuffer stringBuffer = new StringBuffer();
		for (Iterator<Value> iter = value.iterator(); iter.hasNext();) {
			String element = iter.next().getvalue();
			element.trim();
			element = element.replaceAll("\n", "");
			stringBuffer.append(element).append(";");
		}
		return stringBuffer.toString();
	}

	public CounterExample(ModelCheckingReport report) {
		this("", report);
	}

	private String modelName = "";

	public CounterExample(String modelName, ModelCheckingReport report) {
		super();
		this.modelCheckingReport = report;
		this.modelName = modelName;
	}

	public static void main(String[] args) {
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance("org.rebecalang.afra.plugin.internal.view.counterexample");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			ModelCheckingReport report = (ModelCheckingReport) unmarshaller.unmarshal(new File(
					"/home/amshali/tmp/ce.xml"));
			CounterExample counterExample = new CounterExample(report);
			counterExample.parse();
			System.out.println(counterExample);

		}
		catch (JAXBException e) {
			e.printStackTrace();
		}
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
