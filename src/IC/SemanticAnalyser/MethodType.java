package IC.SemanticAnalyser;

public class MethodType extends Type {
    private Type[] paramTypes;
    private Type returnType;
    
    public MethodType(Type[] paramTypes, Type returnType) {
        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }

    public Type[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Type[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        int i = 0;
        String result = "{";
        for (i = 0; i<paramTypes.length-1; i++) { 
            result += paramTypes[i] + ", ";
        }
        result += paramTypes[i] + " -> " + returnType + "}";// maybe i+1 
        return result;
    }
    
    
    
    
}
