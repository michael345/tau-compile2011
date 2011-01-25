package IC.lir.parameter;

public class LIRString extends LIRParameter {
    private LIRStringLabel label;
    private String text;
    private static int stringCounter = 0;

    public LIRString(String text) {
        super();
        this.label = new LIRStringLabel("str" + stringCounter++);
        this.text = text;
        
    }

    public LIRStringLabel getLabel() { 
        return label;
    }
    
    public String getName() {
        return label.toString();
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
        return getName() + ": " + text;
    }
    
    
    
    
    
    
    
}
