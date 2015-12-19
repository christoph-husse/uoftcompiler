package compiler488.ast.decl;

import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.parser.Location;

public class ForwardDeclaration extends Declaration
{
	private RoutineDeclaration routine;

	public ForwardDeclaration(Location location, RoutineDeclaration routine)
	{
		super(location);
		this.routine = routine;
		setName(routine.getName());
		setType(routine.getType());
	}

	public RoutineDeclaration getRoutine()
	{
		return routine;
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(routine);
	}
}
