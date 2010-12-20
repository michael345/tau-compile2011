package IC.SymbolTables;

import java.util.ArrayList;
import java.util.List;

import IC.AST.*;
import IC.TYPE.Kind;

public class SymbolTableConstructor implements Visitor {

   private String ICFilePath;
   private SymbolTable st;
   private int blockIndex = 0;
   private List<SymbolTable> tempFix;
   
   


 
   public SymbolTableConstructor(String ICFilePath) {
       this.ICFilePath = ICFilePath;
       this.st = new GlobalSymbolTable(ICFilePath);
       tempFix = new ArrayList<SymbolTable>();
       
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
          st.addChild((ClassSymbolTable)icClass.accept(this));
       }
       
       for (ICClass icClass : program.getClasses()) {
           if (icClass.hasSuperClass()) {
               SymbolTable son = st.removeChild(icClass.getName());
               String dad = icClass.getSuperClassName();
               SymbolTable dadTable = st.symbolTableLookup(dad);
               dadTable.addChild(son);
               
           }
           
        }
       
       
       fix(st);
       program.setEnclosingScope(st);
       return st;  
   }

   public Object visit(ICClass icClass) {
	   SymbolTable classTable = new ClassSymbolTable(icClass.getName());
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
    	   classTable.addChild((MethodSymbolTable)method.accept(this));
       }
       icClass.setEnclosingScope(classTable);
       return classTable;
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
       //non-scoped
	   return null;
   }

   public Object visit(CallStatement callStatement) {
       //non-scoped
	   return null;
   }

   public Object visit(Return returnStatement) {       //non-scoped
       if (returnStatement.hasValue())
           returnStatement.getValue().accept(this);
       return null;
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
       return null;
   }

   public Object visit(Continue continueStatement) {       //non-scoped
       return null;
   }

   public Object visit(StatementsBlock statementsBlock) {
     
       SymbolTable blockTable = new BlockSymbolTable("block"); // TODO: want this ID to be statement block in "sfunc"
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
       //addAllSubArraysToTypeTable(localVariable.genullype());
       return null;
   }

   public Object visit(VariableLocation location) {
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

   public Object visit(This thisExpression) {
       return null;
   }

   public Object visit(NewClass newClass) {
       return null; // TODO: probably handled when class was declared
   }

   public Object visit(NewArray newArray) {
       //addAllSubArraysToTypeTable( newArray.genullype());
       return null;
   }

   public Object visit(Length length) {
       return null;
   }

   public Object visit(MathBinaryOp binaryOp) {       
       return null;
   }

   public Object visit(LogicalBinaryOp binaryOp) {
       return null;
   }

   public Object visit(MathUnaryOp unaryOp) {
       return null;
   }

   public Object visit(LogicalUnaryOp unaryOp) {
       return null;
   }

   public Object visit(Literal literal) {
       return null;
   }

   public Object visit(ExpressionBlock expressionBlock) {
       expressionBlock.getExpression().accept(this);
       return null;
   }
   
   private Object handleMethod(Method method) {
	   SymbolTable methodTable = new MethodSymbolTable(method.getName());
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
   
   private void fix(SymbolTable st){
	   if ((st.getId().equals("while")) || (st.getId().equals("if"))){
		   for (SymbolTable s : st.getChildren()) {
			   if (s!=null){
			   tempFix.add(s);
			   }   
		}  
	   }
	   else{
	   for (SymbolTable s : st.getChildren()) {
			   fix(s);
	   		}	
	   
	   for (SymbolTable s : tempFix) {
			st.removeChild(s.getParentSymbolTable().getId());
			st.addChild(s);
		   }
	   tempFix.clear();
	   for (SymbolTable s : st.getChildren()) {
		   fix(s);
   		}
	   }
	  

   }
   
}
