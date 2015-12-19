package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.ast.type.BooleanType;
import compiler488.parser.Location;

/**
 * Represents the boolean negation of an expression.
 */
public class UnaryNot extends UnaryOperator
{
	@Override
	public String toString()
	{
		return "-" + getOperand();
	}

	public UnaryNot(Location location, Expression operand)
	{
		super(location, operand);
		setType(new BooleanType(location));
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
