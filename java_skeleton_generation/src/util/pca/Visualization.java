package util.pca;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealVector;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Point2d;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Does the visualisation of the PCA results.
 * Shows the current data point and has sliders to change the point along the eigenvectors
 */
public class Visualization extends Canvas implements ChangeListener {

    private static int width = 1700;
    private static int height = 1010;

    private EigenDecomposition eigenDecomposition;
    private PcaDataPoint mean;
    private Integer[] sortedEigenvalueIndices;

    private JFrame frame;
    private SliderController[] sliders = new SliderController[6];

    private int defaultExportImageIndex = 1;

    private Visualization(EigenDecomposition ed, PcaDataPoint mean, JFrame frame) {
        this.eigenDecomposition = ed;
        this.mean = mean;
        this.frame = frame;

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
        setSize(height, height);

    }

    public static Visualization start(EigenDecomposition ed, PcaDataPoint mean) {

        JFrame frame = new JFrame("PCA Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(width, height));
        frame.setLayout(new FlowLayout());
        frame.setResizable(false);

        Visualization visualization = new Visualization(ed, mean, frame);
        frame.add(visualization);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);
        for (SliderController slider : visualization.sliders) {
            panel.add(slider);
        }
        Button exportButton = new Button("export to file");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    visualization.exportToImage("../PCA/temporary_visualization_exports/PCA_export"+visualization.defaultExportImageIndex +".jpg");
                    visualization.defaultExportImageIndex++;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(exportButton);

        frame.pack();
        frame.setVisible(true);

        return visualization;
    }

    public void exportImagesWithStandardDeviationSettings() throws IOException {
        for (int setting = 0; setting < sliders.length; setting++) {
            for (int i = 0; i < sliders.length; i++) {
                if (i == setting) {
                    sliders[i].setSliderValue(Math.sqrt(eigenDecomposition.getRealEigenvalue(sortedEigenvalueIndices[setting])));
                } else {
                    sliders[i].setSliderValue(0.0);
                }
            }
            exportToImage(String.format("../PCA/temporary_visualization_exports/Eigenvector%d_positive.jpg", setting+1));

            sliders[setting].setSliderValue(-Math.sqrt(eigenDecomposition.getRealEigenvalue(sortedEigenvalueIndices[setting])));
            exportToImage(String.format("../PCA/temporary_visualization_exports/Eigenvector%d_negative.jpg", setting+1));
        }
    }

    /**
     * @param settings scale factors for eigenvectors
     * @param filePath path to folder where files should be stored (with a '/' in the end
     * @param fileName name of the file (without the .jpg extension
     * @throws IOException
     */
    public void exportImagesWithEigenvectorSettings(List<double[]> settings, String filePath, String fileName) throws IOException {
        for (int s = 0; s < settings.size(); s++) {
            if (settings.get(s).length != sliders.length) {
                System.err.println("Found setting with incorrect number of factors");
            }
            for (int i = 0; i < sliders.length; i++) {
                sliders[i].setSliderValue(settings.get(s)[i]);
            }

            exportToImage(filePath + fileName + (s+1) + ".jpg");
            System.out.println("Exported image " + s);
        }
    }

    public void exportToImage(String filePath) throws IOException {
        // wait shortly so settings are done before drawing
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.err.println("Could not sleep");
        }

        // sets everything to double size to get a better resolution of the image, in the end it is turned back
        BufferedImage image = new BufferedImage(width*2, height*2, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.scale(2.0,2.0);
        this.setSize(height*2, height*2);

        frame.paint(g2d);
        this.paint(g2d);
        ImageIO.write(image, "jpg", new File(filePath));

        this.setSize(height, height);
        this.repaint();
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

    public void setSliderValues(double[] values) {
        for (int i = 0; i < sliders.length && i < values.length; i++) {
            sliders[i].setSliderValue(values[i]);
        }
    }

    private PcaDataPoint findPointToDraw() {

        // mapMultiply does not change eigenvector, generates new one
        RealVector[] scaledEigenvectors = new RealVector[sliders.length];
        for (int i = 0; i < scaledEigenvectors.length; i++) {
            scaledEigenvectors[i] = eigenDecomposition.getEigenvector(sortedEigenvalueIndices[i]).mapMultiply(sliders[i].getCurrentValue());
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
