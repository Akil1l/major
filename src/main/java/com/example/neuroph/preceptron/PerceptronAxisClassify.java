package com.example.neuroph.preceptron;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.IterativeLearning;

import java.util.Arrays;
import java.util.Random;

import static org.apache.commons.lang3.RandomUtils.nextDouble;

/**
 * @program: demo
 * @description: PerceptronAxisClassiy
 * @author: Akil
 * @create: 2021-03-09 17:27
 **/

public class PerceptronAxisClassify implements LearningEventListener {
    public static void main(String[] args) {
        new PerceptronAxisClassify().run();
    }

    public void run() {
        DataSet trainingSet = new DataSet(2, 2);
        generateData(trainingSet);
        // 感知机有2个输入
        SimplePerceptron2 myPerceptron = new SimplePerceptron2(2);

        PerceptronLearningRule learningRule = (PerceptronLearningRule) myPerceptron.getLearningRule();
        learningRule.setMaxError(0.001);
        learningRule.addListener(this);

        // 进行学习
        System.out.println("Training neural network...");
        myPerceptron.learn(trainingSet);

        // 测试感知机是否能给出正确输出
        System.out.println("Testing trained neural network");
        testNeuralNetwork(myPerceptron);
    }

    private static void generateData(DataSet trainingSet) {
        for (int i = 0; i < 10000; i++) {
            //第一象限
            trainingSet.add(new DataSetRow(new double[]{1 * nextDouble(), 1 * nextDouble()}, new double[]{1, 1}));
            //第二象限
            trainingSet.add(new DataSetRow(new double[]{-1 * nextDouble(), 1 * nextDouble()}, new double[]{0, 1}));
            //第三象限
            trainingSet.add(new DataSetRow(new double[]{-1 * nextDouble(), -1 * nextDouble()}, new double[]{0, 0}));
            //第四象限
            trainingSet.add(new DataSetRow(new double[]{1 * nextDouble(), -1 * nextDouble()}, new double[]{1, 0}));
        }
    }

    /**
     * Prints network output for the each element from the specified training set.
     *
     * @param neuralNet neural network
     * @param testSet   training set
     */

    static Random r = new Random();

    public static double nextDouble() {
        double re = 0;
        while ((re = r.nextDouble()) != 0) {
            return re;
        }
        return r.nextDouble();
    }

    public static void testNeuralNetwork(NeuralNetwork neuralNet) {

        DataSet dataSet = new DataSet(2, 2);
        generateData(dataSet);
        //正确总数
        int correctCount = 0;
        int incorrectCount = 0;

        //遍历整个测试数组
        for (DataSetRow dataSetRow : dataSet.getRows()) {
            //获得一个输入
            neuralNet.setInput(dataSetRow.getInput());
            neuralNet.calculate();
            double[] output = neuralNet.getOutput();

            //实际输出跟期望输出相比较
            if (Arrays.equals(output, dataSetRow.getDesiredOutput())) {
                correctCount++;
            } else {
                incorrectCount++;
            }

        }
        System.out.println("正确率：" + correctCount * 1.0 / (correctCount + incorrectCount));
    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        IterativeLearning bp = (IterativeLearning) event.getSource();
        System.out.println("iterate:" + bp.getCurrentIteration());
        System.out.println(Arrays.toString(bp.getNeuralNetwork().getWeights()));
        System.out.print("TotalNetworkError:");
        System.out.println(((PerceptronLearningRule) bp.getNeuralNetwork().getLearningRule()).getTotalNetworkError());
    }
}
