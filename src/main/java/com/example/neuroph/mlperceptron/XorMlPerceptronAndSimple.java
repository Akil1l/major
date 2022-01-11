package com.example.neuroph.mlperceptron;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.TransferFunctionType;

import java.util.Arrays;

public class XorMlPerceptronAndSimple{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new XorMlPerceptronAndSimple().run();
	}
	
    public void run() {
        // 建立异或测试数据，不需要训练神经网络，故无需期望值
        DataSet trainingSet = new DataSet(2,1);
        trainingSet.add(new DataSetRow(new double[]{0, 0}, new double[]{Double.NaN}));
        trainingSet.add(new DataSetRow(new double[]{0, 1}, new double[]{Double.NaN}));
        trainingSet.add(new DataSetRow(new double[]{1, 0}, new double[]{Double.NaN}));
        trainingSet.add(new DataSetRow(new double[]{1, 1}, new double[]{Double.NaN}));

        // 神经网络使用Step作为传输函数
        // 拥有2个输入点，2个中间神经元和1个输入神经元
        MlPerceptron myPerceptron = new MlPerceptronAndOutputNoLearn(TransferFunctionType.STEP,2,2,1);
        
        //测试网络对异或的反应
        for(DataSetRow testSetRow : trainingSet.getRows()) {
        	myPerceptron.setInput(testSetRow.getInput());
        	myPerceptron.calculate();
            double[] networkOutput = myPerceptron.getOutput();

            System.out.print("Input: " + Arrays.toString( testSetRow.getInput() ) );
            System.out.println(" Output: " + Arrays.toString( networkOutput) );
        }
    }
}
