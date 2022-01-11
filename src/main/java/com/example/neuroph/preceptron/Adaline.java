package com.example.neuroph.preceptron;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.comp.neuron.BiasNeuron;
import org.neuroph.nnet.comp.neuron.InputNeuron;
import org.neuroph.nnet.learning.LMS;
import org.neuroph.util.*;

public class Adaline extends NeuralNetwork {

    /**
     * The class fingerprint that is set to indicate serialization compatibility
     * with a previous version of the class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates new Perceptron with specified number of neurons in input and
     * output layer, with Step trqansfer function
     *
     * @param inputNeuronsCount number of neurons in input layer
     */
    public Adaline(int inputNeuronsCount, int outputNeuronsCount) {
        this.createNetwork(inputNeuronsCount, outputNeuronsCount);
    }

    /**
     * Creates perceptron architecture with specified number of neurons in input
     * and output layer, specified transfer function
     *
     * @param inputNeuronsCount number of neurons in input layer
     */
    private void createNetwork(int inputNeuronsCount, int outputNeuronsCount) {
        // 设置网络类别为 感知机
        this.setNetworkType(NeuralNetworkType.ADALINE);

        // 输入神经元建立 ，表示输入的刺激
        NeuronProperties inputNeuronProperties = new NeuronProperties();
        inputNeuronProperties.setProperty("neuronType", InputNeuron.class);

        // 由输入神经元构成的输入层
        Layer inputLayer = LayerFactory.createLayer(inputNeuronsCount, inputNeuronProperties);
        this.addLayer(inputLayer);
        // 在输入层增加BiasNeuron，表示神经元偏置
        inputLayer.addNeuron(new BiasNeuron());
        // 传输函数是LINEAR
        NeuronProperties outputNeuronProperties = new NeuronProperties();
        outputNeuronProperties.setProperty("transferFunction", TransferFunctionType.LINEAR);

        // 输出层，也就是神经元
        Layer outputLayer = LayerFactory.createLayer(outputNeuronsCount, outputNeuronProperties);
        this.addLayer(outputLayer);

        // 将输入层的输入导向神经元
        ConnectionFactory.fullConnect(inputLayer, outputLayer);
        NeuralNetworkFactory.setDefaultIO(this);
        // 设置感知机学习算法
        LMS lms = new LMS();
        lms.setLearningRate(0.00001);
        lms.setMaxError(0.00001);
        // LMS学习算法可以理解为第3章介绍的感知机学习算法
        this.setLearningRule(lms);
    }

}