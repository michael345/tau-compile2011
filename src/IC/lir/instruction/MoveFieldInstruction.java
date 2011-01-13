package IC.lir.instruction;

import IC.lir.parameter.FieldPair;
import IC.lir.parameter.LIROperand;

public class MoveFieldInstruction extends DataTransferInstruction {
    FieldPair fieldPair;
    LIROperand data;
    boolean isStore; // if false ==> isLoad
    
    public MoveFieldInstruction(FieldPair fieldPair, LIROperand data, boolean isStore) {
        super();
        this.fieldPair = fieldPair;
        this.data = data;
        this.isStore = isStore;
    }


    public String toString() { 
        if (isStore) { 
            return "MoveField " + data + "," + fieldPair;
        }
        else { 
            return "MoveField " + fieldPair + "," + data; 
         }
    }
    
}
