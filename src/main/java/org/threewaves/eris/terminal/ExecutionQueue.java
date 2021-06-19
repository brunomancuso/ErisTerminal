package org.threewaves.eris.terminal;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;

public class ExecutionQueue {
	
	private static class ExecutionElement {
		final ICommand command;
		final List<String> options;
		final Runnable onFinish;
		ExecutionElement(ICommand command, List<String> options, Runnable onFinish) {
			super();
			this.command = command;
			this.options = options;
			this.onFinish = onFinish;
		}
	}
	private final BlockingQueue<ExecutionElement> queue = new LinkedBlockingQueue<>();	
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final ICommandConsole console;
	
	public ExecutionQueue(ICommandConsole console) {
		this.console = console;
	}

	public void start() {
		executor.submit(() -> run());
	}
	
	private void run() {
		Thread.currentThread().setName("Eris");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				ExecutionElement element = queue.take();
				try {
					element.command.exec(console, element.options);
					element.onFinish.run();
				} catch (IllegalArgumentException e) {
					System.err.println("Usage: " + e.getMessage());
					element.onFinish.run();
				} catch (RuntimeException e) {
					e.printStackTrace();
					element.onFinish.run();
				}
			} catch (InterruptedException e) {
				System.out.println("[CTRL+C]");
			}
		}
	}

	public void execute(ICommand cmd, List<String> o, Runnable onFinish) {
		queue.add(new ExecutionElement(cmd ,o, onFinish));
	}

	public void abort() {
		executor.shutdownNow();
	}

}
