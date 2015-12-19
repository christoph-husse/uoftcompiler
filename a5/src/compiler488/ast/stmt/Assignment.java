package compiler488.ast.stmt;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.ast.expn.Expression;
import compiler488.parser.Location;

/**
 * Holds the assignment of an expression to a variable.
 */
public class Assignment extends Statement
{
	private Expression dst, src;

	public Assignment(Location location, Expression dst, Expression src)
	{
		super(location);
		this.src = src;
		this.dst = dst;
	}

	public Expression getDest()
	{
		return dst;
	}

	public Expression getSource()
	{
		return src;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(dst, src);
	}
}
