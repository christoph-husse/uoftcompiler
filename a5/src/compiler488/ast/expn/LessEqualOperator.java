package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class LessEqualOperator extends ComparisonOperator
{

	public LessEqualOperator(Location location, Expression left, Expression right)
	{
		super(location, BinaryOperators.LessEqual, left, right);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
