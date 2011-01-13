package IC.lir.instruction;

public class LabelInstruction extends LIRInstruction {
    String name;
    public String toString() { 
        return name + ":";
    }
}
