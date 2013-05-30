package requirejsExtractor;

import java.util.List;

import model.RequireJsModule;
import reporting.FunctionCallReport;
import analyzer.FunctionCallAnalyzer;
import analyzer.ReportToplevelMerger;
import export.FunctioncallHtmlExporter;
import extractor.Extractor;

public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Extractor x = new Extractor("brackets/");
		RequireJsModule brackets = x.extractModules("brackets");
		
		FunctionCallAnalyzer analyzer = new FunctionCallAnalyzer();
		List<FunctionCallReport> functionCallReport = analyzer.getFunctionCallReports(brackets);
		List<FunctionCallReport> topLevelFunctionCallReport = ReportToplevelMerger.mergeFunctionCallReports(functionCallReport);
		
		new FunctioncallHtmlExporter(functionCallReport).export("report/report.html", "Brackets");
		new FunctioncallHtmlExporter(topLevelFunctionCallReport).export("report/toplevel.html", "Brackets consolidated");
	}
	
}
