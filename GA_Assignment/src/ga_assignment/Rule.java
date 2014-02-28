package ga_assignment;

import java.util.Arrays;

/**
 * Class to hold information when an individuals genes are converted into rules
 *
 * @author David
 */
public class Rule {

    int[] condition;
    double[] floatCondition;
    int action;
    int timesMatched;

    public Rule(int[] condition, int action) {
        this.condition = condition;
        this.action = action;
    }

    public Rule(double[] floatCondition, int action) {
        this.floatCondition = floatCondition;
        this.action = action;
    }

    public int[] getCondition() {
        return condition;
    }

    public void setCondition(int[] condition) {
        this.condition = condition;
    }

    public double[] getFloatCondition() {
        return floatCondition;
    }

    public void setFloatCondition(double[] floatCondition) {
        this.floatCondition = floatCondition;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getTimesMatched() {
        return timesMatched;
    }

    public void setTimesMatched(int timesMatched) {
        this.timesMatched = timesMatched;
    }

    /**
     * Function to check whether data matches the rule (int data). Checks
     * whether the data value matches the rule value (or if the rule is using
     * generalisation will auto match the data value)
     *
     * @param variable to match against rule
     * @return if variable matches rule, otherwise false
     */
    public boolean matches(int[] variable) {
        for (int i = 0; i < variable.length; i++) {
            if (variable[i] == condition[i] || condition[i] == 2) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Function to check whether data matches the rule. Checks the data value
     * against two values in the rule (upper and lower bound). If the data is
     * between the lower and upper bound, then that point is matchedF
     *
     * @param variable to match against rule
     * @return if variable matches rule, otherwise false
     */
    public boolean matches(double[] variable) {
        for (int i = 0; i < floatCondition.length; i = i + 2) {
            int var = i / 2;

            if (variable[var] > floatCondition[i] && variable[var] < floatCondition[i + 1]) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        if (GeneticAlgorithmConstants.ISFLOAT) {
            return "{Condition = " + Arrays.toString(floatCondition) + "} {Action = " + action + "}";
        } else {
            return "{Condition = " + Arrays.toString(condition) + "} {Action = " + action + "}";
        }
    }
}
