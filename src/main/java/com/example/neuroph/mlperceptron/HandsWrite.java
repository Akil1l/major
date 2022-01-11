package com.example.neuroph.mlperceptron;

import com.example.neuroph.bmp.BmpReader;
import com.example.neuroph.util.Utils;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

import java.io.IOException;
import java.util.Arrays;

public class HandsWrite  implements LearningEventListener {


	public static void main(String[] args) throws IOException {
		new HandsWrite().run();
	}

	public void run() throws IOException {
    	//256个输入表示图像 10个输出表示数字0-9
		DataSet trainingSet = new DataSet(256, 10);
        //目前准备了3组训练数据
        int trainNum=3;
        for(int i=0;i<trainNum;i++){
	        for(int j=0;j<10;j++){
		        String f=String.format("handswriter\\train%d\\%d.bmp", i,j);
		        //将输入图片文件转为神经网络输出
		        double[] bNum= BmpReader.convertBmp2Inputs(f);
		        //将输入图片转为正确的结果输出
		        double[] bRe=BmpReader.convertBmp2Outputs(f);
		        trainingSet.add(new DataSetRow(bNum, bRe));
	        }
        }
        
        MlPerceptron myMlPerceptron = new MlPerceptron(TransferFunctionType.SIGMOID, 256,10);
        //设置可接收的误差
        myMlPerceptron.getLearningRule().setMaxError(0.00001d);

        LearningRule learningRule = myMlPerceptron.getLearningRule();
        learningRule.addListener(this);
        
        System.out.println("Training neural network...");
        myMlPerceptron.learn(trainingSet);

        // test perceptron
        System.out.println("Testing trained neural network");
        testNeuralNetwork(myMlPerceptron, trainingSet);
    }
	
    public static void testNeuralNetwork(NeuralNetwork neuralNet, DataSet testSet) throws IOException {
        for(int i=0;i<10;i++){
	        String f=String.format("handswriter\\test\\%d.bmp", i);
	        double[] bNum=BmpReader.convertBmp2Inputs(f);
	        neuralNet.setInput(bNum);
	        neuralNet.calculate();
	        double[] networkOutput = neuralNet.getOutput();
	        //将活跃度最高的输出视为1，其余视为0
	        networkOutput= Utils.competition(networkOutput);
	        System.out.print("Input: " + i);
	        System.out.println(" Output: " + Arrays.toString( networkOutput) );
        }
    }
    
    @Override
    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation)event.getSource();
        System.out.println(bp.getCurrentIteration() + ". iteration : "+ bp.getTotalNetworkError());
    }  
}
