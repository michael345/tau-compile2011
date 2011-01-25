package IC.lir.instruction;

import java.util.LinkedList;
import java.util.List;

import IC.lir.parameter.LIRParameter;
import IC.lir.parameter.LIRReg;

public class LibraryInstruction extends LIRInstruction {
    private String funcName;
    private LIRParameter [] params;
    private LIRReg destination;

    
    public LibraryInstruction(String funcName,LIRReg destination,  LIRParameter ... params) {
        super();
        this.funcName = funcName;
        this.params = params;
        this.destination = destination;
    }

    public String toString() { 
        int size = params.length;
        StringBuffer output = new StringBuffer("Library __");
        output.append(funcName + "(");
        for (int i = 0; i < size - 1; i++) { 
            output.append(params[i].toString() + ",");
        }
        output.append(params[size-1].toString() + ")," + destination);
        return output.toString();
    }
}
