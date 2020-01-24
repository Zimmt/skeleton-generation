package util.pca;

import javax.vecmath.Point2d;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PcaDataReader {

    /**
     * Reads input data from ./PCA/PCA_data.txt
     */
    public static List<PcaDataPoint> readInputData() throws IOException {
        System.out.print("Reading input data... ");
        File file = new File("../PCA/PCA_data.txt");
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        List<PcaDataPoint> dataPoints = new ArrayList<>();

        String line;
        while ((line = fileReader.readLine()) != null) {
            if (line.charAt(0) == '#') {
                continue;
            }

            String[] lineParts = line.split(",");
            if (lineParts.length != 6) {
                System.err.println("Found wrong number of attributes in line.");
                continue;
            }

            String name = lineParts[0];
            int animalClass = Integer.parseInt(lineParts[1]);
            boolean wings = Integer.parseInt(lineParts[2]) > 0;
            int flooredLegs = Integer.parseInt(lineParts[3]);
            boolean arms = Integer.parseInt(lineParts[4]) > 0;
            double weight = Double.parseDouble(lineParts[5]);

            PcaDataPoint dataPoint = new PcaDataPoint();
            dataPoint.setName(name);
            dataPoint.setAnimalClass(animalClass);
            dataPoint.setWings(wings ? 1.0 : 0.0);
            dataPoint.setFlooredLegs(flooredLegs);
            dataPoint.setArms(arms);
            dataPoint.setWeight(weight);

            String svgFilePath = "../PCA/Skelettbilder/" + name + ".svg";
            SvgReader svgReader = new SvgReader();
            List<ParsedSvgPath> parsedSvgPaths = svgReader.parseFile(svgFilePath);

            for (ParsedSvgPath path : parsedSvgPaths) {
                switch(path.getLabel()) {
                    case "neck":
                        dataPoint.setNeck(path.getPoints());
                        break;
                    case "back":
                        dataPoint.setBack(path.getPoints());
                        break;
                    case "tail":
                        dataPoint.setTail(path.getPoints());
                        break;
                    case "front_leg":
                        dataPoint.setLengthFrontLegs(getLength(path.getPoints()));
                        break;
                    case "back_leg":
                        dataPoint.setLengthBackLegs(getLength(path.getPoints()));
                        break;
                    case "wing":
                        dataPoint.setLengthWings(getLength(path.getPoints()));
                        break;
                    default:
                        System.err.println(path.getLabel() + " is no valid label.");
                }
            }

            if (!dataPoint.processData() || !dataPoint.dataSetMaybeComplete()) {
                System.err.println("Data missing.");
            }
            dataPoints.add(dataPoint);
        }
        System.out.println("Complete.");
        return dataPoints;
    }

    private static double getLength(List<Point2d> points) {
        if (points.size() != 2) {
            System.err.println("Wrong number of points to calculate length from!");
            return 0;
        }
        return points.get(0).distance(points.get(1));
    }
}
