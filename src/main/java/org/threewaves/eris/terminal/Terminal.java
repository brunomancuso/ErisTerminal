package org.threewaves.eris.terminal;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.SystemUtils;
import org.threewaves.eris.engine.CommandConsole;
import org.threewaves.eris.engine.Config;
import org.threewaves.eris.engine.ConfigException;
import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.terminal.GenericAppender.IAppender;
import org.threewaves.eris.terminal.GenericAppender.Type;
import org.threewaves.eris.terminal.commands.CommandFactory;

public class Terminal {
	private static final String CONSOLE_NAME = "Eris Terminal";

	private final ComponentOutputStream out;
	private final ComponentOutputStream error;
	private final ComponentOutputStream notification;
	private final JFrame frame;

	private final JConsole console;
	private final ExecutionQueue executionQueue;
	
	public Terminal(String[] args) throws ConfigException, UnsupportedEncodingException {
		Config config = Config.create();
		TerminalHistory history = TerminalHistory.load();
		Engine engine = new Engine(config.createFactory(), config.createTestSuit());
		frame = new JFrame(CONSOLE_NAME + " - " + workingDirectory() + (isAdmin() ? " - Administrator" : ""));
		Map<String, ICommand> commands = CommandFactory.create(config, engine, () -> {
			history.resize(frame.getWidth(), frame.getHeight());
			history.moved(frame.getX(), frame.getY());
			history.save();
			frame.setVisible(false);
			frame.dispose();
		}, () -> {
			clearStreams();
		});
		engine.getModules().refresh();
		URL iconURL = Terminal.class.getResource("/terminal.png");
		ImageIcon icon = new ImageIcon(iconURL);
		frame.setIconImage(icon.getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		console = new JConsole(config, history, commands.values(), engine.getModules());
		GenericAppender appender = new GenericAppender(new IAppender() {
			@Override
			public void append(Type type, String val) {
				if (type == Type.NORMAL) {
					console.append(JConsole.NORMAL_COLOR, val);
				}
				if (type == Type.ERROR) {
					console.append(JConsole.ERROR_COLOR, val);
				}
				if (type == Type.NOTIFICATION) {
					console.append(JConsole.NOTIFICATION_COLOR, val);
				}

			}

			@Override
			public void remove(int start, int end) {
				console.remove(start, end);
			}

			@Override
			public int size() {
				return console.size();
			}

		}, 1000);

		out = new ComponentOutputStream(appender, Type.NORMAL);
		error = new ComponentOutputStream(appender, Type.ERROR);
		notification = new ComponentOutputStream(appender, Type.NOTIFICATION);

		PrintStream outStream = new PrintStream(out, true, Charset.defaultCharset().name());
		PrintStream errorStream = new PrintStream(error, true, Charset.defaultCharset().name());
		PrintStream notificationStream = new PrintStream(notification, true, Charset.defaultCharset().name());

		CommandConsole defaultConsole = new CommandConsole(outStream, errorStream, notificationStream, (enable) -> {
			enableStreams(enable);
		});
		executionQueue = new ExecutionQueue(defaultConsole);
		executionQueue.start();
		console.addExecutionListener((c, o, onFinish) -> {
			ICommand cmd = commands.get(c);
			defaultConsole.stdout(c + " " + o);			
			executionQueue.execute(cmd, o, onFinish);
		});
		JScrollPane scroll = new JScrollPane( //
				console.getComponent(), //
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, //
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED //
		);
		frame.setLayout(new BorderLayout());
		frame.add(scroll);
		if (history.getWidth() > 0 && history.getHeight() > 0) {
			frame.setSize(history.getWidth(), history.getHeight());
		} else {
			frame.setSize(1440, 600);
		}
		if (history.getPosX() > 0 && history.getPosY() > 0) {
			frame.setLocation(history.getPosX(), history.getPosY());
		} else {
			frame.setLocationRelativeTo(null);
		}
		SwingUtilities.invokeLater(() -> {
			Thread.currentThread().setName("terminal");
		});
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				history.resize(frame.getWidth(), frame.getHeight());
				history.moved(frame.getX(), frame.getY());
				history.save();
				closeStreams();
			}
		});
		engine.initialize();
	}

	public void show() {
		frame.setVisible(true);
		// initialize engine
		System.out.println(" - " + Quote.random());
		console.newPrompt();

	}

	private void enableStreams(Boolean enable) {
		out.enable(enable);
		error.enable(enable);
		notification.enable(enable);
	}

	private void closeStreams() {
		out.close();
		error.close();
		notification.close();
	}

	private void clearStreams() {
		out.clear();
		error.clear();
		notification.clear();
	}

	public static String removeQuotes(String arg) { // Param: a quote.
		return arg.substring(1, arg.length() - 1);
	}

	public static boolean isAdmin() {
		try {
			if (SystemUtils.IS_OS_WINDOWS) {
				ProcessBuilder builder = new ProcessBuilder("net", "session").redirectError(Redirect.PIPE)
						.redirectOutput(Redirect.PIPE);
				Process process = builder.start();
				process.waitFor();
				int i = process.exitValue();
				return i == 0;
			}
		} catch (IOException | InterruptedException e) {
			return false;
		}
		return false;
	}

	static String workingDirectory() {
		String st = "";
		Path p = Paths.get(".").toAbsolutePath();
		if (p != null) {
			p = p.getParent();
		}
		if (p != null) {
			p = p.getParent();
		}
		if (p != null) {
			p = p.getFileName();
		}
		if (p != null) {
			st = p.toString();
		}
		return st;
	}

	public static void main(String[] args) throws UnsupportedEncodingException, ConfigException {
		Terminal terminal = new Terminal(args);
		terminal.show();
	}
}
