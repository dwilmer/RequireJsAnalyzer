# RequireJsAnalyzer
This tool analyzes JavaScript projects that are strung together using RequireJS.

## Workflow
Source files are read using the `Extractor`.
This file analyzes each line using several objects that each extract one feature from the code, and build the model.
The `Extractor` uses the dependencies that are present in the model to find other files.

The model can then be analyzed by analyzers, resulting in reports (in `reporting`).
An example of an analyzer is the `FunctionCallAnalyzer`.
These reports are then exported by exporters, for example the `FunctioncallHtmlExporter`.

This workflow can also be observed in the existing [Main Class](src/requireJsExtractor/Main.java)

