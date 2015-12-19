package compiler488.ast.expn;

import compiler488.ast.ASTList;
import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.parser.Location;

/**
 * Represents a function call with or without arguments.
 */
public class FunctionInvocation extends Access
{
	private ASTList<Expression> arguments;

	public FunctionInvocation(Location location, Identifier name, ASTList<Expression> arguments)
	{
		super(location, name);
		this.arguments = arguments;
	}

	@Override
	public String toString()
	{
		return name.getIdent() + arguments;
	}

	public ASTList<Expression> getArguments()
	{
		return arguments;
	}

	public Identifier getName()
	{
		return name;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(name, arguments);
	}
}
