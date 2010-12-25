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
import IC.AST.Location;
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
import IC.SymbolTables.SemanticSymbol;
import IC.SymbolTables.SymbolTable;
import IC.TYPE.ArrayType;
import IC.TYPE.BoolType;
import IC.TYPE.IntType;
import IC.TYPE.Kind;
import IC.TYPE.MethodType;
import IC.TYPE.NullType;
import IC.TYPE.StringType;
import IC.TYPE.Type;
import IC.TYPE.TypeTable;
import IC.TYPE.VoidType;


public class ScopeChecker implements Visitor { 

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
	   if(icClass.hasSuperClass()){ //checks for redefinition between two classes when one extends the other
	       for (Field field : icClass.getFields()) {
	           String fieldName = field.getName();
	           if(icClass.getEnclosingScope().getParentSymbolTable().lookup(fieldName)!=null){
	        	   return field;
	           }
	       }
	
	       for (Method method : icClass.getMethods()) {
		           String methodName = method.getName();
		           if(icClass.getEnclosingScope().getParentSymbolTable().lookup(methodName)!=null){
		        	   return method;
		           }
		       }
	       }
	   
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
       return null;
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
	   if (temp != null) { 
           return temp;
       }
       temp = assignment.getVariable().accept(this);
       if (temp != null) { 
           return temp;
       }
       
       return null;
   }

   public Object visit(CallStatement callStatement) { 
	   return callStatement.getCall().accept(this);
   }

   public Object visit(Return returnStatement) {      
       if (returnStatement.hasValue())
           returnStatement.getValue().accept(this);
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
     return null;
   }

   public Object visit(While whileStatement) {
      Object temp;
      temp = whileStatement.getCondition().accept(this);
      if (temp != null) return temp;
      temp = whileStatement.getOperation().accept(this);
      if (temp != null) return temp;
      
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
    	   if (temp != null) return statement;
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
       }
       return null;
   }

   public Object visit(VariableLocation location) {//TODO: Test this
       
       if (!location.isExternal()) { 
    	   
           SemanticSymbol check1 = location.getEnclosingScope().lookup(location.getName());
           if (check1 == null) { // Variable used before definition!
               System.out.println("semantic error at line " + location.getLine() + ": variable used before definition");
               System.exit(-1);
           }
           else { 
               location.setSemanticType(check1.getType());
               return null; 
           }
       }
       else { 
           Object temp = location.getLocation().accept(this);
           if (temp != null) {  // No error YET 
               Location motherLocation = (Location) temp;
               Type type = motherLocation.getSemanticType();
               SymbolTable st = getClassSymbolTable(type.toString(),location);
               SemanticSymbol check1 = st.lookup(location.getName());
               if (check1 == null) { // Variable not defined in motherLocation scope
                   System.out.println("semantic error at line " + location.getLine() + ": no such field \"" + location.getName() + "\" in class \"" + type.toString() + "\"");
                   System.exit(-1);
               }
               else {
                   location.setSemanticType(check1.getType());
               }
               
           }
       }
       return location;
       
   }

  


    private SymbolTable getClassSymbolTable(String str, ASTNode startNode) {//TODO: Write this
        SymbolTable temp = startNode.getEnclosingScope();
        while (temp.getParentSymbolTable() != null) { 
            temp = temp.getParentSymbolTable();
        }
        return temp.symbolTableLookup(str);
    }

public Object visit(ArrayLocation location) {
       Expression e1 = location.getIndex();
       Expression e2 = location.getArray();
       Object temp;
       temp = e1.accept(this);
       if (temp != null) { 
           return temp;
       }
       temp = e2.accept(this);
       if (temp != null) { 
           return temp;
       }
       return null;
   }

   public Object visit(StaticCall call) {
	   Object temp;
	   for (Expression e : call.getArguments()) {
		temp = e.accept(this);
		if (temp != null) { 
	           return temp;
	       }
	}
	   
       String funcName = call.getName();
       String className = call.getClassName();
       SymbolTable st = getClassSymbolTable(className, call);
       SemanticSymbol methodSymbol = st.lookup(funcName);
   	   return null;
           
       
   }

   public Object visit(VirtualCall call) {
	   Object temp;
	   for (Expression e : call.getArguments()) {
		temp = e.accept(this);
		if (temp != null) { 
	           return temp;
	       }
	}
	   
       String funcName = call.getName();
       if (call.getLocation() == null) { // method is in the same class as the call
           SemanticSymbol methodSymbol = call.getEnclosingScope().lookup(funcName);
           if (methodSymbol == null) return call;//not found
       }
       else {                           // location = object name 
           VariableLocation objectName = (VariableLocation) call.getLocation();
           SemanticSymbol symbol = call.getEnclosingScope().lookup(objectName.getName());
           if (symbol == null) return call;//not found
           call.getLocation().setSemanticType(symbol.getType());
           Type t = call.getLocation().getSemanticType();
           String str = t.toString(); //this is the classname, for instance A
           SymbolTable st = getClassSymbolTable(str, call);
           SemanticSymbol methodSymbol = st.lookup(funcName);
           
       }
       return null;
   }

   private Object checkFormalsToArgs(Call call, SemanticSymbol methodSymbol) {//TODO: Write this
       MethodType methType = (MethodType) methodSymbol.getType();
       Type[] params = methType.getParamTypes();
       List<Expression> args = call.getArguments();
       
       if (args.size() != params.length) { 
           return call;
       }
       for (int i = 0; i < params.length; i++) { 
           if (!isSubTypeOf(args.get(i), params[i])) { 
               return call; // args dont fit formals type-wise
           }
       }
       call.setSemanticType(methType.getReturnType());
       return null;
}

   public Object visit(This thisExpression) { 
       return null;
   }

   public Object visit(NewClass newClass) { //TODO: Write this
       IC.TYPE.Type type = newClass.getSemanticType();
       String className = newClass.getName();
       if (type != TypeTable.getClassType(className)) { 
           return newClass;
       }
       return null; 
   }

   public Object visit(NewArray newArray) {//TODO: Write this
       Expression size = newArray.getSize();
       IC.TYPE.Type type = newArray.getSemanticType(); //should be array of 
       if (isInt(newArray.getSize())) { 
           if (newArray.getSemanticType() != TypeTable.arrayType(type)) { 
               return newArray;
           }
       }
       return null;
   }

   public Object visit(Length length) {
       Object temp;
       if (length.getArray() != null) {
           temp = length.getArray().accept(this); 
           if(temp!=null) return length; 
       }
       return null;
   }

   public Object visit(MathBinaryOp binaryOp) {
	  Object temp;
	  temp = binaryOp.getFirstOperand().accept(this);
	  if(temp != null){
		  return binaryOp;
	  }
	  temp = binaryOp.getSecondOperand().accept(this);
	  if(temp != null){
		  return binaryOp;
	  }
       
        
       return null;
   }

   public Object visit(LogicalBinaryOp binaryOp) {       
	      Object temp;
		  temp = binaryOp.getFirstOperand().accept(this);
		  if(temp != null){
			  return binaryOp;
		  }
		  temp = binaryOp.getSecondOperand().accept(this);
		  if(temp != null){
			  return binaryOp;
		  }
       return null;
   }



    public Object visit(MathUnaryOp unaryOp) {
        if (!isInt(unaryOp)) { 
            return unaryOp;
        }
        return null;
    }

   public Object visit(LogicalUnaryOp unaryOp) {
       if (!isBool(unaryOp)) { 
           return unaryOp;
       }
      
       return null;
   }

   public Object visit(Literal literal) {
       return null;
   }

   public Object visit(ExpressionBlock expressionBlock) {
       return expressionBlock.getExpression().accept(this);
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
   
   private boolean isInt(ASTNode node) {
       return (node.getSemanticType() == TypeTable.primitiveType(new IntType(0)));
   }
   
   private boolean isBool(ASTNode node) {
       return (node.getSemanticType() == TypeTable.primitiveType(new BoolType(0)));
   }
   
   private boolean isNull(ASTNode node) {
       return (node.getSemanticType() == TypeTable.primitiveType(new NullType(0)));
   }
   
   private boolean isString(ASTNode node) {
       return (node.getSemanticType() == TypeTable.primitiveType(new StringType(0)));
   }
   
   private boolean isVoid(ASTNode node) {
       return (node.getSemanticType() == TypeTable.primitiveType(new VoidType(0)));
   }
   
   private boolean hasSameType(ASTNode node1, ASTNode node2) { 
       return (node1.getSemanticType() == node2.getSemanticType());
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
}
