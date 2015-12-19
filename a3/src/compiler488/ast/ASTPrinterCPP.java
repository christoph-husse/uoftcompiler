package compiler488.ast;

import compiler488.ast.decl.ArrayDeclaration;
import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.ForwardDeclaration;
import compiler488.ast.decl.RoutineDeclaration;
import compiler488.ast.decl.VariableDeclaration;
import compiler488.ast.expn.EqualOperator;
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
import compiler488.compiler.CompilerMain;

/**
 * Prints the AST back out as a C++ code. If the source file is valid, the AST
 * dump will also be compilable!
 */
public class ASTPrinterCPP extends ASTPrinter488CPPShared
{

	public ASTPrinterCPP(Scope programAST)
	{
		super(programAST);
	}

	@Override
	public void visit(EqualOperator expr)
	{
		acceptExpression(expr.getLeft());
		write(" == ");
		acceptExpression(expr.getRight());
	}

	public static String getCppHeaderString()
	{
		String res = "";
		String le = "\n";

		if (CompilerMain.isWindows())
			le = "\r\n";

		res +=
				"#include <iostream>" + le +
						"#include <functional>" + le +
						"#include <array>" + le +
						"#include <string>" + le + le +
						"static void put() {}" + le + le +
						"template<typename THead, typename... TTail>" + le +
						"static void put(THead current, TTail... args)" + le +
						"{" + le +
						"	std::cout << current;" + le +
						"	put(args...);" + le +
						"}" + le + le +
						"static void get() {}" + le + le +
						"template<typename THead, typename... TTail>" + le +
						"static void get(THead& current, TTail&... args)" + le +
						"{" + le +
						"	std::cin >> current;" + le +
						"	get(args...);" + le +
						"}" + le + le +
						"template<typename TElement, int LowerBound, int UpperBound>" + le +
						"class array" + le +
						"{" + le +
						"private:" + le +
						"	std::array<TElement, UpperBound - LowerBound + 1> entries;" + le +
						"public:" + le +
						"	array()" + le +
						"	{" + le +
						"		for(auto it = entries.begin(); it != entries.end(); it++) *it = TElement();" + le +
						"	}" + le +
						"	TElement& operator[](int index)" + le +
						"	{" + le +
						"		return entries.at(index - LowerBound);" + le +
						"	}" + le +
						"};";

		return res;
	}

	@Override
	public void print()
	{
		writeLine(getCppHeaderString());
		writeLine();
		writeLine("int main(int argc, char** argv) { ");
		super.print();
		writeLine();
		writeLine("    return 0;");
		writeLine("}");
	}

	@Override
	public void visit(StringConst expr)
	{
		if (expr.getValue().equals(StringConst.NEW_LINE))
			write("\"\\n\"");
		else
			write("\"" + expr.toString() + "\"");
	}

	@Override
	public void visit(Assignment expr)
	{
		expr.getDest().accept(this);
		write(" = ");
		expr.getSource().accept(this);
		writeLine(";");
	}

	@Override
	public void visit(ExitStatement expr)
	{
		if (expr.hasWhen())
		{
			write("if(");
			expr.getWhen().accept(this);
			write(") ");
		}

		writeLine("goto loopExit_" + expr.getExitBehindLoop().getUniqueId() + ";");
	}

	@Override
	public void visit(VariableDeclaration var)
	{
		var.getType().accept(this);
		write(" ");
		var.getName().accept(this);
		write(" = ");
		var.getType().accept(this);
		write("()");
		writeStmtEnd();
	}

	@Override
	public void visit(GetInvocation expr)
	{
		write("get(");
		expr.getInputs().accept(this);
		writeLine(");");
	}

	@Override
	public void visit(IfStatement expr)
	{
		write("if(");
		expr.getCondition().accept(this);
		writeLine(") {");
		beginIndent();
		expr.getWhenTrue().getStatements().accept(this);
		endIndent();

		if (expr.hasWhenFalse())
		{
			writeLine("} else {");
			beginIndent();
			expr.getWhenFalse().getStatements().accept(this);
			endIndent();
		}
		writeLine("}");
		writeLine();
	}

	@Override
	public void visit(LoopStatement expr)
	{
		writeLine("while(true) ");
		expr.getBody().accept(this);
		writeLine(" loopExit_" + expr.getUniqueId() + ":;");
	}

	@Override
	public void visit(PutInvocation expr)
	{
		write("put(");
		expr.getOutputs().accept(this);
		writeLine(");");
	}

	@Override
	public void visit(ResultStatement expr)
	{
		write("return ");
		expr.getValue().accept(this);
		writeLine(";");
	}

	@Override
	public void visit(Scope scope)
	{
		writeLine("{");
		beginIndent();
		{
			for (Declaration s : scope.getDeclarations())
				s.accept(this);

			if (!scope.getDeclarations().empty())
			{
				writeLine();
				writeLine();
			}

			for (Statement s : scope.getStatements())
				s.accept(this);
		}
		endIndent();
		write("}");
	}

	@Override
	public void visit(ArrayDeclaration arr)
	{
		write("array<");
		arr.getType().accept(this);
		write(", ");
		if (arr.getLowerBound() != 0)
			write(arr.getLowerBound() + ", ");
		else
			write("0, ");

		write(arr.getUpperBound().toString());
		write("> ");

		arr.getName().accept(this);
		writeLine(";");
	}

	@Override
	public void visit(ForwardDeclaration fwd)
	{
		writeMethodDecl(fwd.getRoutine());
	}

	private void writeMethodDecl(RoutineDeclaration func)
	{
		write("std::function<");

		if (func.hasReturnType())
			func.getReturnType().accept(this);
		else
			write("void");

		write("(");
		func.getParameters().accept(this);
		write(")");

		write("> ");
		func.getName().accept(this);
		writeLine(";");
	}

	@Override
	public void visit(RoutineDeclaration decl)
	{
		if (!decl.hasForwardDeclaration())
			writeMethodDecl(decl);

		decl.getName().accept(this);

		write(" = [&](");
		decl.getParameters().accept(this);
		write(")");

		if (decl.hasReturnType())
		{
			write(" -> ");
			decl.getReturnType().accept(this);
		}
		writeLine();
		decl.getBody().accept(this);
		writeLine(";");
	}

	@Override
	public void visit(BooleanType type)
	{
		write("bool");
	}

	@Override
	public void visit(IntegerType type)
	{
		write("int");
	}

	@Override
	public void writeStmtEnd()
	{
		writeLine(";");
	}
}
