package martin.gui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;

import martin.math.MathExpression;
import martin.quantum.SimulationRunner;
import martin.quantum.SystemMatrix;
import martin.quantum.tools.Tools;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class MainGUI {
	
	private static final String DEFAULT_no_of_qubits = 	"3";
	private static final String DEFAULT_inputs =		"a, b";
	private static final String DEFAULT_entanglement =	"(0, 1); (1, 2)";
	private static final String DEFAULT_measurements =	"(0, 0, 0, 0); (1, s0, 0, 0)";
	private static final String DEFAULT_corrections =	"(2, z, s0); (2, x, s1)";//"(2, x, s1); ";
	private static final String DEFAULT_branches =		"1, 1";
	private static final String DEFAULT_variables =		"a = 12 + Im(3); b = 3 + Im(4)";
	
	private final Properties p = new Properties();
	private final static String config_filename = "mbqc.config";
	
	private static final Runtime runtime = Runtime.getRuntime();

	private JFrame frmMbqcSimulatorValpha;
	private JTextField txtInputs;
	private JTextField txtNumbOfQubits;
	private JLabel lblEntanglement;
	private JTextField txtEntanglement;
	private JTextField txtMeasurement;
	private JLabel lblMeasurements;
	private JTextField txtCorrections;
	private JLabel lblCorrections;
	private JLabel lblBranches;
	private JTextField txtBranches;
	private JButton btnGeneratePattern;
	private JLabel lblResult;
	private JTextField txtVariables;
	private JTextPane outSymbolic;
	private SystemMatrix system = null;
	private JTextPane outNumerical;
	private JCheckBox chckbxSimplificationEnabled;
	private JButton btnResetFields;
	private JTextPane outConsole;
	private JScrollPane scrollPane_2;
	private JButton btnClear;
	private JLabel lblConsole;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI window = new MainGUI();
					window.frmMbqcSimulatorValpha.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		MathExpression.simplify = true;
		
		frmMbqcSimulatorValpha = new JFrame();
		frmMbqcSimulatorValpha.setTitle("MBQC Simulator v0");
		frmMbqcSimulatorValpha.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent paramWindowEvent) {
				saveProperties();
			}
		});
		frmMbqcSimulatorValpha.setBounds(100, 100, 933, 618);
		frmMbqcSimulatorValpha.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMbqcSimulatorValpha.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Qubits are indexed from 0. First few qubits are always inputs.");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(145, 5, 431, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblNewLabel);
		
		txtNumbOfQubits = new JTextField();
		txtNumbOfQubits.setBounds(145, 25, 431, 20);
		txtNumbOfQubits.setToolTipText("Integer representing the number of qubits in the system");
		frmMbqcSimulatorValpha.getContentPane().add(txtNumbOfQubits);
		txtNumbOfQubits.setColumns(10);
		
		JLabel lblInitialization = new JLabel("Inputs");
		lblInitialization.setHorizontalAlignment(SwingConstants.TRAILING);
		lblInitialization.setBounds(57, 54, 78, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblInitialization);
		
		txtInputs = new JTextField();
		txtInputs.setBounds(145, 51, 431, 20);
		txtInputs.setToolTipText("input like \"a, b, c,..., z\" would be interpreted as \"a|...00>+b|...01>+c|...10> +... z|...>\"\r\nwhere we iterate over the first log2of(number of coeff) input qubits");
		frmMbqcSimulatorValpha.getContentPane().add(txtInputs);
		txtInputs.setColumns(10);
		
		lblEntanglement = new JLabel("Entanglement");
		lblEntanglement.setHorizontalAlignment(SwingConstants.TRAILING);
		lblEntanglement.setBounds(22, 80, 113, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblEntanglement);
		
		txtEntanglement = new JTextField();
		txtEntanglement.setBounds(145, 77, 431, 20);
		txtEntanglement.setToolTipText("Entanglement commands of the form (id1, id2); (id3, id4) would mean entangle qubit id1 with qubit id2, then entangle id3 with id4");
		frmMbqcSimulatorValpha.getContentPane().add(txtEntanglement);
		txtEntanglement.setColumns(10);
		
		lblMeasurements = new JLabel("Measurements");
		lblMeasurements.setHorizontalAlignment(SwingConstants.TRAILING);
		lblMeasurements.setBounds(18, 106, 117, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblMeasurements);
		
		txtMeasurement = new JTextField();
		txtMeasurement.setBounds(145, 103, 431, 20);
		txtMeasurement.setToolTipText("Measurement commands of the form \"(qubitid, s, t, alpha); (qubit2, p, q, beta)\". s and t could be of the form si+sj, etc. Each measurment is in brackets");
		frmMbqcSimulatorValpha.getContentPane().add(txtMeasurement);
		txtMeasurement.setColumns(10);
		
		lblCorrections = new JLabel("Corrections");
		lblCorrections.setHorizontalAlignment(SwingConstants.TRAILING);
		lblCorrections.setBounds(33, 132, 102, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblCorrections);
		
		txtCorrections = new JTextField();
		txtCorrections.setBounds(145, 129, 431, 20);
		txtCorrections.setToolTipText("Corrections of the form \"(qubitid, M, s); (qubit, M, s)\" where you should put M to be x for x correction or z for z correction");
		frmMbqcSimulatorValpha.getContentPane().add(txtCorrections);
		txtCorrections.setColumns(10);
		
		lblBranches = new JLabel("Branches");
		lblBranches.setHorizontalAlignment(SwingConstants.TRAILING);
		lblBranches.setBounds(44, 158, 91, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblBranches);
		
		txtBranches = new JTextField();
		txtBranches.setBounds(145, 155, 431, 20);
		txtBranches.setToolTipText("Comma seperated values like \"1, 0, 1\"");
		frmMbqcSimulatorValpha.getContentPane().add(txtBranches);
		txtBranches.setColumns(10);
		
		btnGeneratePattern = new JButton("Generate pattern");
		btnGeneratePattern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					generateSystemMatrix();
				} catch (Exception e) {
					throwException(e);
				}
			}
		});
		btnGeneratePattern.setBounds(145, 181, 270, 23);
		frmMbqcSimulatorValpha.getContentPane().add(btnGeneratePattern);
		
		lblResult = new JLabel("Symbolic result");
		lblResult.setHorizontalAlignment(SwingConstants.TRAILING);
		lblResult.setBounds(-20, 214, 155, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblResult);
		
		JLabel lblTotalNumberOf = new JLabel("Total number of qubits");
		lblTotalNumberOf.setHorizontalAlignment(SwingConstants.TRAILING);
		lblTotalNumberOf.setBounds(-20, 28, 155, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblTotalNumberOf);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(145, 215, 431, 152);
		frmMbqcSimulatorValpha.getContentPane().add(scrollPane);
		
		outSymbolic = new JTextPane();
		outSymbolic.setEditable(false);
		scrollPane.setViewportView(outSymbolic);
		
		JLabel lblVriables = new JLabel("Vriables");
		lblVriables.setHorizontalAlignment(SwingConstants.TRAILING);
		lblVriables.setBounds(41, 381, 93, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblVriables);
		
		txtVariables = new JTextField();
		txtVariables.setToolTipText("of the form \"a = 12 + Im(3); b = 3 + Im(4)\", etc");
		txtVariables.setBounds(145, 378, 431, 20);
		frmMbqcSimulatorValpha.getContentPane().add(txtVariables);
		txtVariables.setColumns(10);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(144, 446, 431, 122);
		frmMbqcSimulatorValpha.getContentPane().add(scrollPane_1);
		
		outNumerical = new JTextPane();
		outNumerical.setEditable(false);
		scrollPane_1.setViewportView(outNumerical);
		
		JLabel lblSymbolicResult = new JLabel("Result");
		lblSymbolicResult.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSymbolicResult.setBounds(-17, 443, 151, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblSymbolicResult);
		
		JButton btnEvaluate = new JButton("Evaluate all of the expressions above");
		btnEvaluate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				try {
					if (system == null)
						generateSystemMatrix();
						
					final long intused = runtime.totalMemory() - runtime.freeMemory();
					final long start = System.currentTimeMillis();
					
					final String res = txtVariables.getText();
					
					final long stop = System.currentTimeMillis();
					final long afterused = runtime.totalMemory() - runtime.freeMemory();
					
					Tools.logger.printf("Evaluated in %dms,  used %.4f MB\n", stop - start, (afterused - intused) / 1048576d);
					
					outNumerical.setText(system.printValues(SimulationRunner.parseVariablesAndValues(res)));
					
				} catch (Exception e) {
					throwException(e);
				}
			}
		});
		btnEvaluate.setBounds(144, 409, 431, 23);
		frmMbqcSimulatorValpha.getContentPane().add(btnEvaluate);
		
		chckbxSimplificationEnabled = new JCheckBox("Simplification enabled");
		chckbxSimplificationEnabled.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent paramChangeEvent) {
				MathExpression.simplify = chckbxSimplificationEnabled.isSelected();
			}
		});
		chckbxSimplificationEnabled.setSelected(MathExpression.simplify);
		chckbxSimplificationEnabled.setBounds(421, 181, 155, 23);
		frmMbqcSimulatorValpha.getContentPane().add(chckbxSimplificationEnabled);
		
		btnResetFields = new JButton("Reset to teleportation example");
		btnResetFields.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				setDefaults();
			}
		});
		btnResetFields.setBounds(666, 5, 241, 23);
		frmMbqcSimulatorValpha.getContentPane().add(btnResetFields);
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_2.setBounds(601, 80, 306, 455);
		frmMbqcSimulatorValpha.getContentPane().add(scrollPane_2);
		
		outConsole = new JTextPane() {
			private static final long serialVersionUID = 957697424997673833L;

			public void setBounds(int x, int y,int width, int height)
			{
				Dimension size = this.getPreferredSize();
				super.setBounds(x
						,y
						,Math.max(size.width, width)
						,height
						);
			}
		};
		outConsole.setEditable(false);
		scrollPane_2.setViewportView(outConsole);
		
		btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				outConsole.setText("");
			}
		});
		btnClear.setBounds(601, 545, 306, 23);
		frmMbqcSimulatorValpha.getContentPane().add(btnClear);
		
		lblConsole = new JLabel("Debug messages:");
		lblConsole.setBounds(602, 54, 206, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblConsole);
		
		loadProperties();
	}
	
	private void generateSystemMatrix() throws NumberFormatException, Exception {
		saveProperties();

		final long intused = runtime.totalMemory() - runtime.freeMemory();
		final long start = System.currentTimeMillis();
		
		system = SimulationRunner.run(
				Integer.parseInt(txtNumbOfQubits.getText()),
				txtInputs.getText(),
				txtEntanglement.getText(),
				txtMeasurement.getText(),
				txtCorrections.getText(),
				txtBranches.getText()
				);
		
		final long stop = System.currentTimeMillis();
		final long afterused = runtime.totalMemory() - runtime.freeMemory();
		
		Tools.logger.printf("Generated in %dms,  used %.4f MB\n", stop - start, (afterused - intused) / 1048576d);
		
		outSymbolic.setText(system.toString());
		
		
	}
	
	private void throwException(final Exception e) {
		JOptionPane.showMessageDialog(frmMbqcSimulatorValpha, e.getMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
		e.printStackTrace(Tools.logger);
	}
	
	private void setDefaults() {
		txtNumbOfQubits.setText(DEFAULT_no_of_qubits);
		txtBranches.setText(DEFAULT_branches);
		txtCorrections.setText(DEFAULT_corrections);
		txtEntanglement.setText(DEFAULT_entanglement);
		txtInputs.setText(DEFAULT_inputs);
		txtMeasurement.setText(DEFAULT_measurements);
		txtVariables.setText(DEFAULT_variables);
	}
	
	private void loadProperties() {
		try {
			final FileInputStream in = new FileInputStream(config_filename);
			p.load(in);
			in.close();
			
			txtNumbOfQubits.setText(p.getProperty("prop_no", DEFAULT_no_of_qubits));
			txtBranches.setText(p.getProperty("prop_branch", DEFAULT_branches));
			txtCorrections.setText(p.getProperty("prop_corr", DEFAULT_corrections));
			txtEntanglement.setText(p.getProperty("prop_ent", DEFAULT_entanglement));
			txtInputs.setText(p.getProperty("prop_in", DEFAULT_inputs));
			txtMeasurement.setText(p.getProperty("prop_meas", DEFAULT_measurements));
			txtVariables.setText(p.getProperty("prop_vars", DEFAULT_variables));
		} catch (Exception e) {
			setDefaults();
		}
	}
	
	private void saveProperties() {
		
		try {
			p.setProperty("prop_no", txtNumbOfQubits.getText());
			p.setProperty("prop_branch", txtBranches.getText());
			p.setProperty("prop_corr", txtCorrections.getText());
			p.setProperty("prop_ent", txtEntanglement.getText());
			p.setProperty("prop_in", txtInputs.getText());
			p.setProperty("prop_meas", txtMeasurement.getText());
			p.setProperty("prop_vars", txtVariables.getText());
			
			
			final FileOutputStream out = new FileOutputStream(config_filename);
			p.store(out, "---No Comment---");
			out.close();
		} catch (Exception e) {}
		
	}
	
	final PrintLogger logger = new PrintLogger();
	
	private class PrintLogger extends PrintStream {

		public PrintLogger() {
			super(new OutputStream() {
				
				final SimpleAttributeSet keyWord = new SimpleAttributeSet();
				
				@Override
				public void write(int paramInt) throws IOException {
					StyledDocument doc = outConsole.getStyledDocument();
					try {
						doc.insertString(doc.getLength(), ((char) paramInt)+"", keyWord);
					} catch (BadLocationException e) {
						e.printStackTrace(Tools.logger);
					}
				}
			});
			Tools.logger = this;
		}
		
	}
}
