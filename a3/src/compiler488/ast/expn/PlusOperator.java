package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class PlusOperator extends BinaryArithmeticOperator
{

	public PlusOperator(Location location, Expression left, Expression right)
	{
		super(location, BinaryOperators.Plus, left, right);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
