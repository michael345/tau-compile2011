package IC.lir.instruction;

import java.util.List;

import IC.lir.parameter.ArgumentPair;
import IC.lir.parameter.LIRImmediate;
import IC.lir.parameter.LIRReg;

public class VirtualCallInstruction extends LIRInstruction {

    LIRReg object;
    LIRImmediate offset;
    LIRReg resultReg;
    List<ArgumentPair> pairs;
    
    public VirtualCallInstruction(LIRReg object, LIRImmediate offset,
            LIRReg resultReg, List<ArgumentPair> pairs) {
        super();
        this.object = object;
        this.offset = offset;
        this.resultReg = resultReg;
        this.pairs = pairs;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VirtualCall ");
        builder.append(object);
        builder.append(".");
        builder.append(offset);
        builder.append("(");
        for (ArgumentPair pair : pairs) { 
            builder.append(pair + ",");
        }
        if (pairs.size() > 0) { 
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append(")");
        builder.append(",");
        builder.append(resultReg);
        return builder.toString();
    }
    
    
    
    
    
}
