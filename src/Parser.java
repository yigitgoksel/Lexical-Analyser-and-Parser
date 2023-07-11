import java.util.*;

//YİĞİT GÖKSEL 150119053
//AHMET ÇAĞRI HODOĞLUGİL 150118508
//MUHAMMED ZAHİD MANSIZ 150119754

public class Parser {
    private List<Token> tokens;
    private int currentTokenIndex;

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public Parser(List<Token> tokens) throws Exception {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }



    public void parse() {
        System.out.println("<Program>");
        Program(1);
    }

    private void Program(int depth) {
        if (currentTokenIndex+1 >= tokens.size()) {
            System.out.println("<Program>");
            return;
        }
        printIndentation(depth);
        System.out.println("<TopLevelForm>");
        TopLevelForm(depth + 1);
        Program(depth);
    }

    private void TopLevelForm(int depth) {
        printIndentation(depth);
        System.out.print("<LEFTPAR>");
        match(LexicalAnalyzer.TokenType.LEFTPAR, depth + 1);
        printIndentation(depth);
        System.out.println("<SecondLevelForm>");
        SecondLevelForm(depth + 1);
        printIndentation(depth);
        System.out.print("<RIGHTPAR>");
        match(LexicalAnalyzer.TokenType.RIGHTPAR, depth + 1);
    }

    private void SecondLevelForm(int depth) {
        if (lookahead().equals("DEFINE")) {
            printIndentation(depth);
            System.out.println("<Definition>");
            Definition(depth + 1);
        } else if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.LEFTPAR)) {
            printIndentation(depth);
            System.out.println("<LEFTPAR>");
            match(LexicalAnalyzer.TokenType.LEFTPAR, depth + 1);
            printIndentation(depth);
            System.out.println("<FunCall>");
            FunCall(depth + 1);
            printIndentation(depth);
            System.out.println("<RIGHTPAR>");
            match(LexicalAnalyzer.TokenType.RIGHTPAR, depth + 1);
        } else {
            throw new SyntaxErrorException("Invalid SecondLevelForm at position " + getCurrentToken().getLine() + ":" + getCurrentToken().getColumn());
        }
    }

    private void printIndentation(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("\t");
        }
    }


    private String lookahead() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex).getValue().toUpperCase(Locale.ENGLISH);
        } else {
            return "";
        }
    }

    private Token
    getCurrentToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex);
        } else {
            return new Token
                    (LexicalAnalyzer.TokenType.EOF,"", -1, -1); // Or another suitable value for when there are no more tokens
        }
    }

    private void Definition(int depth) {
        printIndentation(depth);
        System.out.print("<DEFINE> ");
        match(LexicalAnalyzer.TokenType.KEYWORD, depth + 2);
        DefinitionRight(depth + 1);
    }

    private void FunCall(int depth) {
        printIndentation(depth);
        System.out.println("<FunCall>");
        printIndentation(depth + 1);
        System.out.print("<IDENTIFIER>");
        match(LexicalAnalyzer.TokenType.IDENTIFIER, depth + 2);
        Expressions(depth + 1);
    }

    private void DefinitionRight(int depth) {
        printIndentation(depth);
        System.out.println("<DefinitionRight>");
        if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.IDENTIFIER)) {
            printIndentation(depth + 1);
            System.out.println("<IDENTIFIER>");
            match(LexicalAnalyzer.TokenType.IDENTIFIER, depth + 2);
            Expression(depth + 1);
        } else if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.LEFTPAR)) {
            printIndentation(depth + 1);
            System.out.print("<LEFTPAR>");
            match(LexicalAnalyzer.TokenType.LEFTPAR, depth + 2);
            printIndentation(depth + 1);
            System.out.print("<IDENTIFIER>");
            match(LexicalAnalyzer.TokenType.IDENTIFIER, depth + 2);
            ArgList(depth + 1);
            printIndentation(depth + 1);
            System.out.print("<RIGHTPAR>");
            match(LexicalAnalyzer.TokenType.RIGHTPAR, depth + 2);
            Statements(depth + 1);
        } else {
            throw new SyntaxErrorException("Invalid DefinitionRight at position " + getCurrentToken().getLine() + ":" + getCurrentToken().getColumn());
        }
    }


    private void Expressions(int depth) {
        printIndentation(depth);
        System.out.println("<Expressions>");
        if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.IDENTIFIER) || tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.NUMBER) || tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.CHARACTER) ||
                tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.BOOLEAN) || tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.STRING) || tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.LEFTPAR)) {
            Expression(depth + 1);
            Expressions(depth + 1);
        }

    }

    private void ArgList(int depth) {
        printIndentation(depth);
        System.out.println("<ArgList>");
        if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.IDENTIFIER)) {
            printIndentation(depth + 1);
            System.out.print("<IDENTIFIER>");
            match(LexicalAnalyzer.TokenType.IDENTIFIER, depth + 2);
            ArgList(depth + 1);
        }

    }

    private void Statements(int depth) {
        printIndentation(depth);
        System.out.println("<Statements>");
        if (lookahead().equals("DEFINE")) {
            Definition(depth + 1);
            Statements(depth + 1);
        } else {
            Expression(depth + 1);
        }
    }


    private void Expression(int depth) {
        printIndentation(depth);
        System.out.println("<Expression>");
        if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.IDENTIFIER)) {
            printIndentation(depth + 1);
            System.out.print("<IDENTIFIER>");
            match(LexicalAnalyzer.TokenType.IDENTIFIER, depth + 2);
        } else if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.NUMBER)) {
            printIndentation(depth + 1);
            System.out.print("<NUMBER>");
            match(LexicalAnalyzer.TokenType.NUMBER, depth + 2);
        } else if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.CHARACTER)) {
            printIndentation(depth + 1);
            System.out.print("<CHAR>");
            match(LexicalAnalyzer.TokenType.CHARACTER, depth + 2);
        } else if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.BOOLEAN)) {
            printIndentation(depth + 1);
            System.out.print("<BOOLEAN>");
            match(LexicalAnalyzer.TokenType.BOOLEAN, depth + 2);
        } else if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.STRING)) {
            printIndentation(depth + 1);
            System.out.print("<STRING>");
            match(LexicalAnalyzer.TokenType.STRING, depth + 2);
        } else if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.LEFTPAR)) {
            printIndentation(depth + 1);
            System.out.print("<LEFTPAR>");
            match(LexicalAnalyzer.TokenType.LEFTPAR, depth + 2);
            Expr(depth + 1);
            printIndentation(depth + 1);
            System.out.print("<RIGHTPAR>");
            match(LexicalAnalyzer.TokenType.RIGHTPAR, depth + 2);
        } else {
            throw new SyntaxErrorException("Invalid Expression at position " + getCurrentToken().getLine() + ":" + getCurrentToken().getColumn());
        }
    }

    private void Expr(int depth) {
        printIndentation(depth);
        System.out.println("<Expr>");
        if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.IDENTIFIER)){
            FunCall(depth + 1);
        }
        else {
            String lookahead = lookahead();
            switch (lookahead) {
                case "LET":
                    LetExpression(depth + 1);
                    break;
                case "COND":
                    CondExpression(depth + 1);
                    break;
                case "IF":
                    IfExpression(depth + 1);
                    break;
                case "BEGIN":
                    BeginExpression(depth + 1);
                    break;
                case "=":
                    FunCall(depth + 1);
                    break;
                default:
                    throw new SyntaxErrorException("Invalid Expr at position " + getCurrentToken().getLine() + ":" + getCurrentToken().getColumn());
            }
        }

    }


    private void LetExpression(int depth) {
        printIndentation(depth);
        System.out.println("<LetExpression>");
        printIndentation(depth + 1);
        System.out.print("<LET> ");
        match(LexicalAnalyzer.TokenType.KEYWORD, depth + 2);
        LetExpr(depth + 1);
    }

    private void CondExpression(int depth) {
        printIndentation(depth);
        System.out.println("<CondExpression>");
        printIndentation(depth + 1);
        System.out.println("<COND>");
        match(LexicalAnalyzer.TokenType.IDENTIFIER, depth + 2);
        CondBranches(depth + 1);
    }

    private void IfExpression(int depth) {
        printIndentation(depth);
        System.out.println("<IfExpression>");
        printIndentation(depth + 1);
        System.out.print("<IF>");
        match(LexicalAnalyzer.TokenType.KEYWORD, depth + 2);
        Expression(depth + 1);
        Expression(depth + 1);
        EndExpression(depth + 1);
    }

    private void BeginExpression(int depth) {
        printIndentation(depth);
        System.out.println("<BeginExpression>");
        printIndentation(depth + 1);
        System.out.println("<BEGIN>");
        match(LexicalAnalyzer.TokenType.KEYWORD, depth + 2);
        Statements(depth + 1);
    }


    private void LetExpr(int depth) {
        printIndentation(depth);
        System.out.println("<LetExpr>");

        if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.LEFTPAR)) {
            printIndentation(depth + 1);
            System.out.println("<LEFTPAR>");
            match(LexicalAnalyzer.TokenType.LEFTPAR, depth + 2);
            VarDefs(depth + 1);
            printIndentation(depth + 1);
            System.out.println("<RIGHTPAR>");
            match(LexicalAnalyzer.TokenType.RIGHTPAR, depth + 2);
            Statements(depth + 1);
        } else if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.IDENTIFIER)) {
            printIndentation(depth + 1);
            System.out.print("<IDENTIFIER>");
            match(LexicalAnalyzer.TokenType.IDENTIFIER, depth + 2);
            printIndentation(depth + 1);
            System.out.print("<LEFTPAR>");
            match(LexicalAnalyzer.TokenType.LEFTPAR, depth + 2);
            VarDefs(depth + 1);
            printIndentation(depth + 1);
            System.out.print("<RIGHTPAR>");
            match(LexicalAnalyzer.TokenType.RIGHTPAR, depth + 2);
            Statements(depth + 1);
        } else {
            throw new SyntaxErrorException("Invalid LetExpr at position " + getCurrentToken().getLine() + ":" + getCurrentToken().getColumn());
        }
    }

    private void CondBranches(int depth) {
        printIndentation(depth);
        System.out.println("<CondBranches>");

        printIndentation(depth + 1);
        System.out.println("<LEFTPAR>");
        match(LexicalAnalyzer.TokenType.LEFTPAR, depth + 2);
        Expression(depth + 1);
        Statements(depth + 1);
        printIndentation(depth + 1);
        System.out.println("<RIGHTPAR>");
        match(LexicalAnalyzer.TokenType.RIGHTPAR, depth + 2);
        CondBranch(depth + 1);
    }

    private void EndExpression(int depth) {
        printIndentation(depth);
        System.out.println("<EndExpression>");

        if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.IDENTIFIER) || tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.NUMBER) || tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.CHARACTER) ||
                tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.BOOLEAN) || tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.STRING) || tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.LEFTPAR)) {
            Expression(depth + 1);
        }

    }


    private void VarDefs(int depth) {
        printIndentation(depth);
        System.out.println("<VarDefs>");

        printIndentation(depth + 1);
        System.out.print("<LEFTPAR>");
        match(LexicalAnalyzer.TokenType.LEFTPAR, depth + 2);
        printIndentation(depth + 1);
        System.out.print("<IDENTIFIER>");
        match(LexicalAnalyzer.TokenType.IDENTIFIER, depth + 2);
        Expression(depth + 1);
        printIndentation(depth + 1);
        System.out.print("<RIGHTPAR>");
        match(LexicalAnalyzer.TokenType.RIGHTPAR, depth + 2);
        VarDef(depth + 1);
    }

    private void CondBranch(int depth) {
        printIndentation(depth);
        System.out.println("<CondBranch>");

        if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.LEFTPAR)) {
            printIndentation(depth + 1);
            System.out.print("<LEFTPAR>");
            match(LexicalAnalyzer.TokenType.LEFTPAR, depth + 2);
            Expression(depth + 1);
            Statements(depth + 1);
            printIndentation(depth + 1);
            System.out.print("<RIGHTPAR>");
            match(LexicalAnalyzer.TokenType.RIGHTPAR, depth + 2);
        }

    }

    private void VarDef(int depth) {
        printIndentation(depth);
        System.out.println("<VarDef>");

        if (tokens.get(currentTokenIndex).getType().equals(LexicalAnalyzer.TokenType.LEFTPAR)) {
            VarDefs(depth + 1);
        }

    }





    private void match(LexicalAnalyzer.TokenType expected, int depth) {
        Token  token = tokens.get(currentTokenIndex);
        if (token.getType()== LexicalAnalyzer.TokenType.LEFTPAR && token.getType()==expected){
            System.out.println(" (" +ANSI_RED + " ( " + ANSI_RESET + ")");
            currentTokenIndex++;
        }
        else if (token.getType()== LexicalAnalyzer.TokenType.RIGHTPAR && token.getType()==expected ){
            System.out.println(" (" + ANSI_RED + " ) " + ANSI_RESET + ")");
            currentTokenIndex++;
        }
        else if (token.getType().equals(expected)) {
//            printIndentation(depth);
            System.out.println(" (" + ANSI_RED + " " +token.getValue()+ ANSI_RESET + ")");
            currentTokenIndex++;
        } else {
            throw new SyntaxErrorException("Expected " + expected + ", found " + token.getValue() +
                    " at position " + token.getLine() + ":" + token.getColumn());
        }
    }



}

class SyntaxErrorException extends RuntimeException {
    public SyntaxErrorException(String message) {
        super(message);
    }
}
