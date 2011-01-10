package IC.lir.instruction;

public enum JumpInstructionEnum {

    Unconditional(""),
    True("True"),
    False("False"),
    Greater("G"),
    GreaterEqual("GE"),
    Less("L"),
    LessEqual("LE");
    
    private String name;

    private JumpInstructionEnum(String name) { 
        this.name = name;
    }

    public String toString() { 
        return name;
    }
    
    
    
}
