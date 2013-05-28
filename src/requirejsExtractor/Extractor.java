package requirejsExtractor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import analyzer.DependencyAnalyzer;
import analyzer.FileAnalyzer;
import analyzer.FunctionCallAnalyzer;
import analyzer.VariableDefinitionAnalyzer;

import model.FunctionReference;
import model.RequireJsModule;
import model.Tupel;

public class Extractor {
	private Set<String> traversedFiles;
	private String baseFolder;
	
	public static void main(String[] args) {
		testFunctionCalls(args);
	}
	
	private static void testFunctionCalls(String[] args) {
		Extractor x = new Extractor("brackets/");
		ReadResults results = x.readModule("brackets");
		
		System.out.println("Required:");
		for(Tupel<String, String> required : results.dependencies) {
			System.out.println(" - " + required.a);
		}
		System.out.println("Defined:");
		for(Tupel<Integer, String> required : results.definitions) {
			System.out.println(" - " + required.b);
		}
		System.out.println("Called:");
		for(Tupel<Integer,Tupel<String, String>> functionCall: results.functionCalls) {
			System.out.println(" - " + functionCall.b.a + "." + functionCall.b.b + "()");
		}
	}
	
	private static void getAndPrintDependencies(String[] args) {
		String filename = args[0];
		String baseFolder = "";
		int slashIndex = filename.lastIndexOf('/'); 
		if(slashIndex >= 0) {
			baseFolder = filename.substring(0, slashIndex + 1);
			filename = filename.substring(slashIndex + 1);
		}
		Extractor extract = new Extractor(baseFolder);
		RequireJsModule tree = extract.extractModules(filename);
		System.out.println(tree.getTreeString(0));
	}
	
	public Extractor(String baseFolder) {
		this.traversedFiles = new HashSet<String>();
		this.baseFolder = baseFolder;
	}
	
	public RequireJsModule extractModules(String id) {
		// create module
		RequireJsModule module = new RequireJsModule(id);
		
		// check whether we already traversed it (to avoid recursion)
		if(traversedFiles.contains(id)) {
			return module;
		} else {
			traversedFiles.add(id);
		}
		
		// read module
		ReadResults results = readModule(id); //FIXME
		
		// extract dependencies
		for (Tupel<String,String> dependency : results.dependencies) {
			RequireJsModule depModule = extractModules(dependency.b);
			if(depModule != null) {
				if(dependency.a != null) 
					module.addDependency(dependency.a, depModule);
				else
					module.addDependency(depModule);
			}
		}
		
		// extract function calls
		for (Tupel<Integer, Tupel<String,String>> functionCall : results.functionCalls) {
			module.addFunctionCall(new FunctionReference(functionCall.a, functionCall.b.a, functionCall.b.b));
		}
		
		// extract variable definitions
		for (Tupel <Integer, String> definition : results.definitions) {
			module.addVariableDefinition(definition.a, definition.b);
		}
		
		return module;
	}
	
	private RequireJsModule readModule(String id) {
		RequireJsModule module = new RequireJsModule(id);
		try {
			String filename = this.baseFolder + id + ".js";
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			List<FileAnalyzer> analyzers = this.getFileAnalyzers();
			String line = null;
			int lineNumber = 0;
			do {
				line = reader.readLine();
				lineNumber++;
				if(line != null) {
					for(FileAnalyzer analyzer : analyzers) {
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
	
	private List<FileAnalyzer> getFileAnalyzers() {
		List<FileAnalyzer> list = new ArrayList<FileAnalyzer>(3);
		list.add(new DependencyAnalyzer());
		list.add(new VariableDefinitionAnalyzer());
		list.add(new FunctionCallAnalyzer());
		return list;
	}
}
