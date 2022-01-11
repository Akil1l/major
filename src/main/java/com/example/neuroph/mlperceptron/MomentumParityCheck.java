package com.example.neuroph.mlperceptron;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;

public class MomentumParityCheck  implements LearningEventListener {

	public static void main(String[] args) throws FileNotFoundException {
		new MomentumParityCheck().run();
	}
	
	public static double[] int2double(int i){
		double[] re=new double[32];
		for(int j=0;j<32;j++){
			re[j]=(double)((i>>j)&1);
		}
		return re;
	}
	
	public static String networkOutputDisplay(double[] networkOutput){
		if(((int)networkOutput[3])==1)return "正偶数";
		if(((int)networkOutput[2])==1)return "负偶数";
		if(((int)networkOutput[1])==1)return "正奇数";
		if(((int)networkOutput[0])==1)return "负奇数";
		return "未知";
	}
	
	public static String correctClassify(int i){
		if(i>0 && i%2==0){
			return "正偶数";
		}else if(i<0 && i%2==0){
			return "负偶数";
		}else if(i>0 && i%2!=0){
			return "正奇数";
		}else if(i<0 && i%2!=0){
			return "负奇数";
		}
		return "0";
	}
	
	/**
	 * 0001  正偶数
	 * 0010  负偶数
	 * 0100  正奇数
	 * 1000  负奇数
	 * @param i
	 * @return
	 */
	public static double[] int2prop(int i){
		double[] pe={0d,0d,0d,1d};
		double[] ne={0d,0d,1d,0d};
		double[] po={0d,1d,0d,0d};
		double[] no={1d,0d,0d,0d};
		if(i>0 && i%2==0){
			return pe;
		}else if(i<0 && i%2==0){
			return ne;
		}else if(i>0 && i%2!=0){
			return po;
		}else if(i<0 && i%2!=0){
			return no;
		}
		return pe;
	}

    public void run() throws FileNotFoundException {
		DataSet trainingSet = new DataSet(32, 4);
        for(int i=0;i<2000;i++){
        	int in=new Random().nextInt();
        	trainingSet.add(new DataSetRow(int2double(in), int2prop(in)));
        }

        // 创建神经网络 32个输入 10个神经元隐层 4个
		NeuralNetwork myMlPerceptron = NeuralNetwork.load(new FileInputStream("myMlPerceptron.neet"));
//        MlPerceptron myMlPerceptron = new MlPerceptronBinOutput(TransferFunctionType.SIGMOID, 32, 10, 4);
        myMlPerceptron.setLearningRule(new MomentumBackpropagation());
//
        LearningRule learningRule = myMlPerceptron.getLearningRule();
        learningRule.addListener(this);
        // learn the training set
        System.out.println("Training neural network...");
        myMlPerceptron.learn(trainingSet);
		myMlPerceptron.save("myMlPerceptron.neet");
        // test perceptron
        System.out.println("Testing trained neural network");
        testNeuralNetwork(myMlPerceptron);

    }
    public static void testNeuralNetwork(NeuralNetwork neuralNet) {
    	int badcount=0;
    	int COUNT=50000;
    	for(int i=0;i<COUNT;i++){
    		int in=new Random().nextInt();
    		double[] inputnumber=int2double(in);
    		neuralNet.setInput(inputnumber);
    		neuralNet.calculate();
    	    double[] networkOutput = neuralNet.getOutput();
    	    
    	    System.out.print("Input: " + in);
    	    String networkOutputDisplay=networkOutputDisplay(networkOutput);
    	    System.out.println(" Output: " + Arrays.toString( networkOutput)+ networkOutputDisplay );
    	    String cc=correctClassify(in);
    	    if(!cc.equals(networkOutputDisplay)){
    	    	badcount++;
    	    	System.out.print("判别错误:"+in);
    	    	System.out.print(" correctClassify="+cc);
    	    	System.out.println(" networkOutputDisplay="+networkOutputDisplay);
    	    }
    	}
    	System.out.println("正确率："+(COUNT-badcount*1.0)/COUNT*100.0+"%");
    }
    
    @Override
    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation)event.getSource();
        System.out.println(bp.getCurrentIteration() + ". iteration : "+ bp.getTotalNetworkError());
    }   
}

