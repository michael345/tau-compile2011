package IC.SemanticAnalyser;

public class ArrayType extends Type {
    Type elemType;
    int dimension;

    public ArrayType(Type elemType, int dimension) {
        this.elemType = elemType;
        this.dimension = dimension;
    }



    @Override
    public String toString() {
        String result = elemType.toString();
        for (int i = dimension; i>0; i--) { 
            result += "[]";
        }
        return result;
    }



    public Type getElemType() {
        return elemType;
    }



    public void setElemType(Type elemType) {
        this.elemType = elemType;
    }



    public int getDimension() {
        return dimension;
    }



    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
    
    
}
