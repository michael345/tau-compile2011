package IC.lir.instruction;

public class EndLabelInstruction extends LabelInstruction {

    public static int counter = 0;


    public EndLabelInstruction() {
        super("end" + counter++);
        
    }
    

}
