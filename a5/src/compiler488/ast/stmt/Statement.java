package compiler488.ast.stmt;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.parser.Location;

/**
 * A placeholder for statements.
 */
public abstract class Statement extends ASTNode
{

	public Statement(Location location)
	{
		super(location);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}
}
