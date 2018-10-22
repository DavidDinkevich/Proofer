package main;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.text.ParseException;

import de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel;

import ui.swing.ProofCustomizationPanel;

/**
 * Main class for the program.
 * @author David Dinkevich
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = -2549207704173465317L;
	
	public static final String APP_NAME;
	
	static {
		APP_NAME = "Proofer";
		
		/*
		 * Set look and feel
		 */
		
		// SYNTHETICA
		try {
//			UIManager.setLookAndFeel(new SyntheticaAluOxideLookAndFeel());
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		// NIMBUS
//		try {
//			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//		        if ("Nimbus".equals(info.getName())) {
//		            UIManager.setLookAndFeel(info.getClassName());
//		            break;
//		        }
//		    }
//		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
//				| UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		}
	}
	
	// Screen components
	private ProofCustomizationPanel proofPanel;
	
	private boolean windowCreated = false;
	
	public MainWindow() {
		super(APP_NAME);
	}
	
	private void createWindow() {
		if (windowCreated)
			return;
		
		windowCreated = true;
		setSize(1200, 800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		proofPanel = new ProofCustomizationPanel();
		add(proofPanel, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public ProofCustomizationPanel getProofCustomizationPanel() {
		return proofPanel;
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainWindow().createWindow();
			}
		});
	}
}
