package de.bioviz.desktop;

import de.bioviz.structures.Biochip;
import de.bioviz.structures.BiochipField;
import de.bioviz.structures.Dispenser;
import de.bioviz.structures.Net;
import de.bioviz.structures.Sink;
import de.bioviz.ui.BioViz;
import de.bioviz.ui.DrawableAssay;
import de.bioviz.ui.DrawableDroplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * This class implements an infoPanel that shows statistics about the biochip.
 *
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

    /**
     * TableModel for the fluidId to fluidType table.
     */
    private DefaultTableModel fluidToTypeModel =
        new OurTableModel(new String[]{"FluidID", "FluidType"});

    /**
     * TableModel for the dropletId to fluidType table.
     */
    private DefaultTableModel dropToFluidModel =
        new OurTableModel(new String[]{"DropletID", "FluidType"});

    /**
     * Label to show the number of droplets.
     */
    private JLabel dropletCountValue = new JLabel();
    /**
     * Label to show the experiment duration.
     */
    private JLabel experimentDurationValue = new JLabel();
    /**
     * Label to show the min usage.
     */
    private JLabel minUsageValue = new JLabel();
    /**
     * Label to show the max usage.
     */
    private JLabel maxUsageValue = new JLabel();
    /**
     * Label to show the average usage.
     */
    private JLabel avgUsageValue = new JLabel();
    /**
     * Label to show the number of fields.
     */
    private JLabel fieldNumValue = new JLabel();

    /**
     * Label to show the amount of used fields.
     */
    private JLabel usedFieldValue = new JLabel();

    /**
     * Label to show the number of nets.
     */
    private JLabel numNetValue = new JLabel();
    /**
     * Label to show the number of source.
     */
    private JLabel numSourcesValue = new JLabel();
    /**
     * Label to show the number of targets.
     */
    private JLabel numTargetValue = new JLabel();
    /**
     * Label to show the number of sinks.
     */
    private JLabel numSinksValue = new JLabel();
    /**
     * Label to show the number of dispensers.
     */
    private JLabel numDispensersValue = new JLabel();
    /**
     * Label to show the number of detectors.
     */
    private JLabel numDetectorsValue = new JLabel();

    /**
     * FluidId to FluidType table.
     */
    private JTable fluidIdToTypeTable = new JTable(fluidToTypeModel);
    /**
     * DropletId to FluidType table.
     */
    private JTable dropToFluidTable = new JTable(dropToFluidModel);

    /**
     * The current BioViz instance.
     */
    private BioViz bioViz;

    /**
     * The currentAssay.
     */
    private DrawableAssay currentAssay;

    /**
     * The biochip data.
     */
    private Biochip data;


    /**
     * Constructor.
     *
     * @param bioViz the BioViz instance to use.
     */
    public InfoPanel(final BioViz bioViz) {
        final int panelWidth = 200;
        final int panelHeight = 800;
        final int labelHeight = 20;
        final int labelWidth = 120;
        final int valueWidth = 70;
        final int internalWidth = 190;
        final int tableHeight = 90;
        final int seperatorHeight = 5;

        this.bioViz = bioViz;

        // create main panel
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(panelWidth, panelHeight));

        fluidIdToTypeTable.getTableHeader().setResizingAllowed(false);
        fluidIdToTypeTable.getTableHeader().setReorderingAllowed(false);

        dropToFluidTable.getTableHeader().setResizingAllowed(false);
        dropToFluidTable.getTableHeader().setReorderingAllowed(false);

        // create scroll pane for tables
        JScrollPane fluidIdScrollPane = new JScrollPane(fluidIdToTypeTable);
        fluidIdScrollPane.setPreferredSize(new Dimension(internalWidth,
                tableHeight));
        JScrollPane dropToFluidScrollPane = new JScrollPane(dropToFluidTable);
        dropToFluidScrollPane.setPreferredSize(new Dimension(internalWidth,
                tableHeight));

        // create all labels and set their preferred size
        JLabel dropCountLabel = new JLabel("# Droplets: ");
        dropCountLabel.setPreferredSize(new Dimension(labelWidth,
                labelHeight));
        dropletCountValue.setPreferredSize(
                new Dimension(valueWidth, labelHeight));

        JLabel experimentDurationLabel = new JLabel("Timesteps: ");
        experimentDurationLabel.setPreferredSize(
                new Dimension(labelWidth, labelHeight));
        experimentDurationValue.setPreferredSize(
                new Dimension(valueWidth, labelHeight));

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

        JLabel usedFieldLabel = new JLabel("u Fields: ");
        usedFieldLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
        usedFieldValue.setPreferredSize(new Dimension(valueWidth, labelHeight));

        JLabel numNetLabel = new JLabel("# Nets: ");
        numNetLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
        numNetValue.setPreferredSize(new Dimension(valueWidth, labelHeight));

        JLabel numSourcesLabel = new JLabel("# Sources: ");
        numSourcesLabel.setPreferredSize(
                new Dimension(labelWidth, labelHeight));
        numSourcesValue.setPreferredSize(
                new Dimension(valueWidth, labelHeight));

        JLabel numTargetLabel = new JLabel("# Targets: ");
        numTargetLabel.setPreferredSize(new Dimension(labelWidth,
                labelHeight));
        numTargetValue.setPreferredSize(new Dimension(valueWidth,
                labelHeight));

        JLabel numSinksLabel = new JLabel("# Sinks: ");
        numSinksLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
        numSinksValue.setPreferredSize(new Dimension(valueWidth, labelHeight));

        JLabel numDispensersLabel = new JLabel("# Dispensers: ");
        numDispensersLabel.setPreferredSize(
                new Dimension(labelWidth, labelHeight));
        numDispensersValue.setPreferredSize(
                new Dimension(valueWidth, labelHeight));

        JLabel numDetectorsLabel = new JLabel("# Detectors: ");
        numDetectorsLabel.setPreferredSize(
                new Dimension(labelWidth, labelHeight));
        numDetectorsValue.setPreferredSize(
                new Dimension(valueWidth, labelHeight));

        JSeparator infoSep = new JSeparator(SwingConstants.HORIZONTAL);
        infoSep.setPreferredSize(new Dimension(internalWidth,
                seperatorHeight));

        JLabel infoLabel = new JLabel("Statistics");

        // add all elements to the pane
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
        panel.add(usedFieldLabel);
        panel.add(usedFieldValue);
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
        panel.add(numDetectorsLabel);
        panel.add(numDetectorsValue);
        panel.add(fluidIdScrollPane);
        panel.add(dropToFluidScrollPane);
        this.add(panel);
    }

    /**
     * Refreshes the data shown in the infoPanel.
     */
    public void refreshPanelData() {

        if (bioViz != null) {
            currentAssay = this.bioViz.currentAssay;
            if (currentAssay != null) {
                data = currentAssay.getData();
            }
        }

        fluidToTypeModel.setRowCount(0);
        dropToFluidModel.setRowCount(0);
        if (currentAssay != null && data != null) {
            updateMaxT();
            updateDropletCount();
            updateUsage();
            updateFieldCount();
            updateUsedFields();
            updateNets();
            updateFieldTypes();
            updateFluidTable();
            updateDropToFluid();
        }
    }


    /**
     * Updates the amount of used fields.
     */
    private void updateUsedFields() {

        if (data != null) {
            data.computeCellUsage();


            Long cell_usage =
                    data.getAllFields().stream().
                            map(f -> f.getUsage()).
                            filter(e -> e > 0).count();


            usedFieldValue.setText(cell_usage.toString());
        }
        else {
            usedFieldValue.setText("0");
        }
    }


    /**
     * Repaints the tables in case of a repaint event.
     * <p/>
     * Otherwise the	re is no content in the tables.
     */
    @Override
    public void repaint() {
        if (fluidIdToTypeTable != null) {
            fluidIdToTypeTable.repaint();
        }
        if (dropToFluidTable != null) {
            dropToFluidTable.repaint();
        }
    }

    /**
     * Updates maximum executionTime.
     */
    public void updateMaxT() {
        final int maxT = data.getMaxT();
        experimentDurationValue.setText(String.valueOf(maxT));
    }

    /**
     * Updates the number of droplets.
     */
    public void updateDropletCount() {
        final int dropletCount = currentAssay.getDroplets().size();
        dropletCountValue.setText(String.valueOf(dropletCount));
    }

    /**
     * Updates the table data for the dropletId to fluidType table.
     */
    private void updateDropToFluid() {
        if (data != null) {
            for (final DrawableDroplet droplet : currentAssay.getDroplets()) {
                final int dropletID = droplet.droplet.getID();
                String fluidType = data.fluidType(data.fluidID(dropletID));
                if (fluidType != null) {
                    dropToFluidModel.addRow(new Object[]{dropletID, fluidType});
                }
            }

        }
    }

    /**
     * Updates the table data for the fluidId to fluidType table.
     */
    public void updateFluidTable() {
        if (data != null) {
            for (final DrawableDroplet droplet : bioViz.currentAssay
                    .getDroplets()) {
                final int dropletID = droplet.droplet.getID();
                final Integer fluidID = data.fluidID(dropletID);
                final String fluidName = data.fluidType(fluidID);
                if (fluidID != null && fluidName != null) {
                    fluidToTypeModel.addRow(new Object[]{fluidID, fluidName});
                }
            }
        }
    }

    /**
     * Updates the usage values.
     */
    public void updateUsage() {

        if (data != null) {
            data.computeCellUsage();

            int minUsage = Integer.MAX_VALUE;
            int maxUsage = 0;
            double avgUsage = 0;

            for (final BiochipField f : data.getAllFields()) {
                avgUsage += f.getUsage();
                if (maxUsage < f.getUsage()) {
                    maxUsage = f.getUsage();
                }
                if (minUsage > f.getUsage()) {
                    minUsage = f.getUsage();
                }
            }
            avgUsage /= data.getAllFields().size();

            avgUsageValue.setText(String.format("%.3f", avgUsage));
            minUsageValue.setText(String.valueOf(minUsage));
            maxUsageValue.setText(String.valueOf(maxUsage));
        }
    }

    /**
     * Updates the number of fields.
     */
    public void updateFieldCount() {
        int numFields = bioViz.currentAssay.getFields().size();
        fieldNumValue.setText(String.valueOf(numFields));
    }

    /**
     * Updates the net infos.
     */
    public void updateNets() {
        if (data != null) {
            int numNets = data.getNets().size();
            int numSources = 0;
            int numTargets = 0;
            for (final Net n : data.getNets()) {
                numSources += n.getSources().size();
                if (n.getTarget() != null) {
                    numTargets++;
                }
            }

            numNetValue.setText(String.valueOf(numNets));
            numSourcesValue.setText(String.valueOf(numSources));
            numTargetValue.setText(String.valueOf(numTargets));
        }
    }

    /**
     * Updates the infos for special field types.
     */
    public void updateFieldTypes() {
        if (data != null) {
            int numDispenser = 0;
            int numSinks = 0;
            int numDetectors = 0;
            for (final BiochipField f : data.getAllFields()) {
                if (f instanceof Dispenser) {
                    numDispenser++;
                }
                if (f instanceof Sink) {
                    numSinks++;
                }
                if (f.getDetector() != null) {
                    numDetectors++;
                }
            }
            numDispensersValue.setText(String.valueOf(numDispenser));
            numSinksValue.setText(String.valueOf(numSinks));
            numDetectorsValue.setText(String.valueOf(numDetectors));
        }
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
    }

	/**
   * This implements a table model for the tables.
   */
  private class OurTableModel extends DefaultTableModel {

		/**
     * Stores the columnNames.
     */
    private final String[] columnNames;

		/**
     * Creates a new table model with the given columnNames.
     * @param columnNames the column names
     */
    OurTableModel(final String[] columnNames) {
            this.columnNames = columnNames;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(final int index) {
            return columnNames[index];
        }

        @Override
        public boolean isCellEditable(final int col, final int row) {
            return false;
        }
    }

}
