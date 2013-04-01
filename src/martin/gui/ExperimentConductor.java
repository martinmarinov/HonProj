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
import java.awt.Graphics;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import martin.experiments.ExperimTest;
import martin.experiments.ExperimTest.ExperimentalResult;
import martin.quantum.SimulationRunner;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Color;

public class ExperimentConductor {
	
	private final static String TITLE = "Experimental data evaluator";

	private JFrame frmExperimentalDataEvaluator;
	private JTextField txtexpimnpiimimexpimnpi;
	private JTextField txtN;
	private JProgressBar progressBar;
	private JFileChooser fileChooser = new JFileChooser(".");
	private JFileChooser imageSaver = new JFileChooser(".");
	private JFileChooser csvSaver = new JFileChooser(".");
	private JButton btnSaveResultTo;
	private JButton btnSaveResultTo_1;
	private JButton btnOpenExperimentalData;
	private ImageViewer panel;
	private ExperimentalResult expRes = null;

	/**
	 * Create the application.
	 */
	public ExperimentConductor() {
		initialize();
	}
	
	public void makeVisible() {
		frmExperimentalDataEvaluator.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmExperimentalDataEvaluator = new JFrame();
		frmExperimentalDataEvaluator.setResizable(false);
		frmExperimentalDataEvaluator.setTitle(TITLE);
		frmExperimentalDataEvaluator.setBounds(100, 100, 671, 437);
		frmExperimentalDataEvaluator
				.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frmExperimentalDataEvaluator.getContentPane().setLayout(null);

		btnOpenExperimentalData = new JButton("Open experimental data");
		btnOpenExperimentalData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (fileChooser.showOpenDialog(frmExperimentalDataEvaluator) == JFileChooser.APPROVE_OPTION) {
					final File file = fileChooser.getSelectedFile();

					new Thread() {
						public void run() {
							try {
								btnSaveResultTo.setEnabled(false);
								btnSaveResultTo_1.setEnabled(false);
								btnOpenExperimentalData.setEnabled(false);
								frmExperimentalDataEvaluator.setTitle(TITLE+" - "+file.getName());
								expRes = ExperimTest.perform(
										file.getAbsolutePath(),
										txtexpimnpiimimexpimnpi.getText(),
										SimulationRunner
												.parseVariablesAndValues(txtN.getText()),
										new ExperimTest.ProgressListener() {

											@Override
											public void onProgress(double percentage) {
												progressBar.setValue((int) (percentage * 1000));
											}
										});
								btnOpenExperimentalData.setEnabled(true);
								btnSaveResultTo.setEnabled(true);
								btnSaveResultTo_1.setEnabled(true);
								panel.drawImage(expRes.image);
							} catch (Throwable e) {
								throwException(e);
							}
						};
					}.start();
				}
			}
		});
		btnOpenExperimentalData.setBounds(12, 327, 199, 25);
		frmExperimentalDataEvaluator.getContentPane().add(
				btnOpenExperimentalData);

		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setMaximum(1000);
		progressBar.setBounds(12, 365, 641, 24);
		frmExperimentalDataEvaluator.getContentPane().add(progressBar);

		btnSaveResultTo = new JButton("Save result to image");
		btnSaveResultTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (expRes != null) {
					
					if (imageSaver.showSaveDialog(frmExperimentalDataEvaluator) == JFileChooser.APPROVE_OPTION) {
						String fileName = imageSaver.getSelectedFile().getAbsolutePath();
						if (! (fileName.endsWith(".png") || fileName.endsWith(".jpg")))
							fileName = fileName + ".png";
						
						try {
							ImageIO.write(expRes.image, getFileExt(fileName).toLowerCase(), new File(fileName));
						} catch (Throwable e) {
							throwException(e);
						}
					}
					
				} else {
					btnSaveResultTo.setEnabled(false);
					btnSaveResultTo_1.setEnabled(false);
				}
			}
		});
		btnSaveResultTo.setEnabled(false);
		btnSaveResultTo.setBounds(339, 327, 151, 25);
		frmExperimentalDataEvaluator.getContentPane().add(btnSaveResultTo);

		btnSaveResultTo_1 = new JButton("Save result to csv");
		btnSaveResultTo_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				if (expRes != null) {
					
					if (csvSaver.showSaveDialog(frmExperimentalDataEvaluator) == JFileChooser.APPROVE_OPTION) {
						final String fileName = csvSaver.getSelectedFile().getAbsolutePath();
						try {
							expRes.csv.saveToFile(fileName);
						} catch (Throwable e) {
							throwException(e);
						}
					}
					
				} else {
					btnSaveResultTo.setEnabled(false);
					btnSaveResultTo_1.setEnabled(false);
				}
			}
		});
		btnSaveResultTo_1.setEnabled(false);
		btnSaveResultTo_1.setBounds(502, 327, 151, 25);
		frmExperimentalDataEvaluator.getContentPane().add(btnSaveResultTo_1);

		JLabel lblLabCluster = new JLabel("Lab cluster:");
		lblLabCluster.setBounds(12, 299, 79, 16);
		frmExperimentalDataEvaluator.getContentPane().add(lblLabCluster);

		txtexpimnpiimimexpimnpi = new JTextField();
		txtexpimnpiimimexpimnpi
				.setText("1/2,0,0,exp(Im((n*Pi)/4))/2,0,0,0,0,0,0,0,0,Im(1)/2,0,0,-Im(exp(Im((n*Pi)/4)))/2");
		txtexpimnpiimimexpimnpi.setBounds(95, 296, 558, 22);
		frmExperimentalDataEvaluator.getContentPane().add(
				txtexpimnpiimimexpimnpi);
		txtexpimnpiimimexpimnpi.setColumns(10);

		panel = new ImageViewer();
		panel.setBackground(Color.WHITE);
		panel.setBounds(1, 1, 98, 42);
		frmExperimentalDataEvaluator.getContentPane().add(panel);

		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setBounds(12, 10, 641, 247);
		frmExperimentalDataEvaluator.getContentPane().add(scrollPane);

		JLabel lblVariables = new JLabel("Variables:");
		lblVariables.setBounds(12, 270, 79, 16);
		frmExperimentalDataEvaluator.getContentPane().add(lblVariables);

		txtN = new JTextField();
		txtN.setText("n = 0; Pi = 3.141592653589793");
		txtN.setBounds(95, 267, 558, 22);
		frmExperimentalDataEvaluator.getContentPane().add(txtN);
		txtN.setColumns(10);

	}
	
	private static class ImageViewer extends JPanel {
		
		private static final long serialVersionUID = 7999961912995960644L;
		private BufferedImage image = null;

		public void drawImage(final BufferedImage image) {
			this.image = image;
			setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
			revalidate();
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if (image != null)
				g.drawImage(image, 0, 0, null);
		}
	}
	
	private static String getFileExt(final String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

		if (i > p) {
		    extension = fileName.substring(i+1);
		}
		return extension;
	}
	
	private void throwException(final Throwable e) {
		JOptionPane.showMessageDialog(frmExperimentalDataEvaluator, e.getMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}
}
