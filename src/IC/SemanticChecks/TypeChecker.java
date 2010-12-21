package IC.SemanticChecks;

import java.util.ArrayList;
import java.util.List;

import IC.AST.*;
import IC.TYPE.*;


public class TypeChecker implements Visitor {

   
   
   public Object visit(Program program) {
       for (ICClass icClass : program.getClasses()) {
          icClass.accept(this));
       }
       
       
       return null;  
   }

   public Object visit(ICClass icClass) {
       
       for (Field field : icClass.getFields()) {
           field.accept(this);
       }

       for (Method method : icClass.getMethods()) {
           if (method instanceof VirtualMethod) { 
               boolean a = classTable.insert(method.getName(),new SemanticSymbol(method.getSemanticType(), new Kind(Kind.VIRTUALMETHOD),method.getName(),false));
               if (!a) { 
                   System.out.println("Error: Illegal redefinition; element " + method.getName() + " in line #" + method.getLine());
                   System.exit(-1);
               }
           }
           else {
               boolean a = classTable.insert(method.getName(),new SemanticSymbol(method.getSemanticType(), new Kind(Kind.STATICMETHOD),method.getName(),false));
               if (!a) { 
                   System.out.println("Error: Illegal redefinition; element " + method.getName() + " in line #" + method.getLine());
                   System.exit(-1);
               }
           }
       }
       
     
       for (Method method : icClass.getMethods()) {
           currentScope = classTable;
           classTable.addChild((MethodSymbolTable)method.accept(this));
       }
       
       icClass.setEnclosingScope(classTable);
       return classTable;
   }

   public Object visit(PrimitiveType type) {
       type.setEnclosingScope(currentScope);
       return null; 
   }

   public Object visit(UserType type) {
       type.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(Field field) {
       field.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(LibraryMethod method) {
       return handleMethod(method);
   }

   public Object visit(Formal formal) {
       formal.setEnclosingScope(currentScope);
       return null;   
   }

   public Object visit(VirtualMethod method) {
       return handleMethod(method);
   }

   public Object visit(StaticMethod method) {
       return handleMethod(method);
   }

   public Object visit(Assignment assignment) {
       assignment.setEnclosingScope(currentScope);
       assignment.getAssignment().accept(this);
       assignment.getVariable().accept(this);
       return null;
   }

   public Object visit(CallStatement callStatement) {
       callStatement.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(Return returnStatement) {       //non-scoped
       if (returnStatement.hasValue())
           returnStatement.getValue().accept(this);
       returnStatement.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(If ifStatement) {        
      SymbolTable ifSymbolTable = new BlockSymbolTable("if");
      currentScope = ifSymbolTable;
      ifSymbolTable.addChild((SymbolTable)ifStatement.getOperation().accept(this));

       if (ifStatement.hasElse()) { 
           currentScope = ifSymbolTable;
           ifSymbolTable.addChild((SymbolTable)ifStatement.getElseOperation().accept(this));
       }
       ifStatement.setEnclosingScope(ifSymbolTable); //TODO: maybe currentScope
      return ifSymbolTable;
   }

   public Object visit(While whileStatement) {
      SymbolTable whileSymbolTable = new SymbolTable("while");
      whileSymbolTable.setLoop(true);
      currentScope = whileSymbolTable;
      whileSymbolTable.addChild((SymbolTable)whileStatement.getOperation().accept(this));
      whileStatement.setEnclosingScope(whileSymbolTable); //TODO: maybe currentScope
      return whileSymbolTable;
   }

   public Object visit(Break breakStatement) {       
       breakStatement.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(Continue continueStatement) {      
       continueStatement.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(StatementsBlock statementsBlock) {
       SymbolTable blockTable = new BlockSymbolTable("block"); // TODO: want this ID to be statement block in "sfunc"
       SymbolTable symbolTable;
       
       for (Statement statement : statementsBlock.getStatements()) { 
           currentScope = blockTable; // or symbolTable;
           if (statement instanceof LocalVariable) {
               LocalVariable lv = (LocalVariable)statement;
              
               boolean insertSuccessful = blockTable.insert(lv.getName(), new SemanticSymbol(statement.getSemanticType(), new Kind(Kind.VAR), lv.getName(), false));
               if (!insertSuccessful) { 
                   System.out.println("Error: Illegal redefinition; element " + lv.getName() + " in line #" + lv.getLine());
                   System.exit(-1);
               }
               statement.setEnclosingScope(blockTable);
           }
           else {
               symbolTable = (SymbolTable)statement.accept(this);
               if (symbolTable != null){
                   blockTable.addChild((SymbolTable)statement.accept(this));
               }
           }
       }
       
       statementsBlock.setEnclosingScope(blockTable);
       currentScope = blockTable;
       return blockTable;
       
   }

   public Object visit(LocalVariable localVariable) {
       localVariable.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(VariableLocation location) {
       location.setEnclosingScope(currentScope);
       if (location.getLocation() != null) 
           location.getLocation().accept(this);
       return null;
   }

   public Object visit(ArrayLocation location) {
       return null;
   }

   public Object visit(StaticCall call) {
       return null;
   }

   public Object visit(VirtualCall call) {
       return null;
   }

   public Object visit(This thisExpression) { // TODO:
       return null;
   }

   public Object visit(NewClass newClass) { //TODO: similar to newArray below
       return null; 
   }

   public Object visit(NewArray newArray) { // Done I think
       IC.TYPE.Type type = newArray.getType().getSemanticType();
       if (isInt(newArray.getSize())) { 
           if (newArray.getSemanticType() != TypeTable.arrayType(type)) { 
               return newArray;
           }
       }
       return null;
   }

   public Object visit(Length length) {
       if (length.getArray() != null) {
           if (!isInt(length)) { 
               return length;
           }
       }
       return null;
   }

   public Object visit(MathBinaryOp binaryOp) {
       if (isString(binaryOp.getFirstOperand()) && isString(binaryOp.getSecondOperand())) {
           if (binaryOp.getOperator().getOperatorString().compareTo("+") == 0 && !isString(binaryOp)) { // string + string = not string
               return binaryOp; // TODO: maybe just exit here with the info of where shit went wrong; e.g. what error triggered
           }
       }
       
       else if (isInt(binaryOp.getFirstOperand()) && isInt(binaryOp.getSecondOperand())) {
           if (!isInt(binaryOp)) { 
               return binaryOp; //TODO: same as above;
           }
       }
       
       
       
       if (binaryOp.getOperator().getOperatorString().compareTo("+") == 0) { //plus is special!
          
       }
       else if () {  //multiply
           if (binaryOp.getFirstOperand().getSemanticType() == TypeTable.primitiveType(new IntType(0))) &&
           
       }
       else { 
           
           
       }
       
       binaryOp.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(LogicalBinaryOp binaryOp) {
       if (binaryOp.getOperator().getOperatorString().compareTo("&&") == 0 ||
           binaryOp.getOperator().getOperatorString().compareTo("||") == 0) { 
           if (isBool(binaryOp.getFirstOperand()) && isBool(binaryOp.getSecondOperand())) { 
               if (!isBool(binaryOp)) { 
                   return binaryOp;
               }
           }
       }
       else if (binaryOp.getOperator().getOperatorString().compareTo("==") == 0 ||
                   binaryOp.getOperator().getOperatorString().compareTo("!=") == 0) { 
           if (isSubTypeOf(binaryOp.getFirstOperand(),binaryOp.getSecondOperand()) || 
                   isSubTypeOf(binaryOp.getSecondOperand(),binaryOp.getFirstOperand())) { 
               if (!isBool(binaryOp)) { 
                   return binaryOp;
               }
           }
       }
       else {}
       
       return null;
   }

   private boolean isSubTypeOf(ASTNode first, ASTNode second) {
      return (IC.TYPE.TypeTable.isSupTypeOf(first.getSemanticType(), second.getSemanticType()));
}

public Object visit(MathUnaryOp unaryOp) {
       if (isInt(unaryOp.getOperand())) {
           if (!isInt(unaryOp)) {
               return unaryOp;
           }
       }
       return null;
   }

   public Object visit(LogicalUnaryOp unaryOp) {
       if (isBool(unaryOp.getOperand())) {
           if (!isBool(unaryOp)) {
               return unaryOp;
           }
       }
       return null;
   }

   public Object visit(Literal literal) {
       String bah = literal.getType().getDescription();
       if (bah.compareTo("Literal") == 0)  { 
            if (!isNull(literal)) {
                return literal; // problem, types dont match
            }
       }
       else if (bah.compareTo("Boolean literal") == 0) {
           if (!isBool(literal)) {
               return literal;
           }
       }
       else if (bah.compareTo("String literal") == 0) {
           if (!isString(literal)) {
               return literal;
           }
       }
       else if (bah.compareTo("Integer literal") == 0) {
           if (!isInt(literal)) {
               return literal;
           }      
       }
       // Literal is correctly typed
       return null;
   }

   public Object visit(ExpressionBlock expressionBlock) {
       expressionBlock.getExpression().accept(this);
       expressionBlock.setEnclosingScope(currentScope);
       return null;
   }
   
   private Object handleMethod(Method method) {
       SymbolTable methodTable = new MethodSymbolTable(method.getName());
       SymbolTable symbolTable;//child to be
       currentScope = methodTable;
       if (method.getFormals().size() > 0) {
           for (Formal formal : method.getFormals()) { 
               boolean insertSuccessfully = methodTable.insert(formal.getName(), new SemanticSymbol(formal.getSemanticType(), new Kind(Kind.FORMAL), formal.getName(), false));
               if (!insertSuccessfully) { 
                   System.out.println("Error: Illegal redefinition; element " + formal.getName() + " in line #" + formal.getLine());
                   System.exit(-1);
               }
           }
       }
       for (Statement statement : method.getStatements()) { 
           if(statement instanceof LocalVariable){
               LocalVariable lv = (LocalVariable)statement;
               boolean insertSuccessfully = methodTable.insert(lv.getName(), new SemanticSymbol(statement.getSemanticType(), new Kind(Kind.VAR), lv.getName(), false));
               if (!insertSuccessfully) { 
                   System.out.println("Error: Illegal redefinition; element " + lv.getName() + " in line #" + lv.getLine());
                   System.exit(-1);
               }
           }
           else {
               symbolTable = (SymbolTable)statement.accept(this);
               if (symbolTable != null){
                   methodTable.addChild((SymbolTable)statement.accept(this));
               }
               currentScope = methodTable;
           }
       }
       method.setEnclosingScope(methodTable);   
       currentScope = methodTable;
       return methodTable;
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
   
   
}
