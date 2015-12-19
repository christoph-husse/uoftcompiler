package compiler488.ast;

import compiler488.semantics.SemanticError;

public class SemanticException extends RuntimeException
{
	private static final long serialVersionUID = -8634745437325482407L;
	private SemanticError error;
	private ASTNode[] ctx;

	public SemanticException(SemanticError error, ASTNode... ctx)
	{
		this.error = error;
		this.ctx = ctx;
	}

	public SemanticError getError()
	{
		return error;
	}

	public ASTNode[] getContext()
	{
		return ctx;
	}

	@Override
	public String toString()
	{
		return "[ERROR]: " + error;
	}
}
