package compiler488.ast.type;

import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

public class StringType extends Type {
	public StringType(Location location)
	{
		super(location);
	}

	/** Returns the string <b>"string"</b>. */
	@Override
	public String toString()
	{
		return "string";
	}
	
	@Override
	public String toStringWithArticle()
	{
		return "a string";
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
