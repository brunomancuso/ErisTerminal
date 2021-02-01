package org.threewaves.eris.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ShellExec {
	private int exitCode;
	private boolean readOutput, readError;
	private StreamGobbler errorGobbler, outputGobbler;
	private Process process;
	private boolean stdOut;

	public ShellExec() {
		this(false, false, false);
	}

	public ShellExec(boolean stdOut, boolean readOutput, boolean readError) {
		this.readOutput = readOutput;
		this.readError = readError;
		this.stdOut = stdOut;
	}

	/**
	 * Execute a command.
	 * 
	 * @param command command ("c:/some/folder/script.bat" or
	 *                "some/folder/script.sh")
	 * @param workdir working directory or NULL to use command folder
	 * @param wait    wait for process to end
	 * @param args    0..n command line arguments
	 * @return process exit code
	 */

	public int execute(String command, String workdir, boolean wait, String... args) throws IOException {
		List<String> ls = new ArrayList<>();
		for (String s : args) {
			if (!s.trim().isEmpty()) {
				ls.add(s.trim());
			}
		}
		return execute(command, workdir, wait, ls);
	}

	public int execute(String command, String workdir, boolean wait, List<String> ls) throws IOException {
		String[] cmdArr = new String[ls.size() + 1];
		int index = 1;
		cmdArr[0] = command;
		for (String string : ls) {
			cmdArr[index++] = string;
		}
		ProcessBuilder pb = new ProcessBuilder(cmdArr);
		File workingDir = (workdir == null ? new File(command).getParentFile() : new File(workdir));
		pb.directory(workingDir);

		process = pb.start();

		// Consume streams, older jvm's had a memory leak if streams were not read,
		// some other jvm+OS combinations may block unless streams are consumed.
		errorGobbler = new StreamGobbler(process.getErrorStream(), readError, stdOut);
		outputGobbler = new StreamGobbler(process.getInputStream(), readOutput, stdOut);
		if (readError)
			errorGobbler.start();
		if (readOutput)
			outputGobbler.start();

		exitCode = 0;
		if (wait) {
			try {
				process.waitFor();
				exitCode = process.exitValue();
				outputGobbler.join();
				errorGobbler.join();
			} catch (InterruptedException ex) {
				// no log
			}
		}
		return exitCode;
	}

	public int getExitCode() {
		return exitCode;
	}

	public boolean isOutputCompleted() {
		return (outputGobbler != null ? outputGobbler.isCompleted() : false);
	}

	public boolean isErrorCompleted() {
		return (errorGobbler != null ? errorGobbler.isCompleted() : false);
	}

	public String getOutput() {
		return (outputGobbler != null ? outputGobbler.getOutput() : null);
	}

	public String getError() {
		return (errorGobbler != null ? errorGobbler.getOutput() : null);
	}

	private static class StreamGobbler extends Thread {
		private InputStream is;
		private StringBuilder output;
		private volatile boolean completed; // mark volatile to guarantee a thread safety
		private boolean stdOut;

		public StreamGobbler(InputStream is, boolean readStream, boolean stdOut) {
			this.is = is;
			this.stdOut = stdOut;
			this.output = (readStream ? new StringBuilder(256) : null);
		}

		@Override
		public void run() {
			completed = false;
			try {
				String NL = System.getProperty("line.separator", "\r\n");
				Charset encoding = Charset.defaultCharset();
				InputStreamReader isr = new InputStreamReader(is, encoding);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					if (output != null) {
						output.append(line + NL);
					}
					if (stdOut) {
						System.out.println(line);
					}
				}
			} catch (IOException ex) {
				// no log
			}
			completed = true;
		}

		/**
		 * Get inputstream buffer or null if stream was not consumed.
		 * 
		 * @return
		 */
		public String getOutput() {
			return (output != null ? output.toString() : null);
		}

		/**
		 * Is input stream completed.
		 * 
		 * @return
		 */
		public boolean isCompleted() {
			return completed;
		}

	}

	public Process getProcess() {
		return process;
	}

}