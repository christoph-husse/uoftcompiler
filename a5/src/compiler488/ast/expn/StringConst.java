package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.ast.type.StringType;
import compiler488.parser.Location;

/**
 * String literal constants.
 */
public class StringConst extends Const
{
	public static final String NEW_LINE =
		System.getProperty("line.separator");

	private String value;

	@Override
	public String toString()
	{
		return value;
	}

	@Override
	public String getValue()
	{
		return value;
	}

	public StringConst(Location location, String value)
	{
		super(location);
		this.value = value;
		setType(new StringType(location));
	}

	public static StringConst newLine(Location location)
	{
		return new StringConst(location, NEW_LINE);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
