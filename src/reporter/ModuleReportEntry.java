package reporter;

public class ModuleReportEntry implements Comparable<ModuleReportEntry>{
	private String module;
	private int green;
	private int yellow;
	private int red;
	
	public ModuleReportEntry(String module, int green, int yellow, int red) {
		this.module = module;
		this.green = green;
		this.yellow = yellow;
		this.red = red;
	}
	
	public int getScore() {
		if(green + yellow + red == 0) {
			return 0;
		}
		return (100 * red + 50 * yellow) / (green + yellow + red);
	}
	
	@Override
	public int compareTo(ModuleReportEntry arg0) {
		return this.getScore() - arg0.getScore();
	}
	
	public int getGreen() {
		return this.green;
	}
	
	public int getYellow() {
		return this.yellow;
	}
	
	public int getRed() {
		return this.red;
	}
	
	public String getModule() {
		return this.module;
	}
}
