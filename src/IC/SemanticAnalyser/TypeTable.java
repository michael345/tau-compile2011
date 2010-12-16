package IC.SemanticAnalyser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import IC.AST.ICClass;

public class TypeTable {
    private int idIndex; // moved from TypeTableConstructor
    private Map<Type,ArrayType> uniqueArrayTypes;
    private Map<String,ClassType> uniqueClassTypes;
    private Set<MethodType> uniqueMethodTypes;
    private List<Type> uniquePrimitiveTypes;
    private Map<String,Integer> nameToID;
    
    public Type boolType;
    public Type intType; 
    public Type voidType; 
    public Type stringType;
    public Type nullType;
    
    
    public TypeTable() { 
        uniqueArrayTypes = new HashMap<Type,ArrayType>();
        uniqueClassTypes = new HashMap<String,ClassType>();
        uniqueMethodTypes = new HashSet<MethodType>();
        uniquePrimitiveTypes = new ArrayList<Type>();
        nameToID = new HashMap<String,Integer>();
        idIndex = 1;
     }
    
    
    public Type primitiveType(Type t) { 
        return t;
    }
    public Type primitiveType(IntType t) { 
        if (intType == null) { 
            intType = new IntType(idIndex++);
            uniquePrimitiveTypes.add(intType);
        }
        return t;
    }
    
    public Type primitiveType(VoidType t) { 
        if (voidType == null) { 
            voidType = new IntType(idIndex++);
            uniquePrimitiveTypes.add(voidType);
        }
        return t;
    }
    
    public Type primitiveType(NullType t) { 
        if (nullType == null) { 
            nullType = new NullType(idIndex++);
            uniquePrimitiveTypes.add(nullType);
        }
        return t;
    }
    
    public Type primitiveType(StringType t) { 
        if (stringType == null) { 
            stringType = new StringType(idIndex++);
            uniquePrimitiveTypes.add(stringType);
        }
        return stringType;
    }
    
    public Type primitiveType(BoolType t) { 
        if (boolType == null) { 
            boolType = new BoolType(idIndex++);
            uniquePrimitiveTypes.add(boolType);
        }
        return boolType;
    }
    
    
            
   
    
    public MethodType methodType(Type[] paramTypes, Type returnType) { 
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
    
    public ClassType classType(ICClass classAST) {
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
    
    public ClassType getClassType(String className) { 
        if (uniqueClassTypes.containsKey(className)) { 
            return uniqueClassTypes.get(className);
        }
        else return null;
    }
    
    public int getIDByName(String name) { 
        if (nameToID.containsKey(name)) { 
            return nameToID.get(name);
        }
        else return -1;
    }
   
    public ArrayType arrayType(Type elemType) {
        if (uniqueArrayTypes.containsKey(elemType)) { // array type object already created – return it
            return uniqueArrayTypes.get(elemType);
        }
        else {  // object doesn’t exist – create and return it        
            ArrayType arrt = new ArrayType(elemType,idIndex);
            uniqueArrayTypes.put(elemType,arrt);
            idIndex++;
            return arrt;
            }
        }


    public void printTable() {
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
    
    public boolean isLegalHeirarchy() { 
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
}

