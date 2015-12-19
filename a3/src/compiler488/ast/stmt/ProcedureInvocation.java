package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.ast.expn.Expression;
import compiler488.ast.expn.Identifier;
import compiler488.ast.type.Type;
import compiler488.parser.Location;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents calling a procedure.
 */
public class ProcedureInvocation extends Statement
{
	private Identifier name;
	private ASTList<Expression> arguments;

	@Override
	public String toString()
	{
		return name + "(" + arguments + ")";
	}

	public ASTList<Expression> getArguments()
	{
		return arguments;
	}

	public ProcedureInvocation(Location location, Identifier name, ASTList<Expression> args)
	{
		super(location);
		this.arguments = args;
		this.name = name;
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
