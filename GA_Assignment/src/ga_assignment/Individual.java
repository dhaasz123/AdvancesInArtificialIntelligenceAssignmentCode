package ga_assignment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Class to hold information relating to an individual
 *
 * @author David
 */
public class Individual {

    Random rand = new Random();
    DecimalFormat format = new DecimalFormat("#.###");
    public int[] genes = new int[GeneticAlgorithmConstants.NUMBER_OF_GENES]; //int problems
    public double[] fgenes = new double[GeneticAlgorithmConstants.NUMBER_OF_GENES]; //float problems
    public int fitness = 0;
    public int numRules;
    public ArrayList<Rule> matchedRules; //the rules formed by the individual which match at least one piece of data

    /**
     *
     * Method to retrieve the size of the genes array depending on the problem
     *
     * @return the number of genes as an int
     */
    public int size() {
        if (GeneticAlgorithmConstants.ISFLOAT) {
            return fgenes.length;
        } else {
            return genes.length;
        }
    }

    /**
     * Method to return a gene value from a given position from n (int array)
     *
     * @param index of the gene to be returned
     * @return the gene at the given position
     */
    public int getGene(int index) {
        return genes[index];
    }

    /**
     * Method to return a gene value from a given position from n (float array)
     *
     * @param index of the gene to be returned
     * @return the gene at the given position
     */
    public double getFGene(int index) {
        return fgenes[index];
    }

    /**
     * Method to set a gene at a given position within an individual (int array)
     *
     * @param value to be placed within the individual
     * @param index to place the gene
     */
    public void setGene(int value, int index) {
        genes[index] = value;
        fitness = 0;
    }

    /**
     * Method to set a gene at a given position within an individual (float
     * array)
     *
     * @param value to be placed within the individual
     * @param index to place the gene
     */
    public void setGene(double value, int index) {
        fgenes[index] = value;
        fitness = 0;
    }

    /**
     * Method to generate a new set of genes for an individual
     */
    public void genetrateIndividual() {
        boolean alternate = false;
        int reduction = 0; //to ensure step size rate isn't initalised along with the bounds/class values
        if(GeneticAlgorithmConstants.COEVOLVE_STEP_SIZE){
            reduction = 1;
            fgenes[fgenes.length - 1] = rand.nextFloat();
        }
        
        for (int i = 0; i < size() - reduction; i++) {
            if (GeneticAlgorithmConstants.ISFLOAT) { //generate individual for floating point problem               
                double gene;
                if (i > 0 && (i % GeneticAlgorithmConstants.VAR_LENGTH == 0)) { //generate action value
                    if (alternate) {
                        gene = 1.0f;
                    } else {
                        gene = 0.0f;
                    }
                    alternate = !alternate;
                } else {
                    gene = rand.nextDouble();
                }
                fgenes[i] = gene;

                //check it to ensure lower and upper bound conditions are broken
                if ((i % GeneticAlgorithmConstants.VAR_LENGTH != 0 && i - 1 % GeneticAlgorithmConstants.VAR_LENGTH != 0)
                        && !checkEven(i) && (i > 0 && i - 1 >= 0)) { //check bound conditions for 
                    if (fgenes[i] < fgenes[i - 1]) {//if upper bound is less than lower bound, switch bounds
                        gene = fgenes[i];
                        fgenes[i] = fgenes[i - 1];
                        fgenes[i - 1] = gene;
                    }
                }
            } else { //generate individual for binary/ binary generalisation problems
                int gene;
                if (!GeneticAlgorithmConstants.ISGENERALISING) {
                    gene = rand.nextInt(2); //0,1
                } else {
                    gene = rand.nextInt(3); //0,1,2
                }
                genes[i] = gene;
            }
        }
        
        //set up the number of rules, if generalising use random generater
        numRules = rand.nextInt(GeneticAlgorithmConstants.MAX_RULE_SET_SIZE)
                + GeneticAlgorithmConstants.MIN_RULE_SET_SIZE; //ensure minium of 2 rules
    }

    /**
     * Method to return the fitness of an individual. If it is 0, then the
     * fitness is calculated and return
     *
     * @return the fitness of the individual as an int
     */
    public int getFitness() {
        if (fitness == 0) {
            if (GeneticAlgorithmConstants.ISFLOAT) {
                fitness = FitnessCalculator.fitnessForFloatingPointClassification(this);
            } else {
                fitness = FitnessCalculator.calcFitnessBytesRules(this);
            }
        }
        return fitness;
    }

    /**
     * Method to return the individual as a string
     *
     * @return an individual gene array as a string
     */
    @Override
    public String toString() {
        String geneString = "";
        if (GeneticAlgorithmConstants.ISFLOAT) {
            geneString = Arrays.toString(fgenes);
        } else {
            for (int i = 0; i < size(); i++) {
                geneString = Arrays.toString(genes);
            }
        }
        return geneString;
    }

    /**
     * Method to check whether a gene position is an upper or lower bound
     * position (an even number denotes a lower bound and an odd number denotes
     * an upper bound)
     *
     * @param i position in gene array to check whether it is an odd or even
     * position
     * @return true if even, false if odd
     */
    private boolean checkEven(int i) {
        if ((i % 2) == 0) {
            return true;
        } else {
            return false;
        }
    }
}
