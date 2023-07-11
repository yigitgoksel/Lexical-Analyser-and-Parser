# Lexical-Analyser-and-Parser
Implementation of a lexical analyzer (scanner)and a syntax analyzer (parser) for a given specific programming language, i.e., PPLL. 

## Information About the Project

### The Lexical Analyzer Part
Given a PPLL source file, the scanner outputs the sequence of tokens in the input,
one token per line. Each token is annotated with the position of the token in the
input. 

If the input is lexically incorrect, the scanner does not produce any output except a single
error message corresponding to the first incorrect token. The error message indicates
what is wrong and the position in the input where the error was found. 

### The Parser Part

The parser uses the tokens produced by the scanner that is implemented in the first
part. The grammar for a syntactically correct PPLL program is given below:
The parser is a recursive-descent parser that takes a PPLL program and prints its parse
tree to stdout if the input is in fact a syntactically correct program. If the input is lexically incorrect, the
parser outputs the scanner’s error message as in the first part of the project. If the input is lexically
correct but syntactically incorrect, the parser outputs a syntax error for the first invalid token.


