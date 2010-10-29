package IC.Parser;

import java_cup.runtime.Symbol;

public class Token extends Symbol {
    
    final private int id;
    final private int line;
    final private String value;
    
    public Token(int id, int line, String value) {
        super(id, null);
        this.id = id;
        this.line = line;
        this.value = value;
    }
    
    public Token(int id, int line) {
        super(id, null);
        this.id = id;
        this.line = line;
        value = null;
    }
    public int getId() {
        return id;
    }

    public int getLine() {
        return line;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" + line + ": " + id + "(" + value + ")";   
    }
    
    
    
}

