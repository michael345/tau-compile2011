package IC.lir.instruction;

public enum UnaryInstructionEnum {
    NOT("Not"),
    DEC("Dec"),
    INC("Inc"),
    NEG("Neg");
    
    private String name;

    private UnaryInstructionEnum(String name) { 
        this.name = name;
    }

    public String toString() { 
        return name;
    }
    
    
}
