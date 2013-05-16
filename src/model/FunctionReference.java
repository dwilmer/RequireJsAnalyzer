package model;

public class FunctionReference {
	private String objectName;
	private String functionName;
	
	public FunctionReference(String objectName, String functionName) {
		this.objectName = objectName;
		this.functionName = functionName;
	}

	public String getObjectName() {
		return this.objectName;
	}
	
	public String getFunctionName() {
		return this.functionName;
	}
}
