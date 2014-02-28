package ga_assignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 * Class to calculate the fitness of a given individual.
 *
 * @author David
 */
public class FitnessCalculator {

    private static Random rand = new Random();
    public static ArrayList<Data> dataSet = new ArrayList<Data>();

    public static ArrayList<Data> getDataSet() {
        return dataSet;
    }

    public static void setDataSet(ArrayList<Data> dataSet) {
        FitnessCalculator.dataSet = dataSet;
    }

    /**
     * Fitness function to check whether generated rules matched the passed in
     * data set variables and class values
     *
     * @param individual rule set to evaluate fitness for
     * @return fitness of rule as an int
     */
    public static int calcFitnessBytesRules(Individual individual) {
        int fitness = 0;

        if (!GeneticAlgorithmConstants.ISGENERALISING) {
            fitness = fitnessForDataSetNonGeneralise(individual);
        } else {
            fitness = fitnessForDataSetGeneralising(individual);
        }
        return fitness;
    }

    /**
     * Method to calculate fitness for Data set 1 (data1.txt). This function
     * does not involve generalisation and so implicitly matches rule conditions
     * to data variables and then compares the value to the action to determine
     * whether to increment the fitness
     *
     * @param individual to calculate fitness for
     * @return the fitness of the individual as an int
     */
    private static int fitnessForDataSetNonGeneralise(Individual individual) {
        int fitness = 0;
        for (Data data : dataSet) {
            for (int i = 0; i < individual.size(); i = i + GeneticAlgorithmConstants.LINE_LENGTH) {
                int[] condition = Arrays.copyOfRange(individual.genes, i, i + (GeneticAlgorithmConstants.VAR_LENGTH));
                int action = individual.getGene(i + GeneticAlgorithmConstants.VAR_LENGTH);

                if (Arrays.equals(data.variable, condition)) {
                    if (data.classValue == action) {
                        fitness++;
                        break;
                    } else {
                        break;
                    }
                }
            }
        }

        return fitness;
    }

    /**
     * Method to calculate fitness for Data set 2 (data2.txt). This function
     * involves generalisation and so tries to match rule conditions to data
     * variables with a smaller rule set over time and then compares the value
     * to the action to determine whether to increment the fitness.
     *
     * @param individual to calculate fitness for
     * @return the fitness of the individual as an int
     */
    public static int fitnessForDataSetGeneralising(Individual individual) {
        int fitness = 0;
        ArrayList<Rule> ruleSet = splitIntoRules(individual);
        for (Data data : dataSet) {
            for (Rule rule : ruleSet) {
                if (rule.matches(data.variable)) {
                    rule.timesMatched++;
                    if (rule.action == data.classValue) {
                        fitness++;
                        break;
                    } else {
                        break;
                    }
                }
            }
        }

        //remove unmatched rules
        for (Iterator<Rule> it = ruleSet.iterator(); it.hasNext();) {
            Rule rule = it.next();
            if (rule.timesMatched == 0) {
                it.remove();
            }
        }
        individual.matchedRules = ruleSet;
        individual.numRules = individual.matchedRules.size();

        return fitness;
    }

    /**
     * Method to calculate fitness of an individual for floating point
     * classification Aims to match a point of a variable if it falls within the
     * lower and upper bounds of a rule (consists of 2 genes from individual
     * floating point array) 1 dataPoint to 2 gene mapping
     *
     * @param individual to evaluate fitness for
     * @return fitness of the individual as an int
     */
    public static int fitnessForFloatingPointClassification(Individual individual) {
        int fitness = 0;
        ArrayList<Rule> rules = splitIntoRules(individual);
        for (Data data : dataSet) {
            for (Rule rule : rules) {
                if (rule.matches(data.floatVariable)) {
                    rule.timesMatched++;
                    if (rule.action == data.classValue) {
                        fitness++;
                        break;
                    } else {
                        break;
                    }
                }
            }
        }

        //remove unmatched rules
        for (Iterator<Rule> it = rules.iterator(); it.hasNext();) {
            Rule rule = it.next();
            if (rule.timesMatched == 0) {
                it.remove();
            }
        }
        individual.matchedRules = rules;
        individual.numRules = individual.matchedRules.size();

        return fitness;
    }

    /**
     * Method to split individual genes into rules. The number of rules created
     * is based on the rule length seed value in individual (numRules). There is
     * a different rule set created depending on whether the data being
     * classified is integer/binary based or floating point based
     *
     * @param individual to build rules from
     * @return a list of rules generated from the individual
     */
    private static ArrayList<Rule> splitIntoRules(Individual individual) {
        ArrayList<Rule> ruleSet = new ArrayList<Rule>();
        int counter = 0;
        int reduction = 0; //to ensure step size rate isn't initalised along with the bounds/class values

        if (GeneticAlgorithmConstants.COEVOLVE_STEP_SIZE) {
            reduction = 1;
        }

        for (int i = 0; i < individual.size() - reduction; i = i + GeneticAlgorithmConstants.LINE_LENGTH) {
            if (counter == individual.numRules) {
                break;
            }
            if (GeneticAlgorithmConstants.ISFLOAT) { //make rules for floating point genes
                double[] condition = Arrays.copyOfRange(individual.fgenes, i, i + (GeneticAlgorithmConstants.VAR_LENGTH));
                condition = configureBounds(condition);
                int action = (int) individual.getFGene(i + GeneticAlgorithmConstants.VAR_LENGTH);

                Rule rule = new Rule(condition, action);
                ruleSet.add(rule);
            } else { //make rules for int genes 
                int[] condition = Arrays.copyOfRange(individual.genes, i, i + (GeneticAlgorithmConstants.VAR_LENGTH));

                int action = individual.getGene(i + GeneticAlgorithmConstants.VAR_LENGTH);
                if (action == 2) {
                    action = rand.nextInt(2); //action cannot be a wildcard symbol
                }
                Rule rule = new Rule(condition, action);
                ruleSet.add(rule);
            }

            counter++;
        }

        return ruleSet;

    }

    private static double[] configureBounds(double[] condition) {
        double[] configuredCondition = condition;

        for (int i = 0; i < configuredCondition.length; i = i + 2) {
            if (configuredCondition[i] > configuredCondition[i + 1]) {
                double point = configuredCondition[i + 1];
                configuredCondition[i + 1] = configuredCondition[i];
                configuredCondition[i] = point;
            }
        }

        return configuredCondition;
    }
}
