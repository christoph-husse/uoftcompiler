package compiler488.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import compiler488.parser.Location;

/**
 * For nodes with an arbitrary number of children.
 */
public class ASTList<E extends ASTNode> extends ASTNode implements Iterable<E>
{

	private final ArrayList<E> entries = new ArrayList<E>();

	/**
	 * Create an empty list.
	 */
	public ASTList(Location location)
	{
		super(location);
	}

	/**
	 * Create a list with one element.
	 */
	public ASTList(E ast)
	{
		super(ast.getLocation());
		entries.add(ast);
	}

	/**
	 * The number of elements in the list.
	 */
	public int size()
	{
		return entries.size();
	}

	public boolean empty()
	{
		return entries.size() == 0;
	}

	public E at(int index)
	{
		return entries.get(index);
	}

	/**
	 * Append an element to the list, then return the list. This is a
	 * pure-side-effect method, so it doesn't need to return anything. However,
	 * we like the conciseness gained when such methods return the target
	 * object.
	 */
	public ASTList<E> addLast(E ast)
	{

		getLocation().extend(ast.getLocation()).pivotMiddle();

		entries.add(ast);
		return this;
	}

	public ASTList<E> addRange(Iterable<E> list)
	{
		for (E e : list)
			addLast(e);
		return this;
	}

	/**
	 * Return the contatenation of the strings obtained by sending
	 * <b>toString</b> to each element.
	 */
	@Override
	public String toString()
	{
		String res = "";

		for (int i = 0; i < entries.size(); i++)
		{
			res += entries.get(i).toString();
			if (i < entries.size() - 1)
				res += ", ";
		}

		return res;
	}

	@Override
	public Iterator<E> iterator()
	{
		return entries.iterator();
	}

	public void accept(ASTVisitor visitor)
	{
		visitor.visit(this);
	}

	@Override
	protected Iterable<ASTNode> __createListOfChildren_Internal()
	{
		List<ASTNode> list = new ArrayList<ASTNode>();
		list.addAll(entries);
		return list;
	}
}
