package util;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.ParseException;

public class GUI {

    private final JButton startButton;

    private final JFormattedTextField legInputField;
    private final JFormattedTextField wingInputField;
    private final JFormattedTextField armInputField;
    private final JFormattedTextField finInputField;

    public GUI(ActionListener startButtonListener) {
        JFrame frame = new JFrame("Skeleton Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        this.legInputField = constructIntTextField(4);
        this.wingInputField = constructIntTextField(4);
        this.armInputField = constructIntTextField(4);
        this.finInputField = constructIntTextField(6);
        String[] labels = new String[] {
                "floored legs",
                "wings",
                "arms",
                "fins"
        };
        JFormattedTextField[] textFields = new JFormattedTextField[] {
                legInputField,
                wingInputField,
                armInputField,
                finInputField
        };
        JPanel userInputPanel = constructBoxLayout(labels, textFields);

        this.startButton = new JButton("start generator");
        startButton.addActionListener(startButtonListener);

        frame.getContentPane().add(BorderLayout.NORTH, userInputPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, startButton);
        frame.setVisible(true);
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

    private JPanel constructBoxLayout(String[] labelTexts, JFormattedTextField[] textFields) {
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
        textField.setColumns(3);
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

        public Integer stringToValue(String string) throws ParseException {
            try {
                int value = Integer.parseInt(string);
                if (value > max) {
                    value = max;
                }
                return value;
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
