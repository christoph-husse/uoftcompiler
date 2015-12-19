package compiler488.ast.expn;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.parser.Location;

/**
 * The common features of unary expressions.
 */
public class UnaryOperator extends Expression
{
	private Expression operand;

	public Expression getOperand()
	{
		return operand;
	}

	public UnaryOperator(Location location, Expression operand)
	{
		super(location);
		this.operand = operand;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(operand);
	}
}
