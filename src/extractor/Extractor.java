package extractor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import model.RequireJsModule;

public class Extractor {
	private Set<String> traversedFiles;
	private String baseFolder;
	
	public Extractor(String baseFolder) {
		this.traversedFiles = new HashSet<String>();
		this.baseFolder = baseFolder;
	}
	
	public RequireJsModule extractModules(String id) {
		RequireJsModule module = new RequireJsModule(id);
		this.readModule(module);
		return module;
	}
	
	public void readModule(RequireJsModule module) {
		// check whether we already traversed it (to avoid recursion)
		if(traversedFiles.contains(module.getId())) {
			return;
		} else {
			traversedFiles.add(module.getId());
		}
		
		// read module
		readModuleContents(module);
		
		// read dependencies
		for (RequireJsModule dependency : module.getDependencies()) {
			readModule(dependency);
		}
	}
	
	private RequireJsModule readModuleContents(RequireJsModule module) {
		try {
			String filename = this.baseFolder + module.getId() + ".js";
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			List<ExtractorInterface> analyzers = this.getFileAnalyzers();
			String line = null;
			int lineNumber = 0;
			do {
				line = reader.readLine();
				lineNumber++;
				if(line != null) {
					for(ExtractorInterface analyzer : analyzers) {
						analyzer.analyzeLine(line, lineNumber, module);
					}
				}
			} while (line != null);
			
			reader.close();
		} catch (IOException iox) {
			iox.printStackTrace();
		}
		return module;
	}
	
	private List<ExtractorInterface> getFileAnalyzers() {
		List<ExtractorInterface> list = new ArrayList<ExtractorInterface>(3);
		list.add(new DependencyExtractor());
		list.add(new VariableDefinitionExtractor());
		list.add(new FunctionCallExtractor());
		return list;
	}
}
