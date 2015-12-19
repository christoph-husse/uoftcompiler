package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.ast.expn.Expression;
import compiler488.parser.Location;

/**
 * The command to write data on the output device.
 */
public class PutInvocation extends Statement
{
	private final ASTList<Expression> outputs;

	public PutInvocation(Location location, ASTList<Expression> output)
	{
		super(location);
		this.outputs = output;
	}

	@Override
	public String toString()
	{
		return "put " + outputs;
	}

	public ASTList<Expression> getOutputs()
	{
		return outputs;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(outputs);
	}
}
