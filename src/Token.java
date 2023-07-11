import java.util.Locale;


//YİĞİT GÖKSEL 150119053
//AHMET ÇAĞRI HODOĞLUGİL 150118508
//MUHAMMED ZAHİD MANSIZ 150119754


public class Token {
    public LexicalAnalyzer.TokenType type;
    public String value;
    public int line;
    public int column;

    public Token(LexicalAnalyzer.TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public LexicalAnalyzer.TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {

        if (String.valueOf(type)=="KEYWORD"){
            return String.format(value.toUpperCase(Locale.ENGLISH) +" "+ line + ":" +column);

        }else if(String.valueOf(type)=="EOF"){
            return "";
        }
        return String.format(type+ " " + line + ":" +column);
    }
}