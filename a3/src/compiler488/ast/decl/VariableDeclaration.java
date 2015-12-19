package compiler488.ast.decl;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.ast.expn.Identifier;

/**
 * Represents the declaration of a simple variable.
 */
public class VariableDeclaration extends Declaration
{

	@Override
	public String toString()
	{
		return getName().toString();
	}

	public VariableDeclaration(Identifier name)
	{
		super(name.getLocation(), name);
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
