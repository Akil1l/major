
package com.example.neuroph.mlperceptron;

import org.neuroph.core.Connection;
import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;
import org.neuroph.core.transfer.Linear;
import org.neuroph.nnet.comp.neuron.BiasNeuron;
import org.neuroph.nnet.comp.neuron.InputNeuron;
import org.neuroph.util.*;

import java.util.List;

/**
 * Multi Layer Perceptron neural network with Back propagation learning
 * algorithm.
 */
public class MlPerceptronAndOutputNoLearn extends MlPerceptron {
    
    public MlPerceptronAndOutputNoLearn(TransferFunctionType transferFunctionType, int... neuronsInLayers) {
        super(transferFunctionType,neuronsInLayers);
    }

    protected void createNetwork(List<Integer> neuronsInLayers, NeuronProperties neuronProperties) {
        //多层感知机
        this.setNetworkType(NeuralNetworkType.MULTI_LAYER_PERCEPTRON);

        // 输入层，代表每一个输入数据
        NeuronProperties inputNeuronProperties = new NeuronProperties(InputNeuron.class, Linear.class);
        //inputNeuronProperties.setProperty("neuronType", InputNeuron.class);
        //outputNeuronProperties.setProperty("transferFunction", TransferFunctionType.LINEAR);
        Layer layer = LayerFactory.createLayer(neuronsInLayers.get(0), inputNeuronProperties);
        layer.addNeuron(new BiasNeuron());
        this.addLayer(layer);

        // 建立中间隐层
        Layer prevLayer = layer;
        int layerIdx = 1;
        for (layerIdx = 1; layerIdx < neuronsInLayers.size()-1; layerIdx++) {
            Integer neuronsNum = neuronsInLayers.get(layerIdx);
            layer = LayerFactory.createLayer(neuronsNum, neuronProperties);
            layer.addNeuron(new BiasNeuron());
            this.addLayer(layer);
            if (prevLayer != null) {
                ConnectionFactory.fullConnect(prevLayer, layer);
            }
            prevLayer = layer;
        }
        
        //设置初始权值
        Neuron n1=layer.getNeuronAt(0);
        List<Connection> l1 = n1.getInputConnections();
        l1.get(0).setWeight(new Weight(2));
        l1.get(1).setWeight(new Weight(2));
        l1.get(2).setWeight(new Weight(-1));
        
        Neuron n2=layer.getNeuronAt(1);
        List<Connection> l2 = n2.getInputConnections();
        l2.get(0).setWeight(new Weight(-2));
        l2.get(1).setWeight(new Weight(-2));
        l2.get(2).setWeight(new Weight(3));
        
        // 建立输出层
        Integer neuronsNum = neuronsInLayers.get(layerIdx);
        NeuronProperties outProperties=new NeuronProperties();
        // 输入函数为逻辑与
        outProperties.put("inputFunction", org.neuroph.core.input.And.class);
        layer = LayerFactory.createLayer(neuronsNum, outProperties);
        this.addLayer(layer);
        ConnectionFactory.fullConnect(prevLayer, layer);
        prevLayer = layer;
       
        NeuralNetworkFactory.setDefaultIO(this);
    }
}
