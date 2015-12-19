package compiler488.ast.decl;

import compiler488.ast.ASTVisitor;
import compiler488.ast.expn.Identifier;
import compiler488.ast.type.Type;
import compiler488.parser.Location;

/**
 * Holds the declaration part of an array.
 */
public class ArrayDeclaration extends VariableDeclaration
{

	private int lowerBound, upperBound;

	public ArrayDeclaration(Location location, Identifier name, int lowerBound, int upperBound)
	{
		super(name);

		this.__setLocation_Internal(location);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public String toString()
	{
		return getName() + "[" + lowerBound + ".." + upperBound + "]";
	}

	public Integer getSize()
	{
		return upperBound - lowerBound;
	}

	public Integer getLowerBound()
	{
		return lowerBound;
	}

	public Integer getUpperBound()
	{
		return upperBound;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
