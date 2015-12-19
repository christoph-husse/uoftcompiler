package compiler488.parser;

/**
 * Exception subclass for reporting parser syntax errors
 * 
 * @version $Revision: 7 $ $Date: 2010-01-08 17:40:36 -0500 (Fri, 08 Jan 2010) $
 * @author Dave Wortman
 */
public class SyntaxErrorException extends Exception
{
	private static final long serialVersionUID = -826355831626742346L;

	public SyntaxErrorException(String msg)
	{
		super(msg);
	}
}
