package IC.TYPE;

import java.util.StringTokenizer;

import IC.AST.*;

public abstract class Type {
    int id;
    public abstract String toString();
    public int getID() {return id;}
    public boolean equals(Type other) {
        return (toString().compareTo(other.toString()) == 0);
    }
    
    private static Type stringToType(String type) { 
        if (type.compareTo("int") == 0) { 
            return new IntType(0);
        }
        else if (type.compareTo("boolean") == 0) { 
            return new BoolType(0);
        }
        else if (type.compareTo("string") == 0) { 
           return new StringType(0);
        }
        else if (type.compareTo("void") == 0) { 
            return new VoidType(0);
        }
        else if (type.compareTo("null") == 0) { 
            return new NullType(0);
        }
        else { 
            return TypeTable.getClassType(type); // if this is null, class 'type' was never defined and added to type table.
        }
    }
    
    public static Type getArrayType(NewArray node) { 
        Type t  = stringToType(node.getType().getName());
        
        int dim = node.getType().getDimension(); //TODO: WTF!? dim == 0?!
        Type temp = TypeTable.arrayType(t);

        for (int i = 0; i < dim; i++) { //TODO: Maybe dim-1 ?
            temp = TypeTable.arrayType(temp);
        }
        return temp;
    }
    
    public static boolean isInt(ASTNode node) {
        return (node.getSemanticType() instanceof IntType);
    }
    
    public static boolean isBool(ASTNode node) {
        return (node.getSemanticType() instanceof BoolType);
    }
    
    public static boolean isNull(ASTNode node) {
        return (node.getSemanticType() instanceof NullType);
    }
    
    public static boolean isString(ASTNode node) {
        return (node.getSemanticType() instanceof StringType);
    }
    
    public static boolean isVoid(ASTNode node) {
        return (node.getSemanticType() instanceof VoidType);
    }
    
}
