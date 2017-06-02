package lit.codejava.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.FontMetrics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.thoughtworks.xstream.io.path.Path;

import lit.codejava.controller.Controller;
import lit.codejava.controller.GlobalTimer;
import lit.codejava.controller.InvalidInputException;
import lit.codejava.controller.PlayingTimer;
import lit.codejava.controller.TimingController;
import lit.codejava.persistence.Persistence;
import model.AudioTool;
import model.TimeBlock;

public class testView extends JFrame implements ActionListener {

	private Controller player = new Controller();
	private TimingController timingController;
	private Thread playbackThread;
	private PlayingTimer timer;
	private TimeBlock tbInMemory;
	
	private boolean isPlaying = false;
	private boolean isPaused = false;
	private boolean isStopped = true;
	private boolean interrupted = false;
	
	private long initialTime = 0;
	private long finalTime = 0;
	private long targetTime = 0;
	
	private Long selectedTime = null;
	
	private String audioFilePath;
	private String lastOpenPath;
	private String xmlFilePath;
	private String xmlLastOpenPath;
	
	private JLabel labelFileName = new JLabel("Currently Playing:");
	private JLabel labelTimeCounter = new JLabel("00:00:00", SwingConstants.CENTER);
	private JLabel labelDuration = new JLabel("00:00:00", SwingConstants.CENTER);
	
	private JButton buttonOpen = new JButton("Open");
	private JButton buttonPlay = new JButton("Play");
	private JButton buttonQuickPlay = new JButton();
	private JButton buttonPause = new JButton("Pause");
	private JButton buttonToggleLoop = new JButton();
	private JButton buttonSkipForward = new JButton();
	private JButton buttonSkipBackward = new JButton();
	private JButton buttonForwardSecond = new JButton();
	private JButton buttonBackwardSecond = new JButton();
	private JButton buttonAddTimestamp = new JButton("Stamp");
	private JButton buttonSampleStart = new JButton();
	private JButton buttonSampleEnd = new JButton();
	private JButton buttonDeleteSelected = new JButton("Delete Selected");
	private JButton buttonUndoDelete = new JButton("Undo Delete");
	private JButton buttonGoToStamp = new JButton("Go To Selected");
	private JButton buttonSaveFile = new JButton("Save File");
	private JButton buttonLoadFile = new JButton("Load File");
	private JButton buttonTimelineStamp = new JButton("Stamp");
	private JButton buttonTimelineRemove = new JButton("Delete Selected");
	private JButton buttonTimelineRedo = new JButton("Undo Delete");
	
	// for displaying TimeBlock objects
	private ArrayList<TimeBlock> timeBlocks = new ArrayList();
	private ArrayList<String> startTimes = new ArrayList();
	private ArrayList<String> endTimes = new ArrayList();
	private ArrayList<String> types = new ArrayList();
	private ArrayList<String> descriptions = new ArrayList();
	private ArrayList<Long> timeline = new ArrayList();
	private JScrollPane timeBlockScrollPane;
	private JScrollPane timelineScrollPane;
	private JTable timeBlockTable = new JTable();
	private JTable timelineTable = new JTable();
	private JTextArea timeBlockInfo = new JTextArea();
	private String timeBlockType;
	private String timeBlockDescription;
	private String startTime;
	private String endTime;
	
	// for inputting TimeBlock objects
	private JTextField startTimeField = new JTextField(10);
	private JTextField endTimeField = new JTextField(10);
	private JTextField typeField = new JTextField(10);
	private JTextField descriptionField = new JTextField(10);
	private JLabel startTimeLabel = new JLabel("Start Time:");
	private JLabel endTimeLabel = new JLabel("End Time:");
	private JLabel typeLabel = new JLabel("Type:");
	private JLabel descriptionLabel = new JLabel("Description:");
	private JLabel errorLogLabel = new JLabel();
	private JLabel convertedStartTime = new JLabel("Start Time: ");
	private JLabel convertedEndTime = new JLabel("End Time: ");
	
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
	private ImageIcon iconSample = new ImageIcon(getClass().getResource(
			"/lit/codejava/audio/images/Sample.png"));
	
	public testView(){
		
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
		
		buttonSampleStart.setIcon(iconSample);
		buttonSampleStart.setPreferredSize(new Dimension(30, buttonSampleStart.getPreferredSize().height));
		buttonSampleStart.setEnabled(false);
		
		buttonSampleEnd.setIcon(iconSample);
		buttonSampleEnd.setPreferredSize(new Dimension(30, buttonSampleEnd.getPreferredSize().height));
		buttonSampleEnd.setEnabled(false);
		
		buttonAddTimestamp.setEnabled(false);
		buttonDeleteSelected.setEnabled(false);
		buttonDeleteSelected.setPreferredSize(new Dimension(buttonDeleteSelected.getPreferredSize().width + 50, buttonDeleteSelected.getPreferredSize().height));
		
		buttonUndoDelete.setEnabled(false);
		buttonUndoDelete.setPreferredSize(new Dimension(buttonDeleteSelected.getPreferredSize().width, buttonUndoDelete.getPreferredSize().height));
		
		buttonGoToStamp.setEnabled(false);
		buttonGoToStamp.setPreferredSize(new Dimension(buttonGoToStamp.getPreferredSize().width + 30, buttonGoToStamp.getPreferredSize().height));
		
		buttonSaveFile.setEnabled(false);
		buttonSaveFile.setPreferredSize(new Dimension(buttonSaveFile.getPreferredSize().width + 64, buttonSaveFile.getPreferredSize().height));
		
		buttonLoadFile.setEnabled(false);
		buttonLoadFile.setPreferredSize(new Dimension(buttonLoadFile.getPreferredSize().width + 64, buttonLoadFile.getPreferredSize().height));
		
		buttonTimelineStamp.setEnabled(false);
		buttonTimelineStamp.setPreferredSize(new Dimension(150, buttonTimelineStamp.getPreferredSize().height));
		
		buttonTimelineRemove.setEnabled(false);
		buttonTimelineRemove.setPreferredSize(new Dimension(150, buttonTimelineRemove.getPreferredSize().height));
		
		buttonTimelineRedo.setEnabled(false);
		buttonTimelineRedo.setPreferredSize(new Dimension(150, buttonTimelineRedo.getPreferredSize().height));
		
		errorLogLabel.setForeground(Color.RED);
		errorLogLabel.setVerticalAlignment(SwingConstants.TOP);
		
		labelTimeCounter.setFont(new Font("Sans", Font.BOLD, 12));
		labelDuration.setFont(new Font("Sans", Font.BOLD, 12));
		
		labelTimeCounter.setPreferredSize(new Dimension(buttonOpen.getPreferredSize().width, labelTimeCounter.getPreferredSize().height));
		labelDuration.setPreferredSize(new Dimension(buttonOpen.getPreferredSize().width, labelDuration.getPreferredSize().height));
		
		timeSlider.setPreferredSize(new Dimension(820, 20));
		timeSlider.setEnabled(false);
		timeSlider.setValue(0);
		
		timeBlockTable.addMouseListener(new java.awt.event.MouseAdapter() {
		    @Override
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		        int row = timeBlockTable.rowAtPoint(evt.getPoint());
		        int col = timeBlockTable.columnAtPoint(evt.getPoint());
		        startTimeField.setText(timeBlockTable.getValueAt(row, 0).toString());
		        endTimeField.setText(timeBlockTable.getValueAt(row, 1).toString());
		        
		        long millis = Long.valueOf(timeBlockTable.getValueAt(row, 0).toString());
		        long second = (millis / 1000) % 60;
		        long minute = (millis / (1000 * 60)) % 60;
		        long hour = (millis / (1000 * 60 * 60)) % 24;

		        String startTime = String.format("%02d:%02d:%02d", hour, minute, second);
		        convertedStartTime.setText("Start Time: " + startTime);
		        
		        targetTime = millis;
		        
		        millis = Long.valueOf(timeBlockTable.getValueAt(row, 1).toString());
		        second = (millis / 1000) % 60;
		        minute = (millis / (1000 * 60)) % 60;
		        hour = (millis / (1000 * 60 * 60)) % 24;

		        String endTime = String.format("%02d:%02d:%02d", hour, minute, second);
		        convertedEndTime.setText("End Time:  " + endTime);
		    }
		});
		
		timeBlockTable.getTableHeader().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(java.awt.event.MouseEvent e) {
		        int col = timeBlockTable.columnAtPoint(e.getPoint());
		        String name = timeBlockTable.getColumnName(col);
		        System.out.println("Column index selected " + col + " " + name);
		        AudioTool master = AudioTool.getInstance();
		        master.sortTimeBlocks(col);
		        refreshTable();
		    }
		});
		
		GridBagConstraints macroConstraints = new GridBagConstraints();
		macroConstraints.insets = new Insets(5, 5, 5, 5);
		macroConstraints.anchor = GridBagConstraints.CENTER;
		
		macroConstraints.gridx = 0;
		macroConstraints.gridy = 0;
		macroConstraints.gridwidth = 1;
		
		////////////////////////////////////////////////////////////////
		// Audio Player Layout /////////////////////////////////////////
		////////////////////////////////////////////////////////////////
		
		JPanel audioPlayerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints audioConstraints = new GridBagConstraints();
		audioConstraints.insets = new Insets(5, 5, 5, 5);
		audioConstraints.anchor = GridBagConstraints.WEST;
		
		// file name label
		audioConstraints.gridx = 0;
		audioConstraints.gridy = 0;
		audioConstraints.gridwidth = 6;
		
		audioPlayerPanel.add(labelFileName, audioConstraints);
		
		// file open button
		audioConstraints.gridx = 6;
		audioConstraints.gridy = 0;
		audioConstraints.gridwidth = 1;
		
		audioPlayerPanel.add(buttonOpen, audioConstraints);
		
		// current time label
		audioConstraints.gridx = 0;
		audioConstraints.gridy = 1;
		audioConstraints.gridwidth = 1;
		audioConstraints.weightx = 0.5;
		audioConstraints.weighty = 0.5;
		
		audioPlayerPanel.add(labelTimeCounter, audioConstraints);
		
		// time slider
		audioConstraints.gridx = 1;
		audioConstraints.gridy = 1;
		audioConstraints.gridwidth = 5;
						
		audioPlayerPanel.add(timeSlider, audioConstraints);
		
		// duration label
		audioConstraints.gridx = 6;
		audioConstraints.gridy = 1;
		audioConstraints.gridwidth = 1;
		audioConstraints.weightx = 0.5;
		audioConstraints.weighty = 0.5;
						
		audioPlayerPanel.add(labelDuration, audioConstraints);
		
		// toggle loop button
		audioConstraints.gridx = 0;
		audioConstraints.gridy = 2;
		audioConstraints.gridwidth = 1;
		audioConstraints.anchor = GridBagConstraints.CENTER;
		audioConstraints.insets = new Insets(10, 5, 5, 5);
		
		audioPlayerPanel.add(buttonToggleLoop, audioConstraints);
		
		// quick control panel
		JPanel panelSkipFrame = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 5));
		panelSkipFrame.add(buttonBackwardSecond);
		panelSkipFrame.add(buttonSkipBackward);
		panelSkipFrame.add(buttonQuickPlay);
		panelSkipFrame.add(buttonSkipForward);
		panelSkipFrame.add(buttonForwardSecond);
		
		audioConstraints.gridx = 1;
		audioConstraints.gridy = 2;
		audioConstraints.gridwidth = 1;
		audioConstraints.anchor = GridBagConstraints.WEST;
		audioPlayerPanel.add(panelSkipFrame, audioConstraints);
		
		// main play / stop panel
		JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 5));
		panelButtons.add(buttonPlay);
		panelButtons.add(buttonPause);
		
		audioConstraints.gridx = 1;
		audioConstraints.gridy = 2;
		audioConstraints.gridwidth = 1;
		audioConstraints.anchor = GridBagConstraints.CENTER;
		
		audioPlayerPanel.add(panelButtons, audioConstraints);
		
		// add border to audio player
		audioPlayerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		// add audio player to main grid bag
		add(audioPlayerPanel, macroConstraints);
		
		////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////
		
		macroConstraints.gridx = 0;
		macroConstraints.gridy = 1;
		macroConstraints.gridwidth = 1;
		
		////////////////////////////////////////////////////////////////
		// Tabbed Timestamp Layout /////////////////////////////////////
		////////////////////////////////////////////////////////////////
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(this.getPreferredSize().width, 400));
		audioPlayerPanel.setPreferredSize(new Dimension(this.getPreferredSize().width, audioPlayerPanel.getPreferredSize().height));
		
		//////////////////////////////////////////////////
		// Time Block Tab ////////////////////////////////
		
		JPanel timeBlockTab = new JPanel(new GridBagLayout());
		
		GridBagConstraints timeBlockConstraints = new GridBagConstraints();
		timeBlockConstraints.insets = new Insets(5, 5, 5, 5);
		timeBlockConstraints.anchor = GridBagConstraints.WEST;
		
		timeBlockConstraints.gridx = 1;
		timeBlockConstraints.gridy = 0;
		timeBlockConstraints.gridwidth = 3;
		timeBlockConstraints.gridheight = 1;
		
		// add TimeBlock preview panel
		refreshTable();
		setTableColumnsWidth();
		timeBlockTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		timeBlockScrollPane = new JScrollPane(timeBlockTable);
		timeBlockScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		timeBlockScrollPane.setPreferredSize(new Dimension(600, 208));
		
		timeBlockTab.setPreferredSize(new Dimension(850, 220));
		
		timeBlockTab.add(timeBlockScrollPane, timeBlockConstraints);
		
		System.out.println(timeBlockTab.getPreferredSize());
		
		//////////////////////////////////////////////////
		//////////////////////////////////////////////////
		
		//////////////////////////////////////////////////
		// Text Fields ///////////////////////////////////
		
		JPanel timeBlockFields = new JPanel(new GridBagLayout());
		GridBagConstraints inputFieldConstraints = new GridBagConstraints();
		inputFieldConstraints.insets = new Insets(5, 5, 5, 5);
		inputFieldConstraints.anchor = GridBagConstraints.WEST;
		
		inputFieldConstraints.gridx = 0;
		inputFieldConstraints.gridy = 0;
		inputFieldConstraints.gridwidth = 1;
		
		timeBlockFields.add(startTimeLabel, inputFieldConstraints);
		
		inputFieldConstraints.gridx = 1;
		inputFieldConstraints.gridy = 0;
		inputFieldConstraints.gridwidth = 1;
		
		startTimeField.setEnabled(false);
		timeBlockFields.add(startTimeField, inputFieldConstraints);
		
		inputFieldConstraints.gridx = 2;
		inputFieldConstraints.gridy = 0;
		inputFieldConstraints.gridwidth = 1;
		
		timeBlockFields.add(buttonSampleStart, inputFieldConstraints);
		
		inputFieldConstraints.gridx = 0;
		inputFieldConstraints.gridy = 1;
		inputFieldConstraints.gridwidth = 1;
		
		timeBlockFields.add(endTimeLabel, inputFieldConstraints);
		
		inputFieldConstraints.gridx = 1;
		inputFieldConstraints.gridy = 1;
		inputFieldConstraints.gridwidth = 1;
		
		endTimeField.setEnabled(false);
		timeBlockFields.add(endTimeField, inputFieldConstraints);
		
		inputFieldConstraints.gridx = 2;
		inputFieldConstraints.gridy = 1;
		inputFieldConstraints.gridwidth = 1;
		
		timeBlockFields.add(buttonSampleEnd, inputFieldConstraints);
		
		inputFieldConstraints.gridx = 0;
		inputFieldConstraints.gridy = 2;
		inputFieldConstraints.gridwidth = 1;
		
		timeBlockFields.add(typeLabel, inputFieldConstraints);
		
		inputFieldConstraints.gridx = 1;
		inputFieldConstraints.gridy = 2;
		inputFieldConstraints.gridwidth = 1;
		
		typeField.setEnabled(false);
		timeBlockFields.add(typeField, inputFieldConstraints);
		
		inputFieldConstraints.gridx = 0;
		inputFieldConstraints.gridy = 3;
		inputFieldConstraints.gridwidth = 1;
		
		timeBlockFields.add(descriptionLabel, inputFieldConstraints);
		
		inputFieldConstraints.gridx = 1;
		inputFieldConstraints.gridy = 3;
		inputFieldConstraints.gridwidth = 1;
		
		descriptionField.setEnabled(false);
		timeBlockFields.add(descriptionField, inputFieldConstraints);
		
		inputFieldConstraints.gridx = 0;
		inputFieldConstraints.gridy = 4;
		inputFieldConstraints.gridwidth = 1;
		inputFieldConstraints.gridheight = 1;
		
		timeBlockFields.add(buttonAddTimestamp, inputFieldConstraints);
		
/*		inputFieldConstraints.gridx = 1;
		inputFieldConstraints.gridy = 4;
		inputFieldConstraints.gridwidth = 1;
		inputFieldConstraints.gridheight = 1;
		
		timeBlockFields.add(buttonDeleteSelected, inputFieldConstraints);*/
		
		// add border to timeBlockFields
		TitledBorder title;
		title = BorderFactory.createTitledBorder("Create Timestamp");
		title.setTitleJustification(TitledBorder.LEFT);
		timeBlockFields.setBorder(title);
		
		timeBlockConstraints.gridx = 0;
		timeBlockConstraints.gridy = 0;
		timeBlockConstraints.gridwidth = 1;
		timeBlockConstraints.gridheight = 1;
		
		timeBlockTab.add(timeBlockFields, timeBlockConstraints);
		
		//////////////////////////////////////////////////
		//////////////////////////////////////////////////
		
		//////////////////////////////////////////////////
		// add error log /////////////////////////////////
		errorLogLabel.setPreferredSize(new Dimension(timeBlockFields.getPreferredSize().width, 116));
		timeBlockConstraints.gridx = 0;
		timeBlockConstraints.gridy = 1;
		timeBlockConstraints.gridwidth = 1;
		timeBlockConstraints.gridheight = 1;
		
		errorLogLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		
		timeBlockTab.add(errorLogLabel, timeBlockConstraints);
		
		//////////////////////////////////////////////////
		//////////////////////////////////////////////////
		
		//////////////////////////////////////////////////
		// modify table panel ////////////////////////////
		
		JPanel modifyTableElementsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints modifyTableConstraints = new GridBagConstraints();
		modifyTableConstraints.insets = new Insets(5, 5, 5, 5);
		modifyTableConstraints.anchor = GridBagConstraints.WEST;
		
		modifyTableConstraints.gridx = 0;
		modifyTableConstraints.gridy = 0;
		
		modifyTableElementsPanel.add(buttonDeleteSelected, modifyTableConstraints);
		
		modifyTableConstraints.gridx = 0;
		modifyTableConstraints.gridy = 1;
		
		modifyTableElementsPanel.add(buttonUndoDelete, modifyTableConstraints);
		
		// add border to modifyTableElementsPanel
		title = BorderFactory.createTitledBorder("Remove Timestamps");
		title.setTitleJustification(TitledBorder.LEFT);
		modifyTableElementsPanel.setBorder(title);
		
		timeBlockConstraints.gridx = 1;
		timeBlockConstraints.gridy = 1;
		timeBlockConstraints.gridwidth = 1;
		timeBlockConstraints.gridheight = 1;
		
		timeBlockTab.add(modifyTableElementsPanel, timeBlockConstraints);
		
		//////////////////////////////////////////////////
		//////////////////////////////////////////////////
		
		//////////////////////////////////////////////////
		// Go To Selected Panel //////////////////////////
		
		JPanel goToSelectedPanel = new JPanel(new GridBagLayout());
		GridBagConstraints goToSelectedConstraints = new GridBagConstraints();
		goToSelectedConstraints.insets = new Insets(5, 5, 5, 5);
		goToSelectedConstraints.anchor = GridBagConstraints.WEST;
		
		goToSelectedConstraints.gridx = 0;
		goToSelectedConstraints.gridy = 0;
		
		goToSelectedPanel.add(convertedStartTime, goToSelectedConstraints);
		
		goToSelectedConstraints.gridx = 0;
		goToSelectedConstraints.gridy = 1;
		
		goToSelectedPanel.add(convertedEndTime, goToSelectedConstraints);
		
		goToSelectedConstraints.gridx = 0;
		goToSelectedConstraints.gridy = 2;
		
		goToSelectedPanel.add(buttonGoToStamp, goToSelectedConstraints);
		
		title = BorderFactory.createTitledBorder("Go To");
		title.setTitleJustification(TitledBorder.LEFT);
		goToSelectedPanel.setBorder(title);
		
		timeBlockConstraints.gridx = 2;
		timeBlockConstraints.gridy = 1;
		timeBlockConstraints.gridwidth = 1;
		timeBlockConstraints.gridheight = 1;
		
		timeBlockTab.add(goToSelectedPanel, timeBlockConstraints);
		
		//////////////////////////////////////////////////
		//////////////////////////////////////////////////
		
		//////////////////////////////////////////////////
		// Save or Load Panel ////////////////////////////
		
		JPanel saveLoadPanel = new JPanel(new GridBagLayout());
		GridBagConstraints saveLoadConstraints = new GridBagConstraints();
		saveLoadConstraints.insets = new Insets(5, 5, 5, 5);
		saveLoadConstraints.anchor = GridBagConstraints.WEST;
		
		saveLoadConstraints.gridx = 0;
		saveLoadConstraints.gridy = 0;
		
		saveLoadPanel.add(buttonSaveFile, saveLoadConstraints);
		
		saveLoadConstraints.gridx = 0;
		saveLoadConstraints.gridy = 1;
		
		saveLoadPanel.add(buttonLoadFile, saveLoadConstraints);
		
		title = BorderFactory.createTitledBorder("Load/Save File");
		title.setTitleJustification(TitledBorder.LEFT);
		saveLoadPanel.setBorder(title);
		
		timeBlockConstraints.gridx = 3;
		timeBlockConstraints.gridy = 1;
		timeBlockConstraints.gridwidth = 1;
		timeBlockConstraints.gridheight = 1;
		
		timeBlockTab.add(saveLoadPanel, timeBlockConstraints);
		
		//////////////////////////////////////////////////
		//////////////////////////////////////////////////
		
		//////////////////////////////////////////////////
		// Flow Timing Tab ///////////////////////////////
		
		JPanel flowTimingTab = new JPanel(new GridBagLayout());
		
		GridBagConstraints timelineTabConstraints = new GridBagConstraints();
		timelineTabConstraints.insets = new Insets(5, 5, 5, 5);
		timelineTabConstraints.anchor = GridBagConstraints.CENTER;
		
		timelineTabConstraints.gridx = 0;
		timelineTabConstraints.gridy = 1;
		timelineTabConstraints.gridwidth = 1;
		timelineTabConstraints.gridheight = 1;
		
		refreshTimeline();
		setTimelineColumnsWidth();
		
		JTableHeader header = timelineTable.getTableHeader();
		header.setDefaultRenderer(new HeaderRenderer(timelineTable));
		
		timelineTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		timelineScrollPane = new JScrollPane(timelineTable);
		timelineScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		timelineScrollPane.setPreferredSize(new Dimension(600, 208));
		
		flowTimingTab.add(timelineScrollPane, timelineTabConstraints);
		
		JPanel timelineButtons = new JPanel();
		timelineButtons.add(buttonTimelineStamp);
		timelineButtons.add(buttonTimelineRemove);
		timelineButtons.add(buttonTimelineRedo);
		
		timelineTabConstraints.gridx = 0;
		timelineTabConstraints.gridy = 0;
		timelineTabConstraints.gridwidth = 1;
		timelineTabConstraints.gridheight = 1;
		
		flowTimingTab.add(timelineButtons, timelineTabConstraints);
		
		//////////////////////////////////////////////////
		//////////////////////////////////////////////////
		
		// fit panels as tabs
		tabbedPane.addTab("Key Frames", timeBlockTab);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.addTab("Flow Timing", flowTimingTab);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		// add tabbed layout to main grid bag
		add(tabbedPane, macroConstraints);
		
		////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////

		/*constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		add(labelFileName, constraints);
		
		JPanel panelButtonOpen = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 5));
		panelButtonOpen.add(buttonOpen);
		
		constraints.gridwidth = 1;
		constraints.gridx = 6;//2
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
		constraints.gridwidth = 5;//////////////////
		add(timeSlider, constraints);
		constraints.gridwidth = 1;//////////////////
		
		constraints.gridx = 6;//2
		add(labelDuration, constraints);
		
		JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 5));
		//panelButtons.add(buttonOpen);
		panelButtons.add(buttonPlay);
		panelButtons.add(buttonPause);
		
		constraints.gridwidth = 7; //3
		constraints.gridx = 0;
		constraints.gridy = 2;
		add(panelButtons, constraints);
		
		JPanel panelPreview = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 5));
		
//		GridBagConstraints fileDisplayConstraints = new GridBagConstraints();
//		fileDisplayConstraints.insets = new Insets(5, 5, 5, 5);
//		fileDisplayConstraints.anchor = GridBagConstraints.EAST;
		
		// add TimeBlock preview panel
		refreshTable();
		setTableColumnsWidth();
		timeBlockTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		timeBlockScrollPane = new JScrollPane(timeBlockTable);
		panelPreview.add(timeBlockScrollPane);
		//timeBlockScrollPane.setPreferredSize();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridwidth = 1;
		constraints.gridx = 5;
		constraints.gridy = 3;
		//add(timeBlockScrollPane, constraints);
		add(panelPreview, constraints);
		
		// add table to preview panel
		
		
		// add text fields
		constraints.gridwidth = 1;
		constraints.gridx = 2;
		constraints.gridy = 3;
		add(startTimeField, constraints);*/
		
		buttonOpen.addActionListener(this);
		buttonPlay.addActionListener(this);
		buttonPause.addActionListener(this);
		buttonToggleLoop.addActionListener(this);
		buttonSkipForward.addActionListener(this);
		buttonSkipBackward.addActionListener(this);
		buttonForwardSecond.addActionListener(this);
		buttonBackwardSecond.addActionListener(this);
		buttonQuickPlay.addActionListener(this);
		buttonAddTimestamp.addActionListener(this);
		buttonSampleStart.addActionListener(this);
		buttonSampleEnd.addActionListener(this);
		buttonDeleteSelected.addActionListener(this);
		buttonUndoDelete.addActionListener(this);
		buttonGoToStamp.addActionListener(this);
		buttonSaveFile.addActionListener(this);
		buttonLoadFile.addActionListener(this);
		buttonTimelineStamp.addActionListener(this);
		buttonTimelineRemove.addActionListener(this);
		buttonTimelineRedo.addActionListener(this);
		
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
			else if(button == buttonAddTimestamp){
				try {
					createTimeBlock();
					errorLogLabel.setText("");
				} catch (InvalidInputException e) {
					errorLogLabel.setText("<html>" + e.getMessage() + "</html>");
					e.printStackTrace();
				}
			}
			else if(button == buttonSampleStart || button == buttonSampleEnd){
				sampleTime(button);
			}
			else if(button == buttonDeleteSelected){
				deleteTimestamp();
			}
			else if(button == buttonUndoDelete){
				undoDelete();
			}
			else if(button == buttonGoToStamp){
				goToSelected();
			}
			else if(button == buttonSaveFile){
				saveFile();
			}
			else if(button == buttonLoadFile){
				loadFile();
			}
			else if(button == buttonTimelineStamp){
				try {
					createTimelineTime();
				} catch (InvalidInputException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(button == buttonTimelineRemove){
				deleteFromTimeline();
			}
			else if(button == buttonTimelineRedo){
				undoDeleteFromTimeline();
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
			buttonSampleStart.setEnabled(true);
			buttonSampleEnd.setEnabled(true);
			buttonDeleteSelected.setEnabled(true);
			buttonAddTimestamp.setEnabled(true);
			buttonUndoDelete.setEnabled(true);
			buttonGoToStamp.setEnabled(true);
			buttonSaveFile.setEnabled(true);
			buttonLoadFile.setEnabled(true);
			buttonTimelineStamp.setEnabled(true);
			buttonTimelineRemove.setEnabled(true);
			buttonTimelineRedo.setEnabled(true);
			
			startTimeField.setEnabled(true);
			endTimeField.setEnabled(true);
			typeField.setEnabled(true);
			descriptionField.setEnabled(true);
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
					
					timingController = new TimingController(player);//////////////////////
					
					String croppedPath = cropString(audioFilePath);
					
					labelFileName.setText("Playing File: " + croppedPath);
					timeSlider.setMaximum((int) player.getClipSecondLength() * 10);
					
					labelDuration.setText(player.getClipLengthString());
					player.play();
					
					resetControls();

				} catch (UnsupportedAudioFileException ex) {
					JOptionPane.showMessageDialog(testView.this,  
							"The audio format is unsupported!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				} catch (LineUnavailableException ex) {
					JOptionPane.showMessageDialog(testView.this,  
							"Could not play the audio file because line is unavailable!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(testView.this,  
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
	
	private void createTimeBlock() throws InvalidInputException{
		
		Long startTime = null;
		Long endTime = null;
		Integer type = null;
		String description = null;
		try{
			startTime = Long.valueOf(startTimeField.getText());
			endTime = Long.valueOf(endTimeField.getText());
			type = Integer.valueOf(typeField.getText());
			description = descriptionField.getText();
		} catch(Exception e){
			errorLogLabel.setText("<html>Start time, end time and type must be numbers.</html>");
		}
		
		timingController.createTimeBlock(startTime, endTime, type, description);
		refreshTable();
		startTimeField.setText("");
		endTimeField.setText("");
		typeField.setText("");
		descriptionField.setText("");
	}
	
	private void createTimelineTime() throws InvalidInputException{
		
		long time = timeSlider.getValue() * 100;
		
		timingController.addTime(time);
		refreshTimeline();
		setTimelineColumnsWidth();
	}
	
	private void sampleTime(JButton button){
		int time = 0;
		if(button.equals(buttonSampleStart)){
			time = timeSlider.getValue() * 100;
			startTimeField.setText(Integer.toString(time));
		}
		else if(button.equals(buttonSampleEnd)){
			time = timeSlider.getValue() * 100;
			endTimeField.setText(Integer.toString(time));
		}
	}
	
	private void goToSelected(){
		timeSlider.setValue((int)targetTime / 100);
	}
	
	public void deleteTimestamp(){
		int row = timeBlockTable.getSelectedRow();
		tbInMemory = timeBlocks.get(row);
		timeBlocks.remove(row);
		refreshTable();
	}
	
	public void undoDelete(){
		if(tbInMemory != null){
			timeBlocks.add(tbInMemory);
			refreshTable();
		}
	}
	
	public void deleteFromTimeline(){
		int row = timelineTable.getSelectedRow();
		selectedTime = timeline.get(row);
		timeline.remove(row);
		refreshTimeline();
		setTimelineColumnsWidth();
	}
	
	public void undoDeleteFromTimeline(){
		if(selectedTime != null){
			timeline.add(selectedTime);
			refreshTimeline();
			setTimelineColumnsWidth();
		}
	}
	
	private void refreshTable(){
		AudioTool master = AudioTool.getInstance();
		timeBlocks = (ArrayList)master.getTimeBlocks();
		
		startTimes.clear();
		endTimes.clear();
		types.clear();
		descriptions.clear();
		
		DefaultTableModel model = new DefaultTableModel(0, 4) ;
		model.setColumnIdentifiers(master.getPreviewLabels());
		timeBlockTable.setModel(model);
		
		if(timeBlocks.size() == 0){
			model.setColumnIdentifiers(master.getPreviewLabels());
			timeBlockTable.setModel(model);
		}
		else if(timeBlocks.size() > 0){
			for(int i = 0; i < timeBlocks.size(); i++){
				startTimes.add(i, timeBlocks.get(i).getStartTime() + "");
				endTimes.add(i, timeBlocks.get(i).getEndTime() + "");
				types.add(i, timeBlocks.get(i).getType() + "");
				descriptions.add(i, timeBlocks.get(i).getDescription() + "");
			}
			
			String[][] data = {startTimes.toArray(new String[startTimes.size()]), endTimes.toArray(new String[endTimes.size()]), 
					types.toArray(new String[types.size()]), descriptions.toArray(new String[descriptions.size()])};
			
			String[][] treatedData = new String[timeBlocks.size()][data.length];
			
			for(int i = 0; i < timeBlocks.size(); i++){
				for(int j = 0; j < data.length; j++){
					treatedData[i][j] = data[j][i];
				}
			}
			
			model.setDataVector(treatedData, master.getPreviewLabels());
			setTableColumnsWidth();
			timeBlockTable.setModel(model);
		}
		setTableColumnsWidth();
		timeBlockTable.setFillsViewportHeight(true);
		
	}
	
	public void refreshTimeline(){
		String[] identifier = {"Time Stamps"};
		AudioTool master = AudioTool.getInstance();
		timeline = (ArrayList)master.getTimeline();
		
		String treatedTimeline[] = new String[timeline.size()];
		for(int i = 0; i < timeline.size(); i++){
			treatedTimeline[i] = timeline.get(i) + "";
		}
		
		DefaultTableModel model = new DefaultTableModel(0, 1) ;
		model.setColumnIdentifiers(identifier);
		
		String[][] data = {treatedTimeline};
		String[][] treatedData = new String[timeline.size()][data.length];
		for(int i = 0; i < timeline.size(); i++){
			for(int j = 0; j < data.length; j++){
				treatedData[i][j] = data[j][i];
			}
		}
		model.setDataVector(treatedData, identifier);
		
		timelineTable.setModel(model);
		timelineTable.setFillsViewportHeight(true);
	}
	
	public void setTableColumnsWidth(){
		final TableColumnModel columnModel = timeBlockTable.getColumnModel();
		for(int i = 0; i < timeBlockTable.getColumnCount(); i++){
			if(i == timeBlockTable.getColumnCount() - 1){
				columnModel.getColumn(i).setPreferredWidth(300);
			}
			else{
				columnModel.getColumn(i).setPreferredWidth(100);
			}
		}		
	}
	
	public void setTimelineColumnsWidth(){
		final TableColumnModel columnModel = timelineTable.getColumnModel();
		for(int i = 0; i < timelineTable.getColumnCount(); i++){
			columnModel.getColumn(i).setPreferredWidth(600);
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
	
	private void loadFile() {
		JFileChooser fileChooser = null;
		
		if (xmlLastOpenPath != null && !xmlLastOpenPath.equals("")) {
			fileChooser = new JFileChooser(xmlLastOpenPath);
		} else {
			fileChooser = new JFileChooser();
		}
		
		FileFilter wavFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "XML file (*.xml)";
			}

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					return file.getName().toLowerCase().endsWith(".xml");
				}
			}
		};

		
		fileChooser.setFileFilter(wavFilter);
		fileChooser.setDialogTitle("Open Timestamp File");
		fileChooser.setAcceptAllFileFilterUsed(false);

		int userChoice = fileChooser.showOpenDialog(this);
		if (userChoice == JFileChooser.APPROVE_OPTION) {
			xmlFilePath = fileChooser.getSelectedFile().getAbsolutePath();
			xmlLastOpenPath = fileChooser.getSelectedFile().getParent();
			Persistence.loadModel(xmlFilePath);
			refreshTable();
			tbInMemory = null;
		}
	}
	
	private void saveFile() {
		JFileChooser fileChooser = null;
		
		if (xmlLastOpenPath != null && !xmlLastOpenPath.equals("")) {
			fileChooser = new JFileChooser(xmlLastOpenPath);
		} else {
			fileChooser = new JFileChooser();
		}
		
		FileFilter wavFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "XML file (*.xml)";
			}

			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					return file.getName().toLowerCase().endsWith(".xml");
				}
			}
		};

		
		fileChooser.setFileFilter(wavFilter);
		fileChooser.setDialogTitle("Save Timestamp File");
		fileChooser.setAcceptAllFileFilterUsed(false);

		int userChoice = fileChooser.showSaveDialog(this);
		if (userChoice == JFileChooser.APPROVE_OPTION) {
			xmlFilePath = fileChooser.getSelectedFile().getAbsolutePath();
			xmlLastOpenPath = fileChooser.getSelectedFile().getParent();
			try {
				String path = fileChooser.getSelectedFile().getAbsolutePath();
				String filename = fileChooser.getSelectedFile().getName();
				File tempFile = new File("data.xml");
				File newFile = new File(path + ".xml");

				FileInputStream fis = new FileInputStream(tempFile);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
		 
				FileWriter fstream = new FileWriter(newFile, true);
				BufferedWriter out = new BufferedWriter(fstream);
		 
				String aLine = null;
				while ((aLine = in.readLine()) != null) {
					//Process each line and add output to Dest.txt file
					out.write(aLine);
					out.newLine();
				}
		 
				// do not forget to close the buffer reader
				in.close();
		 
				// close buffer writer
				out.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
