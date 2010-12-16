package IC.SemanticAnalyser;

import IC.AST.ICClass;

public class ClassType extends Type {
    ICClass classAST;

    public ClassType(ICClass classAST, int id) {
        super();
        this.classAST = classAST;
        this.id = id;
    }



    @Override
    public String toString() {
            return classAST.getName();
        
    }
    
    public ICClass getICClass() { 
        return classAST;
    }
    
}
