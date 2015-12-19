package compiler488.symbol;

import java.util.HashMap;

import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.ParameterDeclaration;
import compiler488.ast.decl.RoutineDeclaration;
import compiler488.ast.stmt.Scope;
import compiler488.symbol.SymbolEntry;

public class SymbolTable
{
	private Scope scope;
	private final HashMap<String, SymbolEntry> symbols = new HashMap<String, SymbolEntry>();

	/**
	 * 
	 */
	public SymbolTable(Scope scope)
	{
		RoutineDeclaration decl;
		this.scope = scope;
	
		// Enter the declarations in this scope into the symbol table.
		// This should implement S10-S19, S47, S48, S54
		for (Declaration d : scope.getDeclarations())
		{
			symbols.put(d.getName().toString(),
				new SymbolEntry(d.getName().toString(), d));
		}
		
		if ( scope.getParent() instanceof RoutineDeclaration )
		{
			decl = (RoutineDeclaration)scope.getParent();
			for (ParameterDeclaration p : decl.getParameters())
			{
				symbols.put(p.getName().toString(),
					new SymbolEntry(p.getName().toString(), p));
			}
		}
	}

	@Override
	public String toString()
	{
		return symbols.toString();
	}

	public Scope getScope()
	{
		return scope;
	}

	public SymbolEntry resolve(String name)
	{
		return symbols.get(name);
	}

	public boolean contains(String name)
	{
		return symbols.containsKey(name);
	}
}
