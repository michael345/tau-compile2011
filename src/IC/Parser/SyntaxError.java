package IC.Parser;

public class SyntaxError extends Exception {

    private static final long serialVersionUID = 1L;
    private int line;
    private String message;
    private Token token; // might be needed
    private int errorCode; // for switch case maybe later
    
    SyntaxError(int line, Token token, String message) { 
        this.line = line;
        this.message = message;
        this.token = token;
    }
    
    void printErrorMsg() { 
        System.out.println("Line " + line + ": Token " + token.getValue() + " - " + message);
    }
    
}
