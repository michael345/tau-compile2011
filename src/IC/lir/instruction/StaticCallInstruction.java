package IC.lir.instruction;

import java.util.List;

import IC.lir.parameter.ArgumentPair;
import IC.lir.parameter.LIRLabel;
import IC.lir.parameter.LIRReg;

public class StaticCallInstruction extends LIRInstruction {
    private List<ArgumentPair> pairs;
    private String funcName; //funcName
    private LIRReg resultReg;
    
    public StaticCallInstruction(String funcName, LIRReg resultReg, List<ArgumentPair> pairs) {
        super();
        this.funcName = funcName;
        this.resultReg = resultReg;
        this.pairs = pairs;
    }

    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StaticCall ");
        builder.append(funcName);
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
