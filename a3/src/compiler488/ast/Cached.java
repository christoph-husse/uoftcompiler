package compiler488.ast;

public class Cached<T>
{
	private T value;
	private boolean hasVal;

	public T getValue()
	{
		if (!hasValue())
			throw new RuntimeException("Attempting to access value in empty cache.");

		return value;
	}

	public void setValue(T value)
	{
		hasVal = true;
		this.value = value;
	}

	public void clear()
	{
		hasVal = false;
		value = null;
	}

	public boolean hasValue()
	{
		return hasVal;
	}
}
