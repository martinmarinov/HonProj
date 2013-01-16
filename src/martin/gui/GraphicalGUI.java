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

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class GraphicalGUI {
	
	private Inventory toolBar = new Inventory(
			new Arrow(),
			new Qubit(type.input),
			new Qubit(type.normal),
			new Qubit(type.output),
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
	
	private MainGUI mgui = new MainGUI();
	private JMenuItem mntmOpenGraphical;
	private JMenuItem mntmSaveGraphical;
	
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
	}
	
	private void throwException(final Exception e) {
		JOptionPane.showMessageDialog(frmMeasurementBasedQuantum, e.getMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}
	
}
