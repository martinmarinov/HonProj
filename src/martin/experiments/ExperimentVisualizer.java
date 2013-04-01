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
package martin.experiments;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ExperimentVisualizer {
	
	private final static int left_offset = 110;
	private final static int vert_offset = 1;
	private final static int text_offset = 5;
	private final static int resid_offset = 55;
	private final static int width = 610;
	private final static int height = 40;
	
	private final ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	
	public void add(final String alabel, final Double[] a, final String blabel, final Double[] b) {
		double max = 0;
		double min = 1.0;
		double sum = 0;
		double resdiff = 0;
		for (int i = 0; i < a.length; i++) {
			sum+=a[i];
			if (a[i] > max) max = a[i];
			if (a[i] < min) min = a[i];
			resdiff += Math.abs(a[i]-b[i]);
		}
		resdiff /= (double) a.length;
		resdiff *= 100; // in percent
		final double asum = sum;
		sum = 0;
		for (int i = 0; i < b.length; i++) {
			sum+=b[i];
			if (b[i] > max) max = b[i];
			if (b[i] < min) min = b[i];
		}
		final double range = max - min;
		final double bsum = sum;
		
		if (Math.abs(bsum - asum) > 0.1) System.err.println("WARNING: Sums of input values differ: "+asum+" and "+bsum);
		
		final BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		final Graphics g = im.createGraphics();

		g.setColor(Color.white);
		
		g.fillRect(0, 0, width, height);
		g.setColor(Color.black);
		g.drawString(String.format("%.4f%%",resdiff), width - resid_offset+text_offset, height / 2);
		g.drawRect(0, 0, width - resid_offset, height);

		g.drawString(alabel, text_offset, height / 4);
		int av_width = width - left_offset - resid_offset;
		float box_size = av_width / (float) a.length;
		for (int i = 0; i < a.length; i++) {
			int col = (int) (250.0d * (a[i] - min) / range);
			if (col > 255) col = 255;
			if (col < 0) col = 0;

			g.setColor(new Color(col, col, col));
			g.fillRect((int) (left_offset+box_size*i), vert_offset, (int) (box_size+1.0f), height / 2 - vert_offset);
		}

		g.setColor(Color.black);
		g.drawString(blabel, text_offset, 3*height / 4);
		float box_size2 = av_width / (float) b.length;
		for (int i = 0; i < b.length; i++) {
			int col = (int) (250.0d * (b[i] - min) / range);
			if (col > 255) col = 255;
			if (col < 0) col = 0;

			g.setColor(new Color(col, col, col));
			g.fillRect((int) (left_offset+box_size2*i), height/2, (int) (box_size2+1.0f), height / 2 - vert_offset);
		}

		g.dispose();
		images.add(im);
	}
	
	public BufferedImage getImage() {
		int width = 0;
		int height = 0;
		for (final BufferedImage im : images) {
			final int imw = im.getWidth();
			if (imw > width) width = imw;
			height += im.getHeight();
		}
		
		int height_so_far = 0;
		final BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		final Graphics g = res.createGraphics();
		for (final BufferedImage im : images) {
			g.drawImage(im, 0, height_so_far, null);
			height_so_far += im.getHeight();
		}
		g.dispose();
		
		return res;
	}
	
	public void dumpToFile(final String name) throws IOException {
		ImageIO.write(getImage(), "png", new File(name));
	}
	
	
	
}
