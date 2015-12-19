package compiler488.semantics;

import compiler488.ast.decl.ArrayDeclaration;
import compiler488.ast.decl.VariableDeclaration;
import compiler488.ast.expn.IntConst;
import compiler488.symbol.SymbolEntry;
import compiler488.ast.type.Type;

public class Semantics_Cloud extends SemanticASTVisitor
{
	@Override
	protected void initialize()
	{
		// Any global state for your checks can be initialized here!
		// You can already access the AST at this point using getAST()...
	}

	// ////////////////////////////////////////////////////////////////////
	// /////////// Dispatch checks based on visited AST nodes
	// ////////////////////////////////////////////////////////////////////

	@Override
	public void visit(ArrayDeclaration decl)
	{
		semanticCheck_S46(decl);
		super.visit(decl);
	}

	@Override
	public void visit(VariableDeclaration decl)
	{
		semanticCheck_T01(decl);
		super.visit(decl);
	}

	@Override
	public void visit(IntConst cons)
	{
		semanticCheck_T08(cons);
		super.visit(cons);
	}

	// ////////////////////////////////////////////////////////////////////
	// /////////// Semantic checks
	// ////////////////////////////////////////////////////////////////////

	private void semanticCheck_S46(ArrayDeclaration decl)
	{
		if (decl.getLowerBound() > decl.getUpperBound())
		{
			reportArrayBoundMismatch(decl);
		}
	}

	private void semanticCheck_T01(VariableDeclaration decl)
	{
		SymbolEntry symbol = decl.resolveSymbol(decl.getName().toString());
		if (symbol != null)
		{
			Type symbolEntry = symbol.getDeclaration().getType();
			if (!(decl.getType().equals(symbolEntry)))
			{
				reportRedeclaration(decl);
			}
		}
	}

	private void semanticCheck_T08(IntConst cons)
	{
		if (cons.getValue() > Short.MAX_VALUE ||
			cons.getValue() < Short.MIN_VALUE)
		{
			reportIntegerTooLarge(cons);
		}
	}

	// ////////////////////////////////////////////////////////////////////
	// /////////// Error messages to display
	// ////////////////////////////////////////////////////////////////////
	
	private void reportArrayBoundMismatch(ArrayDeclaration decl)
	{
		beginError()
			.print("[ERROR (S46)]: Array lower bound must not exceed upper bound.")
			.newLine()
			.visualizeASTNode(decl)
			.submit();
	}

	private void reportRedeclaration(VariableDeclaration decl)
	{
		String symbolName = decl.getName().toString();
		beginError()
			.print("[ERROR (T01)]: " + symbolName +
			       " was redeclared in the same scope.")
			.newLine()
			.visualizeASTNode(decl)
			.submit();
	}

	private void reportIntegerTooLarge(IntConst cons)
	{
		beginError()
			.print("[ERROR (T08)]: Integer value is too large for a word.")
			.newLine()
			.visualizeASTNode(cons)
			.submit();
	}
}
