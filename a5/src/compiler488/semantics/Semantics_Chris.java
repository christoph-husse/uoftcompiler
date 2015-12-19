package compiler488.semantics;

import compiler488.ast.SemanticException;
import compiler488.ast.decl.ForwardDeclaration;
import compiler488.ast.decl.RoutineDeclaration;
import compiler488.ast.stmt.ExitStatement;

public class Semantics_Chris extends SemanticASTVisitor
{
	@Override
	protected void initialize()
	{
		// Any global state for your checks can be initialized here!
		// You can already access the AST at this point using getAST()...
	}

	// /////////////////////////////////////////////////////////////////////////
	// /////////// Dispatch checks based on visited AST nodes
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void visit(ExitStatement exit)
	{
		semanticCheck_S50_S53(exit);

		super.visit(exit);
	}

	@Override
	public void visit(RoutineDeclaration routine)
	{
		semanticCheck_S49(routine);

		super.visit(routine);
	}

	// /////////////////////////////////////////////////////////////////////////
	// /////////// Semantic checks
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * S50: Check that exit statement is inside a loop. <br>
	 * S53: Check that integer is > 0 and <= number of containing loops. <br>
	 */
	private void semanticCheck_S50_S53(ExitStatement exit)
	{
		try
		{
			// This method will implicitly check semantics, because it needs the
			// info anyway! So we just see what happens.
			exit.getExitBehindLoop();
		}
		catch (SemanticException e)
		{
			switch (e.getError())
			{
			case ExitThroughRoutineBounds:
				reportExitThroughRoutineBounds(exit);
				break;
			case ExitLevelInvalid:
				reportExitLevelInvalid(exit);
				break;
			default:
				throw e; // this should never happen
			}
		}
	}

	/**
	 * S49: If function/procedure was declared forward, verify forward
	 * declaration matches. <br>
	 */
	private void semanticCheck_S49(RoutineDeclaration routine)
	{
		try
		{
			// This method will implicitly check semantics, because it needs the
			// info anyway! So we just see what happens.
			routine.getForwardDeclaration();
		}
		catch (SemanticException e)
		{
			switch (e.getError())
			{
			case FowardDeclAfterDefinition:
				reportFowardDeclAfterDefinition((RoutineDeclaration) e.getContext()[0], (ForwardDeclaration) e.getContext()[1]);
				break;
			case FowardDeclSignatureMismatch:
				reportFowardDeclSignatureMismatch((RoutineDeclaration) e.getContext()[0], (ForwardDeclaration) e.getContext()[1]);
				break;
			default:
				throw e; // this should never happen
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// /////////// Error messages to display
	// /////////////////////////////////////////////////////////////////////////

	private void reportExitLevelInvalid(ExitStatement exit)
	{
		beginError()
				.print("[ERROR (S53)]: Level parameter for exit statement shall be greater than zero!")
				.newLine()
				.visualizeASTNode(exit)
				.submit();
	}

	private void reportExitThroughRoutineBounds(ExitStatement exit)
	{
		beginError()
				.print("[ERROR (S50)]: Exit statement can not exit loops through routine boundaries!")
				.newLine()
				.visualizeASTNode(exit)
				.submit();
	}

	private void reportFowardDeclAfterDefinition(RoutineDeclaration routine, ForwardDeclaration fwd)
	{
		beginError()
				.print("[ERROR (S49)]: Forward declaration shall occur before actual method definition.")
				.newLine()
				.visualizeASTNode("Method definition", routine)
				.visualizeASTNode("Forward-declaration", fwd)
				.submit();
	}

	private void reportFowardDeclSignatureMismatch(RoutineDeclaration routine, ForwardDeclaration fwd)
	{
		beginError()
				.print("[ERROR (S49)]: Forward declaration and method definition shall have the same signature.")
				.newLine()
				.visualizeASTNode("Method definition", routine)
				.visualizeASTNode("Forward-declaration", fwd)
				.submit();
	}
}
