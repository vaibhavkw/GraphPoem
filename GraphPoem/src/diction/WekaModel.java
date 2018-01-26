package diction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;


public class WekaModel {

	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";

	protected Classifier m_Classifier = null;

	protected String m_TrainingFile = null;

	protected Instances m_Training = null;

	Instance inst = null;

	public MetaphorGeneric objMG;

	static int percent = 50;

	public WekaModel() {
		super();
	}


	public void setTraining(String name) throws Exception {
		m_TrainingFile = name;
		m_Training = new Instances(new BufferedReader(new FileReader(m_TrainingFile)));
		inst = (Instance) m_Training.instance(0).copy();

		m_Training.setClassIndex(m_Training.numAttributes() - 1);
	}


	public String predict(String word1, String word2) throws Exception {
		createInstance(inst, word1, word2);
		double clsLabel = m_Classifier.classifyInstance(inst);
		double confidence = m_Classifier.distributionForInstance(inst)[(int)clsLabel];
		if(clsLabel == 0.0){
			System.out.println("Prediction: literal with confidence " + m_Classifier.distributionForInstance(inst)[(int)clsLabel]);
			return "literal with confidence " + m_Classifier.distributionForInstance(inst)[(int)clsLabel];
		}else{
			System.out.println("Prediction: metaphor with confidence " + m_Classifier.distributionForInstance(inst)[(int)clsLabel]);
			return "metaphor with confidence " + m_Classifier.distributionForInstance(inst)[(int)clsLabel];
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
	
	public void loadModel() throws Exception {
		FileInputStream modelFile = new FileInputStream(basePath + "weka/randomforest_full.ser");
		RandomForest rf = (RandomForest) (new ObjectInputStream(modelFile)).readObject();
		modelFile.close();
		modelFile = null;
		m_Classifier = rf;
		
		String dataset = basePath + "weka/temp.arff";
		setTraining(dataset);
	}


/*	public static void main(String[] args) throws Exception {
		System.out.println("Starting..");
		long starttime = System.currentTimeMillis();		

		WekaModel obj = new WekaModel();
		obj.loadModel();
		
		obj.objMG = new MetaphorGeneric();
		System.out.println("Starting init..");
		obj.objMG.preInit();
		System.out.println("Init done");

		String inputText = "I went to the classroom to absorb knowledge";
		ArrayList<String> list = obj.objMG.getWordPairs(inputText.toLowerCase());

		obj.predict("elephant", "room");
		obj.predict("pig", "flies");
		obj.predict("pig", "eats");
		obj.predict("bike", "flies");
		obj.predict("bike", "ride");
		obj.predict("night", "dark");
		obj.predict("i", "am");
		obj.predict("elephant", "drinks");
		obj.predict("elephant", "dance");
		obj.predict("rain", "dance");

		for(int i=0;i<list.size();i++){
			String val = list.get(i);
			String []arr = val.split("##");
			obj.predict(arr[0], arr[1]);
		}

		long endtime = System.currentTimeMillis();
		System.out.println("\nTime taken : " + (endtime-starttime)/1000.0 + " secs");

	}*/
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starting..");
		long starttime = System.currentTimeMillis();

		WekaModel obj = new WekaModel();
		obj.loadModel();
		
		obj.objMG = new MetaphorGeneric();
		System.out.println("Starting init..");
		obj.objMG.preInit();
		System.out.println("Init done");
		
		String newline = System.getProperty("line.separator");

		String inputText = "I have too much power";
		
		String []newlineArr = inputText.split(newline);	
		System.out.println("Lines : " + newlineArr.length);
		
		for(int t=0;t<newlineArr.length;t++){
			ArrayList<String> list = obj.objMG.getWordPairs(newlineArr[t].toLowerCase());

			for(int i=0;i<list.size();i++){
				String val = list.get(i);
				String []arr = val.split("##");
				obj.predict(arr[0], arr[1]);
			}

		}

		long endtime = System.currentTimeMillis();
		System.out.println("\nTime taken : " + (endtime-starttime)/1000.0 + " secs");

	}
}
