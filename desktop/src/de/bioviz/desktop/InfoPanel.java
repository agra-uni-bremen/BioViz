package de.bioviz.desktop;

import de.bioviz.structures.Biochip;
import de.bioviz.ui.BioViz;
import de.bioviz.ui.DrawableCircuit;
import de.bioviz.ui.DrawableDroplet;
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
	private JTable table = new JTable(model);
	private JTable dropToFluidTable = new JTable(dropToFluidModel);

	private BioViz bioViz;


	public InfoPanel(final BioViz bioViz) {
		int panelWidth = 200;
		int panelHeight = 600;

		this.bioViz = bioViz;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//this.setLayout(new FlowLayout());

		this.setPreferredSize(new Dimension(panelWidth, panelHeight));

		JScrollPane scrollPane = new JScrollPane(table);
		JScrollPane dropToFluidScrollPane = new JScrollPane(dropToFluidTable);

		JPanel dropCountPane = new JPanel();
		dropCountPane.setLayout(new BoxLayout(dropCountPane, BoxLayout
				.X_AXIS));
		JLabel dropCountLabel = new JLabel("# of Droplets: ");
		dropCountPane.add(dropCountLabel);
		dropCountPane.add(dropletCountValue);

		JPanel experimentDurationPane = new JPanel();
		experimentDurationPane.setLayout(new BoxLayout(experimentDurationPane,
													   BoxLayout.X_AXIS));
		JLabel experimentDurationLabel = new JLabel("Max Duration: ");
		experimentDurationPane.add(experimentDurationLabel);
		experimentDurationPane.add(experimentDurationValue);

		JSeparator infoSep = new JSeparator(SwingConstants.HORIZONTAL);
		infoSep.setMaximumSize(new Dimension(180, 5));

		this.add(new JLabel("Info"));
		this.add(infoSep);
		this.add(dropCountPane);
		this.add(experimentDurationPane);
		this.add(scrollPane);
		this.add(dropToFluidScrollPane);
	}

	public void refreshPanelData() {
		model.setRowCount(0);
		dropToFluidModel.setRowCount(0);
		if (bioViz != null) {
			DrawableCircuit currentCircuit = bioViz.currentCircuit;
			if (currentCircuit != null) {

				updateDropToFluid();
				final int animationDuration = bioViz.getAnimationDuration();
				final int maxT = currentCircuit.getData().getMaxT();
				final int dropletCount = currentCircuit.getDroplets().size();
				dropletCountValue.setText(String.valueOf(dropletCount));
				experimentDurationValue.setText(String.valueOf(maxT));

				// final int cellUsage = currentCircuit.getData()
				// .computeCellUsage();

				Biochip data = currentCircuit.getData();
				for (final DrawableDroplet droplet : currentCircuit
						.getDroplets()) {
					final int dropletID = droplet.droplet.getID();
					if (data != null) {
						logger.debug("Adding rowData");
						final Integer fluidID = data.fluidID(dropletID);
						if (!currentCircuit.isHidden(droplet) &&
							droplet.getColor().a > 0.1 && fluidID != null) {
							final String fluidName = data.fluidType(fluidID);
							model.addRow(new Object[]{fluidID, fluidName});
						}
					}
				}
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

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

}
