package brown;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class BrownCSVGenerator {
	public static String basePath = System.getProperty("user.dir") + "/resources/";
	
	public static String basePath2 = System.getProperty("user.dir") + "/resources/brown/";
	
	HashMap<String, String> stopWordsMap = new HashMap<String, String>();
	
	ArrayList<String> poemList = new ArrayList<String>(); 

	public static void main(String args[]){
		BrownCSVGenerator obj = new BrownCSVGenerator();
		obj.loadStopWords();
		obj.readInputFolder();
		obj.writeToFile("PoemList.txt", obj.poemList);
	}

	public void readInputFolder(){
		String inputFolder = basePath2 + "browndata/";
		BufferedWriter bw = null;		
		FileWriter fw = null;
		File file = new File(basePath2 + "abc.dat");
		try{
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);

			File inpFolder = new File(inputFolder);
			File[] children = inpFolder.listFiles();
			bw.write(children.length + "\n");
			
			int count = -1;
			for(int loop=0; loop<children.length; loop++){
				count++;
				File currentFile = children[count];
				if(currentFile.isFile()){
					String content = "";
					String fileName = currentFile.getName();
					System.out.println("Processing file " + (count+1) + " : " + fileName);
					poemList.add(fileName);

					BufferedReader br = null;
					String sCurrentLine = "";
					try{
						br = new BufferedReader(new FileReader(currentFile));
						while ((sCurrentLine = br.readLine()) != null) {
							if(sCurrentLine.trim().equals("")){
								continue;
							}
							String processeLine = processLine(sCurrentLine);
							content = content + " " + processeLine;
						}

						br.close();
						br = null;
						currentFile = null;
						
						//bw.write(fileName + "\t" + "X" + "\t" + poemContent + "\n");
						bw.write(content.trim() + "\n");
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

	}

	public String processLine(String currentLine){
		currentLine = currentLine.trim();
		currentLine = currentLine.toLowerCase();
		currentLine = eliminateIdentifier(currentLine);
		currentLine = handlePunctuation(currentLine);
		String intermediateStr = cleanPrefix(currentLine);
		String outLine = cleanSuffix(intermediateStr);
		outLine = excludeStopWords(outLine);
		return outLine;
	}
	
	public String eliminateIdentifier(String sCurrentLine){
		String retStr = "";
		String[] arr = sCurrentLine.split(" ");
		for(int i=0;i<arr.length;i++){
			String []splitWord = arr[i].split("/");
			retStr = retStr + splitWord[0] + " ";
		}
		if(!retStr.equals("")){
			retStr = retStr.substring(0, retStr.length()-1);
		}
		return retStr;
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
