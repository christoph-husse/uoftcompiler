package compiler488.codegen;

import java.util.*;

import compiler488.ast.*;
import compiler488.ast.decl.*;
import compiler488.ast.expn.*;
import compiler488.ast.stmt.*;
import compiler488.ast.type.*;
import compiler488.compiler.ReportVerbosity;
import compiler488.parser.Report;
import compiler488.runtime.Machine;
import compiler488.symbol.SymbolEntry;

/**
 * Converts an AST into 488 machine code.
 */
public class CodeGenASTVisitor extends ASTVisitorAdapter 
{
	private Scope ast;
	private ArrayList<Short> code = new ArrayList<Short>();
    private ArrayList<String> codeName = new ArrayList<String>(Arrays.asList(
                "HALT", "ADDR", "LOAD", "STORE", "PUSH", "PUSHMT", "SETD", "POP", "POPN", 
                "DUP", "DUPN", "BR", "BF", "NEG", "ADD", "SUB", "MUL", "DIV", "EQ", "LT", 
                "OR", "SWAP", "READC", "PRINTC", "READI", "PRINTI", "TRON", "TROFF", "ILIMIT"));
	private short lexLevel = 0;

    //  Special register to save return value.
    private short REG_RV = 0x0;  //  memSize = 8192 = 0x2000

    //  Store start address holes for functions/procedures. (For function/procedure calls.)
    private HashMap<Integer, RoutineDeclaration> startAddressHoles = new HashMap<Integer, RoutineDeclaration>();

    //  We leave some holes in the generated code because 
    //  we don't know the start addresses of functions or 
    //  procedures. When we have traversed the full AST, 
    //  we should be able to know these addresses. So fill 
    //  them up.
    //
    //  Call this function at the end of code generation to 
    //  patch the generated code.
    public void fillStartAddressHoles() {
        Iterator it = startAddressHoles.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, RoutineDeclaration> pairs = (Map.Entry<Integer, RoutineDeclaration>)it.next();
            Integer pc = pairs.getKey();
            RoutineDeclaration decl = pairs.getValue();
            code.set(pc, decl.getStartAddress());
            it.remove();
        }
    }

    //  Store end address holes for functions/procedures. (For return/result statements.)
    private HashMap<Integer, RoutineDeclaration> endAddressHoles = new HashMap<Integer, RoutineDeclaration>();

    //  We leave some holes in the generated code because 
    //  we don't know the end addresses of functions or 
    //  procedures. When we have traversed the full AST, 
    //  we should be able to know these addresses. So fill 
    //  them up.
    //
    //  Call this function at the end of code generation to 
    //  patch the generated code.
    public void fillEndAddressHoles() {
        Iterator it = endAddressHoles.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, RoutineDeclaration> pairs = (Map.Entry<Integer, RoutineDeclaration>)it.next();
            Integer pc = pairs.getKey();
            RoutineDeclaration decl = pairs.getValue();
            code.set(pc, decl.getEndAddress());
            it.remove();
        }
    }
    
    // Store exit address holes.
    private HashMap<Integer, LoopStatement> exitAddressHoles = new HashMap<Integer, LoopStatement>();
    
    // Levelled exit statements leave holes in the generated code
    // because we don't know the exit addresses of the corresponding
    // loop. When we have traversed the full AST, we should be able
    // to know these addresses. So fill them up.
    //
    // Call this function at the end of code generation to patch the generated code.
    public void fillExitAddressHoles() 
    {
    	Iterator it = exitAddressHoles.entrySet().iterator();
    	while (it.hasNext()) 
    	{
    		Map.Entry<Integer, LoopStatement> pairs = (Map.Entry<Integer, LoopStatement>)it.next();
    		Integer pc = pairs.getKey();
    		LoopStatement stmt = pairs.getValue();
    		code.set(pc,  (short)stmt.getExitAddress());
    		it.remove();
    	}
    }

    public CodeGenASTVisitor(Scope programAST)
    {
        ast = programAST;
    }

	private static void print(String str)
	{
		Report.begin(ReportVerbosity.Verbose).print(str).submit();
	}

	private static void println(String str)
	{
		Report.begin(ReportVerbosity.Verbose).print(str).newLine().submit();
	}

    public void run()
    {
		// Reserved space for return value
		code.add((short) 0);

        // Uncomment to turn on tracing
		code.add(Machine.TRON);

		// Do the syntax tree traversal
		ast.accept(this);

		// Last instruction, halts execution
		code.add(Machine.HALT);
		
		// Patch generated code to fix func/proc calls' start addresses.
		fillStartAddressHoles();
		// Patch generated code to fix result/return stmts' end addresses.
		fillEndAddressHoles();
		// Patch generated code to fix exit statement branch addresses
		fillExitAddressHoles();

		// Write code into memory
		for (short i = 0; i < code.size(); i++)
		{
			Machine.writeMemory(i, (short) code.get(i));
		}

		// where code begins
		Machine.setPC((short) 1);
		// where memory stack begins
		Machine.setMSP((short) code.size());
		// Limit of stack
		Machine.setMLP((short) (Machine.memorySize - 1));
	}

    public void dump()
    {
        for (int i = 0;i < code.size();i++) {
			print(i+"\t");
            short inst = code.get(i);
            switch (inst) {
                case Machine.ADDR:
                    println(codeName.get(inst) + " " + code.get(i + 1) + " " + code.get(i + 2));
                    i += 2;
                    break;
                case Machine.PUSH:
                    println(codeName.get(inst) + " " + code.get(i + 1));
                    i += 1;
                    break;
                case Machine.SETD:
                    println(codeName.get(inst) + " " + code.get(i + 1));
                    i += 1;
                    break;
                default:
                    println(codeName.get(inst));
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    //  Visit each kind of ASTNode.
    //
    //  -   Commented entries are not needed because implementation is provided 
    //      for their super class.
    //
    ///////////////////////////////////////////////////////////////////////////

	@Override
	public void visit(AndOperator oper)
	{
		oper.getLeft().accept(this);
		applyNot();
		oper.getRight().accept(this);
		applyNot();
		code.add(Machine.OR);
		applyNot();
	}

	@Override
	public void visit(ArrayAccess v)
	{
		// Push address of the array element onto the stack
		pushArrayAddr(v);

		// Load the contents and push it onto the top of the stack
		code.add(Machine.LOAD);
	}

	/**
	 * Handles cases in the pattern: Resolve left expression Resolve right
	 * expression Apply operations
	 */
	@Override
	public void visit(BinaryOperator oper)
	{
		oper.getLeft().accept(this);
		oper.getRight().accept(this);
		switch (oper.getOperator())
		{
		case Minus:
			code.add(Machine.SUB);
			break;
		case Plus:
			code.add(Machine.ADD);
			break;
		case Multiply:
			code.add(Machine.MUL);
			break;
		case Divide:
			code.add(Machine.DIV);
			break;
		case Or:
			code.add(Machine.OR);
			break;
		case Equal:
			code.add(Machine.EQ);
			break;
		case Less:
			code.add(Machine.LT);
			break;
		case Greater:
			code.add(Machine.SWAP);
			code.add(Machine.LT);
			break;
		case GreaterEqual:
			code.add(Machine.PUSH);
			code.add((short) 1);
			code.add(Machine.SUB);
			code.add(Machine.SWAP);
			code.add(Machine.LT);
			break;
		case NotEqual:
			code.add(Machine.EQ);
			applyNot();
			break;
		default:
			throw new RuntimeException("Unhandled binary operator");
		}
	}

	@Override
	public void visit(BoolConst b)
	{
		boolean value = ((BoolConst) b).getValue();
		code.add(Machine.PUSH);
		code.add(value ? Machine.MACHINE_TRUE : Machine.MACHINE_FALSE);
	}

	@Override
	public void visit(FunctionInvocation func) {

        //  The calling convention is as follows:
        //
        //  Caller's job (Enter):
        //
        //  -   Push arguments.
        //  -   Push current value of display[lexicLevel(callee)].
        //  -   Push return address.
        //  -   Jump to callee.

        //  Now the control is given to callee and the stack is like:
        //
        //  ---------------------------
        //    return address
        //  ---------------------------
        //    saved disp[LL(callee)] 
        //  ---------------------------
        //    arg1
        //  ---------------------------
        //    arg2
        //  ---------------------------
        //    arg3
        //  ---------------------------

        //  Callee's job (Enter):
        //
        //  -   Set display[lexicLevel(callee)] to `new disp[LL(callee)]`.
        //  -   Reserve space for local variables.

        //  ---------------------------
        //    local3
        //  ---------------------------
        //    local2
        //  ---------------------------
        //    local1
        //  --------------------------- <- new disp[LL(callee)]
        //    return address
        //  ---------------------------
        //    saved disp[LL(callee)] 
        //  ---------------------------
        //    arg1
        //  ---------------------------
        //    arg2
        //  ---------------------------
        //    arg3
        //  ---------------------------

        //  Callee's job (Exit):
        //
        //  -   Clean up locals.
        //  -   Put return value in REG_RV. (It should already be set there by Result/Return.)
        //  -   Return (which will consume return address).
        
        //  Now the control is given back to caller and the stack is like:
        //
        //  ---------------------------
        //          
        //  ---------------------------
        //  
        //  ---------------------------
        //  
        //  --------------------------- <- new disp[LL(callee)]
        //    return value
        //  ---------------------------
        //    saved disp[LL(callee)] 
        //  ---------------------------
        //    arg1
        //  ---------------------------
        //    arg2
        //  ---------------------------
        //    arg3
        //  ---------------------------

        //  Caller's job (Exit):
        //
        //  -   Restore display[lexicLevel(callee)].
        //  -   Clean up arguments.
        //  -   Copy return value from REG_RV to top of stack for further processing.
        //
        ///////////////////////////////////////////////////////////////////////

        //  Caller's job (Enter):

        //  Push arguments.
        //
        //  -   Arguments are left on stack after evaluation.
        //  -   Arguments are evaluated right-to-left.
        for (int i = func.getArguments().size() - 1;i >= 0;i--) {
            Expression expr = func.getArguments().at(i);
			expr.accept(this);
        }

        //  Push current value of display[lexicLevel(callee)].
        code.add(Machine.ADDR);
        code.add(((RoutineDeclaration)func.getDeclaration()).getBody().lexicLevel());
        code.add((short)0);

        //  Push return address.
        //
        //  -   Return address is (PC + 4).
        //  -   4 = sizeof(self + PUSH + addr + BR).
        //  -   We jump to Caller's job (Exit).
        code.add(Machine.PUSH);
        code.add((short)(code.size() + 4));

        //  Jump to callee.
        //
        //  Currently we don't know the address of callee's code. 
        //  So we push 0 as target address and mark it as to be updated.
        code.add(Machine.PUSH);
        startAddressHoles.put(code.size(), (RoutineDeclaration)func.getDeclaration());
        code.add((short)0);
        code.add(Machine.BR);

        //  Caller's job (Exit):

        //  Restore display[lexicLevel(callee)].
        code.add(Machine.SETD);
        code.add(((RoutineDeclaration)func.getDeclaration()).getBody().lexicLevel());

        //  Clean up arguments.
        code.add(Machine.PUSH);
        code.add((short)func.getArguments().size());
        code.add(Machine.POPN);

        //  Put the return value on top of stack for further processing.
        code.add(Machine.PUSH);    
        code.add(REG_RV);    
        code.add(Machine.LOAD);
	}

	@Override
	public void visit(IntConst i)
	{
		int value = ((IntConst) i).getValue();
		code.add(Machine.PUSH);
		code.add((short) value);
	}

	@Override
	public void visit(LessEqualOperator oper)
	{
		oper.getLeft().accept(this);
		code.add(Machine.PUSH);
		code.add((short) 1);
		code.add(Machine.SUB);
		oper.getRight().accept(this);
		code.add(Machine.LT);
	}

	@Override
	public void visit(UnaryMinus oper)
	{
		oper.getOperand().accept(this);
		code.add(Machine.NEG);
	}

	@Override
	public void visit(UnaryNot oper)
	{
		oper.getOperand().accept(this);
		applyNot();
	}

	@Override
	public void visit(VariableAccess v)
	{
		pushAddr(v);
		code.add(Machine.LOAD);
	}

	@Override
	public void visit(Assignment expr)
	{
		if (expr.getDest() instanceof ArrayAccess)
		{
			ArrayAccess dest = (ArrayAccess) expr.getDest();
			pushArrayAddr(dest);
		}
		else if (expr.getDest() instanceof VariableAccess)
		{
			VariableAccess dest = (VariableAccess) expr.getDest();
			pushAddr(dest);
		}
		else
		{
			throw new RuntimeException("Unhandled assignment");
		}

		// Resolve the rhs
		expr.getSource().accept(this);

		// Perform the assignment
		code.add(Machine.STORE);
	}

	@Override
	public void visit(ExitStatement expr)
	{		
		// Statement has condition
		if (expr.hasWhen())
		{
			// Generate condition expression
			expr.getWhen().accept(this);
			
			// Don't know end of statement address. Push 0 temporarily
			code.add(Machine.PUSH);
			int branchAddressPos = code.size();
			code.add((short)0);
			
			// Branch out of exit statement on false
			code.add(Machine.BF);
			
			// Don't know end of loop address. Push 0 temporarily and
			// mark it to be updated
			code.add(Machine.PUSH);
			exitAddressHoles.put(code.size(), expr.getExitBehindLoop());
			code.add((short)0);
			
			// Branch out of the referred loop
			code.add(Machine.BR);
			
			// Resolve above end of statement branch address
			code.set(branchAddressPos, (short)code.size());
		}
		// Statement does not have a condition
		else
		{
			// Don't know end of loop address. Push 0 temporarily and
			// mark it to be updated
			code.add(Machine.PUSH);
			exitAddressHoles.put(code.size(), expr.getExitBehindLoop());
			code.add((short)0);
						
			// Branch out of the referred loop
			code.add(Machine.BR);
		}
	}

	@Override
	public void visit(GetInvocation expr)
	{
		for (VariableAccess v : expr.getInputs())
		{
			// Push variable address
			pushAddr(v);
			
			// Read from standard input
			code.add(Machine.READI);
			
			// Write the input value
			code.add(Machine.STORE);
		}
	}

	@Override
	public void visit(IfStatement expr)
	{
		// Generate condition expression
		expr.getCondition().accept(this);

		// If statement has an else block
		if (expr.hasWhenFalse())
		{
			// Don't know address of else block. Push 0 temporarily
			code.add(Machine.PUSH);
			int branchAddressPos = code.size();
			code.add((short) 0);

			// Branch to else block on false
			code.add(Machine.BF);

			// Generate then block code
			expr.getWhenTrue().accept(this);

			// Don't know end of if statement address. Push 0 temporarily
			code.add(Machine.PUSH);
			int branchAddressPos2 = code.size();
			code.add((short) 0);

			// Branch out of if statement
			code.add(Machine.BR);

			// Resolve above else block branch address and generate else block
			// code
			code.set(branchAddressPos, (short) code.size());
			expr.getWhenFalse().accept(this);
			
			// Resolve above end of it statement branch address
			code.set(branchAddressPos2, (short) code.size());
		}
		// statement does not have an else block
		else
		{
			// Don't know end of if statement address. Push 0 temporarily
			code.add(Machine.PUSH);
			int branchAddressPos = code.size();
			code.add((short) 0);

			// Branch out of if statement on false
			code.add(Machine.BF);

			// Generate then block code
			expr.getWhenTrue().accept(this);

			// Resolve above end of if statement branch address
			code.set(branchAddressPos, (short) code.size());
		}
	}

	@Override
	public void visit(LoopStatement expr)
	{
		int startAddress = code.size();

		// Generate statement code
		expr.getBody().accept(this);

		// Push loop start address
		code.add(Machine.PUSH);
        code.add((short)startAddress);
        
        // Branch to start of loop
        code.add(Machine.BR);
        
        // Set loop exit address
        expr.setExitAddress(code.size());
	}

	@Override
	public void visit(ProcedureInvocation proc)
	{
        //  Almost the same as FunctionInvocation.
        //
        //  We just omit one step in the end: Put the return value on top 
        //  of stack. Because the return value is meaningless and undefined.

        //  Caller's job (Enter):

        //  Push arguments.
        for (int i = proc.getArguments().size() - 1; i >= 0; i--) {
            Expression expr = proc.getArguments().at(i);
			expr.accept(this);
        }

        //  Push current value of display[lexicLevel(callee)].
        code.add(Machine.ADDR);
        code.add(((RoutineDeclaration)proc.getDeclaration()).getBody().lexicLevel());
        code.add((short)0);

        //  Push return address.
        code.add(Machine.PUSH);
        code.add((short)(code.size() + 4));

        //  Jump to callee.
        code.add(Machine.PUSH);
        startAddressHoles.put(code.size(), (RoutineDeclaration)proc.getDeclaration());
        code.add((short)0);
        code.add(Machine.BR);

        //  Caller's job (Exit):

        //  Restore display[lexicLevel(callee)].
        code.add(Machine.SETD);
        code.add(((RoutineDeclaration)proc.getDeclaration()).getBody().lexicLevel());

        //  Clean up arguments.
        code.add(Machine.PUSH);
        code.add((short)proc.getArguments().size());
        code.add(Machine.POPN);
	}

	@Override
	public void visit(PutInvocation expr)
	{
		ASTList outputs = expr.getOutputs();
		for (int i = outputs.size()-1; i >= 0; --i)
		{
			Expression e = (Expression) outputs.at(i);
			if (e instanceof StringConst)
			{
				String s = ((StringConst) e).getValue();
				for (int j = s.length()-1; j >= 0; --j)
				{
					code.add(Machine.PUSH);
					code.add((short) s.charAt(j));
				}
			}
			else
			{
				e.accept(this);
			}
		}

		for (Expression e : expr.getOutputs())
		{
			if (e instanceof StringConst)
			{
				String s = ((StringConst) e).getValue();
				for (int j = s.length()-1; j >= 0; --j)
				{
					code.add(Machine.PRINTC);
				}
			}
			else
			{
				code.add(Machine.PRINTI);
			}
		}
	}

	public void visit(ResultStatement stmt) {
        //  Save return value.
        code.add(Machine.PUSH);
        code.add(REG_RV);
        stmt.getValue().accept(this);
        code.add(Machine.STORE);

        RoutineDeclaration decl = getParentRoutineDeclaration(stmt);
        code.add(Machine.PUSH);
        endAddressHoles.put(code.size(), decl);
        code.add((short)0);
        code.add(Machine.BR);
    }

	public void visit(ReturnStatement stmt) {
        RoutineDeclaration decl = getParentRoutineDeclaration(stmt);
        code.add(Machine.PUSH);
        endAddressHoles.put(code.size(), decl);
        code.add((short)0);
        code.add(Machine.BR);
    }

	@Override
	public void visit(Scope scope)
	{
		// Set the display pointer to the base variable for this scope
		code.add(Machine.PUSHMT);
		code.add(Machine.SETD);
		code.add(scope.lexicLevel());

		for (Declaration d : scope.getDeclarations()) {
            boolean needJump = (d instanceof RoutineDeclaration);
            int jumpAddr = 0;

            if (needJump) {
                //  Set holes. (Jump over RoutineDeclarations.)
                code.add(Machine.PUSH);
                jumpAddr = code.size();
                code.add((short)0);
                code.add(Machine.BR);
            }

			d.accept(this);

            if (needJump) {
                //  Fill holes.
                code.set(jumpAddr, (short)code.size());
            }
        }


		for (Statement s : scope.getStatements())
			s.accept(this);
	}

	@Override
	public void visit(ArrayDeclaration arr)
	{
		// Push (arr.size) words onto the stack to store the elements
		code.add(Machine.PUSH);
		code.add((short) 0);
		code.add(Machine.PUSH);
		code.add(arr.getSize().shortValue());
		code.add(Machine.DUPN);

		// Record the offset of the array start
		arr.getSymbols().setOffset(
				arr.getName().toString(),
				arr.getSize().shortValue());
	}

	public void visit(ForwardDeclaration fwd)
    {
        //  Do nothing for ForwardDeclaration.
        ;
    }

	@Override
	public void visit(ParameterDeclaration param)
	{
		// Record the variable offset
		param.getSymbols().setOffsetBackward(
			param.getName().toString(), (short) 1);
	}

	@Override
	public void visit(RoutineDeclaration routine) {

        //  Callee's job (Enter):

        routine.setStartAddress((short)code.size());

        //  Set display[lexicLevel(callee)] to `new disp[LL(callee)]`.
        //  This is done in visit(Scope). So don't duplicate it here.
        //code.add(Machine.PUSHMT);
        //code.add(Machine.SETD);
        //code.add(routine.lexicLevel());

        //  Reserve space for local variables.
        //  We do this by duplicating 0 #localVariable times.
        //  This is done in visit(VariableDeclaration) and visit(ArrayDeclaration). 
        //  So don't duplicate it here.
        //code.add(Machine.PUSH);
        ////  v (= 0) for DUPN.
        //code.add((short)0);
        //code.add(Machine.PUSH);
        ////  n ( = size of locals) for DUPN.
        int localCount = countLocals(routine);
        //code.add((short)localCount);
        //code.add(Machine.DUPN);


        routine.getParameters().accept(this);
        routine.getBody().accept(this);
        routine.setEndAddress((short)code.size());

        //  Callee's job (Exit):
        //
        //  Clean up locals.
        code.add(Machine.PUSH);
        code.add((short)localCount);
        code.add(Machine.POPN);

        //  Return (which will consume return address).
        code.add(Machine.BR);
    }

	@Override
	public void visit(VariableDeclaration var)
	{
		// Push a word onto the stack for each scalar variable
		if (var.getType() instanceof IntegerType ||
		    var.getType() instanceof BooleanType)
		{
			code.add(Machine.PUSH);
			code.add((short) 0);

			// Record the variable offset
			var.getSymbols().setOffset(var.getName().toString(), (short) 1);
		}
	}

	public <E extends ASTNode> void visit(ASTList<E> list)
	{
		for (E e : list)
			e.accept(this);
	}

    ///////////////////////////////////////////////////////////////////

	/**
	 * Pushes the address of s onto the stack.
	 */
	public void pushAddr(VariableAccess s)
	{
		// Load the address by looking in the display.
		code.add(Machine.ADDR);
		code.add(s.getDeclaration().lexicLevel());

		// Inline the offset as the order number
        Declaration d = s.getDeclaration();
        if (d instanceof VariableDeclaration)
            code.add(((VariableDeclaration)d).getOffset());
        else if (d instanceof ParameterDeclaration)
            code.add(((ParameterDeclaration)d).getOffset());
        else
			throw new RuntimeException("Unhandled VariableAccess");
	}

	/**
	 * Pushes the address of the array element accessed onto the stack.
	 */
	public void pushArrayAddr(ArrayAccess v)
	{
		SymbolEntry s = v.resolveSymbol(v.getName().toString());

		// Push the address of the array start, inlining the lower bound
		// into ADDR's order number
		ArrayDeclaration d = (ArrayDeclaration) s.getDeclaration();
		Short lowerBound = d.getLowerBound().shortValue();
		code.add(Machine.ADDR);
		code.add(s.getDeclaration().lexicLevel());
		code.add((short) (d.getOffset() - lowerBound));

		// Resolve the index amount and use it as an offset
		v.getIndex().accept(this);
		code.add(Machine.ADD);
	}

	/**
	 * Applies boolean NOT to the top of the stack.
	 */
	public void applyNot()
	{
		// NEG(SUB(operand,1))
		code.add(Machine.PUSH);
		code.add(Machine.MACHINE_TRUE);
		code.add(Machine.SUB);
		code.add(Machine.NEG);
	}

    /**
     * Calculate size of local variables in a routine.
     */
    public int countLocals(RoutineDeclaration routine)
    {
        Scope body = routine.getBody();
        ASTList<Declaration> decls = body.getDeclarations();

        int localCount = 0;
        for (Declaration decl : decls) {
            if (decl instanceof VariableDeclaration) {
                localCount++;
            } else if (decl instanceof ArrayDeclaration) {
                localCount += ((ArrayDeclaration)decl).getSize();
            }
        }
        return localCount;
    }

    /**
     * Get parent RoutineDeclaration for Result stmts.
     */
    public RoutineDeclaration getParentRoutineDeclaration(ResultStatement stmt) {
        ASTNode node = stmt.getParent();
        while (!(node instanceof RoutineDeclaration))
            node = node.getParent();
        return (RoutineDeclaration)node;
    }

    /**
     * Get parent RoutineDeclaration for Return stmts.
     */
    public RoutineDeclaration getParentRoutineDeclaration(ReturnStatement stmt) {
        ASTNode node = stmt.getParent();
        while (!(node instanceof RoutineDeclaration))
            node = node.getParent();
        return (RoutineDeclaration)node;
    }
}
