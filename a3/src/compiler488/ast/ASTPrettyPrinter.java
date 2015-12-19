package compiler488.ast;

import java.io.PrintStream;
import java.util.Stack;

import compiler488.ast.expn.Expression;
import compiler488.ast.stmt.Scope;

public class ASTPrettyPrinter extends ASTVisitorAdapter
{

	private Scope ast;
	private PrintStream stream;
	private boolean newLine = false;
	private Stack<Integer> indents = new Stack<Integer>();

	public ASTPrettyPrinter(Scope programAST)
	{
		ast = programAST;
	}

	public void setPrintStream(PrintStream stream)
	{
		this.stream = stream;
	}

	public void print()
	{
		ast.accept(this);
	}

	protected int getIndent()
	{
		if (indents.empty())
			return 0;
		else
			return indents.peek();
	}

	protected void writeIndent()
	{
		if (!newLine)
			return;

		newLine = false;
		for (int i = 0; i < getIndent(); i++)
			write(" ");
	}

	protected void writeLine()
	{
		stream.println();
		newLine = true;
	}

	protected void write(String str)
	{
		writeIndent();
		stream.print(str);
	}

	protected void writeLine(String line)
	{
		writeIndent();
		stream.println(line);
		newLine = true;
	}

	protected void beginIndent()
	{
		indents.push(getIndent() + 4);
	}

	protected void endIndent()
	{
		indents.pop();
	}

	protected void acceptExpression(Expression expr)
	{
		if (expr.hasParenthesis())
			write("(");

		expr.accept(this);

		if (expr.hasParenthesis())
			write(")");
	}

}
