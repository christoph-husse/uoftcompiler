package compiler488.ast.expn;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.parser.Location;

/**
 * References to an array element variable
 * 
 * Treat array subscript operation as a special form of unary expression.
 * operand must be an integer expression
 */
public class ArrayAccess extends VariableAccess
{

	public ArrayAccess(Location location, Identifier name, Expression index)
	{
		super(name);

		this.__setLocation_Internal(location);
		this.index = index;
	}

	private Expression index;

	public Expression getIndex()
	{
		return index;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(getName(), index);
	}
}
