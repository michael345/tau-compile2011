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
       assignment.getAssignment().accept(this);
       assignment.getVariable().accept(this);
       if (!isSubTypeOf(assignment.getAssignment(), assignment.getVariable())) { 
           return assignment;
       }
       return null;
   }

   public Object visit(CallStatement callStatement) {
       return callStatement.getCall().accept(this);
   }

   public Object visit(Return returnStatement) {       //non-scoped
       String methodId = getEnclosingMethod(returnStatement).getId();
       MethodType methodType = (MethodType) getEnclosingMethod(returnStatement).lookup(methodId).getType();
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
       if (location.getLocation() == null) { 
           location.setSemanticType(location.getEnclosingScope().lookup(location.getName()).getType());
           SemanticSymbol symbol = location.getEnclosingScope().lookup(location.getName());
           Type t = symbol.getType();
           location.setSemanticType(t);
           
           
           if (location.getLocation() != null) 
               location.getLocation().accept(this);
           return null;
       }
       else if (location.getLocation() instanceof VariableLocation ) {
           VariableLocation vl = (VariableLocation) location.getLocation();
           location.getLocation().setSemanticType(location.getEnclosingScope().lookup(vl.getName()).getType());
           Type t = location.getLocation().getSemanticType();
           String str = t.toString(); //this is the classname, for instance A
           SymbolTable st = getClassSymbolTable(str, location); 
           Type realType = st.lookup(location.getName()).getType();
           location.setSemanticType(realType);
           return null;
       }
       else { //take care of static cases, e.g. A.x instead of beler.x
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
       SemanticSymbol methodSymbol = st.lookup(funcName);
       return checkFormalsToArgs(call, methodSymbol);
           
       
   }

   public Object visit(VirtualCall call) {
       String funcName = call.getName();
       if (call.getLocation() == null) { // method is in the same class as the call
           SemanticSymbol methodSymbol = call.getEnclosingScope().lookup(funcName); 
           return checkFormalsToArgs(call, methodSymbol);
       }
       else {                           // location = object name 
           VariableLocation objectName = (VariableLocation) call.getLocation();
           call.getLocation().setSemanticType(call.getEnclosingScope().lookup(objectName.getName()).getType());
           Type t = call.getLocation().getSemanticType();
           String str = t.toString(); //this is the classname, for instance A
           SymbolTable st = getClassSymbolTable(str, call);
           SemanticSymbol methodSymbol = st.lookup(funcName);
           return checkFormalsToArgs(call, methodSymbol);
       }
   }

   private Object checkFormalsToArgs(Call call, SemanticSymbol methodSymbol) {
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

   public Object visit(This thisExpression) { // TODO:
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

   public Object visit(NewArray newArray) { // Done I think       
       Expression size = newArray.getSize();
       size.accept(this);
       IC.TYPE.Type type = newArray.getSemanticType(); //should be array of 
       if (Type.isInt(size)) { 
           if (newArray.getSemanticType() != TypeTable.arrayType(type)) { 
               return newArray;
           }
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
}
