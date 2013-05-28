package extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.RequireJsModule;

public class VariableDefinitionExtractor implements ExtractorInterface {
	private static Pattern FUNCTION_REGEX = Pattern.compile("function[\\s]?([a-zA-Z$_][a-zA-Z0-9$_]*)?\\(([a-zA-Z$_][a-zA-Z0-9$_]*(,[\\s]*[a-zA-Z$_][a-zA-Z0-9$_]*)*)?\\)");
	private static Pattern VARDEF_REGEX = Pattern.compile("[\\s]([a-zA-Z$_][a-zA-Z0-9$_]*)[\\s]*=");
	
	@Override
	public void analyzeLine(String line, int lineNumber, RequireJsModule module) {
		Matcher match = VARDEF_REGEX.matcher(line);
		if(match.find()) {
			String defined = match.group(1);
			
			if(module.getModule(defined) != null)
				module.addVariableDefinition(lineNumber, defined);
		}
		
		match = FUNCTION_REGEX.matcher(line);
		if(match.find() && match.group(2) != null && line.indexOf("define(") == -1) {
			String[] defined = match.group(2).split(",[\\s]*");
			for(String var : defined) {
				module.addVariableDefinition(lineNumber, var);
			}
		}
	}
	
}
