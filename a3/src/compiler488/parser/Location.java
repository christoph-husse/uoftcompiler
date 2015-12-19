package compiler488.parser;

import java.io.File;
import java.util.ArrayList;

import java_cup.runtime.Symbol;

import compiler488.ast.ASTNode;
import compiler488.compiler.FileContainer;

public class Location
{

	private long begin = -1, end = -1, pivot_start = -1, pivot_end = -1;
	private final String sourceCode;
	private final File fileName;
	private final ArrayList<Integer> lineMap;

	private static String currentCompilationUnit;
	private static File currentFileName;
	private static ArrayList<Integer> currentLineMap;

	public static final int END = 0;
	public static final int PIVOT_START = 1;
	public static final int PIVOT_END = 2;
	public static final int BEGIN = 3;

	public static void setCompilationUnit(FileContainer unit)
	{
		currentFileName = unit.getName();
		currentCompilationUnit = unit.getRequiredContent();
		currentLineMap = new ArrayList<Integer>();

		currentLineMap.add(0);
		for (int i = 0; i < currentCompilationUnit.length(); i++)
		{
			if (currentCompilationUnit.charAt(i) == '\n')
				currentLineMap.add(i + 1);
		}
	}

	public static Location begin(Symbol start)
	{
		return begin(start.left, start.right);
	}

	public static Location begin(int line, int column)
	{
		Location loc = new Location();
		loc.begin = getSymbolCode(line, column);
		return loc;
	}

	public Location end(Symbol start)
	{
		end = getSymbolCode(start);
		return this;
	}

	public Location end(int line, int column)
	{
		end = getSymbolCode(line, column);
		return this;
	}

	public Location end(int line, int column, int rightShift)
	{
		end = getSymbolCode(line, column + rightShift);
		return this;
	}

	public Location pivot(Object range)
	{
		return pivot((ASTNode) range);
	}

	public Location extend(Location child)
	{
		if ((begin == -1) && (end == -1) && (pivot_start == -1) && (pivot_end == -1))
		{
			begin = child.begin;
			end = child.end;
			pivot_start = child.pivot_start;
			pivot_end = child.pivot_end;
		}
		else
		{
			begin = getCode(BEGIN);
			end = getCode(END);

			if (getCode(BEGIN) > child.getCode(BEGIN))
				begin = child.getCode(BEGIN);

			if (getCode(END) < child.getCode(END))
				end = child.getCode(END);
		}

		return this;
	}

	public Location pivotMiddle()
	{
		// clearing the pivot will cause location bounds to be used,
		// which has the desired effect of putting it in the middle...
		pivot_start = -1;
		pivot_end = -1;
		return this;
	}

	public Location pivot(ASTNode range)
	{
		Location l = range.getLocation();
		pivot_start = getSymbolCode(l.getLineNumber(PIVOT_START) - 1, l.getColumn(PIVOT_START) - 1);
		pivot_end = getSymbolCode(l.getLineNumber(PIVOT_END) - 1, l.getColumn(PIVOT_END) - 1);
		return this;
	}

	public Location pivot(Object start, int eline, int ecolumn)
	{
		return pivot((ASTNode) start, eline, ecolumn);
	}

	public Location pivot(ASTNode start, int eline, int ecolumn)
	{
		Location l = start.getLocation();
		pivot_start = getSymbolCode(l.getLineNumber(END) - 1, l.getColumn(END) - 1);
		pivot_end = getSymbolCode(eline, ecolumn);
		return this;
	}

	public Location pivot(int eline, int ecolumn)
	{
		pivot_end = getSymbolCode(eline, ecolumn);
		return this;
	}

	public Location pivot(int eline, int ecolumn, int rightShift)
	{
		pivot_end = getSymbolCode(eline, ecolumn + rightShift);
		return this;
	}

	public Location pivot(int sline, int scolumn, int eline, int ecolumn)
	{
		pivot_start = getSymbolCode(sline, scolumn);
		pivot_end = getSymbolCode(eline, ecolumn);
		return this;
	}

	private static long getSymbolCode(Symbol sym)
	{
		return getSymbolCode(sym.left, sym.right);
	}

	private static long getSymbolCode(int line, int column)
	{
		return ((long) line) << 32 | column;
	}

	public Location()
	{
		lineMap = currentLineMap;
		sourceCode = currentCompilationUnit;
		fileName = currentFileName;
	}

	public String getSourceCode()
	{
		return currentCompilationUnit;
	}

	public int getOffset(int ofWhat)
	{
		return Math.max(0, Math.min(lineMap.get(getLineNumber(ofWhat) - 1) + getColumn(ofWhat) - 1, sourceCode.length() - 1));
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("{begin:(" + getLineNumber(BEGIN) + ", " + getColumn(BEGIN) + "), ");
		builder.append("pivot_start:(" + getLineNumber(PIVOT_START) + ", " + getColumn(PIVOT_START) + "), ");
		builder.append("pivot_end:(" + getLineNumber(PIVOT_END) + ", " + getColumn(PIVOT_END) + "), ");
		builder.append("end:(" + getLineNumber(END) + ", " + getColumn(END) + ")}\n");

		int offset = getOffset(BEGIN);

		builder.append(sourceCode.substring(offset, getOffset(END) + 1).replace('\r', ' ').replace('\n', ' ').replace('\t', ' '));
		builder.append("\n");

		builder.append(genChars(' ', getOffset(PIVOT_START) - offset));
		builder.append("^");

		if (getOffset(PIVOT_START) > getOffset(PIVOT_END))
			throw new RuntimeException();

		if (getOffset(PIVOT_START) < getOffset(PIVOT_END))
		{
			builder.append(genChars('-', getOffset(PIVOT_END) - getOffset(PIVOT_START) - 1));
			builder.append("^");
		}
		else
			builder.append("{2}");

		return builder.toString();
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

	public File getFile()
	{
		return fileName;
	}

	public int getLineNumberForOffset(int offset)
	{
		for (int i = 0; i < lineMap.size(); i++)
		{
			if (lineMap.get(i) > offset)
				return i;
		}

		return lineMap.size();
	}

	public int getColumnForOffset(int offset)
	{
		for (int i = 0; i < lineMap.size(); i++)
		{
			if (lineMap.get(i) > offset)
				return offset - lineMap.get(i - 1) + 1;
		}

		return offset - lineMap.get(lineMap.size() - 1) + 1;
	}

	private long takeFirst(long a, long b, long c, long d)
	{
		if (a != -1)
			return a;
		if (b != -1)
			return b;
		if (c != -1)
			return c;
		if (d != -1)
			return d;
		throw new RuntimeException("Location not initialized properly. Needs at least one valid offset.");
	}

	private void setFirst(long a, long b, long c, long d, long newCode)
	{
		long entry;
		if (a != -1)
			entry = a;
		else if (b != -1)
			entry = b;
		else if (c != -1)
			entry = c;
		else if (d != -1)
			entry = d;
		else
			throw new RuntimeException("Location not initialized properly. Needs at least one valid offset.");

		if (entry == begin)
			begin = newCode;
		if (entry == pivot_start)
			pivot_start = newCode;
		if (entry == pivot_end)
			pivot_end = newCode;
		if (entry == end)
			end = newCode;
	}

	private long getCode(int ofWhat)
	{
		long code;
		switch (ofWhat)
		{
		case BEGIN:
			code = takeFirst(begin, pivot_start, pivot_end, end);
			break;
		case END:
			code = takeFirst(end, pivot_end, pivot_start, begin);
			break;
		case PIVOT_START:
			code = takeFirst(pivot_start, begin, pivot_end, end);
			break;
		case PIVOT_END:
			code = takeFirst(pivot_end, end, pivot_start, begin);
			break;
		default:
			throw new RuntimeException();
		}
		return code;
	}

	public int getLineNumber(int ofWhat)
	{
		return Math.max(1, Math.min((int) (getCode(ofWhat) >> 32) + 1, lineMap.size()));
	}

	public int getColumn(int ofWhat)
	{
		return Math.max(0, (int) (getCode(ofWhat) & 0xFFFFFFFF) + 1);
	}

	public String getLine()
	{
		int ln = getLineNumber(BEGIN);

		for (int i = 0, nl = 1; i < sourceCode.length(); i++)
		{
			if (nl >= ln)
			{
				int iEnd = i;
				for (; iEnd < sourceCode.length(); iEnd++)
				{
					if ((sourceCode.charAt(iEnd) == '\n') || (sourceCode.charAt(iEnd) == '\r'))
						break;
				}

				return sourceCode.substring(i, iEnd);
			}

			if (sourceCode.charAt(i) == '\n')
				nl++;
		}

		return "";
	}

	private void setCode(int ofWhat, long code)
	{
		switch (ofWhat)
		{
		case BEGIN:
			setFirst(begin, pivot_start, pivot_end, end, code);
			break;
		case END:
			setFirst(end, pivot_end, pivot_start, begin, code);
			break;
		case PIVOT_START:
			setFirst(pivot_start, begin, pivot_end, end, code);
			break;
		case PIVOT_END:
			setFirst(pivot_end, end, pivot_start, begin, code);
			break;
		default:
			throw new RuntimeException();
		}
	}

	private void setLineNumberAndColumnFromOffset(int ofWhat, int offset)
	{
		long code = getSymbolCode(getLineNumberForOffset(offset) - 1, getColumnForOffset(offset) - 1);

		setCode(ofWhat, code);
	}

	public void snapToNonWhitespaces()
	{

		int i;
		for (i = getOffset(BEGIN); (i < sourceCode.length()) && Character.isWhitespace(sourceCode.charAt(i)); i++);
		setLineNumberAndColumnFromOffset(BEGIN, i);
		for (i = getOffset(PIVOT_START); (i < sourceCode.length()) && Character.isWhitespace(sourceCode.charAt(i)); i++);
		setLineNumberAndColumnFromOffset(PIVOT_START, i);
		for (i = getOffset(PIVOT_END); (i < sourceCode.length()) && Character.isWhitespace(sourceCode.charAt(i)); i--);
		setLineNumberAndColumnFromOffset(PIVOT_END, i);
		for (i = getOffset(END); (i < sourceCode.length()) && Character.isWhitespace(sourceCode.charAt(i)); i--);
		setLineNumberAndColumnFromOffset(END, i);
	}
}
