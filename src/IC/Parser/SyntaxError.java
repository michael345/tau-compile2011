package IC.Parser;

public class SyntaxError extends Exception {

    private static final long serialVersionUID = 1L;
    private Token token;
   
    

    SyntaxError(Token token) { 
        this.token = token;
    }
   
    
    public void printErrorMsg() { 
        System.out.println("Syntax error discoverd on file! Line " + token.getLine() + ", Problem on Token " + ": " + Utils.tokenIDToString(token.getId()));
    }
}

