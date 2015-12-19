package compiler488.ast;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import compiler488.ast.decl.RoutineDeclaration;
import compiler488.ast.stmt.Scope;
import compiler488.compiler.ReportVerbosity;
import compiler488.parser.Location;
import compiler488.parser.Report;
import compiler488.symbol.SymbolEntry;
import compiler488.symbol.SymbolTable;

/**
 * 
 */
public class ASTNode
{
	private ASTNode parent;
	private Iterable<ASTNode> children;
	private Location location;
	private final int id;

	private final static AtomicInteger globalIdCounter = new AtomicInteger(1);

	/**
	 * For internal use only! The only class interacting with this method should
	 * be <b>ASTVisitor</b>.
	 */
	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	/**
	 * For internal use only! You should never have to call this constructor
	 * directly!
	 */
	public ASTNode(Location location)
	{
		this.location = location;
		this.id = globalIdCounter.getAndIncrement();
	}

	public <E extends ASTNode> E getParent()
	{
		return (E) parent;
	}

	/**
	 * Returns an ID unique for each constructed instance during the current
	 * execution of the program (not persistent).
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Returns location info for this node. Its been a major effort to provide
	 * as much information about each node's position in the source code as
	 * possible!
	 */
	public Location getLocation()
	{
		return location;
	}

	/**
	 * Reserved for internal use ONLY!
	 */
	public void __setLocation_Internal(Location newLoc)
	{
		location = newLoc;
	}

	/**
	 * If there is a parent scope, then its instance is returned. Otherwise
	 * <b>null</b> is returned. If the current node is itself a scope, this
	 * method does still return the parent scope, and not <b>this</b>.
	 */
	public Scope getScope()
	{
		ASTNode parent = getParent();
		if (parent == null)
			return null;

		if (parent instanceof Scope)
			return (Scope) parent;
		return parent.getScope();
	}

	/**
	 * Returns the local symbol table of the parent scope. Note that in the
	 * returned table, only symbols declared explicitly in the parent scope are
	 * visible. To do a symbol query over all visible symbols, use
	 * <b>ASTNode.hasSymbol()</b> or <b>ASTNode.resolveSymbol()</b> instead. If
	 * you call this method on a scope node, it will return the symbol table of
	 * the parent scope. To query the symbol table of a scope node, use
	 * <b>Scope.getLocalSymbolTable()</b> instead.
	 */
	public SymbolTable getSymbols()
	{
		return getScope().getLocalSymbolTable();
	}

	/**
	 * Is a symbol with the given name defined in any parent scope?
	 */
	public boolean hasSymbol(String name)
	{
		Scope scope = getScope();

		if (scope == null)
			return false;

		if (scope.getLocalSymbolTable().contains(name))
			return true;

		return scope.hasSymbol(name);
	}

	/**
	 * Resolves a name to the first symbol with this name encountered while
	 * traversing all parent scopes, starting from the most nested one. If no
	 * such symbol can be found, null is returned.
	 */
	public SymbolEntry resolveSymbol(String name)
	{
		Scope scope = getScope();

		if (scope == null)
			return null;

		SymbolEntry sym = scope.getLocalSymbolTable().resolve(name);

		if (sym != null)
			return sym;

		return scope.resolveSymbol(name);
	}

	/**
	 * Returns an iterable which lists all children of an AST node in
	 * lexical-order. For instance "b := b + b;" has two children. The first one
	 * is the target variable "b" and the second one is the source expression
	 * "b + b". So the lexical-order simply means the order in the file.
	 */
	public Iterable<ASTNode> getChildren()
	{
		if (children != null)
			return children;
		else
			return children = __createListOfChildren_Internal();
	}

	/**
	 * Returns a forward iterator starting from the parent node, recursively
	 * following the path to the AST's root. Use a foreach-loop to follow this
	 * path.
	 */
	public Iterable<ASTNode> getParents()
	{
		return new Iterable<ASTNode>()
		{
			@Override
			public Iterator<ASTNode> iterator()
			{
				return new Iterator<ASTNode>()
				{
					private ASTNode node = ASTNode.this;

					@Override
					public boolean hasNext()
					{
						return node.getParent() != null;
					}

					@Override
					public ASTNode next()
					{
						return node = node.getParent();
					}

					@Override
					public void remove()
					{
					}
				};
			}
		};
	}

	/**
	 * Returns a forward iterator starting from the parent scope, if there is
	 * any, and recursively following the path to the AST's root, listing all
	 * <b>Scope</b> nodes on its way.
	 */
	public Iterable<Scope> getParentScopes()
	{
		return new Iterable<Scope>()
		{
			@Override
			public Iterator<Scope> iterator()
			{
				return new Iterator<Scope>()
				{
					private ASTNode anchor = ASTNode.this;

					@Override
					public boolean hasNext()
					{
						return anchor.getScope() != null;
					}

					@Override
					public Scope next()
					{
						return (Scope) (anchor = anchor.getScope());
					}

					@Override
					public void remove()
					{
					}
				};
			}
		};
	}

	/**
	 * Returns the most nested routine, this node is embedded in. If the node is
	 * not embedded in a routine, then <b>null</b> is returned.
	 */
	public RoutineDeclaration getParentRoutine()
	{
		for (ASTNode p : getParents())
		{
			if (p instanceof RoutineDeclaration)
				return (RoutineDeclaration) p;
		}

		return null;
	}

	/**
	 * Same as getChildren(), just that this one is not cached and should return
	 * a new iterable everytime!
	 * 
	 * Any AST node must implement this function properly for the compiler to
	 * work!
	 * */
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		return new FixedIterable<ASTNode>();
	}

	/**
	 * For internal use only. Called immediately after AST has been generated by
	 * JCup! The ONLY responsibility of this function is to walk the entire tree
	 * and connecting all nodes properly. It will implicitly invoke
	 * createListOfChildren() for all nodes who are reachable through
	 * getChildren().
	 **/
	public void __initialize_Internal()
	{
		location.snapToNonWhitespaces();

		for (ASTNode child : getChildren())
		{
			child.parent = this;
		}

		for (ASTNode child : getChildren())
		{
			child.__initialize_Internal();
		}

		if (parent == null)
		{
			// uncomment to dump ASTNode formatting while parsing
			// printSilbings();
		}
	}

	/**
	 * A debug method for testing visualization of AST nodes. Just recursively
	 * dumps out visualizations for ALL nodes of the AST.
	 */
	private void printSilbings()
	{
		Report.beginInfo(ReportVerbosity.Normal).visualizeASTNode(this);

		for (ASTNode child : getChildren())
		{
			child.printSilbings();
		}
	}

	/**
	 * Return the offset (base zero) of the rightmost character in the source
	 * file <b>ASTNode.getLocation().getSourceCode()</b> that is still part of
	 * this node or one of its children. The offset points at this character
	 * (inclusive), not behind it.
	 */
	public int getRightmostOffset()
	{
		return getRightmostLocation(location).getOffset(Location.END);
	}

	/**
	 * Recursively looks for the rightmost character of this node.
	 */
	private Location getRightmostLocation(Location current)
	{
		if (current.getOffset(Location.END) < location.getOffset(Location.END))
			current = location;

		for (ASTNode child : getChildren())
		{
			current = child.getRightmostLocation(current);
		}

		return current;
	}

	/**
	 * Return the offset (base zero) of the leftmost character in the source
	 * file <b>ASTNode.getLocation().getSourceCode()</b> that is still part of
	 * this node or one of its children. The offset points at this character
	 * (inclusive), not behind it.
	 */
	public int getLeftmostOffset()
	{
		return getLeftmostLocation(location).getOffset(Location.BEGIN);
	}

	/**
	 * Recursively looks for the leftmost character of this node.
	 */
	private Location getLeftmostLocation(Location current)
	{
		if (current.getOffset(Location.BEGIN) > location.getOffset(Location.BEGIN))
			current = location;

		for (ASTNode child : getChildren())
		{
			current = child.getLeftmostLocation(current);
		}

		return current;
	}

	/**
	 * Return some source code information about this node. Unless a node has a
	 * better way to express itself, this method is usually enough to provide
	 * useful insight during debugging.
	 */
	@Override
	public String toString()
	{
		return location.toString();
	}
}
