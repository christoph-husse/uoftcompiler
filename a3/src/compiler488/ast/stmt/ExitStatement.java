package compiler488.ast.stmt;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.Cached;
import compiler488.ast.FixedIterable;
import compiler488.ast.SemanticException;
import compiler488.ast.decl.RoutineDeclaration;
import compiler488.ast.expn.Expression;
import compiler488.parser.Location;
import compiler488.semantics.SemanticError;

/**
 * Represents the command to exit from a loop.
 */

public class ExitStatement extends Statement
{

	private Expression when = null;
	private Integer level = 1;
	private boolean hasLevel;
	private final Cached<LoopStatement> exitedLoop = new Cached<LoopStatement>();

	public ExitStatement(Location location, int level, Expression when)
	{
		super(location);
		this.hasLevel = true;
		this.level = level;
		this.when = when;
	}

	public ExitStatement(Location location, int level)
	{
		super(location);
		this.hasLevel = true;
		this.level = level;
		this.when = null;
	}

	public ExitStatement(Location location, Expression when)
	{
		super(location);
		this.hasLevel = false;
		this.level = 1;
		this.when = when;
	}

	public ExitStatement(Location location)
	{
		super(location);
		this.hasLevel = false;
		this.level = 1;
		this.when = null;
	}

	/**
	 * Returns the loop after which execution continues when this exit statement
	 * was run. Internally, it also does semantic validation when queried for
	 * the first time.
	 **/
	public LoopStatement getExitBehindLoop()
	{
		if (exitedLoop.hasValue())
			return exitedLoop.getValue();

		LoopStatement loop = null;
		int i = 0;

		if (getLevel() < 1)
			throw new SemanticException(SemanticError.ExitLevelInvalid, this);

		for (ASTNode p : getParents())
		{
			if (p instanceof RoutineDeclaration)
				break;

			if (p instanceof LoopStatement)
			{
				loop = (LoopStatement) p;
				i++;
			}

			if (i == getLevel())
			{
				exitedLoop.setValue(loop);
				return exitedLoop.getValue();
			}
		}

		throw new SemanticException(SemanticError.ExitThroughRoutineBounds, this);
	}

	public boolean hasWhen()
	{
		return when != null;
	}

	public boolean hasLevel()
	{
		return hasLevel;
	}

	public Expression getWhen()
	{
		return when;
	}

	public Integer getLevel()
	{
		return level;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(when);
	}
}
