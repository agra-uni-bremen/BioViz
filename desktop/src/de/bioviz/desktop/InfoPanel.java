package de.bioviz.desktop;

import de.bioviz.structures.Biochip;
import de.bioviz.structures.BiochipField;
import de.bioviz.ui.BioViz;
import de.bioviz.ui.DrawableCircuit;
import de.bioviz.ui.DrawableDroplet;
import de.bioviz.ui.DrawableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * @author Maximilian Luenert
 */
public class InfoPanel extends JPanel {

	/**
	 * Used to handle feedback for the user about the program behaviour (and of
	 * course the developer, too). Anything logged using this instance will
	 * report as originating from the DesktopLauncher class.
	 */
	private static Logger logger =
			LoggerFactory.getLogger(PreferencesWindow.class);

	private DefaultTableModel model = new DefaultTableModel() {
		private String[] columnNames = {"FluidID", "FluidType"};

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int index) {
			return columnNames[index];
		}

		@Override
		public boolean isCellEditable(int col, int row) {
			return false;
		}
	};

	private DefaultTableModel dropToFluidModel = new DefaultTableModel() {
		private String[] columnNames = {"DropletID", "FluidType"};

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int index) {
			return columnNames[index];
		}

		@Override
		public boolean isCellEditable(int col, int row) {
			return false;
		}
	};

	private JLabel dropletCountValue = new JLabel();
	private JLabel experimentDurationValue = new JLabel();
	private JLabel minUsageValue = new JLabel();
	private JLabel maxUsageValue = new JLabel();
	private JLabel avgUsageValue = new JLabel();

	private JLabel fieldNumValue = new JLabel();

	private JTable table = new JTable(model);
	private JTable dropToFluidTable = new JTable(dropToFluidModel);

	private BioViz bioViz;


	public InfoPanel(final BioViz bioViz) {
		int panelWidth = 200;
		int panelHeight = 600;
		int labelHeight = 20;
		int labelWidth = 120;
		int valueWidth = 70;
		int internalWidth = 190;

		this.bioViz = bioViz;

		JPanel panel = new JPanel();

		panel.setPreferredSize(new Dimension(panelWidth,panelHeight));

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(internalWidth, 100));
		JScrollPane dropToFluidScrollPane = new JScrollPane(dropToFluidTable);
		dropToFluidScrollPane.setPreferredSize(new Dimension(internalWidth, 100));

		JLabel dropCountLabel = new JLabel("# Droplets: ");
		dropCountLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
		dropletCountValue.setPreferredSize(new Dimension(valueWidth,labelHeight));

		JLabel experimentDurationLabel = new JLabel("Timesteps: ");
		experimentDurationLabel.setPreferredSize(
				new Dimension(labelWidth,	labelHeight));
		experimentDurationValue.setPreferredSize(
				new Dimension(valueWidth,	labelHeight));

		JLabel minUsageLabel = new JLabel("Min usage: ");
		minUsageLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
		minUsageValue.setPreferredSize(new Dimension(valueWidth, labelHeight));

		JLabel maxUsageLabel = new JLabel("Max usage: ");
		maxUsageLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
		maxUsageValue.setPreferredSize(new Dimension(valueWidth, labelHeight));

		JLabel avgUsageLabel = new JLabel("Avg usage: ");
		avgUsageLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
		avgUsageValue.setPreferredSize(new Dimension(valueWidth, labelHeight));

		JLabel fieldNumLabel = new JLabel("# Fields: ");
		fieldNumLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
		fieldNumValue.setPreferredSize(new Dimension(valueWidth, labelHeight));

		JSeparator infoSep = new JSeparator(SwingConstants.HORIZONTAL);
		infoSep.setPreferredSize(new Dimension(internalWidth, 5));

		JLabel infoLabel = new JLabel("Info");
		infoLabel.setPreferredSize(new Dimension(internalWidth, 15));

		panel.add(infoLabel);
		panel.add(infoSep);
		panel.add(dropCountLabel);
		panel.add(dropletCountValue);
		panel.add(experimentDurationLabel);
		panel.add(experimentDurationValue);
		panel.add(minUsageLabel);
		panel.add(minUsageValue);
		panel.add(maxUsageLabel);
		panel.add(maxUsageValue);
		panel.add(avgUsageLabel);
		panel.add(avgUsageValue);
		panel.add(fieldNumLabel);
		panel.add(fieldNumValue);
		panel.add(scrollPane);
		panel.add(dropToFluidScrollPane);
		this.add(panel);
	}

	public void refreshPanelData() {
		model.setRowCount(0);
		dropToFluidModel.setRowCount(0);
		if (bioViz != null) {
			DrawableCircuit currentCircuit = bioViz.currentCircuit;
			if (currentCircuit != null) {

				final int maxT = currentCircuit.getData().getMaxT();
				final int dropletCount = currentCircuit.getDroplets().size();
				dropletCountValue.setText(String.valueOf(dropletCount));
				experimentDurationValue.setText(String.valueOf(maxT));

				updateUsage();
				updateFluidTable();
				updateDropToFluid();
				updateFieldCount();
			}
		}
	}

	private void updateDropToFluid() {
		DrawableCircuit currentCircuit = bioViz.currentCircuit;
		Biochip data = currentCircuit.getData();
		for (final DrawableDroplet droplet : currentCircuit.getDroplets()) {
			final int dropletID = droplet.droplet.getID();
			if (data != null) {
				logger.debug("Adding rowData");
				String fluidName = data.fluidType(data.fluidID(dropletID));
				dropToFluidModel.addRow(new Object[]{dropletID, fluidName});
			}
		}
	}

	public void repaint() {
		if (table != null) {
			table.repaint();
		}
		if (dropToFluidTable != null) {
			dropToFluidTable.repaint();
		}
	}

	public void updateFluidTable(){
		Biochip data = bioViz.currentCircuit.getData();
		for (final DrawableDroplet droplet : bioViz.currentCircuit.getDroplets()) {
			final int dropletID = droplet.droplet.getID();
			if (data != null) {
				final Integer fluidID = data.fluidID(dropletID);
				if(!bioViz.currentCircuit.isHidden(droplet) &&
						droplet.getColor().a > 0.1 && fluidID != null) {
					final String fluidName = data.fluidType(fluidID);
					model.addRow(new Object[]{fluidID, fluidName});
				}
			}
		}
	}

	public void updateUsage(){

		DrawableCircuit currentCircuit = bioViz.currentCircuit;
		Biochip data = currentCircuit.getData();

		data.computeCellUsage();

		int minUsage = Integer.MAX_VALUE;
		int maxUsage = 0;
		double avgUsage = 0;

		for (final DrawableField f : currentCircuit.getFields()){
			BiochipField field = f.getField();
			avgUsage += field.getUsage();
			if(maxUsage < field.getUsage()){
				maxUsage = field.getUsage();
			}
			if(minUsage > field.getUsage() && field.getUsage() != 0){
				minUsage = field.getUsage();
			}
		}
		avgUsage /= currentCircuit.getFields().size();

		avgUsageValue.setText(String.valueOf(avgUsage));
		minUsageValue.setText(String.valueOf(minUsage));
		maxUsageValue.setText(String.valueOf(maxUsage));
	}

	public void updateFieldCount(){
		int numFields = bioViz.currentCircuit.getFields().size();
		fieldNumValue.setText(String.valueOf(numFields));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

}
