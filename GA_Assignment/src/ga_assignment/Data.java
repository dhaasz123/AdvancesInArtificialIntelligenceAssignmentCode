package ga_assignment;

import java.util.Arrays;
/**
 * Class to hold data read in from data sets
 * @author David
 */
public class Data {
    int[] variable;
    double[] floatVariable;
    int classValue;
    
    public Data(int[] variable, int classValue){
        this.variable = variable;
        this.classValue = classValue;
    }
    
    public Data(double[] floatVariable, int classValue){
        this.floatVariable = floatVariable;
        this.classValue = classValue;
    }

    public int[] getVariable() {
        return variable;
    }

    public void setVariable(int[] variable) {
        this.variable = variable;
    }

    public double[] getFloatVariable() {
        return floatVariable;
    }

    public void setFloatVariable(double[] floatVariable) {
        this.floatVariable = floatVariable;
    }

    public int getClassValue() {
        return classValue;
    }

    public void setClassValue(int classValue) {
        this.classValue = classValue;
    }
    
    @Override
    public String toString(){
        if(GeneticAlgorithmConstants.ISFLOAT){
            return "{Variable = " + Arrays.toString(floatVariable) + "} {Class = " + classValue + "}";
        } else {
            return "{Variable = " + Arrays.toString(variable) + "} {Class = " + classValue + "}";
        }
    }
}
