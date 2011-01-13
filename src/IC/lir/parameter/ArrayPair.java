package IC.lir.parameter;

public class ArrayPair {
    LIRReg location;
    LIROperand index;
    
    public ArrayPair(LIRReg location, LIROperand index) {
        super();
        this.location = location;
        this.index = index;
    }
    
    public String toString() {
        return location + "[" + index + "]";
    }

    public LIRReg getLocation() {
        return location;
    }

    public void setLocation(LIRReg location) {
        this.location = location;
    }

    public LIROperand getIndex() {
        return index;
    }

    public void setIndex(LIROperand index) {
        this.index = index;
    }
    
    public void free() {
        location.makeFreeRegister();
        if (index instanceof LIRReg) { 
            ((LIRReg) index).makeFreeRegister();
        }
    }
}
