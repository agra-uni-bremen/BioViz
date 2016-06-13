package de.bioviz.desktop;

import de.bioviz.structures.*;
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
	private JLabel numNetValue = new JLabel();
	private JLabel numSourcesValue = new JLabel();
	private JLabel numTargetValue = new JLabel();
	private JLabel numSinksValue = new JLabel();
	private JLabel numDispensersValue = new JLabel();

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

		JLabel numNetLabel = new JLabel("# Nets: ");
		numNetLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
		numNetValue.setPreferredSize(new Dimension(valueWidth, labelHeight));

		JLabel numSourcesLabel = new JLabel("# Sources: ");
		numSourcesLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
		numSourcesValue.setPreferredSize(new Dimension(valueWidth, labelHeight));

		JLabel numTargetLabel = new JLabel("# Targets: ");
		numTargetLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
		numTargetValue.setPreferredSize(new Dimension(valueWidth, labelHeight));

		JLabel numSinksLabel = new JLabel("# Sinks: ");
		numSinksLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
		numSinksValue.setPreferredSize(new Dimension(valueWidth, labelHeight));

		JLabel numDispensersLabel = new JLabel("# Dispensers: ");
		numDispensersLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
		numDispensersValue.setPreferredSize(new Dimension(valueWidth, labelHeight));

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
		panel.add(numNetLabel);
		panel.add(numNetValue);
		panel.add(numSourcesLabel);
		panel.add(numSourcesValue);
		panel.add(numTargetLabel);
		panel.add(numTargetValue);
		panel.add(numDispensersLabel);
		panel.add(numDispensersValue);
		panel.add(numSinksLabel);
		panel.add(numSinksValue);
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
				updateNets();
				updateSinksAndDispensers();
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

	private void updateDropToFluid() {
		DrawableCircuit currentCircuit = bioViz.currentCircuit;
		Biochip data = currentCircuit.getData();
		for (final DrawableDroplet droplet : currentCircuit.getDroplets()) {
			final int dropletID = droplet.droplet.getID();
			String fluidName = data.fluidType(data.fluidID(dropletID));
			dropToFluidModel.addRow(new Object[]{dropletID, fluidName});
		}
	}

	public void updateFluidTable() {
		Biochip data = bioViz.currentCircuit.getData();
		for (final DrawableDroplet droplet : bioViz.currentCircuit.getDroplets()) {
			final int dropletID = droplet.droplet.getID();
			final Integer fluidID = data.fluidID(dropletID);
			final String fluidName = data.fluidType(fluidID);
			model.addRow(new Object[]{fluidID, fluidName});
		}
	}

	public void updateUsage() {

		DrawableCircuit currentCircuit = bioViz.currentCircuit;
		Biochip data = currentCircuit.getData();

		data.computeCellUsage();

		int minUsage = Integer.MAX_VALUE;
		int maxUsage = 0;
		double avgUsage = 0;

		for (final BiochipField f : currentCircuit.getData().getAllFields()) {
			avgUsage += f.getUsage();
			if (maxUsage < f.getUsage()) {
				maxUsage = f.getUsage();
			}
			if (minUsage > f.getUsage()) {
				minUsage = f.getUsage();
			}
		}
		avgUsage /= currentCircuit.getFields().size();

		avgUsageValue.setText(String.valueOf(avgUsage));
		minUsageValue.setText(String.valueOf(minUsage));
		maxUsageValue.setText(String.valueOf(maxUsage));
	}

	public void updateFieldCount() {
		int numFields = bioViz.currentCircuit.getFields().size();
		fieldNumValue.setText(String.valueOf(numFields));
	}

	public void updateNets() {
		DrawableCircuit currentCircuit = bioViz.currentCircuit;
		int numNets = currentCircuit.getData().getNets().size();
		int numSources = 0;
		int numTargets = 0;
		for (final Net n : currentCircuit.getData().getNets()) {
			numSources += n.getSources().size();
			if (n.getTarget() != null) {
				numTargets++;
			}
		}

		numNetValue.setText(String.valueOf(numNets));
		numSourcesValue.setText(String.valueOf(numSources));
		numTargetValue.setText(String.valueOf(numTargets));
	}

	public void updateSinksAndDispensers() {
		DrawableCircuit currentCircuit = bioViz.currentCircuit;
		int numDispenser = 0;
		int numSinks = 0;
		for (BiochipField f : currentCircuit.getData().getAllFields()) {
			if (f instanceof Dispenser) {
				numDispenser++;
			}
			if (f instanceof Sink) {
				numSinks++;
			}
		}
		numDispensersValue.setText(String.valueOf(numDispenser));
		numSinksValue.setText(String.valueOf(numSinks));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

}
