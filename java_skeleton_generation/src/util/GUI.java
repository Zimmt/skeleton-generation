package util;

import jv.loader.PjImportModel;
import jv.project.PgGeometryIf;
import jv.vecmath.PdVector;
import jv.viewer.PvDisplay;
import jv.viewer.PvViewer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;

public class GUI {
    private String[] exampleNames;
    private ActionListener startButtonListener;

    private JCheckBox readFromFile;
    private JTextField inputFilePath;
    private JButton chooseInputFileButton;
    private JFileChooser inputFileChooser;
    private JCheckBox constructFromExample;
    private JComboBox<String> exampleName;
    private JCheckBox createVariations;

    private JPanel algorithmConstraintsPanel;
    private JFormattedTextField legInputField;
    private JFormattedTextField wingInputField;
    private JFormattedTextField armInputField;
    private JFormattedTextField finInputField;
    private JFormattedTextField neckYLengthInputField;
    private JFormattedTextField tailXLengthInputField;
    private JCheckBox twoExtremitiesPerGirdleAllowed;
    private JComboBox<String> secondShoulder;
    private JComboBox<String> headKind;

    private JFormattedTextField skeletonCount;
    private JTextField skeletonFileName;
    private JComboBox<String> resolution;
    private JCheckBox saveToFile;
    private JTextField metaDataFileName;
    private JFileChooser skeletonFileChooser;

    private JButton startButton;

    public GUI(String[] exampleNames, ActionListener startButtonListener) {
        this.exampleNames = exampleNames;
        this.startButtonListener = startButtonListener;
    }

    public void startGUI() {
        JFrame frame = new JFrame("Skeleton Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel userInputPanel = initializeUserInputPanel(exampleNames);

        this.startButton = new JButton("start generator");
        startButton.addActionListener(startButtonListener);

        frame.getContentPane().add(BorderLayout.NORTH, userInputPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, startButton);
        frame.pack();
        frame.setSize(600, 700);
        frame.setVisible(true);
    }

    public boolean getReadFromFile() {
        return readFromFile.isSelected();
    }

    public String getInputFilePath() {
        return inputFilePath.getText();
    }

    public boolean getConstructFromExample() {
        return constructFromExample.isSelected();
    }

    public String getPcaDataPointName() {
        return (String) exampleName.getSelectedItem();
    }

    public boolean getCreateVariationsInput() {
        return createVariations.isSelected();
    }

    public Integer getLegInput() {
        return legInputField.getValue() == null ? null : (Integer) legInputField.getValue() / 2;
    }

    public Integer getWingInput() {
        return wingInputField.getValue() == null ? null : (Integer) wingInputField.getValue() / 2;
    }

    public Integer getArmInput() {
        return armInputField.getValue() == null ? null : (Integer) armInputField.getValue() / 2;
    }

    public Integer getFinInput() {
        return finInputField.getValue() == null ? null : (Integer) finInputField.getValue() / 2;
    }

    public Double getNeckInput() {
        return (Double) neckYLengthInputField.getValue();
    }

    public Double getTailInput() {
        return (Double) tailXLengthInputField.getValue();
    }

    public boolean getTwoExtremitiesPerGirdleAllowed() {
        return twoExtremitiesPerGirdleAllowed.isSelected();
    }

    public Boolean getSecondShoulderInput() {
        switch ((String) secondShoulder.getSelectedItem()) {
            case "allowed": return null;
            case "disallowed": return false;
            case "enforced": return true;
            default:
                System.err.println("invalid value!");
                return null;
        }
    }

    public String getHeadKindInput() {
        return (String) headKind.getSelectedItem();
    }

    public int getSkeletonCount() {
        Integer count = (Integer) skeletonCount.getValue();
        if (count == null || count <= 0) {
            return 1;
        }
        return count;
    }

    public String getSkeletonFileName() {
        return skeletonFileName.getText();
    }

    public boolean getAllCubes() {
        return resolution.getSelectedItem().equals("only bounding boxes");
    }

    public boolean getLowResoultion() {
        return resolution.getSelectedItem().equals("low");
    }

    public boolean getSaveToFile() {
        return saveToFile.isSelected();
    }

    public String getMetaDataFileName() {
        return metaDataFileName.getText();
    }


    private JPanel initializeUserInputPanel(String[] exampleNames) {
        JPanel userInputPanel = new JPanel();
        userInputPanel.setLayout(new BoxLayout(userInputPanel, BoxLayout.Y_AXIS));

        JPanel fromFilePanel = initializeFromFilePanel(exampleNames);
        JSeparator separator1 = new JSeparator();
        this.algorithmConstraintsPanel = initializeAlgorithmConstraintsPanel();
        JSeparator separator2 = new JSeparator();
        JPanel otherInputPanel = initializeOtherInputPanel();

        userInputPanel.add(fromFilePanel);
        userInputPanel.add(separator1);
        userInputPanel.add(algorithmConstraintsPanel);
        userInputPanel.add(separator2);
        userInputPanel.add(otherInputPanel);
        return userInputPanel;
    }

    private JPanel initializeFromFilePanel(String[] exampleNames) {
        this.readFromFile = new JCheckBox();
        this.inputFilePath = new JTextField();
        inputFilePath.setEnabled(false);
        this.chooseInputFileButton = new JButton("choose");
        chooseInputFileButton.addActionListener(this::chooseInputFile);
        chooseInputFileButton.setEnabled(false);
        this.inputFileChooser = new JFileChooser(".");
        inputFileChooser.setFileFilter(new FileNameExtensionFilter("txt files","txt"));

        JPanel loadFilePanel = new JPanel(new BorderLayout());
        loadFilePanel.add(readFromFile, BorderLayout.LINE_START);
        loadFilePanel.add(inputFilePath, BorderLayout.CENTER);
        loadFilePanel.add(chooseInputFileButton, BorderLayout.LINE_END);

        this.constructFromExample = new JCheckBox();
        this.exampleName = new JComboBox<>(exampleNames);
        exampleName.setEnabled(false);
        JPanel constructFromExamplePanel = new JPanel(new BorderLayout());
        constructFromExamplePanel.add(constructFromExample, BorderLayout.LINE_START);
        constructFromExamplePanel.add(exampleName, BorderLayout.CENTER);

        this.createVariations = new JCheckBox();
        createVariations.setEnabled(false);

        readFromFile.addActionListener(e -> {
            boolean enabled = readFromFile.isSelected();
            inputFilePath.setEnabled(enabled);
            chooseInputFileButton.setEnabled(enabled);
            if (enabled) constructFromExample.setSelected(false);
            createVariations.setEnabled(enabled || constructFromExample.isSelected());
            for (Component c : algorithmConstraintsPanel.getComponents()) {
                c.setEnabled(!enabled);
            }
        });
        constructFromExample.addActionListener(e -> {
            boolean enabled = constructFromExample.isSelected();
            exampleName.setEnabled(enabled);
            if (enabled) readFromFile.setSelected(false);
            createVariations.setEnabled(enabled || readFromFile.isSelected());
        });

        String[] labels = new String[] {"load from file", "construct from example", "create variations"};
        Component[] inputComponents = new Component[] {loadFilePanel, constructFromExamplePanel, createVariations};
        return constructGridLayout(labels, inputComponents);
    }

    private JPanel initializeAlgorithmConstraintsPanel() {
        this.legInputField = constructIntTextField(8, true);
        this.wingInputField = constructIntTextField(8, true);
        this.armInputField = constructIntTextField(8, true);
        this.finInputField = constructIntTextField(12, true);
        this.neckYLengthInputField = constructDoubleTextField();
        this.tailXLengthInputField = constructDoubleTextField();
        this.twoExtremitiesPerGirdleAllowed = new JCheckBox();
        this.secondShoulder = new JComboBox<>(new String[]{"allowed", "disallowed", "enforced"});
        this.headKind = new JComboBox<>(new String[]{"horse_skull"});

        String[] labels = new String[] {
                "floored legs",
                "wings",
                "arms",
                "fins",
                "neck y length",
                "tail x length",
                "2 extremities per girdle allowed?",
                "second shoulder on neck?",
                "head kind"
        };
        Component[] inputComponents = new Component[] {
                legInputField,
                wingInputField,
                armInputField,
                finInputField,
                neckYLengthInputField,
                tailXLengthInputField,
                twoExtremitiesPerGirdleAllowed,
                secondShoulder,
                headKind
        };
        return constructGridLayout(labels, inputComponents);
    }

    private JPanel initializeOtherInputPanel() {
        this.skeletonCount = constructIntTextField(1000, false);
        skeletonCount.setValue(1);
        this.skeletonFileName = new JTextField("skeleton");
        this.resolution = new JComboBox<>(new String[] {"only bounding boxes", "low", "high"});
        this.saveToFile = new JCheckBox();
        this.metaDataFileName = new JTextField();
        JButton chooseSkeletonButton = new JButton("choose skeleton");
        chooseSkeletonButton.addActionListener(this::chooseAndShowSkeleton);
        this.skeletonFileChooser = new JFileChooser(".");
        skeletonFileChooser.setFileFilter(new FileNameExtensionFilter("obj files", "obj"));

        JPanel saveToFilePanel = new JPanel(new BorderLayout());
        saveToFilePanel.add(saveToFile, BorderLayout.LINE_START);
        saveToFilePanel.add(metaDataFileName, BorderLayout.CENTER);

        String[] labels = new String[] {"number of skeletons to generate", "skeleton name", "resolution", "save metadata to file", "show skeleton"};
        Component[] inputComponents = new Component[] {skeletonCount, skeletonFileName, resolution, saveToFilePanel, chooseSkeletonButton};

        return constructGridLayout(labels, inputComponents);
    }

    private void chooseInputFile(ActionEvent e) {
        int chooserReturn = inputFileChooser.showOpenDialog(inputFilePath);
        if (chooserReturn == JFileChooser.APPROVE_OPTION) {
            File file = inputFileChooser.getSelectedFile();
            inputFilePath.setText(file.getPath());
        }
    }

    private void chooseAndShowSkeleton(ActionEvent e) {
        int chooserReturn = skeletonFileChooser.showOpenDialog(null);
        if (chooserReturn == JFileChooser.APPROVE_OPTION) {
            File file = skeletonFileChooser.getSelectedFile();

            PvViewer javaView = new PvViewer();

            PjImportModel importModel = new PjImportModel();
            importModel.load(file.getPath());
            PgGeometryIf geometry = importModel.getGeometry();

            PvDisplay pvDisplay = (PvDisplay) javaView.getDisplay();
            pvDisplay.addGeometry(geometry);
            pvDisplay.getCamera().setViewDir(new PdVector(0.0, 0.0, -1.0));
            pvDisplay.getLight().setIntensity(0.4);

            pvDisplay.setVisible(true);

            JDialog dialog = new JDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.add(pvDisplay);
            dialog.setSize(500, 500);
            dialog.setVisible(true);
        }
    }

    private JPanel constructGridLayout(String[] labelTexts, Component[] textFields) {
        if (labelTexts.length != textFields.length) {
            return null;
        }
        JPanel panel = new JPanel(new GridLayout(labelTexts.length, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        for (int i = 0; i < labelTexts.length; i++) {
            JLabel label = new JLabel(labelTexts[i]);
            panel.add(label);
            panel.add(textFields[i]);
        }
        return panel;
    }

    private JFormattedTextField constructIntTextField(int maxValue, boolean onlyEvenNumbers) {
        JFormattedTextField textField = new JFormattedTextField(new IntFormatter(maxValue, onlyEvenNumbers));
        textField.setColumns(5);
        return textField;
    }

    private JFormattedTextField constructDoubleTextField() {
        JFormattedTextField textField = new JFormattedTextField(new DoubleFormatter());
        textField.setColumns(5);
        return textField;
    }

    private static class IntFormatter extends DefaultFormatter {

        private final int max;
        private final boolean onlyEvenNumbers;

        public IntFormatter(int max, boolean onlyEvenNumbers) {
            super();
            this.max = max;
            this.onlyEvenNumbers = onlyEvenNumbers;
        }

        public String valueToString(Object object) throws ParseException {
            return super.valueToString(object);
        }

        public Integer stringToValue(String string) {
            try {
                int value = Integer.parseInt(string);
                if (value < 0) {
                    value = -value;
                }
                if (value > max) {
                    value = max;
                }
                if (onlyEvenNumbers && value % 2 != 0) {
                    value -= 1;
                }
                return value;
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    private static class DoubleFormatter extends DefaultFormatter {

        public String valueToString(Object object) throws ParseException {
            return super.valueToString(object);
        }

        public Double stringToValue(String string) {
            try {
                double value = Double.parseDouble(string);
                if (value < 0) {
                    value = -value;
                }
                return value;
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
