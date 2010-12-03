package IC.Parser;

public class SyntaxError extends Exception {

    private static final long serialVersionUID = 1L;
    private String message;
    private Token token; // might be needed
    private int errorCode; // for switch case maybe later
    
    SyntaxError(Token token, String message) { 
        this.message = message;
        this.token = token;
    }
    
    void printErrorMsg() { 
        System.out.println("Line " + token.getLine() + ": Token " + token.getValue() + " - " + message);
    }
    
}
