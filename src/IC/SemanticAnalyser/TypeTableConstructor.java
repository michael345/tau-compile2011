package IC.SemanticAnalyser;

import IC.AST.*;

public class TypeTableConstructor implements Visitor {

    private int depth = 0; // depth of indentation

    private String ICFilePath;
    private TypeTable tt;

    public TypeTableConstructor(String ICFilePath) {
        this.ICFilePath = ICFilePath;
        this.tt = new TypeTable();
    }


    private Type ASTTypeToType(IC.AST.Type type) {
        int dim = type.getDimension();
        int i = 0;
        Type temp = stringToType(type.getName());
        for (i = 0; i<dim; i++) { 
            temp = new ArrayType(temp,0);
        }
        return temp;
    }
    
    private Type addAllSubArraysToTypeTable(IC.AST.Type type) { 
        int dim = type.getDimension();
        Type temp = stringToType(type.getName());
        // adding the basic element of the array (if array at all)
        if (type.getName().compareTo("int") == 0) { 
            tt.primitiveType(new IntType(0));
        }
        else if (type.getName().compareTo("boolean") == 0) { 
            tt.primitiveType(new BoolType(0));
        }
        else if (type.getName().compareTo("string") == 0) { 
            tt.primitiveType(new StringType(0));
        }
        else if (type.getName().compareTo("void") == 0) { 
            tt.primitiveType(new VoidType(0));
        }
         
        
        
        while (dim > 0) { 
            tt.arrayType(temp);
            temp = new ArrayType(temp,0); //TODO: remove this: id is irrelevant in this line
            dim--;
        }
        return temp;
    }
    
    private Type stringToType(String type) { // TODO: make sure index doesnt get fucked
        if (type.compareTo("int") == 0) { 
            return new IntType(0);
        }
        else if (type.compareTo("boolean") == 0) { 
            return new BoolType(0);
        }
        else if (type.compareTo("string") == 0) { 
           return new StringType(0);
        }
        else if (type.compareTo("void") == 0) { 
            return new VoidType(0);
        }
        else if (type.compareTo("null") == 0) { 
            return new NullType(0);
        }
        else { 
            return tt.getClassType(type); // if this is null, class 'type' was never defined and added to type table.
        }
    }
    
    public Object visit(Program program) {
        //output.append("Abstract Syntax Tree: " + ICFilePath + "\n");
        for (ICClass icClass : program.getClasses()) {
            tt.classType(icClass);
        }
        
        for (ICClass icClass : program.getClasses()) {
            icClass.accept(this);
        }
        return tt;
    }

    public Object visit(ICClass icClass) {
        icClass.setSemanticType(tt.getClassType(icClass.getName()));
        for (Field field : icClass.getFields())
            field.accept(this);
        for (Method method : icClass.getMethods())
           method.accept(this);
        return tt;
    }

    public Object visit(PrimitiveType type) {
        Type temp = stringToType(type.getName());
        type.setSemanticType(tt.primitiveType(temp));
        return tt; 
    }

    public Object visit(UserType type) {
        type.setSemanticType(tt.getClassType(type.getName()));
        return tt;
    }

    public Object visit(Field field) {
        IC.AST.Type type = field.getType();
        field.setSemanticType(addAllSubArraysToTypeTable(type));
        return tt;
    }

    public Object visit(LibraryMethod method) {
        method.setSemanticType((IC.SemanticAnalyser.Type) handleMethod(method));
        return tt;
    }

    public Object visit(Formal formal) {
        IC.AST.Type type = formal.getType();
        formal.setSemanticType(addAllSubArraysToTypeTable(type));
        return tt;   
    }

    public Object visit(VirtualMethod method) {
        method.setSemanticType((IC.SemanticAnalyser.Type) handleMethod(method));
        return tt;
    }

    public Object visit(StaticMethod method) {
        method.setSemanticType((IC.SemanticAnalyser.Type) handleMethod(method));
        return tt;
    }


   

    public Object visit(Assignment assignment) {
        assignment.getAssignment().accept(this);
        assignment.setSemanticType(assignment.getAssignment().getSemanticType()); 
        //TODO: does this work?
        return tt;
    }

    public Object visit(CallStatement callStatement) {
        // TODO: set callStatement.setSemanticType() to be the return value of the call?
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
        localVariable.setSemanticType(addAllSubArraysToTypeTable(localVariable.getType()));
        return tt;
    }

    public Object visit(VariableLocation location) {
        if (location.getLocation() != null) 
           location.getLocation().accept(this);
       return tt;
    }

    public Object visit(ArrayLocation location) {
        //TODO set location.setSemanticType to something
        return tt;
    }

    public Object visit(StaticCall call) {
      //TODO: set call.setSemanticType to something
        return tt;
    }

    public Object visit(VirtualCall call) {
        //TODO: set call.setSemanticType to something
        return tt;
    }

    public Object visit(This thisExpression) {
      //TODO: set call.setSemanticType to something - or not
        return tt;
    }

    public Object visit(NewClass newClass) {
        newClass.setSemanticType(tt.getClassType(newClass.getName())); //TODO: might be errornous :o
        return tt; // TODO: probably handled when class was declared
    }

    public Object visit(NewArray newArray) {
        newArray.setSemanticType(addAllSubArraysToTypeTable(newArray.getType()));
        return tt;
    }

    public Object visit(Length length) {
        // TODO: maybe set length.setSemanticType to TypeInt ?
        return tt;
    }

    public Object visit(MathBinaryOp binaryOp) {
        binaryOp.setSemanticType(tt.primitiveType(new IntType(0)));
        binaryOp.getFirstOperand().accept(this);
        binaryOp.getSecondOperand().accept(this);        
        return tt;
    }

    public Object visit(LogicalBinaryOp binaryOp) {
        binaryOp.setSemanticType(tt.primitiveType(new BoolType(0)));
        binaryOp.getFirstOperand().accept(this);
        binaryOp.getSecondOperand().accept(this); 
        return tt;
    }

    public Object visit(MathUnaryOp unaryOp) {
        unaryOp.setSemanticType(tt.primitiveType(new IntType(0)));
        unaryOp.getOperand().accept(this);
        return tt;
    }

    public Object visit(LogicalUnaryOp unaryOp) {
        unaryOp.setSemanticType(tt.primitiveType(new BoolType(0)));
        unaryOp.getOperand().accept(this);
        return tt;
    }

    public Object visit(Literal literal) {
        String bah = literal.getType().getDescription();
        IC.SemanticAnalyser.Type temp = null;
        if (bah.compareTo("Literal") == 0) 
            temp = tt.primitiveType(new NullType(0));
        else if (bah.compareTo("Boolean literal") == 0) 
            temp = tt.primitiveType(new BoolType(0));
        else if (bah.compareTo("String literal") == 0) 
            temp = tt.primitiveType(new StringType(0));
        else if (bah.compareTo("Integer literal") == 0) 
            temp = tt.primitiveType(new IntType(0));
        literal.setSemanticType(temp);
        return tt;
    }

    public Object visit(ExpressionBlock expressionBlock) {
        expressionBlock.getExpression().accept(this);
        return tt;
    }
    
    private Object handleMethod(Method method) {
        Type returnType = ASTTypeToType(method.getType()); // TODO: maybe add returntype to tt right now
        addAllSubArraysToTypeTable(method.getType());
        Type[] paramTypes = null;
        if (method.getFormals().size() > 0) {
            paramTypes = new Type[method.getFormals().size()];
            int i = 0;
            for (Formal formal : method.getFormals()) { 
                paramTypes[i++] = ASTTypeToType(formal.getType());
                formal.accept(this);
            }
        }
        Type temp = tt.methodType(paramTypes, returnType);
        for (Statement statement : method.getStatements()) { 
            statement.accept(this);
        }
        return temp;
    }
}