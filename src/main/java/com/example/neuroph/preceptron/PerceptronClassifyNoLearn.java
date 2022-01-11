package com.example.neuroph.preceptron;

import org.neuroph.core.*;
import org.neuroph.nnet.comp.neuron.InputNeuron;
import org.neuroph.util.*;

import java.util.Arrays;
import java.util.Scanner;

public class PerceptronClassifyNoLearn extends NeuralNetwork {


    public PerceptronClassifyNoLearn(int i) {
        createNetwork(i);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line = null;
        double[] input = new double[2];
        PerceptronClassifyNoLearn perceptron = new PerceptronClassifyNoLearn(2);
        try {
            while ((line = scanner.nextLine()) != null) {
                String[] numbers = line.split("[\\s|,;]");
                input[0] = Double.parseDouble(numbers[0]);
                input[1] = Double.parseDouble(numbers[1]);
                perceptron.setInput(input);
                perceptron.calculate();
                double[] networkOutput = perceptron.getOutput();
                System.out.println(Arrays.toString(input) + " =" + posToString(networkOutput));
            }
        } finally {
            scanner.close();
        }

    }

    private static String posToString(double[] arr) {
        if (arr[0]>0){
            if (arr[1]>0){
                return "第一象限";
            }else {
                return "第四象限";
            }
        }else {
            if (arr[1]>0){
                return "第二象限";
            }else {
                return "第三象限";
            }
        }
    }

    private void createNetwork(int inputNeuronsCount) {
        //设置网络类别为感知机 
        this.setNetworkType(NeuralNetworkType.PERCEPTRON);
        //输入神经元建立，表示输入的刺激
        NeuronProperties inputNeuronProperties = new NeuronProperties();
        inputNeuronProperties.setProperty("neuronType", InputNeuron.class);
        //由输入神经元构成的输入层
        Layer inputLayer = LayerFactory.createLayer(inputNeuronsCount, inputNeuronProperties);
        this.addLayer(inputLayer);
        NeuronProperties outputNeuronProperties = new NeuronProperties();
        outputNeuronProperties.setProperty("transferFunction", TransferFunctionType.STEP);
        Layer outputLayer = LayerFactory.createLayer(2, outputNeuronProperties);
        this.addLayer(outputLayer);
        ConnectionFactory.fullConnect(inputLayer, outputLayer);
        NeuralNetworkFactory.setDefaultIO(this);
        Neuron n = outputLayer.getNeuronAt(0);
        n.getInputConnections().get(0).getWeight().setValue(1);
        n.getInputConnections().get(1).getWeight().setValue(0);
        n = outputLayer.getNeuronAt(1);
        n.getInputConnections().get(0).getWeight().setValue(0);
        n.getInputConnections().get(1).getWeight().setValue(1);
    }
}