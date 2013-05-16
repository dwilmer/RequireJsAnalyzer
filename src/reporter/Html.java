package reporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
			out.write("<table border=0><tr><th>Module</th><th>Module/variable naming</th></tr>");
			
			Object[] modules = this.modules.values().toArray();
			Arrays.sort(modules);
			for(Object moduleO : modules) {
				RequireJsModule module = (RequireJsModule) moduleO;
				int numModules = 0;
				int numCorrectModules = 0;
				for(Entry<String, RequireJsModule> entry : module.getNamedDependencies().entrySet()) {
					numModules++;
					String moduleId = entry.getValue().getId();
					String correctName = moduleId.substring(moduleId.lastIndexOf('/') + 1).toLowerCase();
					if(entry.getKey().toLowerCase().equals(correctName)) {
						numCorrectModules++;
					}
				}
				int greenWidth = width;
				if(numModules > 0) {
					greenWidth = (width * numCorrectModules) / numModules;
				}
				out.write("<tr><td>");
				out.write(module.getId());
				out.write("</td><td><img src=\"green.gif\" height=10 width=" + greenWidth + ">");
				out.write("<img src=\"red.gif\" height=10 width=" + (width - greenWidth) + ">");
				out.write(numCorrectModules + "/" + numModules);
				out.write("</td></tr>");
			}
			
			out.write("</table></body></html>");
			out.flush();
			out.close();
		} catch (IOException iox) {
			iox.printStackTrace();
		}
	}
}
