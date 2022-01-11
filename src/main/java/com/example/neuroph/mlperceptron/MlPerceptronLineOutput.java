
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
 * Multi Layer Perceptron neural network with Back propagation learning
 * algorithm.
 */
public class MlPerceptronLineOutput extends MlPerceptron {
    public MlPerceptronLineOutput(int... neuronsInLayers) {
        super(neuronsInLayers);
    }
    public MlPerceptronLineOutput(TransferFunctionType transferFunctionType, int... neuronsInLayers) {
        super(transferFunctionType,neuronsInLayers);
    }
    /**
     * @param neuronsInLayers collection of neuron numbers in getLayersIterator
     * @param neuronProperties neuron properties
     */
    protected void createNetwork(List<Integer> neuronsInLayers, NeuronProperties neuronProperties) {

        // set network type
        this.setNetworkType(NeuralNetworkType.MULTI_LAYER_PERCEPTRON);

        // create input layer
        NeuronProperties inputNeuronProperties = new NeuronProperties(InputNeuron.class, Linear.class);
        Layer layer = LayerFactory.createLayer(neuronsInLayers.get(0), inputNeuronProperties);
        layer.addNeuron(new BiasNeuron());
        this.addLayer(layer);

        // create layers
        Layer prevLayer = layer;
        
        int layerIdx = 1;
        for (layerIdx = 1; layerIdx < neuronsInLayers.size()-1; layerIdx++) {
            Integer neuronsNum = neuronsInLayers.get(layerIdx);
            // createLayer layer
            layer = LayerFactory.createLayer(neuronsNum, neuronProperties);
            layer.addNeuron(new BiasNeuron());
            // add created layer to network
            this.addLayer(layer);
            // createLayer full connectivity between previous and this layer
            if (prevLayer != null) {
                ConnectionFactory.fullConnect(prevLayer, layer);
            }

            prevLayer = layer;
        }

        Integer neuronsNum = neuronsInLayers.get(layerIdx);
        NeuronProperties outProperties=new NeuronProperties();
        outProperties.put("transferFunction", Linear.class);
        layer = LayerFactory.createLayer(neuronsNum, outProperties);
        this.addLayer(layer);
        ConnectionFactory.fullConnect(prevLayer, layer);
        prevLayer = layer;
        
        // set input and output cells for network
        NeuralNetworkFactory.setDefaultIO(this);

        // set learnng rule
        //this.setLearningRule(new SigmoidDeltaRule());
        this.setLearningRule(new BackPropagation());

        this.randomizeWeights(new NguyenWidrowRandomizer(-0.7, 0.7));

    }


}
