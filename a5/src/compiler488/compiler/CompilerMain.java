package compiler488.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.EnumSet;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.builder.SwitchBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;

import compiler488.ast.ASTGraphPrinter;
import compiler488.ast.ASTPrinter488;
import compiler488.ast.ASTPrinterCPP;
import compiler488.ast.stmt.Scope;
import compiler488.codegen.CodeGenASTVisitor;
import compiler488.compiler.Console.Color;
import compiler488.parser.Lexer;
import compiler488.parser.Location;
import compiler488.parser.Report;
import compiler488.runtime.Machine;
import compiler488.semantics.Semantics;
import compiler488.testing.TestSuite;

public class CompilerMain
{
	private static Group options = null;

	private static Option outFileFormat;
	private static Option outputOpt;
	private static Option inputOpt;
	private static Option whatToTrace;
	private static Option verboseSwitch;
	private static Option logLevelOpt;
	private static Option helpSwitch;
	private static Option testSuiteOpt;
	private static Option machineExecOpt;
	private static Option cppCompilerOpt;
	private static Option cppCompilerExecOpt;
	private static Option cppEnableStdLibOpt;
	private static Option cppCompilerValidateOpt;

	private static OutputFileFormat outputFormat = OutputFileFormat.None;
	private static FileContainer outputFile;
	private static FileContainer inputFile;
	private static EnumSet<TraceModule> tracedModules = EnumSet.noneOf(TraceModule.class);

	private static Scope programAST;
	private static boolean machineExec;
	private static CppCompiler cppCompiler;
	private static boolean cppRunExecutable;
	private static final StringBuilder machineOutput = new StringBuilder();
	private static boolean runCppValidator;

	public static boolean isWindows()
	{
		return System.getProperty("os.name").startsWith("Windows");
	}

	public static File createTempFile(String suffix)
	{
		File tmp;
		try
		{
			tmp = File.createTempFile("compiler488_", suffix);
			tmp.deleteOnExit();
		}
		catch (IOException e)
		{
			Report.fatalError("Unable to create temporary file.", e);
			return null; // we will never get here...
		}
		return tmp;
	}

	public static void reset()
	{
		// all classes with static variables need to provide a reset function
		// so that main() is reusable without having to spawn additional
		// processes...
		Report.reset();

		// reset CompilerMain
		options = null;

		outFileFormat = null;
		outputOpt = null;
		inputOpt = null;
		whatToTrace = null;
		verboseSwitch = null;
		logLevelOpt = null;
		helpSwitch = null;
		testSuiteOpt = null;
		machineExecOpt = null;
		cppCompilerOpt = null;
		cppEnableStdLibOpt = null;
		cppCompilerExecOpt = null;
		cppCompilerValidateOpt = null;

		outputFormat = OutputFileFormat.None;
		outputFile = null;
		inputFile = null;
		tracedModules = EnumSet.noneOf(TraceModule.class);

		runCppValidator = false;
		cppRunExecutable = false;
		programAST = null;
		machineExec = true;
		cppCompiler = null;
	}

	public static void main(final String args[])
	{
		reset();

		/**
		 * The thread thing is a little hacky. The compiler behaves
		 * single-threaded. BUT, it makes fatal error a lot easier to handle, if
		 * we can just quit the current thread of execution. Throwing an
		 * exception is usually not a good option since it may get caught. In
		 * combination with having global state reset, this allows for
		 * re-entrence of main(), thus allowing the compiler to invoke itself
		 * without having to start additional processes. This mechanism is used
		 * to implement the test-suite.
		 */
		Thread t = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					CommandLine cl = parseCommandLine(args);
					analyzeCommandLine(cl);
				}
				catch (Exception e)
				{
					Report.fatalError("Internal compiler error.", e);
				}
				finally
				{
					Console.resetColor();
					System.out.flush();
				}
			}
		});

		t.start();

		while (true)
		{
			try
			{
				t.join();
				break;
			}
			catch (InterruptedException e)
			{
			}
		}
	}

	private static void analyzeCommandLine(CommandLine cl)
	{
		// process first because it can disable error reporting
		// and command line analysis may otherwise print unwanted stuff!
		if (cl.hasOption(logLevelOpt))
		{
			String ll = (String) cl.getValue(logLevelOpt);

			try
			{
				ReportVerbosity level = ReportVerbosity.valueOf(Character.toUpperCase(ll.charAt(0)) + ll.toLowerCase().substring(1));
				Report.setVerbosity(level);
			}
			catch (Exception e)
			{
				Report.fatalError("Invalid log-level \"" + ll + "\".");
				return;
			}
		}

		if (cl.hasOption(verboseSwitch))
			Report.setVerbosity(ReportVerbosity.Verbose);

		if (cl.hasOption(outputOpt))
		{
			outputFile = new FileContainer("Output file", (String) cl.getValue(outputOpt));
			outputFile.setCanOverwrite(true);

			String strFmt = ((String) cl.getValue(outFileFormat, "488")).toLowerCase();

			if (strFmt.compareTo("488") == 0)
				outputFormat = OutputFileFormat._488;
			else if (strFmt.compareTo("cpp") == 0)
				outputFormat = OutputFileFormat.CPP;
			else if (strFmt.compareTo("x86") == 0)
				outputFormat = OutputFileFormat.X86;
			else if (strFmt.compareTo("dot") == 0)
				outputFormat = OutputFileFormat.Graph;
			else
				Report.fatalError("Invalid output file format \"" + strFmt + "\".");
		}

		if (cl.hasOption(whatToTrace))
		{

		}

		if (cl.hasOption(machineExecOpt))
		{
			machineExec = false;
		}

		if (cl.hasOption(cppCompilerOpt) || (outputFormat == OutputFileFormat.X86))
		{
			cppCompiler = new CppCompiler((String) cl.getValue(cppCompilerOpt, isWindows() ? "run-icl.bat" : "g++"));

			if (outputFormat == OutputFileFormat.X86)
				cppCompiler.setOutputFile(outputFile.getName());

			if (cl.hasOption(cppCompilerExecOpt))
				cppRunExecutable = true;

			if (cl.hasOption(cppCompilerValidateOpt))
				runCppValidator = true;

			cppRunExecutable |= runCppValidator;
		}

		// has to be processed last since it starts further compilation stages
		if (cl.hasOption(testSuiteOpt))
		{
			// gather files for test-suite run
			String dir = (String) cl.getValue(inputOpt);
			String cmdLine = ((String) cl.getValue(testSuiteOpt, "@-ll=quiet")).substring(1);

			TestSuite.run(dir, cmdLine);
		}
		else
		{
			// usual compilation
			inputFile = new FileContainer("Compilation unit", (String) cl.getValue(inputOpt));

			runParser();
			runSemanticChecks();
			runFileGenerator();
			runCodeGenerator();
			executeGeneratedCode();

		}
	}

	private static void runParser()
	{
		Report.ReportEntryBuilder act = Report.beginAction(ReportVerbosity.Verbose,
				"Parsing input file...");

		Location.setCompilationUnit(inputFile);
		compiler488.parser.Parser p = new compiler488.parser.Parser(new Lexer(inputFile.getRequiredReader()));

		try
		{
			if (tracedModules.contains(TraceModule.Parser))
				programAST = (Scope) p.debug_parse().value;
			else
				programAST = (Scope) p.parse().value;

			programAST.__initialize_Internal();
		}
		catch (Exception e)
		{
			Report.fatalError("Internal parser error.", e);
		}

		act.success();
	}

	private static void runSemanticChecks()
	{
		Report.ReportEntryBuilder act = Report.beginAction(ReportVerbosity.Verbose,
				"Running semantic analysis...");

		try
		{
			Semantics check = new Semantics(programAST);
			check.run();

			if (check.hasErrors())
			{
				// following compilation stages require valid semantics, so we
				// can't continue!
				Report.beginFatalError()
						.newLine()
						.newLine("[FATAL-ERROR]: Input file contains semantic errors!")
						.newLine()
						.submit();
			}
		}
		catch (Exception e)
		{
			Report.fatalError("Internal error during semantic analysis.", e);
		}

		act.success();
	}

	private static void runFileGenerator()
	{
		switch (outputFormat)
		{
		case _488:
		case CPP:
		case Graph:
			break;
		default:
			return;
		}

		Report.ReportEntryBuilder act = Report.beginAction(ReportVerbosity.Normal,
				"Generating output file \"" + outputFile.getCanonicalName() + "\"...");

		try
		{
			PrintStream stream = null;

			switch (outputFormat)
			{
			case _488:
			{
				ASTPrinter488 printer = new ASTPrinter488(programAST);
				printer.setPrintStream(stream = outputFile.getPrintStream());
				printer.print();
			}
				break;

			case CPP:
			{
				ASTPrinterCPP printer = new ASTPrinterCPP(programAST);
				printer.setPrintStream(stream = outputFile.getPrintStream());
				printer.print();
			}
				break;

			case Graph:
			{
				ASTGraphPrinter printer = new ASTGraphPrinter(programAST,
						stream = outputFile.getPrintStream());
				printer.print();
			}
				break;
			}

			if (stream != null)
			{
				stream.flush();
				stream.close();
			}
		}
		catch (Exception e)
		{
			Report.fatalError("Internal error during output-file generation.", e);
		}

		act.success();
	}

	private static void runCodeGenerator()
	{
		if (cppCompiler != null)
		{
			Report.ReportEntryBuilder act = Report.beginAction(ReportVerbosity.Normal,
					"Generating executable file...");

			try
			{
				PrintStream stream = null;

				File tmpFile = createTempFile(".cpp");
				ASTPrinterCPP printer = new ASTPrinterCPP(programAST);

				printer.setPrintStream(stream = new PrintStream(tmpFile));
				printer.print();
				stream.flush();
				stream.close();

				if (cppCompiler.getOutputFile() == null)
					cppCompiler.setOutputFile(createTempFile(".exe"));

				cppCompiler.compile(tmpFile);
			}
			catch (Exception e)
			{
				Report.fatalError("Unexpected error during code generation!", e);
			}

			act.success();
		}

		if (machineExec)
		{
			Report.ReportEntryBuilder act =
					Report.beginAction(
							ReportVerbosity.Normal,
							"Generating and executing machine code...");

			try
			{
				machineOutput.setLength(0);

				Machine.powerOn();
				CodeGenASTVisitor gen = new CodeGenASTVisitor(programAST);
				gen.run();
                gen.dump();
				Machine.run();
			}
			catch (Exception e)
			{
				Report.fatalError(
						"Unexpected error during machine code generation.", e);
			}

			act.success();
		}
	}

	public static void onMachineOutput(String str)
	{
		if (!runCppValidator)
			Report.begin(ReportVerbosity.Normal).print(str).submit();

		machineOutput.append(str);
	}

	public static void waitFor(Process p)
	{
		while (true)
		{
			try
			{
				p.exitValue();
				break;
			}
			catch (IllegalThreadStateException e)
			{
				try
				{
					Thread.sleep(500);
				}
				catch (InterruptedException e1)
				{
				}
			}
		}
	}

	private static String escapeCharacter(char c)
	{
		if ((c < 32) || (c > 127))
			return "\\0x" + Integer.toString((int) c, 16).toUpperCase();
		else
			return Character.toString(c);
	}

	private static void executeGeneratedCode()
	{
		if ((cppCompiler != null) && ((outputFormat != OutputFileFormat.X86) || cppRunExecutable))
		{
			try
			{
				org.apache.commons.exec.CommandLine cmdLine = org.apache.commons.exec.CommandLine.parse("'" + cppCompiler.getOutputFile().getAbsolutePath() + "'");

				DefaultExecutor executor = new DefaultExecutor();
				ByteArrayOutputStream cppExecOutput = new ByteArrayOutputStream();

				executor.setExitValue(0);

				if (runCppValidator)
					executor.setStreamHandler(new PumpStreamHandler(cppExecOutput, System.err, System.in));
				else
					executor.setStreamHandler(new PumpStreamHandler(System.out, System.err, System.in));

				Report.beginInfo(ReportVerbosity.Normal).print("Running generated code:").newLine().submit();
				Console.resetColor();

				try
				{
					executor.execute(cmdLine);
				}
				catch (ExecuteException e)
				{
					Report.fatalError("Executed code failed with non-zero exit code.");
				}

				if (runCppValidator)
				{
					// print stdout of executed code
					String output_cpp = cppExecOutput.toString("utf8");
					String output_488 = machineOutput.toString();

					// compare executions
					int minLen = Math.min(output_cpp.length(), output_488.length());
					for (int i = 0; i < minLen; i++)
					{
						char a = output_cpp.charAt(i);
						char b = output_488.charAt(i);

						if (a == b)
							Report.beginInfo(ReportVerbosity.Normal).print(Character.toString(a)).submit();
						else
						{
							Report.beginFatalError().newLine("[ERROR]: Execution starts to diverge! C++ character is '" + escapeCharacter(a) + "', 488 character is '" + escapeCharacter(b) + "'.").newLine().submit();
							return;
						}
					}

					if (output_cpp.length() != output_488.length())
					{
						Report.beginFatalError().newLine("[ERROR]: Execution starts to diverge! No mismatching characters, but C++ output length is '"
								+ output_cpp.length() + "', while 488 output length is '" + output_488.length() + "'.").newLine().submit();
						return;
					}

					Report.begin(ReportVerbosity.Normal).newLine("[INFO]: C++ and 488 execution are identical!").newLine().submit();
				}
			}
			catch (Exception e)
			{
				Report.fatalError("Unexpected error during code execution!", e);
			}
		}
	}

	private static Group getCommandLineOptions()
	{
		if (options != null)
			return options;

		// !! using the Apache Command Line Parser:
		// http://commons.apache.org/sandbox/cli2/manual/index.html

		DefaultOptionBuilder defaultOpt = new DefaultOptionBuilder();
		GroupBuilder groupOpt = new GroupBuilder();
		ArgumentBuilder argOpt = new ArgumentBuilder();
		SwitchBuilder switchOpt = new SwitchBuilder();

		outFileFormat = defaultOpt
				.withDescription("FORMAT=<488|CPP|X86|DOT> defaults to 488, which outputs equivalent, compilable 488 code. Another valid format is" +
						" CPP, which outputs compilable C++ code! If you have a compatible C++ compiler, you may specify X86 as FORMAT to generate an executable file.")
				.withShortName("f")
				.withLongName("format")
				.withArgument(argOpt
						.withName("FORMAT")
						.withMinimum(1)
						.withMaximum(1)
						.withInitialSeparator('=')
						.create())
				.create();

		outputOpt = defaultOpt
				.withArgument(argOpt
						.withName("FILE")
						.withMinimum(1)
						.withMaximum(1)
						.withInitialSeparator('=')
						.create())
				.withShortName("o")
				.withLongName("output")
				.withChildren(groupOpt.withOption(outFileFormat).create())
				.withDescription("Instead of compiling and execution the input file, there is also the option to transform the input into a supported" +
						" output format, usually an equivalent representation of the input file. The FILE argument is required and denotes the ouput file.")
				.create();

		testSuiteOpt = defaultOpt
				.withArgument(argOpt
						.withName("FWDCMDLINE")
						.withMinimum(0)
						.withMaximum(1)
						.withInitialSeparator('=')
						.create())
				.withLongName("test-suite")
				.withDescription("Do a test-suite run. The compiler fetches files in all \"fail\" and \"pass\" " +
						"sub-directories inside the directory given by the FILE argument of the input option \"--input (-i)\" " +
						"and checks if their compilation passes and fails accordingly. " +
						"It also outputs the results in a nice tree-like table. FWDCMDLINE will be forwarded to all" +
						"subsequent compiler invocationd uring the test-run. Multiple commands in FWDCMDLINE must be" +
						"wrapped into parentheses and the first command always has to start with a @ character, like \"@-v\".")
				.create();

		cppCompilerExecOpt = defaultOpt
				.withDescription("Execute generated binary code.")
				.withLongName("exec")
				.create();

		cppCompilerValidateOpt = defaultOpt
				.withDescription("Compare 488 machine code execution with C++ execution (binary stdout comparison).")
				.withLongName("validate")
				.create();

		cppEnableStdLibOpt = defaultOpt
				.withDescription("Enable a standard library for 488 programs. Later, these might be also available for normal 488 code generation.")
				.withLongName("std-lib")
				.create();

		machineExecOpt = defaultOpt
				.withShortName("X")
				.withLongName("suppress-execution")
				.withDescription("Suppresses Execution")
				.create();

		cppCompilerOpt = defaultOpt
				.withArgument(argOpt
						.withName("CPPEXE")
						.withMinimum(0)
						.withMaximum(1)
						.withInitialSeparator('=')
						.create())
				.withChildren(groupOpt
						.withOption(cppEnableStdLibOpt)
						.withOption(cppCompilerExecOpt)
						.withOption(cppCompilerValidateOpt)
						.create())
				.withShortName("cpp")
				.withLongName("cpp-executable")
				.withDescription("Automatic compilation of the input file to binary code is only supported if there is a compatible " +
						"c++ compiler available. On windows systems, \"run-icl.bat\" is attempted and on linux \"g++\". But if that " +
						"does not work, you need to specify the full path manually in CPPEXE.")
				.create();

		inputOpt = defaultOpt
				.withArgument(argOpt
						.withName("FILE")
						.withMinimum(1)
						.withMaximum(1)
						.withInitialSeparator('=')
						.create())
				.withShortName("i")
				.withLongName("input")
				.withChildren(groupOpt.withOption(testSuiteOpt).create())
				.withRequired(true)
				.withDescription("A 488 source code file to be processed.")
				.create();

		verboseSwitch = defaultOpt
				.withDescription("Shortcut for \"--log-level verbose\".")
				.withShortName("v")
				.withLongName("verbose")
				.create();

		helpSwitch = defaultOpt
				.withDescription("Shows this manual.")
				.withShortName("h")
				.withLongName("help")
				.create();

		Group traceGroup = groupOpt
				.withMinimum(1)
				.withRequired(true)
				.withOption(defaultOpt
						.withLongName("file")
						.withDescription("Instead of using stdout, write the trace into a FILE.")
						.create())
				.create();

		whatToTrace = defaultOpt
				.withDescription("MODULES=<parser|execution>: Some compiler stages support tracing, which you can enable and configure through this group. " +
						"You may enable tracing of multiple stages by stringing them together, separated by a comma.")
				.withShortName("t")
				.withLongName("trace")
				.withArgument(argOpt
						.withName("MODULES")
						.withMaximum(1)
						.withMinimum(1)
						.withInitialSeparator('=')
						.create())
				.withChildren(traceGroup)
				.create();

		logLevelOpt = defaultOpt
				.withDescription("VERBOSITY=<verbose|normal|warning|error|quiet> defaults to \"normal\" and is suitable for most applications.")
				.withShortName("ll")
				.withLongName("log-level")
				.withArgument(argOpt
						.withName("VERBOSITY")
						.withMaximum(1)
						.withMinimum(1)
						.withInitialSeparator('=')
						.create())
				.create();

		return CompilerMain.options = groupOpt
				.withName("options")
				.withOption(outputOpt)
				.withOption(inputOpt)
				.withOption(whatToTrace)
				.withOption(verboseSwitch)
				.withOption(logLevelOpt)
				.withOption(helpSwitch)
				.withOption(machineExecOpt)
				.withOption(cppCompilerOpt)
				.create();
	}

	private static CommandLine parseCommandLine(String args[])
	{
		CommandLine cl;

		Console.beginColor(Color.Yellow);
		{
			if (args.length == 1)
			{
				if ((args[0].compareToIgnoreCase("--help") != 0) && (args[0].compareToIgnoreCase("-h") != 0))
				{
					// shortcut for processing a file...
					String file = args[0];
					args = new String[] { "-i", file };
				}
			}

			Parser p = new Parser();
			p.setGroup(getCommandLineOptions());
			p.setHelpFormatter(new HelpFormatter());
			p.setHelpOption(helpSwitch);
			cl = p.parseAndHelp(args);
		}
		Console.endColor();

		if (cl == null)
		{
			Console.resetColor();
			Thread.currentThread().stop();
		}

		return cl;
	}
}
