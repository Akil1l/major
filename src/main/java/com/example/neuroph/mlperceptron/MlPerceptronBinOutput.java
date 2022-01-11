
package com.example.neuroph.mlperceptron;

import org.neuroph.core.Layer;
import org.neuroph.core.transfer.Linear;
import org.neuroph.nnet.comp.neuron.BiasNeuron;
import org.neuroph.nnet.comp.neuron.InputNeuron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.*;
import org.neuroph.util.random.NguyenWidrowRandomizer;

import java.util.List;

/**
 * 具有2值输出的多层感知机
 * algorithm.
 */
public class MlPerceptronBinOutput extends MlPerceptron {
    
    public MlPerceptronBinOutput(TransferFunctionType transferFunctionType, int... neuronsInLayers) {
        super(transferFunctionType,neuronsInLayers);
    }
    
    /**
     * Creates MultiLayerPerceptron Network architecture - fully connected feed
     * forward with specified number of neurons in each layer
     * 
     * @param neuronsInLayers collection of neuron numbers in getLayersIterator
     * @param neuronProperties neuron properties
     */
    protected void createNetwork(List<Integer> neuronsInLayers, NeuronProperties neuronProperties) {
        this.setNetworkType(NeuralNetworkType.MULTI_LAYER_PERCEPTRON);
        // 输入层
        NeuronProperties inputNeuronProperties = new NeuronProperties(InputNeuron.class, Linear.class);
        Layer layer = LayerFactory.createLayer(neuronsInLayers.get(0), inputNeuronProperties);
        layer.addNeuron(new BiasNeuron());
        this.addLayer(layer);
        
        Layer prevLayer = layer;
        int layerIdx = 1;
        for (layerIdx = 1; layerIdx < neuronsInLayers.size()-1; layerIdx++) {
            Integer neuronsNum = neuronsInLayers.get(layerIdx);
            // 中间层，传输函数为Sigmoid
            layer = LayerFactory.createLayer(neuronsNum, neuronProperties);
            layer.addNeuron(new BiasNeuron());
            this.addLayer(layer);
            if (prevLayer != null) {
                ConnectionFactory.fullConnect(prevLayer, layer);
            }
            prevLayer = layer;
        }

        Integer neuronsNum = neuronsInLayers.get(layerIdx);
        NeuronProperties outProperties=new NeuronProperties();
        // 输出层为Step的二值函数
        outProperties.put("transferFunction", org.neuroph.core.transfer.Step.class);
        layer = LayerFactory.createLayer(neuronsNum, outProperties);
        this.addLayer(layer);
        ConnectionFactory.fullConnect(prevLayer, layer);
        prevLayer = layer;
        
        NeuralNetworkFactory.setDefaultIO(this);

        // 学习算法为反向传播
        this.setLearningRule(new BackPropagation());
        // Nguyen-Widrow方法初始化权值
        this.randomizeWeights(new NguyenWidrowRandomizer(-0.7, 0.7));
    }
}
