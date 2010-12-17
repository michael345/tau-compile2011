package IC.SemanticAnalyser;

import IC.AST.*;

public class SymbolTableConstructor implements Visitor {

   private String ICFilePath;
   private SymbolTable st;
   private TypeTable tt;

 
   public SymbolTableConstructor(String ICFilePath, TypeTable tt) {
       this.ICFilePath = ICFilePath;
       this.st = new SymbolTable("global");
       this.tt = tt;
   }
   
   public Object visit(Program program) {
       //output.append("Abstract Syntax Tree: " + ICFilePath + "\n");
       
       SemanticSymbol temp; 
       
       for (ICClass icClass : program.getClasses()) {
           temp = new SemanticSymbol(tt.getClassType(icClass.getName()),new Kind(Kind.CLASS),icClass.getName(),false);
           icClass.setEnclosingScope(st);
           if (st.insert(icClass.getName(),temp)) { 
               ;// good;
           }
           else { 
               // TODO: more than one class with same name, probably throw error
           }
       }
       
       for (ICClass icClass : program.getClasses()) {
           st.addChild((SymbolTable) icClass.accept(this));
       }
       program.setEnclosingScope(st);
       return st;
   }

   public Object visit(ICClass icClass) {
       SymbolTable curSt = new SymbolTable(icClass.getName());
       SemanticSymbol temp;
       for (Field field : icClass.getFields()) {
           temp = new SemanticSymbol(field.getSemanticType(),new Kind(Kind.FIELD),field.getName(),false);
           curSt.insert(field.getName(),temp); //TODO: Requires checking, did this while being talked to
       }
       for (Method method : icClass.getMethods()) {
           if (method instanceof VirtualMethod) {
               temp = new SemanticSymbol(method.getSemanticType(),new Kind(Kind.VIRTUALMETHOD),method.getName(),false);         
           }
           else { 
               temp = new SemanticSymbol(method.getSemanticType(),new Kind(Kind.STATICMETHOD),method.getName(),false);
           }
           curSt.insert(method.getName(), temp); //TODO: if returned false - do something 
        }
       
       for (Method method : icClass.getMethods()) {
          st.addChild((SymbolTable) method.accept(this));
       }
       return tt;
   }
   
   //TODO: we did up to here
   public Object visit(PrimitiveType type) {
       //Type temp = stringToType(type.getName());
       //tt.primitiveType(temp);
       return tt; // maybe the type we created?
   }

   public Object visit(UserType type) {
       // right now we ignore it, cause it will be added upon definition of class "type"
       return tt;
   }

   public Object visit(Field field) {
       IC.AST.Type type = field.getType();
       //addAllSubArraysToTypeTable(type);
       return tt;
   }

   public Object visit(LibraryMethod method) {
       return handleMethod(method);
   }

   public Object visit(Formal formal) {
       IC.AST.Type type = formal.getType();
       //addAllSubArraysToTypeTable(type);
       return tt;   
   }

   public Object visit(VirtualMethod method) {
       return handleMethod(method);
   }

   public Object visit(StaticMethod method) {
       return handleMethod(method);
   }


  

   public Object visit(Assignment assignment) {
       return tt;
   }

   public Object visit(CallStatement callStatement) {
       return tt;
   }

   public Object visit(Return returnStatement) {
       if (returnStatement.hasValue())
           returnStatement.getValue().accept(this);
       return tt;
   }

   public Object visit(If ifStatement) {
      
       ifStatement.getCondition().accept(this);
       ifStatement.getOperation().accept(this);

       if (ifStatement.hasElse()) { 
           ifStatement.getElseOperation().accept(this);
       }
      return tt;
   }

   public Object visit(While whileStatement) {
       whileStatement.getCondition().accept(this);
       whileStatement.getOperation().accept(this);
       return tt;
   }

   public Object visit(Break breakStatement) {
       return tt;
   }

   public Object visit(Continue continueStatement) {
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
