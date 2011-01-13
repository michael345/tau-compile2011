package IC.lir.parameter;

public class LIRReg extends LIROperand {
    private int registerID;
    private final static int[] auxilary = new int[64];
    private static int registerCount = 0;
    
    public LIRReg() {
        super();
        assignFreeReg();
    }
    
    public int assignFreeReg() { 
        for (int i = 0; i < auxilary.length; i++) { 
            if (auxilary[i] == 0) { 
                auxilary[i] = 1;
                increment();
                registerID = i;
                return i;
            }
        }
        return -1; // couldnt find free register
    }
    
    public void makeFreeRegister() { 
        int index = this.registerID;
        if (index >= auxilary.length) {
            return;
        } 
        else if (auxilary[index] == 0) { 
            return; 
        }
        else { 
            auxilary[index] = 0;
           // registerID = -1;
        }
        decrement();
    }
    
    public String toString() { 
        return "R" + registerID;
    }
    
    public void increment() { registerCount++;}
    public void decrement() {
        if (registerCount - 1 < 0) { 
            System.out.println("Error: Can't decrement register.");
            System.exit(-1);
        }
        registerCount--;
    }

    public static void printUsedRegisters() { 
        System.out.println("Used registers are:");
        for (int i= 0; i < auxilary.length; i++) { 
            if (auxilary[i] != 0) { 
                System.out.println("R" + i + "\n");
            }
        }
        
    }
    
    public int getID() { 
        return registerID;
    }
}
