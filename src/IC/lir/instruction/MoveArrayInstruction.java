package IC.lir.instruction;

import IC.lir.parameter.ArrayPair;
import IC.lir.parameter.LIROperand;

public class MoveArrayInstruction extends DataTransferInstruction {
    ArrayPair arrayPair;
    LIROperand data;
    boolean isStore; // if false ==> isLoad
    
    public MoveArrayInstruction(ArrayPair arrayPair, LIROperand data, boolean isStore) {
        super();
        this.arrayPair = arrayPair;
        this.data = data;
        this.isStore = isStore;
    }


    public String toString() { 
        if (isStore) { 
            return "MoveArray " + data + "," + arrayPair;
        }
        else { 
            return "MoveArray " + arrayPair + "," + data; 
         }
    }
}
