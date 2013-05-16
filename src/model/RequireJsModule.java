package model;

import java.util.LinkedList;
import java.util.List;

public class RequireJsModule {
	private String id;
	private List<RequireJsModule> dependencies;
	
	public RequireJsModule(String id) {
		this.id = id;
		this.dependencies = new LinkedList<RequireJsModule>();
	}
	
	public void addDependency(RequireJsModule dependency) {
		this.dependencies.add(dependency);
	}
	
	public String getId() {
		return this.id;
	}
	
	public List<RequireJsModule> getDependencies() {
		return this.dependencies;
	}
	
	public String getTreeString(int indent) {
		StringBuilder treeStringBuilder = new StringBuilder();
		for(int i = 0; i < indent; i++) {
			treeStringBuilder.append("  ");
		}
		treeStringBuilder.append(this.id);
		treeStringBuilder.append('\n');
		
		for (RequireJsModule dependency : this.dependencies) {
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
}
