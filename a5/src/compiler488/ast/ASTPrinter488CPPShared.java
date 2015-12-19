package compiler488.ast;

import java.util.Iterator;

import compiler488.ast.decl.ParameterDeclaration;
import compiler488.ast.expn.ArrayAccess;
import compiler488.ast.expn.BinaryOperator;
import compiler488.ast.expn.BoolConst;
import compiler488.ast.expn.FunctionInvocation;
import compiler488.ast.expn.Identifier;
import compiler488.ast.expn.IntConst;
import compiler488.ast.expn.UnaryMinus;
import compiler488.ast.expn.UnaryNot;
import compiler488.ast.expn.VariableAccess;
import compiler488.ast.stmt.ProcedureInvocation;
import compiler488.ast.stmt.ReturnStatement;
import compiler488.ast.stmt.Scope;
import compiler488.ast.stmt.Statement;

public abstract class ASTPrinter488CPPShared extends ASTPrettyPrinter
{

	public ASTPrinter488CPPShared(Scope programAST)
	{
		super(programAST);
	}

	public abstract void writeStmtEnd();

	@Override
	public void visit(BinaryOperator expr)
	{
		acceptExpression(expr.getLeft());

		String sym;

		switch (expr.getOperator())
		{
		case Minus:
			sym = "-";
			break;
		case Plus:
			sym = "+";
			break;
		case Multiply:
			sym = "*";
			break;
		case Divide:
			sym = "/";
			break;
		case And:
			sym = "&";
			break;
		case Or:
			sym = "|";
			break;
		case GreaterEqual:
			sym = ">=";
			break;
		case Equal:
			sym = "=";
			break;
		case Greater:
			sym = ">";
			break;
		case Less:
			sym = "<";
			break;
		case LessEqual:
			sym = "<=";
			break;
		case NotEqual:
			sym = "!=";
			break;
		default:
			throw new RuntimeException();
		};

		write(" " + sym + " ");
		acceptExpression(expr.getRight());
	}

	@Override
	public void visit(ArrayAccess expr)
	{
		write(expr.getName().toString());
		write("[");
		expr.getIndex().accept(this);
		write("]");
	}

	@Override
	public void visit(BoolConst expr)
	{
		write(expr.toString());
	}

	@Override
	public void visit(FunctionInvocation expr)
	{
		expr.getName().accept(this);
		write("(");
		expr.getArguments().accept(this);
		write(")");
	}

	@Override
	public void visit(Identifier expr)
	{
		write(expr.getIdent());
	}

	@Override
	public void visit(VariableAccess expr)
	{
		expr.getName().accept(this);
	}

	@Override
	public void visit(IntConst expr)
	{
		write(expr.toString());
	}

	@Override
	public void visit(ProcedureInvocation expr)
	{
		expr.getName().accept(this);
		write("(");
		expr.getArguments().accept(this);
		write(")");
		writeStmtEnd();
	}

	@Override
	public void visit(ReturnStatement expr)
	{
		write("return");
		writeStmtEnd();
	}

	@Override
	public void visit(ParameterDeclaration param)
	{
		param.getType().accept(this);
		write(" ");
		param.getName().accept(this);
	}

	@Override
	public void visit(UnaryMinus expr)
	{
		write("-");
		acceptExpression(expr.getOperand());
	}

	@Override
	public void visit(UnaryNot expr)
	{
		write("!");
		acceptExpression(expr.getOperand());
	}

	@Override
	public <E extends ASTNode> void visit(ASTList<E> list)
	{
		for (Iterator<E> iter = list.iterator(); iter.hasNext();)
		{
			ASTNode e = iter.next();
			e.accept(this);
			if (iter.hasNext() && !(e instanceof Statement))
				write(", ");
		}
	}

}
