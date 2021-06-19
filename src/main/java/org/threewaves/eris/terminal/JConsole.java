package org.threewaves.eris.terminal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import org.threewaves.eris.engine.Config;
import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.footprint.Module;
import org.threewaves.eris.engine.footprint.Modules;

class JConsole {
	public static final Color NORMAL_COLOR = new Color(190, 190, 190);
	public static final Color ERROR_COLOR = new Color(255, 140, 144);
	public static final Color NOTIFICATION_COLOR = new Color(20, 168, 255);

	@FunctionalInterface
	public interface IExecutionListener {
		void execute(String command, List<String> options, Runnable onFinish);
	}

	private static class PopUp extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		JMenuItem copyButton;

		public PopUp(String mostRecentSelectedText) {
			copyButton = new JMenuItem(new AbstractAction("copy") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (!mostRecentSelectedText.equals("")) {
						StringSelection selection = new StringSelection(mostRecentSelectedText);
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(selection, selection);
					}
				}
			});
			add(copyButton);
		}
	}

	private final JTextPane component = new JTextPane() {
		private static final long serialVersionUID = 1L;
		
		@Override
		public boolean getScrollableTracksViewportWidth() {
			Component parent = getParent();
			ComponentUI ui = getUI();
			return parent != null ? (ui.getPreferredSize(this).width <= parent.getSize().width) : true;
		}		
	};
	private static final String PROMPT = "e> ";
	private static final String NEWLINE = "\r\n";
	private final List<IExecutionListener> listeners = new ArrayList<>();
	private final MergeAutoComplete autoComplete = new MergeAutoComplete();

	public JConsole(Config config, TerminalHistory history, Collection<ICommand> cmds, Modules modules) {
		KeywordAutoComplete keywords = new KeywordAutoComplete();
		keywords.add(cmds.stream().map(ICommand::name).collect(Collectors.toList()));
		keywords.add(modules.getList().stream().map(Module::getName).collect(Collectors.toList()));
		autoComplete.add(keywords).add(new DirectoryAutoComplete());
		Color back = new Color(25, 29, 31);
		Color front = NORMAL_COLOR;

		component.setBackground(back);
		component.setFont(new Font(config.fontFamily, Font.PLAIN, config.fontSize));
		component.setForeground(front);
		component.setCaretColor(front);
		DefaultCaret caret = (DefaultCaret) component.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		component.addMouseListener(new MouseAdapter() {
			String mostRecentSelectedText = "";

			@Override
			public void mouseReleased(MouseEvent e) {
				if (component.getSelectedText() != null) // See if they selected something
					mostRecentSelectedText = component.getSelectedText();
				else
					mostRecentSelectedText = "";
				if (e.isPopupTrigger())
					doPop(e);
			}

			private void doPop(MouseEvent e) {
				PopUp menu = new PopUp(mostRecentSelectedText);
				menu.show(e.getComponent(), e.getX(), e.getY());
			}

		});
		component.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				showPrompt();
			}
		});

		component.addKeyListener(new KeyAdapter() {
			private String currentCommand = null;

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					replacePrompt("");
					history.maxIndex();
					currentCommand = null;
				} else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
				} else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
				} else if (e.getKeyCode() == KeyEvent.VK_HOME) {
					homePrompt();
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (currentPromptPosition() <= 0) {
						e.consume();
					}
				} else  if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if (currentPromptPosition() <= 0) {
						e.consume();
						Toolkit.getDefaultToolkit().beep();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					String text = currentPrompt();
					if (currentPromptPosition() >= text.length()) {
						e.consume();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					e.consume();
					if (history.getIndex() <= 0) {
						history.resetIndex(); // It should never be less than zero, but you never know...
						Toolkit.getDefaultToolkit().beep();
						return;
					}
					if (history.getIndex() >= history.sizePrompt()) {
						history.maxIndex();
						currentCommand = currentPrompt(); // save the current command, for down arrow use.
					}
					history.decreaseIndex();
				
					// Index prompts and write the replacement.
					String replacementCommand = history.getPrompt();
					replacePrompt(replacementCommand);

				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					e.consume(); // pretty sure you can't go down, but if you can... don't.
					if (history.getIndex() >= history.sizePrompt()) {
						Toolkit.getDefaultToolkit().beep();
						return;
					}
					history.increaseIndex();
					if (history.getIndex() == history.sizePrompt()) {
						if (currentCommand != null) {
							replacePrompt(currentCommand);
						}
						return;
					}
					if (history.getIndex() < 0) {
						history.resetIndex();
					}
					replacePrompt(history.getPrompt());

				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String text = currentPrompt();
					if (text != null && text.length() > 0) {
						List<String> options = new ArrayList<>();
						String[] tmp = text.split("\\s+");
						currentCommand = null;
						if (tmp.length > 0) {
							history.addPrompt(text);
							history.save();
							for (int i = 1; i < tmp.length; i++) {
								options.add(tmp[i]);
							}
							history.maxIndex();
							publishExecution(tmp[0], Collections.unmodifiableList(options), () -> newPrompt());
						}
						
					} else {
						newPrompt();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
					e.consume();
					String input = currentPrompt();
					List<List<String>> completions = autoComplete.complete(input);
					if (IAutoComplete.count(completions) == 0) {
						// no completions
						Toolkit.getDefaultToolkit().beep();
					} else if (IAutoComplete.count(completions) == 1) {
						String toInsert = completions.get(0).get(0);
						int start = input.lastIndexOf(' ');
						if (start <= -1) {
							start = input.lastIndexOf('\t');
						}
						if (start <= -1) {
							start = 0;
						} else {
							start += 1;
						}
						input = input.substring(0, start);
						replacePrompt(input + toInsert);
					} else {
						StringBuilder help = new StringBuilder();
						help.append('\n');
						int count = 0;
						for (List<String> list : completions) {
							for (String str : list) {
								help.append(' ');
								help.append(str);
								count++;
								if (count > 10) {
									help.append("\n");
									count = 0;
								}

							}
							help.append("\n");
							count = 0;
						}
						System.out.print(help);
						newPrompt();
						System.out.print(input);
					}
				} else if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()) {
					System.out.println("CTRL+C");
					publishExecution("CTRL+C", Collections.emptyList(), () -> newPrompt());
					e.consume();
				} else {
					if (!isWritingPrompt()) {
						e.consume();
					}
				}
			}
		});
	}

	private void publishExecution(String command, List<String> options, Runnable onFinish) {
		listeners.forEach(e -> {
			try {
				e.execute(command, options, onFinish);
			} catch (IllegalArgumentException ex) {
				System.err.println("Error in parameters");
				System.err.println(ex.getMessage());

			}
		});
	}

	public void addExecutionListener(IExecutionListener listener) {
		this.listeners.add(listener);
	}

	void homePrompt() {
		try {
			int lineNumber = lineNumberPrompt();
			int startOffset = getLineStartOffset(lineNumber);
			component.setCaretPosition(startOffset + PROMPT.length());
		} catch (RuntimeException | BadLocationException e) {
		//nothing
		}
	}

	void newPrompt() {
		System.out.print(PROMPT);
		showPrompt();
	}

	void newPromptLine() {
		System.out.print(PROMPT + NEWLINE);
		showPrompt();
	}

	private void showPrompt() {
		int lastOffset = SwingHelper.getLastOffset(component);
		if (lastOffset > 0) {
			component.setCaretPosition(lastOffset - 1);
		}
	}

	private void replacePrompt(String prompt) {
		try {
			int lineNumber = lineNumberPrompt();
			int startOffset = getLineStartOffset(lineNumber);
			int endOffset = getLineEndOffset(lineNumber);
			remove(startOffset, endOffset);
			append(NORMAL_COLOR, PROMPT + prompt);
		} catch (RuntimeException | BadLocationException e) {
			//nothing
		}
	}

	public boolean isWritingPrompt() {
		try {
			return (lineNumberPrompt() == getLineOfOffset(size() - 1));
		} catch (BadLocationException e) {
			//nothing
		}
		return false;
	}
	private int getLineEndOffset(int line) throws BadLocationException {
		return SwingHelper.getLineEndOffset(component, line);
	}

	private int getLineStartOffset(int line) throws BadLocationException {
		return SwingHelper.getLineStartOffset(component, line);
	}

	private int getLineOfOffset(int offset) throws BadLocationException {
		return SwingHelper.getLineOfOffset(component, offset);
	}

	public String currentPrompt() {
		try {
			int lineNumber = lineNumberPrompt();
			int startOffset = getLineStartOffset(lineNumber);
			int endOffset = getLineEndOffset(lineNumber);
			String text = component.getText(startOffset, endOffset - startOffset - 1);
			if (text.startsWith(PROMPT)) {
				return text.substring(PROMPT.length());
			}
		} catch (RuntimeException | BadLocationException e) {
		}
		return "";
	}

	private int lineNumberPrompt() {
		try {
			int offset = SwingHelper.getLastOffset(component) - 1;		
			return getLineOfOffset(offset);
		} catch (RuntimeException | BadLocationException e) {
		}
		return 0;
	}

	public int currentPromptPosition() {
		try {
			int caretOffset = component.getCaretPosition();
			int lineNumber = getLineOfOffset(caretOffset);
			int startOffset = getLineStartOffset(lineNumber);
			return caretOffset - startOffset - PROMPT.length();
		} catch (RuntimeException | BadLocationException e) {
		}
		return -1;
	}

	public Component getComponent() {
		return component;
	}

	public void append(Color color, String val) {
		SwingHelper.appendString(component, val, color);
	}

	public void remove(int start, int end) {
		component.select(start, end);
		component.replaceSelection("");
	}

	public int size() {
		return SwingHelper.getLastOffset(component) + 1;
	}
}
