package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.ast.type.BooleanType;
import compiler488.parser.Location;

/**
 * Place holder for all binary expression where both operands must be boolean
 * expressions.
 */
public class BinaryBooleanOperator extends BinaryOperator
{

	public BinaryBooleanOperator(Location location, BinaryOperators opKind, Expression left, Expression right)
	{
		super(location, opKind, left, right);
		setType(new BooleanType(location));
		operandType = new BooleanType(location);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
