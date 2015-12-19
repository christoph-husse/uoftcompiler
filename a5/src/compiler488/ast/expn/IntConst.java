package compiler488.ast.expn;

import compiler488.ast.ASTVisitor;
import compiler488.ast.type.IntegerType;
import compiler488.parser.Location;

/**
 * Represents a literal integer constant.
 */
public class IntConst extends Const
{
	private Integer value;

	@Override
	public String toString()
	{
		return value.toString();
	}

	@Override
	public Integer getValue()
	{
		return value;
	}

	public IntConst(Location location, Integer value)
	{
		super(location);

		location.end(
				location.getLineNumber(Location.BEGIN) - 1,
				location.getColumn(Location.BEGIN) - 1,
				value.toString().length() - 1);
		this.value = value;
		setType(new IntegerType(location));
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
