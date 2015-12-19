package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class NotEqualOperator extends ComparisonOperator
{

	public NotEqualOperator(Location location, Expression left, Expression right)
	{
		super(location, BinaryOperators.NotEqual, left, right);
		operandType = null;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
