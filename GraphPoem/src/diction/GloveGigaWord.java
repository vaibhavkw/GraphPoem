package diction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

public class GloveGigaWord {

	public static int dimensions = 100;

	public static String basePath = System.getProperty("user.dir") + "/resources/diction/glove/";

	public static String glovePath = "D:/study/graph_poem/Tools/Metaphor/WordEmbeddings/glove.6B/";

	Properties prop = new Properties();
	Properties prop2 = new Properties();

	public static void main(String[] args) {
		GloveGigaWord obj = new GloveGigaWord();
		System.out.println("Started");
		obj.initProp();
		//obj.initProp2();
		System.out.println("Ended");
		System.out.println(obj.prop.get("sandberger"));
		//obj.readGloveFile();
		//obj.processSpecificWords();
	}

	GloveGigaWord(){
		//initProp2();
	}

	public void initProp() {
		try {
			prop.load(new FileInputStream(basePath + "glove.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initProp2() {
		try {
			prop2.load(new FileInputStream(basePath + "glove_small.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveProp() {
		try {
			prop.store(new FileOutputStream(basePath + "glove.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveProp2() {
		try {
			prop2.store(new FileOutputStream(basePath + "glove_small.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getProp(String key){
		String value = prop.getProperty(key);
		if(value == null){
			/*for(int i=1;i<=dimensions;i++){
				value = value + ",0.0";
			}*/
			value = "";
		}
		return value;
	}

	public String getProp2(String key){
		String value = prop2.getProperty(key);
		if(value == null){
			/*for(int i=1;i<=dimensions;i++){
				value = value + ",0.0";
			}*/
			value = "";
		}
		return value;
	}


	public void readGloveFile(){
		File currentFile = new File(glovePath + "glove.6B.200d.txt");
		BufferedReader br = null;
		String sCurrentLine;		
		try{
			br = new BufferedReader(new FileReader(currentFile));
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				processLine(sCurrentLine);
				if(lineNumber%5000 == 0){
					saveProp();
					System.out.println(lineNumber);
				}
			}
			br.close();
			br = null;

			currentFile = null;

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void processLine(String str){
		String []arr = str.split(" ");
		String key = arr[0];
		String value = "";
		for(int i=1;i<arr.length;i++){
			value = value + arr[i] + ",";
		}
		prop.setProperty(key, value);
	}

	public void processSpecificWords(){
		File currentFile = new File(basePath + "type1_metaphor_final7.txt");
		BufferedReader br = null;
		String sCurrentLine;		
		try{
			br = new BufferedReader(new FileReader(currentFile));
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				processLine2(sCurrentLine);
			}
			br.close();
			br = null;
			currentFile = null;

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void processLine2(String str){
		String []arr = str.split(",");
		String key = arr[0];
		key = key.substring(1, key.length()-1);
		String []arr2 = key.split(" ");
		String noun1 = arr2[0];
		String noun2 = arr2[2];
		String val1 = getProp(noun1);
		String val2 = getProp(noun2);
		prop2.setProperty(noun1, val1);
		prop2.setProperty(noun2, val2);
		saveProp2();
		//prop.setProperty(key, value);
	}

	public String cosineSimilarity(String str1, String str2) {
		double out = 0.0;
		try{			
			String []arr1 = str1.split(",");
			String []arr2 = str2.split(",");
			if(arr1.length == 1 || arr1.length == 0){
				arr1 = getZeroArray();
			}
			if(arr2.length == 1 || arr2.length == 0){
				arr2 = getZeroArray();
			}
			double dotProduct = 0.0;
			double normA = 0.0;
			double normB = 0.0;
			for(int i=0;i<arr1.length;i++){
				double val1 = Double.parseDouble(arr1[i]);
				double val2 = Double.parseDouble(arr2[i]);
				dotProduct += val1 * val2;
				normA += Math.pow(val1, 2);
				normB += Math.pow(val2, 2);
				//retStr = retStr + diff + ",";
				//System.out.println(val1 + "::" + val2 + "::" + diff);
			}

			out = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
		}catch(Exception e){			
			System.out.println(str1 + "::" + str2);
			e.printStackTrace();
		}
		return String.valueOf(out);
	}

	public String[] getZeroArray(){
		String []retArr = new String[dimensions+1];
		for(int i=0;i<dimensions+1;i++){
			retArr[i] = "0";
		}
		return retArr;
	}

	public String[] getBlankArray(){
		String []retArr = new String[dimensions+1];
		for(int i=0;i<dimensions+1;i++){
			retArr[i] = "?";
		}
		return retArr;
	}

}
