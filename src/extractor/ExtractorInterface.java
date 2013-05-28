package extractor;

import model.RequireJsModule;

public interface ExtractorInterface {
	public void analyzeLine(String line, int lineNumber, RequireJsModule module);
}
