package ga_assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Main class used to run GA
 *
 * @author David
 */
public class Main {

    //MAIN Method
    public static void main(String[] args) throws Exception {
        File file = new File(GeneticAlgorithmConstants.FILE_LOC
                + GeneticAlgorithmConstants.FILE_NAME); //read file
        if (file.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            ArrayList<Data> dataSet = new ArrayList<Data>();
            ArrayList<Data> trainingSet = new ArrayList<Data>();
            ArrayList<Data> testSet = new ArrayList<Data>();

            if (GeneticAlgorithmConstants.ISFLOAT) { //is floating point data
                while ((line = br.readLine()) != null) {
                    if (line.contains("class")) {
                        continue; //skip first data set description
                    }
                    //read in data (convert variable from space separated to float array)
                    String variable = line.substring(0, line.lastIndexOf(" ")).trim();
                    String[] variables = variable.split("\\s*(=>|,|\\s)\\s*");
                    double[] classVariable = new double[GeneticAlgorithmConstants.DATA_SIZE];
                    int i = 0;
                    //read variable data into floating point array
                    for (String s : variables) {
                        Double floating = new Double(s);
                        classVariable[i] = floating.floatValue();
                        i++;
                    }

                    String value = line.substring(line.lastIndexOf(" "), line.length()).trim();
                    if (!GeneticAlgorithmConstants.FILE_NAME.contains("data")) { //convert class label to 0/1 for Wisconsin Data Set
                        value = convertClassLabel(value);
                    }
                    Integer classValue = new Integer(value);

                    Data data = new Data(classVariable, classValue);
                    dataSet.add(data);
                }

                //if using benchmark data set scale to suit range of 0-1
                if (!GeneticAlgorithmConstants.FILE_NAME.contains("data")) {
                    dataSet = scaleDataSet(dataSet);
                }
                
                //optionally split data set
                if (GeneticAlgorithmConstants.SPLIT_DATASET) {
                    splitDataSet(dataSet, trainingSet, testSet);
                    FitnessCalculator.setDataSet(trainingSet);
                } else {
                    FitnessCalculator.setDataSet(dataSet);
                }

                //run ga on training set
                Population myPop = new Population(true);
                Algorithm algor = new Algorithm();
                int generationCount = 0;
                while (generationCount < GeneticAlgorithmConstants.MAX_GENERATIONS) {
                    generationCount++;
                    System.out.println("Generation: " + generationCount + " Fittest: "
                            + myPop.getFittest().getFitness());
                    System.out.println("Total Fitness: "
                            + myPop.getTotalPopulationFitness() + " Mean Fitness: "
                            + myPop.getMeanFitness());
                    System.out.println("Rule Size: " + myPop.getFittest().matchedRules.size());
                    System.out.println("Rule Set: " + myPop.getFittest().matchedRules);
                    System.out.println("Average Rule Size: " + myPop.getMeanRuleSize());
                    if (GeneticAlgorithmConstants.COEVOLVE_STEP_SIZE) {
                        System.out.println("Fittest Step Size: " + myPop.getFittest().getFGene(myPop.getFittest().size() - 1));
                    }

                    myPop = algor.evolvePopulation(myPop);
                }
                if (GeneticAlgorithmConstants.SPLIT_DATASET) {
                    FitnessCalculator.setDataSet(testSet);
                    myPop = algor.evolvePopulation(myPop);
                    System.out.println("Fittest: " + myPop.getFittest().fitness);
                    System.out.println("Number of Errors: "
                            + (FitnessCalculator.getDataSet().size() - myPop.getFittest().fitness));
                }
            } else { //binary data data1.txt and data2.tx
                while ((line = br.readLine()) != null) {
                    if (line.contains("class")) {
                        continue;
                    }
                    String variable = line.substring(0, GeneticAlgorithmConstants.VAR_LENGTH).trim();
                    String Value = line.substring(line.lastIndexOf(" "), line.length()).trim();

                    //convert variable to int array
                    int[] array = new int[GeneticAlgorithmConstants.VAR_LENGTH];
                    for (int i = 0; i < variable.length(); i++) {
                        Integer aByte = new Integer(String.valueOf(variable.charAt(i)));
                        array[i] = aByte.intValue();
                    }
                    Integer classValue = new Integer(Value);

                    //add data to dataSet
                    Data data = new Data(array, classValue.intValue());
                    dataSet.add(data);
                }

                //optionally split data set
                if (GeneticAlgorithmConstants.SPLIT_DATASET) {
                    splitDataSet(dataSet, trainingSet, testSet);
                    FitnessCalculator.setDataSet(trainingSet);
                } else {
                    FitnessCalculator.setDataSet(dataSet);
                }

                //run ga
                Population myPop = new Population(true);
                Algorithm algor = new Algorithm();
                int generationCount = 0;
                while (generationCount < GeneticAlgorithmConstants.MAX_GENERATIONS) {
                    generationCount++;
                    System.out.println("Generation: " + generationCount + " Fittest: "
                            + myPop.getFittest().getFitness());
                    System.out.println("Total Fitness: "
                            + myPop.getTotalPopulationFitness() + " Mean Fitness: "
                            + myPop.getMeanFitness());
                    if (GeneticAlgorithmConstants.ISGENERALISING) {
                        System.out.println("Rule Size: " + myPop.getFittest().matchedRules.size());
                        System.out.println("Rule Set: " + myPop.getFittest().matchedRules);
                        System.out.println("Average Rule Size: " + myPop.getMeanRuleSize());
                    }

                    myPop = algor.evolvePopulation(myPop);
                }
                //run algorithm on test Set
                if (GeneticAlgorithmConstants.SPLIT_DATASET) {
                    FitnessCalculator.setDataSet(testSet);
                    myPop = algor.evolvePopulation(myPop);
                    System.out.println("Fittest: " + myPop.getFittest().fitness);
                    System.out.println("Number of Errors: "
                            + (FitnessCalculator.getDataSet().size() - myPop.getFittest().fitness));
                }
            }
        } else {
            throw new Exception("Unable to read data file");
        }
    }

    /**
     * Method to split a dataset that will eventually be used to evolve a GA
     *
     * @param dataSet to split
     * @param trainingSet used to train the GA
     * @param testSet used to run once the GA has been trained
     */
    private static void splitDataSet(ArrayList<Data> dataSet, ArrayList<Data> trainingSet,
            ArrayList<Data> testSet) {
        int counter = 0;
        int splitThreshold = dataSet.size() / 2;

        for (int i = 0; i < dataSet.size(); i++) {
            if (counter >= splitThreshold) {
                testSet.add(dataSet.get(i));
            } else {
                trainingSet.add(dataSet.get(i));
            }
            counter++;
        }
    }

    /**
     * Method for converting the Wisconsin Data Set class labels of Malignant
     * and Benign to integer labels 0 or 1 (0 for Benign data and 1 for
     * Malignant class labels
     *
     * @param label from data to convert to a class label appropriate for the
     * Genetic Algorithm
     * @return a new label either a 0 for Benign data or a 1 for Malignant data
     */
    private static String convertClassLabel(String label) {
        String convertedLabel;

        if (label.equals("M")) {
            convertedLabel = "1"; //for Malignant Classification
        } else {
            convertedLabel = "0"; //for Benign Classification
        }

        return convertedLabel;
    }

    /**
     * Method to scale the variable data so that each datapoint falls in the 
     * range of 0-1. This makes use of feature scaling.
     * @param dataSet to scale to appropriate range
     * @return dataSet scaled to the range 0-1
     */
    private static ArrayList<Data> scaleDataSet(ArrayList<Data> dataSet) {
        ArrayList<Data> scaledData = dataSet;

        for (int i = 0; i < GeneticAlgorithmConstants.DATA_SIZE; i++) {
            double min = 0.0, max = 0.0;
            boolean first = true;
            for (Data d : scaledData) { //loop to find min and max values of column
                if (first) { //initialise min and max with first input data
                    min = d.floatVariable[i];
                    max = d.floatVariable[i];
                    first = false;
                } else if (d.floatVariable[i] < min) {
                    min = d.floatVariable[i];
                } else if (d.floatVariable[i] > max) {
                    max = d.floatVariable[i];
                } else {
                    continue;
                }
            }
            for (Data d : scaledData) { //scale data so it fits into range 0-1
                double dataPoint = d.floatVariable[i];
                dataPoint = runFeatureScaling(dataPoint, min, max);
                d.floatVariable[i] = dataPoint; // changed to scaled value for that column
            }
        }

        return scaledData;
    }

    /**
     * Method to scale a feature so that it fits into the range 0-1 so that it
     * can be processed by the Genetic Algorithm. This method makes use of the
     * rescaling equation as outlined here:
     * http://en.wikipedia.org/wiki/Feature_scaling#Rescaling
     *
     * @param x the feature to scale
     * @param min or the smallest value of the data in that particular column
     * @param max or the largest value of the data in that particular column
     * @return a normalised or scaled feature that falls in the range 0-1
     */
    private static double runFeatureScaling(double x, double min, double max) {
        double scaledValue;

        scaledValue = (x - min) / (max - min);

        return scaledValue;
    }
}
