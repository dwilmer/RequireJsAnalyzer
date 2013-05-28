package extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.RequireJsModule;

public class DependencyExtractor implements ExtractorInterface {
	private static Pattern NAMED_REQUIRE_REGEX = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)[\\s]*= require\\(\"(.*)\"\\)(\\.)?");
	private static Pattern ANON_REQUIRE_REGEX = Pattern.compile("^[\\s]*require\\(\"(.*)\"\\)");
	
	@Override
	public void analyzeLine(String line, int lineNumber, RequireJsModule module) {
		String varName = null;
		String dependencyId = null;
		
		Matcher match = NAMED_REQUIRE_REGEX.matcher(line);
		if(match.find()) {
			varName = match.group(1);
			String tentativeDependency = match.group(2);
			if(!tentativeDependency.startsWith("text!") && !tentativeDependency.startsWith("i18n!")) {
				dependencyId = tentativeDependency;
			}
		} else {
			match = ANON_REQUIRE_REGEX.matcher(line);
			if(match.find()) {
				String tentativeDependency = match.group(1);
				if(!tentativeDependency .startsWith("text!") && !tentativeDependency.startsWith("i18n!")) {
					dependencyId = tentativeDependency;
				}
			}
		}
		if(dependencyId != null) {
			RequireJsModule dependency = new RequireJsModule(dependencyId);
			if(varName == null)
				module.addDependency(dependency);
			else
				module.addDependency(varName, dependency);
		}
		
	}
	
}
