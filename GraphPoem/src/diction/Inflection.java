package diction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import concordance.WordNet;

public class Inflection {
	
	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";


	public static void main(String[] args) {
		Inflection obj = new Inflection();
		obj.readInputFolderPoetryFoundationNew();
		//obj.writeToFile("posOutput.txt");
	}

	public void readInputFolderPoetryFoundation(){
		String inputFolder = basePath + "lemma/";
		File inpFolder = new File(inputFolder);
		File[] children = inpFolder.listFiles();
		
		WordNet obj = new WordNet();
		File outFile = new File(basePath + "inflection.txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			
			fw = new FileWriter(outFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		}catch(Exception e){
			e.printStackTrace();
		}

		
		int count = -1;
		StringBuffer sb = new StringBuffer();
		for(int loop=0; loop<children.length; loop++){
			count++;
			
			ArrayList<Word> wordList = new ArrayList<Word>();
			ArrayList inflectionList = new ArrayList();
			ArrayList noninflectionList = new ArrayList();
			
			File currentFile = children[count];
			if(currentFile.isFile() && currentFile.getName().contains(".lemma")){
				String fileName = currentFile.getName();
				fileName = fileName.substring(0, fileName.length()-6);
				File currentPOSFile = new File(basePath + "pos/" + fileName + ".pos");
				System.out.println("\nProcessing file " + (count+1) + " : " + fileName);

				BufferedReader br = null;
				BufferedReader brPOS = null;
				String sCurrentLine;
				String sCurrentLinePOS;
				try{
					bw.write("\n\nFile " + (count+1) + " : " + fileName);
					br = new BufferedReader(new FileReader(currentFile));
					brPOS = new BufferedReader(new FileReader(currentPOSFile));
					int lineNumber = 0;
					while ((sCurrentLine = br.readLine()) != null) {
						lineNumber++;
						sCurrentLinePOS = brPOS.readLine();
						
						if(sCurrentLine.trim().equals("")){
							continue;
						}
						if(sCurrentLine.trim().split(" ").length == 1){
							continue;
						}

						String processedLine = processLine(sCurrentLine);
						String processedPOSLine = processPOSLine(sCurrentLinePOS);
						
						//getWordsWithPOS(processedLine, processedPOSLine, wordList);
					}
					
					
					checkInflection(obj, wordList, inflectionList, noninflectionList);
					
					/*System.out.println("Nouns : " + nounList.toString());
					System.out.println(abstractList.size() + " Abstract words : " + abstractList);
					System.out.println(concreteList.size() + " Concrete words: " + concreteList);
					System.out.println("Abstract/Concrete ratio: " + abstractList.size()*1.0/concreteList.size());*/
					
					//bw.write("\nWords : " + nounList.toString());
					bw.write("\n" + inflectionList.size() + " Inflectional words: " + inflectionList);
					bw.write("\n" + noninflectionList.size() + " Non Inflectional words : " + noninflectionList);
					
					bw.write("\nInflection ratio: " + inflectionList.size()*1.0/noninflectionList.size());
					bw.flush();
					
					
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
				
			}

			//writeToFile("poem_output" + (loop+1) + ".txt");
		}
		
		try{
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		children = null;
		inpFolder = null;

	}
	
	public void readInputFolderPoetryFoundationNew(){
		String inputFolder = basePath + "collated_pos/";
		File inpFolder = new File(inputFolder);
		File[] children = inpFolder.listFiles();
		
		WordNet obj = new WordNet();
		File outFile = new File(basePath + "inflection.txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			
			fw = new FileWriter(outFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		}catch(Exception e){
			e.printStackTrace();
		}

		
		int count = -1;
		StringBuffer sb = new StringBuffer();
		for(int loop=0; loop<children.length; loop++){
			count++;
			
			ArrayList<Word> wordList = new ArrayList<Word>();
			ArrayList inflectionList = new ArrayList();
			ArrayList noninflectionList = new ArrayList();
			
			File currentFile = children[count];
			if(currentFile.isFile() && currentFile.getName().contains(".pos")){
				String fileName = currentFile.getName();
				fileName = fileName.substring(0, fileName.length()-4);
				//File currentPOSFile = new File(basePath + "pos/" + fileName + ".pos");
				System.out.println("\nProcessing file " + (count+1) + " : " + fileName);

				BufferedReader br = null;
				//BufferedReader brPOS = null;
				String sCurrentLine;
				//String sCurrentLinePOS;
				try{
					bw.write("\n\nFile " + (count+1) + " : " + fileName);
					br = new BufferedReader(new FileReader(currentFile));
					//brPOS = new BufferedReader(new FileReader(currentPOSFile));
					int lineNumber = 0;
					while ((sCurrentLine = br.readLine()) != null) {
						lineNumber++;
						//sCurrentLinePOS = brPOS.readLine();
						
						if(sCurrentLine.trim().equals("")){
							continue;
						}
						if(sCurrentLine.trim().split(" ").length == 1){
							continue;
						}

						String processedLine = processLine(sCurrentLine);
						//String processedPOSLine = processPOSLine(sCurrentLinePOS);
						
						getWordsWithPOS(processedLine, wordList);
					}
					
					checkInflection(obj, wordList, inflectionList, noninflectionList);
					
					/*System.out.println("Nouns : " + nounList.toString());
					System.out.println(abstractList.size() + " Abstract words : " + abstractList);
					System.out.println(concreteList.size() + " Concrete words: " + concreteList);
					System.out.println("Abstract/Concrete ratio: " + abstractList.size()*1.0/concreteList.size());*/
					
					//bw.write("\nWords : " + nounList.toString());
					bw.write("\n" + inflectionList.size() + " Inflectional words: " + inflectionList);
					bw.write("\n" + noninflectionList.size() + " Non Inflectional words : " + noninflectionList);
					
					bw.write("\nInflection ratio: " + inflectionList.size()*1.0/noninflectionList.size());
					bw.flush();
					
					
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
				
			}

			//writeToFile("poem_output" + (loop+1) + ".txt");
		}
		
		try{
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		children = null;
		inpFolder = null;

	}
	
	public void checkInflection(WordNet obj, ArrayList<Word> wordList, ArrayList inflectionList, ArrayList noninflectionList){
		for(int i=0;i<wordList.size();i++){
			Word word = wordList.get(i);
			try{
				String wordClass = obj.getInflectionOrNot(word.getWordText(), word.getPostag());
				//System.out.println(word + " : " + wordClass);
				if(wordClass.equals("inflectional")){
					inflectionList.add(word.getWordText());
				}
				if(wordClass.equals("noninflectional")){
					noninflectionList.add(word.getWordText());
				}
			}catch(Exception e){
				//System.out.println("Error : " + e.getMessage());
			}
			//System.out.println();
		}
	}
	
	public void getWordsWithPOS(String currentLine, ArrayList<Word> wordList){
		String []lineArr = currentLine.split(" +");
		for(int i=0;i<lineArr.length;i++){
			//System.out.println(lineArr[i] + " : " + lineArrPOS[i]);
			//System.out.println(currentLine);
			//System.out.println(i);
			//System.out.println("Word::" + lineArr[i]);
			if(lineArr[i].trim().equals("")){
				continue;
			}
			int indexOfSlash = lineArr[i].indexOf("/");
			String word = lineArr[i].substring(0,indexOfSlash);
			String tag = lineArr[i].substring(indexOfSlash+1, lineArr[i].length());
			if(tag.equalsIgnoreCase("noun") || tag.equalsIgnoreCase("verb") || tag.equalsIgnoreCase("adj")){
				Word objWord = new Word();
				objWord.setWordText(word.replaceAll("-", ""));
				objWord.setPostag(tag);
				wordList.add(objWord);
			}
		}
	}
	
	
	public String processLine(String currentLine){
		currentLine = currentLine.trim();
		currentLine = currentLine.toLowerCase();
		//currentLine = currentLine.replaceAll("   ", " ");
		//currentLine = currentLine.replaceAll("  ", " ");
		return currentLine;
	}
	
	public String processPOSLine(String currentLine){
		String ret = currentLine;
		//ret = ret.replaceAll("SPACE", "");
		//ret = ret.replaceAll("PUNCT ", "");
		//ret = ret.replaceAll("SYM ", "");
		//ret = ret.replaceAll("  ", " ");
		//ret = ret.trim();
		return ret;
	}



}
