package compiler488.testing;

import java.io.File;
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

		// run passing tests
		ReportVerbosity oldVerbosity = Report.getVerbosity();

		for (File pass : filesToPass.keySet())
		{
			CompilerMain.main(new String[] { "-i", pass.getAbsolutePath(), cmdLine });
			filesToPass.put(pass, !Report.hadFatalError());
		}

		// run failing tests
		for (File fail : filesToFail.keySet())
		{
			CompilerMain.main(new String[] { "-i", fail.getAbsolutePath(), cmdLine });
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
