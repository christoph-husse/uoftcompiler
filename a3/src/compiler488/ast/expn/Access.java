package compiler488.ast.expn;

import compiler488.ast.decl.Declaration;
import compiler488.ast.type.Type;
import compiler488.parser.Location;
import compiler488.symbol.SymbolEntry;

public class Access extends Expression {
	protected Identifier name;
	
	public Access(Location location, Identifier name) {
		super(location);
		this.name = name;
	}

	public Identifier getName()
	{
		return this.name;
	}
	
	public Declaration getDeclaration()
	{
		SymbolEntry entry = resolveSymbol(name.getIdent());
		
		//System.out.println(entry + " " + entry.getDeclaration().getType());
		if ( entry != null )
		{
			return entry.getDeclaration();
		}
		
		return null;
	}
	
	@Override
	public Type getType() {
		Type temp = super.getType();
		
		if ( temp == null )
		{
			if ( getDeclaration() != null )
			{
				temp = getDeclaration().getType();
			}
		}
		
		return temp;
	}
}
