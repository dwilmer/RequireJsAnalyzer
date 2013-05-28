package extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.FunctionReference;
import model.RequireJsModule;

public class FunctionCallExtractor implements ExtractorInterface {
	private static Pattern FUNCTIONCALL_REGEX = Pattern.compile("[\\s(]([a-zA-Z$_][a-zA-Z0-9$_]*)\\.([a-zA-Z$_][a-zA-Z0-9$_]*)\\(");
	
	@Override
	public void analyzeLine(String line, int lineNumber, RequireJsModule module) {
		Matcher match = FUNCTIONCALL_REGEX.matcher(line);
		if(match.find()) {
			module.addFunctionCall(new FunctionReference(lineNumber, match.group(1), match.group(2)));
		}
	}
	
}
