package compiler488.testing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import compiler488.compiler.CompilerMain;
import compiler488.compiler.Console;
import compiler488.compiler.Console.Color;
import compiler488.compiler.ReportVerbosity;
import compiler488.parser.Report;

public class TestSuite
{
	public static void run(String dir, String cmdLine)
	{
		// gather testcases
		SortedMap<File, Boolean> filesToPass = new TreeMap<File, Boolean>();
		SortedMap<File, Boolean> filesToFail = new TreeMap<File, Boolean>();
		File absDir = new File(dir).getAbsoluteFile();
		int fileCount = 0;

		try
		{
			for (File f : FileUtils.listFiles(absDir, new String[] { "488" }, true))
			{
				fileCount++;
				String parent = f.getParentFile().getName().toLowerCase();

				if (parent.equals("pass"))
					filesToPass.put(f, false);
				else if (parent.equals("fail"))
					filesToFail.put(f, false);
			}
		}
		catch (Exception e)
		{
			Report.fatalError("Invalid test-suite directory \"" + dir + "\".");
			return;
		}

		Report.begin(ReportVerbosity.Normal)
				.switchColor(Color.Yellow)
				.print("Running test-suite on " + (filesToPass.size() + filesToFail.size()) + " out of " + fileCount + " files...")
				.newLine();

		// prepare command line
		List<String> caseCmdLine = new ArrayList<String>();
		String currentArg = "";

		for (int i = 0; i < cmdLine.length(); i++)
		{
			char c = cmdLine.charAt(i);

			if (c == '\'')
			{
				if (!currentArg.isEmpty())
					caseCmdLine.add(currentArg);
				currentArg = "";

				for (; i < cmdLine.length(); i++)
				{
					if (c == '\'')
						break;

					currentArg += c;
				}

				caseCmdLine.add(currentArg);
				currentArg = "";
			}
			else if (c == ' ')
			{
				if (!currentArg.isEmpty())
					caseCmdLine.add(currentArg);
				currentArg = "";
			}
			else
			{
				currentArg += c;
			}
		}

		if (!currentArg.isEmpty())
			caseCmdLine.add(currentArg);
		currentArg = "";

		// run passing tests
		ReportVerbosity oldVerbosity = Report.getVerbosity();

		for (File pass : filesToPass.keySet())
		{
			String[] passCmdLine = new String[2 + caseCmdLine.size()];
			passCmdLine[0] = "-i";
			passCmdLine[1] = pass.getAbsolutePath();
			for (int i = 0; i < caseCmdLine.size(); i++)
				passCmdLine[i + 2] = caseCmdLine.get(i);

			CompilerMain.main(passCmdLine);
			filesToPass.put(pass, !Report.hadFatalError());
		}

		// run failing tests
		for (File fail : filesToFail.keySet())
		{
			String[] passCmdLine = new String[2 + caseCmdLine.size()];
			passCmdLine[0] = "-i";
			passCmdLine[1] = fail.getAbsolutePath();
			for (int i = 0; i < caseCmdLine.size(); i++)
				passCmdLine[i + 2] = caseCmdLine.get(i);

			CompilerMain.main(passCmdLine);
			filesToFail.put(fail, Report.hadFatalError());
		}

		CompilerMain.reset();
		Report.setVerbosity(oldVerbosity);

		// format results
		Report.begin().switchColor(Color.Pruple).print("TEST-SUITE RESULTS:").newLine().newLine();

		dir = absDir.getPath();
		for (File pass : filesToPass.keySet())
		{
			String p = pass.getPath();
			boolean res = filesToPass.get(pass);

			Report.begin().switchColor(res ? Color.Green : Color.Red)
					.print(p.substring(dir.length()).replace(File.separator, " -> "))
					.print(res ? " [OK]" : " [FAILED]").newLine();
		}

		Console.writeLine();

		for (File pass : filesToFail.keySet())
		{
			String p = pass.getPath();
			boolean res = filesToFail.get(pass);

			Report.begin().switchColor(res ? Color.Green : Color.Red)
					.print(p.substring(dir.length()).replace(File.separator, " -> "))
					.print(res ? " [OK]" : " [FAILED]").newLine();
		}
	}
}
