package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class MinusOperator extends BinaryArithmeticOperator
{

	public MinusOperator(Location location, Expression left, Expression right)
	{
		super(location, BinaryOperators.Minus, left, right);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
