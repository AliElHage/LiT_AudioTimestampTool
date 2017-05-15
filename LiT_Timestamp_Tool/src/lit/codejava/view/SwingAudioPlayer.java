package lit.codejava.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import lit.codejava.controller.Controller;
import lit.codejava.controller.GlobalTimer;
import lit.codejava.controller.PlayingTimer;
import model.TimeBlock;

public class SwingAudioPlayer extends JFrame implements ActionListener {

	private Controller player = new Controller();
	private Thread playbackThread;
	private PlayingTimer timer;
	
	private boolean isPlaying = false;
	private boolean isPaused = false;
	private boolean isStopped = true;
	private boolean interrupted = false;
	
	private long initialTime = 0;
	private long finalTime = 0;
	
	private String audioFilePath;
	private String lastOpenPath;
	
	private JLabel labelFileName = new JLabel("Currently Playing:");
	private JLabel labelTimeCounter = new JLabel("00:00:00");
	private JLabel labelDuration = new JLabel("00:00:00");
	
	private JButton buttonOpen = new JButton("Open");
	private JButton buttonPlay = new JButton("Play");
	private JButton buttonQuickPlay = new JButton();
	private JButton buttonPause = new JButton("Pause");
	private JButton buttonToggleLoop = new JButton();
	private JButton buttonSkipForward = new JButton();
	private JButton buttonSkipBackward = new JButton();
	private JButton buttonForwardSecond = new JButton();
	private JButton buttonBackwardSecond = new JButton();
	
	// for displaying TimeBlock objects
	private JList timeBlockList = new JList<TimeBlock>();
	private ArrayList<String> startTimes = new ArrayList();
	private ArrayList<String> endTimes = new ArrayList();
	private ArrayList<String> types = new ArrayList();
	private ArrayList<String> descriptions = new ArrayList();
	private JScrollPane timeBlockScrollPane;
	private JTable timeBlockTable = new JTable();
	private JTextArea timeBlockInfo = new JTextArea();
	private String timeBlockType;
	private String timeBlockDescription;
	private String startTime;
	private String endTime;
	
	// for inputting TimeBlock objects
	private JTextField startTimeField = new JTextField(5);
	private JTextField endTimeField = new JTextField();
	private JTextField descriptionField = new JTextField();
	
	private JSlider timeSlider = new JSlider();
	
	private ImageIcon iconOpen = new ImageIcon(getClass().getResource(
			"/lit/codejava/audio/images/Open.png"));
	private ImageIcon iconPlay = new ImageIcon(getClass().getResource(
			"/lit/codejava/audio/images/Play.gif"));
	private ImageIcon iconStop = new ImageIcon(getClass().getResource(
			"/lit/codejava/audio/images/Stop.gif"));
	private ImageIcon iconPause = new ImageIcon(getClass().getResource(
			"/lit/codejava/audio/images/Pause.png"));
	private ImageIcon iconLoop = new ImageIcon(getClass().getResource(
			"/lit/codejava/audio/images/Loop.png"));
	private ImageIcon iconSkipForward = new ImageIcon(getClass().getResource(
			"/lit/codejava/audio/images/ForwardFrameSkip.png"));
	private ImageIcon iconSkipBackward = new ImageIcon(getClass().getResource(
			"/lit/codejava/audio/images/BackFrameSkip.png"));
	private ImageIcon iconForwardSecond = new ImageIcon(getClass().getResource(
			"/lit/codejava/audio/images/ForwardSecondSkip.png"));
	private ImageIcon iconBackwardSecond = new ImageIcon(getClass().getResource(
			"/lit/codejava/audio/images/BackwardSecondSkip.png"));
	
	public SwingAudioPlayer(){
		
		super("LiT Audio Marker");
		setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.anchor = GridBagConstraints.WEST;
		
		buttonOpen.setFont(new Font("Sans", Font.BOLD, 14));
		buttonOpen.setIcon(iconOpen);
		
		buttonPlay.setFont(new Font("Sans", Font.BOLD, 14));
		buttonPlay.setIcon(iconPlay);
		buttonPlay.setEnabled(false);
		
		buttonPause.setFont(new Font("Sans", Font.BOLD, 14));
		buttonPause.setIcon(iconPause);
		buttonPause.setEnabled(false);
		
		buttonToggleLoop.setIcon(iconLoop);
		buttonToggleLoop.setPreferredSize(new Dimension(30, buttonToggleLoop.getPreferredSize().height));
		buttonToggleLoop.setEnabled(false);
		
		buttonSkipForward.setIcon(iconSkipForward);
		buttonSkipForward.setPreferredSize(new Dimension(30, buttonSkipForward.getPreferredSize().height));
		buttonSkipForward.setEnabled(false);
		
		buttonSkipBackward.setIcon(iconSkipBackward);
		buttonSkipBackward.setPreferredSize(new Dimension(30, buttonSkipBackward.getPreferredSize().height));
		buttonSkipBackward.setEnabled(false);
		
		buttonForwardSecond.setIcon(iconForwardSecond);
		buttonForwardSecond.setPreferredSize(new Dimension(30, buttonForwardSecond.getPreferredSize().height));
		buttonForwardSecond.setEnabled(false);
		
		buttonBackwardSecond.setIcon(iconBackwardSecond);
		buttonBackwardSecond.setPreferredSize(new Dimension(30, buttonBackwardSecond.getPreferredSize().height));
		buttonBackwardSecond.setEnabled(false);
		
		buttonQuickPlay.setIcon(iconPlay);
		buttonQuickPlay.setPreferredSize(new Dimension(30, buttonQuickPlay.getPreferredSize().height));
		buttonQuickPlay.setEnabled(false);
		
		labelTimeCounter.setFont(new Font("Sans", Font.BOLD, 12));
		labelDuration.setFont(new Font("Sans", Font.BOLD, 12));
		
		timeSlider.setPreferredSize(new Dimension(800, 20));
		timeSlider.setEnabled(false);
		timeSlider.setValue(0);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		add(labelFileName, constraints);
		
		JPanel panelButtonOpen = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 5));
		panelButtonOpen.add(buttonOpen);
		
		constraints.gridwidth = 1;
		constraints.gridx = 2;
		constraints.gridy = 0;
		add(panelButtonOpen, constraints);
		
		JPanel panelButtonToggleLoop = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
		panelButtonToggleLoop.add(buttonToggleLoop);
		
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		constraints.gridy = 2;
		add(panelButtonToggleLoop, constraints);
		
		JPanel panelSkipFrame = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 5));
		panelSkipFrame.add(buttonBackwardSecond);
		panelSkipFrame.add(buttonSkipBackward);
		panelSkipFrame.add(buttonQuickPlay);
		panelSkipFrame.add(buttonSkipForward);
		panelSkipFrame.add(buttonForwardSecond);
		
		constraints.gridwidth = 1;
		constraints.gridx = 1;
		constraints.gridy = 2;
		add(panelSkipFrame, constraints);
		
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.insets = new Insets(5, 25, 5, 25);
		add(labelTimeCounter, constraints);
		constraints.insets = new Insets(5, 5, 5, 5);
		
		constraints.gridx = 1;
		add(timeSlider, constraints);
		
		constraints.gridx = 2;
		add(labelDuration, constraints);
		
		JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 5));
		//panelButtons.add(buttonOpen);
		panelButtons.add(buttonPlay);
		panelButtons.add(buttonPause);
		
		constraints.gridwidth = 3;
		constraints.gridx = 0;
		constraints.gridy = 2;
		add(panelButtons, constraints);
		
		GridBagConstraints fileDisplayConstraints = new GridBagConstraints();
		fileDisplayConstraints.insets = new Insets(5, 5, 5, 5);
		fileDisplayConstraints.anchor = GridBagConstraints.WEST;
		
		// add TimeBlock preview panel
		timeBlockList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		timeBlockScrollPane = new JScrollPane(timeBlockList);
		fileDisplayConstraints.gridwidth = 2;
		fileDisplayConstraints.gridx = 0;
		fileDisplayConstraints.gridy = 3;
		add(timeBlockScrollPane, fileDisplayConstraints);
		
		// add text fields
		fileDisplayConstraints.gridwidth = 1;
		fileDisplayConstraints.gridx = 2;
		fileDisplayConstraints.gridy = 3;
		add(startTimeField, fileDisplayConstraints);
		
		buttonOpen.addActionListener(this);
		buttonPlay.addActionListener(this);
		buttonPause.addActionListener(this);
		buttonToggleLoop.addActionListener(this);
		buttonSkipForward.addActionListener(this);
		buttonSkipBackward.addActionListener(this);
		buttonForwardSecond.addActionListener(this);
		buttonBackwardSecond.addActionListener(this);
		buttonQuickPlay.addActionListener(this);
		
		//timer = new PlayingTimer(labelTimeCounter, timeSlider);
		
		timeSlider.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (!GlobalTimer.getTimerUpdate() && !GlobalTimer.getUserUpdate()) {
            	JSlider source = (JSlider)e.getSource();
            	
            	if(!GlobalTimer.getStateChanged()){
            		GlobalTimer.setStateChanged(true);
            		GlobalTimer.setInitPosition(source.getValue());
            		initialTime = System.currentTimeMillis();
            	}
            	
            	if(!source.getValueIsAdjusting()){
            		GlobalTimer.setUserUpdate(true);
            		finalTime = System.currentTimeMillis();
            		GlobalTimer.addMoveDelay(finalTime - initialTime);
            	}
            	//System.out.println(finalTime - initialTime);
                try {
                    int progress = timeSlider.getValue();
                    long time = (long) progress * 100000;
                    player.getAudioClip().setMicrosecondPosition(time);
                } finally {
                	
                }
            }
        }
    });
		
		pack();
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
	}
	
	public void actionPerformed(ActionEvent event){
		
		Object source = event.getSource();
		if(source instanceof JButton){
			JButton button = (JButton)source;
			if(button == buttonOpen){
				openFile();
			}
			else if(button == buttonPlay){
				if(!isPlaying){
					playBack();
				}
				else {
					stop();
				}
			}
			else if(button == buttonQuickPlay){
				if(!isPaused && !isStopped){
					pause();
				}
				else{
					System.out.println(isStopped);
					if(isStopped){
						playBack();
					}
					else resume();
				}
			}
			else if(button == buttonPause){
				if(!isPaused){
					pause();
				}
				else resume();
			}
			else if(button == buttonSkipForward){
				skipFrame(1);
			}
			else if(button == buttonSkipBackward){
				skipFrame(-1);
			}
			else if(button == buttonForwardSecond){
				skipFrame(10);
			}
			else if(button == buttonBackwardSecond){
				skipFrame(-10);
			}
			else if(button == buttonToggleLoop){
				toggleLoop();
				System.out.println(player.getLoopEnabled());
			}
		}
		
	}

	private void openFile() {
		JFileChooser fileChooser = null;
		
		if (lastOpenPath != null && !lastOpenPath.equals("")) {
			fileChooser = new JFileChooser(lastOpenPath);
		} else {
			fileChooser = new JFileChooser();
		}
		
		FileFilter wavFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "Sound file (*.WAV)";
			}

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					return file.getName().toLowerCase().endsWith(".wav");
				}
			}
		};

		
		fileChooser.setFileFilter(wavFilter);
		fileChooser.setDialogTitle("Open Audio File");
		fileChooser.setAcceptAllFileFilterUsed(false);

		int userChoice = fileChooser.showOpenDialog(this);
		if (userChoice == JFileChooser.APPROVE_OPTION) {
			audioFilePath = fileChooser.getSelectedFile().getAbsolutePath();
			lastOpenPath = fileChooser.getSelectedFile().getParent();
			if (isPlaying || isPaused) {
				stop();
				while (player.getAudioClip().isRunning()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
			playBack();
			buttonToggleLoop.setEnabled(true);
			buttonSkipForward.setEnabled(true);
			buttonSkipBackward.setEnabled(true);
			buttonForwardSecond.setEnabled(true);
			buttonBackwardSecond.setEnabled(true);
		}
	}

	/**
	 * Start playing back the sound.
	 */
	private void playBack() {
		timer = new PlayingTimer(labelTimeCounter, timeSlider);
		timer.start();
		isPlaying = true;
		isStopped = false;
		interrupted = false;
		playbackThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					buttonPlay.setText("Stop");
					buttonPlay.setIcon(iconStop);
					buttonPlay.setEnabled(true);
					
					buttonQuickPlay.setIcon(iconPause);
					buttonQuickPlay.setEnabled(true);
					
					buttonPause.setText("Pause");
					buttonPause.setEnabled(true);
					
					player.load(audioFilePath);
					timer.setAudioClip(player.getAudioClip());
					timeSlider.setEnabled(true);
					
					String croppedPath = cropString(audioFilePath);
					
					labelFileName.setText("Playing File: " + croppedPath);
					timeSlider.setMaximum((int) player.getClipSecondLength() * 10);
					
					labelDuration.setText(player.getClipLengthString());
					player.play();
					
					resetControls();

				} catch (UnsupportedAudioFileException ex) {
					JOptionPane.showMessageDialog(SwingAudioPlayer.this,  
							"The audio format is unsupported!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				} catch (LineUnavailableException ex) {
					JOptionPane.showMessageDialog(SwingAudioPlayer.this,  
							"Could not play the audio file because line is unavailable!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(SwingAudioPlayer.this,  
							"I/O error while playing the audio file!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				}

			}
		});

		playbackThread.start();
	}

	private void stop() {
		isPaused = false;
		isStopped = true;
		buttonPause.setText("Pause");
		buttonPause.setEnabled(false);
		buttonPause.setIcon(iconPause);
		timer.resetTimer();
		timer.interrupt();
		player.stop();
		playbackThread.interrupt();
		GlobalTimer.setMoveDelay(0);
		GlobalTimer.setOffsetValue(0);
		interrupted = true;
		player.toggleLoop();
		buttonToggleLoop.setSelected(false);
	}
	
	private void pause() {
		buttonPause.setText("Resume");
		buttonPause.setIcon(iconPlay);
		buttonQuickPlay.setIcon(iconPlay);
		isPaused = true;
		player.pause();
		timer.pauseTimer();
		playbackThread.interrupt();
	}
	
	private void resume() {
		buttonPause.setText("Pause");
		buttonPause.setIcon(iconPause);
		buttonQuickPlay.setIcon(iconPause);
		isPaused = false;
		player.resume();
		timer.resumeTimer();
		playbackThread.interrupt();		
	}
	
	private void skipFrame(int value){
		if(timeSlider.getValue() + value <= timeSlider.getMaximum()){
			timeSlider.setValue(timeSlider.getValue() + value);
		}
	}
	
	private void toggleLoop() {
		//buttonToggleLoop.setBorder(BorderFactory.createLoweredBevelBorder());
		buttonToggleLoop.setPressedIcon(iconLoop);
		player.toggleLoop();
		if(player.getLoopEnabled()) buttonToggleLoop.setSelected(true);
		else buttonToggleLoop.setSelected(false);
	}
	
	private void resetControls() {
		timer.resetTimer();
		timer.interrupt();

		buttonPlay.setText("Play");
		buttonPlay.setIcon(iconPlay);
		buttonQuickPlay.setIcon(iconPlay);
		
		buttonPause.setEnabled(false);
		
		isPlaying = false;
		
		GlobalTimer.setMoveDelay(0);
		GlobalTimer.setOffsetValue(0);
		
		isStopped = true;
		
		if(player.getLoopEnabled() && !interrupted){
			System.out.println("LALALALELELE");
			playBack();
			interrupted = false;
		}
		
	}
	
	private String cropString(String path){
		String croppedPath = "";
		int index = path.length() - 1;
		while(path.charAt(index) != '\\'){
			index--;
		}
		while(index < path.length() - 1){
			index++;
			croppedPath += path.charAt(index);
		}
		return croppedPath;
	}
	
}
