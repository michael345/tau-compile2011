package IC.SemanticChecks;

import java.util.Collection;
import java.util.List;

import IC.AST.ASTNode;
import IC.AST.ArrayLocation;
import IC.AST.Assignment;
import IC.AST.Break;
import IC.AST.Call;
import IC.AST.CallStatement;
import IC.AST.Continue;
import IC.AST.Expression;
import IC.AST.ExpressionBlock;
import IC.AST.Field;
import IC.AST.Formal;
import IC.AST.ICClass;
import IC.AST.If;
import IC.AST.Length;
import IC.AST.LibraryMethod;
import IC.AST.Literal;
import IC.AST.LocalVariable;
import IC.AST.LogicalBinaryOp;
import IC.AST.LogicalUnaryOp;
import IC.AST.MathBinaryOp;
import IC.AST.MathUnaryOp;
import IC.AST.Method;
import IC.AST.NewArray;
import IC.AST.NewClass;
import IC.AST.PrimitiveType;
import IC.AST.Program;
import IC.AST.Return;
import IC.AST.Statement;
import IC.AST.StatementsBlock;
import IC.AST.StaticCall;
import IC.AST.StaticMethod;
import IC.AST.This;
import IC.AST.UserType;
import IC.AST.VariableLocation;
import IC.AST.VirtualCall;
import IC.AST.VirtualMethod;
import IC.AST.Visitor;
import IC.AST.While;
import IC.SymbolTables.BlockSymbolTable;
import IC.SymbolTables.MethodSymbolTable;
import IC.SymbolTables.SemanticSymbol;
import IC.SymbolTables.SymbolTable;
import IC.TYPE.ArrayType;
import IC.TYPE.BoolType;
import IC.TYPE.ClassType;
import IC.TYPE.IntType;
import IC.TYPE.Kind;
import IC.TYPE.MethodType;
import IC.TYPE.NullType;
import IC.TYPE.StringType;
import IC.TYPE.Type;
import IC.TYPE.TypeTable;
import IC.TYPE.VoidType;


public class TypeChecker implements Visitor {

   public Object visit(Program program) {
       Object temp;
       for (ICClass icClass : program.getClasses()) {
          temp = icClass.accept(this);
          if (temp != null) { 
              return temp;
          }
       }
       
       return null;  
   }

   public Object visit(ICClass icClass) {
       Object temp;
       for (Field field : icClass.getFields()) {
           temp = field.accept(this);
           if (temp != null) { 
               return temp;
           }
       }

       for (Method method : icClass.getMethods()) {
           temp = method.accept(this);
           if (temp != null) { 
               return temp;
           }
       }
       
       return null;
   }

   public Object visit(PrimitiveType type) {
       return null; 
   }

   public Object visit(UserType type) {
       return null;
   }

   public Object visit(Field field) {
       return null;
   }

   public Object visit(LibraryMethod method) {
       return handleMethod(method);
   }

   public Object visit(Formal formal) {
       return null;   
   }

   public Object visit(VirtualMethod method) {
       return handleMethod(method);
   }

   public Object visit(StaticMethod method) {
       return handleMethod(method);
   }

   public Object visit(Assignment assignment) {
       Object temp;
       temp = assignment.getAssignment().accept(this);
       if (temp != null){
    	   return temp;
    	   }
       assignment.getVariable().accept(this);
       if (!isSubTypeOf(assignment.getAssignment(), assignment.getVariable())) { 
           return assignment;
       }
       return null;
   }

   public Object visit(CallStatement callStatement) {
       return callStatement.getCall().accept(this);
   }

   public Object visit(Return returnStatement) {      
       String methodId = getEnclosingMethod(returnStatement).getId();
       boolean inStatic = returnStatement.getEnclosingScope().isStatic();
       MethodType methodType = null;
       if (inStatic) { 
           methodType = (MethodType) getEnclosingMethod(returnStatement).getParentSymbolTable().staticLookup(methodId).getType();
       }
       else { 
           methodType = (MethodType) getEnclosingMethod(returnStatement).getParentSymbolTable().lookup(methodId).getType();

       }
       Type formalReturnType = methodType.getReturnType();
       Type actualRetType = null;
       boolean returned = false;
       
       if (returnStatement.hasValue()) {
           returnStatement.getValue().accept(this); 
           actualRetType = returnStatement.getValue().getSemanticType();
           returned = true;
       }
       
       if (formalReturnType.equals(new VoidType(0))) { 
           if (returned) { 
               System.out.println("semantic error at line " + returnStatement.getLine() + ": " + methodId + " has a return type of void");
               System.exit(-1);
           }
           else { 
               // All is well. 
           }
       }
       else { //formal return type is not void
           if (!returned) { 
               System.out.println("semantic error at line " + returnStatement.getLine() + ": " + methodId + " has a return type of " + formalReturnType + ". must return that type.");
               System.exit(-1);
           }
           else { 
               if (!isSubTypeOf(actualRetType, formalReturnType)) { 
                   System.out.println("semantic error at line " + returnStatement.getLine() + ": " + methodId + " return type mismatch.");
                   System.exit(-1);
               }
           }
       }
       return null;
   }


   public Object visit(If ifStatement) {        
     Object temp;
     temp = ifStatement.getCondition().accept(this);
     if (temp != null) return temp;
         temp = ifStatement.getOperation().accept(this);
     if (temp != null) return temp;
     if (ifStatement.hasElse()) { 
         temp = ifStatement.getElseOperation().accept(this);
         if (temp != null) return temp;
     }
     
     if (!Type.isBool(ifStatement.getCondition())) { 
         return ifStatement;
     }
     return null;
   }

   public Object visit(While whileStatement) {
      whileStatement.getCondition().accept(this);
      whileStatement.getOperation().accept(this);
      if (!Type.isBool(whileStatement.getCondition())) { 
          return whileStatement;
      }
      return null;
   }

   public Object visit(Break breakStatement) {       
       return null;
   }

   public Object visit(Continue continueStatement) {      
       return null;
   }

   public Object visit(StatementsBlock statementsBlock) {
       Object temp;
       for (Statement statement : statementsBlock.getStatements()) { 
           temp = statement.accept(this);
           if (temp != null) { 
               return temp;
           }
       }
       
       return null;
       
   }

   public Object visit(LocalVariable localVariable) {
       Object temp;
       if (localVariable.hasInitValue()) { 
           temp = localVariable.getInitValue().accept(this);
           if (temp != null) { 
               return temp;
           }
           
           if (!isSubTypeOf(localVariable.getInitValue(), localVariable)) { 
               return localVariable;
           }
       }
       return null;
   }

   public Object visit(VariableLocation location) {
       boolean inStatic = location.getEnclosingScope().isStatic();
       if (location.getLocation() == null) { 
           location.setSemanticType(find(location.getEnclosingScope(),location.getName(),inStatic).getType());
           SemanticSymbol symbol = find(location.getEnclosingScope(),location.getName(),inStatic);
           Type t = symbol.getType();
           location.setSemanticType(t);
           
           
           if (location.getLocation() != null) 
               location.getLocation().accept(this);
           return null;
       }
       else if (location.getLocation() instanceof VariableLocation) {
           VariableLocation vl = (VariableLocation) location.getLocation();
           SymbolTable st;
           if (vl.getSemanticType() instanceof ClassType){
        	   st = getClassSymbolTable(vl.getSemanticType().toString(), location);
           }
           else{
	           SemanticSymbol smbo = find(location.getEnclosingScope(),vl.getName(),inStatic);
	           location.getLocation().setSemanticType(smbo.getType());
	           Type t = location.getLocation().getSemanticType();
	           String str = t.toString(); //this is the classname, for instance A
	           st = getClassSymbolTable(str, location); 
           }
           Type realType = st.lookup(location.getName()).getType();
           location.setSemanticType(realType);
           return null;
       }
       else { 
           return null;
       }
       
   }
   
  


    private SymbolTable getClassSymbolTable(String str, ASTNode startNode) {
        SymbolTable temp = startNode.getEnclosingScope();
        while (temp.getParentSymbolTable() != null) { 
            temp = temp.getParentSymbolTable();
        }
        return temp.symbolTableLookup(str);
    }

public Object visit(ArrayLocation location) {
       Expression e1 = location.getIndex();
       Expression e2 = location.getArray();
       e1.accept(this);
       e2.accept(this);
       if (Type.isInt(e1)) { 
           if (e2.getSemanticType() instanceof ArrayType) { 
               ArrayType e3 = (ArrayType) e2.getSemanticType();
               location.setSemanticType(e3.getElemType());
               return null;
           }
           else { 
               return location;
           }
       }
       return location;
   }

   public Object visit(StaticCall call) {
       String funcName = call.getName();
       String className = call.getClassName();
       SymbolTable st = getClassSymbolTable(className, call);
       SemanticSymbol methodSymbol = st.staticLookup(className,funcName,call);
       return checkFormalsToArgs(call, methodSymbol);
           
       
   }

   public Object visit(VirtualCall call) {
       String funcName = call.getName();
       boolean inStatic = call.getEnclosingScope().isStatic();
       if (call.getLocation() == null) { // method is in the same class as the call
           SemanticSymbol methodSymbol = find(call.getEnclosingScope(),funcName,inStatic);
           if(!(methodSymbol.getType() instanceof MethodType)){
        	   System.out.println("semantic error at line " + call.getLine() + ": there is no method in the name of '" + call.getName() +"'.");
               System.exit(-1); 
           }
           return checkFormalsToArgs(call, methodSymbol);
       }
       else {                           // location = object name 
           if (call.getLocation() instanceof VariableLocation) {
               VariableLocation objectName = (VariableLocation) call.getLocation();
               SymbolTable st;
               if (objectName.getSemanticType() instanceof ClassType){
            	   st = getClassSymbolTable(objectName.getSemanticType().toString(), call);
               }
               else {
               SemanticSymbol smbo = find(call.getEnclosingScope(),objectName.getName(),inStatic);
              // call.getLocation().setSemanticType(smbo.getType());
               
               Type t = call.getLocation().getSemanticType();
               String str = t.toString(); //this is the classname, for instance A
               st = getClassSymbolTable(str, call);
               }
               
               SemanticSymbol methodSymbol = st.lookup(funcName); // has to be VIRTUAL lookup at this line because this is a virtual call. e.g "a.foo()"
               return checkFormalsToArgs(call, methodSymbol);
           }
           else {  
               return null;
               
           }
       }
   }

   private Object checkFormalsToArgs(Call call, SemanticSymbol methodSymbol) {
       MethodType methType = (MethodType) methodSymbol.getType();
       Type[] params = methType.getParamTypes();
       List<Expression> args = call.getArguments();
       int paramsLength = 0;
       if (params != null) { 
           paramsLength = params.length;
       }
       
       
       if (args.size() != paramsLength) { 
    	   System.out.println("semantic error at line " + call.getLine() + ": not the correct amount of parameters in call '" + call.getName() +"'.");
           System.exit(-1);       }
       for (int i = 0; i < paramsLength; i++) { 
           if (!isSubTypeOf(args.get(i), params[i])) { 
        	   System.out.println("semantic error at line " + call.getLine() + ": parameter number " + (i+1) + " is not of the correct type.");
               System.exit(-1);
           }
       }
       call.setSemanticType(methType.getReturnType());
       return null;
}

   public Object visit(This thisExpression) { 
       return null;
   }

   public Object visit(NewClass newClass) { 
       IC.TYPE.Type type = newClass.getSemanticType();
       String className = newClass.getName();
       if (type != TypeTable.getClassType(className)) { 
           return newClass;
       }
       return null; 
   }

   public Object visit(NewArray newArray) {   
       Expression size = newArray.getSize();
       size.accept(this);
       Type arrayType = Type.getArrayType(newArray);        
       if (Type.isInt(newArray.getSize())) { 
           if (newArray.getSemanticType() != arrayType) { 
               return newArray;
           }
       }
       else { 
           System.out.println("semantic error at line " + newArray.getLine() + ": array size must be of int type.");
           System.exit(-1);
       }
       return null;
   }

   public Object visit(Length length) {
       
       if (length.getArray() != null) {
           length.getArray().accept(this); 
           Type t = length.getArray().getSemanticType();
           if (!(t instanceof ArrayType)) { 
               return length;
           }
           if (!Type.isInt(length)) { 
               return length;
           }
       }
       return null;
   }

   public Object visit(MathBinaryOp binaryOp) {
       binaryOp.getFirstOperand().accept(this);
       binaryOp.getSecondOperand().accept(this);
       if (!Type.isString(binaryOp) && !Type.isInt(binaryOp)) {
           return binaryOp;
       }
        
       return null;
   }

   public Object visit(LogicalBinaryOp binaryOp) {       
       binaryOp.getFirstOperand().accept(this);
       binaryOp.getSecondOperand().accept(this);
       if (!Type.isBool(binaryOp)) { 
           return binaryOp;
       } 
       return null;
   }



    public Object visit(MathUnaryOp unaryOp) {
        if (!Type.isInt(unaryOp)) { 
            return unaryOp;
        }
        return null;
    }

   public Object visit(LogicalUnaryOp unaryOp) {
       if (!Type.isBool(unaryOp)) { 
           return unaryOp;
       }
      
       return null;
   }

   public Object visit(Literal literal) {
       String bah = literal.getType().getDescription();
       if (bah.compareTo("Literal") == 0)  { 
            if (!Type.isNull(literal)) {
                return literal; // problem, types dont match
            }
       }
       else if (bah.compareTo("Boolean literal") == 0) {
           if (!Type.isBool(literal)) {
               return literal;
           }
       }
       else if (bah.compareTo("String literal") == 0) {
           if (!Type.isString(literal)) {
               return literal;
           }
       }
       else if (bah.compareTo("Integer literal") == 0) {
           if (!Type.isInt(literal)) {
               return literal;
           }      
       }
       // Literal is correctly typed
       return null;
   }

   public Object visit(ExpressionBlock expressionBlock) {
       expressionBlock.getExpression().accept(this);
       expressionBlock.setSemanticType(expressionBlock.getExpression().getSemanticType());
       return null;
   }
   
   private Object handleMethod(Method method) {
       Object temp;
       for (Formal formal : method.getFormals()) { 
           temp = formal.accept(this);
           if (temp != null) { 
               return temp;
           }
           
       }
       for (Statement statement : method.getStatements()) { 
           temp = statement.accept(this);
           if (temp != null) { 
               return temp;
           }
       }
       return null;
}
   

   
   private boolean isSubTypeOf(Type first, Type second) {
       return (IC.TYPE.TypeTable.isSubTypeOf(first, second));
 }
   
   
   private boolean isSubTypeOf(ASTNode first, Type second) {
       return isSubTypeOf(first.getSemanticType(), second);
    }
   
   private boolean isSubTypeOf(ASTNode first, ASTNode second) {
      return isSubTypeOf(first.getSemanticType(), second.getSemanticType());
   }
   
   //assumes node is indeed in somewhere in method scope
   private SymbolTable getEnclosingMethod(ASTNode node) {
       SymbolTable temp = node.getEnclosingScope();
       while (temp != null) { 
           if (temp instanceof MethodSymbolTable) { 
               return temp;
           }
           temp = temp.getParentSymbolTable();
       }
       return null;
   }
   private SemanticSymbol find(SymbolTable sTable, String name, boolean inStatic) {
       SemanticSymbol stbl;
       
       if(inStatic){
           stbl = sTable.staticLookup(name);
       }
       else{
           stbl = sTable.lookup(name);
       }

       return stbl;
   }
}
