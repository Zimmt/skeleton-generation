package util;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.ParseException;

public class GUI {

    private final JButton startButton;

    private JCheckBox createVariations;

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
    private JComboBox<String> resolution;
    private JCheckBox saveToFile;
    private JTextField saveFileName;

    public GUI(ActionListener startButtonListener) {
        JFrame frame = new JFrame("Skeleton Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        JPanel userInputPanel = initializeUserInputPanel();

        this.startButton = new JButton("start generator");
        startButton.addActionListener(startButtonListener);

        frame.getContentPane().add(BorderLayout.NORTH, userInputPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, startButton);
        frame.setVisible(true);
    }

    public boolean getCreateVariationsInput() {
        return createVariations.isSelected();
    }

    public Integer getLegInput() {
        return (Integer) legInputField.getValue();
    }

    public Integer getWingInput() {
        return (Integer) wingInputField.getValue();
    }

    public Integer getArmInput() {
        return (Integer) armInputField.getValue();
    }

    public Integer getFinInput() {
        return (Integer) finInputField.getValue();
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

    public boolean getAllCubes() {
        return resolution.getSelectedItem().equals("only bounding boxes");
    }

    public boolean getLowResoultion() {
        return resolution.getSelectedItem().equals("low");
    }

    public boolean getSaveToFile() {
        return saveToFile.isSelected();
    }

    public String getSaveFileName() {
        return saveFileName.getText();
    }


    private JPanel initializeUserInputPanel() {
        JPanel userInputPanel = new JPanel();
        userInputPanel.setLayout(new BoxLayout(userInputPanel, BoxLayout.Y_AXIS));

        JPanel fromFilePanel = initializeFromFilePanel();
        JSeparator separator1 = new JSeparator();
        JPanel algorithmConstraintsPanel = initializeAlgorithmConstraintsPanel();
        JSeparator separator2 = new JSeparator();
        JPanel otherInputPanel = initializeOtherInputPanel();

        userInputPanel.add(fromFilePanel);
        userInputPanel.add(separator1);
        userInputPanel.add(algorithmConstraintsPanel);
        userInputPanel.add(separator2);
        userInputPanel.add(otherInputPanel);
        return userInputPanel;
    }

    private JPanel initializeFromFilePanel() {
        this.createVariations = new JCheckBox();

        String[] labels = new String[] {"create variations"};
        Component[] inputComponents = new Component[] {createVariations};
        return constructGridLayout(labels, inputComponents);
    }

    private JPanel initializeAlgorithmConstraintsPanel() {
        this.legInputField = constructIntTextField(4);
        this.wingInputField = constructIntTextField(4);
        this.armInputField = constructIntTextField(4);
        this.finInputField = constructIntTextField(6);
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
        this.skeletonCount = constructIntTextField(1000);
        skeletonCount.setValue(1);
        this.resolution = new JComboBox<>(new String[] {"only bounding boxes", "low", "high"});
        this.saveToFile = new JCheckBox();
        this.saveFileName = new JTextField();

        JPanel saveToFilePanel = new JPanel(new BorderLayout());
        saveToFilePanel.add(saveToFile, BorderLayout.LINE_START);
        saveToFilePanel.add(saveFileName, BorderLayout.CENTER);

        String[] labels = new String[] {"number of skeletons to generate", "resolution", "save to file"};
        Component[] inputComponents = new Component[] {skeletonCount, resolution, saveToFilePanel};

        return constructGridLayout(labels, inputComponents);
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

    private JFormattedTextField constructIntTextField(int maxValue) {
        JFormattedTextField textField = new JFormattedTextField(new IntFormatter(maxValue));
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

        public IntFormatter(int max) {
            super();
            this.max = max;
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
