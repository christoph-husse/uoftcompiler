package compiler488.symbol;

import java.util.HashMap;

import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.ParameterDeclaration;
import compiler488.ast.decl.RoutineDeclaration;
import compiler488.ast.decl.VariableDeclaration;
import compiler488.ast.stmt.Scope;
import compiler488.symbol.SymbolEntry;

public class SymbolTable
{
	private Scope scope;
	private final HashMap<String, SymbolEntry> symbols = new HashMap<String, SymbolEntry>();

	// Stack offset of the next variable on this table
	private short currOffset = 0;
	private short currOffsetBackward = -3;

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

	/**
	 * Sets the offset of symbol "name".
	 * The symbol's must be declared as a VariableAccess.
	 * @param words the size of the variable (1 for vars, more for arrays)
	 */
	public void setOffset(String name, short words)
	{
		Declaration d = symbols.get(name).getDeclaration();
        if (d instanceof VariableDeclaration) 
            ((VariableDeclaration)d).setOffset(currOffset);
        else if (d instanceof ParameterDeclaration)
            ((ParameterDeclaration)d).setOffset(currOffset);
		else
			throw new RuntimeException("Unhandled offset declaration type.");

		currOffset += words;
	}

	public void setOffsetBackward(String name, short words)
	{
		Declaration d = symbols.get(name).getDeclaration();
		((ParameterDeclaration)d).setOffset(currOffsetBackward);

		currOffsetBackward -= words;
	}
}
