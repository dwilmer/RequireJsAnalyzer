package model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RequireJsModule implements Comparable<RequireJsModule> {
	private String id;
	private Map<String, RequireJsModule> namedDependencies;
	private List<RequireJsModule> anonDependencies;
	private List<FunctionReference> functionCalls;
	
	public RequireJsModule(String id) {
		this.id = id;
		this.namedDependencies = new HashMap<String, RequireJsModule>();
		this.anonDependencies = new LinkedList<RequireJsModule>();
		this.functionCalls = new LinkedList<FunctionReference>();
	}
	
	public void addDependency(RequireJsModule dependency) {
		this.anonDependencies.add(dependency);
	}
	
	public void addDependency(String varName, RequireJsModule dependency) {
		this.namedDependencies.put(varName, dependency);
	}
	
	public String getId() {
		return this.id;
	}
	
	public List<RequireJsModule> getDependencies() {
		List<RequireJsModule> allDependencies = new LinkedList<RequireJsModule>();
		allDependencies.addAll(anonDependencies);
		allDependencies.addAll(namedDependencies.values());
		return allDependencies;
	}
	
	public Map<String, RequireJsModule> getNamedDependencies() {
		return this.namedDependencies;
	}
	
	public List<RequireJsModule> getAnonDependencies() {
		return this.anonDependencies;
	}
	
	public RequireJsModule getModule(String varName) {
		return this.namedDependencies.get(varName);
	}
	
	public void addFunctionCall(String objectName, String functionName) {
		this.functionCalls.add(new FunctionReference(objectName, functionName));
	}
	
	public List<FunctionReference> getFunctionCalls() {
		return this.functionCalls;
	}
	
	public String getTreeString(int indent) {
		StringBuilder treeStringBuilder = new StringBuilder();
		for(int i = 0; i < indent; i++) {
			treeStringBuilder.append("  ");
		}
		treeStringBuilder.append(this.id);
		treeStringBuilder.append('\n');
		
		for (RequireJsModule dependency : this.getDependencies()) {
			treeStringBuilder.append(dependency.getTreeString(indent + 1));
		}
		
		return treeStringBuilder.toString();
	}
	
	public String toString() {
		return this.id;
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof RequireJsModule) || other == null) {
			return false;
		}
		RequireJsModule otherModule = (RequireJsModule)other;
		return otherModule.id.equals(this.id);
	}
	
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public int compareTo(RequireJsModule o) {
		return this.id.compareTo(o.id);
	}
}
