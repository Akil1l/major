package com.example.demo.deep;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.nnet.learning.ResilientPropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.Arrays;

public class CalculatePerceptron {
    public static void main(String[] args) {
//        建立AND训练集
        DataSet trainAndSet = new DataSet(2, 1);
        trainAndSet.add(new DataSetRow(new double[]{0, 0}, new double[]{0}));
        trainAndSet.add(new DataSetRow(new double[]{0, 1}, new double[]{0}));
        trainAndSet.add(new DataSetRow(new double[]{1, 0}, new double[]{0}));
        trainAndSet.add(new DataSetRow(new double[]{1, 1}, new double[]{1}));

//        建立OR训练集
        DataSet trainOrSet = new DataSet(2, 1);
        trainOrSet.add(new DataSetRow(new double[]{0, 0}, new double[]{0}));
        trainOrSet.add(new DataSetRow(new double[]{0, 1}, new double[]{1}));
        trainOrSet.add(new DataSetRow(new double[]{1, 0}, new double[]{1}));
        trainOrSet.add(new DataSetRow(new double[]{1, 1}, new double[]{1}));

//        建立XOR训练集
        DataSet trainXorSet = new DataSet(2, 1);
        trainXorSet.add(new DataSetRow(new double[]{0, 0}, new double[]{0}));
        trainXorSet.add(new DataSetRow(new double[]{0, 1}, new double[]{1}));
        trainXorSet.add(new DataSetRow(new double[]{1, 0}, new double[]{1}));
        trainXorSet.add(new DataSetRow(new double[]{1, 1}, new double[]{0}));

//        建立感知机
        Perceptron perceptron = new Perceptron(2, 1);
//        监听
        perceptron.getLearningRule().addListener(learningEvent -> {
            SupervisedLearning bp = (SupervisedLearning) learningEvent.getSource();
            if (learningEvent.getEventType() != LearningEvent.Type.LEARNING_STOPPED) {
                System.out.println(bp.getCurrentIteration() + ". iteration : " + bp.getTotalNetworkError());
            }
        });
//        训练AND集
        perceptron.learn(trainAndSet);
        System.out.println("测试感知机AND集训练结果：");
        perceptron.save("AND_learn_result.nnet");
        testNeuralNetwork(perceptron, trainAndSet);

//        训练OR集
        perceptron.learn(trainOrSet);
        System.out.println("测试感知机Or集训练结果：");
        perceptron.save("OR_learn_result.nnet");
        testNeuralNetwork(perceptron, trainOrSet);

//        训练XOR集
//        perceptron.learn(trainXorSet);
//        System.out.println("测试感知机Xor集训练结果：");
//        testNeuralNetwork(perceptron, trainXorSet);
//        由于XOR输入输出情况线性不可分，将无法完成训练
        //设置ResilientPropagation学习规则,作废，现阶段一般用反向传播机制
        multiStudy(trainXorSet);
//        resilientStudy(trainXorSet);


    }

    public static void testNeuralNetwork(NeuralNetwork<?> perceptron, DataSet tset) {

        for (DataSetRow dataRow : tset.getRows()) {

            perceptron.setInput(dataRow.getInput());
            perceptron.calculate();
            double[] networkOutput = perceptron.getOutput();
            System.out.print("Input: " + Arrays.toString(dataRow.getInput()));
            System.out.println(" Output: " + Arrays.toString(networkOutput));
        }
    }

    private static void resilientStudy(DataSet set) {

        // 转移函数采用sigmoid,也可以用tanh之类的

        MultiLayerPerceptron mlp = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 2, 3, 1);

        // ResilientPropagation学习规则

        mlp.setLearningRule(new ResilientPropagation());

        LearningRule learningRule = mlp.getLearningRule();

        // 学习

        learningRule.addListener(learningEvent -> {
            SupervisedLearning bp = (SupervisedLearning) learningEvent.getSource();
            if (learningEvent.getEventType() != LearningEvent.Type.LEARNING_STOPPED) {
                System.out.println(bp.getCurrentIteration() + ". iteration : " + bp.getTotalNetworkError());
            }
        });

        System.out.println("Training neural network...");

        mlp.learn(set);

        System.out.println("Learned in " + mlp.getLearningRule().getCurrentIteration() + " iterations");

        System.out.println("Testing trained neural network");

        testNeuralNetwork(mlp, set);


    }


    private static void multiStudy(DataSet set) {

        // 创建多层感知机，输入层2个神经元，隐含层3个神经元，最后输出层为1个隐含神经元，

        // 使用双曲正切TANH传输函数最后格式化输出

        MultiLayerPerceptron mlp = new MultiLayerPerceptron(TransferFunctionType.TANH, 2, 2, 1);

        // 启用batch模式

        if (mlp.getLearningRule() instanceof MomentumBackpropagation)

            ((MomentumBackpropagation) mlp.getLearningRule()).setBatchMode(true);


        //反向误差传播

        mlp.setLearningRule(new BackPropagation());

        LearningRule learningRule = mlp.getLearningRule();

        // 学习

        learningRule.addListener(learningEvent -> {
            SupervisedLearning bp = (SupervisedLearning) learningEvent.getSource();
            if (learningEvent.getEventType() != LearningEvent.Type.LEARNING_STOPPED) {
                System.out.println(bp.getCurrentIteration() + ". iteration : " + bp.getTotalNetworkError());
            }
        });

        mlp.learn(set);

        // 测试感知机

        testNeuralNetwork(mlp, set);

        // 保存结果

        mlp.save("mlp.nnet");

        NeuralNetwork<?> loadedMlp = NeuralNetwork.load("mlp.nnet");

        // test loaded neural network

        System.out.println("Testingloaded neural network");

        testNeuralNetwork(loadedMlp, set);


    }
}