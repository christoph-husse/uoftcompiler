package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class MultiplicationOperator extends BinaryArithmeticOperator
{

	public MultiplicationOperator(Location location, Expression left, Expression right)
	{
		super(location, BinaryOperators.Multiply, left, right);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
