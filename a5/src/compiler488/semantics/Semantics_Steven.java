package compiler488.semantics;

import compiler488.ast.ASTNode;
import compiler488.ast.decl.RoutineDeclaration;
import compiler488.ast.expn.FunctionInvocation;
import compiler488.ast.stmt.ProcedureInvocation;
import compiler488.ast.stmt.ResultStatement;
import compiler488.ast.stmt.ReturnStatement;
import compiler488.symbol.SymbolEntry;

public class Semantics_Steven extends SemanticASTVisitor
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
	public void visit(ResultStatement stmt)
	{
		semanticCheck_S51(stmt);

		super.visit(stmt);
	}
	
	@Override
	public void visit(ReturnStatement stmt)
	{
		semanticCheck_S52(stmt);

		super.visit(stmt);
	}
	
	@Override
	public void visit(FunctionInvocation expr)
	{
		semanticCheck_S40(expr);

		super.visit(expr);
	}
	
	@Override
	public void visit(ProcedureInvocation stmt)
	{
		semanticCheck_S41(stmt);

		super.visit(stmt);
	}

	// /////////////////////////////////////////////////////////////////////////
	// /////////// Semantic checks
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * S51: Check that result is inside a function. <br>
	 */
	private void semanticCheck_S51(ResultStatement stmt)
	{
		for (ASTNode p : stmt.getParents()) 
		{
			if (p instanceof RoutineDeclaration) 
			{
				if (((RoutineDeclaration) p).hasReturnType()) 
				{
					return;
				}
				
				reportResultInsideProcedure(stmt);
			}
		}
		
		reportResultThroughRoutineBounds(stmt);
	}
	
	/**
	 * S52: Check that return statement is inside a procedure. <br>
	 */
	private void semanticCheck_S52(ReturnStatement stmt)
	{
		for (ASTNode p : stmt.getParents()) 
		{
			if (p instanceof RoutineDeclaration) 
			{
				if (!((RoutineDeclaration) p).hasReturnType()) 
				{
					return;
				}
				
				reportReturnInsideFunction(stmt);
			}
		}
		
		reportReturnThroughRoutineBounds(stmt);
	}
	
	/**
	 * S40: Check that identifier has been declared as a function. <br>
	 */
	private void semanticCheck_S40(FunctionInvocation expr) 
	{
		if (expr.getDeclaration() != null) 
		{
			if (expr.getDeclaration() instanceof RoutineDeclaration) 
			{
				if (((RoutineDeclaration) expr.getDeclaration()).hasReturnType()) 
				{
					return;
				}
			}
		}
		
		reportBadFunctionCall(expr);
	}
	
	/**
	 * S41: Check that identifier has been declared as a procedure. <br>
	 */
	private void semanticCheck_S41(ProcedureInvocation stmt) 
	{
		SymbolEntry entry = stmt.resolveSymbol(stmt.getName().getIdent());
		
		if (entry != null)
		{
			if (entry.getDeclaration() instanceof RoutineDeclaration) 
			{
				if (!((RoutineDeclaration) entry.getDeclaration()).hasReturnType()) 
				{
					return;
				}
			}
		}
				
		reportBadProcedureCall(stmt);
	}

	// /////////////////////////////////////////////////////////////////////////
	// /////////// Error messages to display
	// /////////////////////////////////////////////////////////////////////////
	
	private void reportResultInsideProcedure(ResultStatement stmt)
	{
		beginError()
				.print("[ERROR (S51)]: Result statement cannot be used within procedures!")
				.newLine()
				.visualizeASTNode(stmt)
				.submit();
	}
	
	private void reportResultThroughRoutineBounds(ResultStatement stmt)
	{
		beginError()
				.print("[ERROR (S51)]: Result statement must be within a function scope!")
				.newLine()
				.visualizeASTNode(stmt)
				.submit();
	}
	
	private void reportReturnInsideFunction(ReturnStatement stmt)
	{
		beginError()
				.print("[ERROR (S52)]: Return statement cannot be used within functions!")
				.newLine()
				.visualizeASTNode(stmt)
				.submit();
	}
	
	private void reportReturnThroughRoutineBounds(ReturnStatement stmt)
	{
		beginError()
				.print("[ERROR (S52)]: Return statement must be within a function scope!")
				.newLine()
				.visualizeASTNode(stmt)
				.submit();
	}
	
	private void reportBadFunctionCall(FunctionInvocation expr)
	{
		beginError()
				.print("[ERROR (S40)]: Identifier has not been declared as a function!")
				.newLine()
				.visualizeASTNode(expr)
				.submit();
	}
	
	private void reportBadProcedureCall(ProcedureInvocation stmt)
	{
		beginError()
				.print("[ERROR (S41)]: Identifier has not been declared as a procedure!")
				.newLine()
				.visualizeASTNode(stmt)
				.submit();
	}
}
