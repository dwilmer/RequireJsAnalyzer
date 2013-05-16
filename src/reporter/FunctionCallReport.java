package reporter;

import java.util.LinkedList;
import java.util.List;

public class FunctionCallReport {
	public int numRequireFunctionCalls;
	public final List<Integer> localFunctionCallDistances;
	public int numUnknownFunctionCalls;
	
	public FunctionCallReport() {
		this.numRequireFunctionCalls = 0;
		this.localFunctionCallDistances = new LinkedList<Integer>();
		this.numUnknownFunctionCalls = 0;
	}
	
	public void add(FunctionCallReport other) {
		this.numRequireFunctionCalls += other.numRequireFunctionCalls;
		this.localFunctionCallDistances.addAll(other.localFunctionCallDistances);
		this.numUnknownFunctionCalls += other.numUnknownFunctionCalls;
	}
	
	public int getTotal() {
		return this.numRequireFunctionCalls + this.getNumLocalFunctionCalls() + this.numUnknownFunctionCalls;
	}
	
	public int getNumLocalFunctionCalls() {
		return this.localFunctionCallDistances.size();
	}
	
	public int getNumLocalFunctionCallsUnder(int threshold) {
		int count = 0;
		for(Integer distance : this.localFunctionCallDistances) {
			if(distance <= threshold)
				count++;
		}
		return count;
	}
	
	public int getAverageFunctionCallDistance() {
		if(this.localFunctionCallDistances.size() == 0)
			return -1;
		
		int total = 0;
		for(Integer distance : this.localFunctionCallDistances) {
			total += distance;
		}
		return total / this.localFunctionCallDistances.size();
	}
}
