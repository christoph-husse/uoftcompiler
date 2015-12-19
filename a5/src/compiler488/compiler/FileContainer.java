package compiler488.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

import compiler488.parser.Report;

public class FileContainer
{
	private File fileName;
	private String description;
	private boolean canOverwrite = true;

	public FileContainer(String description, String name)
	{
		this(description, new File(name));
	}

	public FileContainer(String description, File name)
	{
		this.fileName = name;
		this.description = description;
	}

	public File getName()
	{
		return fileName;
	}

	public String getDescription()
	{
		return description;
	}

	public Reader getReader()
	{
		return getReader(false);
	}

	public Reader getRequiredReader()
	{
		return getReader(true);
	}

	public void setCanOverwrite(boolean value)
	{
		canOverwrite = value;
	}

	public void mustBeWritable()
	{
		getPrintStream();
	}

	private Reader getReader(boolean isRequired)
	{
		try
		{
			return new FileReader(getName());
		}
		catch (FileNotFoundException e)
		{
			if (isRequired)
				Report.fatalError("Could not open file \"" + getCanonicalName() + "\" (" + description + ")!", e);
			else
				Report.error("Could not open file \"" + getCanonicalName() + "\" (" + description + ")!", e);

			return null;
		}
	}

	public PrintStream getPrintStream()
	{
		return getPrintStream(false);
	}

	public PrintStream getRequiredPrintStream()
	{
		return getPrintStream(true);
	}

	private PrintStream getPrintStream(boolean isRequired)
	{
		try
		{
			if (getName().exists())
			{
				if (!canOverwrite)
				{
					String msg = "File \"" + getCanonicalName() + "\" (" + description + ") does already exist and user policy forbids overwriting it!";

					if (isRequired)
						Report.fatalError(msg);
					else
						Report.error(msg);

					return null;
				}

				getName().delete();
			}

			return new PrintStream(getName());
		}
		catch (FileNotFoundException e)
		{
			String msg = "Could not open file \"" + getCanonicalName() + "\" (" + description + ") for writing!";

			if (isRequired)
				Report.fatalError(msg, e);
			else
				Report.error(msg, e);

			return null;
		}
	}

	public String getCanonicalName()
	{
		try
		{
			return getName().getCanonicalPath();
		}
		catch (IOException e1)
		{
			return getName().getName();
		}
	}

	public String getContent()
	{
		return getContent(false);
	}

	public String getRequiredContent()
	{
		return getContent(true);
	}

	private String getContent(boolean isRequired)
	{
		Reader in = getReader(isRequired);

		if (in == null)
			return null;

		char[] arr = new char[1024];
		StringBuffer buf = new StringBuffer();
		int numChars;

		try
		{
			while ((numChars = in.read(arr, 0, arr.length)) > 0)
			{
				buf.append(arr, 0, numChars);
			}
		}
		catch (IOException e)
		{
			String msg = "Could not read entire content of file \"" + getCanonicalName() + "\" (" + description + ")!";

			if (isRequired)
				Report.fatalError(msg, e);
			else
				Report.error(msg, e);

			return null;
		}

		return buf.toString();
	}

}
