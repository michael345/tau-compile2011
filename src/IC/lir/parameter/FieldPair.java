package IC.lir.parameter;

public class FieldPair {
    LIRReg location;
    LIROperand fieldOffset;
    
    public FieldPair(LIRReg location, LIROperand fieldOffset) {
        super();
        this.location = location;
        this.fieldOffset = fieldOffset;
    }
    
    public String toString() {
        return location + "." + fieldOffset;
    }

    public LIRReg getLocation() {
        return location;
    }

    public void setLocation(LIRReg location) {
        this.location = location;
    }

    public LIROperand getFieldOffset() {
        return fieldOffset;
    }

    public void setFieldOffset(LIROperand fieldOffset) {
        this.fieldOffset = fieldOffset;
    }
    
    public void free() { 
        location.makeFreeRegister();
    }
    
    
    
    
}
