package IC.lir.instruction;

public enum BinaryInstrucionEnum {
    ADD("Add"),
    SUB("Sub"),
    MUL("Mul"),
    DIV("Div"),
    MOD("Mod"),
    AND("And"),
    OR("Or"),
    XOR("Xor"),
    COMPARE("Compare");
    
    
    private String name;

    private BinaryInstrucionEnum(String name) { 
        this.name = name;
    }

    public String toString() { 
        return name;
    }
    
}
