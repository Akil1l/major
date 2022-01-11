package com.example.demo.neuroph;

import com.example.neuroph.mlperceptron.MlPerceptron;
import com.example.neuroph.mlperceptron.MlPerceptronBinOutput;
import org.neuroph.core.Connection;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.IterativeLearning;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * @program: demo
 * @description:
 * @author: Akil
 * @create: 2021-03-23 13:59
 **/

public class MaJonDeepLearning implements LearningEventListener {
    public static void main(String[] args) throws FileNotFoundException {
        new MaJonDeepLearning().run();
    }

    public void run() throws FileNotFoundException {
        // 训练数据
        DataSet trainingSet = new DataSet(2, 1);
        trainingSet.add(new DataSetRow(new double[]{1, 4}, new double[]{0}));
        trainingSet.add(new DataSetRow(new double[]{1, 5}, new double[]{0}));
        trainingSet.add(new DataSetRow(new double[]{2, 4}, new double[]{0}));
        trainingSet.add(new DataSetRow(new double[]{2, 5}, new double[]{0}));
        trainingSet.add(new DataSetRow(new double[]{3, 1}, new double[]{1}));
        trainingSet.add(new DataSetRow(new double[]{3, 2}, new double[]{1}));
        trainingSet.add(new DataSetRow(new double[]{4, 1}, new double[]{1}));
        trainingSet.add(new DataSetRow(new double[]{4, 2}, new double[]{1}));
        // 2个输入数据，1个输出
        MultiLayerPerceptron myPerceptron = new MultiLayerPerceptron(TransferFunctionType.STEP, 2, 1);
//        MlPerceptron myPerceptron = (MlPerceptron) MlPerceptronBinOutput.load(new FileInputStream("Xor.neet"));
        LearningRule learningRule = myPerceptron.getLearningRule();
//        myPerceptron.getLearningRule().setMaxIterations(1);
        learningRule.addListener(this);
        // 训练神经网络
        System.out.println("Training neural network...");
        myPerceptron.learn(trainingSet);
        myPerceptron.save("Fury.neet");
        // 测试神经网络
        System.out.println("Testing trained neural network");
        testNeuralNetwork(myPerceptron, trainingSet);
    }

    public static void testNeuralNetwork(NeuralNetwork neuralNet, DataSet testSet) {

        for (DataSetRow testSetRow : testSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            double[] networkOutput = neuralNet.getOutput();
            System.out.print("Input: " + Arrays.toString(testSetRow.getInput()));
            System.out.println(" Output: " + Arrays.toString(networkOutput));
        }
    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        System.out.println("============");
        System.out.println(event.getClass().toString());
        IterativeLearning bp = (IterativeLearning) event.getSource();
        System.out.println("iterate:" + bp.getCurrentIteration());
        Neuron neuron = (Neuron) bp.getNeuralNetwork().getOutputNeurons().get(0);

        for (Connection conn : neuron.getInputConnections()) {
            System.out.println(conn.getWeight().value);
        }
    }
}
