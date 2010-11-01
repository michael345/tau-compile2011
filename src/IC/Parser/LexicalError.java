package IC.Parser;

public class LexicalError extends Exception
{
    private int line;
    private String message;
    private static final long serialVersionUID = 1L;
    
    public LexicalError(String message) {
        super(message);
        this.message = message;
        this.line = 0;
    }

    public LexicalError(int line,String message) {
        super(message);
        this.message = message;
        this.line = line;
    }
    
    public void printMessage() { 
        System.out.println("Line " + line + ": " + message);
    }
    
    
}

