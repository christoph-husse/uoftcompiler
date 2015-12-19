package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.ast.type.BooleanType;
import compiler488.ast.type.IntegerType;
import compiler488.parser.Location;

/**
 * Place holder for all ordered comparisions expression where both operands must
 * be integer expressions. e.g. < , > etc. comparisons
 */
public class ComparisonOperator extends BinaryOperator
{

	public ComparisonOperator(Location location, BinaryOperators opKind, Expression left, Expression right)
	{
		super(location, opKind, left, right);
		setType(new BooleanType(location));
		operandType = new IntegerType(location);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
