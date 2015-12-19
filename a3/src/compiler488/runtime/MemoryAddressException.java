package compiler488.runtime;

/**
 * Exception subclass for reporting machine address errors
 * 
 * @version $ CSC488S Winter 2012/2013 2013-01-28 11:26 $
 * @author Danny House
 */
public class MemoryAddressException extends RuntimeException
{
	private static final long serialVersionUID = 3036339939871904424L;

	public MemoryAddressException(String msg)
	{
		super(msg);
	}
}
