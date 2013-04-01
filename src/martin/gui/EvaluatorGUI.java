/*******************************************************************************
 * Copyright (c) 2013 Martin Marinov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Martin - initial API and implementation
 ******************************************************************************/
package martin.gui;

import java.awt.Dimension;

import javax.swing.JFrame;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;

import martin.math.Complex;
import martin.quantum.McalcDescription;
import martin.quantum.SimulationRunner;
import martin.quantum.SystemMatrix;
import martin.quantum.tools.Tools;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class EvaluatorGUI {
	
	private final static String PROG_TITLE = "MBQC Simulator alpha";
	
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
	private JTextPane outConsole;
	private JScrollPane scrollPane_2;
	private JButton btnClear;
	private JLabel lblConsole;
	private JFileChooser fileChooser = new JFileChooser(".");

	/**
	 * Create the application.
	 */
	public EvaluatorGUI() {
		initialize();
	}

	public void makeVisible() {
		frmMbqcSimulatorValpha.setVisible(true);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		Tools.SIMPLIFICATION_ENABLED = true;
		
		frmMbqcSimulatorValpha = new JFrame();
		frmMbqcSimulatorValpha.setTitle(PROG_TITLE);
		frmMbqcSimulatorValpha.setBounds(100, 100, 933, 618);
		frmMbqcSimulatorValpha.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frmMbqcSimulatorValpha.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Qubits are indexed from 1. First few qubits are always inputs.");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(145, 5, 431, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblNewLabel);
		
		txtNumbOfQubits = new JTextField();
		txtNumbOfQubits.setBounds(145, 25, 233, 20);
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
		txtBranches.setToolTipText("\"1, 0, 1\" to represent b0 = 1, b1 = 0, b2 = 1... otherwise use \"(2, 0); (4, 1)\" to represent b2 = 0, b4 = 1");
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
					final HashMap<String, Complex> rules = SimulationRunner.parseVariablesAndValues(res);
					
					final long stop = System.currentTimeMillis();
					final long afterused = runtime.totalMemory() - runtime.freeMemory();
					
					Tools.logger.printf("Evaluated in %dms,  used %.4f MB\n", stop - start, (afterused - intused) / 1048576d);
					
					outNumerical.setText(system.printValues(rules, true)+"\nProbability: "+system.getQuickProbability(rules));					
					
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
				Tools.SIMPLIFICATION_ENABLED = chckbxSimplificationEnabled.isSelected();
			}
		});
		chckbxSimplificationEnabled.setSelected(Tools.SIMPLIFICATION_ENABLED);
		chckbxSimplificationEnabled.setBounds(421, 181, 155, 23);
		frmMbqcSimulatorValpha.getContentPane().add(chckbxSimplificationEnabled);
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_2.setBounds(601, 54, 306, 481);
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
		lblConsole.setBounds(603, 28, 206, 14);
		frmMbqcSimulatorValpha.getContentPane().add(lblConsole);
		
		btnOpenFile = new JButton("Open file");
		btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				
				final int returnVal = fileChooser.showOpenDialog(frmMbqcSimulatorValpha);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						final File f = fileChooser.getSelectedFile();
						final McalcDescription desc = new McalcDescription(f);
						populateFrom(desc);
						
						frmMbqcSimulatorValpha.setTitle(PROG_TITLE+" - "+f.getName());
					} catch (Exception e) {
						e.printStackTrace(Tools.logger);
					}
				}
			}
		});
		btnOpenFile.setBounds(388, 24, 89, 23);
		frmMbqcSimulatorValpha.getContentPane().add(btnOpenFile);
		
		btnSaveFile = new JButton("Save file");
		btnSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				final int returnVal = fileChooser.showSaveDialog(frmMbqcSimulatorValpha);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = fileChooser.getSelectedFile();
					if (f.exists() && 
							JOptionPane.showConfirmDialog(
									frmMbqcSimulatorValpha, 
									"File '"+f.getName()+"' already exists. Overwrite?",
									"Overwriting file", 
									JOptionPane.YES_NO_OPTION, 
									JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
						return;
					
					try {
						final McalcDescription desc = generateMcalcDesc();
						desc.saveToFile(fileChooser.getSelectedFile());
						frmMbqcSimulatorValpha.setTitle(PROG_TITLE+" - "+f.getName());
					} catch (Exception e) {
						e.printStackTrace(Tools.logger);
					}
				}
			}
		});
		btnSaveFile.setBounds(487, 24, 89, 23);
		frmMbqcSimulatorValpha.getContentPane().add(btnSaveFile);
		
	}
	
	private void generateSystemMatrix() throws NumberFormatException, Exception {
		final long intused = runtime.totalMemory() - runtime.freeMemory();
		final long start = System.currentTimeMillis();
		
		system = SimulationRunner.run( new McalcDescription(
				Integer.parseInt(txtNumbOfQubits.getText()),
				txtInputs.getText(),
				txtEntanglement.getText(),
				txtMeasurement.getText(),
				txtCorrections.getText(),
				txtBranches.getText()
				)
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
	
	public void populateFrom(final McalcDescription desc) {
		
		if (desc == null) return;

		txtNumbOfQubits.setText(String.valueOf(desc.n));
		txtBranches.setText(desc.branches);
		txtCorrections.setText(desc.corrections);
		txtEntanglement.setText(desc.entanglement);
		txtInputs.setText(desc.inputs);
		txtMeasurement.setText(desc.measurements);
		txtVariables.setText(desc.variables);

	}
	
	private McalcDescription generateMcalcDesc() {
		
		return new McalcDescription(
				Integer.valueOf(txtNumbOfQubits.getText()), 
				txtInputs.getText(), txtEntanglement.getText(), txtMeasurement.getText(), txtCorrections.getText(), txtBranches.getText(), txtVariables.getText());
		
	}
	
	final PrintLogger logger = new PrintLogger();
	private JButton btnOpenFile;
	private JButton btnSaveFile;
	
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
			if (!Tools.FORCE_SYSTEM_LOGGER) Tools.logger = this;
		}
		
	}
}
