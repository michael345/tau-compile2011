package IC.lir.instruction;

public enum LogicalInstructionEnum {
    NOT("Not"),
    AND("And"),
    OR("Or"),
    XOR("Xor"),
    COMPARE("Compare");
    
    private String name;

    private LogicalInstructionEnum(String name) { 
        this.name = name;
    }

    public String toString() { 
        return name;
    }
    
    
}
