package ui;

import java.awt.Toolkit;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

import util.Utils;

public class FigureTextField extends TextField {

	public FigureTextField() {
		init();
	}

	public FigureTextField(String arg0) {
		super(arg0);
		init();
	}
	
	private void init() {
		setTextFormatter(new TextFormatter<String>((Change change) -> {
			
			StringBuilder newText = new StringBuilder(
					change.getText().toUpperCase()
			);
			
			for (int i = newText.length() - 1; i >= 0; i--) {
				if (!Utils.isPermissibleText(newText.charAt(i))) {
					newText.deleteCharAt(i);
					Toolkit.getDefaultToolkit().beep();
				}
			}
			
			change.setText(newText.toString());
			return change;
			
		}));
	}
	
}
