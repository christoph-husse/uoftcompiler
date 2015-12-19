package compiler488.ast.decl;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.ast.stmt.*;
import compiler488.ast.expn.Identifier;
import compiler488.ast.type.Type;
import compiler488.symbol.*;
import compiler488.parser.Location;

/**
 * Represents the declaration of a parameter.
 */

public class ParameterDeclaration extends Declaration
{
	private Short offset = null;

	public ParameterDeclaration(Location location, Type type, Identifier name)
	{
		super(location, name);
		this.type = type;
		setName(name);
	}

	@Override
	public String toString()
	{
		return type + " " + getName();
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
		return new FixedIterable<ASTNode>(type, getName());
	}

    @Override
	public SymbolTable getSymbols()
	{
        ASTNode node = this;
        while (!(node instanceof RoutineDeclaration)) {
            node = node.getParent();
        }

		return ((RoutineDeclaration)node).getBody().getLocalSymbolTable();
	}

	@Override
	public short lexicLevel() {
        ASTNode node = this;
        while (!(node instanceof RoutineDeclaration)) {
            node = node.getParent();
        }

		return ((RoutineDeclaration)node).getBody().lexicLevel();
	}
}
