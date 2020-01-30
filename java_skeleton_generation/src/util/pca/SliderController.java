package util.pca;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class SliderController extends JPanel implements ChangeListener {

    private static int precision = 100;

    private JSlider slider;
    private String sliderTitle;
    private double currentValue = 0.0;
    private JLabel label;

    public SliderController(String sliderTitle, double min, double max, Visualization parent) {
        this.sliderTitle = sliderTitle;
        this.slider = new JSlider(JSlider.HORIZONTAL, (int) Math.ceil(min*precision), (int) Math.ceil(max*precision), 0);
        slider.addChangeListener(this);
        slider.addChangeListener(parent);
        slider.setPreferredSize(new Dimension(500, 40));

        label = new JLabel(this.sliderTitle + ": " + 0.0);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(label);
        add(slider);
    }

    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            currentValue = (double) source.getValue() / (double) precision;
            label.setText(this.sliderTitle + ": " + currentValue);
        }
    }

    public void setSliderValue(double value) {
        slider.setValue((int) Math.ceil(value * precision));
    }

    public double getCurrentValue() {
        return currentValue;
    }
}
