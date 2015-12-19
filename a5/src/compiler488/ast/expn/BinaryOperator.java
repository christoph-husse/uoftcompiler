package compiler488.ast.expn;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.ast.type.Type;
import compiler488.parser.Location;

/**
 * The common features of binary expressions.
 */
public class BinaryOperator extends Expression
{
	Expression left, right;
	BinaryOperators opKind;
	protected Type operandType;

	public Expression getLeft()
	{
		return left;
	}

	public BinaryOperators getOperator()
	{
		return opKind;
	}

	public Expression getRight()
	{
		return right;
	}
	
	public Type getOperandType()
	{
		return operandType;
	}

	public BinaryOperator(Location location, BinaryOperators opKind, Expression left, Expression right)
	{
		super(location);
		this.opKind = opKind;
		this.left = left;
		this.right = right;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(left, right);
	}
}
