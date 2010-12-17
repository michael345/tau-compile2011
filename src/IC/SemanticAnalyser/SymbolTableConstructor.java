package IC.SemanticAnalyser;

import IC.AST.*;

public class SymbolTableConstructor implements Visitor {

   private String ICFilePath;
   private SymbolTable st;
   private int blockIndex = 0;
   private TypeTable tt;
   


 
   public SymbolTableConstructor(String ICFilePath) {
       this.ICFilePath = ICFilePath;
       this.st = new SymbolTable("global");
   }
   
   public Object visit(Program program) {
       //output.append("Abstract Syntax Tree: " + ICFilePath + "\n");
       SemanticSymbol temp; 
       for (ICClass icClass : program.getClasses()) {
           temp = new SemanticSymbol(program.getSemanticType(), new Kind(Kind.CLASS), icClass.getName(), false);
           if (st.insert(icClass.getName(),temp)) { 
               
           }
           else { 
               
           }
          
       }
       
       for (ICClass icClass : program.getClasses()) {
          st.addChild((SymbolTable)icClass.accept(this));
       }
       program.setEnclosingScope(st);
       return st;  
   }

   public Object visit(ICClass icClass) {
	   SymbolTable classTable = new SymbolTable(icClass.getName());
       for (Field field : icClass.getFields())
    	   classTable.insert(field.getName(), new SemanticSymbol(field.getSemanticType(), new Kind(Kind.FIELD), field.getName(), false));

       for (Method method : icClass.getMethods()) {
           if (method instanceof VirtualMethod) { 
               classTable.insert(method.getName(),new SemanticSymbol(method.getSemanticType(), new Kind(Kind.VIRTUALMETHOD),method.getName(),false));
           }
           else 
               classTable.insert(method.getName(),new SemanticSymbol(method.getSemanticType(), new Kind(Kind.STATICMETHOD),method.getName(),false));
       }
       
     
           for (Method method : icClass.getMethods()){
    	   classTable.addChild((SymbolTable)method.accept(this));
       }
       icClass.setEnclosingScope(classTable);
       return classTable;
   }

   public Object visit(PrimitiveType type) {
       return tt; 
   }

   public Object visit(UserType type) {
       return tt;
   }

   public Object visit(Field field) {
       return tt;
   }

   public Object visit(LibraryMethod method) {
       return handleMethod(method);
   }

   public Object visit(Formal formal) {
       return tt;   
   }

   public Object visit(VirtualMethod method) {

	   return handleMethod(method);

	  

   }

   public Object visit(StaticMethod method) {
       return handleMethod(method);
   }

   public Object visit(Assignment assignment) {
       //non-scoped
	   return tt;
   }

   public Object visit(CallStatement callStatement) {
       //non-scoped
	   return tt;
   }

   public Object visit(Return returnStatement) {       //non-scoped
       if (returnStatement.hasValue())
           returnStatement.getValue().accept(this);
       return tt;
   }

   public Object visit(If ifStatement) {        
      SymbolTable ifSymbolTable = new SymbolTable("if");
      ifSymbolTable.addChild((SymbolTable)ifStatement.getOperation().accept(this));

       if (ifStatement.hasElse()) { 
    	   ifSymbolTable.addChild((SymbolTable)ifStatement.getElseOperation().accept(this));
       }
       ifStatement.setEnclosingScope(ifSymbolTable);
      return ifSymbolTable;
   }

   public Object visit(While whileStatement) {
      SymbolTable whileSymbolTable = new SymbolTable("while");
      whileSymbolTable.addChild((SymbolTable)whileStatement.getOperation().accept(this));
      whileStatement.setEnclosingScope(whileSymbolTable); 
      return whileSymbolTable;
   }

   public Object visit(Break breakStatement) {       //non-scoped
       return tt;
   }

   public Object visit(Continue continueStatement) {       //non-scoped
       return tt;
   }

   public Object visit(StatementsBlock statementsBlock) {
     
       SymbolTable blockTable = new SymbolTable("block"+ blockIndex);
       SymbolTable symbolTable;
       
       for (Statement statement : statementsBlock.getStatements()) { 
           if (statement instanceof LocalVariable) {
               LocalVariable lv = (LocalVariable)statement;
              
               blockTable.insert(lv.getName(), new SemanticSymbol(statement.getSemanticType(), new Kind(Kind.VAR), lv.getName(), false));
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
       return blockTable;
       
   }

   public Object visit(LocalVariable localVariable) {
       //addAllSubArraysToTypeTable(localVariable.getType());
       return tt;
   }

   public Object visit(VariableLocation location) {
      if (location.getLocation() != null) 
          location.getLocation().accept(this);
      return tt;
   }

   public Object visit(ArrayLocation location) {
       return tt;
   }

   public Object visit(StaticCall call) {
       return tt;
   }

   public Object visit(VirtualCall call) {
       return tt;
   }

   public Object visit(This thisExpression) {
       return tt;
   }

   public Object visit(NewClass newClass) {
       return tt; // TODO: probably handled when class was declared
   }

   public Object visit(NewArray newArray) {
       //addAllSubArraysToTypeTable( newArray.getType());
       return tt;
   }

   public Object visit(Length length) {
       return tt;
   }

   public Object visit(MathBinaryOp binaryOp) {
       tt.primitiveType(new IntType(0));
       binaryOp.getFirstOperand().accept(this);
       binaryOp.getSecondOperand().accept(this);        
       return tt;
   }

   public Object visit(LogicalBinaryOp binaryOp) {
       tt.primitiveType(new BoolType(0));
       binaryOp.getFirstOperand().accept(this);
       binaryOp.getSecondOperand().accept(this); 
       return tt;
   }

   public Object visit(MathUnaryOp unaryOp) {
       tt.primitiveType(new IntType(0));
       unaryOp.getOperand().accept(this);
       return tt;
   }

   public Object visit(LogicalUnaryOp unaryOp) {
       tt.primitiveType(new BoolType(0));
       unaryOp.getOperand().accept(this);
       return tt;
   }

   public Object visit(Literal literal) {
       String bah = literal.getType().getDescription();
       if (bah.compareTo("Literal") == 0) 
           tt.primitiveType(new NullType(0));
       else if (bah.compareTo("Boolean literal") == 0) 
           tt.primitiveType(new BoolType(0));
       else if (bah.compareTo("String literal") == 0) 
           tt.primitiveType(new StringType(0));
       else if (bah.compareTo("Integer literal") == 0) 
           tt.primitiveType(new IntType(0));
       return tt;
   }

   public Object visit(ExpressionBlock expressionBlock) {
       expressionBlock.getExpression().accept(this);
       return tt;
   }
   
   private Object handleMethod(Method method) {
	   SymbolTable methodTable = new SymbolTable(method.getName());
	   SymbolTable symbolTable;//child to be
       if (method.getFormals().size() > 0) {
           for (Formal formal : method.getFormals()) { 
               methodTable.insert(formal.getName(), new SemanticSymbol(formal.getSemanticType(), new Kind(Kind.FORMAL), formal.getName(), false));
           }
       }
	   for (Statement statement : method.getStatements()) { 
           if(statement instanceof LocalVariable){
        	   LocalVariable lv = (LocalVariable)statement;
        	   methodTable.insert(lv.getName(), new SemanticSymbol(statement.getSemanticType(), new Kind(Kind.VAR), lv.getName(), false));
           }
           else {
        	   symbolTable = (SymbolTable)statement.accept(this);
        	   if (symbolTable != null){
        		   methodTable.addChild((SymbolTable)statement.accept(this));
        	   }
           }
       }
       method.setEnclosingScope(methodTable);	  
       return methodTable;
}
}
