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

public class Extractor {
	private static Pattern REQUIRE_REGEX = Pattern.compile("require\\(\"(.*)\"\\)");
	private Set<String> traversedFiles;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Extractor extract = new Extractor();
		RequireJsModule tree = extract.extractModules("brackets");
		System.out.println(tree.toString());
	}
	
	public Extractor() {
		this.traversedFiles = new HashSet<String>();
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
		Queue<String> dependencies = readModule(id);
		
		// extract dependencies
		for (String dependency : dependencies) {
			RequireJsModule depModule = extractModules(dependency);
			if(depModule != null) {
				module.addDependency(depModule);
			}
		}
		
		return module;
	}
	
	private Queue<String> readModule(String id) {
		Queue<String> dependencies = new LinkedList<String>();
		try {
			String filename = id + ".js";
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			String line = null;
			do {
				line = reader.readLine();
				if(line != null) {
					Matcher match = REQUIRE_REGEX.matcher(line);
					if(match.find()) {
						String dependency = match.group(1);
						if(!dependency.startsWith("text!") && !dependency.startsWith("i18n!")) {
							dependencies.add(dependency);
						}
					}
				}
			} while (line != null);
			
			reader.close();
		} catch (IOException iox) {
			iox.printStackTrace();
		}
		return dependencies;
	}
}
