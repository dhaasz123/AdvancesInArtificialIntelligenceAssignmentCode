package ga_assignment;
import java.util.Random;
/**
 * Population class to hold and manage individuals
 *
 * @author David
 */
public class Population {

    Individual[] individuals;
    int reduction; //to ensure step size rate isn't initalised along with the bounds/class values
    /**
     * Constructor for population
     *
     * @param initalise if true fill individuals array with random individuals,
     * otherwise create an empty population
     */
    public Population(boolean initalise) {
        individuals = new Individual[GeneticAlgorithmConstants.MAX_POPULATION];
        if (initalise) {
            for (int i = 0; i < size(); i++) {
                Individual individual = new Individual();
                individual.genetrateIndividual();
                saveIndividual(i, individual);
            }
        }
    }

    /**
     * Method to get size of population
     *
     * @return size of population as an integer
     */
    public int size() {
        return individuals.length;
    }

    /**
     * Method to save a given individual into the population
     *
     * @param index to place individual
     * @param individual to add to population
     */
    public void saveIndividual(int index, Individual individual) {
        individuals[index] = individual;
    }

    /**
     * Method to retrieve an individual from the population
     *
     * @param index of the population
     * @return an individual at the given index
     */
    public Individual getIndividual(int index) {
        return individuals[index];
    }

    /**
     * Method to set up a new population of individuals
     *
     * @param individuals to make up population as an array
     */
    public void setIndividuals(Individual[] individuals) {
        this.individuals = individuals;
    }

    /**
     * Method to get the Individual with the highest fitness value
     *
     * @return the Individual with the higher fitness
     */
    public Individual getFittest() {
        Individual fittest = individuals[0];
        //Loop through individuals to find fittest
        for (int i = 0; i < size(); i++) {
            if (fittest.getFitness() <= getIndividual(i).getFitness()) {
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }

    /**
     * Method to calculate the total population fitness. This is the sum of all
     * the individuals' fitness in the populations
     *
     * @return the fitness of the population as an int
     */
    public int getTotalPopulationFitness() {
        int totalFitness = 0;
        //Loop through individuals summing their fitness values
        for (int i = 0; i < size(); i++) {
            totalFitness = totalFitness + getIndividual(i).getFitness();
        }
        return totalFitness;
    }

    /**
     * Method to return the mean fitness of a population. This calculated by the
     * sum of all individuals fitness divided by the number of individuals in a
     * population
     *
     * @return the mean fitness of the population as a double
     */
    public double getMeanFitness() {
        int totalFitness = 0;
        //Loop through individuals summing their fitness values
        for (int i = 0; i < size(); i++) {
            totalFitness += getIndividual(i).getFitness();
        }
        return totalFitness / size();
    }

    /**
     * Method to calculate the average rule size of a population. This
     * calculated by the sum of all the number of rules in an individuals rules
     * set divided by the number of individuals in a population
     *
     * @return the average number of rules of the population as an int
     */
    public int getMeanRuleSize() {
        int totalRuleSize = 0;

        //Loop through individuals summing their rule set size
        for (int i = 0; i < size(); i++) {
            totalRuleSize += getIndividual(i).matchedRules.size();
        }

        return (int) Math.round(totalRuleSize / size());
    }
    
}
