package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.ast.expn.VariableAccess;
import compiler488.parser.Location;

/**
 * The command to read data into one or more variables.
 */
public class GetInvocation extends Statement
{

	private ASTList<VariableAccess> inputs;

	public GetInvocation(Location location, ASTList<VariableAccess> inputs)
	{
		super(location);
		this.inputs = inputs;
	}

	/** Returns a string describing the <b>get</b> statement. */
	@Override
	public String toString()
	{
		return "get " + inputs;
	}

	public ASTList<VariableAccess> getInputs()
	{
		return inputs;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(inputs);
	}
}
