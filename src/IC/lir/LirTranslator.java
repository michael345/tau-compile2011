package IC.lir;

import java.util.LinkedList;
import java.util.List;

import IC.BinaryOps;
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
import IC.TYPE.TypeTable;
import IC.TYPE.VoidType;
import IC.lir.instruction.BinaryInstrucionEnum;
import IC.lir.instruction.BinaryInstruction;
import IC.lir.instruction.ConditionLabelInstruction;
import IC.lir.instruction.JumpInstruction;
import IC.lir.instruction.JumpInstructionEnum;
import IC.lir.instruction.LIRInstruction;
import IC.lir.instruction.LabelInstruction;
import IC.lir.instruction.LibraryInstruction;
import IC.lir.instruction.MoveArrayInstruction;
import IC.lir.instruction.MoveFieldInstruction;
import IC.lir.instruction.MoveInstruction;
import IC.lir.instruction.UnaryInstruction;
import IC.lir.instruction.UnaryInstructionEnum;
import IC.lir.parameter.ArrayPair;
import IC.lir.parameter.FieldPair;
import IC.lir.parameter.LIRImmediate;
import IC.lir.parameter.LIRLabel;
import IC.lir.parameter.LIRMemory;
import IC.lir.parameter.LIROperand;
import IC.lir.parameter.LIRParameter;
import IC.lir.parameter.LIRReg;
import IC.lir.parameter.LIRString;
import IC.lir.parameter.LIRStringLabel;
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
        LIRDispatchTable table = new LIRDispatchTable(icClass.getName());
        lirProg.addDispatchTable(table);
        for (Method method : icClass.getMethods()) { 
           method.accept(this);
        }
        return null;
    }

    



    public Object visit(Field field) {
        return null;
    }

    
    public Object visit(VirtualMethod method) {
        // 1.create label for method
        lirProg.addCommentIntsruction(method.getName() + " in " + method.getEnclosingScope().getParentSymbolTable().getId());
        String name = method.getEnclosingScope().getParentSymbolTable().getId() + "_" + method.getName();
        LabelInstruction entryPoint = new LabelInstruction(name);
        lirProg.addInstruction(entryPoint);
        // 2. add label to Dispatch Vector of the class we're in
        //ClassLayout temp = lirProg.getClassLayout(method.getEnclosingScope().getParentSymbolTable().getId());
        //temp = temp;
        lirProg.getDispatchTable(method.getEnclosingScope().getParentSymbolTable().getId()).addLabel(new LIRLabel(name));
        // 3. visit the method statements

        // TODO Auto-generated method stub
        return null;
    }

    
    public Object visit(StaticMethod method) { //unfinished
     // create label for method
        LabelInstruction entryPoint;
        if (method.getName().compareTo("main") == 0) {          
            entryPoint = new LabelInstruction("ic_main");
            lirProg.addCommentIntsruction("main in " + method.getEnclosingScope().getParentSymbolTable().getId());
        }
        else { 
            entryPoint = new LabelInstruction(method.getName());
            lirProg.addCommentIntsruction(method.getName() + " in " + method.getEnclosingScope().getParentSymbolTable().getId());
        }
        
        lirProg.addInstruction(entryPoint);
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
        lirProg.addCommentIntsruction("Assignment on line " + assignment.getLine());
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
            else if (initVal instanceof LIRString) { 
                reg = new LIRReg();
                MoveInstruction movie = new MoveInstruction((LIRStringLabel) (((LIRString) (initVal)).getLabel()), reg);
                lirProg.addInstruction(movie);
            }
            else if (initVal instanceof LIRMemory) { 
                
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

    
    public Object visit(VariableLocation location) { // add support for fields
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
        //else if (/*TODO: check if location is field or not*/) { 
            
            
        //}
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
        lirProg.addCommentIntsruction("Math binary operation '" + binaryOp.getOperator().getOperatorString() + "' at line " + binaryOp.getLine());
        Object second = binaryOp.getSecondOperand().accept(this);
        Object first = binaryOp.getFirstOperand().accept(this);
        LIRReg firstReg = null, secondReg = null;
        BinaryOps operator = binaryOp.getOperator();
        if (binaryOp.getSemanticType().equals(TypeTable.stringType)) { //"Hello" + "Kitty"
            secondReg = putInRegister(second);
            firstReg = putInRegister(first);
            LIRReg result = new LIRReg();
            LibraryInstruction inst = new LibraryInstruction("stringCat",result,firstReg,secondReg);
            lirProg.addInstruction(inst);
            firstReg.makeFreeRegister();
            secondReg.makeFreeRegister();
            return result;
        }
        else { 
            secondReg = putInRegister(second);
            firstReg = putInRegister(first);
            BinaryInstruction inst = new BinaryInstruction(secondReg, firstReg, getBinaryEnum(operator));
            lirProg.addInstruction(inst);
            secondReg.makeFreeRegister();
        }
        
        return firstReg;
    }
    
    public BinaryInstrucionEnum getBinaryEnum(BinaryOps operator ) { 
        switch (operator) { 
        case PLUS: 
            return BinaryInstrucionEnum.ADD;
        case MINUS:
            return BinaryInstrucionEnum.SUB;
        case MOD:
            return BinaryInstrucionEnum.MOD;
        case LAND:
            return BinaryInstrucionEnum.AND;
        case LOR: 
            return BinaryInstrucionEnum.OR;
        case DIVIDE:
            return BinaryInstrucionEnum.DIV;
        case MULTIPLY:
            return BinaryInstrucionEnum.MUL; 
        default:
            return BinaryInstrucionEnum.COMPARE;
        }
    }
    
    public JumpInstructionEnum getJumpEnum(BinaryOps operator ) { 
        switch (operator) { 
        case GT: 
            return JumpInstructionEnum.Greater;
        case GTE:
            return JumpInstructionEnum.GreaterEqual;
        case LT:
            return JumpInstructionEnum.Less;
        case LTE:
            return JumpInstructionEnum.LessEqual;
        case LOR: 
            return JumpInstructionEnum.True;
        case LAND:
            return JumpInstructionEnum.True;
        case NEQUAL:
            return JumpInstructionEnum.False;
        case EQUAL:
            return JumpInstructionEnum.True;
        default:
            return JumpInstructionEnum.True;
        }
    }
    
    

    
    public Object visit(LogicalBinaryOp binaryOp) {
        lirProg.addCommentIntsruction("Logical Binary Op at line " + binaryOp.getLine());
        Object first = binaryOp.getFirstOperand().accept(this);
        Object second = null;
        BinaryOps operator = binaryOp.getOperator();
        LIRReg firstReg = null, secondReg = null;
        
        if (binaryOp.getOperator() == BinaryOps.LAND) { }       //TODO: Need to do this shit
        else if (binaryOp.getOperator() == BinaryOps.LOR) { }   //TODO: Need to do this shit
        else { 
            second = binaryOp.getSecondOperand().accept(this);
            secondReg = putInRegister(second);
            firstReg = putInRegister(first);
            BinaryInstruction comparison = new BinaryInstruction(firstReg,secondReg,getBinaryEnum(operator)); //Should be Compare
            ConditionLabelInstruction condLabel = new ConditionLabelInstruction(); 
            LabelInstruction endLabel = new LabelInstruction("endLabel");
            JumpInstruction jumpToFalse = new JumpInstruction(getJumpEnum(operator),condLabel.getLabel()); //TODO: check, maybe completely wrong jump enum recieved, needs testing
            JumpInstruction jumpToEnd = new JumpInstruction(JumpInstructionEnum.Unconditional, endLabel.getLabel());
            MoveInstruction trueCompareInstruction = new MoveInstruction(new LIRImmediate(1),secondReg);
            MoveInstruction falseCompareInstruction = new MoveInstruction(ZeroImmediate.getInstance(),secondReg);
            
            
            // remember, Compare = op2 - op1 
            lirProg.addInstruction(comparison);
            lirProg.addInstruction(jumpToFalse);
            lirProg.addInstruction(trueCompareInstruction);
            lirProg.addInstruction(jumpToEnd);
            lirProg.addInstruction(condLabel);
            lirProg.addInstruction(falseCompareInstruction);
            lirProg.addInstruction(endLabel);
            firstReg.makeFreeRegister(); //secondReg holds result
        }

        
        return secondReg;
    }

    
    public Object visit(MathUnaryOp unaryOp) {
        Object operand = unaryOp.getOperand().accept(this); //ClassCastException expected
        if (operand == null) { 
            System.out.println("Fuck you! mathUnaryOp");
            System.exit(-1);
        }
        LIRReg operandReg = null;  
        LIRReg resultReg = getZeroRegister(); //preparing the result register
        LIRInstruction substract;
        
        if (operand instanceof LIRImmediate) { // happens on input: "int y = -5;
            // operand is immediate, no need to be in register
            substract = new BinaryInstruction((LIRImmediate) operand, resultReg, BinaryInstrucionEnum.SUB); 
        }
        else { 
            operandReg = (LIRReg) operand; //TODO: Do other cases exist? other than immediate or reg?
            substract = new BinaryInstruction(operandReg, resultReg, BinaryInstrucionEnum.SUB); 
        }
        lirProg.addInstruction(substract);
        
        if (operandReg == null) { }
        else {
            operandReg.makeFreeRegister();
        }
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
            strParam = lirProg.addStringLiteral(strParam);
            param = strParam;
        }
        return param;
        
    }

    
    public Object visit(ExpressionBlock expressionBlock) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /*
     * Allocates register, with the value 0, and returns it. this function adds the relevant instruction to the LirProgram
     * e.g. mov 0 Rtarger 
     */
    public LIRReg getZeroRegister() { 
        LIRReg resultReg = new LIRReg();
        LIRImmediate zero = ZeroImmediate.getInstance(); 
        LIRInstruction loadZero = new MoveInstruction(zero,resultReg);
        lirProg.addInstruction(loadZero);
        return resultReg;
    }
    
    public LIRReg putInRegister(Object input) { 
        if (input == null) {
            return null;
        }
        else if (input instanceof LIRReg) { 
           return (LIRReg) input; 
        }
        else if (input instanceof LIRImmediate) {
            LIRReg reg = new LIRReg();
            MoveInstruction move = new MoveInstruction((LIRImmediate) input, reg);
            lirProg.addInstruction(move);
            return reg;
        }
        else if (input instanceof LIRMemory) { 
            LIRReg reg = new LIRReg();
            MoveInstruction move = new MoveInstruction((LIRMemory) input, reg);
            lirProg.addInstruction(move);
            return reg;
        }
        else if (input instanceof LIRStringLabel) { 
            LIRReg reg = new LIRReg();
            MoveInstruction move = new MoveInstruction((LIRStringLabel) input, reg);
            lirProg.addInstruction(move);
            return reg;
        }
        else if (input instanceof LIRString) { 
            LIRReg reg = new LIRReg();
            MoveInstruction move = new MoveInstruction(((LIRString) input).getLabel(), reg);
            lirProg.addInstruction(move);
            return reg;
        }
        
        else { 
            return null;
            //TODO: Handle pairs? fields? etc
        }
        
    }

}
