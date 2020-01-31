package util.pca;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataExporter {

    List<PcaDataPoint>  data;

    public DataExporter(List<PcaDataPoint> data) {
        this.data = data;
    }

    public void projectAndExportToFile(String filePathAndName, RealVector xDimension, RealVector yDimension, RealVector zDimension) throws IOException {
        File file = new File(filePathAndName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("# Projected PCA examples");
        writer.newLine();

        RealVector mean = new ArrayRealVector(PcaDataPoint.getMean(data).getScaledDataForPCA());
        for (PcaDataPoint point : data) {
            RealVector rawPoint = new ArrayRealVector(point.getScaledDataForPCA()).subtract(mean);
            double x = rawPoint.dotProduct(xDimension);
            double y = rawPoint.dotProduct(yDimension);
            double z = rawPoint.dotProduct(zDimension);

            writer.write("" + x + " " +  y + " " + z);
            writer.newLine();
        }
        writer.write("pause -1");
        writer.close();
    }
}
