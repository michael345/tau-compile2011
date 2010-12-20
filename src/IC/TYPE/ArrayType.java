package IC.TYPE;

public class ArrayType extends Type {
    Type elemType;
    
    public ArrayType(Type elemType, int id) {
        super();
        this.elemType = elemType;
        this.id = id;
    }


    public String toString() { // surprisingly this actually works
        Type tempType = elemType;
        ArrayType tempArrayType;
        String extraBrackets = "";
        String baseType = "";
        while (tempType instanceof ArrayType) {
            extraBrackets += "[]";
            tempArrayType = (ArrayType) tempType;
            tempType = tempArrayType.getElemType();
        }
        baseType = tempType.toString();
        return baseType + "[]" + extraBrackets;
    }
    

    public Type getElemType() {
        return elemType;
    }

    public void setElemType(Type elemType) {
        this.elemType = elemType;
    }

    
}
