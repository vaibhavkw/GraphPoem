package diction;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;


public class Weka {

	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";

	protected Classifier m_Classifier = null;


	protected Filter m_Filter = null;

	protected String m_TrainingFile = null;

	protected Instances m_Training = null;

	Instance inst = null;

	protected Evaluation m_Evaluation = null;

	MetaphorGeneric objMG;
	
	GloveGigaWord objGlove = null;

	static int percent = 90;

	public Weka() {
		super();
	}


	public void setClassifier(String name, String[] options) throws Exception {
		m_Classifier = AbstractClassifier.forName(name, options);
	}


	public void setFilter(String name, String[] options) throws Exception {
		m_Filter = (Filter) Class.forName(name).newInstance();
		if (m_Filter instanceof OptionHandler) {
			((OptionHandler) m_Filter).setOptions(options);
		}
	}


	public void setTraining(String name) throws Exception {
		m_TrainingFile = name;
		m_Training = new Instances(new BufferedReader(new FileReader(m_TrainingFile)));
		inst = (Instance) m_Training.instance(0).copy();

		/*Instances tmp = new Instances(new BufferedReader(new FileReader(m_TrainingFile)));
		Remove remove = new Remove();
		remove.setAttributeIndices("1");
		remove.setInvertSelection(false);
		remove.setInputFormat(tmp);
		m_Training = Filter.useFilter(tmp, remove);*/

		m_Training.setClassIndex(m_Training.numAttributes() - 1);
	}


	public void executeCrossValidation() throws Exception {
		// run filter
		m_Filter.setInputFormat(m_Training);
		Instances filtered = Filter.useFilter(m_Training, m_Filter);

		// train classifier on complete file for tree
		m_Classifier.buildClassifier(filtered);

		// 10fold CV with seed=1
		m_Evaluation = new Evaluation(filtered);
		m_Evaluation.crossValidateModel(m_Classifier, filtered, 10,
				m_Training.getRandomNumberGenerator(1));
	}

	public void executePercentSplit() throws Exception {
		System.out.println("Executing training..");
		// run filter
		m_Filter.setInputFormat(m_Training);
		Instances filtered = Filter.useFilter(m_Training, m_Filter);

		/*// train classifier on complete file for tree
		m_Classifier.buildClassifier(filtered);

		// 10fold CV with seed=1
		m_Evaluation = new Evaluation(filtered);
		m_Evaluation.crossValidateModel(m_Classifier, filtered, 10,
				m_Training.getRandomNumberGenerator(1));
		 */

		filtered.randomize(new Random(1));		

		int trainSize = (int) Math.round(filtered.numInstances() * percent / 100);
		int testSize = filtered.numInstances() - trainSize;
		Instances train = new Instances(filtered, 0, trainSize);
		Instances test = new Instances(filtered, trainSize, testSize);
		//inst = (Instance) test.instance(0).copy();

		m_Classifier.buildClassifier(train);
		m_Evaluation = new Evaluation(train);
		m_Evaluation.evaluateModel(m_Classifier, test);

		System.out.println("Percent accuracy : " + Math.round(m_Evaluation.pctCorrect()*10000)/10000.0d + " %");

		/*for (int i = 0; i < test.numInstances(); i++) {
			double clsLabel = m_Classifier.classifyInstance(test.instance(i));
			System.out.println((i+1)+ " : " + test.instance(i).classValue() + " : " + clsLabel);
			//labeled.instance(i).setClassValue(clsLabel);
		}*/

		//m_Classifier.buildClassifier(filtered);
		//System.out.println("Full dataset trained");
		System.out.println(percent + "% dataset trained");
	}

	public void executeTraining() throws Exception {
		System.out.println("Executing training..");
		m_Filter.setInputFormat(m_Training);
		Instances filtered = Filter.useFilter(m_Training, m_Filter);

		filtered.randomize(new Random(1));
		m_Classifier.buildClassifier(filtered);

		m_Evaluation = new Evaluation(filtered);
		m_Evaluation.evaluateModel(m_Classifier, filtered);

		System.out.println("Percent accuracy : " + Math.round(m_Evaluation.pctCorrect()*10000)/10000.0d + " %");
		System.out.println("Full dataset trained");
	}

	public void predict(String word1, String word2) throws Exception {
		createInstance(inst, word1, word2);
		double clsLabel = m_Classifier.classifyInstance(inst);
		if(clsLabel == 0.0){
			System.out.println("Prediction: literal");
		}else{
			System.out.println("Prediction: metaphor");
		}

	}

	public void createInstance(Instance inst, String word1, String word2){
		//System.out.println("inst.value:" + inst.value(0));

		String retStr = objMG.makeStringTypeGen(word1, word2);
		String []strArr = retStr.split(",");
		for(int i=1;i<strArr.length;i++){
			//System.out.println(strArr[i]);
			if(strArr[i].equals("?")){
				inst.setMissing(i-1);
				continue;
			}
			if(strArr[i].equals("false")){
				inst.setValue(i-1, 0.0);
				continue;
			}
			if(strArr[i].equals("true")){
				inst.setValue(i-1, 1.0);
				continue;
			}
			inst.setValue(i-1, Double.parseDouble(strArr[i]));
		}
		//System.out.println("after:" + inst.value(103));
		inst.setMissing(103);
		//System.out.println(inst.value(103));
	}

	public void serializeModel() {
		try {
			FileOutputStream fileOut = new FileOutputStream(basePath + "weka/randomforest_full.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(m_Classifier);
			out.close();
			fileOut.close();
			System.out.printf("Serialized data is saved in model.ser");
		}catch(IOException i) {
			i.printStackTrace();
		}
	}


	@Override
	public String toString() {
		StringBuffer result;

		result = new StringBuffer();
		result.append("Weka - Demo\n===========\n\n");

		result.append("Classifier...: " + Utils.toCommandLine(m_Classifier) + "\n");
		if (m_Filter instanceof OptionHandler) {
			result.append("Filter.......: " + m_Filter.getClass().getName() + " "
					+ Utils.joinOptions(((OptionHandler) m_Filter).getOptions()) + "\n");
		} else {
			result.append("Filter.......: " + m_Filter.getClass().getName() + "\n");
		}
		result.append("Training file: " + m_TrainingFile + "\n");
		result.append("\n");

		result.append(m_Classifier.toString() + "\n");
		result.append(m_Evaluation.toSummaryString() + "\n");
		try {
			result.append(m_Evaluation.toMatrixString() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			result.append(m_Evaluation.toClassDetailsString() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.toString();
	}


	public static String usage() {
		return "\nusage:\n  " + Weka.class.getName()
				+ "  CLASSIFIER <classname> [options] \n"
				+ "  FILTER <classname> [options]\n" + "  DATASET <trainingfile>\n\n"
				+ "e.g., \n" + "  java -classpath \".:weka.jar\" WekaDemo \n"
				+ "    CLASSIFIER weka.classifiers.trees.J48 -U \n"
				+ "    FILTER weka.filters.unsupervised.instance.Randomize \n"
				+ "    DATASET iris.arff\n";
	}


	public static void main(String[] args) throws Exception {
		long starttime = System.currentTimeMillis();
		Weka obj = new Weka();
		
		//obj.objGlove = new GloveGigaWord();
		//obj.objGlove.initProp();
		//obj.serializeMGObj();
		//System.exit(0);
		
		obj.objMG = new MetaphorGeneric();
		System.out.println("Starting init..");
		obj.objMG.preInit();
		System.out.println("Init done");

		String inputText = "sunshine is happiness to me";
		ArrayList<String> list = obj.objMG.getWordPairs(inputText.toLowerCase());

		String classifier = "";
		String filter = "";
		String dataset = "";
		Vector<String> classifierOptions = new Vector<String>();
		Vector<String> filterOptions = new Vector<String>();

		/*int i = 0;
		String current = "";
		boolean newPart = false;
		do {
			// determine part of command line
			if (args[i].equals("CLASSIFIER")) {
				current = args[i];
				i++;
				newPart = true;
			} else if (args[i].equals("FILTER")) {
				current = args[i];
				i++;
				newPart = true;
			} else if (args[i].equals("DATASET")) {
				current = args[i];
				i++;
				newPart = true;
			}

			if (current.equals("CLASSIFIER")) {
				if (newPart) {
					classifier = args[i];
				} else {
					classifierOptions.add(args[i]);
				}
			} else if (current.equals("FILTER")) {
				if (newPart) {
					filter = args[i];
				} else {
					filterOptions.add(args[i]);
				}
			} else if (current.equals("DATASET")) {
				if (newPart) {
					dataset = args[i];
				}
			}

			// next parameter
			i++;
			newPart = false;
		} while (i < args.length);
		 */

		classifier = "weka.classifiers.trees.RandomForest";
		//classifier = "weka.classifiers.functions.SMO";
		//classifierOptions.add("-K");
		//classifierOptions.add("weka.classifiers.functions.supportVector.Puk -O 1.0 -S 1.0 -C 250007");
		//filter = "weka.filters.unsupervised.instance.Randomize";
		filter = "weka.filters.unsupervised.attribute.Remove";
		filterOptions.add("-R");
		filterOptions.add("1");
		dataset = basePath + "pofo_680_plus_shutova_plus_trofi_glove.arff";

		if (classifier.equals("") || filter.equals("") || dataset.equals("")) {
			System.out.println("Not all parameters provided!");
			System.out.println(Weka.usage());
			System.exit(2);
		}

		
		obj.setClassifier(classifier, classifierOptions.toArray(new String[classifierOptions.size()]));
		obj.setFilter(filter, filterOptions.toArray(new String[filterOptions.size()]));
		obj.setTraining(dataset);
		//demo.executeCrossValidation();
		obj.executePercentSplit();
		//obj.executeTraining();
		//obj.serializeModel();
		//obj.serializeMGObj();
		//System.out.println(obj.toString());

		/*obj.predict("elephant", "room");
		obj.predict("pig", "flies");
		obj.predict("pig", "eats");
		obj.predict("bike", "flies");
		obj.predict("bike", "ride");
		obj.predict("night", "dark");
		obj.predict("i", "am");
		obj.predict("elephant", "drinks");
		obj.predict("elephant", "dance");
		obj.predict("rain", "dance");*/

		for(int i=0;i<list.size();i++){
			String val = list.get(i);
			String []arr = val.split("##");
			obj.predict(arr[0], arr[1]);
		}

		long endtime = System.currentTimeMillis();
		System.out.println("Time taken : " + (endtime-starttime)/1000.0 + " secs");


	}
}
