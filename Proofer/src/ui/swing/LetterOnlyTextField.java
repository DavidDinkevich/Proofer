package ui.swing;

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import util.Utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

@SuppressWarnings("serial")
public class LetterOnlyTextField extends JTextField {
	private boolean beepOnInvalidInput = false;
	
	public LetterOnlyTextField(int columns) {
		super(columns);
	}

	public LetterOnlyTextField(String text, int columns) {
		super(text, columns);
	}

	public LetterOnlyTextField(String text) {
		super(text);
	}
	
	public LetterOnlyTextField() {
	}
	
	@Override
	public final void setDocument(Document doc) {
		super.setDocument(new LetterOnlyDocument());
	}
	
	public boolean beepOnInvalidInput() {
		return beepOnInvalidInput;
	}
	
	public void setBeepOnInvalidInput(boolean beepOnInvalidInput) {
		this.beepOnInvalidInput = beepOnInvalidInput;
	}
	
	
	private class LetterOnlyDocument extends PlainDocument {
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			String newString = str;
			// If the string contains spaces, special characters, or digits
			// (Utils.GEOMETRY_CHARS are allowed)
			if (!newString.matches("[" + Utils.GEOMETRY_CHARS + "a-zA-Z]*")) {
				if (beepOnInvalidInput)
					Toolkit.getDefaultToolkit().beep();
				newString = newString.replaceAll("[^a-zA-Z" + Utils.GEOMETRY_CHARS + "]", "");
			}
			super.insertString(offs, newString.toUpperCase(), a);
		}
		
	}
}
