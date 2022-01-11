package com.example.neuroph.preceptron;

import org.neuroph.core.Connection;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;
import org.neuroph.core.learning.SupervisedLearning;

import java.io.Serializable;

public class PerceptronLearningRule extends SupervisedLearning implements Serializable {

    @Override
    protected void calculateWeightChanges(double[] outputError) {
        int i = 0;
        for (Neuron neuron : neuralNetwork.getOutputNeurons()) {
            neuron.setDelta(outputError[i]);
            double neuronError = neuron.getDelta();
            //根据所有的神经元输入迭代学习
            for (Connection connection : neuron.getInputConnections()) {
                //神经元的一个输入
                double input = connection.getInput();
                //计算权值的变更
                double weightChange = -0.1 * neuronError * input;
                //更新权值
                Weight weight = connection.getWeight();
                weight.weightChange = weightChange;
            }
            i++;
        }
    }
}