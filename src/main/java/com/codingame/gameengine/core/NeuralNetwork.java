package com.codingame.gameengine.core;

import java.util.ArrayList;
import java.util.Random;

public class NeuralNetwork
{
    private Random random = new Random();
    public float RandomNumber(double min, double max)
    {
        { // synchronize
            double span = max - min;
            return (float)((random.nextDouble() * span) + min);
        }
    }

    //fundamental
    private int[] layers;//layers
    private float[][] neurons;//neurons
    private float[][] biases;//biasses
    private float[][][] weights;//weights
    private int[] activations;//layers

    //genetic
    public float fitness = 0;//fitness

    //backprop
    public float learningRate = 0.01f;//learning rate
    public float cost = 0;


    public NeuralNetwork(int[] layers, String[] layerActivations)
    {
        this.layers = new int[layers.length];
        for (int i = 0; i < layers.length; i++)
        {
            this.layers[i] = layers[i];
        }
        activations = new int[layers.length - 1];
        for (int i = 0; i < layers.length - 1; i++)
        {
            String action = layerActivations[i];
            switch (action)
            {
                case "sigmoid":
                    activations[i] = 0;
                    break;
                case "tanh":
                    activations[i] = 1;
                    break;
                case "relu":
                    activations[i] = 2;
                    break;
                case "leakyrelu":
                    activations[i] = 3;
                    break;
                default:
                    activations[i] = 2;
                    break;
            }
        }
        InitNeurons();
        InitBiases();
        InitWeights();
    }


    private void InitNeurons()//create empty storage array for the neurons in the network.
    {
        ArrayList<float[]> neuronsList = new ArrayList<float[]>();
        for (int i = 0; i < layers.length; i++)
        {
            neuronsList.add(new float[layers[i]]);
        }
        neurons = neuronsList.toArray(neurons);
    }

    private void InitBiases()//initializes random array for the biases being held within the network.
    {
        ArrayList<float[]> biasList = new ArrayList<float[]>();
        for (int i = 1; i < layers.length; i++)
        {
            float[] bias = new float[layers[i]];
            for (int j = 0; j < layers[i]; j++)
            {
                bias[j] = RandomNumber(-0.5f, 0.5f);
            }
            biasList.add(bias);
        }
        biases = biasList.toArray(biases);
    }

    private void InitWeights()//initializes random array for the weights being held in the network.
    {
        ArrayList<float[][]> weightsList = new ArrayList<float[][]>();
        for (int i = 1; i < layers.length; i++)
        {
            ArrayList<float[]> layerWeightsList = new ArrayList<float[]>();
            int neuronsInPreviousLayer = layers[i - 1];
            for (int j = 0; j < layers[i]; j++)
            {
                float[] neuronWeights = new float[neuronsInPreviousLayer];
                for (int k = 0; k < neuronsInPreviousLayer; k++)
                {
                    neuronWeights[k] = RandomNumber(-0.5f, 0.5f);
                }
                layerWeightsList.add(neuronWeights);
            }
            weightsList.add(layerWeightsList.toArray(new float[0][]));
        }
        weights = weightsList.toArray(weights);
    }

    public float[] FeedForward(float[] inputs)//feed forward, inputs >==> outputs.
    {
        for (int i = 0; i < inputs.length; i++)
        {
            neurons[0][i] = inputs[i];
        }
        for (int i = 1; i < layers.length; i++)
        {
            int layer = i - 1;
            for (int j = 0; j < layers[i]; j++)
            {
                float value = 0f;
                for (int k = 0; k < layers[i - 1]; k++)
                {
                    value += weights[i - 1][j][k] * neurons[i - 1][k];
                }
                neurons[i][j] = activate(value + biases[i - 1][j], layer);
            }
        }
        return neurons[layers.length - 1];
    }
    //Backpropagation implemtation down until mutation.
    public float activate(float value, int layer)//all activation functions
    {
        switch (activations[layer])
        {
            case 0:
                return sigmoid(value);
            case 1:
                return tanh(value);
            case 2:
                return relu(value);
            case 3:
                return leakyrelu(value);
            default:
                return relu(value);
        }
    }
    public float activateDer(float value, int layer)//all activation function derivatives
    {
        switch (activations[layer])
        {
            case 0:
                return sigmoidDer(value);
            case 1:
                return tanhDer(value);
            case 2:
                return reluDer(value);
            case 3:
                return leakyreluDer(value);
            default:
                return reluDer(value);
        }
    }

    public float sigmoid(float x)//activation functions and their corrosponding derivatives
    {
        float k = (float)Math.exp(x);
        return k / (1.0f + k);
    }
    public float tanh(float x)
    {
        return (float)Math.tanh(x);
    }
    public float relu(float x)
    {
        return (0 >= x) ? 0 : x;
    }
    public float leakyrelu(float x)
    {
        return (0 >= x) ? 0.01f * x : x;
    }
    public float sigmoidDer(float x)
    {
        return x * (1 - x);
    }
    public float tanhDer(float x)
    {
        return 1 - (x * x);
    }
    public float reluDer(float x)
    {
        return (0 >= x) ? 0 : 1;
    }
    public float leakyreluDer(float x)
    {
        return (0 >= x) ? 0.01f : 1;
    }

    public void BackPropagate(float[] inputs, float[] expected)//backpropogation;
    {
        float[] output = FeedForward(inputs);//runs feed forward to ensure neurons are populated correctly

        cost = 0;
        for (int i = 0; i < output.length; i++) cost += (float)Math.pow(output[i] - expected[i], 2);//calculated cost of network
        cost = cost / 2;//this value is not used in calculions, rather used to identify the performance of the network



        ArrayList<float[]> gammaList = new ArrayList<float[]>();
        for (int i = 0; i < layers.length; i++)
        {
            gammaList.add(new float[layers[i]]);
        }
        float[][] gamma = new float[0][];
        gamma = gammaList.toArray(gamma);//gamma initialization

        int layer = layers.length - 2;
        for (int i = 0; i < output.length; i++) gamma[layers.length - 1][i] = (output[i] - expected[i]) * activateDer(output[i], layer);//Gamma calculation
        for (int i = 0; i < layers[layers.length - 1]; i++)//calculates the w' and b' for the last layer in the network
        {
            biases[layers.length - 2][i] -= gamma[layers.length - 1][i] * learningRate;
            for (int j = 0; j < layers[layers.length - 2]; j++)
            {

                weights[layers.length - 2][i][j] -= gamma[layers.length - 1][i] * neurons[layers.length - 2][j] * learningRate;//*learning
            }
        }

        for (int i = layers.length - 2; i > 0; i--)//runs on all hidden layers
        {
            layer = i - 1;
            for (int j = 0; j < layers[i]; j++)//outputs
            {
                gamma[i][j] = 0;
                for (int k = 0; k < gamma[i + 1].length; k++)
                {
                    gamma[i][j] += gamma[i + 1][k] * weights[i][k][j];
                }
                gamma[i][j] *= activateDer(neurons[i][j], layer);//calculate gamma
            }
            for (int j = 0; j < layers[i]; j++)//itterate over outputs of layer
            {
                biases[i - 1][j] -= gamma[i][j] * learningRate;//modify biases of network
                for (int k = 0; k < layers[i - 1]; k++)//itterate over inputs to layer
                {
                    weights[i - 1][j][k] -= gamma[i][j] * neurons[i - 1][k] * learningRate;//modify weights of network
                }
            }
        }
    }

    //Genetic implementations down onwards until save.

    public void Mutate(int high, float val)//used as a simple mutation function for any genetic implementations.
    {
        for (int i = 0; i < biases.length; i++)
        {
            for (int j = 0; j < biases[i].length; j++)
            {
                biases[i][j] = (RandomNumber(0f, high) <= 2) ? biases[i][j] += RandomNumber(-val, val) : biases[i][j];
            }
        }

        for (int i = 0; i < weights.length; i++)
        {
            for (int j = 0; j < weights[i].length; j++)
            {
                for (int k = 0; k < weights[i][j].length; k++)
                {
                    weights[i][j][k] = (RandomNumber(0f, high) <= 2) ? weights[i][j][k] += RandomNumber(-val, val) : weights[i][j][k];
                }
            }
        }
    }

    public int CompareTo(NeuralNetwork other) //Comparing For Genetic implementations. Used for sorting based on the fitness of the network
    {
        if (other == null) return 1;

        if (fitness > other.fitness)
            return 1;
        else if (fitness < other.fitness)
            return -1;
        else
            return 0;
    }

    public NeuralNetwork copy(NeuralNetwork nn) //For creatinga deep copy, to ensure arrays are serialzed.
    {
        for (int i = 0; i < biases.length; i++)
        {
            for (int j = 0; j < biases[i].length; j++)
            {
                nn.biases[i][j] = biases[i][j];
            }
        }
        for (int i = 0; i < weights.length; i++)
        {
            for (int j = 0; j < weights[i].length; j++)
            {
                for (int k = 0; k < weights[i][j].length; k++)
                {
                    nn.weights[i][j][k] = weights[i][j][k];
                }
            }
        }
        return nn;
    }
}