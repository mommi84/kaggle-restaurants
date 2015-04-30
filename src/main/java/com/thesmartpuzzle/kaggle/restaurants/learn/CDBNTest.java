package com.thesmartpuzzle.kaggle.restaurants.learn;

import java.io.IOException;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;
import org.deeplearning4j.datasets.fetchers.MnistDataFetcher;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.classifiers.dbn.DBN;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class CDBNTest {

	private static Logger log = Logger.getLogger(CDBNTest.class);

	private static int iterations = 100;
	private static String layerSpec = "50,25,10";
	private static float l_rate = 1e-1f;

	private static int[] layers;

	public static void main(String[] args) throws IOException {

		CDBNTest.loadArgs(args);
		CDBNTest.run();

	}

	private static void loadLayers() {
		String[] lyr = layerSpec.split(",");
		layers = new int[lyr.length];
		for (int i = 0; i < layers.length; i++)
			layers[i] = Integer.parseInt(lyr[i]);
	}

	private static void loadArgs(String[] args) {
		if (args.length == 0)
			return;

		iterations = Integer.parseInt(args[0]);
		layerSpec = args[1];
		l_rate = Float.parseFloat(args[2]);
	}

	public static void run() throws IOException {

		log.info(CDBNTest.class.getSimpleName() + " started.");
		log.info("iterations = " + iterations);
		log.info("layers = " + layerSpec);
		log.info("learning rate = " + l_rate);

		CDBNTest.loadLayers();

		RandomGenerator gen = new MersenneTwister(123);

		NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder()
				.momentum(5e-1f)
				.constrainGradientToUnitNorm(false)
				.iterations(iterations)
				.withActivationType(
						NeuralNetConfiguration.ActivationType.SAMPLE)
				.lossFunction(
						LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
				.rng(gen).learningRate(l_rate).nIn(784).nOut(10).build();

		DBN dbn = new DBN.Builder().configure(conf).hiddenLayerSizes(layers)
				.build();

		dbn.getOutputLayer().conf().setNumIterations(10);
		NeuralNetConfiguration.setClassifier(dbn.getOutputLayer().conf());

		// loads the MNIST dataset (handwritten digits, classes 0-9)
		MnistDataFetcher fetcher = new MnistDataFetcher();
		fetcher.fetch(10);
		DataSet d2 = fetcher.next();

		// Vectorizer v = null;
		// DataSet d2 = v.vectorize();

		dbn.fit(d2);

		// evaluation
		INDArray predict2 = dbn.output(d2.getFeatureMatrix());

		Evaluation eval = new Evaluation();
		eval.eval(d2.getLabels(), predict2);
		log.info(eval.stats());
		int[] predict = dbn.predict(d2.getFeatureMatrix());
		log.info("Predict " + Arrays.toString(predict));

	}
}
