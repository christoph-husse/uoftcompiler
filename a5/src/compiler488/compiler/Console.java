package compiler488.compiler;

import java.util.Stack;

import org.fusesource.jansi.AnsiConsole;

/**
 * You should not use this class directly, but "Report" instead.
 */
public class Console
{
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_BLACK = "\u001B[30m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_PURPLE = "\u001B[35m";
	private static final String ANSI_CYAN = "\u001B[36m";

	private static final String ANSI_WHITE = "\u001B[37m";

	public enum Color
	{
		Red, Green, Yellow, Blue, Pruple, Cyan, Default,
	}

	private static final Stack<Color> colorStack = new Stack<Color>();

	static
	{
		// colored console output is highly platform specific and this library
		// just makes sure that it does either nothing or applies colors in the
		// way it was intended.
		AnsiConsole.systemInstall();
	}

	public static int getWidth()
	{
		return 70;
	}

	public static void resetColor()
	{
		colorStack.clear();
		System.out.print(ANSI_RESET);
	}

	public static void beginColor(Color color)
	{
		colorStack.push(color);
		System.out.print(getColorEscape(color));
	}

	private static String getColorEscape(Color color)
	{
		String escape = ANSI_RESET;

		switch (color)
		{
		case Red:
			escape = ANSI_RED;
			break;
		case Green:
			escape = ANSI_GREEN;
			break;
		case Yellow:
			escape = ANSI_YELLOW;
			break;
		case Blue:
			escape = ANSI_BLUE;
			break;
		case Pruple:
			escape = ANSI_PURPLE;
			break;
		case Cyan:
			escape = ANSI_CYAN;
			break;
		case Default:
			escape = ANSI_RESET;
			break;
		}

		return escape;
	}

	public static void endColor()
	{
		if (colorStack.empty())
			return;

		colorStack.pop();

		if (!colorStack.empty())
			System.out.print(getColorEscape(colorStack.peek()));
		else
			System.out.print(ANSI_RESET);
	}

	public static void write(Color color, String line)
	{
		beginColor(color);
		write(line);
		endColor();
	}

	public static void write(String line)
	{
		System.out.print(line);
	}

	public static void writeLine()
	{
		writeLine("");
	}

	public static void writeLine(Color color, String line)
	{
		write(color, line);
		System.out.println();
	}

	public static void writeLine(String line)
	{
		write(line);
		System.out.println();
	}
}
