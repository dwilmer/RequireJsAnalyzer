package export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import model.FunctionReference;
import model.RequireJsModule;
import reporting.FunctionCallReport;
import requirejsExtractor.Extractor;

public class FunctioncallHtmlExporter {
	private List<FunctionCallReport> reports;
	
	public FunctioncallHtmlExporter(List<FunctionCallReport> reports) {
		this.reports = reports;
	}
	
	private void exportModuleDetails() {
		String filename = "report/report.html";
		int width = 200;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			out.write("<!doctype html>");
			out.write("<html><head><title>Brackets analyzability</title><style type=\"text/css\"></style></head><body><h1>Brackets</h1>");
			out.write("<table border=0><tr><th>Module</th><th>Function call tracability</th><th>Stars</th></tr>");
			
			Object[] modules = this.modules.values().toArray();
			List<ModuleReportEntry> reportEntries = getReportEntries(modules);
			
			// get scores - lower is better
			int[] scores = new int[modules.length];
			int i = 0;
			for(ModuleReportEntry reportEntry : reportEntries) {
				scores[i++] = reportEntry.getScore();
			}
			
			// sort
			Arrays.sort(scores);
			
			// get bounds
			int fiveStarBound = scores[scores.length * 5 / 100]; // 65 to 95 percent get 4 stars, top five percent get 5 stars
			int fourStarBound = scores[scores.length * 35 / 100]; // 35 to 65 percent get 3 stars
			int threeStarBound = scores[scores.length * 65 / 100]; // 5 to 35 percent get 2 stars
			int twoStarBound = scores[scores.length * 95 / 100];  // bottom five percent get 1 star
			
			ModuleReportEntry totals = new ModuleReportEntry("Total", 0, 0, 0);
			for(ModuleReportEntry reportEntry : reportEntries) {
				if(reportEntry.getModule().indexOf("thirdparty") == 0) {
					continue;
				}
				
				out.write("<tr><td>");
				out.write(reportEntry.getModule());
				out.write("</td>");
				
				printBar(out, reportEntry, width);
				
				out.write("<td>");
				out.write("*");
				int score = reportEntry.getScore();
				if(score <= twoStarBound) out.write("*");
				if(score <= threeStarBound) out.write("*");
				if(score <= fourStarBound) out.write("*");
				if(score <= fiveStarBound) out.write("*");
				out.write("</td>");
				
				out.write("</tr>");
				totals.add(reportEntry);
			}
			out.write("<tr><td><strong>totals</strong></td>");
			printBar(out, totals, width);
			out.write("<td>");
			int percentage = (totals.getGreen() * 100) / (totals.getGreen() + totals.getYellow() + totals.getRed());
			out.write("" + percentage);
			out.write("% fully tracable</td></tr></table></body></html>");
			out.flush();
			out.close();
		} catch (IOException iox) {
			iox.printStackTrace();
		}
	}

	private void printBar(BufferedWriter out, ModuleReportEntry reportEntry, int barWidth) throws IOException {
		printBar(out, reportEntry.getGreen(), reportEntry.getYellow() + reportEntry.getGreen(), reportEntry.getRed() + reportEntry.getYellow() + reportEntry.getGreen(), barWidth);
	}
	
	private void printBar(BufferedWriter out, int lower, int upper, int total, int barWidth) throws IOException {
		int greenWidth = barWidth;
		int yellowWidth = 0;
		if(total > 0) {
			greenWidth = (barWidth * lower) / total;
			yellowWidth = (barWidth * upper) / total - greenWidth;
		}
		out.write("<td><img src=\"green.gif\" height=10 width=" + greenWidth + ">");
		out.write("<img src=\"yellow.gif\" height=10 width=" + (yellowWidth) + ">");
		out.write("<img src=\"red.gif\" height=10 width=" + ((barWidth - yellowWidth) - greenWidth) + ">");
		out.write(lower + "/" + upper + "/" + total);
		out.write("</td>");
	}
	
	private List<ModuleReportEntry> getReportEntries(Object[] modules) {
		Arrays.sort(modules);
		List<ModuleReportEntry> reports = new ArrayList<ModuleReportEntry>(modules.length);
		for(Object moduleO : modules) {
			RequireJsModule module = (RequireJsModule) moduleO;
			FunctionCallReport callReport = investigateFunctionCalls(module);
			int green = callReport.getNumLocalFunctionCalls() + callReport.numRequireFunctionCalls;
			int yellow = callReport.numRequireWrongFunctionCalls;
			int total = callReport.numUnknownFunctionCalls;
			reports.add(new ModuleReportEntry(module.getId(), green, yellow, total));
		}
		return reports;
	}

	private int getNumCorrectImports(RequireJsModule module) {
		int numCorrectModules = 0;
		for(Entry<String, RequireJsModule> entry : module.getNamedDependencies().entrySet()) {
			String moduleId = entry.getValue().getId();
			String correctName = moduleId.substring(moduleId.lastIndexOf('/') + 1).toLowerCase();
			if(entry.getKey().toLowerCase().equals(correctName)) {
				numCorrectModules++;
			}
		}
		return numCorrectModules;
	}
}
