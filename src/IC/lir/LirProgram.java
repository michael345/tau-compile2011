package IC.lir;
import java.util.LinkedList;
import java.util.List;

import IC.lir.instruction.LIRInstruction;
import IC.lir.parameter.LIRReg;
import IC.lir.parameter.LIRString;
/**
 * 
 * This class is an object representation of the Lir Program having been translated from IC to LIR.
 * Singleton!
 */

public class LirProgram { 
    
   

    private static LirProgram program;
    
    private LirProgram() {}
    
    public static LirProgram getInstance() { 
        if (program == null) { 
            program = new LirProgram();
            program.dispatchTables = new LinkedList<LIRDispatchTable>();
            program.instructions = new LinkedList<LIRInstruction>();
            program.stringLiterals = new LinkedList<LIRString>();
            program.classLayouts = new LinkedList<ClassLayout>();
        }
        return program;
        
    }
    
    List<ClassLayout> classLayouts;
    
    List<LIRString> stringLiterals; // TODO: maybe use a map that maps from string_name to actual text?
    List<LIRDispatchTable> dispatchTables; // TODO: also, maybe make it a map instead of list
    List<LIRInstruction> instructions;

    public List<LIRString> getStringLiterals() {
        return stringLiterals;
    }

    public void setStringLiterals(List<LIRString> stringLiterals) {
        this.stringLiterals = stringLiterals;
    }

    public List<LIRDispatchTable> getDispatchTables() {
        return dispatchTables;
    }

    public void setDispatchTables(List<LIRDispatchTable> dispatchTables) {
        this.dispatchTables = dispatchTables;
    }

    public List<LIRInstruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<LIRInstruction> instructions) {
        this.instructions = instructions;
    }

    public void addInstruction(LIRInstruction inst) {
        this.instructions.add(inst);
    }
    
    public void addDispatchTable(LIRDispatchTable dis) { 
        this.dispatchTables.add(dis);
    }
    
    public void addStringLiteral(LIRString str) {
        this.stringLiterals.add(str);
    }
    
    public List<ClassLayout> getClassLayouts() {
        return classLayouts;
    }

    public void setClassLayouts(List<ClassLayout> classLayouts) {
        this.classLayouts = classLayouts;
    }
    
    public ClassLayout getClassLayout(String className) { 
        for (ClassLayout classLayout : classLayouts) { 
            if (className.compareTo(classLayout.getName()) == 0) { 
                return classLayout;
            }
        }
        return null;
    }
    
    public String toString() { 
        StringBuffer result = new StringBuffer("");
        for (LIRString stringLiteral : stringLiterals) { 
            result.append(stringLiteral + "\n");
        }
        
        for (LIRDispatchTable dispatch : dispatchTables) { 
            result.append(dispatch + "\n");
        }
        
        for (LIRInstruction inst : instructions) { 
            result.append(inst + "\n");
        }
        
        LIRReg.printUsedRegisters();
        
        return result.toString();
        
    }
}
