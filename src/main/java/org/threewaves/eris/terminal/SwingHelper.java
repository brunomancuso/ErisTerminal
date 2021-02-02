package org.threewaves.eris.terminal;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

class SwingHelper {

	public static int getLineOfOffset(JTextComponent comp, int offset) throws BadLocationException {
		Document doc = comp.getDocument();
		if (offset < 0) {
			throw new BadLocationException("Can't translate offset to line", -1);
		} else if (offset > doc.getLength()) {
			throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
		} else {
			Element map = doc.getDefaultRootElement();
			return map.getElementIndex(offset);
		}
	}

	public static int getLastOffset(JTextComponent comp) {
		Document doc = comp.getDocument();
		Element map = doc.getDefaultRootElement();
		return map.getEndOffset();
	}

	public static int getLineStartOffset(JTextComponent comp, int line) throws BadLocationException {
		Element map = comp.getDocument().getDefaultRootElement();
		if (line < 0) {
			throw new BadLocationException("Negative line", -1);
		} else if (line >= map.getElementCount()) {
			throw new BadLocationException("No such line", comp.getDocument().getLength() + 1);
		} else {
			Element lineElem = map.getElement(line);
			return lineElem.getStartOffset();
		}
	}

	public static int getLineEndOffset(JTextComponent comp, int line) throws BadLocationException {
		Element map = comp.getDocument().getDefaultRootElement();
		if (line < 0) {
			throw new BadLocationException("Negative line", -1);
		} else if (line >= map.getElementCount()) {
			throw new BadLocationException("No such line", comp.getDocument().getLength() + 1);
		} else {
			Element lineElem = map.getElement(line);
			return lineElem.getEndOffset();
		}
	}

	public static void appendString(JTextPane component, String str, Color color) {
		StyledDocument document = (StyledDocument) component.getDocument();
		try {
			document.insertString(document.getLength(), str, updateColor(component, color));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public static MutableAttributeSet updateColor(JTextPane component, Color color) {
		Font f = component.getFont();
		MutableAttributeSet attrs = component.getInputAttributes();
		StyleConstants.setFontFamily(attrs, f.getFamily());
		StyleConstants.setFontSize(attrs, f.getSize());
		StyleConstants.setItalic(attrs, (f.getStyle() & Font.ITALIC) != 0);
		StyleConstants.setBold(attrs, (f.getStyle() & Font.BOLD) != 0);
		StyleConstants.setForeground(attrs, color);
		return attrs;

	}

}
