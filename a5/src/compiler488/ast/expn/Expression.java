package compiler488.ast.expn;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.type.Type;
import compiler488.parser.Location;
import compiler488.symbol.SymbolEntry;
import compiler488.symbol.SymbolTable;

/**
 * A placeholder for all expressions.
 */
public abstract class Expression extends ASTNode
{
	private boolean needsParenthesis = false;
	private Type type = null;

	public Expression(Location location)
	{
		super(location);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	public Expression addParenthesis()
	{
		needsParenthesis = true;
		return this;
	}

	public boolean hasParenthesis()
	{
		return needsParenthesis;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}
}
