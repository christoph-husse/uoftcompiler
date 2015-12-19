package compiler488.ast.type;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

/**
 * The type of things that may be true or false.
 */
public class BooleanType extends Type
{
	public BooleanType(Location location)
	{
		super(location);
	}

	/** Returns the string <b>"Boolean"</b>. */
	@Override
	public String toString()
	{
		return "boolean";
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
