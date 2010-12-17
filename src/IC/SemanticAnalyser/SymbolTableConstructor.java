package IC.SemanticAnalyser;

import IC.AST.*;

public class SymbolTableConstructor implements Visitor {

   private String ICFilePath;
   private SymbolTable st;
   private TypeTable tt;
   private int depth = 0; // depth of indentation

 
   public SymbolTableConstructor(String ICFilePath, TypeTable tt) {
       this.ICFilePath = ICFilePath;
       this.st = new SymbolTable("global");
       this.tt = tt;
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
       for (Method method : icClass.getMethods())
           classTable.insert(method.getName(),new SemanticSymbol(method.getSemanticType(), new Kind(Kind.VIRTUALMETHOD),method.getName(),false));
       for (Method method : icClass.getMethods()){
    	   classTable.addChild((SymbolTable)method.accept(this));
       }
       icClass.setEnclosingScope(classTable);
       return classTable;
   }

   public Object visit(PrimitiveType type) {
       //non-scoped
       return tt; 
   }

   public Object visit(UserType type) {
       //non-scoped
       return tt;
   }

   public Object visit(Field field) {
       //non-scoped
       return tt;
   }

   public Object visit(LibraryMethod method) {
       return handleMethod(method);
   }

   public Object visit(Formal formal) {
       //non-scoped
       return tt;   
   }

   public Object visit(VirtualMethod method) {
	  
	   SymbolTable methodTable = new SymbolTable(method.getName());
	   SymbolTable symbolTable;//child to be
       Type[] paramTypes = null;
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
	  
       return methodTable;
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

   public Object visit(If ifStatement) {        //non-scoped
      
       ifStatement.getCondition().accept(this);
       ifStatement.getOperation().accept(this);

       if (ifStatement.hasElse()) { 
           ifStatement.getElseOperation().accept(this);
       }
      return tt;
   }

   public Object visit(While whileStatement) {
       whileStatement.getCondition().accept(this);//non-scoped
       whileStatement.getOperation().accept(this);
       return tt;
   }

   public Object visit(Break breakStatement) {       //non-scoped
       return tt;
   }

   public Object visit(Continue continueStatement) {       //non-scoped
       return tt;
   }

   public Object visit(StatementsBlock statementsBlock) {
     
       for (Statement statement : statementsBlock.getStatements())
           statement.accept(this);
       return tt;
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
     //  Type returnType = ASTTypeToType(method.getType()); // TODO: maybe add returntype to tt right now
      // addAllSubArraysToTypeTable(method.getType());
       Type[] paramTypes = null;
       if (method.getFormals().size() > 0) {
           paramTypes = new Type[method.getFormals().size()];
           int i = 0;
           for (Formal formal : method.getFormals()) { 
        //       paramTypes[i++] = ASTTypeToType(formal.getType());
               formal.accept(this);
           }
       }
      // tt.methodType(paramTypes, returnType);
       for (Statement statement : method.getStatements()) { 
           statement.accept(this);
       }
       return tt;
   }
}
