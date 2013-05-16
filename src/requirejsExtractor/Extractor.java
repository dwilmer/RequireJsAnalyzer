package requirejsExtractor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.FunctionReference;
import model.RequireJsModule;
import model.Tupel;

public class Extractor {
	private static Pattern NAMED_REQUIRE_REGEX = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)[\\s]*= require\\(\"(.*)\"\\)(\\.)?");
	private static Pattern ANON_REQUIRE_REGEX = Pattern.compile("^[\\s]*require\\(\"(.*)\"\\)");
	private static Pattern FUNCTIONCALL_REGEX = Pattern.compile("[\\s(]([a-zA-Z$_][a-zA-Z0-9$_]*)\\.([a-zA-Z$_][a-zA-Z0-9$_]*)\\(");
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
		System.out.println("Found:");
		for(Tupel<String, String> functionCall: results.functionCalls) {
			System.out.println(" - " + functionCall.a + "." + functionCall.b + "()");
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
		ReadResults results = readModule(id);
		
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
		for (Tupel<String,String> functionCall : results.functionCalls) {
			module.addFunctionCall(functionCall.a, functionCall.b);
		}
		
		return module;
	}
	
	private ReadResults readModule(String id) {
		ReadResults results = new ReadResults();
		try {
			String filename = this.baseFolder + id + ".js";
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			String line = null;
			do {
				line = reader.readLine();
				if(line != null) {
					String varName = null;
					String dependency = null;
					
					Matcher match = NAMED_REQUIRE_REGEX.matcher(line);
					if(match.find()) {
						varName = match.group(1);
						String tentativeDependency = match.group(2);
						if(!tentativeDependency.startsWith("text!") && !tentativeDependency .startsWith("i18n!")) {
							dependency = tentativeDependency;
						}
					} else {
						match = ANON_REQUIRE_REGEX.matcher(line);
						if(match.find()) {
							String tentativeDependency = match.group(1);
							if(!tentativeDependency .startsWith("text!") && !tentativeDependency .startsWith("i18n!")) {
								dependency = tentativeDependency;
							}
						}
					}
					if(dependency != null) {
						results.dependencies.add(new Tupel<String,String>(varName, dependency));
					}
					
					match = FUNCTIONCALL_REGEX.matcher(line);
					if(match.find()) {
						results.functionCalls.add(new Tupel<String, String>(match.group(1), match.group(2)));
					}
				}
			} while (line != null);
			
			reader.close();
		} catch (IOException iox) {
			iox.printStackTrace();
		}
		return results;
	}
}
