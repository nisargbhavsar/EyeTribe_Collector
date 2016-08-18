package Collector;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.GazeManager.ApiVersion;
import com.theeyetribe.client.GazeManager.ClientMode;

import javax.swing.*;

public class EyeTest extends JFrame {

	private static final long serialVersionUID = 1L;

	// Declare variables
	private GazeManager gm = GazeManager.getInstance();
	private static GazeListener gazeListener;
	private JButton pause;
	private JButton resume;
	static JButton startStreaming;
	private JButton stopStreaming;
	private JButton changeDirectory;
	private HintTextField trialNumber;
	private static int trial = 1;
	private static boolean isDirectoryChanged = false;
	private static JFileChooser fc;
	private JTextArea[] eyeData = new JTextArea[2];
	private static ArrayList<String> streamData = new ArrayList<String>();
	private static FileWriter fileWriter = null;

	// Establish connection to local eye tribe server
	private boolean isConnect = gm.activate(ApiVersion.VERSION_1_0,
			ClientMode.PUSH);

	public EyeTest() {
		super("Eye Tribe Data Collector");
		initiateUI();
	}

	/**
	 * A method for initializing UI
	 */
	private void initiateUI() {
		this.setResizable(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(300, 350);

		// eyeData[0] = left eye
		// eyeData[1] = right eye
		for (int i = 0; i < 2; i++) {
			eyeData[i] = new JTextArea();
			eyeData[i].setEditable(false);
			eyeData[i].setOpaque(false);
		}
		eyeData[0].setBounds(10, 10, 600, 100);
		eyeData[1].setBounds(10, 80, 600, 50);

		pause = new JButton("Pause Tracking");
		pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gazeListener.pause();
			}
		});
		pause.setBounds(10, 130, 140, 50);

		resume = new JButton("Resume Tracking");
		resume.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gazeListener.resume();
			}
		});
		resume.setBounds(150, 130, 140, 50);

		startStreaming = new JButton("Start Streaming");
		startStreaming.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GazeListener.startStreaming();
				startStreaming.setBackground(Color.GREEN);
				startStreaming.setText("Streaming...");
			}
		});
		startStreaming.setBounds(10, 190, 140, 50);

		stopStreaming = new JButton("Stop Streaming");
		stopStreaming.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				streamData = gazeListener.stopStreaming();
				startStreaming.setText("Start Streaming");
				startStreaming.setBackground(Color.RED);
				try {
					// Check if there is data in the stream data ArrayList
					if (streamData.size() == 0) {
						return;
					}
					File dir;
					// Check if there is data in the stream data ArrayList
					if (streamData.size() == 0) {
						trial--;
						return;
					}
					// Create new directory if it does not exist
					if (!isDirectoryChanged) {
						dir = new File(System.getProperty("user.dir")
								+ "/Eye Tribe Data");
					} else {
						dir = new File(fc.getSelectedFile().toString()
								+ "/Eye Tribe Data");
					}

					if (!dir.exists())
						dir.mkdir();

					File file = new File(dir, getTrialNumber());
					fileWriter = new FileWriter(file);

					// Algorithm for producing meaningful streaming data
					// and write them into text files
					String temp = "";
					temp += "Left Eye Coordinates in Pixels		Right Eye Coordinates in Pixels		Timestamp\n";
					for (int i = 0; i < streamData.size() - 4; i = i + 3) {
						temp += String.format("%-25s%19s%41sms\n",
								streamData.get(i), streamData.get(i + 1),
								streamData.get(i + 2));
					}
					fileWriter.write(temp);
					gazeListener.stopStreaming().clear();
					trial++;
				} catch (IOException exception) {
				} finally {
					try {
						fileWriter.close();
					} catch (IOException exception) {
					}
				}
			}
		});
		stopStreaming.setBounds(150, 190, 140, 50);

		changeDirectory = new JButton("Change Directory");
		changeDirectory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				isDirectoryChanged = true;
				// Get custom file directory
				fc = new JFileChooser();
				fc.setCurrentDirectory(new java.io.File("."));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(null,
							"Stream Data will be saved to "
									+ fc.getSelectedFile().toString()
									+ "/Eye Tribe Data", "Directory Changed",
							JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		changeDirectory.setBounds(10, 250, 140, 50);

		trialNumber = new HintTextField("Change Trial #");
		trialNumber.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (Integer.parseInt(trialNumber.getText()) > 0)
						trial = Integer.parseInt(trialNumber.getText());
					else
						JOptionPane.showMessageDialog(null, "Invalid Number",
								"Error", JOptionPane.PLAIN_MESSAGE);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, "Invalid Number",
							"Error", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		trialNumber.setBounds(150, 250, 140, 50);

		if (!isConnect) {
			eyeData[0].setText("Server Not Set Up, Please Run EyeTribe Server Setup");
		}

		// Add widget to screen
		this.setLayout(null);
		this.add(eyeData[0]);
		this.add(eyeData[1]);
		this.add(pause);
		this.add(resume);
		this.add(startStreaming);
		this.add(stopStreaming);
		this.add(changeDirectory);
		this.add(trialNumber);
		// Initialize GazeListener and add listener to Gaze Manager
		gazeListener = new GazeListener(eyeData);
		gm.addGazeListener(gazeListener);

		// Set everything visible
		setVisible(true);
	}

//	public static void main(String[] args) {
//		new EyeTest();
//	}

	public static String getTrialNumber() {
		return "Trial_" + trial + ".txt";
	}

	public static void saveFile() {
		streamData = gazeListener.stopStreaming();
		startStreaming.setText("Start Streaming");
		try {
			// Check if there is data in the stream data ArrayList
			if (streamData.size() == 0) {
				return;
			}
			File dir;
			// Check if there is data in the stream data ArrayList
			if (streamData.size() == 0) {
				trial--;
				return;
			}
			// Create new directory if it does not exist
			if (!isDirectoryChanged) {
				dir = new File(System.getProperty("user.dir")
						+ "/Eye Tribe Data");
			} else {
				dir = new File(fc.getSelectedFile().toString()
						+ "/Eye Tribe Data");
			}

			if (!dir.exists())
				dir.mkdir();

			File file = new File(dir, getTrialNumber());
			fileWriter = new FileWriter(file);

			// Algorithm for producing meaningful streaming data
			// and write them into text files
			String temp = "";
			temp += "Left Eye Coordinates in Pixels		Right Eye Coordinates in Pixels		Timestamp\n";
			for (int i = 0; i < streamData.size() - 4; i = i + 3) {
				temp += String.format("%-25s%19s%41sms\n", streamData.get(i),
						streamData.get(i + 1), streamData.get(i + 2));
			}
			fileWriter.write(temp);
			gazeListener.stopStreaming().clear();
			trial++;
		} catch (IOException exception) {
			System.out.println("Error with file writing.");
		} finally {
			try {
				fileWriter.close();
			} catch (IOException exception) {
				System.out.println("Error with file writing.");
			}
		}
	}
}

class HintTextField extends JTextField implements FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String hint;
	private boolean showingHint;

	public HintTextField(final String hint) {
		super(hint);
		this.hint = hint;
		this.showingHint = true;
		super.addFocusListener(this);
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText("");
			showingHint = false;
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText(hint);
			showingHint = true;
		}
	}

	@Override
	public String getText() {
		return showingHint ? "" : super.getText();
	}
}
