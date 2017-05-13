package diction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import concordance.WordNet;

public class Metaphor {

	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";

	public HashMap<String, String> copulaVerbList = new HashMap<String, String>(); 
	
	int totalPOSSequenceMatch = 0;
	
	int totalCopularSequenceMatch = 0;
	
	int totalAbsConMeta = 0;
	
	int totalConClassOverlap = 0;
	
	ConceptNet objCN = null;

	public static void main(String[] args) {
		Metaphor obj = new Metaphor();
		obj.populateCopulaList();
		obj.readInputFolderPoetryFoundationNew();
		//obj.writeToFile("posOutput.txt");

	}

	public void populateCopulaList(){
		copulaVerbList.put("be", "be");
		copulaVerbList.put("is", "is");
		copulaVerbList.put("am", "am");
		copulaVerbList.put("are", "are");
		copulaVerbList.put("was", "was");
		copulaVerbList.put("were", "were");
	}

	public void readInputFolderPoetryFoundationNew(){
		objCN = new ConceptNet();
		String inputFolder = basePath + "opennlp_pos_subset/";
		//String inputFolder = basePath + "opennlp_pos/";
		File inpFolder = new File(inputFolder);
		File[] children = inpFolder.listFiles();

		WordNet obj = new WordNet();
		File outFile = new File(basePath + "type2_metaphor.txt");
		File outFile2 = new File(basePath + "type2_metaphor_anno.txt");
		FileWriter fw = null;
		FileWriter fw2 = null;
		BufferedWriter bw = null;
		BufferedWriter bw2 = null;
		try{
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			if (!outFile2.exists()) {
				outFile2.createNewFile();
			}

			fw = new FileWriter(outFile.getAbsoluteFile());
			fw2 = new FileWriter(outFile2.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw2 = new BufferedWriter(fw2);
		}catch(Exception e){
			e.printStackTrace();
		}


		int count = -1;
		StringBuffer sb = new StringBuffer();
		for(int loop=0; loop<children.length; loop++){
			count++;
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
						ArrayList<Word> wordList = new ArrayList<Word>();
						lineNumber++;
						//sCurrentLinePOS = brPOS.readLine();

						if(sCurrentLine.trim().equals("")){
							continue;
						}
						if(sCurrentLine.trim().split(" ").length == 1){
							continue;
						}

						String processedLine = processLine(sCurrentLine);
						String currentLineWithoutPOS = removePOSTags(sCurrentLine);
						//String processedPOSLine = processPOSLine(sCurrentLinePOS);

						String posSequence = getWordsWithPOSSequence(processedLine, wordList);
						if(posSequence.contains("noun_verb_noun")){
							//System.out.println(posSequence);
							ArrayList<Integer> indexList = convertPOSSequenceToIndex(posSequence, "noun_verb_noun");
							for(int currIndex : indexList){
								totalPOSSequenceMatch++;
								System.out.println(currentLineWithoutPOS);
								bw.write("\nLine : " + sCurrentLine);
								//bw2.write("\n" + currentLineWithoutPOS + "@" + (currIndex+1) + "@");
								//bw.write(checkMetaphorType1(bw2, currentLineWithoutPOS, obj, wordList, currIndex, "noun_verb_noun").toString());
								bw.write(checkMetaphorType2(bw2, currentLineWithoutPOS, obj, wordList, currIndex, "noun_verb_noun").toString());
							}
						}
						if(posSequence.contains("noun_verb_det_noun")){
							//System.out.println(posSequence);
							ArrayList<Integer> indexList = convertPOSSequenceToIndex(posSequence, "noun_verb_det_noun");
							for(int currIndex : indexList){
								totalPOSSequenceMatch++;
								System.out.println(currentLineWithoutPOS);
								bw.write("\nLine : " + sCurrentLine);
								//bw2.write("\n" + currentLineWithoutPOS + "@" + (currIndex+1) + "@");
								//bw.write(checkMetaphorType1(bw2, currentLineWithoutPOS, obj, wordList, currIndex, "noun_verb_det_noun").toString());
								bw.write(checkMetaphorType2(bw2, currentLineWithoutPOS, obj, wordList, currIndex, "noun_verb_det_noun").toString());
							}
						}
						bw.flush();
						bw2.flush();
					}

					//checkInflection(obj, wordList, inflectionList, noninflectionList);

					br.close();
					br = null;
					currentFile = null;
				} catch (Exception e) {
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
			bw.write("\n\nTotal POS Sequence Matches : " + totalPOSSequenceMatch);
			bw.write("\n\nTotal Sequence Matches : " + totalCopularSequenceMatch);
			bw.write("\n\nTotal Abstract-Concrete Metaphors : " + totalAbsConMeta);
			bw.write("\n\nTotal CCO Metaphors : " + totalConClassOverlap);
			bw.flush();
			bw2.flush();
			
			bw.close();
			bw2.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		children = null;
		inpFolder = null;

	}

	public StringBuffer checkMetaphorType1(BufferedWriter bw2, String origStr, WordNet obj, ArrayList<Word> wordList, int index, String posSequence) throws Exception {
		StringBuffer sb = new StringBuffer();
		String noun1 = "";
		String noun2 = "";
		String verb = "";
		int countUnderScore = posSequence.split("_").length;
		for(int i=index;i<index+countUnderScore;i++){
			Word word = wordList.get(i);
			if(word.getPostag().equalsIgnoreCase("noun")){
				if(noun1.equals("")){
					noun1 = word.getWordText();
				}else{
					noun2 = word.getWordText();
				}
			}
			if(word.getPostag().equalsIgnoreCase("verb")){
				//System.out.println(word.getWordText() + " : " + word.getPostag());
				if(copulaVerbList.containsKey(word.getWordText())){
					verb = word.getWordText();
					totalCopularSequenceMatch++;
					bw2.write("\n" + origStr + "@" + (index+1) + "@");
				}else{
					System.out.println("Copular verb absent:" + word.getWordText());
					//sb.append("\nCopular verb absent.");
					return sb;
				}
			}
		}
		System.out.println("Processing Type1: " + noun1 + " : " + verb + " : " + noun2);


		String classOfNoun1 = obj.getConcreteAbstract(noun1);
		String classOfNoun2 = obj.getConcreteAbstract(noun2);

		System.out.println(noun1 + ":" + classOfNoun1 + "    " + noun2 + ":" + classOfNoun2);


		if(classOfNoun1.equals("abstract") && classOfNoun2.equals("concrete")){
			//sb.append("\n\nLine : " + origStr);
			sb.append("\nProcessing Type1: " + noun1 + " : " + verb + " : " + noun2);
			sb.append("\n" + noun1 + ":" + classOfNoun1 + "    " + noun2 + ":" + classOfNoun2);
			System.out.println("Metaphor by Abstract-Concrete relation");
			sb.append("\nMetaphor by Abstract-Concrete relation");
			totalAbsConMeta++;
			return sb;
		}

		//if(classOfNoun1.equals("abstract") && classOfNoun2.equals("abstract")){
		//}

		if(classOfNoun1.equals("concrete") && classOfNoun2.equals("concrete")){
			boolean overlap = false;
			LinkedHashMap<String, String> hypernymList1 = obj.getHypernyms(noun1, "noun");
			LinkedHashMap<String, String> hypernymList2 = obj.getHypernyms(noun2, "noun");
			Iterator it = hypernymList1.keySet().iterator();
			while(it.hasNext()){
				String hypernym = (String) it.next();
				//System.out.println(hypernym);
				if(hypernymList2.containsKey(hypernym)){
					overlap = true;
				}
			}
			System.out.println("Class Overlap:" + overlap);

			if(!overlap){
				System.out.println("Metaphor by CCO relation");
				//sb.append("\n\nLine : " + origStr);
				sb.append("\nProcessing Type1: " + noun1 + " : " + verb + " : " + noun2);
				sb.append("\n" + noun1 + ":" + classOfNoun1 + "    " + noun2 + ":" + classOfNoun2);
				sb.append("\nHypernym Class Overlap:" + overlap);
				sb.append("\nMetaphor by CCO relation");
				totalConClassOverlap++;
			}
		}
		return sb;
	}
	
	public StringBuffer checkMetaphorType2(BufferedWriter bw2, String origStr, WordNet obj, ArrayList<Word> wordList, int index, String posSequence) throws Exception {
		StringBuffer sb = new StringBuffer();
		String noun1 = "";
		String noun2 = "";
		String verb = "";
		int countUnderScore = posSequence.split("_").length;
		for(int i=index;i<index+countUnderScore;i++){
			Word word = wordList.get(i);
			if(word.getPostag().equalsIgnoreCase("noun")){
				if(noun1.equals("")){
					noun1 = word.getWordText();
				}else{
					noun2 = word.getWordText();
				}
			}
			if(word.getPostag().equalsIgnoreCase("verb")){
				//System.out.println(word.getWordText() + " : " + word.getPostag());
				if(copulaVerbList.containsKey(word.getWordText())){
					//System.out.println("Copular verb present:" + word.getWordText());
					//sb.append("\nCopular verb absent.");
					return sb;
				}else{
					verb = word.getWordText();
					totalCopularSequenceMatch++;
					bw2.write("\n" + origStr + "@" + (index+1) + "@");
				}
			}
		}
		System.out.println("Processing Type2: " + noun1 + " : " + verb + " : " + noun2);

		/*noun1 = "sweet";
		verb = "dreams";
		noun2 = "gasoline";*/
		
		String classOfNoun1 = obj.getConcreteAbstract(noun1);
		String classOfNoun2 = obj.getConcreteAbstract(noun2);

		System.out.println(noun1 + ":" + classOfNoun1 + "    " + noun2 + ":" + classOfNoun2);

		String concepts1 = objCN.getProp(noun1);
		String concepts2 = objCN.getProp(noun2);
		String verbConcepts = objCN.getProp(verb);
		
		boolean conceptOverlap1 = checkConceptOverlap(concepts1, verbConcepts);
		System.out.println("Concept Overlap : " + conceptOverlap1);
		
		boolean conceptOverlap2 = checkConceptOverlap(concepts2, verbConcepts);
		System.out.println("Concept Overlap : " + conceptOverlap2);
		
		boolean conceptOverlap3 = checkConceptOverlap(concepts1, concepts2);
		System.out.println("Concept Overlap : " + conceptOverlap3);

		if(classOfNoun1.equals("abstract") && classOfNoun2.equals("concrete")){
			//sb.append("\n\nLine : " + origStr);
			sb.append("\nProcessing Type2: " + noun1 + " : " + verb + " : " + noun2);
			sb.append("\n" + noun1 + ":" + classOfNoun1 + "    " + noun2 + ":" + classOfNoun2);
			System.out.println("Metaphor by Abstract-Concrete relation");
			sb.append("\nMetaphor by Abstract-Concrete relation");
			totalAbsConMeta++;
			return sb;
		}

		//if(classOfNoun1.equals("abstract") && classOfNoun2.equals("abstract")){
		//}

		if(classOfNoun1.equals("concrete") && classOfNoun2.equals("concrete")){
			boolean overlap = false;
			LinkedHashMap<String, String> hypernymList1 = obj.getHypernyms(noun1, "noun");
			LinkedHashMap<String, String> hypernymList2 = obj.getHypernyms(noun2, "noun");
			Iterator it = hypernymList1.keySet().iterator();
			while(it.hasNext()){
				String hypernym = (String) it.next();
				//System.out.println(hypernym);
				if(hypernymList2.containsKey(hypernym)){
					overlap = true;
				}
			}
			System.out.println("Class Overlap:" + overlap);

			if(!overlap){
				System.out.println("Metaphor by CCO relation");
				//sb.append("\n\nLine : " + origStr);
				sb.append("\nProcessing Type2: " + noun1 + " : " + verb + " : " + noun2);
				sb.append("\n" + noun1 + ":" + classOfNoun1 + "    " + noun2 + ":" + classOfNoun2);
				sb.append("\nHypernym Class Overlap:" + overlap);
				sb.append("\nMetaphor by CCO relation");
				totalConClassOverlap++;
			}
		}
		return sb;
	}
	
	public boolean checkConceptOverlap(String str1, String str2){
		boolean overlap = false;
		HashMap<String, String> map = new HashMap<String, String>(); 
		String[] strArr1 = str1.split(",");
		String[] strArr2 = str2.split(",");
		for(String word : strArr1){
			map.put(word, "");
		}
		String overlapList = "";
		for(String word : strArr2){
			if(map.containsKey(word)){
				overlap = true;
				overlapList = overlapList + word + ", ";
			}
		}
		if(overlap){
			System.out.println("Overlap List= " + overlapList);
		}
		return overlap;
	}

	public ArrayList<Integer> convertPOSSequenceToIndex(String posSequence, String toCheck){
		ArrayList<Integer> retIndexList = new ArrayList<Integer>(); 
		StringBuffer modStr = new StringBuffer(posSequence);
		//modStr = new StringBuffer("noun_verb_noun_verb_noun_cc_noun_adv_.");
		//toCheck = "noun_verb_noun";
		int index = modStr.indexOf(toCheck);
		while(index != -1){			
			if(index == 0){
				retIndexList.add(0);
			}else{
				String subStr = modStr.substring(0, index);
				String []strArr = subStr.split("_"); 
				retIndexList.add(strArr.length);
			}
			modStr.setCharAt(index, 'x');
			index = modStr.indexOf(toCheck);
		}
		return retIndexList;
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


	public String processLine(String currentLine){
		currentLine = currentLine.trim();
		currentLine = currentLine.toLowerCase();
		//currentLine = currentLine.replaceAll("   ", " ");
		//currentLine = currentLine.replaceAll("  ", " ");
		return currentLine;
	}

	public String removePOSTags(String currentLine){
		StringBuffer sb = new StringBuffer();
		String []lineArr = currentLine.split(" +");
		for(int i=0;i<lineArr.length;i++){
			if(lineArr[i].trim().equals("")){
				continue;
			}
			int indexOfSlash = lineArr[i].indexOf("/");
			String word = lineArr[i].substring(0,indexOfSlash);
			sb.append(word + " ");
		}

		return sb.toString();
	}

}
