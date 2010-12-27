package IC.SymbolTables;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import IC.AST.*;
import IC.TYPE.Kind;
import IC.TYPE.Type;
import IC.TYPE.TypeTable;

public class SymbolTableConstructor implements Visitor {

   private String ICFilePath;
   private SymbolTable st;
   private SymbolTable currentScope;
   private boolean forwardRef;
   
   private List<ASTNode> forwardRefs;
   
 
   public SymbolTableConstructor(String ICFilePath) {
       this.ICFilePath = ICFilePath;
       this.st = new GlobalSymbolTable(ICFilePath);
       this.forwardRefs = new LinkedList<ASTNode>();
       this.forwardRef = false;
       
   }
   
   public Object visit(Program program) {
       SemanticSymbol temp; 
       for (ICClass icClass : program.getClasses()) {
           temp = new SemanticSymbol(TypeTable.classType(icClass), new Kind(Kind.CLASS), icClass.getName(), false);
           if (!st.insert(icClass.getName(),temp)) {  //Symbol already in symbol table
               System.out.println("Error: Illegal redefinition; element " + icClass.getName() + " in line #" + icClass.getLine());
               System.exit(-1);
           }
       }
       
       for (ICClass icClass : program.getClasses()) {
          currentScope = st;
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
       program.setEnclosingScope(st);
       forwardRef = true;
       for (ASTNode node : forwardRefs) { 
           node.accept(this);
       }
       return st;  
   }

   public Object visit(ICClass icClass) {
	   SymbolTable classTable = new ClassSymbolTable(icClass.getName());
	   icClass.setEnclosingScope(classTable);
	   currentScope = classTable;
	   SemanticSymbol thisSym = new SemanticSymbol(TypeTable.getClassType(icClass.getName()), new Kind(Kind.FIELD),"this",false);
	   if (!classTable.insert("this",thisSym)) { 
           System.out.println("semantic error at line " + icClass.getLine()  +"  Fatal error!");
           System.exit(-1);
       }
       for (Field field : icClass.getFields())
    	   if (!classTable.insert(field.getName(), new SemanticSymbol(field.getSemanticType(), new Kind(Kind.FIELD), field.getName(), false))) { 
    	       System.out.println("Error: Illegal redefinition; element " + field.getName() + " in line #" + field.getLine());
               System.exit(-1);
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
       callStatement.getCall().accept(this);
       callStatement.setEnclosingScope(currentScope);
       
	   return null;
   }

   public Object visit(Return returnStatement) {     
       if (returnStatement.hasValue()) 
           returnStatement.getValue().accept(this);
       returnStatement.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(If ifStatement) {        
      SymbolTable ifSymbolTable = new BlockSymbolTable("if");
      ifStatement.setEnclosingScope(ifSymbolTable);
      ifSymbolTable.setParentSymbolTable(currentScope);
      currentScope = ifSymbolTable;
      ifStatement.getCondition().accept(this);
      ifSymbolTable.addChild((SymbolTable)ifStatement.getOperation().accept(this));
      currentScope = ifSymbolTable;

       if (ifStatement.hasElse()) { 
           ifSymbolTable.addChild((SymbolTable)ifStatement.getElseOperation().accept(this));
       }
       
       currentScope = ifSymbolTable;
       return ifSymbolTable;
   }

   public Object visit(While whileStatement) {
	   
      SymbolTable whileSymbolTable = new SymbolTable("while");
      whileStatement.setEnclosingScope(whileSymbolTable); //TODO: maybe currentScope
      whileSymbolTable.setLoop(true);
      whileSymbolTable.setParentSymbolTable(currentScope);
      currentScope = whileSymbolTable;
      whileStatement.getCondition().accept(this);
      whileSymbolTable.addChild((SymbolTable)whileStatement.getOperation().accept(this));
     
      currentScope = whileSymbolTable;
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
       statementsBlock.setEnclosingScope(blockTable);
       blockTable.setParentSymbolTable(currentScope);
       for (Statement statement : statementsBlock.getStatements()) { 
           currentScope = blockTable; 
           
           if (statement instanceof LocalVariable) {
        	   statement.accept(this);
               LocalVariable lv = (LocalVariable)statement;
              
               boolean insertSuccessful = blockTable.insert(lv.getName(), new SemanticSymbol(statement.getSemanticType(), new Kind(Kind.VAR), lv.getName(), false));
               if (!insertSuccessful) { 
                   System.out.println("Error: Illegal redefinition; element " + lv.getName() + " in line #" + lv.getLine());
                   System.exit(-1);
               }
           }
           else {
               symbolTable = (SymbolTable)statement.accept(this);
               if (symbolTable != null){
                   blockTable.addChild((SymbolTable)statement.accept(this));
               }
           }
       }
       
       currentScope = blockTable;
       return blockTable;
       
   }

   public Object visit(LocalVariable localVariable) {
       localVariable.setEnclosingScope(currentScope);
       if (localVariable.hasInitValue()) { 
           localVariable.getInitValue().accept(this);
       }
       localVariable.getType().accept(this);
       return null;
   }

   private SymbolTable getClassSymbolTable(String str, ASTNode startNode) {
       SymbolTable temp = startNode.getEnclosingScope();
       while (temp.getParentSymbolTable() != null) { 
           temp = temp.getParentSymbolTable();
       }
       return temp.symbolTableLookup(str);
   }
   
   public Object visit(VariableLocation location) {
       if (location.getEnclosingScope() == null) { 
           location.setEnclosingScope(currentScope);
       }
       SemanticSymbol temp;
       if (location.isExternal()) {
           if (location.getLocation() instanceof This) { 
               location.getLocation().accept(this);
               String str = location.getLocation().getSemanticType().toString(); //this is the classname, for instance A
               SymbolTable st = getClassSymbolTable(str, location); 
               if (st == null)  {
                   if (forwardRef == true) { 
                       System.out.println("semantic error at line " + location.getLine() + ": unresolved identifier.");
                       System.exit(-1);
                   }
                   else { 
                       forwardRefs.add(location);
                   }
                   
                   //return null;
               }
               else {
                   Type realType = st.lookup(location.getName()).getType();
                   location.setSemanticType(realType);
               }
           }
           else {
               if (location.getLocation() instanceof VariableLocation) {
                   VariableLocation vl = (VariableLocation) location.getLocation();
                   SemanticSymbol symbolLookup = location.getEnclosingScope().lookup(vl.getName());
                   if (symbolLookup == null) {
                       if (forwardRef == true) { 
                           System.out.println("semantic error at line " + location.getLine() + ": unresolved identifier.");
                           System.exit(-1);
                       }
                       forwardRefs.add(location);
                       
                   }
                   else {
                       location.getLocation().setSemanticType(symbolLookup.getType());
                       Type t = location.getLocation().getSemanticType();
                       String str = t.toString(); //this is the classname, for instance A
                       SymbolTable st = getClassSymbolTable(str, location); 
                       if (st == null) { 
                           if (forwardRef == true) { 
                               System.out.println("semantic error at line " + location.getLine() + ": unresolved identifier.");
                               System.exit(-1);
                           }
                           forwardRefs.add(location);
                       } 
                       else {
                           Type realType = st.lookup(location.getName()).getType();
                           location.setSemanticType(realType);
                       }
                   }
               }
               else { // location.getLocation instanceof ArrayLocation
                   location.getLocation().accept(this); 
                   Type t = location.getLocation().getSemanticType();
                   if (t == null) { 
                       if (forwardRef) { 
                           System.out.println("semantic error at line " + location.getLine() + ": unresolved identifier.");
                           System.exit(-1);
                       }
                       else {
                           forwardRefs.add(location);
                       }
                   }
                   else {
                       String className = t.toString();
                       SymbolTable st = getClassSymbolTable(className, location);
                       if (st == null) {
                           if (forwardRef) { 
                               System.out.println("semantic error at line " + location.getLine() + ": unresolved identifier.");
                               System.exit(-1);
                           }
                           else { 
                               forwardRefs.add(location);
                           }
                       }
                       else {
                           SemanticSymbol checkMe = st.lookup(location.getName());
                           if (checkMe == null) { 
                               System.out.println("semantic error at line " + location.getLine() + ": no such field " + location.getName() + " in class " + className + ".");
                               System.exit(-1);
                           }
                           Type realType = checkMe.getType();
                           location.setSemanticType(realType); 
                       }
                   }
               }
           }
       }
       else {
           if ((temp = location.getEnclosingScope().lookup(location.getName())) == null) { 
               if (forwardRef) {
                   System.out.println("Semantic error at line " + location.getLine() + ": var " + location.getName() + " used before definition.");
                   System.exit(-1); 
               }
               else { 
                   forwardRefs.add(location);
                   //return null;
                   
               } 
           }
           else { 
               location.setSemanticType(temp.getType());
           }
       }
       
       if (location.getLocation() != null) {
              location.getLocation().accept(this);
       }
       
       return null;
   }

   public Object visit(ArrayLocation location) {
       location.setEnclosingScope(currentScope);
       if (location.getArray() != null) {
           location.getArray().accept(this);
       }
       location.getIndex().accept(this);
       
       location.setSemanticType(TypeTable.returnElemType(location.getArray().getSemanticType()));
           
       return null;
   }

   public Object visit(StaticCall call) {
	   for (Expression e : call.getArguments()){
    	   e.accept(this);
       }
       call.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(VirtualCall call) {
	   for (Expression e : call.getArguments()){
    	   e.accept(this);
       }
	   if (call.isExternal()) {
	       call.getLocation().accept(this);
	   }
       call.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(This thisExpression) {
       if (!forwardRef) {
           thisExpression.setEnclosingScope(currentScope);
           thisExpression.setSemanticType(currentScope.lookup("this").getType());
       }
       return null;
   }

   public Object visit(NewClass newClass) {
       if (newClass.getEnclosingScope() == null) {
           newClass.setEnclosingScope(currentScope);
       }
       SemanticSymbol temp = currentScope.getGlobal().lookup(newClass.getName());
       if (temp == null) { 
           if (forwardRef == true) { 
               System.out.println("semantic error at line " + newClass.getLine() + ": unresolved identifier.");
               System.exit(-1);
           }
           forwardRefs.add(newClass);
       }
       else {
           newClass.setSemanticType(temp.getType());
       }
       return null; 
   }

   public Object visit(NewArray newArray) {
       newArray.getSize().accept(this);
       newArray.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(Length length) {
       length.setEnclosingScope(currentScope);
       length.getArray().setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(MathBinaryOp binaryOp) {
       binaryOp.setEnclosingScope(currentScope);
       binaryOp.getFirstOperand().accept(this);
       binaryOp.getSecondOperand().accept(this);
       return null;
   }

   public Object visit(LogicalBinaryOp binaryOp) {
       binaryOp.setEnclosingScope(currentScope);
       binaryOp.getFirstOperand().accept(this);
       binaryOp.getSecondOperand().accept(this);
       return null;
   }

   public Object visit(MathUnaryOp unaryOp) {
       unaryOp.setEnclosingScope(currentScope);
       unaryOp.getOperand().accept(this);
       return null;
   }

   public Object visit(LogicalUnaryOp unaryOp) {
       unaryOp.setEnclosingScope(currentScope);
       unaryOp.getOperand().accept(this);
       return null;
   }

   public Object visit(Literal literal) {
       literal.setEnclosingScope(currentScope);
       return null;
   }

   public Object visit(ExpressionBlock expressionBlock) {
       if (expressionBlock.getEnclosingScope() == null) { 
           expressionBlock.setEnclosingScope(currentScope);
       }
       expressionBlock.getExpression().accept(this);
       
       if (expressionBlock.getExpression().getSemanticType() == null) { 
           if (forwardRef == true) { 
               System.out.println("semantic error at line " + expressionBlock.getLine() + ": unresolved identifier.");
               System.exit(-1);
           }
           forwardRefs.add(expressionBlock);
       }
       else {
           expressionBlock.setSemanticType(expressionBlock.getExpression().getSemanticType());
       }
       return null;
   }
   
   private Object handleMethod(Method method) {
	   SymbolTable methodTable = new MethodSymbolTable(method.getName());
	   method.setEnclosingScope(methodTable);
	   methodTable.setParentSymbolTable(currentScope);
	   SymbolTable symbolTable;//child to be
	   
	   currentScope = methodTable;
       if (method.getFormals().size() > 0) {
           for (Formal formal : method.getFormals()) { 
        	   formal.accept(this);
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
        	  
        	   lv.accept(this);
        	   boolean insertSuccessfully = methodTable.insert(lv.getName(), new SemanticSymbol(statement.getSemanticType(), new Kind(Kind.VAR), lv.getName(), false));
        	   if (!insertSuccessfully) { 
                   System.out.println("Error: Illegal redefinition; element " + lv.getName() + " in line #" + lv.getLine());
                   System.exit(-1);
               }
           }
           else {
        	   symbolTable = (SymbolTable)statement.accept(this);
        	   if (symbolTable != null){
        		   methodTable.addChild(symbolTable);
        	   }
        	   currentScope = methodTable;
           }
       }
       return methodTable;
}
   
   
   
}
