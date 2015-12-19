package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class EqualOperator extends ComparisonOperator
{

	public EqualOperator(Location location, Expression left, Expression right)
	{
		super(location, BinaryOperators.Equal, left, right);
		operandType = null;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
