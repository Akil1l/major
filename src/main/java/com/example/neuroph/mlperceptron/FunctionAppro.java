package com.example.neuroph.mlperceptron;

import com.example.neuroph.util.FileUtils;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.learning.BackPropagation;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class FunctionAppro  implements LearningEventListener {

	public static int i=2;
	public static double maxError=0.0001d;
	
	public static void main(String[] args) throws IOException {
		new FunctionAppro().run();
	}
	
	public double func(double x){
		return 1+Math.sin(Math.PI*i/4*x);
	}
	
    public void run() throws IOException {
        DataSet trainingSet = new DataSet(1, 1);
        for(int i=0;i<2000;i++){
        	double in=new Random().nextDouble()*4-2;
        	double out=func(in);
        	trainingSet.add(new DataSetRow(new double[]{in}, new double[]{out}));
        }
        MlPerceptron myMlPerceptron = new MlPerceptronLineOutput(1,4,1);
        myMlPerceptron.setLearningRule(new BackPropagation());
        myMlPerceptron.getLearningRule().setMaxError(maxError);

        LearningRule learningRule = myMlPerceptron.getLearningRule();
        learningRule.addListener(this);
        System.out.println("Training neural network...");
        myMlPerceptron.learn(trainingSet);

        // test perceptron
        System.out.println("Testing trained neural network");
        testNeuralNetwork(myMlPerceptron);
    }
    
    public static void testNeuralNetwork(NeuralNetwork neuralNet) throws IOException {
    	StringBuffer x=new StringBuffer();
    	StringBuffer y=new StringBuffer();
    	for(int i=0;i<100;i++){
    		double in=new Random().nextDouble()*4-2;
    		neuralNet.setInput(in);
    		neuralNet.calculate();
    	    double[] networkOutput = neuralNet.getOutput();
    	    
    	    x.append(in);
    	    x.append("\t");
    	    y.append(networkOutput[0]);
    	    y.append("\t");
    	}
    	//输出到文件 共scilab绘制图像
		FileUtils.writeStringToFile(new File("C:/x.txt"), x.toString());
		FileUtils.writeStringToFile(new File("C:/y.txt"), y.toString());
    }
    
    @Override
    public void handleLearningEvent(LearningEvent event) {
    	SupervisedLearning bp = (SupervisedLearning)event.getSource();
        System.out.println(bp.getCurrentIteration() + ". iteration : "+ bp.getTotalNetworkError());
    }   
}

