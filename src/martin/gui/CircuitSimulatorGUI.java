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

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import martin.translatortest.TranslatorRunner;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Scanner;
import java.awt.Font;

public class CircuitSimulatorGUI {

	private JFrame frmQuantumCircuitEvaluator;
	private JTextField txtExeName;
	private JTextPane txtOutput;
	private JTextPane txtInputCirc;
	private JTextPane txtWebFriendly;
	private JFileChooser fcExe = new JFileChooser(".");
	private JFileChooser fcQC = new JFileChooser(".");

	public void makeVisible() {
		frmQuantumCircuitEvaluator.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public CircuitSimulatorGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmQuantumCircuitEvaluator = new JFrame();
		frmQuantumCircuitEvaluator.setResizable(false);
		frmQuantumCircuitEvaluator.setTitle("Quantum Circuit Evaluator");
		frmQuantumCircuitEvaluator.setBounds(100, 100, 663, 493);
		frmQuantumCircuitEvaluator.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frmQuantumCircuitEvaluator.getContentPane().setLayout(null);
		
		JButton btnBrowseForExe = new JButton("Browse for exe");
		btnBrowseForExe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (fcExe.showOpenDialog(frmQuantumCircuitEvaluator) == JFileChooser.APPROVE_OPTION) {
					final File file = fcExe.getSelectedFile();
					txtExeName.setText(file.getAbsolutePath());
				}
			}
		});
		btnBrowseForExe.setBounds(12, 42, 127, 25);
		frmQuantumCircuitEvaluator.getContentPane().add(btnBrowseForExe);
		
		txtExeName = new JTextField();
		txtExeName.setText("C:\\Users\\Martin\\Desktop\\data\\einar\\ParallelQC.exe");
		txtExeName.setBounds(151, 43, 491, 22);
		frmQuantumCircuitEvaluator.getContentPane().add(txtExeName);
		txtExeName.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 177, 167, 230);
		frmQuantumCircuitEvaluator.getContentPane().add(scrollPane);
		
		txtInputCirc = new JTextPane();
		txtInputCirc.setText("ZZ q1 q2\r\nZZ q2 q3\r\nH q3");
		scrollPane.setViewportView(txtInputCirc);
		
		JLabel lblInputQuantumCircuit = new JLabel("Input quantum circuit:");
		lblInputQuantumCircuit.setBounds(12, 80, 141, 16);
		frmQuantumCircuitEvaluator.getContentPane().add(lblInputQuantumCircuit);
		
		JLabel lblYouWouldNeed = new JLabel("You would need to have the ParallelQC executable in order to be able to run quantum circuits.");
		lblYouWouldNeed.setBounds(12, 13, 630, 16);
		frmQuantumCircuitEvaluator.getContentPane().add(lblYouWouldNeed);
		
		JButton btnLoadFromFile = new JButton("Load from file");
		btnLoadFromFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (fcQC.showOpenDialog(frmQuantumCircuitEvaluator) == JFileChooser.APPROVE_OPTION) {
					final File file = fcQC.getSelectedFile();
					try {
						txtInputCirc.setText(new Scanner(file).useDelimiter("\\Z").next());
					} catch (Throwable e) {
						throwException(e);
					}
				}
			}
		});
		btnLoadFromFile.setBounds(12, 109, 167, 25);
		frmQuantumCircuitEvaluator.getContentPane().add(btnLoadFromFile);
		
		JLabel lblWebDescriptionOf = new JLabel("Web description of the circuit:");
		lblWebDescriptionOf.setBounds(191, 80, 199, 16);
		frmQuantumCircuitEvaluator.getContentPane().add(lblWebDescriptionOf);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(191, 109, 451, 132);
		frmQuantumCircuitEvaluator.getContentPane().add(scrollPane_1);
		
		txtWebFriendly = new JTextPane();
		txtWebFriendly.setFont(new Font("Monospaced", txtWebFriendly.getFont().getStyle(), txtWebFriendly.getFont().getSize()));
		txtWebFriendly.setEditable(false);
		scrollPane_1.setViewportView(txtWebFriendly);
		
		JLabel lblOutput = new JLabel("Output:");
		lblOutput.setBounds(191, 254, 56, 16);
		frmQuantumCircuitEvaluator.getContentPane().add(lblOutput);
		
		JButton btnEvaluate = new JButton("EVALUATE");
		btnEvaluate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				try {
					final String[] res = TranslatorRunner.runTestFromString(txtExeName.getText(), txtInputCirc.getText());
					txtWebFriendly.setText(res[0]);
					txtOutput.setText(res[1]);
				} catch (Throwable e) {
					throwException(e);
				}
			}
		});
		btnEvaluate.setBounds(12, 420, 167, 25);
		frmQuantumCircuitEvaluator.getContentPane().add(btnEvaluate);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(191, 276, 451, 169);
		frmQuantumCircuitEvaluator.getContentPane().add(scrollPane_2);
		
		txtOutput = new JTextPane();
		txtOutput.setEditable(false);
		scrollPane_2.setViewportView(txtOutput);
		
		JButton btnGenerateRandom = new JButton("Generate random");
		btnGenerateRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				txtInputCirc.setText(TranslatorRunner.generateRandomCircuitDesc());
			}
		});
		btnGenerateRandom.setBounds(12, 139, 167, 25);
		frmQuantumCircuitEvaluator.getContentPane().add(btnGenerateRandom);
	}
	
	private void throwException(final Throwable e) {
		JOptionPane.showMessageDialog(frmQuantumCircuitEvaluator, e.getMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}

}
