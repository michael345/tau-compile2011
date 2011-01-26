package IC.lir.parameter;

import IC.SymbolTables.SemanticSymbol;

public class LIRMemory extends LIROperand {

    private SemanticSymbol symbol;

    public LIRMemory(SemanticSymbol symbol) {
        super();
        this.symbol = symbol;
    }
    
    public String getName() { 
        return symbol.getId();
    }
    
    public String toString() { 
        return getName() + symbol.getUniqueID();
    }
    
}
