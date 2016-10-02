package gibbsimpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class GibbsCSVGenerator {
	
	public static final int MINCOUNT = 1;
	
	public static String basePath = System.getProperty("user.dir") + "/resources/";
	
	public static String basePath2 = System.getProperty("user.dir") + "/resources/gibbs/";
	
	HashMap<String, String> stopWordsMap = new HashMap<String, String>();
	
	ArrayList<String> poemList = new ArrayList<String>();
	
	HashMap<String, Integer> wordFreqMap = new HashMap<String, Integer>(); 

	public static void main(String args[]){
		GibbsCSVGenerator obj = new GibbsCSVGenerator();
		obj.loadStopWords();
		obj.readInputFolder();
		obj.writeProcessedCSV();
		//System.out.println(obj.wordFreqMap.get("gandy"));
		obj.writeToFile("PoemList.txt", obj.poemList);

	}

	public void readInputFolder(){
		String inputFolder = basePath + "poetry_foundation_final/";
		BufferedWriter bw = null;		
		FileWriter fw = null;
		File file = new File(basePath + "poemContent.txt");
		HashMap<String, String> checkDuplMap = new HashMap<String, String>();
		try{
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);

			File inpFolder = new File(inputFolder);
			File[] children = inpFolder.listFiles();
			//bw.write(children.length + "\n");
			
			int count = -1;
			for(int loop=0; loop<children.length; loop++){
				count++;
				File currentFile = children[count];
				if(currentFile.isFile() && currentFile.getName().contains(".txt")){
					String poemContent = "";
					String fileName = currentFile.getName();
					System.out.println("Processing file " + (count+1) + " : " + fileName);
					poemList.add(fileName);

					BufferedReader br = null;
					String sCurrentLine;
					String poemName = "";
					String poemAuthor = "";
					try{
						br = new BufferedReader(new FileReader(currentFile));
						int lineNumber = 0;
						while ((sCurrentLine = br.readLine()) != null) {
							lineNumber++;
							if(lineNumber == 1){
								poemName = sCurrentLine;
							}
							if(lineNumber == 2){
								poemAuthor = sCurrentLine;
							}
							if(lineNumber <= 9){
								continue;
							}
							if(sCurrentLine.trim().equals("")){
								continue;
							}
							if(sCurrentLine.split(" ").length == 1){
								continue;
							}

							String processeLine = processLine(sCurrentLine);
							poemContent = poemContent + " " + processeLine;
						}

						br.close();
						br = null;
						currentFile = null;
						
						//bw.write(fileName + "\t" + "X" + "\t" + poemContent + "\n");
						
						if(!poemContent.trim().equalsIgnoreCase("")){
							if(!checkDuplMap.containsKey(poemContent.trim())){
								checkDuplMap.put(poemContent.trim(), "X");
								bw.write(fileName + "," + poemContent.trim() + "\n");
								addToWordFreqMap(poemContent.trim());
							}
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (br != null) {
								br.close();
							}
							currentFile = null;
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}

			}
			children = null;
			inpFolder = null;
			
			bw.close();
			fw.close();
			fw = null;
			bw = null;
			file = null;

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
				file = null;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("poemContent.txt file written");

	}
	
	public void addToWordFreqMap(String poemContent){
		String []arr = poemContent.split(" ");
		for(int i=0;i<arr.length;i++){
			if(wordFreqMap.containsKey(arr[i])){
				int count = wordFreqMap.get(arr[i]);
				count++;
				wordFreqMap.remove(arr[i]);
				wordFreqMap.put(arr[i], count);
			}else{
				wordFreqMap.put(arr[i], 1);
			}
		}
	}
	
	public void writeProcessedCSV(){
		BufferedWriter bw = null;		
		FileWriter fw = null;
		File file = new File(basePath + "poemContentProcess.txt");
		try{
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);

			//bw.write(children.length + "\n");

			File currentFile = new File(basePath + "poemContent.txt");

			BufferedReader br = null;
			String sCurrentLine;
			String poemName = "";
			try{
				br = new BufferedReader(new FileReader(currentFile));
				int lineNumber = 0;
				while ((sCurrentLine = br.readLine()) != null) {
					lineNumber++;
					String finalStr = "";
					String []firstSp = sCurrentLine.split(",");
					poemName = firstSp[0];
					String origLine = firstSp[1];
					String []secondSp = origLine.split(" ");
					finalStr = poemName + ",";
					for(int iter=0;iter<secondSp.length;iter++){
						int freq = wordFreqMap.get(secondSp[iter]);
						if(freq >= MINCOUNT){
							finalStr = finalStr + secondSp[iter] + " ";
						}
					}
					finalStr = finalStr.substring(0, finalStr.length()-1);
					bw.write(finalStr + "\n");
				}

				br.close();
				br = null;
				currentFile = null;

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null) {
						br.close();
					}
					currentFile = null;
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

			bw.close();
			fw.close();
			fw = null;
			bw = null;
			file = null;

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
				file = null;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("poemContentProcess.txt file written");

	}

	public String processLine(String currentLine){
		currentLine = currentLine.trim();
		currentLine = currentLine.toLowerCase();
		currentLine = handlePunctuation(currentLine);
		String intermediateStr = cleanPrefix(currentLine);
		String outLine = cleanSuffix(intermediateStr);
		outLine = excludeStopWords(outLine);
		return outLine;
	}
	
	public String excludeStopWords(String sCurrentLine){
		String retStr = "";
		String[] arr = sCurrentLine.split(" ");
		for(int i=0;i<arr.length;i++){
			if(arr[i].trim().equals("")){
				continue;
			}
			if(stopWordsMap.containsKey(arr[i].trim())){
				continue;
			}
			retStr = retStr + arr[i] + " ";
		}
		if(!retStr.equals("")){
			retStr = retStr.substring(0, retStr.length()-1);
		}
		return retStr;
	}

	public String cleanPrefix(String currentWord){
		String finalStr = currentWord;
		int j=0;
		for(j=0;j<currentWord.length();j++){
			int asciiValue = currentWord.charAt(j);
			if(((asciiValue>=48 && asciiValue<=57) || (asciiValue>=65 && asciiValue<=90) || (asciiValue>=97 && asciiValue<=122))){
				break;
			}else{
				//System.out.println();
			}
		}
		finalStr = currentWord.substring(j, currentWord.length());
		return finalStr;
	}

	public static String cleanSuffix(String currentWord){
		String finalStr = currentWord;
		int j=0;
		for(j=currentWord.length()-1;j>=0;j--){
			int asciiValue = currentWord.charAt(j);
			if(((asciiValue>=48 && asciiValue<=57) || (asciiValue>=65 && asciiValue<=90) || (asciiValue>=97 && asciiValue<=122))){
				break;
			}else{
				//System.out.println();
			}
		}
		finalStr = currentWord.substring(0, j+1);
		return finalStr;
	}
	
	public String handlePunctuation(String inpStr){
		//inpStr = "!!~~``%%^^&&**(())__--++==||\\[[]]{{}};;::\\\\''\"\",,..//<<>>";
		String retStr = inpStr;
		retStr = retStr.replaceAll("!", " ");
		retStr = retStr.replaceAll("~", " ");
		retStr = retStr.replaceAll("`", " ");
		retStr = retStr.replaceAll("\\$", " ");
		retStr = retStr.replaceAll("%", " ");
		retStr = retStr.replaceAll("\\^", " ");
		retStr = retStr.replaceAll("&", " ");
		retStr = retStr.replaceAll("\\*", " ");
		retStr = retStr.replaceAll("\\(", " ");
		retStr = retStr.replaceAll("\\)", " ");
		retStr = retStr.replaceAll("__", " ");
		retStr = retStr.replaceAll("--", " ");
		retStr = retStr.replaceAll("\\+", " ");
		retStr = retStr.replaceAll("=", " ");
		retStr = retStr.replaceAll("\\|", " ");
		retStr = retStr.replaceAll("\\\\", " ");
		retStr = retStr.replaceAll("\\[", " ");
		retStr = retStr.replaceAll("]", " ");
		retStr = retStr.replaceAll("\\{", " ");
		retStr = retStr.replaceAll("}", " ");
		retStr = retStr.replaceAll(";", " ");
		retStr = retStr.replaceAll(":", " ");
		retStr = retStr.replaceAll("\"", " ");
		retStr = retStr.replaceAll("'", " ");
		retStr = retStr.replaceAll(",", " ");
		retStr = retStr.replaceAll("\\.", " ");
		retStr = retStr.replaceAll("//", " ");
		retStr = retStr.replaceAll("<", " ");
		retStr = retStr.replaceAll(">", " ");
		retStr = retStr.replaceAll("\\?", " ");
		retStr = retStr.replaceAll("â€œ", " ");
		retStr = retStr.replaceAll("â€™", " ");
		retStr = retStr.replaceAll("â", " ");
		retStr = retStr.replaceAll("€", " ");
		retStr = retStr.replaceAll("-", " ");
		retStr = retStr.replaceAll("  ", " ");
		return retStr;
	}
	
	public void loadStopWords(){
		BufferedReader br = null;
		String sCurrentLine = "";
		try{
			br = new BufferedReader(new FileReader(basePath + "stoplists_en.txt"));
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				stopWordsMap.put(sCurrentLine, sCurrentLine);
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("Total StopWords loaded:" + stopWordsMap.size());
	}
	
	public void writeToFile(String str, ArrayList<String> arr){
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File file = new File(basePath2 + str);

			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			for(int i=0;i<arr.size();i++){
				bw.write(arr.get(i) + "\n");
				bw.flush();
			}			
			bw.close();

			System.out.println(str + " file written");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

}
