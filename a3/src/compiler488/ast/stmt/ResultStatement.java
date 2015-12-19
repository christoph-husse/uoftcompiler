package compiler488.ast.stmt;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.ast.expn.Expression;
import compiler488.parser.Location;

/**
 * The command to return from a function or procedure.
 */
public class ResultStatement extends Statement
{
	private Expression value = null;

	public ResultStatement(Location location, Expression value)
	{
		super(location);
		this.value = value;
	}

	public Expression getValue()
	{
		return value;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(value);
	}
}
