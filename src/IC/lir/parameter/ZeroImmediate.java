package IC.lir.parameter;

public class ZeroImmediate extends LIRImmediate {

    private static ZeroImmediate instance; 
    
    private ZeroImmediate() {
        super(0);
    }
    
     public static ZeroImmediate getInstance() { 
        if (instance == null) { 
            instance = new ZeroImmediate();
        }
        return instance;
    }
     
     public String toString() { 
         return "0";
     }
    
}
