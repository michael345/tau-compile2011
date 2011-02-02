package IC.lir;

import java.util.LinkedList;

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
import IC.SymbolTables.SemanticSymbol;
import IC.SymbolTables.SymbolTable;
import IC.TYPE.BoolType;
import IC.TYPE.IntType;
import IC.TYPE.Kind;
import IC.TYPE.MethodType;
import IC.TYPE.NullType;
import IC.TYPE.StringType;
import IC.TYPE.TypeTable;
import IC.TYPE.VoidType;
import IC.lir.instruction.ArrayLengthInstruction;
import IC.lir.instruction.BinaryInstrucionEnum;
import IC.lir.instruction.BinaryInstruction;
import IC.lir.instruction.ConditionLabelInstruction;
import IC.lir.instruction.EndLabelInstruction;
import IC.lir.instruction.JumpInstruction;
import IC.lir.instruction.JumpInstructionEnum;
import IC.lir.instruction.LIRInstruction;
import IC.lir.instruction.LabelInstruction;
import IC.lir.instruction.LibraryInstruction;
import IC.lir.instruction.LoopLabelInstruction;
import IC.lir.instruction.MoveArrayInstruction;
import IC.lir.instruction.MoveFieldInstruction;
import IC.lir.instruction.MoveInstruction;
import IC.lir.instruction.ReturnInstruction;
import IC.lir.instruction.StaticCallInstruction;
import IC.lir.instruction.UnaryInstruction;
import IC.lir.instruction.UnaryInstructionEnum;
import IC.lir.instruction.VirtualCallInstruction;
import IC.lir.parameter.ArgumentPair;
import IC.lir.parameter.ArrayPair;
import IC.lir.parameter.FieldPair;
import IC.lir.parameter.LIRDummyReg;
import IC.lir.parameter.LIRImmediate;
import IC.lir.parameter.LIRLabel;
import IC.lir.parameter.LIRMemory;
import IC.lir.parameter.LIROperand;
import IC.lir.parameter.LIRParameter;
import IC.lir.parameter.LIRReg;
import IC.lir.parameter.LIRString;
import IC.lir.parameter.LIRStringLabel;
import IC.lir.parameter.ThisMemory;
import IC.lir.parameter.ZeroImmediate;

public class LirTranslator implements IC.AST.Visitor{

    LirProgram lirProg;
    FieldPair lastPair;
    ArrayPair lastArrayPair;
    
    String recentLoopLabel;
    String recentEndLabel;
    
    
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
                            classLayout.addMethodOffset("_" + icClass.getName() + "_" + method.getName());
                        }
                    }
                    lirProg.getClassLayouts().add(classLayout);
                }
            }
        }
        
         
        //constructing the dispatch table data structure
        for (ClassLayout layout : lirProg.getClassLayouts()) {
            LIRDispatchTable dTable = new LIRDispatchTable(new LIRLabel("_DV_" + layout.getName()));
            for (String method : layout.getMethodToOffset().keySet()) { 
                dTable.addLabel(new LIRLabel(method));
            }
            lirProg.addDispatchTable(dTable);
        }
        
        
        
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
        // 1.create label for method
        lirProg.addCommentIntsruction(method.getName() + " in " + method.getEnclosingScope().getParentSymbolTable().getId());
        String name = method.getEnclosingScope().getParentSymbolTable().getId() + "_" + method.getName();
        LabelInstruction entryPoint = new LabelInstruction(name);
        lirProg.addInstruction(entryPoint);
        // 2. visit the method statements
        for (Statement statement : method.getStatements()) { 
            statement.accept(this);
        }
        
        // to make each function returns something
        ReturnInstruction ret = new ReturnInstruction(new LIRImmediate(9999)); 
        lirProg.addInstruction(ret);
        lirProg.addCommentIntsruction("#############################");
        return null;
    }

    
    public Object visit(StaticMethod method) { //unfinished
     // create label for method
        LabelInstruction entryPoint;
        if (method.getName().compareTo("main") == 0) {          
            entryPoint = new LabelInstruction("ic_main");
        }
        else { 
            String name = method.getEnclosingScope().getParentSymbolTable().getId() + "_" + method.getName();
            entryPoint = new LabelInstruction(name);
        }
        lirProg.addCommentIntsruction(method.getName() + " in " + method.getEnclosingScope().getParentSymbolTable().getId());
        lirProg.addInstruction(entryPoint);
        
        for (Statement statement :method.getStatements()) { 
            statement.accept(this);
        }
        
        if (method.getName().compareTo("main") == 0) {          
            LibraryInstruction libInst = new LibraryInstruction("exit", LIRDummyReg.getInstance(), ZeroImmediate.getInstance());
            lirProg.addInstruction(libInst);
        }
        else { 
            ReturnInstruction ret = new ReturnInstruction(new LIRImmediate(9999)); 
            lirProg.addInstruction(ret);
        }
        lirProg.addCommentIntsruction("#############################");
        return null;
    }

    
    public Object visit(LibraryMethod method) {
        return null;
    }

    
    public Object visit(Formal formal) {
        return null;
    }

    
    public Object visit(PrimitiveType type) {
        return null;
    }

    
    public Object visit(UserType type) {
        return null;
    }

    
    public Object visit(Assignment assignment) { 
        lirProg.addCommentIntsruction("Assignment on line " + assignment.getLine());
        Object rhs = assignment.getAssignment().accept(this);
        LIRReg rightReg = putInRegister(rhs);
        
        
        Object lhs = assignment.getVariable().accept(this);
        if (lhs instanceof VariableLocation) { 
            //lirProg.deleteLastLine();
           // lhsObj = dependantVisitVariable(lhs,false);
        }
        else if (lhs instanceof ArrayLocation) {
           // lirProg.deleteLastLine();
            //lhsObj = dependantVisitArray(lhs,false);
        }
        else { 
           // System.out.println("Something went wrong, assingment on line " + assignment.getLine());
           //System.exit(-1);
        }
        lirProg.deleteLastLine();
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
                if (isField((VariableLocation) (assignment.getVariable()))) { 
                    MoveFieldInstruction moveField = new MoveFieldInstruction(lastPair,rightReg,true); //move R2, R1
                    lirProg.addInstruction(moveField);
                    lastPair.getLocation().makeFreeRegister();
                    lastPair = null;
                }
                else { 
                    LIRMemory mem = new LIRMemory(assignment.getEnclosingScope().lookup(((VariableLocation) assignment.getVariable()).getName()));
                    MoveInstruction move = new MoveInstruction(rightReg,mem);
                    lirProg.addInstruction(move);
                }
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
        callStatement.getCall().accept(this); 
        return null;
    }

    
    public Object visit(Return returnStatement) {
        if (returnStatement.hasValue()) { 
            LIRReg result = putInRegister(returnStatement.getValue().accept(this));
            ReturnInstruction ret = new ReturnInstruction(result);
            lirProg.addInstruction(ret);
            result.makeFreeRegister();
        }
        return null;
    }

    
    public Object visit(If ifStatement) {
        lirProg.addCommentIntsruction("If at line " + ifStatement.getLine()); 
        LIRReg cond = putInRegister(ifStatement.getCondition().accept(this));
        ConditionLabelInstruction trueActions = new ConditionLabelInstruction();
        ConditionLabelInstruction falseActions = null;
        JumpInstruction conditionalJumpToFalse = null;
        JumpInstruction conditionalJumpToEnd = null;

        if (ifStatement.hasElse()) { 
            falseActions = new ConditionLabelInstruction();
            conditionalJumpToFalse = new JumpInstruction(JumpInstructionEnum.True, falseActions.getLabel());
        }
        
        
        EndLabelInstruction endLabel = new EndLabelInstruction();
        BinaryInstruction compare = new BinaryInstruction(ZeroImmediate.getInstance(), cond, BinaryInstrucionEnum.COMPARE);
        JumpInstruction jumpToEndUnconditional = new JumpInstruction(JumpInstructionEnum.Unconditional, endLabel.getLabel());
        conditionalJumpToEnd = new JumpInstruction(JumpInstructionEnum.True, endLabel.getLabel());

        
        lirProg.addInstruction(compare);

        if (ifStatement.hasElse()) { 
            lirProg.addInstruction(conditionalJumpToFalse);
            lirProg.addInstruction(trueActions);
            ifStatement.getOperation().accept(this);
            lirProg.addInstruction(jumpToEndUnconditional);
            lirProg.addInstruction(falseActions);
            ifStatement.getElseOperation().accept(this);
        }
        else { 
            lirProg.addInstruction(conditionalJumpToEnd);
            lirProg.addInstruction(trueActions);
            ifStatement.getOperation().accept(this);
        }
        lirProg.addInstruction(endLabel);
        return null;
    }

    
    public Object visit(While whileStatement) {
        lirProg.addCommentIntsruction("While at " + whileStatement.getLine());
        LoopLabelInstruction loopy = new LoopLabelInstruction();
        EndLabelInstruction endLabel = new EndLabelInstruction();
        recentEndLabel = endLabel.getLabel();
        recentLoopLabel = loopy.getLabel();
        lirProg.addInstruction(loopy);
        LIRReg cond = putInRegister(whileStatement.getCondition().accept(this));
        BinaryInstruction compare = new BinaryInstruction(ZeroImmediate.getInstance(), cond, BinaryInstrucionEnum.COMPARE);
        lirProg.addInstruction(compare);
        JumpInstruction conditionalJumpToEnd = new JumpInstruction(JumpInstructionEnum.True, endLabel.getLabel());
        lirProg.addInstruction(conditionalJumpToEnd);
        whileStatement.getOperation().accept(this);
        JumpInstruction unconditionalJumpToloop = new JumpInstruction(JumpInstructionEnum.Unconditional, loopy.getLabel());
        lirProg.addInstruction(unconditionalJumpToloop);
        lirProg.addInstruction(endLabel);
        return null;
    }

    
    public Object visit(Break breakStatement) {
        JumpInstruction breaky = new JumpInstruction(JumpInstructionEnum.Unconditional, recentEndLabel);
        lirProg.addInstruction(breaky);
        return null;
    }

    
    public Object visit(Continue continueStatement) {
        JumpInstruction continuey = new JumpInstruction(JumpInstructionEnum.Unconditional, recentLoopLabel);
        lirProg.addInstruction(continuey);
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
            mem = new LIRMemory(localVariable.getEnclosingScope().lookup(localVariable.getName()));
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
            
            int offset = lirProg.getClassLayout(location.getLocation().getSemanticType().toString()).getFieldToOffset().get(location.getName());
            LIRImmediate offsetImmediate = new LIRImmediate(offset);
            if (lastPair != null) { 
                lastPair.getLocation().makeFreeRegister();   
            }
            reg = new LIRReg(); //R2  
            FieldPair pair = new FieldPair(locationReg,offsetImmediate); 
            lastPair = pair;
            MoveFieldInstruction mfi = new MoveFieldInstruction(pair, reg, false);
            lirProg.addInstruction(mfi);
        }
        else if (isField(location)) { 
            reg = new LIRReg();
            MoveInstruction move = new MoveInstruction(new ThisMemory(), reg);
            LIRReg fieldReg = new LIRReg();
            ClassLayout cl = lirProg.getClassLayout(location.getEnclosingScope().getEnclosingClassSymbolTable().getId()); //Maybe _DV_ ?
            int offset = cl.getFieldOffset(location.getName());
            if (lastPair != null) { 
                lastPair.getLocation().makeFreeRegister();   
            }
            lastPair = new FieldPair(reg,new LIRImmediate(offset));
            MoveFieldInstruction moveField = new MoveFieldInstruction(lastPair, fieldReg, false);
            lirProg.addInstruction(move);
            lirProg.addInstruction(moveField);
            reg.makeFreeRegister();
            return fieldReg;
            
        }
        else { 
            mem = new LIRMemory(location.getEnclosingScope().lookup(location.getName()));
            reg = new LIRReg();
            MoveInstruction move = new MoveInstruction(mem, reg);
            lirProg.addInstruction(move);
        }
        return reg;
    }


    private boolean isField(VariableLocation location) {
        return location.getEnclosingScope().lookup(location.getName()).getKind().getKind()==Kind.FIELD;
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
       
        if (call.getClassName().compareTo("Library") == 0) {
            lirProg.addCommentIntsruction("Library call " + call.getName() + " at line " + call.getLine());
            SymbolTable methodSymbolTable = call.getEnclosingScope().getMethod(call.getClassName(), call.getName());
            MethodType methodType = (MethodType) methodSymbolTable.lookup(call.getName()).getType();
            LIRReg resultReg;
        
            if (methodType.getReturnType().equals(TypeTable.voidType)) { 
                resultReg = LIRDummyReg.getInstance();
            }
            else {
                resultReg = new LIRReg();
            } 
        
            LinkedList<LIRParameter> arguments = new LinkedList<LIRParameter>();
            for (Expression expr : call.getArguments()) { 
                arguments.add(putInRegister(expr.accept(this)));
            }
            
            LibraryInstruction lib = new LibraryInstruction(call.getName(), resultReg, arguments);
            for (LIRParameter liro : arguments) { 
                if (liro instanceof LIRReg) { 
                    ((LIRReg) liro).makeFreeRegister();
                }
            }
            lirProg.addInstruction(lib);
            return resultReg;
        }
        else {
            lirProg.addCommentIntsruction("Static Call " + call.getName() + " at line " + call.getLine());
    
            LinkedList<LIROperand> arguments = new LinkedList<LIROperand>();
            for (Expression argument : call.getArguments()) { 
                arguments.add((LIROperand)argument.accept(this));
            }
            LinkedList<ArgumentPair> pairs = new LinkedList<ArgumentPair>();
            SymbolTable methodSymbolTable = call.getEnclosingScope().getMethod(call.getClassName(), call.getName());
            int i = arguments.size()-1;
            for (SemanticSymbol symbol : methodSymbolTable.getEntries().values()) { 
                if (symbol.getKind().getKind() == Kind.FORMAL) { 
                    ArgumentPair pair = new ArgumentPair(symbol.getId() + symbol.getUniqueID(),arguments.get(i--).toString());
                    pairs.add(pair);
                    
                }
            }
            pairs = reverseList(pairs);
            LIRReg resultReg;
            MethodType methodType = (MethodType) methodSymbolTable.lookup(call.getName()).getType();
            
            
            if (methodType.getReturnType().equals(TypeTable.voidType)) { 
                resultReg = LIRDummyReg.getInstance();
            }
            else {
                resultReg = new LIRReg();
            } 
            
            String funcName = "_" + call.getClassName() + "_" + call.getName();
            StaticCallInstruction staticCallInstruction = new StaticCallInstruction(funcName, resultReg, pairs);
            lirProg.addInstruction(staticCallInstruction);
            return resultReg;
        }
    }

    
    public Object visit(VirtualCall call) {
        lirProg.addCommentIntsruction("Virtual Call " + call.getName() + " at line " + call.getLine());
        LIRReg object;
        String className;
        if (call.isExternal()) { 
            object = putInRegister(call.getLocation().accept(this));
            className = call.getLocation().getSemanticType().toString();
        }
        else { 
            LIRMemory that = new ThisMemory();
            object = putInRegister(that);
            className = call.getEnclosingScope().lookupSymbolTableContaining(call.getName()).getId();
        }
        
        LIRImmediate offset = new LIRImmediate(lirProg.getClassLayout(className).getMethodOffset("_" + className + "_" + call.getName()));
        
        LinkedList<LIROperand> arguments = new LinkedList<LIROperand>();
        for (Expression argument : call.getArguments()) { 
            arguments.add((LIROperand)argument.accept(this));
        }
        
        
        LinkedList<ArgumentPair> pairs = new LinkedList<ArgumentPair>();
        SymbolTable methodSymbolTable = call.getEnclosingScope().getMethod(className, call.getName());
        int i = arguments.size()-1;
        for (SemanticSymbol symbol : methodSymbolTable.getEntries().values()) { 
            if (symbol.getKind().getKind() == Kind.FORMAL) { 
                ArgumentPair pair = new ArgumentPair(symbol.getId() + symbol.getUniqueID(),arguments.get(i--).toString());
                pairs.add(pair);
                
            }
        }
        pairs = reverseList(pairs);
        LIRReg resultReg;
        MethodType methodType = (MethodType) methodSymbolTable.lookup(call.getName()).getType();
        
        
        if (methodType.getReturnType().equals(TypeTable.voidType)) { 
            resultReg = LIRDummyReg.getInstance();
        }
        else {
            resultReg = new LIRReg();
        } 
        
        VirtualCallInstruction virtualCallInstruction = new VirtualCallInstruction(object,offset,resultReg,pairs);
        lirProg.addInstruction(virtualCallInstruction);
        return resultReg;
    }

    
    public Object visit(This thisExpression) {
        LIRReg result = new LIRReg();
        MoveInstruction move = new MoveInstruction(new ThisMemory(), result);
        lirProg.addInstruction(move);
        return result;
    }

    
    public Object visit(NewClass newClass) {
        lirProg.addCommentIntsruction("New Class " + newClass.getName() + " at line " + newClass.getLine());
        int size = lirProg.getClassLayout(newClass.getName()).getAllocationSize();
        LIRReg resultReg = new LIRReg();
        LibraryInstruction libInst = new LibraryInstruction("allocateObject", resultReg, new LIRImmediate(size));
        MoveFieldInstruction moveField = new MoveFieldInstruction(new FieldPair(resultReg,ZeroImmediate.getInstance()),lirProg.getDispatchTable("_DV_" + newClass.getName()).getLabel() , true);
        lirProg.addInstruction(libInst);
        lirProg.addInstruction(moveField);
        return resultReg;
    }

    
    public Object visit(NewArray newArray) {
        LIRReg size = putInRegister(newArray.getSize().accept(this));
        LIRImmediate factor = new LIRImmediate(4);
        BinaryInstruction multiply = new BinaryInstruction(factor, size, BinaryInstrucionEnum.MUL);
        LIRReg result = new LIRReg();
        LibraryInstruction lib = new LibraryInstruction("allocateArray", result, size);
        lirProg.addInstruction(multiply);
        lirProg.addInstruction(lib);
        size.makeFreeRegister();
        return result;
    }

    
    public Object visit(Length length) { 
        LIRReg reg = putInRegister(length.getArray().accept(this));
        LIRReg result = new LIRReg();
        ArrayLengthInstruction inst = new ArrayLengthInstruction(result,reg);
        lirProg.addInstruction(inst);
        reg.makeFreeRegister();
        return result;
    }

    
    public Object visit(MathBinaryOp binaryOp) {
        lirProg.addCommentIntsruction("Math binary operation '" + binaryOp.getOperator().getOperatorString() + "' at line " + binaryOp.getLine());
        Object second = binaryOp.getSecondOperand().accept(this);
        Object first = binaryOp.getFirstOperand().accept(this);
        LIRReg firstReg = null, secondReg = null;
        BinaryOps operator = binaryOp.getOperator();
        if (binaryOp.getSemanticType().equals(TypeTable.stringType)) {
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
    
    public JumpInstructionEnum getJumpEnum(BinaryOps operator) { 
        switch (operator) { 
        case GT: 
            return JumpInstructionEnum.GreaterEqual;
        case GTE:
            return JumpInstructionEnum.Greater;
        case LT:
            return JumpInstructionEnum.LessEqual;
        case LTE:
            return JumpInstructionEnum.Less;
        case LOR: 
            return JumpInstructionEnum.True;
        case LAND:
            return JumpInstructionEnum.True;
        case NEQUAL:
            return JumpInstructionEnum.True;
        case EQUAL:
            return JumpInstructionEnum.False;
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
        EndLabelInstruction endLabel = new EndLabelInstruction();
        ConditionLabelInstruction condLabel = new ConditionLabelInstruction(); 

        
        if (binaryOp.getOperator() == BinaryOps.LAND) { 
            firstReg = putInRegister(first);
            BinaryInstruction compare = new BinaryInstruction(ZeroImmediate.getInstance(),firstReg, BinaryInstrucionEnum.COMPARE);
            lirProg.addInstruction(compare);
            JumpInstruction jumpFalse = new JumpInstruction(JumpInstructionEnum.True, condLabel.getLabel());
            lirProg.addInstruction(jumpFalse);
            
            second = binaryOp.getSecondOperand().accept(this);
            secondReg = putInRegister(second);
            MoveInstruction trueCompareInstruction = new MoveInstruction(new LIRImmediate(1),secondReg);
            MoveInstruction falseCompareInstruction = new MoveInstruction(ZeroImmediate.getInstance(),secondReg);
            
            BinaryInstruction compare2 = new BinaryInstruction(ZeroImmediate.getInstance(),secondReg, BinaryInstrucionEnum.COMPARE);
            lirProg.addInstruction(compare2);
            lirProg.addInstruction(jumpFalse);
            lirProg.addInstruction(trueCompareInstruction);
            JumpInstruction jumpToEnd = new JumpInstruction(JumpInstructionEnum.Unconditional, endLabel.getLabel());
            lirProg.addInstruction(jumpToEnd);
            lirProg.addInstruction(condLabel);
            lirProg.addInstruction(falseCompareInstruction);
            lirProg.addInstruction(endLabel);
            firstReg.makeFreeRegister();
            return secondReg;
        }       
        else if (binaryOp.getOperator() == BinaryOps.LOR) {
            firstReg = putInRegister(first);
            BinaryInstruction compare = new BinaryInstruction(new LIRImmediate(1),firstReg, BinaryInstrucionEnum.COMPARE);
            lirProg.addInstruction(compare);
            JumpInstruction jumpTrue = new JumpInstruction(JumpInstructionEnum.True, condLabel.getLabel());
            lirProg.addInstruction(jumpTrue);
            secondReg = putInRegister(binaryOp.getSecondOperand().accept(this));
            BinaryInstruction compare2 = new BinaryInstruction(new LIRImmediate(1),secondReg, BinaryInstrucionEnum.COMPARE);
            lirProg.addInstruction(compare2);
            lirProg.addInstruction(jumpTrue);
            MoveInstruction falseCompareInstruction = new MoveInstruction(ZeroImmediate.getInstance(),secondReg);
            lirProg.addInstruction(falseCompareInstruction);
            JumpInstruction jumpToEnd = new JumpInstruction(JumpInstructionEnum.Unconditional, endLabel.getLabel());
            lirProg.addInstruction(jumpToEnd);
            lirProg.addInstruction(condLabel);
            MoveInstruction trueCompareInstruction = new MoveInstruction(new LIRImmediate(1),secondReg);
            lirProg.addInstruction(trueCompareInstruction);
            lirProg.addInstruction(endLabel);
            firstReg.makeFreeRegister();
            return secondReg;
        }   
        else { 
            second = binaryOp.getSecondOperand().accept(this);
            secondReg = putInRegister(second);
            firstReg = putInRegister(first);
            MoveInstruction trueCompareInstruction = new MoveInstruction(new LIRImmediate(1),secondReg);
            MoveInstruction falseCompareInstruction = new MoveInstruction(ZeroImmediate.getInstance(),secondReg);
            BinaryInstruction comparison = new BinaryInstruction(firstReg,secondReg,getBinaryEnum(operator)); //Should be Compare
            JumpInstruction jumpToFalse = new JumpInstruction(getJumpEnum(operator),condLabel.getLabel()); 
            JumpInstruction jumpToEnd = new JumpInstruction(JumpInstructionEnum.Unconditional, endLabel.getLabel());
              
            
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
        Object operand = unaryOp.getOperand().accept(this);
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
            //semantic analysis made sure this doesnt happen.
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
        return expressionBlock.getExpression().accept(this);
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
    
    public LinkedList<ArgumentPair> reverseList(LinkedList<ArgumentPair> pairs) { 
        ArgumentPair [] array = new ArgumentPair[pairs.size()];
        int i = 0;
        for (ArgumentPair pair : pairs) { 
            array[i++] = pair;
        }
        LinkedList<ArgumentPair> result = new LinkedList<ArgumentPair>();
        for (i = array.length-1; i>=0; i--) { 
            result.add(array[i]);
        }
        return result;
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
            
        }
        
    }

}
