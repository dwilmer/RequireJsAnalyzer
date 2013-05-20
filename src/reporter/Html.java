package reporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import model.FunctionReference;
import model.RequireJsModule;
import requirejsExtractor.Extractor;

public class Html {
	private Map<String, RequireJsModule> modules;
	
	public static void main(String[] args) {
		Extractor x = new Extractor("brackets/");
		RequireJsModule tree = x.extractModules("brackets");
		
		Html exporter = new Html();
		exporter.gatherModules(tree);
		exporter.exportModuleDetails();
	}
	
	public Html() {
		this.modules = new HashMap<String, RequireJsModule>();
	}
	
	public void gatherModules(RequireJsModule module) {
		RequireJsModule stored = this.modules.get(module.getId());
		if(stored == null || stored.getDependencies().size() < module.getDependencies().size()) {
			this.modules.put(module.getId(), module);
		}
		for(RequireJsModule dependency : module.getDependencies()) {
			gatherModules(dependency);
		}
	}
	
	private void exportModuleDetails() {
		String filename = "report/report.html";
		int width = 200;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			out.write("<!doctype html>");
			out.write("<html><head><title>Brackets analyzability</title><style type=\"text/css\"></style></head><body><h1>Brackets</h1>");
			out.write("<table border=0><tr><th>Module</th><th>Function call tracability</th></tr>");
			
			Object[] modules = this.modules.values().toArray();
			Arrays.sort(modules);
			
			int totalModules = 0;
			int totalCorrectModules = 0;
			FunctionCallReport functionTotals = new FunctionCallReport();
			for(Object moduleO : modules) {
				RequireJsModule module = (RequireJsModule) moduleO;
				if(module.getId().indexOf("thirdparty") == 0) {
					continue;
				}
				int numModules = module.getNamedDependencies().size();
				int numCorrectModules = getNumCorrectImports(module);
				totalModules += numModules;
				totalCorrectModules += numCorrectModules;
				
				FunctionCallReport callReport = investigateFunctionCalls(module);
				functionTotals.add(callReport);
				
				out.write("<tr><td>");
				out.write(module.getId());
				out.write("</td>");
				
				//printBar(out, numCorrectModules, numModules, numModules, width);
				//printBar(out, callReport.getNumLocalFunctionCalls(), callReport.getNumLocalFunctionCalls() + callReport.numRequireFunctionCalls, callReport.getTotal(), width);
				//printBar(out, callReport.getNumLocalFunctionCallsUnder(40), callReport.getNumLocalFunctionCallsUnder(120), callReport.getNumLocalFunctionCalls(), width);
				int green = callReport.getNumLocalFunctionCalls() + callReport.numRequireFunctionCalls;
				int yellow = green + callReport.numRequireWrongFunctionCalls;
				int total = yellow + callReport.numUnknownFunctionCalls;
				printBar(out, green, yellow, total, width);
				
				out.write("</tr>");
			}
			
			out.write("<tr><td><strong>Total</strong></td>");
//			printBar(out, totalCorrectModules, totalCorrectModules, totalModules, width);
//			printBar(out, functionTotals.getNumLocalFunctionCalls(), functionTotals.getNumLocalFunctionCalls() + functionTotals.numRequireFunctionCalls, functionTotals.getTotal(), width);
//			printBar(out, functionTotals.getNumLocalFunctionCallsUnder(40), functionTotals.getNumLocalFunctionCallsUnder(120), functionTotals.getNumLocalFunctionCalls(), width);
			int green = functionTotals.getNumLocalFunctionCalls() + functionTotals.numRequireFunctionCalls;
			int yellow = green + functionTotals.numRequireWrongFunctionCalls;
			int total = yellow + functionTotals.numUnknownFunctionCalls;
			printBar(out, green, yellow, total, width);
			out.write("</tr></table></body></html>");
			out.flush();
			out.close();
		} catch (IOException iox) {
			iox.printStackTrace();
		}
	}

	public void printBar(BufferedWriter out, int lower, int upper, int total, int barWidth)	throws IOException {
		int greenWidth = barWidth;
		int yellowWidth = 0;
		if(total > 0) {
			greenWidth = (barWidth * lower) / total;
			yellowWidth = (barWidth * upper) / total - greenWidth;
		}
		out.write("<td><img src=\"green.gif\" height=10 width=" + greenWidth + ">");
		out.write("<img src=\"yellow.gif\" height=10 width=" + (yellowWidth) + ">");
		out.write("<img src=\"red.gif\" height=10 width=" + ((barWidth - yellowWidth) - greenWidth) + ">");
		out.write(lower + "/" + upper + "/" + total);
		out.write("</td>");
	}

	private int getNumCorrectImports(RequireJsModule module) {
		int numCorrectModules = 0;
		for(Entry<String, RequireJsModule> entry : module.getNamedDependencies().entrySet()) {
			String moduleId = entry.getValue().getId();
			String correctName = moduleId.substring(moduleId.lastIndexOf('/') + 1).toLowerCase();
			if(entry.getKey().toLowerCase().equals(correctName)) {
				numCorrectModules++;
			}
		}
		return numCorrectModules;
	}
	
	private FunctionCallReport investigateFunctionCalls(RequireJsModule module) {
		FunctionCallReport report = new FunctionCallReport();
		
		Set<String> knownNames = new HashSet<String>(module.getNamedDependencies().keySet()); 
		knownNames.add("require");
		knownNames.add("exports");
		knownNames.add("module");
		knownNames.add("window");
		knownNames.add("console");
		knownNames.add("$");
		knownNames.add("jQuery");
		
		Map<String, List<Integer>> definitions = module.getVariableDefinitions();
		
		for(FunctionReference ref : module.getFunctionCalls()) {
			if(knownNames.contains(ref.getObjectName())) {
				RequireJsModule mod = module.getNamedDependencies().get(ref);
				if(mod != null && mod.getId().toLowerCase().equals(ref.getObjectName().toLowerCase())) {
					report.numRequireFunctionCalls++;
				} else {
					report.numRequireWrongFunctionCalls++;
				}
			} else {
				List<Integer> defined = definitions.get(ref.getObjectName());
				if(defined != null) {
					int listIndex;
					for(listIndex = 0; listIndex < defined.size() && defined.get(listIndex) < ref.getLineNumber(); listIndex++);
					listIndex--;
					if(listIndex < 0) {
						report.numUnknownFunctionCalls++;
					} else {
						report.localFunctionCallDistances.add(ref.getLineNumber() - defined.get(listIndex));
					}
				} else {
					report.numUnknownFunctionCalls++;
				}
			}
		}
		
		return report;
	}
}
