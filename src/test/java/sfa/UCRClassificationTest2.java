// Copyright (c) 2016 - Patrick Schäfer (patrick.schaefer@zib.de)
// Distributed under the GLP 3.0 (See accompanying file LICENSE)
package sfa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sfa.classification.Classifier;
import sfa.classification.WEASELClassifier;
import sfa.classification.BOSSVSClassifier;
import sfa.timeseries.TimeSeries;
import sfa.timeseries.TimeSeriesLoader;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@RunWith(JUnit4.class)
public class UCRClassificationTest2 {


    /**
     * $ export _JAVA_OPTIONS="-Xms20000m -Xmx40000m"
     * $ gradle test --tests sfa.UCRClassificationTest
     * <p>
     * export _JAVA_OPTIONS="-Xms40000m -Xmx80000m"
     * gradle test --tests sfa.UCRClassificationTest2
     */

    // The datasets to use
    public static String[] datasets = new String[]{
            "Coffee",
            "NLOS-6/2_classes_WIFI",
            "NLOS-6/3_classes_WIFI",
            "NLOS-6/4_classes_WIFI",
            "NLOS-6/5_classes_WIFI",
            "NLOS-6/6_classes_WIFI",
//            "2_classes_WIFI_len_796",
//            "2_classes_WIFI_len_1596",
//            "2_classes_WIFI_len_3996",
//            "2_classes_WIFI_len_7996",
            // "NonInvasiveFatalECG_Thorax2",
            // "2_classes_WIFI_small",
//            "2_classes_WIFI",
//            "3_classes_WIFI",
//            "4_classes_WIFI",
//            "5_classes_WIFI",
//            "6_classes_WIFI",
            "NLOS-10/2_classes_WIFI",
            "NLOS-10/3_classes_WIFI",
            "NLOS-10/4_classes_WIFI",
            "NLOS-10/5_classes_WIFI",
            "NLOS-10/6_classes_WIFI",
            "NLOS-15/2_classes_WIFI",
            "NLOS-15/3_classes_WIFI",
            "NLOS-15/4_classes_WIFI",
            "NLOS-15/5_classes_WIFI",
            "NLOS-15/6_classes_WIFI",
            "LOS-15/2_classes_WIFI",
            "LOS-15/3_classes_WIFI",
            "LOS-15/4_classes_WIFI",
            "LOS-15/5_classes_WIFI",
            "LOS-15/6_classes_WIFI",
            "LOS-10/6_classes_WIFI",
            "LOS-10/5_classes_WIFI",
            "LOS-10/4_classes_WIFI",
            "LOS-10/3_classes_WIFI",
            "LOS-10/2_classes_WIFI",
            "LOS-6/2_classes_WIFI",
            "LOS-6/3_classes_WIFI",
            "LOS-6/4_classes_WIFI",
            "LOS-6/5_classes_WIFI",
            "LOS-6/6_classes_WIFI",
//            "LOS-10/2_classes_WIFI",
//            "LOS-10/3_classes_WIFI",
//            "LOS-10/4_classes_WIFI",
//            "LOS-10/5_classes_WIFI",
//            "LOS-10/6_classes_WIFI",
//            "LOS-15/2_classes_WIFI",
//            "LOS-15/3_classes_WIFI",
//            "LOS-15/4_classes_WIFI",
//            "LOS-15/5_classes_WIFI",
//            "LOS-15/6_classes_WIFI",
//          "Beef", "CBF",
//          "ECG200",
//          "FaceFour", "OliveOil",
//          "Gun_Point",
//          "DiatomSizeReduction",
//          "ECGFiveDays",
//          "TwoLeadECG",
//          "SonyAIBORobotSurfaceII",
//          "MoteStrain",
//          "ItalyPowerDemand",
//          "SonyAIBORobotSurface",
    };

    public static void showHeapSize() {
        //Get the jvm heap size.
        long heapSize = Runtime.getRuntime().totalMemory();

        //Print the jvm heap size.
        System.out.println("Heap Size = " + heapSize);
    }

    @Test
    public void testUCRClassification() throws IOException {
        showHeapSize();

        // the relative path to the datasets
        ClassLoader classLoader = SFAWordsTest.class.getClassLoader();

        File dir = new File(classLoader.getResource("datasets/univariate/").getFile());
        //File dir = new File("/Users/bzcschae/workspace/similarity/datasets/classification");

        for (String s : datasets) {
            System.out.println("dataset: " + s);
            Instant start = Instant.now();
            File d = new File(dir.getAbsolutePath() + "/" + s);
            if (d.exists() && d.isDirectory()) {
                for (File train : d.listFiles()) {
                    if (train.getName().toUpperCase().endsWith("TRAIN")) {
                        File test = new File(train.getAbsolutePath().replaceFirst("TRAIN", "TEST"));

                        if (!test.exists()) {
                            System.err.println("File " + test.getName() + " does not exist");
                            test = null;
                        }

                        Classifier.DEBUG = false;

                        // Load the train/test splits
                        TimeSeries[] testSamples = TimeSeriesLoader.loadDataset(test);
                        TimeSeries[] trainSamples = TimeSeriesLoader.loadDataset(train);

                        // The WEASEL-classifier
//                        Instant begin = Instant.now();
//                        Classifier w = new WEASELClassifier();
//                        Classifier.Score scoreW = w.eval(trainSamples, testSamples);
//                        Instant end = Instant.now();
//                        long timeElapsed = Duration.between(begin, end).getSeconds();
//                        System.out.println(s + ";" + scoreW.toString() + ";" + timeElapsed);

                        // The BOSS VS classifier
                        Instant begin = Instant.now();
                        Classifier bossVS = new BOSSVSClassifier();
                        Classifier.Score scoreBOSSVS = bossVS.eval(trainSamples, testSamples);
                        Instant end = Instant.now();
                        long timeElapsed = Duration.between(begin, end).getSeconds();
                        System.out.println(s + ";" + scoreBOSSVS.toString() + ";" + timeElapsed);
                        System.out.flush();

//                        // The BOSS ensemble classifier
//                        Classifier boss = new BOSSEnsembleClassifier();
//                        Classifier.Score scoreBOSS = boss.eval(trainSamples, testSamples);
//                        System.out.println(s + ";" + scoreBOSS.toString());
//
//                        // The Shotgun ensemble classifier
//                        Classifier shotgunEnsemble = new ShotgunEnsembleClassifier();
//                        Classifier.Score scoreShotgunEnsemble = shotgunEnsemble.eval(trainSamples, testSamples);
//                        System.out.println(s + ";" + scoreShotgunEnsemble.toString());
//
//                        // The Shotgun classifier
//                        Classifier shotgun = new ShotgunClassifier();
//                        Classifier.Score scoreShotgun = shotgun.eval(trainSamples, testSamples);
//                        System.out.println(s + ";" + scoreShotgun.toString());
                    }
                }
            } else {
                // not really an error. just a hint:
                System.out.println("Dataset could not be found: " + d.getAbsolutePath() + ". " +
                        "Please download datasets from [http://www.cs.ucr.edu/~eamonn/time_series_data/].");
            }
            Instant end = Instant.now();
            long timeElapsed = Duration.between(start, end).getSeconds();
            System.out.println("Elapsed time: " + timeElapsed + " sec");
        }
    }

    public static void main(String[] args) throws IOException {
        UCRClassificationTest2 ucr = new UCRClassificationTest2();
        ucr.testUCRClassification();
    }
}
