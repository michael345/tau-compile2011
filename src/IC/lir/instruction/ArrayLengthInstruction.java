package IC.lir.instruction;

import IC.lir.parameter.LIROperand;
import IC.lir.parameter.LIRReg;

public class ArrayLengthInstruction extends LIRInstruction {
    LIRReg resultReg;
    LIROperand arrayOrString;
    public ArrayLengthInstruction(LIRReg resultReg, LIROperand arrayOrString) {
        super();
        this.resultReg = resultReg;
        this.arrayOrString = arrayOrString;
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ArrayLength ");
        builder.append(arrayOrString);
        builder.append(",");
        builder.append(resultReg);
        return builder.toString();
    }
    
    
}
