package reporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
			out.write("<table border=0><tr><th>Module</th><th>Module/variable naming</th><th>Tracable function calls</th></tr>");
			
			Object[] modules = this.modules.values().toArray();
			Arrays.sort(modules);
			
			int totalModules = 0;
			int totalCorrectModules = 0;
			int totalFunctionCalls = 0;
			int totalTracableFunctionCalls = 0;
			for(Object moduleO : modules) {
				RequireJsModule module = (RequireJsModule) moduleO;
				if(module.getId().indexOf("thirdparty") == 0) {
					continue;
				}
				int numModules = module.getNamedDependencies().size();
				int numCorrectModules = getNumCorrectImports(module);
				totalModules += numModules;
				totalCorrectModules += numCorrectModules;
				
				int importsGreenWidth = width;
				if(numModules > 0) {
					importsGreenWidth = (width * numCorrectModules) / numModules;
				}
				
				int numFunctionCalls = module.getFunctionCalls().size();
				int numTraceableFunctionCalls = getNumCorrectFunctionCalls(module);
				totalFunctionCalls += numFunctionCalls;
				totalTracableFunctionCalls += numTraceableFunctionCalls;
				
				int functionsGreenWidth = width;
				if(numFunctionCalls > 0) {
					functionsGreenWidth = (width * numTraceableFunctionCalls) / numFunctionCalls;
				}
				
				out.write("<tr><td>");
				out.write(module.getId());
				out.write("</td><td><img src=\"green.gif\" height=10 width=" + importsGreenWidth + ">");
				out.write("<img src=\"red.gif\" height=10 width=" + (width - importsGreenWidth) + ">");
				out.write(numCorrectModules + "/" + numModules);
				out.write("</td><td><img src=\"green.gif\" height=10 width=" + functionsGreenWidth + ">");
				out.write("<img src=\"red.gif\" height=10 width=" + (width - functionsGreenWidth) + ">");
				out.write(numTraceableFunctionCalls + "/" + numFunctionCalls);
				out.write("</td></tr>");
			}
			
			int importsGreenWidth = width;
			if(totalModules > 0) {
				importsGreenWidth = (width * totalCorrectModules) / totalModules;
			}
			int functionsGreenWidth = width;
			if(totalFunctionCalls > 0) {
				functionsGreenWidth = (width * totalTracableFunctionCalls) / totalFunctionCalls;
			}
			out.write("<tr><td><strong>Total</strong></td><td><img src=\"green.gif\" height=10 width=" + importsGreenWidth + ">");
			out.write("<img src=\"red.gif\" height=10 width=" + (width - importsGreenWidth) + ">");
			out.write(totalCorrectModules + "/" + totalModules);
			out.write("</td><td><img src=\"green.gif\" height=10 width=" + functionsGreenWidth + ">");
			out.write("<img src=\"red.gif\" height=10 width=" + (width - functionsGreenWidth) + ">");
			out.write(totalTracableFunctionCalls + "/" + totalFunctionCalls);
			out.write("</td></tr>");
			out.write("</table></body></html>");
			out.flush();
			out.close();
		} catch (IOException iox) {
			iox.printStackTrace();
		}
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
	
	private int getNumCorrectFunctionCalls(RequireJsModule module) {
		int numCorrectFunctionCalls = 0;
		
		Set<String> knownNames = new HashSet<String>(module.getNamedDependencies().keySet()); 
		knownNames.add("require");
		knownNames.add("exports");
		knownNames.add("module");
		knownNames.add("window");
		knownNames.add("console");
		knownNames.add("$");
		knownNames.add("jQuery");
		
		for(FunctionReference ref : module.getFunctionCalls()) {
			if(knownNames.contains(ref.getObjectName())) {
				numCorrectFunctionCalls++;
			}
		}
		
		return numCorrectFunctionCalls;
	}
}
