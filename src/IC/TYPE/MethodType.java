package IC.TYPE;

public class MethodType extends Type {
    private Type[] paramTypes;
    private Type returnType;
    
    public MethodType(Type[] paramTypes, Type returnType, int id) {
        super();
        this.paramTypes = paramTypes;
        this.returnType = returnType;
        this.id = id;
    }
    
    public boolean equals(MethodType mt) { 
        return (mt.toString().compareTo(toString()) == 0);
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
        if (paramTypes == null) 
            return result + " -> " + returnType + "}";
        for (i = 0; i<paramTypes.length-1; i++) { 
            result += paramTypes[i] + ", ";
        }
        result += paramTypes[i] + " -> " + returnType + "}";// maybe i+1 
        return result;
    }
    
    
    
    
}
