package compiler488.ast.stmt;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.parser.Location;

/**
 * The command to return from a procedure.
 */
public class ReturnStatement extends Statement
{

	public ReturnStatement(Location location)
	{
		super(location);
	}

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
