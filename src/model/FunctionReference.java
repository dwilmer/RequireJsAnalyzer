package model;

public class FunctionReference {
	private int lineNumber;
	private String objectName;
	private String functionName;
	
	public FunctionReference(int lineNumber, String objectName, String functionName) {
		this.lineNumber = lineNumber;
		this.objectName = objectName;
		this.functionName = functionName;
	}
	
	public int getLineNumber() {
		return this.lineNumber;
	}

	public String getObjectName() {
		return this.objectName;
	}
	
	public String getFunctionName() {
		return this.functionName;
	}
}
