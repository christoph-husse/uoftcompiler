package compiler488.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import compiler488.ast.ASTNode;
import compiler488.compiler.Console;
import compiler488.compiler.Console.Color;
import compiler488.compiler.ReportVerbosity;

/**
 * This class shall be used for ALL console output. It provides convenient
 * wrappers around quite complex IO tasks.
 */
public class Report
{
	private static ReportVerbosity verbosity;
	private static ReportVerbosity[] descendingVerbosities;
	private static boolean needsNewLine;
	private static boolean hadFatalError;

	static
	{
		reset();
	}

	/**
	 * Fully reset global state. This is important for main() reentrence.
	 */
	public static void reset()
	{
		verbosity = ReportVerbosity.Normal;
		descendingVerbosities = ReportVerbosity.values();
		needsNewLine = false;
		hadFatalError = false;
	}

	/**
	 * Set to true if a fatal error has occured since the last reset(). No code
	 * except the test-suite is in a position to take advantage of this method!
	 */
	public static boolean hadFatalError()
	{
		return hadFatalError;
	}

	private static void error(boolean isFatal, String message, Exception e)
	{
		ReportEntryBuilder builder = beginError()
				.newLine((isFatal ? "[FATAL-ERROR]: " : "[ERROR]: ") + message)
				.newLine();

		if (e != null)
		{
			builder
					.indent(4)
					.newLine("Exception details:")
					.indent(4)
					.newLine(e.toString())
					.newLine()
					.newLine("[STACKTRACE]")
					.printStackTrace(e);
		}

		builder
				.setFatal(isFatal)
				.submit();
	}

	/**
	 * Is the verbosity of the message to print at least as high as our current
	 * report verbosity?
	 */
	private static boolean shouldPrint(ReportVerbosity v)
	{
		for (int i = 0; i < descendingVerbosities.length; i++)
		{
			if (descendingVerbosities[i] == verbosity)
				return true;

			if (descendingVerbosities[i] == v)
				return false;
		}

		return false;
	}

	private static void error(boolean isFatal, String message)
	{
		error(isFatal, message, null);
	}

	/**
	 * Standard error reporting with a short message and an exception. The
	 * exception stack-trace is only visible in verbose mode.
	 */
	public static void error(String message, Exception e)
	{
		error(false, message, e);
	}

	/**
	 * Prints an error message.
	 */
	public static void error(String message)
	{
		error(false, message);
	}

	/**
	 * Same as error(message, e), just that compilation terminates directly.
	 * This method will NOT return!
	 */
	public static void fatalError(String message, Exception e)
	{
		error(true, message, e);
	}

	/**
	 * Same as error(message), just that compilation terminates directly. This
	 * method will NOT return!
	 */
	public static void fatalError(String message)
	{
		error(true, message);
	}

	/**
	 * Same as error(message), just that compilation terminates directly. This
	 * method will NOT return!
	 */
	public static void setVerbosity(ReportVerbosity level)
	{
		verbosity = level;
	}

	/**
	 * The current report verbosity. Only messages with at least that verbosity
	 * will be visible.
	 */
	public static ReportVerbosity getVerbosity()
	{
		return verbosity;
	}

	/**
	 * Start an info message with custom verbosity. Nothing is printed until you
	 * invoke submit() on the result.
	 */
	public static ReportEntryBuilder beginInfo(ReportVerbosity verbosity)
	{
		return begin(verbosity)
				.switchColor(Color.Default);
	}

	/**
	 * Start an error message with error verbosity. Nothing is printed until you
	 * invoke submit() on the result.
	 */
	public static ReportEntryBuilder beginError()
	{
		return begin(ReportVerbosity.Error)
				.switchColor(Color.Red);
	}

	/**
	 * Start a fatal-error message with error verbosity. Nothing is printed
	 * until you invoke submit() on the result. The call to submit() will never
	 * return!
	 */
	public static ReportEntryBuilder beginFatalError()
	{
		return begin(ReportVerbosity.Error)
				.switchColor(Color.Red)
				.setFatal(true);
	}

	/**
	 * Start a message with custom verbosity. Nothing is printed until you
	 * invoke submit() on the result.
	 */
	public static ReportEntryBuilder begin(ReportVerbosity verbosity)
	{
		return new ReportEntryBuilder(verbosity);
	}

	/**
	 * Start a message with normal verbosity. Nothing is printed until you
	 * invoke submit() on the result.
	 */
	public static ReportEntryBuilder begin()
	{
		return new ReportEntryBuilder();
	}

	/**
	 * Start an action with custom verbosity. Nothing is printed until you
	 * invoke submit() on the result. An action supports failed() and success()
	 * calls which visualize the outcome. If anything has been printed in
	 * between, it will output the action description again. This is quite handy
	 * and hard/impossible to accomplish without this action mechanism...
	 */
	public static ReportEntryBuilder beginAction(ReportVerbosity verbosity, String description)
	{
		ReportEntryBuilder res = new ReportEntryBuilder(verbosity, description).switchColor(Color.Yellow).print(description);

		needsNewLine = true;
		return res;
	}

	/**
	 * Since messages can get quite convoluted, we follow the builder pattern.
	 * You get a builder back and can create a complex message. When you are
	 * done, just call submit() on it to print it. In reality it is not
	 * specified when stuff will be printed but after calling submit() it has
	 * been printed for sure (unless of course the verbosity was too low)!
	 * 
	 * You should not create builders yourself, but rather use the above helpers
	 * for it.
	 */
	public static class ReportEntryBuilder
	{
		private String actionDescription;
		private boolean shouldPrint, isFatal;
		private int colorSwitches = 0;

		public ReportEntryBuilder()
		{
			shouldPrint = shouldPrint(ReportVerbosity.Normal);
		}

		public ReportEntryBuilder(ReportVerbosity verbosity)
		{
			shouldPrint = shouldPrint(verbosity);
		}

		public ReportEntryBuilder(ReportVerbosity verbosity, String actionDescription)
		{
			this.actionDescription = actionDescription;
			shouldPrint = shouldPrint(verbosity);
		}

		/**
		 * Print a string. Huh. This is basically the same as System.out.print()
		 * just that it isn't when going into the details.
		 */
		public ReportEntryBuilder print(String str)
		{
			if (!shouldPrint)
				return this;

			if (needsNewLine)
				Console.writeLine();
			needsNewLine = false;

			Console.write(str);
			return this;
		}

		/**
		 * An action has succeeded. Will print a green "[DONE]" and if necessary
		 * also the original action description.
		 */
		public ReportEntryBuilder success()
		{
			if (!shouldPrint)
				return this;

			if (!needsNewLine)
				switchColor(Color.Yellow).print(actionDescription);

			switchColor(Color.Green);
			Console.writeLine(" [DONE]");
			needsNewLine = false;
			return this;
		}

		/**
		 * An action has succeeded. Will print a red "[ERROR]" and if necessary
		 * also the original action description.
		 */
		public ReportEntryBuilder failed()
		{
			if (!shouldPrint)
				return this;

			if (!needsNewLine)
				switchColor(Color.Yellow).print(actionDescription);

			switchColor(Color.Red);
			Console.writeLine(" [ERROR]");
			needsNewLine = false;
			return this;
		}

		/**
		 * Same as System.out.println();
		 */
		public ReportEntryBuilder newLine()
		{
			if (!shouldPrint)
				return this;
			Console.writeLine();
			return this;
		}

		/**
		 * Same as System.out.println(str);
		 */
		public ReportEntryBuilder newLine(String str)
		{
			if (!shouldPrint)
				return this;
			Console.writeLine();
			return print(str);
		}

		/**
		 * Change the color for all further print() commands in the current
		 * builder. Not that colored output is not supported for all consoles,
		 * so you should make sure that your output makes sense without color.
		 */
		public ReportEntryBuilder switchColor(Color color)
		{
			if (!shouldPrint)
				return this;
			Console.beginColor(color);
			colorSwitches++;
			return this;
		}

		/**
		 * Sets if after submit(), the application will be terminated.
		 */
		public ReportEntryBuilder setFatal(boolean isFatal)
		{
			this.isFatal = isFatal;
			return this;
		}

		/**
		 * No effect at the moment, since too complicated to implement in a
		 * cross-platform fashion. Should leave space at the beginning of each
		 * line.
		 */
		public ReportEntryBuilder indent(int chars)
		{
			if (!shouldPrint)
				return this;
			// TODO: this is tough to implement!
			return this;
		}

		/**
		 * Prints nothing but does other work that is necessary to make
		 * everything work as it should. So this command is still quite
		 * important!
		 */
		@SuppressWarnings("deprecation")
		public ReportEntryBuilder submit()
		{
			if (isFatal)
			{
				hadFatalError = true;
				Console.resetColor();
				Thread.currentThread().stop();
			}

			if (!shouldPrint)
				return this;

			for (int i = 0; i < colorSwitches; i++)
				Console.endColor();

			// make sure that Java really puts that stuff into the console LOL.
			System.out.flush();
			return this;
		}

		/**
		 * Print a filename in default console color, followed by a newLine().
		 */
		public ReportEntryBuilder visualizeFile(File file)
		{
			if (!shouldPrint)
				return this;

			String fullName = file.getName();
			try
			{
				fullName = file.getCanonicalPath();
			}
			catch (IOException e)
			{
			}

			return switchColor(Color.Default).print("File \"" + fullName + "\":").newLine();
		}

		/**
		 * Visualize a location, followed by a newLine().
		 */
		public ReportEntryBuilder visualizeLocation(Location loc)
		{
			visualizeLocation(null, loc, loc.getOffset(Location.BEGIN), loc.getOffset(Location.END));
			return this;
		}

		/**
		 * Visualize an AST node, followed by a newLine().
		 */
		public ReportEntryBuilder visualizeASTNode(ASTNode node)
		{
			return visualizeASTNode(null, node);
		}

		/**
		 * Visualize an AST node, followed by a newLine(). In addition a short
		 * description is printed in front of the line information which can
		 * give more insight to what node is displayed.
		 */
		public ReportEntryBuilder visualizeASTNode(String description, ASTNode node)
		{
			if (!shouldPrint)
				return this;

			this.switchColor(Color.Default);
			begin(ReportVerbosity.Verbose).print("=> " + node.getClass().getName() + ":").newLine().submit();

			visualizeFile(node.getLocation().getFile());
			return visualizeLocation(description, node.getLocation(), node.getLeftmostOffset(), node.getRightmostOffset());
		}

		private ReportEntryBuilder visualizeLocation(String description, Location loc, int leftmostOffset, int rightmostOffset)
		{
			if (!shouldPrint)
				return this;

			/*
			 * The hard part here is to determine the range of this node in the
			 * source file and especially to compress the source code range in
			 * case it spans across multiple lines, exceeding console width.
			 */

			Location nodeLoc = loc;
			String src = nodeLoc.getSourceCode();

			/*
			 * Determine source code location of pivot character! If you have an
			 * expression "a + b" and "node" is the plus operator, then plus is
			 * the pivot character. In general the getLocation() of an
			 * expression is a range of characters and the happy medium will be
			 * selected as pivot.
			 * 
			 * The pivot is the "^" in our underlining, like
			 * 
			 * Line 2> 4 + 23 * 45 + 7 - 34 ~~~~~~~~^~~
			 * 
			 * So the pivot helps to identify the precise semantic node that
			 * caused trouble. If it were only underlined without the pivot,
			 * then it would be unclear whether the plus or minus operator
			 * caused trouble. Even though in that case one could infer it by
			 * precedence rules but this is not what error messages are about!
			 */

			int pivot = (nodeLoc.getOffset(Location.PIVOT_START) + nodeLoc.getOffset(Location.PIVOT_END)) / 2;

			/*
			 * Determining where a node starts and ends within source code is
			 * not trivial. There is some code involved behind the scenes but
			 * the abstract idea is to recursively determine the left-most and
			 * right-most child within the node's tree. Right/left-most is meant
			 * in terms of source code position. Each node has a location from
			 * the parser which contains the preceding symbol and the end
			 * positions for both, the preceding and the node's symbol in source
			 * code.
			 * 
			 * The resulting "span" of a node's tree is what we want to
			 * underline in the source code for the user...
			 */
			int left = leftmostOffset;
			int right = rightmostOffset + 1;

			pivot = Math.max(0, pivot - left);

			/*
			 * The span alone could now stretch over multiple lines and be too
			 * short (in which case we want to append some surrounding code) or
			 * far too long (so we want to compress it and insert "..."
			 * characters). So now we just generate a suitable output string
			 * that can be written to the console and keeps track of where to
			 * show the pivot as well as where our original span starts and
			 * ends.
			 */
			ArrayList<String> output = beautifyString(src.substring(left, right), false, pivot);
			int startDelta = 0;
			int endDelta = 0;
			int len = flattenString(output).length();
			String finalLine = "";

			if (len < Console.getWidth() - 10)
			{
				int fillLen = Console.getWidth() - len;

				// gather beautified source code pieces to put in front and
				// after our underlined statement
				String lext = flattenString(beautifyString(src.substring(Math.max(0, left - 100), left), true, -1));
				String rext = flattenString(beautifyString(src.substring(right, Math.min(src.length(), right + 100)), false, -1));

				// select only as much as we need to fill the console line
				lext = lext.substring(Math.max(0, lext.length() - fillLen / 2));
				fillLen -= fillLen / 2;
				rext = rext.substring(0, Math.min(rext.length(), fillLen));

				// add to output and save deltas for further processing
				output.add(0, lext);
				output.add(rext);

				startDelta = output.get(0).length();
				endDelta = output.get(output.size() - 1).length();

				// convert pieces to string
				for (String s : output)
				{
					if (s != null)
						finalLine += s;
					else
						pivot = finalLine.length() - 1;
				}
			}
			else
			{
				int fillLen = Console.getWidth() - 10;

				// don't compress near pivot
				for (pivot = 0; pivot < output.size(); pivot++)
				{
					if (output.get(pivot) == null)
						break;
				}

				// compress right to pivot
				String lext = flattenString(output, 0, pivot);
				String rext = flattenString(output, pivot + 1, output.size());

				if (lext.length() > fillLen / 2)
				{
					// compress left part
					lext = lext.substring(0, fillLen / 4) + " ... " + lext.substring(lext.length() - fillLen / 4);
				}

				if (rext.length() > fillLen / 2)
				{
					// compress right part
					rext = rext.substring(0, fillLen / 4) + " ... " + rext.substring(rext.length() - fillLen / 4);
				}

				finalLine = lext + rext;
				pivot = lext.length() - 1;
			}

			// finally print line
			if (description != null)
				description = "[" + description + "] in line ";
			else
				description = "Line ";

			this.switchColor(Color.Default);

			if (nodeLoc.getLineNumberForOffset(left) != nodeLoc.getLineNumberForOffset(right))
				this.print(description + nodeLoc.getLineNumberForOffset(left) + " to " + nodeLoc.getLineNumberForOffset(right) + " >");
			else
				this.print(description + nodeLoc.getLineNumberForOffset(left) + " >");

			String prefix = genChars(' ', 5);
			this.switchColor(Color.Yellow).newLine(prefix + finalLine);

			// print underlining and pivot
			this.newLine(prefix + genChars(' ', startDelta));
			this.switchColor(Color.Green).print(genChars('~', pivot - startDelta));
			this.switchColor(Color.Red).print("^");
			this.switchColor(Color.Green).print(genChars('~', (finalLine.length() - endDelta - pivot - 1)));
			this.newLine();

			return this;
		}

		private String flattenString(ArrayList<String> array)
		{
			return flattenString(array, 0, array.size());
		}

		private String flattenString(ArrayList<String> array, int begin, int end)
		{
			String res = "";
			for (int i = begin; i < end; i++)
			{
				String s = array.get(i);
				if (s != null)
					res += s;
			}
			return res;
		}

		private ArrayList<String> beautifyString(String srcSpan, boolean reverse, int pivot)
		{
			ArrayList<String> output = new ArrayList<String>();
			boolean wasSpace = false;
			int len = 0;

			for (int i = reverse ? srcSpan.length() - 1 : 0, l = srcSpan.length(); reverse ? i >= 0 : i < l; i = i + (reverse ? -1 : 1))
			{
				char c = srcSpan.charAt(i);
				int where = reverse ? 0 : output.size();

				if (Character.isWhitespace(c) || (c <= ' '))
				{
					// dont really care about also applying this to strings
					// defined in code, since this is only for report messages
					if (c == '\n')
					{
						output.add(where, "\\n ");
						len += 3;
					}

					if (!wasSpace)
					{
						wasSpace = true;
						output.add(where, " ");
						len++;
					}
				}
				else
				{
					wasSpace = false;
					output.add(where, "" + c);
					len++;
				}

				if (pivot == i)
					output.add(null);
			}

			return output;
		}

		private String genChars(char c, int count)
		{
			String res = "";
			while (count > 0)
			{
				res += c;
				count--;
			}
			return res;
		}

		/**
		 * Print an exception stack-trace.
		 */
		public ReportEntryBuilder printStackTrace(Exception e)
		{
			if (!shouldPrint)
				return this;
			e.printStackTrace();
			return this;
		}
	}
}
