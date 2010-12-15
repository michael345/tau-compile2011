package SemanticAnalyser;

import java.util.List;

public class SemanticType {
    private List<SemanticType> parameterTypes;
    private SemanticType type;
    
    public SemanticType(SemanticType type) {
        this.parameterTypes = null;
        this.type = type;
    }

    public SemanticType(List<SemanticType> parameterTypes, SemanticType type) {
        this.parameterTypes = parameterTypes;
        this.type = type;
    }

    public List<SemanticType> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<SemanticType> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public SemanticType getType() {
        return type;
    }

    public void setType(SemanticType type) {
        this.type = type;
    }
    
    
}
