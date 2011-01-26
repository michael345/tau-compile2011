package IC.lir.parameter;

public class LIRDummyReg extends LIRReg {
    
    private static LIRDummyReg instance;
    
    public static LIRDummyReg getInstance() { 
        
        if (instance == null) { 
            instance = new LIRDummyReg();
            instance.makeFreeRegister();
        }
        return instance;
    }
    
    private LIRDummyReg() { 
        super();
    }
    
    
    
    public String toString() { 
        return "Rdummy";
    }
}
