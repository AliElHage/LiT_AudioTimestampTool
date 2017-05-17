package lit.codejava.application;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import lit.codejava.view.SwingAudioPlayer;
import lit.codejava.view.testView;

public class Application {
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				//new SwingAudioPlayer().setVisible(true);
				new testView().setVisible(true);
			}
		});
	}
	
}
