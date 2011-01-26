package IC.lir.instruction;

public class LoopLabelInstruction extends LabelInstruction {
    private static int counter = 0;

    public LoopLabelInstruction() {
        super("loop" + counter++);
    }
    
    
}
