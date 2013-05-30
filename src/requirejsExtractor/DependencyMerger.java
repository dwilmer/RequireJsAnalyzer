package requirejsExtractor;

import java.util.HashMap;
import java.util.Map;

import extractor.Extractor;

import model.RequireJsModule;

public class DependencyMerger {
	private Map<String, RequireJsModule> mergedTree;
	
	public static void main(String[] args) {
		Extractor x = new Extractor("brackets");
		RequireJsModule rawTree = x.extractModules("brackets");
		System.out.println(new DependencyMerger(rawTree).mergedTree.get("brackets").getTreeString(0));
	}
	
	public DependencyMerger(RequireJsModule tree) {
		this.mergedTree = new HashMap<String, RequireJsModule>();
		this.mergeModule(tree);
	}
	
	public void mergeModule(RequireJsModule module) {
		String topLevelElement = getTopLevelElement(module);
		
		RequireJsModule mergedModule = getMergedModule(topLevelElement);
		
		// merge dependencies
		for (RequireJsModule dependency : module.getDependencies()) {
			// get TLE of dependency
			String dependencyTopLevelElement = getTopLevelElement(dependency);
			
			// if it does not equal the TLE
			if(!dependencyTopLevelElement.equals(topLevelElement)) {
				// Create new dependency
				RequireJsModule mergedDependency = new RequireJsModule(dependencyTopLevelElement);
				if(!mergedModule.getDependencies().contains(mergedDependency)) {
					mergedModule.addDependency(mergedDependency);
					if(mergedTree.get(dependencyTopLevelElement) == null) {
						mergedTree.put(dependencyTopLevelElement, mergedDependency);
					}
				}
			}
		}
		
		for (RequireJsModule dependency : module.getDependencies()) {
			mergeModule(dependency);
		}
	}

	private RequireJsModule getMergedModule(String topLevelElement) {
		RequireJsModule mergedModule = mergedTree.get(topLevelElement);
		if(mergedModule == null) {
			mergedModule = new RequireJsModule(topLevelElement);
			mergedTree.put(topLevelElement, mergedModule);
		}
		return mergedModule;
	}
	
	private static String getTopLevelElement(RequireJsModule module) {
		String id = module.getId();
		int rootSize = id.indexOf('/');
		if(rootSize <= 0) {
			return id;
		}
		
		return id.substring(0, rootSize);
	}
}
