package compiler488.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FixedIterable<T> implements Iterable<T>
{

	private List<T> elements;

	public FixedIterable()
	{
		elements = new ArrayList<T>(0);
	}

	public FixedIterable(Iterable<T> iter)
	{
		elements = new ArrayList<T>();
		for (T e : iter)
			elements.add(e);
	}

	public FixedIterable(T e1)
	{
		elements = new ArrayList<T>(2);
		if (e1 != null)
			elements.add(e1);
	}

	public FixedIterable(T e1, T e2)
	{
		elements = new ArrayList<T>(1);
		if (e1 != null)
			elements.add(e1);
		if (e2 != null)
			elements.add(e2);
	}

	public FixedIterable(T e1, T e2, T e3)
	{
		elements = new ArrayList<T>(3);
		if (e1 != null)
			elements.add(e1);
		if (e2 != null)
			elements.add(e2);
		if (e3 != null)
			elements.add(e3);
	}

	public FixedIterable(T e1, T e2, T e3, T e4)
	{
		elements = new ArrayList<T>(4);
		if (e1 != null)
			elements.add(e1);
		if (e2 != null)
			elements.add(e2);
		if (e3 != null)
			elements.add(e3);
		if (e4 != null)
			elements.add(e4);
	}

	@Override
	public Iterator<T> iterator()
	{
		return elements.iterator();
	}

}
