package compiler488.symbol;

import compiler488.ast.decl.Declaration;

public class SymbolEntry
{
	private Declaration decl;
	private String name;

	public SymbolEntry(String name, Declaration decl)
	{
		this.name = name;
		this.decl = decl;
	}

	public Declaration getDeclaration()
	{
		return decl;
	}

	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		return decl.getClass() + " " + name;
	}
}
