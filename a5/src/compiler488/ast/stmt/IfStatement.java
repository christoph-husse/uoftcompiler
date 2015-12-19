package compiler488.ast.stmt;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.ast.expn.Expression;
import compiler488.parser.Location;

/**
 * Represents an if-then or an if-then-else construct.
 */
public class IfStatement extends Statement
{
	private Expression condition;
	private Scope whenTrue;
	private Scope whenFalse = null;

	public IfStatement(Location location, Expression cond, Scope whenTrue, Scope whenFalse)
	{
		super(location);
		this.condition = cond;
		this.whenTrue = whenTrue;
		this.whenFalse = whenFalse;
	}

	public IfStatement(Location location, Expression cond, Scope whenTrue)
	{
		this(location, cond, whenTrue, null);
	}

	public Expression getCondition()
	{
		return condition;
	}

	public Scope getWhenFalse()
	{
		return whenFalse;
	}

	public boolean hasWhenFalse()
	{
		return whenFalse != null;
	}

	public Scope getWhenTrue()
	{
		return whenTrue;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(condition, whenTrue, whenFalse);
	}
}
