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
	private Short offset = null;

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

	/**
	 * Returns number of words between the first variable in this scope
	 * and the start of this variable.
	 */
	public Short getOffset()
	{
		return offset;
	}

	/**
	 * Sets the offset of this symbol.
	 * @see SymbolTable.setOffset
	 */
	public void setOffset(short offset)
	{
		this.offset = offset;
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>();
	}
}
