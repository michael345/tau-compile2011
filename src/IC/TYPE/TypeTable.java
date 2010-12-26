package IC.TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import IC.AST.ICClass;

public class TypeTable {
    private static int idIndex = 1; // moved from TypeTableConstructor
    private static Map<Type,ArrayType> uniqueArrayTypes = new HashMap<Type,ArrayType>();
    private static Map<String,ClassType> uniqueClassTypes = new HashMap<String,ClassType>();
    private static Set<MethodType> uniqueMethodTypes = new HashSet<MethodType>();
    private static List<Type> uniquePrimitiveTypes = new ArrayList<Type>();
    private static Map<String,Integer> nameToID = new HashMap<String,Integer>();
    
    public static Type boolType;  
    public static Type intType;
    public static Type voidType;
    public static Type stringType;
    public static Type nullType;   
    
    public static Type primitiveType(Type t) { 
        return t;
    }
    public static Type primitiveType(IntType t) { 
        if (!uniquePrimitiveTypes.contains(intType)) {
            intType = new IntType(idIndex++);
            uniquePrimitiveTypes.add(intType);
        }    
        return intType;
    }
    
    public static Type primitiveType(VoidType t) { 
        if (!uniquePrimitiveTypes.contains(voidType)) {
            voidType = new VoidType(idIndex++);
            uniquePrimitiveTypes.add(voidType);
        }    
        return voidType;
    }
    
    public static Type primitiveType(NullType t) { 
        if (!uniquePrimitiveTypes.contains(nullType)) {
            nullType = new NullType(idIndex++);
            uniquePrimitiveTypes.add(nullType);
        }    
        return nullType;
    }
    
    public static  Type primitiveType(StringType t) { 
        if (!uniquePrimitiveTypes.contains(stringType)) {
            stringType = new StringType(idIndex++);
            uniquePrimitiveTypes.add(stringType);
        }    
        return stringType;
    }
    
    public static  Type primitiveType(BoolType t) { 
        if (!uniquePrimitiveTypes.contains(boolType)) {
            boolType = new BoolType(idIndex++);
            uniquePrimitiveTypes.add(boolType);
        }    
        return boolType;
    }
    
    public static MethodType methodType(Type[] paramTypes, Type returnType) { 
        MethodType temp = new MethodType(paramTypes,returnType,idIndex);
        String str = temp.toString();
        for (MethodType mt : uniqueMethodTypes) { 
            if (mt.toString().compareTo(str) == 0)  {
                return temp;
            }
        }
        uniqueMethodTypes.add(temp);
        idIndex++;
        return temp;
    }
    
    public static ClassType classType(ICClass classAST) {
        if (uniqueClassTypes.containsKey(classAST.getName())) { 
            return uniqueClassTypes.get(classAST.getName());
        }
        else { 
            ClassType ctype = new ClassType(classAST, idIndex);
            uniqueClassTypes.put(classAST.getName(),ctype);
            nameToID.put(classAST.getName(), idIndex);
            idIndex++;
            return ctype;
        }
        
    }
    
    public static ClassType getClassType(String className) { 
        if (uniqueClassTypes.containsKey(className)) { 
            return uniqueClassTypes.get(className);
        }
        else return null;
    }
    
    public static int getIDByName(String name) { 
        if (nameToID.containsKey(name)) { 
            return nameToID.get(name);
        }
        else return -1;
    }
   
    public static ArrayType arrayType(Type elemType) {
        String str = elemType.toString();
        for (Type ty : uniqueArrayTypes.keySet()) { 
            if (ty.toString().compareTo(str) == 0) { 
                return uniqueArrayTypes.get(ty); 
            }
        }   
        ArrayType arrt = new ArrayType(elemType,idIndex);
        uniqueArrayTypes.put(elemType,arrt);
        idIndex++;
        return arrt;
    }



    public static void printTable() {
       for (Type prim : uniquePrimitiveTypes) {
           System.out.println(prim.getID() + ": Primitive type: " + prim.toString());
       }
       for (ClassType ctype : uniqueClassTypes.values()) {
           if (ctype.getICClass().hasSuperClass()) { 
               System.out.println(ctype.getID() + ": Class: " + ctype.toString() + ", Superclass ID: " + nameToID.get(ctype.getICClass().getSuperClassName()));
           }
           else {

           System.out.println(ctype.getID() + ": Class: " + ctype.toString());
           }
       }
       for (ArrayType atype : uniqueArrayTypes.values()) { 
           System.out.println(atype.getID() + ": Array type: " + atype.toString());
       }
       for (MethodType mtype : uniqueMethodTypes) { 
           System.out.println(mtype.getID() + ": Method type: " + mtype.toString());
       }
        
    }
    /** 
     * Check if subtype is a subtype of type, according to ic specification
     * **/
    public static boolean isSubTypeOf(Type subtype, Type type) { 
        if (subtype == nullType) return true;
        else if (type == nullType) return false;
        else if (subtype.equals(type)) return true;
        else if (!(subtype instanceof ClassType) || !(type instanceof ClassType)) return false; 
        else {
            ClassType subClassType = (ClassType) subtype;
            ClassType classType = (ClassType) type;
            ClassType temp = subClassType;

            while (temp != null) {
                if (temp.getICClass().getSemanticType() == classType) { //TODO: MUST check this
                    return true;
                }
                temp = uniqueClassTypes.get(subClassType.getICClass().getSuperClassName());
            }
            return false;
        }
    }
    
    public static boolean isLegalHeirarchy() { 
        ClassType temp;
        for (ClassType c : uniqueClassTypes.values()) { 
            temp = uniqueClassTypes.get(c.getICClass().getSuperClassName());
            while (temp != null) { 
                if (c.getICClass().getName() == temp.getICClass().getName())
                    return false;
                temp = uniqueClassTypes.get(temp.getICClass().getSuperClassName());
            }
        }
        return true;
        
    }
    public static Type returnElemType(Type semanticType) {
        if (semanticType instanceof ArrayType) { 
            return ((ArrayType) semanticType).getElemType();
        }
        else {
            return null;
        }
   }
}

