package analyzer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import model.FunctionReference;
import model.RequireJsModule;
import reporting.FunctionCallReport;

public class FunctionCallAnalyzer {
	private Set<String> analyzedModules;
	
	public FunctionCallAnalyzer() {
		this.analyzedModules = new HashSet<String>();
	}
	
	public List<FunctionCallReport> getFunctionCallReports(RequireJsModule module) {
		List<FunctionCallReport> reports = new LinkedList<FunctionCallReport>();
		
		if(!this.analyzedModules.contains(module.getId())) {
			this.analyzedModules.add(module.getId());
			
			reports.add(this.getFunctionCallReport(module));
			for (RequireJsModule dependency : module.getDependencies()) {
				reports.addAll(this.getFunctionCallReports(dependency));
			}
		}
		
		return reports;
	}
	
	private FunctionCallReport getFunctionCallReport(RequireJsModule module) {
		FunctionCallReport report = new FunctionCallReport(module.getId());
		
		Set<String> knownNames = new HashSet<String>(module.getNamedDependencies().keySet()); 
		knownNames.add("require");
		knownNames.add("exports");
		knownNames.add("module");
		knownNames.add("window");
		knownNames.add("console");
		knownNames.add("$");
		knownNames.add("jQuery");
		
		Set<String> definitions = module.getVariableDefinitions().keySet();
		
		for(FunctionReference ref : module.getFunctionCalls()) {
			if(knownNames.contains(ref.getObjectName())) {
				RequireJsModule mod = module.getNamedDependencies().get(ref);
				if(mod != null && mod.getId().toLowerCase().equals(ref.getObjectName().toLowerCase())) {
					report.numRequireFunctionCalls++;
				} else {
					report.numRequireWrongFunctionCalls++;
				}
			} else {
				if(definitions.contains(ref.getObjectName())) {
					report.numLocalFunctionCalls++;
				} else {
					report.numUnknownFunctionCalls++;
				}
			}
		}
		
		return report;
	}
}
