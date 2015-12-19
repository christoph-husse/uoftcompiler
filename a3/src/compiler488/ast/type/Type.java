package compiler488.ast.type;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.parser.Location;

/**
 * A placeholder for types.
 */
public class Type extends ASTNode
{

	public Type(Location location)
	{
		super(location);
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

	public static boolean equals(Type a, Type b)
	{
		return a != null && b != null && a.getClass().equals(b.getClass());
	}
	
	public String toStringWithArticle()
	{
		return "void";
	}
}
