package IC.lir.instruction;

import IC.lir.parameter.*;

public class MoveInstruction extends DataTransferInstruction{
    LIROperand op1;
    LIROperand op2;
    
    
    public MoveInstruction(LIRReg op1, LIRReg dest) { 
        super();
        this.op1 = op1;
        this.op2 = dest;
    }
    
    public MoveInstruction(LIRMemory op1, LIRReg dest) { 
        super();
        this.op1 = op1;
        this.op2 = dest;
    }
    public MoveInstruction(LIRReg op1, LIRMemory dest) { 
        super();
        this.op1 = op1;
        this.op2 = dest;
    }
    
    public MoveInstruction(LIRImmediate op1, LIRMemory dest) { 
        super();
        this.op1 = op1;
        this.op2 = dest;
    }

    
    public MoveInstruction(LIROperand op1, LIROperand dest) {
        super();
        this.op1 = op1;
        this.op2 = dest;
    }

    public String toString() { 
        return "Move " + op1 + "," + op2;
    }
       
}
