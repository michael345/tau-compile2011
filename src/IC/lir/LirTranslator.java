package IC.lir;

import IC.AST.ArrayLocation;
import IC.AST.Assignment;
import IC.AST.Break;
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
import IC.AST.While;
import IC.TYPE.BoolType;
import IC.TYPE.IntType;
import IC.TYPE.NullType;
import IC.TYPE.StringType;
import IC.TYPE.VoidType;
import IC.lir.instruction.BinaryInstrucionEnum;
import IC.lir.instruction.BinaryInstruction;
import IC.lir.instruction.CommentInstruction;
import IC.lir.instruction.LIRInstruction;
import IC.lir.instruction.MoveArrayInstruction;
import IC.lir.instruction.MoveFieldInstruction;
import IC.lir.instruction.MoveInstruction;
import IC.lir.instruction.UnaryInstruction;
import IC.lir.instruction.UnaryInstructionEnum;
import IC.lir.parameter.ArrayPair;
import IC.lir.parameter.FieldPair;
import IC.lir.parameter.LIRImmediate;
import IC.lir.parameter.LIRMemory;
import IC.lir.parameter.LIROperand;
import IC.lir.parameter.LIRParameter;
import IC.lir.parameter.LIRReg;
import IC.lir.parameter.LIRString;
import IC.lir.parameter.ZeroImmediate;

public class LirTranslator implements IC.AST.Visitor{

    LirProgram lirProg;
    FieldPair lastPair;
    ArrayPair lastArrayPair;
    
    public Object visit(Program program) {
       // debug this so much
       lirProg = LirProgram.getInstance();
        
        while (lirProg.getClassLayouts().size() < program.getClasses().size() -1) { 
            for (ICClass icClass : program.getClasses()) { 
                if (lirProg.getClassLayout(icClass.getName()) != null) { 
                    continue;
                }
                else if (icClass.hasSuperClass() && null == lirProg.getClassLayout(icClass.getSuperClassName())) { 
                    continue;
                }
                else if (icClass.getName().compareTo("Library") == 0) { 
                    continue;
                }
                else { 
                    ClassLayout classLayout;
                    if (icClass.hasSuperClass()) { 
                        classLayout = new ClassLayout(icClass.getName(),lirProg.getClassLayout(icClass.getSuperClassName()));
                    } 
                    else { 
                        classLayout = new ClassLayout(icClass.getName());
                    }
                    for (Field field : icClass.getFields()) { 
                        classLayout.addFieldOffset(field.getName());
                    }
                    for (Method method : icClass.getMethods()) { 
                        if (method instanceof VirtualMethod) { // maybe need static. if do - remove this if
                            classLayout.addMethodOffset(method.getName());
                        }
                    }
                    lirProg.getClassLayouts().add(classLayout);
                }
            }
        }
        
        /*
         * debugging section
         */ 
//        for (ClassLayout classLayout : lirProg.getClassLayouts()) { 
//            System.out.println(classLayout.toString());
//            System.out.println();
//
//        }
        // end of debug
      
        for (ICClass icClass : program.getClasses()) { 
            if (icClass.getName().compareTo("Library") == 0) {}
            else {
                icClass.accept(this);
            }
        }
        return null;
    }

    
    public Object visit(ICClass icClass) {
        for (Method method : icClass.getMethods()) { 
           method.accept(this);
        }
        return null;
    }

    



    public Object visit(Field field) {
        return null;
    }

    
    public Object visit(VirtualMethod method) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(StaticMethod method) { //temporary
        for (Statement statement :method.getStatements()) { 
            statement.accept(this);
        }
        return null;
    }

    
    public Object visit(LibraryMethod method) {
        return null;
    }

    
    public Object visit(Formal formal) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(PrimitiveType type) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(UserType type) {
        return null;
    }

    
    public Object visit(Assignment assignment) { //TODO: Finish this
        CommentInstruction comment = new CommentInstruction("Assignment on line " + assignment.getLine());
        lirProg.addInstruction(comment);
        Object rhs = assignment.getAssignment().accept(this);
        LIRReg rightReg = null;
        if (rhs instanceof LIRReg) { 
            rightReg = (LIRReg) rhs;
        }
        else if (rhs instanceof LIRMemory) { 
             //TODO:
        }
        else if (rhs instanceof LIRImmediate) { 
            rightReg = new LIRReg();
            LIRInstruction move1 = new MoveInstruction((LIRImmediate) rhs, rightReg);
            lirProg.addInstruction(move1);
        }
        
        
        Object lhs = assignment.getVariable().accept(this);
        LIRReg lhsReg = (LIRReg) lhs;
        if (assignment.getVariable() instanceof ArrayLocation) {            
            MoveArrayInstruction move = new MoveArrayInstruction(lastArrayPair, rightReg, true);
            lirProg.addInstruction(move);
            lastArrayPair.free();
            lastArrayPair =  null;
        }
        else  { //Variable Location
            if (((VariableLocation) assignment.getVariable()).isExternal()) {
                MoveFieldInstruction move = new MoveFieldInstruction(lastPair, rightReg, true);
                lirProg.addInstruction(move);
                lastPair.getLocation().makeFreeRegister();
                lastPair = null;

            }
            else {
                LIRMemory mem = new LIRMemory(((VariableLocation) assignment.getVariable()).getName());
                MoveInstruction move = new MoveInstruction(rightReg,mem);
                lirProg.addInstruction(move);
            }
        }
        
        rightReg.makeFreeRegister();
        lhsReg.makeFreeRegister();
        if (lastPair != null) { 
            lastPair.free();
        }
        if (lastArrayPair != null) {
            lastArrayPair.free();
        }
        
        
               
        return null;
    }

    
    public Object visit(CallStatement callStatement) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(Return returnStatement) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(If ifStatement) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(While whileStatement) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(Break breakStatement) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(Continue continueStatement) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(StatementsBlock statementsBlock) {
        for (Statement st : statementsBlock.getStatements()) { 
            st.accept(this);
        }
        return null;
    }

    
    public Object visit(LocalVariable localVariable) {
        LIRReg reg = null;
        LIRMemory mem = null;
        if (localVariable.hasInitValue()) { 
            Object initVal = localVariable.getInitValue().accept(this);
            if (initVal instanceof LIRImmediate) { 
                   reg = new LIRReg();
                   MoveInstruction movie = new MoveInstruction((LIRImmediate)initVal, reg);
                   lirProg.addInstruction(movie);
            }
            else { 
                reg = (LIRReg) initVal;
            }
            mem = new LIRMemory(localVariable.getName());
            MoveInstruction move = new MoveInstruction(reg,mem);
            lirProg.addInstruction(move);
            reg.makeFreeRegister();
        }
        
        return mem;
    }

    
    public Object visit(VariableLocation location) {
        LIRMemory mem = null;
        Object temp;
        LIRReg reg = null;
        if (location.isExternal()) { 
            if ((temp = location.getLocation().accept(this)) == null ) {
                System.out.println("Motherfuck! variableLocation");
                System.exit(-1);
            }
            LIRReg locationReg = (LIRReg) temp; //R1
            reg = new LIRReg(); //R2  
            int offset = lirProg.getClassLayout(location.getLocation().getSemanticType().toString()).getFieldToOffset().get(location.getName());
            LIRImmediate offsetImmediate = new LIRImmediate(offset);
            if (lastPair != null) { 
                lastPair.getLocation().makeFreeRegister();   
            }
            FieldPair pair = new FieldPair(locationReg,offsetImmediate); 
            lastPair = pair;
            MoveFieldInstruction mfi = new MoveFieldInstruction(pair, reg, false);
            lirProg.addInstruction(mfi);
            
            
        }
        else { 
            mem = new LIRMemory(location.getName());
            reg = new LIRReg();
            MoveInstruction move = new MoveInstruction(mem, reg);
            lirProg.addInstruction(move);
        }
        return reg;
    }

    
    public Object visit(ArrayLocation location) {
        Expression array = location.getArray(); 
        Object tempLocation = array.accept(this); //Register
        Object tempIndex = location.getIndex().accept(this); //Register or Immediate
        ArrayPair arrayPair = new ArrayPair((LIRReg) tempLocation,(LIROperand) tempIndex);
        lastArrayPair = arrayPair;
        
        LIRReg result = new LIRReg();
        MoveArrayInstruction move = new MoveArrayInstruction(arrayPair, result, false);
        lirProg.addInstruction(move);
        
        
        return result;
    }

    
    public Object visit(StaticCall call) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(VirtualCall call) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(This thisExpression) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(NewClass newClass) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(NewArray newArray) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(Length length) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(MathBinaryOp binaryOp) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(LogicalBinaryOp binaryOp) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(MathUnaryOp unaryOp) {
        Object operand = unaryOp.getOperand().accept(this); //ClassCastException expected
        if (operand == null) { 
            System.out.println("Fuck you! mathUnaryOp");
            System.exit(-1);
        }
        
        LIRReg operandReg = (LIRReg) operand; //TODO: Is this always register?
        
        
        LIRImmediate zero = ZeroImmediate.getInstance(); 
        
        LIRReg resultReg = new LIRReg();
        LIRInstruction second = new MoveInstruction(zero,resultReg);
        lirProg.addInstruction(second);
        
        LIRInstruction substract = new BinaryInstruction(operandReg, resultReg, BinaryInstrucionEnum.SUB); 
        
        
        lirProg.addInstruction(substract);
        
        operandReg.makeFreeRegister();
        return resultReg;
    }

    
    public Object visit(LogicalUnaryOp unaryOp) {
        Object operand = unaryOp.getOperand().accept(this); //ClassCastException expected
        if (operand == null) { 
            System.out.println("Fuck you! logicalUnaryOp");
            System.exit(-1);
        }
        
        LIRReg operandReg = (LIRReg) operand;
        
        LIRInstruction negation = new UnaryInstruction(operandReg,UnaryInstructionEnum.NEG);
        lirProg.addInstruction(negation);
        return operandReg;
    }

    
    public Object visit(Literal literal) {
        LIRParameter param = null;
        if (literal.getSemanticType().equals(new NullType(0))) { 
            param = new LIRImmediate(0); //convention
        }
        else if (literal.getSemanticType().equals(new BoolType(0))) { 
            boolean value = (Boolean) literal.getValue();
            if (value) { 
                param = new LIRImmediate(1);
            }
            else { 
                param = new LIRImmediate(0);
            }
        }
        else if (literal.getSemanticType().equals(new IntType(0))) { 
            String strValue = (String) literal.getValue();
            int value = Integer.parseInt(strValue);
            param = new LIRImmediate(value);
        }
        else if (literal.getSemanticType().equals(new VoidType(0))) { 
            //TODO: Make sure this isnt needed.
        }
        else if (literal.getSemanticType().equals(new StringType(0))) { 
            String strValue = (String) literal.getValue();
            LIRString strParam = new LIRString(strValue);
            lirProg.addStringLiteral(strParam);
            param = strParam;
            
        }
        return param;
        
    }

    
    public Object visit(ExpressionBlock expressionBlock) {
        // TODO Auto-generated method stub
        return null;
    }

}
