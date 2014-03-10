package ga_assignment;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Class to hold GA processes to evolve a population of individuals
 *
 * @author David
 */
public class Algorithm {

    DecimalFormat format = new DecimalFormat("#.###");
    private Random rand = new Random();

    /**
     * Method to carry out the genetic algorithm process of evolving a
     * population of individuals. It carries out the following steps: 1) Create
     * new blank population (optional elitism to add fittest individual from
     * passed in population to the new population) 2) Carry out selection
     * (tournament) to select individuals to be a part of the crossover process
     * 3) Carry out crossover (uniform) to create a new population of
     * individuals 4) Carry out mutation on new populations and return the new
     * population
     *
     * @param pop to evolve
     * @return a new population with hopefully a greater fitness than the
     * population passed in, or just return the passed in population if the new
     * population is less fit
     */
    public Population evolvePopulation(Population pop) {
        //1. temp population for offspring storage
        Population newPopulation = new Population(false);
        int elitismOffset;

        //get fittest individual from population
        if (GeneticAlgorithmConstants.ELITISM) {
            newPopulation.saveIndividual(0, pop.getFittest());
        }
        if (GeneticAlgorithmConstants.ELITISM) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }

        //2.generate new population using tournament selection
        for (int i = elitismOffset; i < pop.size(); i++) {
            Individual indiv = tournamentSelection(pop);
            newPopulation.saveIndividual(i, indiv);

        }

        //3.carry out crossover process
        Individual[] offspring = new Individual[GeneticAlgorithmConstants.MAX_POPULATION];
        for (int i = 0; i < newPopulation.size(); i = i + 2) {
            if (Math.random() <= GeneticAlgorithmConstants.CROSSOVER_RATE) {
                offspring[i] = uniformCrossover(newPopulation.getIndividual(i),
                        newPopulation.getIndividual(i + 1));
                offspring[i + 1] = uniformCrossover(newPopulation.getIndividual(i + 1),
                        newPopulation.getIndividual(i));
            } else {
                //put individuals in offspring if they don't crossover
                offspring[i] = newPopulation.getIndividual(i);
                offspring[i + 1] = newPopulation.getIndividual(i + 1);
            }

        }
        //set offspring to temp population
        newPopulation.setIndividuals(offspring);

        //4.carry out mutation process
        for (int i = 0; i < newPopulation.size(); i++) {
            mutation(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }

    /**
     * Function to encapsulate the algorithm needed for tournament selection
     *
     * @param pop to select individuals from
     * @return the individual selected (the winner of the tournament)
     */
    private Individual tournamentSelection(Population pop) {
        //Create a tournament population
        Individual[] tournament = new Individual[GeneticAlgorithmConstants.TOURNAMENT_SIZE];

        //For each place in the tournament get an individual
        for (int i = 0; i < tournament.length - 1; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament[i] = pop.getIndividual(randomId);
        }
        //return the fittest individual
        Individual fittest = tournament[0];
        for (int i = 0; i < tournament.length - 1; i++) {
            if (GeneticAlgorithmConstants.ISGENERALISING) {
                if (fittest.getFitness() < tournament[i].getFitness()) { //compare fitness
                    fittest = tournament[i];
                } else if (fittest.getFitness() == tournament[i].getFitness()) {
                    if (fittest.numRules > tournament[i].numRules) { //check which has smaller ruleset
                        fittest = tournament[i];
                    }
                }
            } else { //just compare fitness
                if (fittest.getFitness() <= tournament[i].getFitness()) {
                    fittest = tournament[i];
                }
            }
        }
        return fittest;
    }

    /**
     * Function to encapsulate the algorithm needed for individual mutation
     *
     * @param indiv to mutate
     */
    private void mutation(Individual indiv) {
        int reduction = 0; //to ensure step size rate isn't initalised along with the bounds/class values
        double localMutationRate = GeneticAlgorithmConstants.MUTATION_RATE;
        if (GeneticAlgorithmConstants.COEVOLVE_STEP_SIZE) {
            reduction = 1;
            //evolve step size before evolving other genes
            double newStepSize = evolveMutationRate(indiv.getFGene(indiv.size() - 1));
            indiv.setGene(newStepSize, indiv.size() - 1);
            localMutationRate = indiv.getFGene(indiv.size() - 1);
        }
        //Loop through genes
        if (GeneticAlgorithmConstants.ISGENERALISING) {
            if (Math.random() <= localMutationRate) {
                indiv.numRules = rand.nextInt(GeneticAlgorithmConstants.MAX_RULE_SET_SIZE)
                        + GeneticAlgorithmConstants.MIN_RULE_SET_SIZE;
            }
        }
        for (int i = 0; i < indiv.size() - reduction; i++) {
            if (GeneticAlgorithmConstants.ISFLOAT) { //mutate floating point genes
                if (Math.random() <= localMutationRate) {
                    double gene;
                    if (i > 0 && (i % GeneticAlgorithmConstants.VAR_LENGTH == 0)) { //random action value
                        gene = (float) rand.nextInt(2); //0,1
                    } else {
                        gene = getGaussian(indiv.getFGene(i), indiv); //apply non-uniform mutation (GA Lecture 2)
                    }

                    indiv.setGene(gene, i);
                }
            } else { //mutate integer genes
                if (Math.random() <= localMutationRate) {
                    int gene;
                    if (!GeneticAlgorithmConstants.ISGENERALISING) {
                        gene = rand.nextInt(2); //0,1
                    } else {
                        gene = rand.nextInt(3); //0,1,2
                    }
                    indiv.setGene(gene, i);
                }
            }
        }

        if (GeneticAlgorithmConstants.ISFLOAT) {
            //loop through newly created gene and ensure lower bounds are not greater than upper bounds
            //following crossover (odd number denotes 
            for (int i = 0; i < indiv.size() - reduction; i++) {
                if ((i % GeneticAlgorithmConstants.VAR_LENGTH != 0 && i - 1 % GeneticAlgorithmConstants.VAR_LENGTH != 0)
                        && !checkEven(i) && (i > 0 && i - 1 >= 0)) {
                    if (indiv.getFGene(i) < indiv.getFGene(i - 1)) {
                        double gene = indiv.getFGene(i);
                        indiv.setGene(indiv.getFGene(i - 1), i);
                        indiv.setGene(gene, i - 1);
                    }
                }
            }
        }
    }

    /**
     * Function to encapsulate the algorithm needed for uniform crossover
     *
     * @param indiv1 an individual representing the first parent
     * @param indiv2 an individual representing the second parent
     * @return an individual with the combined genes of both passed in parent
     * individuals
     */
    private Individual uniformCrossover(Individual indiv1, Individual indiv2) {
        Individual newIndiv = new Individual();
        int reduction = 0;//to ensure step size rate isn't initalised along with the bounds/class values

        if (GeneticAlgorithmConstants.COEVOLVE_STEP_SIZE) {
            reduction = 1;
            if (Math.random() <= 0.5) {
                newIndiv.setGene(indiv1.getFGene(indiv1.size() - 1), newIndiv.size() - 1); //get step size from parent one
            } else {
                newIndiv.setGene(indiv2.getFGene(indiv2.size() - 1), newIndiv.size() - 1); //get step size from parent one
            }
        }

        if (GeneticAlgorithmConstants.ISGENERALISING) {
            newIndiv.numRules = (indiv1.numRules < indiv2.numRules) ? indiv1.numRules : indiv2.numRules;
        } else {
            newIndiv.numRules = GeneticAlgorithmConstants.MAX_RULE_SET_SIZE;
        }
        //Loop through genes
        for (int i = 0; i < indiv1.size() - reduction; i++) {
            if (GeneticAlgorithmConstants.ISFLOAT) { //crossover floating point genes (discrete crossover)
                if (Math.random() <= GeneticAlgorithmConstants.CROSSOVER_RATE) {
                    newIndiv.setGene(indiv1.getFGene(i), i);
                } else {
                    newIndiv.setGene(indiv2.getFGene(i), i);
                }
            } else { //crossover integer genes
                if (Math.random() <= GeneticAlgorithmConstants.CROSSOVER_RATE) {
                    newIndiv.setGene(indiv1.getGene(i), i);
                } else {
                    newIndiv.setGene(indiv2.getGene(i), i);
                }
            }
        }

        if (GeneticAlgorithmConstants.ISFLOAT) {
            //loop through newly created gene and ensure lower bounds are not greater than upper bounds
            //following crossover (odd number denotes 
            for (int i = 0; i < newIndiv.size() - reduction; i++) {
                if ((i % GeneticAlgorithmConstants.VAR_LENGTH != 0 && i - 1 % GeneticAlgorithmConstants.VAR_LENGTH != 0)
                        && !checkEven(i) && (i > 0 && i - 1 >= 0)) {
                    if (newIndiv.getFGene(i) < newIndiv.getFGene(i - 1)) {
                        double gene = newIndiv.getFGene(i);
                        newIndiv.setGene(newIndiv.getFGene(i - 1), i);
                        newIndiv.setGene(gene, i - 1);
                    }
                }
            }
        }

        return newIndiv;
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

    /**
     * Method to carry out non-uniform mutation on float point genes.Is a
     * probabilistic scheme and only makes a small change to the value. Involves
     * adding a random deviate to a gene taken from the N(0,$) Gaussian
     * distribution and curtailed to range. The standard deviation constant $
     * controls the amount of change. 2/3 variations will lie in range (- $ to +
     * $)
     *
     * @param oldGene to apply deviate to
     * @return the gene with the deviate from the Gaussian distribution applied
     */
    private double getGaussian(double oldGene, Individual indiv) {
        double newGene, variant;

        if (GeneticAlgorithmConstants.COEVOLVE_STEP_SIZE) {
            //use evolving step size
            variant = rand.nextGaussian() * indiv.getFGene(indiv.size() - 1);
        } else {
            //use fixed step size
            variant = rand.nextGaussian() * GeneticAlgorithmConstants.STEP_SIZE;
        }

        if (Math.random() <= 0.5) {
            newGene = oldGene - variant;
        } else {
            newGene = oldGene + variant;
        }

        if (newGene > 1) {
            newGene = 1.0; //curtail range if larger than maximun
        } else if (newGene < 0) {
            newGene = 0.0; //curtail range if less than minimum
        }

        return newGene;
    }

    /**
     * Method to evolve a step size based on the method in evolution strategies
     * lecture (slide 11). Makes use of mutation and a learning rate calculated
     * based on population size
     *
     * @param oldStepSize is the step size to evolve
     * @return the evolved step size as a float
     */
    private double evolveMutationRate(double oldRate) {
        double newStepSize;
        //calculate learning rate
        double learningRate = 1.0 / Math.sqrt((double) GeneticAlgorithmConstants.MAX_POPULATION);

        newStepSize = oldRate * (Math.exp(learningRate * rand.nextGaussian()));

        //check boundary condition
        if (newStepSize <= 0) {
            newStepSize = 0.01;
        } else if (newStepSize > 1) {
            newStepSize = 1.0;
        }

        return newStepSize;
    }
}
