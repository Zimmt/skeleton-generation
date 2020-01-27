package util.pca;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealVector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Point2d;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

/**
 * Does the visualisation of the PCA results.
 * Shows the current data point and has sliders to change the point along the eigenvectors
 */
public class Visualization extends Canvas implements ChangeListener {

    private EigenDecomposition eigenDecomposition;
    private PcaDataPoint mean;
    private Integer[] sortedEigenvalueIndices;

    private SliderController[] sliders = new SliderController[6];

    private Visualization(EigenDecomposition ed, PcaDataPoint mean) {
        this.eigenDecomposition = ed;
        this.mean = mean;

        double[] eigenvalues = ed.getRealEigenvalues();
        Integer[] sortedEigenvalueIndices = new Integer[eigenvalues.length];
        for (int i = 0; i < sortedEigenvalueIndices.length; i++) {
            sortedEigenvalueIndices[i] = i;
        }
        Arrays.sort(sortedEigenvalueIndices, (o1, o2) -> -Double.compare(eigenvalues[o1], eigenvalues[o2]));
        this.sortedEigenvalueIndices = sortedEigenvalueIndices;

        for (int i = 0; i < sliders.length; i++) {
            sliders[i] =  new SliderController(
                    String.format("Eigenvector %x (Eigenvalue: %.3f)", i+1, eigenDecomposition.getRealEigenvalue(sortedEigenvalueIndices[i])),
                    -1.5, 1.5, this);
        }
        setSize(1010, 1010);

    }

    public static Visualization start(EigenDecomposition ed, PcaDataPoint mean) {

        JFrame frame = new JFrame("PCA Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1700, 1010));
        frame.setLayout(new FlowLayout());

        Visualization visualization = new Visualization(ed, mean);
        frame.add(visualization);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);
        for (SliderController slider : visualization.sliders) {
            panel.add(slider);
        }

        frame.pack();
        frame.setVisible(true);

        return visualization;
    }

    /**
     * triggered when a slider changes its state
     */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            repaint();
        }
    }

    // is called automatically whenever the canvas needs to be redrawn
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));
        PcaDataPoint pointToDraw = findPointToDraw();

        paintTextData(pointToDraw, g2d);
        paintSpine(pointToDraw, g2d);
        paintArm(pointToDraw, g2d);
        paintLeg(pointToDraw, g2d);
    }

    private PcaDataPoint findPointToDraw() {

        // mapMultiply does not change eigenvector, generates new one
        RealVector[] scaledEigenvectors = new RealVector[sliders.length];
        for (int i = 0; i < scaledEigenvectors.length; i++) {
            scaledEigenvectors[i] = eigenDecomposition.getEigenvector(i).mapMultiply(sliders[i].getCurrentValue());
        }
        PcaDataPoint pointToDraw = mean;
        for (RealVector scaledEigenvector : scaledEigenvectors) {
            pointToDraw = pointToDraw.add(scaledEigenvector);
        }

        return pointToDraw;
    }

    private void paintTextData(PcaDataPoint pointToDraw, Graphics2D g2d) {
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 15));
        g2d.setColor(Color.BLACK);
        int yPosition = 65;

        g2d.drawString(String.format("wings: %.3f", pointToDraw.getWings()), 50, yPosition); yPosition += 25;
        g2d.drawString(String.format("floored legs: %.3f", pointToDraw.getFlooredLegs()), 50, yPosition); yPosition += 25;
        g2d.drawString(String.format("length upper arm: %.3f", pointToDraw.getLengthUpperArm()), 50, yPosition); yPosition += 25;
        g2d.drawString(String.format("length lower arm: %.3f", pointToDraw.getLengthLowerArm()), 50, yPosition); yPosition += 25;
        g2d.drawString(String.format("length hand: %.3f", pointToDraw.getLengthHand()), 50, yPosition); yPosition += 25;
        g2d.drawString(String.format("length upper leg: %.3f", pointToDraw.getLengthUpperLeg()), 50, yPosition); yPosition += 25;
        g2d.drawString(String.format("length lower leg: %.3f", pointToDraw.getLengthLowerLeg()), 50, yPosition); yPosition += 25;
        g2d.drawString(String.format("length foot: %.3f", pointToDraw.getLengthFoot()), 50, yPosition); yPosition += 25;
        g2d.drawString(String.format("weight: %.3f", pointToDraw.getWeight()), 50, yPosition);
    }

    private void paintSpine(PcaDataPoint pointToDraw, Graphics2D g2d) {
        List<Point2d> spinePoints = pointToDraw.getSpine();
        CubicCurve2D.Double neck = new CubicCurve2D.Double(
                spinePoints.get(0).x, 1000 - spinePoints.get(0).y,
                spinePoints.get(1).x, 1000 - spinePoints.get(1).y,
                spinePoints.get(2).x, 1000 - spinePoints.get(2).y,
                spinePoints.get(3).x, 1000 - spinePoints.get(3).y);
        g2d.setColor(Color.BLUE);
        g2d.draw(neck);

        CubicCurve2D.Double back = new CubicCurve2D.Double(
                spinePoints.get(3).x, 1000 - spinePoints.get(3).y,
                spinePoints.get(4).x, 1000 - spinePoints.get(4).y,
                spinePoints.get(5).x, 1000 - spinePoints.get(5).y,
                spinePoints.get(6).x, 1000 - spinePoints.get(6).y);
        g2d.setColor(Color.GREEN);
        g2d.draw(back);

        CubicCurve2D.Double tail = new CubicCurve2D.Double(
                spinePoints.get(6).x, 1000 - spinePoints.get(6).y,
                spinePoints.get(7).x, 1000 - spinePoints.get(7).y,
                spinePoints.get(8).x, 1000 - spinePoints.get(8).y,
                spinePoints.get(9).x, 1000 - spinePoints.get(9).y);
        g2d.setColor(Color.RED);
        g2d.draw(tail);
    }

    private void paintArm(PcaDataPoint pointToDraw, Graphics2D g2d) {
        List<Point2d> spinePoints = pointToDraw.getSpine();

        g2d.setColor(Color.ORANGE);
        Point2d elbow = new Point2d(spinePoints.get(3).x, spinePoints.get(3).y - pointToDraw.getLengthUpperArm());
        g2d.draw(new Line2D.Double(spinePoints.get(3).x, 1000 - spinePoints.get(3).y,
                elbow.x, 1000 - elbow.y));

        g2d.setColor(Color.MAGENTA);
        Point2d wrist = new Point2d(elbow.x, elbow.y - pointToDraw.getLengthLowerArm());
        g2d.draw(new Line2D.Double(elbow.x, 1000 - elbow.y, wrist.x, 1000 - wrist.y));

        g2d.setColor(Color.GREEN);
        g2d.draw(new Line2D.Double(wrist.x, 1000-wrist.y, wrist.x - pointToDraw.getLengthHand(), 1000 - wrist.y));
    }

    private void paintLeg(PcaDataPoint pointToDraw, Graphics2D g2d) {
        List<Point2d> spinePoints = pointToDraw.getSpine();

        g2d.setColor(Color.ORANGE);
        Point2d knee = new Point2d(spinePoints.get(6).x, spinePoints.get(6).y - pointToDraw.getLengthUpperLeg());
        g2d.draw(new Line2D.Double(spinePoints.get(6).x, 1000 - spinePoints.get(6).y,
                knee.x, 1000 - knee.y));

        g2d.setColor(Color.MAGENTA);
        Point2d ankle = new Point2d(knee.x, knee.y - pointToDraw.getLengthLowerLeg());
        g2d.draw(new Line2D.Double(knee.x, 1000 - knee.y, ankle.x, 1000 - ankle.y));

        g2d.setColor(Color.GREEN);
        g2d.draw(new Line2D.Double(ankle.x, 1000-ankle.y, ankle.x - pointToDraw.getLengthFoot(), 1000 - ankle.y));

        g2d.setColor(Color.BLACK);
        g2d.draw(new Rectangle2D.Double(1,1,1000,1000));
    }
}
