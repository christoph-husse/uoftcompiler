package compiler488.ast.type;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

/**
 * Used to declare objects that yield integers.
 */
public class IntegerType extends Type
{

	public IntegerType(Location location)
	{
		super(location);
	}

	/** Returns the string <b>"Integer"</b>. */
	@Override
	public String toString()
	{
		return "integer";
	}
	
	@Override
	public String toStringWithArticle()
	{
		return "an integer";
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
