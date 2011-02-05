package IC.SemanticChecks;
import java.util.Collection;
import java.util.List;

import IC.BinaryOps;
import IC.UnaryOps;
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
       for (ICClass icClass : program.getClasses()) {
          icClass.accept(this);
       }
       
       return null;  
   }

   public Object visit(ICClass icClass) {
	   if(icClass.hasSuperClass()){ //checks for redefinition between two classes when one extends the other
	       for (Field field : icClass.getFields()) {
	           String fieldName = field.getName();
	           if(icClass.getEnclosingScope().getParentSymbolTable().lookup(fieldName)!=null){
	        	   System.out.println("semantic error at line " + field.getLine() + " : field " + field.getName() +" is redefined in extending class");
	        	   System.exit(-1);
	           }
	       }
	
	       for (Method method : icClass.getMethods()) {
		           String methodName = method.getName();
		           SemanticSymbol idFromUpper;
		           if(method instanceof VirtualMethod){
		        	   idFromUpper = icClass.getEnclosingScope().getParentSymbolTable().lookup(methodName);
		           }
		           else{// if(method instanceof StaticMethod){
		        	   idFromUpper = icClass.getEnclosingScope().getParentSymbolTable().staticLookup(methodName);
		           }
		           if(idFromUpper!=null && (idFromUpper.getKind().getKind() == new Kind(Kind.FIELD).getKind())){
		        	   System.out.println("semantic error at line " + method.getLine() + " : method " + method.getName() +" is redefined in extending class");
		        	   System.exit(-1);
		        	   
		           }
		       }
	       }
	   
       for (Field field : icClass.getFields()) {
           field.accept(this);
       }

       for (Method method : icClass.getMethods()) { 
           method.accept(this);
       }
       
       return null;
   }

   public Object visit(PrimitiveType type) { 
       return null; 
   }

   public Object visit(UserType type) { 
	   String className = type.getName();
	   SymbolTable st = getClassSymbolTable(className, type);
       if (st == null){
    	   System.out.println("semantic error at line " + type.getLine() + " : Class " + className +" is undefined");
    	   System.exit(-1);
       }
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
	   assignment.getAssignment().accept(this);
       assignment.getVariable().accept(this);
       
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
      ifStatement.getCondition().accept(this);
      ifStatement.getOperation().accept(this);
     if (ifStatement.hasElse()) { 
         ifStatement.getElseOperation().accept(this);
     }
     return null;
   }

   public Object visit(While whileStatement) {
      
      whileStatement.getCondition().accept(this);
      whileStatement.getOperation().accept(this);
      return null;
   }

   public Object visit(Break breakStatement) {      
       return null;
   }

   public Object visit(Continue continueStatement) {      
       return null;
   }

   public Object visit(StatementsBlock statementsBlock) {
	   for (Statement statement : statementsBlock.getStatements()) {
    	   statement.accept(this);
	}
	   return null; 
   }

   public Object visit(LocalVariable localVariable) {          
       
       if (localVariable.hasInitValue()) { 
           localVariable.getInitValue().accept(this);
       }
       localVariable.getType().accept(this);
       return null;
   }

   public Object visit(VariableLocation location) {
       boolean inStatic = location.getEnclosingScope().isStatic();
       SemanticSymbol check1;
       if (!location.isExternal()) { 
    	   if(inStatic){
               check1 = location.getEnclosingScope().staticLookup(location.getName());
    	   }
    	   else{
    		   check1 = location.getEnclosingScope().lookup(location.getName());
    	   }
           if (check1 == null) { // Variable used before definition!
               System.out.println("semantic error at line " + location.getLine() + ": variable '" + location.getName() + "' used before definition");
               System.exit(-1);
           }
           else { 
               location.setSemanticType(check1.getType());
               return location; 
           }
       }
       else { 
           Object temp = location.getLocation().accept(this);
           if (temp != null) {  // No error YET 
               Location motherLocation = (Location) temp;
               Type type = motherLocation.getSemanticType();
               SymbolTable st = getClassSymbolTable(type.toString(),location);
               if(inStatic){
            	   check1 = st.staticLookup(location.getName());
        	   }
        	   else{
        		   check1 = st.lookup(location.getName());
        	   }
               
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
       Object temp;
       e1.accept(this);
       e2.accept(this);
       return null;
   }

   public Object visit(StaticCall call) {
	   for (Expression e : call.getArguments()) {
	       e.accept(this);
	   }
   	   return null;
           
       
   }

   public Object visit(VirtualCall call) {
	   for (Expression e : call.getArguments()) {
		e.accept(this);
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

   public Object visit(NewClass newClass) { 
       IC.TYPE.Type type = newClass.getSemanticType();
       String className = newClass.getName();
       SymbolTable st = getClassSymbolTable(className, newClass);
       if (st == null) {
        	   System.out.println("semantic error at line " + newClass.getLine() + " : Class " + className +" is undefined");
        	   System.exit(-1);
           }       
       return null; 
   }

   public Object visit(NewArray newArray) {
       return null;
   }

   public Object visit(Length length) {
       if (length.getArray() != null) {
           length.getArray().accept(this); 
       }
       return null;
   }

   public Object visit(MathBinaryOp binaryOp) {
       Expression first = binaryOp.getFirstOperand();
       Expression second = binaryOp.getSecondOperand();
       first.accept(this);
       second.accept(this);        
       return null;
   }

   public Object visit(LogicalBinaryOp binaryOp) {       
  
       binaryOp.getFirstOperand().accept(this);
       binaryOp.getSecondOperand().accept(this);  
       return null;
   }



    public Object visit(MathUnaryOp unaryOp) {
	   unaryOp.getOperand().accept(this);
       return null;
    }

   public Object visit(LogicalUnaryOp unaryOp) {
	   unaryOp.getOperand().accept(this);
       return null;
   }

   public Object visit(Literal literal) {
       return null;
   }

   public Object visit(ExpressionBlock expressionBlock) {
       return expressionBlock.getExpression().accept(this);
   }
   
   private Object handleMethod(Method method) {
       for (Formal formal : method.getFormals()) { 
           formal.accept(this);
       }
       for (Statement statement : method.getStatements()) { 
           statement.accept(this);
       }
       return null;
}
   
   private boolean isInt(ASTNode node) {
       return Type.isInt(node);
   }
   
   private boolean isBool(ASTNode node) {
       return Type.isBool(node);
   }
   
   private boolean isNull(ASTNode node) {
       return Type.isNull(node);
   }
   
   private boolean isString(ASTNode node) {
       return Type.isString(node);
   }
   
   private boolean isVoid(ASTNode node) {
       return Type.isVoid(node);
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
