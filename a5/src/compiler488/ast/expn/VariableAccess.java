package compiler488.ast.expn;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;

public class VariableAccess extends Access
{
	public VariableAccess(Identifier name)
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
		return new FixedIterable<ASTNode>(name);
	}
}
