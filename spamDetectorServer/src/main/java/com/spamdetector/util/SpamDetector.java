package com.spamdetector.util;

import com.spamdetector.domain.TestFile;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


/**
 * TODO: This class will be implemented by you
 * You may create more methods to help you organize you strategy and make you code more readable
 */
public class SpamDetector {

    Map<String, Integer> trainHamFreq;
    Map<String, Integer> trainSpamFreq;
    Map<String, Double> prSWiMap;

    int TruePositive = 0;
    int TrueNegative = 0;

    int FalsePositive = 0;
    int FalseNegative = 0;

    public SpamDetector() {
        trainHamFreq = new HashMap<>();
        trainSpamFreq = new HashMap<>();
        prSWiMap = new TreeMap<>();
    }

    private Map<String, Double> getPrSWiMap() {
        return prSWiMap;
    }

    public void train(String hamPath, String spamPath){
        readFiles(hamPath, false);
        readFiles(spamPath, true);
        computeProbabilities(hamPath, spamPath);
    }

    private void readFiles(String folderPath, boolean isSpam) {
        File folder = new File(folderPath);
        for (File file : folder.listFiles()) {
            Set<String> seenWords = new HashSet<>();
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNext()) {
                    String word = scanner.next().toLowerCase();
                    if (!seenWords.contains(word)) {
                        seenWords.add(word);
                        if(isWord(word)) {
                            if (isSpam) {
                                trainSpamFreq.put(word, trainSpamFreq.getOrDefault(word, 0) + 1);
                            } else {
                                trainHamFreq.put(word, trainHamFreq.getOrDefault(word, 0) + 1);

                            }
                        }
                    }

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void computeProbabilities(String ham, String spam)
    {
        File hams = new File(ham);
        File[] hamFiles = hams.listFiles();
        int hamLength = hamFiles.length;

        File spams = new File(spam);
        File[] spamFiles = spams.listFiles();
        int spamLength = spamFiles.length;

        Set<String> mergedKeys = new HashSet<>();
        mergedKeys.addAll(trainHamFreq.keySet());
        mergedKeys.addAll(trainSpamFreq.keySet());

        Iterator<String> keysIterator = mergedKeys.iterator();


        while(keysIterator.hasNext()){
            String key = keysIterator.next();

            if(isWord(key)){
                if (trainHamFreq.containsKey(key) && trainSpamFreq.containsKey(key)) {
                    double prWH = (double) trainHamFreq.get(key) / hamLength;
                    double prWS = (double) trainSpamFreq.get(key) / spamLength;
                    double prSW = prWS / (prWS + prWH);
                    prSWiMap.put(key, prSW);
                }
            }
        }

    }


    public List<TestFile> trainAndTest(File mainDirectory) throws IOException {
        // Set up the paths to the ham and spam directories within the main directory
        String hamFolderPath = mainDirectory.getAbsolutePath() + "/test/ham";
        String spamFolderPath = mainDirectory.getAbsolutePath() + "/test/spam";

        String hamFolderPathTrain = mainDirectory.getAbsolutePath() + "/train/ham";
        String spamFolderPathTrain = mainDirectory.getAbsolutePath() + "/train/spam";

        // Train the spam detector using the training data in the ham and spam directories
        SpamDetector trainer = new SpamDetector();
        trainer.train(hamFolderPathTrain, spamFolderPathTrain);

        // Test the spam detector on the files in the test/ham and test/spam directories
        List<TestFile> testFiles = new ArrayList<>();
        testFiles.addAll(testFolder(hamFolderPath, "ham", trainer));
        testFiles.addAll(testFolder(spamFolderPath, "spam", trainer));

        return testFiles;
    }


    private List<TestFile> testFolder(String folderPath, String actualClass, SpamDetector trainer) throws IOException {
        File folder = new File(folderPath);
        List<TestFile> testFiles = new ArrayList<>();

        for (File file : folder.listFiles()) {
            try (Scanner scanner = new Scanner(file)) {
                double spamProb = 0.0;

                // Compute the spam probability of the file based on the probabilities of its words
                while (scanner.hasNext()) {
                    String word = scanner.next().toLowerCase();
                    if(isWord(word)){
                        if (trainer.getPrSWiMap().containsKey(word)) {
                            double prSW = trainer.getPrSWiMap().get(word);
                            spamProb += (Math.log(1 - prSW) - Math.log(prSW));
                        }
                    }
                }

                double prob = 1.0 / (1 + Math.exp(spamProb));

                if (prob > 0.5){
                    if(actualClass == "spam"){
                        this.TruePositive++;
                    }
                    else{
                        this.FalsePositive++;
                    }
                }
                else{
                    if(actualClass == "ham"){
                        this.TrueNegative++;
                    }
                    else{
                        this.FalseNegative++;
                    }
                }


                TestFile testFile = new TestFile(file.getName(), prob, actualClass);
                testFiles.add(testFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return testFiles;
    }


    public Double getAccuracy() throws URISyntaxException, IOException {

        int numFiles = this.TrueNegative + this.TruePositive + this.FalseNegative + this.FalsePositive;

        return (double) (this.TruePositive + this.TrueNegative)/numFiles;
    }

    public Double getPrecision() throws URISyntaxException, IOException {

        return (double) this.TruePositive/(this.FalsePositive+ this.TruePositive);
    }

    private Boolean isWord(String word){
        if (word == null){
            return false;
        }

        String pattern = "^[a-zA-Z]*$";
        if(word.matches(pattern)){
            return true;
        }
        return false;
    }

}

