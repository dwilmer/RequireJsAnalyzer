package requirejsExtractor;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.Tupel;

public class ReadResults {
	public final Queue<Tupel<String,String>> dependencies;
	public final List<Tupel<String, String>> functionCalls;
	
	public ReadResults() {
		this.dependencies = new LinkedList<Tupel<String,String>>();
		this.functionCalls = new LinkedList<Tupel<String,String>>();
	}
}
