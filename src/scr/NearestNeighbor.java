package scr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NearestNeighbor {

    private List<Sample> trainingData;
    private KDTree kdtree;
    private int[] classCounts; 
    private String firstLineOfTheFile;
    private int numeroFeature;
    private Normalizer normalizer;

    public NearestNeighbor(String filename) {
        this.trainingData = new ArrayList<>();
        this.classCounts = new int[10];
        this.firstLineOfTheFile = "ANGLE;SPEED_X;SPEED_Y;TRACK_-45;TRACK_-20;TRACK_0;TRACK_20;TRACK_45;TRACK_POS;CLASS";
        this.readPointsFromCSV(filename);


        this.numeroFeature = trainingData.get(0).getFeatures().length;

        this.normalizer = new Normalizer();

        normalizer.defineMinMax(trainingData);

        normalizer.normalizeTrainingData(trainingData);

        this.kdtree = new KDTree(trainingData);
    }

    private void readPointsFromCSV(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(firstLineOfTheFile)) {
                    continue;
                }

                trainingData.add(new Sample(line));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Sample> findKNearestNeighbors(Sample testPoint, int k) {
        return kdtree.kNearestNeighbors(testPoint, k);
    }

    public int classify(Sample testPoint, int k) {

        normalizer.normalizeSample(testPoint, numeroFeature);

        List<Sample> kNearestNeighbors = findKNearestNeighbors(testPoint, k);


        for (int i = 0; i < classCounts.length; i++) {
            classCounts[i] = 0;
        }


        for (Sample neighbor : kNearestNeighbors) {
            classCounts[neighbor.cls]++;
        }


        int maxCount = -1;
        int predictedClass = -1;
        for (int i = 0; i < classCounts.length; i++) {
            if (classCounts[i] > maxCount) {
                maxCount = classCounts[i];
                predictedClass = i;
            }
        }

        return predictedClass;
    }

    public List<Sample> getTrainingData() {
        return trainingData;
    }
}
