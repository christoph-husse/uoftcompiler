package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.parser.Location;

/**
 * Represents a infinite loop.
 */
public class LoopStatement extends Statement
{

	private Scope body;
	private int uid;
	private static int global_uid = 0;

	public Scope getBody()
	{
		return body;
	}

	public int getUniqueId()
	{
		return uid;
	}

	public LoopStatement(Location location, ASTList<Statement> body)
	{
		super(location);
		this.body = new Scope(location, body);
		this.uid = global_uid++;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(body);
	}
}
