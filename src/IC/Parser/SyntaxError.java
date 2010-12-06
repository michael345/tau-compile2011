package IC.Parser;

public class SyntaxError extends Exception {

    private static final long serialVersionUID = 1L;
    private String message;
    private Token token;
    private int errorCode; // for switch case maybe later
    

    SyntaxError(Token token) { 

        this.message = message;
        this.token = token;
    }
   
    
    void printErrorMsg() { 

        System.out.println("Line " + token.getLine() + ", Problem on Token " + ": " + Utils.tokenIDToString(token.getId()));

    }
}

