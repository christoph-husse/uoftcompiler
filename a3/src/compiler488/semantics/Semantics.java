package compiler488.semantics;

import java.util.ArrayList;

import compiler488.ast.stmt.Scope;

public class Semantics
{
	private Scope ast;
	private boolean hasErrors = false;
	private final ArrayList<SemanticASTVisitor> checks = new ArrayList<SemanticASTVisitor>();

	public Scope getAST()
	{
		return ast;
	}

	public Semantics(Scope programAST)
	{
		ast = programAST;

		registerSemanticCheck(new Semantics_Steven());
		registerSemanticCheck(new Semantics_Chris());
		registerSemanticCheck(new Semantics_Cloud());
		registerSemanticCheck(new Semantics_Zheng());
		registerSemanticCheck(new Semantics_Jack());
	}

	public void registerSemanticCheck(SemanticASTVisitor visitor)
	{
		checks.add(visitor);
	}

	public void run()
	{
		hasErrors = false;

		for (SemanticASTVisitor visitor : checks)
		{
			visitor.run(this);
			hasErrors |= visitor.hasErrors();
		}
	}

	public boolean hasErrors()
	{
		return hasErrors;
	}
}
