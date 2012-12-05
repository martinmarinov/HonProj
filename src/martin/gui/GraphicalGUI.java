package martin.gui;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import martin.gui.quantum.Arrow;
import martin.gui.quantum.Inventory;
import martin.gui.quantum.Qubit;
import martin.gui.quantum.Qubit.type;
import martin.gui.quantum.Visualizer;

import javax.swing.JMenuBar;
import javax.swing.JMenu;

public class GraphicalGUI {
	
	private Inventory toolBar = new Inventory(
			new Arrow(),
			new Qubit(type.input),
			new Qubit(type.normal),
			new Qubit(type.output));
	
	private JFrame frmMeasurementBasedQuantum;
	private Visualizer visualizer;
	private JMenuBar menuBar;
	private JMenu mnFile;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
	}
}
