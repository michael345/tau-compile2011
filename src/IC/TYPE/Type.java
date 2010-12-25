package IC.TYPE;

import IC.AST.ASTNode;

public abstract class Type {
    int id;
    public abstract String toString();
    public int getID() {return id;}
    public boolean equals(Type other) {
        return (toString().compareTo(other.toString()) == 0);
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
