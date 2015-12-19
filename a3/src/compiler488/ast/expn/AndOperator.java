package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class AndOperator extends BinaryBooleanOperator
{

	public AndOperator(Location location, Expression left, Expression right)
	{
		super(location, BinaryOperators.And, left, right);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
