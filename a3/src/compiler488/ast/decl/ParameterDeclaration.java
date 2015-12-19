package compiler488.ast.decl;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.ast.expn.Identifier;
import compiler488.ast.type.Type;
import compiler488.parser.Location;

/**
 * Represents the declaration of a parameter.
 */

public class ParameterDeclaration extends Declaration
{
	public ParameterDeclaration(Location location, Type type, Identifier name)
	{
		super(location, name);
		this.type = type;
		setName(name);
	}

	@Override
	public String toString()
	{
		return type + " " + getName();
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(type, getName());
	}
}
