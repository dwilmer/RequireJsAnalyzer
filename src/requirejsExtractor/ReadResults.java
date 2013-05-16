package requirejsExtractor;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.Tupel;

public class ReadResults {
	public final Queue<Tupel<String,String>> dependencies;
	public final List<Tupel<Integer, String>> definitions;
	public final List<Tupel<Integer, Tupel<String,String>>> functionCalls;
	
	public ReadResults() {
		this.dependencies = new LinkedList<Tupel<String,String>>();
		this.definitions = new LinkedList<Tupel<Integer,String>>();
		this.functionCalls = new LinkedList<Tupel<Integer,Tupel<String,String>>>();
	}
}
