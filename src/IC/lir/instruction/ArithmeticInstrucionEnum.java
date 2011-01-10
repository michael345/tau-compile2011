package IC.lir.instruction;

public enum ArithmeticInstrucionEnum {
    ADD("Add"),
    SUB("Sub"),
    MUL("Mul"),
    DIV("Div"),
    MOD("Mod"),
    INT("Int"),
    DEC("Dec"),
    NEG("Neg");
    
    private String name;

    private ArithmeticInstrucionEnum(String name) { 
        this.name = name;
    }

    public String toString() { 
        return name;
    }
    
}
