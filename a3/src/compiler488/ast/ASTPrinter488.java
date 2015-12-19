package compiler488.ast;

import compiler488.ast.decl.ArrayDeclaration;
import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.ForwardDeclaration;
import compiler488.ast.decl.RoutineDeclaration;
import compiler488.ast.decl.VariableDeclaration;
import compiler488.ast.expn.StringConst;
import compiler488.ast.stmt.Assignment;
import compiler488.ast.stmt.ExitStatement;
import compiler488.ast.stmt.GetInvocation;
import compiler488.ast.stmt.IfStatement;
import compiler488.ast.stmt.LoopStatement;
import compiler488.ast.stmt.PutInvocation;
import compiler488.ast.stmt.ResultStatement;
import compiler488.ast.stmt.Scope;
import compiler488.ast.stmt.Statement;
import compiler488.ast.type.BooleanType;
import compiler488.ast.type.IntegerType;

/**
 * Prints the AST back out as a 488 code. If the source file is valid, the AST
 * dump will also be compilable! This is kind of an excellent way of verifying
 * the AST data structure, and its visitor pattern...
 */
public class ASTPrinter488 extends ASTPrinter488CPPShared
{

	public ASTPrinter488(Scope programAST)
	{
		super(programAST);
	}

	@Override
	public void visit(StringConst expr)
	{
		if (expr.getValue().equals(StringConst.NEW_LINE))
			write("skip");
		else
			write("\"" + expr.toString() + "\"");
	}

	@Override
	public void visit(Assignment expr)
	{
		expr.getDest().accept(this);
		write(" := ");
		expr.getSource().accept(this);
		writeLine();
	}

	@Override
	public void visit(ExitStatement expr)
	{
		write("exit");

		if (expr.hasLevel())
		{
			write(" ");
			write(expr.getLevel().toString());
		}

		if (expr.hasWhen())
		{
			write(" when ");
			expr.getWhen().accept(this);
		}

		writeLine();
	}

	@Override
	public void visit(GetInvocation expr)
	{
		write("get ");
		expr.getInputs().accept(this);
		writeLine();
	}

	@Override
	public void visit(IfStatement expr)
	{
		write("if ");
		expr.getCondition().accept(this);
		writeLine(" then");
		beginIndent();
		expr.getWhenTrue().getStatements().accept(this);
		endIndent();

		if (expr.hasWhenFalse())
		{
			writeLine("else");
			beginIndent();
			expr.getWhenFalse().getStatements().accept(this);
			endIndent();
		}
		writeLine("fi");
		writeLine();
	}

	@Override
	public void visit(LoopStatement expr)
	{
		writeLine("loop");
		expr.getBody().accept(this);
		writeLine("pool");
	}

	@Override
	public void visit(PutInvocation expr)
	{
		write("put ");
		expr.getOutputs().accept(this);
		writeLine();
	}

	@Override
	public void visit(ResultStatement expr)
	{
		write("result ");
		expr.getValue().accept(this);
		writeLine();
	}

	@Override
	public void visit(Scope scope)
	{
		writeLine("begin");
		beginIndent();
		{
			for (Declaration s : scope.getDeclarations())
				s.accept(this);
			writeLine();
			for (Statement s : scope.getStatements())
				s.accept(this);
		}
		endIndent();
		writeLine("end");
	}

	@Override
	public void visit(ArrayDeclaration arr)
	{
		arr.getType().accept(this);
		write(" ");
		arr.getName().accept(this);
		write("[");
		if (arr.getLowerBound() != 0)
			write(arr.getLowerBound() + "..");
		write(arr.getUpperBound().toString());
		writeLine("]");
	}

	@Override
	public void visit(ForwardDeclaration fwd)
	{
		write("forward ");
		writeRoutineHead(fwd.getRoutine());
	}

	private void writeRoutineHead(RoutineDeclaration decl)
	{
		if (decl.hasReturnType())
			decl.getReturnType().accept(this);
		else
			write("proc");

		write(" ");
		decl.getName().accept(this);
		write("(");
		decl.getParameters().accept(this);
		write(")");
		writeLine();
	}

	@Override
	public void visit(RoutineDeclaration routine)
	{
		writeRoutineHead(routine);
		routine.getBody().accept(this);
		writeLine();
	}

	@Override
	public void visit(VariableDeclaration var)
	{
		var.getType().accept(this);
		write(" ");
		var.getName().accept(this);
		writeLine();
	}

	@Override
	public void visit(BooleanType type)
	{
		write("boolean");
	}

	@Override
	public void visit(IntegerType type)
	{
		write("integer");
	}

	@Override
	public void writeStmtEnd()
	{
		writeLine();
	}
}
