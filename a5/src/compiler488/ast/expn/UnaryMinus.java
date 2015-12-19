package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.ast.type.IntegerType;
import compiler488.parser.Location;

/**
 * Represents negation of an integer expression
 */
public class UnaryMinus extends UnaryOperator
{

	@Override
	public String toString()
	{
		return "-" + getOperand();
	}

	public UnaryMinus(Location location, Expression operand)
	{
		super(location, operand);
		setType(new IntegerType(location));
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
