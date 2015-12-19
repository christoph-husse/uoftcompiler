package compiler488.ast.expn;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.parser.Location;

/**
 * References to a scalar variable.
 */
public class Identifier extends Expression
{
	private String ident; // name of the identifier

	public Identifier(Location location, String ident)
	{
		super(location);

		location.extend(new Location().end(location.getLineNumber(Location.BEGIN) - 1, location.getColumn(Location.BEGIN) + ident.length() - 2));
		this.ident = ident;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Identifier))
			return false;

		return ((Identifier) obj).ident.equals(ident);
	}

	/**
	 * Returns the name of the variable or function.
	 */
	@Override
	public String toString()
	{
		return ident;
	}

	public String getIdent()
	{
		return ident;
	}

	public void setIdent(String ident)
	{
		this.ident = ident;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>();
	}
}
