package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class OrOperator extends BinaryBooleanOperator
{

	public OrOperator(Location location, Expression left, Expression right)
	{
		super(location, BinaryOperators.Or, left, right);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
