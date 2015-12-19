package compiler488.ast;

import compiler488.ast.ASTVisitorAdapter;
import compiler488.ast.decl.*;
import compiler488.ast.expn.*;
import compiler488.ast.stmt.*;
import compiler488.ast.type.*;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Prints the AST in dot format.
 */
public class ASTGraphPrinter extends ASTVisitorAdapter
{
	private Scope ast;
	private PrintStream stream;
	private short lexLevel = 0;

	public ASTGraphPrinter(Scope programAST, PrintStream stream)
	{
		this.ast = programAST;
		this.stream = stream;
	}

	public void print()
	{
		stream.println(
			"digraph AST {"
			);

		// Do the syntax tree traversal
		ast.accept(this);

		stream.println("}");
	}

	private void println(String line)
	{
		stream.println(line);
	}

	public void visit(ASTNode node)
	{
		drawNode(node);
		linkChildren(node);
	}

	private void drawNode(ASTNode node)
	{
		drawNode(node, node.getClass().getSimpleName());
	}

	private String getNodeName(ASTNode node)
	{
		return "n"+node.getId();
	}

	private void drawNode(ASTNode node, String label)
	{
		// Draw this node
		println("n"+node.getId() + " [");
		println("  label=\"" + label + "\"");
		println("];");
	}

	private void drawEdge(ASTNode from, ASTNode to)
	{
		println("n"+from.getId() + " -> " + "n"+to.getId() + ";");
	}

	private void linkChildren(ASTNode node)
	{
		for (ASTNode child : node.getChildren())
		{
			drawEdge(node, child);
			child.accept(this);
		}
	}

	@Override
	public void visit(Const i)
	{
		Object value = ((Const) i).getValue();
		drawNode(i, value.toString().trim());
	}

	@Override
	public void visit(StringConst s)
	{
		String value = ((StringConst) s).getValue();
		drawNode(s, "\\\""+value.toString().trim()+"\\\"");
	}

	@Override
	public void visit(VariableDeclaration d)
	{
		drawNode(d, d.getType().toString()+" "+d.toString());
	}

	@Override
	public void visit(ArrayDeclaration d)
	{
		drawNode(d, d.getType().toString()+" "+d.toString());
	}

	@Override
	public void visit(VariableAccess v)
	{
		drawNode(v, v.getName().toString());
	}

	@Override
	public void visit(ArrayAccess v)
	{
		drawNode(v);
		linkChildren(v);
	}

	@Override
	public void visit(BinaryOperator o)
	{
		drawNode(o, opToSymbol(o.getOperator()));
		linkChildren(o);
	}

	@Override
	public void visit(Identifier i)
	{
		drawNode(i, i.toString());
	}

	private String opToSymbol(BinaryOperators op)
	{
		switch (op)
		{
			case Minus: return "-";
			case Plus: return "+";
			case Multiply: return "*";
			case Divide: return "/";
			case And: return "&";
			case Or: return "|";
			case GreaterEqual: return ">=";
			case Equal: return "=";
			case Greater: return ">";
			case Less: return "<";
			case LessEqual: return "<=";
			case NotEqual: return "!=";
			default: return null;
		}
	}
}
