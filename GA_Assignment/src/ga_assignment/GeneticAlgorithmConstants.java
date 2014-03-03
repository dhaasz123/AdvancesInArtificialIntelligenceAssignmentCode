package ga_assignment;

/**
 * Class to hold constants to be used in the application
 * @author David
 */
public class GeneticAlgorithmConstants {
    
     //Population and gene setup
    public static final int NUMBER_OF_GENES = 261; //add a 1 if co-evolving step size mutation
    public static final int MAX_POPULATION = 100;
    public static final int MAX_GENERATIONS = 1000;
    
    
    //properties of the algorithm
    public static final double CROSSOVER_RATE = 0.9;
    public static final double MUTATION_RATE = 0.015;
    public static final boolean ELITISM =true;
    public static final int TOURNAMENT_SIZE = 5;
    
    //properties regarding the classification of data
    public static final int VAR_LENGTH = 12; //for seperating rule from action value
    public static final int LINE_LENGTH = 13; //for rule creation loop
    public static final int MAX_RULE_SET_SIZE = 19; 
    public static final int MIN_RULE_SET_SIZE = 2;
    
    public static final String FILE_LOC = "C:\\Users\\David\\Documents\\NetBeansProjects\\GA_Assignment\\src\\data";
    public static final String FILE_NAME = "\\data3.txt";
    public static final boolean ISFLOAT = true;
    public static final int DATA_SIZE = 6; //the amount of float point data values
    
    public static final boolean ISGENERALISING = false; //to take account number of rules when selecting individuals, used for data2.txt only
    public static final boolean SPLIT_DATASET = true; //for training and then test
    
    //standard deviation for use with guassian function
    public static final float STANDARD_DEVIATION = 0.7f;
    public static final boolean COEVOLVE_STEP_SIZE = true; //determines whether to use a fixed or evolving step size
}
