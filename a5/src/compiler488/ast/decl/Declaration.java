package compiler488.ast.decl;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.expn.Identifier;
import compiler488.ast.type.Type;
import compiler488.parser.Location;

/**
 * The common features of declarations.
 */
public abstract class Declaration extends ASTNode
{
	protected Type type = null;
	private Identifier name;

	public Declaration(Location location)
	{
		super(location);
	}

	public Declaration(Location location, Identifier name)
	{
		this(location);
		this.name = name;
	}

	public Identifier getName()
	{
		return name;
	}

	public Declaration setName(Identifier name)
	{
		this.name = name;
		return this;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	public boolean hasForwardDeclaration()
	{
		return false;
	}
}
