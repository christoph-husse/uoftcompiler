package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class LessOperator extends ComparisonOperator
{

	public LessOperator(Location location, Expression left, Expression right)
	{
		super(location, BinaryOperators.Less, left, right);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
