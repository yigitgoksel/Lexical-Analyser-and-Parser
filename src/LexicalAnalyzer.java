import java.io.*;
import java.util.*;

//YİĞİT GÖKSEL 
//AHMET ÇAĞRI HODOĞLUGİL 




public class LexicalAnalyzer {
    public static final char EOF = '\0';
    public static final String[] KEYWORDS = {"define", "let", "cond", "if", "begin"};

    public static final String[] BOOLEAN = {"true", "false"};



    public final BufferedReader reader;
    public char currentChar;
    public int currentLine;
    public int currentColumn;

    public Token result;

    public LexicalAnalyzer(File file) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(file));
        this.currentChar = EOF;
        this.currentLine = 1;
        this.currentColumn = 0;
        advance();
    }
    public static void main(String[] args) {
        try {
            // read input from file
            File file = new File("input.txt");


            // create lexer and tokenize input
            LexicalAnalyzer lexer = new LexicalAnalyzer(file);
            List<Token> tokens = lexer.tokenize();

            // print tokens to console
            for (Token token : tokens) {
                System.out.println(token);
            }



            Parser parser = new Parser(tokens);
            parser.parse();


        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void advance() {
        try {
            int nextChar = reader.read();
            if (nextChar == -1) {
                currentChar = EOF;
            } else {
                currentChar = (char) nextChar;
            }
            currentColumn++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    int errorCheckNumber=0;
    public void skipWhitespace() {
        errorCheckNumber=0;
        while (Character.isWhitespace(currentChar)) {
            if (currentChar == '\n') {
                currentLine++;
                currentColumn = 0;
            }
            advance();
        }
        errorCheckNumber=1;
    }

    public void skipComment() {
        while (currentChar != '\n' && currentChar != EOF) {
            advance();
        }
        skipWhitespace();
    }

    int columnResetNumber=0;
    public String readNumber() {
        StringBuilder result = new StringBuilder();
        columnResetNumber =0;
        while (Character.isDigit(currentChar) || currentChar == '.' || currentChar == 'x' || currentChar == 'b' || currentChar == '+' || currentChar == '-' || currentChar == 'e' || currentChar == 'E') {
            result.append(currentChar);
            columnResetNumber++;
            advance();
        }
        currentColumn-= columnResetNumber;
        return result.toString();
    }

    int columnResetIdentifier=0;
    public String readIdentifier() {
        StringBuilder result = new StringBuilder();
        columnResetIdentifier=0;
        String tempString;
        while (Character.isLetterOrDigit(currentChar) || currentChar == '.' || currentChar == '!' || currentChar == '*' || currentChar == '/' || currentChar == ':' || currentChar == '<' || currentChar == '=' || currentChar == '>' || currentChar == '?') {
            result.append(currentChar);
            columnResetIdentifier++;
            advance();
        }
        currentColumn-=columnResetIdentifier;
        return result.toString();
    }

    int columnResetCharacter;
    public String readCharacter() {
        columnResetCharacter=0;
        advance(); // consume opening single-quote
        columnResetCharacter++;
        if (currentChar == '\\') {
            advance(); // consume escape character
            columnResetCharacter++;
        }
        char ch = currentChar;
        advance(); // consume character
        columnResetCharacter++;
        advance(); // consume closing single-quote
        columnResetCharacter++;
        currentColumn-=columnResetCharacter;
        return String.valueOf(ch);
    }

    int columnResetString;
    public String readString() {
        columnResetString =0;
        advance(); // consume opening double-quote
        columnResetString++;
        StringBuilder result = new StringBuilder();
        while (currentChar != '"') {
            if (currentChar == '\\') {
                columnResetString++;
                advance(); // consume escape character
            }
            result.append(currentChar);
            columnResetString++;
            advance();
        }
        columnResetString++;
        advance(); // consume closing double-quote
        currentColumn-= columnResetString;
        return result.toString();
    }

    public Token getNextToken() {
        while (currentChar != EOF) {
            if (currentChar == '~') {
                skipComment();
                continue;
            }
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }
            if (currentChar == '(') {
                result=new Token(TokenType.LEFTPAR, "(", currentLine, currentColumn);
                advance();
                return result;

            }
            if (currentChar == ')') {
                result=new Token(TokenType.RIGHTPAR, ")", currentLine, currentColumn);
                advance();
                return result;
            }
            if (currentChar == '[') {
                result= new Token(TokenType.LEFTSQUAREB, "[", currentLine, currentColumn);
                advance();
                return result;
            }

            if (currentChar == ']') {
                result= new Token(TokenType.RIGHTSQUAREB, "]", currentLine, currentColumn);
                advance();
                return result;
            }
            if (currentChar == '{') {
                result= new Token(TokenType.LEFTBRACE, "{", currentLine, currentColumn);
                advance();
                return result;
            }
            if (currentChar == '}') {
                result= new Token(TokenType.RIGHTBRACE, "}", currentLine, currentColumn);
                advance();
                return result;

            }
            if (currentChar == '\'') {
                result=new Token(TokenType.CHARACTER, readCharacter(), currentLine, currentColumn);
                currentColumn+=columnResetCharacter;
                return result;
            }
            if (currentChar == '"') {
                result=new Token(TokenType.STRING, readString(), currentLine, currentColumn);
                currentColumn+= columnResetString;
                return result;
            }
            if (Character.isDigit(currentChar)) {
                String errorString=String.valueOf(currentChar);
                String num=Character.toString(currentChar);
                advance();
                if(!Character.isDigit(currentChar) && currentChar!='x' && errorCheckNumber!=0 && !Character.isWhitespace(currentChar) && currentChar!=')'  && currentChar!=']'  && currentChar!='}'){
                    errorString+=(String.valueOf(currentChar));
                    advance();
                    while (!Character.isWhitespace(currentChar) && currentChar!='\n'  && currentChar!=')'  && currentChar!=']'  && currentChar!='}'){
                        errorString+=(String.valueOf(currentChar));
                        advance();
                    }
                    throw new RuntimeException("Unexpected character "+ errorString+" at line " + currentLine + " column " + currentColumn);
                }
                currentColumn--;
                readNumber();
                result= new Token(TokenType.NUMBER,num , currentLine, currentColumn);
                currentColumn++;
                currentColumn+= columnResetNumber;
                return result;

            }
            if (Character.isLetter(currentChar)) {
                String identifier = readIdentifier();
                if (Arrays.asList(KEYWORDS).contains(identifier)) {
                    result=new Token(TokenType.KEYWORD, identifier, currentLine, currentColumn);
                    currentColumn+=columnResetIdentifier;
                    return result;
                }
                else if(Arrays.asList(BOOLEAN).contains(identifier)){
                    result=new Token(TokenType.BOOLEAN, identifier, currentLine, currentColumn);
                    currentColumn+=columnResetIdentifier;
                    return result;
                }
                result= new Token(TokenType.IDENTIFIER, identifier, currentLine, currentColumn);
                currentColumn+=columnResetIdentifier;
                return result;
            }
            if (currentChar == '+' ) {
                advance();
                if (Character.isDigit(currentChar)) {
                    currentColumn--;
                    result= new Token(TokenType.NUMBER, readNumber(), currentLine, currentColumn);
                    currentColumn++;
                    advance();
                    return result;
                }
                currentColumn--;
                result=new Token(TokenType.IDENTIFIER, "+", currentLine, currentColumn);
                currentColumn++;

                advance();
                return result;
            }
            if (currentChar == '-') {
                advance();
                if (Character.isDigit(currentChar)) {
                    currentColumn--;
                    result= new Token(TokenType.NUMBER, readNumber(), currentLine, currentColumn);
                    currentColumn++;
                    advance();
                    return result;
                }
                currentColumn--;
                result=new Token(TokenType.IDENTIFIER, "-", currentLine, currentColumn);
                currentColumn++;
                advance();
                return result;
            }
            if (currentChar == '*') {
                result=new Token(TokenType.IDENTIFIER, "*", currentLine, currentColumn);
                advance();
                return result;
            }
            if (currentChar == '/') {
                result=new Token(TokenType.IDENTIFIER, "/", currentLine, currentColumn);
                advance();
                return result;
            }
            if (currentChar == '%') {
                result=new Token(TokenType.IDENTIFIER, "%", currentLine, currentColumn);
                advance();
                return result;
            }
            if (currentChar == '=') {
                advance();
                if (currentChar == '=') {
                    currentColumn--;
                    result=new Token(TokenType.IDENTIFIER, "==", currentLine, currentColumn);
                    currentColumn++;
                    advance();
                    return result;
                } else {
                    currentColumn--;
                    result= new Token(TokenType.IDENTIFIER, "=", currentLine, currentColumn);
                    currentColumn++;
                    return result;
                }
            }
            if (currentChar == '<') {
                advance();
                if (currentChar == '=') {
                    currentColumn--;
                    result=new Token(TokenType.IDENTIFIER, "<=", currentLine, currentColumn);
                    currentColumn++;
                    advance();
                    return result;
                } else {
                    currentColumn--;
                    result= new Token(TokenType.IDENTIFIER, "<", currentLine, currentColumn);
                    currentColumn++;
                    return result;
                }
            }
            if (currentChar == '>') {
                advance();
                if (currentChar == '=') {
                    currentColumn--;
                    result=new Token(TokenType.IDENTIFIER, ">=", currentLine, currentColumn);
                    currentColumn++;
                    advance();
                    return result;
                } else {
                    currentColumn--;
                    result= new Token(TokenType.IDENTIFIER, ">", currentLine, currentColumn);
                    currentColumn++;
                    return result;
                }
            }
            if (currentChar == '!') {
                advance();
                if (currentChar == '=') {
                    currentColumn--;
                    result=new Token(TokenType.IDENTIFIER, "!=", currentLine, currentColumn);
                    currentColumn++;
                    advance();
                    return result;
                } else {
                    currentColumn--;
                    throw new RuntimeException("Unexpected character ! at line " + currentLine + " column " + currentColumn);
                }
            }
            throw new RuntimeException("Unexpected character " + currentChar + " at line " + currentLine + " column " + currentColumn);
        }
        return new Token(TokenType.EOF, "", currentLine, currentColumn);
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token = getNextToken();
        while (token.getType() != TokenType.EOF) {
            tokens.add(token);
            token = getNextToken();
        }
        tokens.add(token);
        return tokens;
    }



    enum TokenType {
        LEFTPAR, RIGHTPAR, LEFTBRACE, RIGHTBRACE, LEFTSQUAREB, RIGHTSQUAREB,
        BOOLEAN, IDENTIFIER, STRING, NUMBER, CHARACTER, KEYWORD, EOF
    }


}
