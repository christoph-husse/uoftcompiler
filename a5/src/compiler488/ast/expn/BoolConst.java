package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.ast.type.BooleanType;
import compiler488.parser.Location;

/**
 * Boolean literal constants.
 */
public class BoolConst extends Const
{
	private boolean value;

	@Override
	public String toString()
	{
		return (value ? "true" : "false");
	}

	@Override
	public Boolean getValue()
	{
		return value;
	}

	public BoolConst(Location location, boolean value)
	{
		super(location);
		this.value = value;
		setType(new BooleanType(location));
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
