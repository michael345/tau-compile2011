package IC.lir.instruction;

public class ConditionLabelInstruction extends LabelInstruction {
    private static int counter = 0;
    String name;
    public ConditionLabelInstruction() {
        super("cond" + counter++);
    }
    
    
    
    
    
    

}
