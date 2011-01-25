package IC.lir.instruction;

import IC.lir.parameter.LIRLabel;

public class JumpInstruction extends ControlTransferInstruction {
    JumpInstructionEnum func;
    String label;
    
    
    
    public JumpInstruction(JumpInstructionEnum func, String label) {
        super();
        this.func = func;
        this.label = label;
    }



    public String toString() { 
        return "Jump" +  func + " " + label;
    }
}
