package IC.SemanticAnalyser;

import IC.AST.ICClass;

public class ClassType extends Type {
    ICClass classAST;

    public ClassType(ICClass classAST) {
        this.classAST = classAST;
    }



    @Override
    public String toString() {
        return classAST.getName();
    }
}
