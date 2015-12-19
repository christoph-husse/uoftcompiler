package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.ast.type.IntegerType;
import compiler488.parser.Location;

/**
 * Place holder for all binary expression where both operands must be integer
 * expressions.
 */
public class BinaryArithmeticOperator extends BinaryOperator
{

	public BinaryArithmeticOperator(Location location, BinaryOperators opKind, Expression left, Expression right)
	{
		super(location, opKind, left, right);
		IntegerType temp = new IntegerType(location);
		operandType = temp;
		setType(temp);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
