package compiler488.ast.stmt;

import compiler488.ast.ASTList;
import compiler488.ast.ASTNode;
import compiler488.ast.ASTVisitor;
import compiler488.ast.FixedIterable;
import compiler488.ast.decl.Declaration;
import compiler488.parser.Location;
import compiler488.symbol.SymbolTable;

/**
 * Represents the declarations and instructions of a scope construct.
 */
public class Scope extends Statement
{
	private ASTList<Declaration> declarations;
	private ASTList<Statement> statements;
	private SymbolTable symbols;

	public Scope(Location location, ASTList<Declaration> decls, ASTList<Statement> stmts)
	{
		super(location);
		declarations = decls;
		statements = stmts;
	}

	public Scope(Location location, ASTList<Statement> stmts)
	{
		this(location, new ASTList<Declaration>(location), stmts);
	}

	public SymbolTable getLocalSymbolTable()
	{
		if (symbols == null)
			symbols = new SymbolTable(this);

		return symbols;
	}

	public ASTList<Declaration> getDeclarations()
	{
		return declarations;
	}

	public ASTList<Statement> getStatements()
	{
		return statements;
	}

	@Override
	/**
	 * Returns the lexical level of this scope's contents.
	 */
	public short lexicLevel()
	{
		return (short) (super.lexicLevel()+1);
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>(declarations, statements);
	}
}
