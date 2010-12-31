package IC.SemanticChecks;

import IC.AST.*;
import IC.SymbolTables.*;

public class ThisChecker implements Visitor {

    
    public Object visit(Program program) {
       
        Object temp;
        
        for (ICClass icClass : program.getClasses()) {
           if  ((temp = icClass.accept(this)) != null) { 
               return temp;
           }
        }
        return null; // Program is correct semantically for this check
        
    }

    public Object visit(ICClass icClass) {
        
        Object temp;
        
        for (Method method : icClass.getMethods()) {
            temp = method.accept(this);
            
            if  (temp  != null) { 
                return temp;
            }
         }
         return null; // Class is correct semantically for this check
       
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
        Location loc = assignment.getVariable();
        Object temp = null;
        if (loc instanceof VariableLocation) { 
            Expression l = ((VariableLocation)loc).getLocation(); //TODO: wtf..?
        	if (l != null){ 
        		temp = l.accept(this);
        	}
             if (temp != null) { 
                 return temp;
             }
        }
        else {
            temp = ((ArrayLocation) loc).getArray().accept(this);
            if (temp != null) { 
                return temp;
            }
        }
        temp = assignment.getAssignment().accept(this);
        return temp;
    }

    public Object visit(CallStatement callStatement) {
        return null;
    }

    public Object visit(Return returnStatement) {       //non-scoped
        return null;
    }

    public Object visit(If ifStatement) {      
        Object temp;
        temp = ifStatement.getOperation().accept(this);
        
        if (temp != null) { 
            return temp;
        }
        

        if (ifStatement.hasElse()) { 
            temp = ifStatement.getElseOperation().accept(this);
            if (temp != null) { 
                return temp;
            }
        }
        
       return null;
    }

    public Object visit(While whileStatement) {
       Object temp;
       temp = whileStatement.getOperation().accept(this);
       if (temp != null) { 
           return temp;
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
            if (temp != null){
                return temp;
            }
        }
        return null;
    }

    public Object visit(LocalVariable localVariable) {
        return null;
    }

    public Object visit(VariableLocation location) {
        
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
        SymbolTable current = thisExpression.getEnclosingScope();
        while (!(current instanceof MethodSymbolTable)) { 
            current = current.getParentSymbolTable();
        }
        String methodName = current.getId();
        SemanticSymbol temp = current.lookup(methodName);
        if (temp.getKind().toString().compareTo("Static method") == 0) { 
            return thisExpression;
        }        
        return null;
    }

    public Object visit(NewClass newClass) {
        
        return null; 
    }

    public Object visit(NewArray newArray) {
        
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
        Object temp;
        
        temp = expressionBlock.getExpression().accept(this);
        if (temp != null){
            return temp;
        }
        
        return null;
    }
    
    private Object handleMethod(Method method) {
        Object temp;
        for (Statement statement : method.getStatements()) { 
            
            temp = statement.accept(this);
            if (temp != null){
                return temp;
            }
        }
        return null;
    }
    
 }
    

