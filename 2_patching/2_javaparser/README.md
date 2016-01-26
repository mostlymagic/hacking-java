JavaParser
==========

[JavaParser](http://javaparser.github.io/javaparser/) is a freely available, fully functional Java language
parser with AST generation and visitor support. It lets you create ASTs from scratch or parse existing .java files.

It allows modifying the AST and writing the changed version to files or other output.

Limitations:

JavaParser works on source files only, it will not manage references to other types or automatically create import
statements for you. This makes the API very verbose and hard to use.

On the other hand, it's the only standalone framework known to me that supports a full roundtrip from source to
modification to source, and the processed code will have no runtime dependencies the original code didn't have.