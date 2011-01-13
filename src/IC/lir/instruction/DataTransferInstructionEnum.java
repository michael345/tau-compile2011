package IC.lir.instruction;

public enum DataTransferInstructionEnum {
    MOVE("Move"),
    MOVEARRAY("MoveArray"),
    MOVEFIELD("MoveField"),
    ARRAYLENGTH("ArrayLength");
    
    private String name;

    private DataTransferInstructionEnum(String name) { 
        this.name = name;
    }

    public String toString() { 
        return name;
    }
}
