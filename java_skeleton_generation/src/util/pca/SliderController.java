package util.pca;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class SliderController extends JPanel implements ChangeListener {

    private String sliderTitle;
    private double currentValue = 0.0;
    private JLabel label;

    public SliderController(String sliderTitle, double min, double max, Visualization parent) {
        this.sliderTitle = sliderTitle;
        JSlider slider = new JSlider(JSlider.HORIZONTAL, (int) Math.ceil(min*100), (int) Math.ceil(max*100), 0);
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
            currentValue = (double) source.getValue() / 100.0;
            label.setText(this.sliderTitle + ": " + currentValue);
        }
    }

    public double getCurrentValue() {
        return currentValue;
    }
}
