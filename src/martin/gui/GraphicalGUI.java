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
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import martin.gui.quantum.Arrow;
import martin.gui.quantum.Corrector;
import martin.gui.quantum.Entangler;
import martin.gui.quantum.Inventory;
import martin.gui.quantum.Qubit;
import martin.gui.quantum.Corrector.corrtype;
import martin.gui.quantum.Qubit.type;
import martin.gui.quantum.Visualizer;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.awt.Color;
import javax.swing.JRadioButtonMenuItem;

public class GraphicalGUI {
	
	private Inventory toolBar = new Inventory(
			new Arrow(),
			new Qubit(type.input),
			new Qubit(type.normal),
			new Entangler(),
			new Corrector(corrtype.X),
			new Corrector(corrtype.Z));
	
	private JFrame frmMeasurementBasedQuantum;
	private Visualizer visualizer;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnSimulate;
	private JMenuItem mntmTranslateToMcalc;
	
	private JFileChooser fileChooser = new JFileChooser(".");
	
	private EvaluatorGUI mgui = new EvaluatorGUI();
	private ExperimentConductor expverf = new ExperimentConductor();
	private CircuitSimulatorGUI qcemul = new CircuitSimulatorGUI();
	private JDialog license = new LicenseDialog();
	private JDialog about = new AboutDialog();
	
	private JMenuItem mntmOpenGraphical;
	private JMenuItem mntmSaveGraphical;
	private JMenu mnTools;
	private JMenuItem mntmExperimentVerifier;
	private JMenu mnView;
	private JRadioButtonMenuItem rdbtnmntmDefaultView;
	private JRadioButtonMenuItem rdbtnmntmPrinterFriendly;
	private JMenuItem mntmExit;
	private JMenuItem mntmNew;
	private JMenuItem mntmQuantumCircuitEmulator;
	private JMenuItem mntmLicense;
	private JMenuItem mntmAbout;
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GraphicalGUI window = new GraphicalGUI();
					window.frmMeasurementBasedQuantum.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GraphicalGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMeasurementBasedQuantum = new JFrame();
		frmMeasurementBasedQuantum.setTitle("Measurement Based Quantum Computing Simulator v0.1a");
		frmMeasurementBasedQuantum.setBounds(100, 100, 663, 553);
		frmMeasurementBasedQuantum.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMeasurementBasedQuantum.getContentPane().setLayout(new BorderLayout(0, 0));
		
		visualizer = new Visualizer();
		visualizer.registerInventory(toolBar);
		frmMeasurementBasedQuantum.getContentPane().add(visualizer);
		toolBar.setBackground(new Color(99, 97, 122));
		
		frmMeasurementBasedQuantum.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		menuBar = new JMenuBar();
		frmMeasurementBasedQuantum.setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmOpenGraphical = new JMenuItem("Open graphical");
		mntmOpenGraphical.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final int returnVal = fileChooser.showOpenDialog(frmMeasurementBasedQuantum);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						final File f = fileChooser.getSelectedFile();
						visualizer.loadFromFile(f);
						
					} catch (Exception e) {
						throwException(e);
					}
				}
			}
		});
		
		mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				try {
					visualizer.loadFromFile(null);
				} catch (Exception e) {
					throwException(e);
				}
			}
		});
		mnFile.add(mntmNew);
		mnFile.addSeparator();
		mnFile.add(mntmOpenGraphical);
		
		mntmSaveGraphical = new JMenuItem("Save graphical");
		mntmSaveGraphical.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final int returnVal = fileChooser.showSaveDialog(frmMeasurementBasedQuantum);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = fileChooser.getSelectedFile();
					if (f.exists() && 
							JOptionPane.showConfirmDialog(
									frmMeasurementBasedQuantum, 
									"File '"+f.getName()+"' already exists. Overwrite?",
									"Overwriting file", 
									JOptionPane.YES_NO_OPTION, 
									JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
						return;
					
					try {
						visualizer.saveToFile(f);
					} catch (Exception ee) {
						throwException(ee);
					}
				}
			}
		});
		mnFile.add(mntmSaveGraphical);
		mnFile.addSeparator();
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		mnSimulate = new JMenu("Simulation");
		menuBar.add(mnSimulate);
		
		mntmTranslateToMcalc = new JMenuItem("Translate to Mcalc and simulate");
		mntmTranslateToMcalc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					mgui.populateFrom(visualizer.generateMcalcDesc());
					mgui.makeVisible();
				} catch (Exception e) {
					throwException(e);
				}
			}
		});
		mnSimulate.add(mntmTranslateToMcalc);
		
		mnView = new JMenu("View");
		menuBar.add(mnView);
		
		rdbtnmntmDefaultView = new JRadioButtonMenuItem("Default view");
		rdbtnmntmDefaultView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				rdbtnmntmDefaultView.setSelected(true);
				rdbtnmntmPrinterFriendly.setSelected(false);
				
				Visualizer.background = new Color(0x3e3b5a);
				Visualizer.foreground = Color.white;
				Visualizer.fill_color = new Color(255, 255, 255, 120);
				Visualizer.grid_color = new Color(Visualizer.fill_color.getRed(), Visualizer.fill_color.getBlue(), Visualizer.fill_color.getGreen(), 50);
			
				visualizer.repaint();
			}
		});
		rdbtnmntmDefaultView.setSelected(true);
		mnView.add(rdbtnmntmDefaultView);
		
		rdbtnmntmPrinterFriendly = new JRadioButtonMenuItem("Printer friendly");
		rdbtnmntmPrinterFriendly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				rdbtnmntmDefaultView.setSelected(false);
				rdbtnmntmPrinterFriendly.setSelected(true);
				
				Visualizer.background = Color.white;
				Visualizer.foreground = Color.black;
				Visualizer.fill_color = new Color(0, 0, 0, 30);
				Visualizer.grid_color = new Color(Visualizer.fill_color.getRed(), Visualizer.fill_color.getBlue(), Visualizer.fill_color.getGreen(), 50);
				
				visualizer.repaint();
			}
		});
		mnView.add(rdbtnmntmPrinterFriendly);
		
		mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		
		mntmExperimentVerifier = new JMenuItem("Experiment verifier");
		mntmExperimentVerifier.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				expverf.makeVisible();
			}
		});
		mnTools.add(mntmExperimentVerifier);
		
		mntmQuantumCircuitEmulator = new JMenuItem("Quantum Circuit emulator");
		mntmQuantumCircuitEmulator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				qcemul.makeVisible();
			}
		});
		mnTools.add(mntmQuantumCircuitEmulator);
		mnTools.addSeparator();
		mntmLicense = new JMenuItem("License");
		mntmLicense.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				license.setVisible(true);
			}
		});
		mnTools.add(mntmLicense);
		
		mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				about.setVisible(true);
			}
		});
		mnTools.add(mntmAbout);
	}
	
	private void throwException(final Exception e) {
		JOptionPane.showMessageDialog(frmMeasurementBasedQuantum, e.getMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}
	
	public JRadioButtonMenuItem getRdbtnmntmDefaultView() {
		return rdbtnmntmDefaultView;
	}
	public JRadioButtonMenuItem getRdbtnmntmPrinterFriendly() {
		return rdbtnmntmPrinterFriendly;
	}
}
