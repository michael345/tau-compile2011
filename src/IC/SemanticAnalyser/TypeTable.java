package IC.SemanticAnalyser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import IC.AST.ICClass;

public class TypeTable {
    private Map<Type,ArrayType> uniqueArrayTypes;
    private Map<String,ClassType> uniqueClassTypes;
    private Set<MethodType> uniqueMethodTypes;
    private List<Type> uniquePrimitiveTypes;
    
    public static Type boolType = new BoolType(); // not sure about the instantiation
    public static Type intType = new IntType(); // not sure about the instantiation 
    public static Type voidType = new VoidType(); // not sure about the instantiation 
    public static Type stringType = new StringType();// not sure about the instantiation
    public static Type nullType = new NullType();// not sure about the instantiation
    
    
    public TypeTable() { 
        uniqueArrayTypes = new HashMap<Type,ArrayType>();
        uniqueClassTypes = new HashMap<String,ClassType>();
        uniqueMethodTypes = new TreeSet<MethodType>();
        uniquePrimitiveTypes = new ArrayList<Type>();
     }
    
    public Type primitiveType(Type t) { 
        if (uniquePrimitiveTypes.contains(t))
            ;
        else { 
            uniquePrimitiveTypes.add(t);
        }
        return t;
    }
    
    public MethodType methodType(Type[] paramTypes, Type returnType) { 
        MethodType temp = new MethodType(paramTypes,returnType);
        uniqueMethodTypes.add(temp);
        return temp;
    }
    
    public ClassType classType(ICClass classAST) {
        if (uniqueClassTypes.containsKey(classAST.getName())) { 
            return uniqueClassTypes.get(classAST.getName());
        }
        else { 
            ClassType ctype = new ClassType(classAST);
            uniqueClassTypes.put(classAST.getName(),ctype);
            return ctype;
        }
        
    }
   
    public ArrayType arrayType(Type elemType) {
        if (uniqueArrayTypes.containsKey(elemType)) { // array type object already created – return it
            return uniqueArrayTypes.get(elemType);
        }
        else {  // object doesn’t exist – create and return it        
            ArrayType arrt = new ArrayType(elemType);
            uniqueArrayTypes.put(elemType,arrt);
            return arrt;
            }
        }


    public void printTable() {
       int i = 1;
       for (Type prim : uniquePrimitiveTypes) {
           System.out.println(i++ + ": " + prim.toString());
       }
       for (ClassType ctype : uniqueClassTypes.values()) {
           System.out.println(i++ + ": " + ctype.toString());
       }
       for (ArrayType atype : uniqueArrayTypes.values()) { 
           System.out.println(i++ + ": " + atype.toString());
       }
       for (MethodType mtype : uniqueMethodTypes) { 
           System.out.println(i++ + ": " + mtype.toString());
       }
        
    }
}

