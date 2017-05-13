package diction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BrownCorpus {
	
	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";
	
	public static void main(String[] args) {
		BrownCorpus obj = new BrownCorpus();
		obj.readBrownFolder();		
	}

	public void readBrownFolder(){
		System.out.println("Processing...");
		BufferedReader br = null;
		File dirPath = null;
		try {
			//loadStopWords();
			dirPath = new File(basePath + "brown/");
			File[] children = dirPath.listFiles();
			String sCurrentLine;
			for(int i=0;i<children.length;i++){
				br = new BufferedReader(new FileReader(children[i]));
				System.out.println(children[i]);
				int lineNumber = 0;
				while ((sCurrentLine = br.readLine()) != null) {
					ArrayList<Word> wordList = new ArrayList<Word>();
					lineNumber++;
					if(!sCurrentLine.trim().equals("")){
						String posSequence = getWordsWithPOSSequence(sCurrentLine, wordList);
						System.out.println(posSequence);
					}
				}
				br.close();
			}
			System.out.println("Loop ended");

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
	}
	
	public static void processLine(String currentLine){
		//String lowerCaseString = currentLine.toLowerCase();
		String[] whiteSpaceSplit = currentLine.split(" ");
		for(int i=0;i<whiteSpaceSplit.length;i++){
			
		}
		//countTokens(whiteSpaceSplit);
		
	}
	
	public String getWordsWithPOSSequence(String currentLine, ArrayList<Word> wordList){
		String ret = "";
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
			tag = convertPennTag(tag);
			if(word.equals("i")){
				word = "I";
				tag = "pron";
			}
			if(!tag.equalsIgnoreCase("space")){
				Word objWord = new Word();
				objWord.setWordText(word.replaceAll("-", ""));
				objWord.setPostag(tag);
				ret = ret + tag + "_";
				wordList.add(objWord);
			}
		}
		ret = ret.substring(0, ret.length()-1);
		return ret;
	}
	
	public String convertPennTag(String tag){
		tag = tag.toUpperCase();
		if(tag.equals("NN") || tag.equals("NNS") || tag.equals("NNP") || tag.equals("NNPS")){
			return "noun";
		}
		if(tag.equals("VB") || tag.equals("VBD") || tag.equals("VBG") || tag.equals("VBN") || tag.equals("VBP") || tag.equals("VBZ")){
			return "verb";
		}
		if(tag.equals("JJ") || tag.equals("JJR") || tag.equals("JJS")){
			return "adj";
		}
		if(tag.equals("RB") || tag.equals("RBR") || tag.equals("RBS") || tag.equals("WRB")){
			return "adv";
		}
		if(tag.equals("DT") || tag.equals("PDT") || tag.equals("WDT")){
			return "det";
		}
		if(tag.equals("PRP") || tag.equals("PRP$") || tag.equals("WP") || tag.equals("WP$")){
			return "pron";
		}
		return tag.toLowerCase();
	}
	
}
