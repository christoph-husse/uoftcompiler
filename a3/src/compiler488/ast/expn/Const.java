package compiler488.ast.expn;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.parser.Location;

/**
 * This is a place holder for literal constants.
 */
public abstract class Const extends Expression
{

	public Const(Location location)
	{
		super(location);
	}

	public abstract Object getValue();

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>();
	}
}
