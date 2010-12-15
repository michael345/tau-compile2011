package IC.SemanticAnalyser;

import java.util.List;

import IC.AST.Type;

public class SemanticType {
    private List<SemanticType> parameterTypes;
    private Type type;
    
    public SemanticType(Type type) {
        this.parameterTypes = null;
        this.type = type;
    }

    public SemanticType(List<SemanticType> parameterTypes, Type type) {
        this.parameterTypes = parameterTypes;
        this.type = type;
    }

    public List<SemanticType> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<SemanticType> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    
}
