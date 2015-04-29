package com.thesmartpuzzle.kaggle.restaurants.learn;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.deeplearning4j.datasets.fetchers.MnistDataFetcher;
import org.deeplearning4j.distributions.Distributions;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.classifiers.dbn.DBN;
import org.deeplearning4j.nn.WeightInit;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.nd4j.linalg.api.activation.Activations;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class CDBNTest {

    private static Logger log = LoggerFactory.getLogger(CDBNTest.class);

    public static void main(String[] args) throws Exception {
        RandomGenerator gen = new MersenneTwister(123);

        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder()
                .momentum(5e-1f).constrainGradientToUnitNorm(false).iterations(1000)
                .withActivationType(NeuralNetConfiguration.ActivationType.SAMPLE)
                .lossFunction(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY).rng(gen)
                .learningRate(1e-1f).nIn(784).nOut(10).build();

        DBN d = new DBN.Builder().configure(conf)
                .hiddenLayerSizes(new int[]{500, 250, 100})
                .build();

        d.getOutputLayer().conf().setNumIterations(10);
        NeuralNetConfiguration.setClassifier(d.getOutputLayer().conf());


        MnistDataFetcher fetcher = new MnistDataFetcher(true);
        fetcher.fetch(1000);
        DataSet d2 = fetcher.next();

        d.fit(d2);

        INDArray predict2 = d.output(d2.getFeatureMatrix());

        Evaluation eval = new Evaluation();
        eval.eval(d2.getLabels(),predict2);
        log.info(eval.stats());
        int[] predict = d.predict(d2.getFeatureMatrix());
        log.info("Predict " + Arrays.toString(predict));
        
    }
}
