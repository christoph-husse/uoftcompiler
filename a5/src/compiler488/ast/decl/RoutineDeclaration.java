package compiler488.ast.decl;

import compiler488.ast.ASTList;
import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.Cached;
import compiler488.ast.FixedIterable;
import compiler488.ast.SemanticException;
import compiler488.ast.expn.Identifier;
import compiler488.ast.stmt.*;
import compiler488.ast.type.Type;
import compiler488.parser.Location;
import compiler488.semantics.SemanticError;

/**
 * Represents the parameters and instructions associated with a function or
 * procedure.
 */
public class RoutineDeclaration extends Declaration
{

	public RoutineDeclaration(Location location)
	{
		super(location);
		parameters = new ASTList<ParameterDeclaration>(location);
	}

	private final ASTList<ParameterDeclaration> parameters;
	private Scope body;
	private Cached<ForwardDeclaration> fwdDecl = new Cached<ForwardDeclaration>();

    //  Start/end addresses of code fragment.
    //
    //  We set this field while generating code.
    private short startAddress;
    private short endAddress;

	@Override
	public String toString()
	{
		return getName() + "(" + parameters + ")";
	}

	public boolean hasBody()
	{
		return body != null;
	}

	public Scope getBody()
	{
		return body;
	}

	/**
	 * FOR INTERNAL USE ONLY!
	 * */
	public RoutineDeclaration setBody(Scope body)
	{
		this.body = body;
		return this;
	}

	public boolean hasReturnType()
	{
		return type != null;
	}

	public Type getReturnType()
	{
		return type;
	}

    public void setStartAddress(short addr) {
        startAddress = addr;
    }

    public short getStartAddress() {
        return startAddress;
    }

    public void setEndAddress(short addr) {
        endAddress = addr;
    }

    public short getEndAddress() {
        return endAddress;
    }

	/**
	 * FOR INTERNAL USE ONLY!
	 * */
	public RoutineDeclaration setReturnType(Type returnType)
	{
		this.type = returnType;
		return this;
	}

	public ASTList<ParameterDeclaration> getParameters()
	{
		return parameters;
	}

	public RoutineDeclaration addParameter(Location location, Type type, Identifier name)
	{
		parameters.addLast(new ParameterDeclaration(location, type, name));
		return this;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(type, getName(), parameters, body);
	}

	public ForwardDeclaration getForwardDeclaration()
	{
		if (fwdDecl.hasValue())
			return fwdDecl.getValue();

		// must be forward declared in same scope and before actual definition
		ForwardDeclaration fwd = null;
		boolean thisDefined = false;

		for (Declaration decl : getScope().getDeclarations())
		{
			if (decl instanceof ForwardDeclaration)
			{
				if (!decl.getName().equals(getName()))
					continue;

				if (thisDefined)
					throw new SemanticException(SemanticError.FowardDeclAfterDefinition, this, decl);

				if (!doSignaturesMatch(((ForwardDeclaration) decl).getRoutine(), this))
					throw new SemanticException(SemanticError.FowardDeclSignatureMismatch, this, decl);

				fwd = (ForwardDeclaration) decl;
				break;
			}
			else if (decl == this)
			{
				thisDefined = true;
			}
		}

		fwdDecl.setValue(fwd);
		return fwdDecl.getValue();
	}

	public static boolean doSignaturesMatch(RoutineDeclaration a, RoutineDeclaration b)
	{
		if (a.hasReturnType() ^ b.hasReturnType())
			return false;

		if (a.hasReturnType() && !Type.equals(a.type, b.type))
			return false;

		if (a.getParameters().size() != b.getParameters().size())
			return false;

		for (int i = 0; i < a.getParameters().size(); i++)
		{
			if (!Type.equals(a.getParameters().at(i).getType(), b.getParameters().at(i).getType()))
				return false;
		}

		return true;
	}

	public boolean hasForwardDeclaration()
	{
		return getForwardDeclaration() != null;
	}
}
