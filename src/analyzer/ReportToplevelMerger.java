package analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import reporting.FunctionCallReport;

public class ReportToplevelMerger {
	private static String getTopLevelId(String id) {
		return id.split("/", 2)[0];
	}
	
	public static List<FunctionCallReport> mergeFunctionCallReports(List<FunctionCallReport> reports) {
		HashMap<String, FunctionCallReport> mergedReports = new HashMap<String, FunctionCallReport>();
		for (FunctionCallReport report: reports) {
			String topLevelId = getTopLevelId(report.getId());
			
			FunctionCallReport mergedReport = mergedReports.get(topLevelId);
			if(mergedReport == null) {
				mergedReport = new FunctionCallReport(topLevelId);
				mergedReports.put(topLevelId, mergedReport);
			}
			mergedReport.add(report);
		}
		return new ArrayList<FunctionCallReport>(mergedReports.values());
	}
}
