package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class GreaterOperator extends ComparisonOperator
{

	public GreaterOperator(Location location, Expression left, Expression right)
	{
		super(location, BinaryOperators.Greater, left, right);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
