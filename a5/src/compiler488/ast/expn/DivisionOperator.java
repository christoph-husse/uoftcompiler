package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class DivisionOperator extends BinaryArithmeticOperator
{

	public DivisionOperator(Location location, Expression left, Expression right)
	{
		super(location, BinaryOperators.Divide, left, right);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
