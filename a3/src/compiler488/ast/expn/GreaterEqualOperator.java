package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class GreaterEqualOperator extends ComparisonOperator
{

	public GreaterEqualOperator(Location location, Expression left, Expression right)
	{
		super(location, BinaryOperators.GreaterEqual, left, right);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
