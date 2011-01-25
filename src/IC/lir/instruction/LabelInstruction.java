package IC.lir.instruction;

public class LabelInstruction extends LIRInstruction {
    String name;
    
    
    
    public LabelInstruction(String name) {
        super();
        this.name = "_" + name;
    }

    public String toString() { 
        return name + ":";
    }
    
    public String getLabel() { 
        return name;
    }
    
    
}
