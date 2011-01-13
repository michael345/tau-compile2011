package IC.lir.instruction;

import IC.lir.parameter.*;

public class MoveInstruction extends DataTransferInstruction{
    LIROperand op1;
    LIROperand op2;
    
    
    public MoveInstruction(LIRReg op1, LIRReg op2) { 
        super();
        this.op1 = op1;
        this.op2 = op2;
    }
    
    public MoveInstruction(LIRMemory op1, LIRReg op2) { 
        super();
        this.op1 = op1;
        this.op2 = op2;
    }
    public MoveInstruction(LIRReg op1, LIRMemory op2) { 
        super();
        this.op1 = op1;
        this.op2 = op2;
    }
    
    public MoveInstruction(LIRImmediate op1, LIRMemory op2) { 
        super();
        this.op1 = op1;
        this.op2 = op2;
    }

    
    public MoveInstruction(LIROperand op1, LIROperand op2) {
        super();
        this.op1 = op1;
        this.op2 = op2;
    }

    public String toString() { 
        return "Move " + op1 + "," + op2;
    }
       
}
