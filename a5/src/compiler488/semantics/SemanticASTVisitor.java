package compiler488.semantics;

import compiler488.ast.ASTLexicalOrderVisitor;
import compiler488.ast.stmt.Scope;
import compiler488.parser.Report;

abstract class SemanticASTVisitor extends ASTLexicalOrderVisitor
{
	private Semantics semantics;
	private boolean hasErrors = false;

	public SemanticASTVisitor()
	{
	}

	protected abstract void initialize();

	public Scope getAST()
	{
		return semantics.getAST();
	}

	public boolean hasErrors()
	{
		return hasErrors;
	}

	public void run(Semantics semantics)
	{
		hasErrors = false;
		this.semantics = semantics;

		initialize();

		getAST().accept(this);
	}

	protected Report.ReportEntryBuilder beginError()
	{
		hasErrors = true;
		return Report.beginError();
	}
}