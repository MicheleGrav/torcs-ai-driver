package scr;

import java.util.Arrays;
import java.util.List;

public class Normalizer{

    private double[] min;
    private double[] max;

    public void defineMinMax(List<Sample> data) {
        int numFeatures = data.get(0).getFeatures().length;

        min = new double[numFeatures];
        max = new double[numFeatures];

        Arrays.fill(min, Double.MAX_VALUE);
        Arrays.fill(max, -Double.MAX_VALUE);

        for (Sample sample : data) {
            double[] features = sample.getFeatures();
            for (int i = 0; i < numFeatures; i++) {
                if (features[i] < min[i]) {
                    min[i] = features[i];
                }
                if (features[i] > max[i]) {
                    max[i] = features[i];
                }
            }
        }
    }

    public void normalizeSample(Sample sample, int numFeatures){
        double[] features = sample.getFeatures();
        for (int i = 0; i < numFeatures; i++) {
            features[i] = (features[i] - min[i]) / (max[i] - min[i]);
        }
        sample.setFeatures(features);
    }

    public void normalizeTrainingData(List<Sample> data) {
        int numFeatures = data.get(0).getFeatures().length;

        for (Sample sample : data) {
            normalizeSample(sample, numFeatures);
        }
    }
}
