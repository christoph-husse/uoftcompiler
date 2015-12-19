package compiler488.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;

import compiler488.ast.ASTPrinterCPP;
import compiler488.parser.Report;

public class CppCompiler
{
	private String path;
	private File outputFile;

	public CppCompiler(String path)
	{
		this.path = path;

		test();
	}

	public void setOutputFile(File file)
	{
		this.outputFile = file;
	}

	public File getOutputFile()
	{
		return outputFile;
	}

	public void compile(File inputFile) throws IOException
	{
		Report.ReportEntryBuilder action = Report.beginAction(ReportVerbosity.Normal, "Compiling code with C++ compiler...");

		try
		{
			File tmpLog1 = CompilerMain.createTempFile(".log");
			File tmpLog2 = CompilerMain.createTempFile(".log");

			CommandLine cmdLine;
			if (CompilerMain.isWindows())
				cmdLine = CommandLine.parse("cmd.exe /c '" + path + "' '" + inputFile.getAbsolutePath() + "' '" + outputFile.getAbsolutePath() + "'");
			else
				cmdLine = CommandLine.parse("'" + path + "' -std=gnu++11 -o '" + outputFile.getAbsolutePath() + "' '" + inputFile.getAbsolutePath() + "'");

			DefaultExecutor executor = new DefaultExecutor();
			final OutputStream subErr = new FileOutputStream(tmpLog1);
			final OutputStream subOut = new FileOutputStream(tmpLog2);
			executor.setExitValue(0);
			executor.setStreamHandler(new PumpStreamHandler(subOut, subErr));

			try
			{
				executor.execute(cmdLine);
			}
			catch (ExecuteException e)
			{
				subErr.flush();
				subErr.close();
				subErr.flush();
				subOut.close();

				String errMsg = FileUtils.readFileToString(tmpLog2) + FileUtils.readFileToString(tmpLog1);
				Report.beginFatalError().newLine("[FATAL-ERROR]: C++ compilation failed. The following is a dump of the log-file:").newLine(errMsg).submit();
			}
		}
		catch (IOException e)
		{
			Report.fatalError("Unexpected error during C++ code compilation.", e);
		}

		action.success();
	}

	private void test()
	{
		Report.ReportEntryBuilder action = Report.beginAction(ReportVerbosity.Normal, "Checking for working C++11 compiler...");

		try
		{
			// write temporary test file that will succeed with any
			// compatible compiler...
			File tmpSrc = CompilerMain.createTempFile(".cpp");
			File tmpExe = CompilerMain.createTempFile(".exe");
			File tmpLog1 = CompilerMain.createTempFile(".log");
			File tmpLog2 = CompilerMain.createTempFile(".log");
			FileUtils.writeStringToFile(tmpSrc, ASTPrinterCPP.getCppHeaderString() + "int main(int argc, char** argv) { return 0; }");

			CommandLine cmdLine;
			if (CompilerMain.isWindows())
				cmdLine = CommandLine.parse("cmd.exe /c '" + path + "' '" + tmpSrc.getAbsolutePath() + "' '" + tmpExe.getAbsolutePath() + "'");
			else
				cmdLine = CommandLine.parse("'" + path + "' -std=gnu++11 -o '" + tmpExe.getAbsolutePath() + "' '" + tmpSrc.getAbsolutePath() + "'");

			DefaultExecutor executor = new DefaultExecutor();
			final OutputStream subErr = new FileOutputStream(tmpLog1);
			final OutputStream subOut = new FileOutputStream(tmpLog2);
			executor.setExitValue(0);
			executor.setStreamHandler(new PumpStreamHandler(subOut, subErr));

			try
			{
				executor.execute(cmdLine);
			}
			catch (ExecuteException e)
			{
				subErr.flush();
				subErr.close();
				subErr.flush();
				subOut.close();

				String errMsg = FileUtils.readFileToString(tmpLog2) + FileUtils.readFileToString(tmpLog1);
				Report.beginFatalError().newLine("[FATAL-ERROR]: C++ compiler is not working or does not support all the required C++11 features. The following is a dump of the log-file:").newLine(errMsg).submit();
			}
		}
		catch (IOException e)
		{
			Report.fatalError("C++ compiler \"" + path + "\" is not working.", e);
		}

		action.success();
	}
}
