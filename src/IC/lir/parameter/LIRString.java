package IC.lir.parameter;

public class LIRString extends LIRParameter {
    private String name;
    private String text;
    private static int stringCounter = 0;

    public LIRString(String text) {
        super();
        this.name = "str" + stringCounter++;
        this.text = text;
        
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }


    public static int getStringCounter() {
        return stringCounter;
    }

    public static void setStringCounter(int stringCounter) {
        LIRString.stringCounter = stringCounter;
    }
    
    public String toString() { 
        return name + ": " + text;
    }
    
    
    
    
    
    
    
}
