package IC.AST;

import java.util.ArrayList;
import java.util.List;

public class ClassContents {
    List<Field> fields;
    List<Method> methods;
    
    public ClassContents(int line) {
        this.fields = new ArrayList<Field>();
        this.methods = new ArrayList<Method>();
    }
    
    public List<Field> getFields() {
        return fields;
    }
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
    public List<Method> getMethods() {
        return methods;
    }
    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    
    
    
}
