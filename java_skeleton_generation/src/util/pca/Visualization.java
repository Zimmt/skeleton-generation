package util.pca;

import javax.swing.*;
import javax.vecmath.Point2d;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class Visualization extends Canvas {

    PcaDataPoint toShow;

    public static Visualization initialize() {

        JFrame frame = new JFrame("test frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Visualization visualization = new Visualization();
        visualization.setSize(2000, 1000);
        frame.add(visualization);
        frame.pack();
        frame.setVisible(true);

        return visualization;
    }

    public void showPoint(PcaDataPoint point) {
        this.toShow = point;
    }

    // is called automatically whenever the canvas needs to be drawn; for example, when the window is moved or resized
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 15));
        g2d.setColor(Color.BLACK);
        g2d.drawString(String.format("floored legs: %.3f", toShow.getFlooredLegs()), 50, 65);
        g2d.drawString(String.format("length front legs: %.3f", toShow.getLengthFrontLegs()), 50, 90);
        g2d.drawString(String.format("length back legs: %.3f", toShow.getLengthBackLegs()), 50, 115);
        g2d.drawString(String.format("weight: %.3f", toShow.getWeight()), 50, 140);

        List<Point2d> neckPoints = toShow.getNeck();
        CubicCurve2D.Double neck = new CubicCurve2D.Double(
                neckPoints.get(0).x, 1000 - neckPoints.get(0).y,
                neckPoints.get(1).x, 1000 - neckPoints.get(1).y,
                neckPoints.get(2).x, 1000 - neckPoints.get(2).y,
                neckPoints.get(3).x, 1000 - neckPoints.get(3).y);
        g2d.setColor(Color.BLUE);
        g2d.draw(neck);

        List<Point2d> backPoints = toShow.getBack();
        CubicCurve2D.Double back = new CubicCurve2D.Double(
                backPoints.get(0).x, 1000 - backPoints.get(0).y,
                backPoints.get(1).x, 1000 - backPoints.get(1).y,
                backPoints.get(2).x, 1000 - backPoints.get(2).y,
                backPoints.get(3).x, 1000 - backPoints.get(3).y);
        g2d.setColor(Color.GREEN);
        g2d.draw(back);

        List<Point2d> tailPoints = toShow.getTail();
        CubicCurve2D.Double tail = new CubicCurve2D.Double(
                tailPoints.get(0).x, 1000 - tailPoints.get(0).y,
                tailPoints.get(1).x, 1000 - tailPoints.get(1).y,
                tailPoints.get(2).x, 1000 - tailPoints.get(2).y,
                tailPoints.get(3).x, 1000 - tailPoints.get(3).y);
        g2d.setColor(Color.RED);
        g2d.draw(tail);

        g2d.setColor(Color.BLACK);
        g2d.draw(new Rectangle2D.Double(1,1,999,999));
        this.setSize(1000, 1000);
    }
}
