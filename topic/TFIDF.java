package topic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

//Doing TF-IDF mapping on Poetry Foundation 12831 poems dataset
public class TFIDF {
	
	public static String basePath = System.getProperty("user.dir") + "/resources/";
	public static int batches  = 10;
	
	//List of all documents for TF calculation
	ArrayList<Doc> docList = new ArrayList<Doc>();
	
	//Map to store Term to Document reference for IDF calculation
	HashMap<String, Set<String>> termToDocMap = new HashMap<String, Set<String>>();
	
	public static void main(String args[]){
		TFIDF obj = new TFIDF();
		obj.readInputFolderPoetryFoundation();
		obj.calculateTFIDF();
	}
	
	public void calculateTFIDF(){
		int totalDocs = docList.size();
		for(int i=0;i<docList.size();i++){
			Doc doc = docList.get(i);
			System.out.println("Document : " + doc.docName);
			HashMap<String, Term> terms = doc.terms;
			Iterator it = terms.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry el = (Entry) it.next();
				String word = (String) el.getKey();
				Term currentTerm = (Term) el.getValue();
				int count = currentTerm.count;
				int docPresence = termToDocMap.get(word).size() + 1;
				double tf = Math.sqrt(count);
				double idf = Math.log((double)totalDocs/docPresence);
				double tfidf = tf * idf; 
				System.out.println(word + ":" + tfidf);
			}
		}
	}
	
	public void readInputFolderPoetryFoundation(){
		String inputFolder = basePath + "poetry_foundation_final_subset/";
		File inpFolder = new File(inputFolder);
		File[] children = inpFolder.listFiles();
		int batchSize = children.length / batches;
		int count = -1;
		
		for(int loop=0; loop<batches; loop++){

			for(int i=0;i<batchSize;i++){
				count++;
				File currentFile = children[count];
				if(currentFile.isFile() && currentFile.getName().contains(".txt")){
					String fileName = currentFile.getName();
					System.out.println("Processing file " + (count+1) + " : " + fileName);
					String poemContent = "";
					
					//Single Document with details
					Doc doc = new Doc();
					doc.docName = fileName;
					//All terms of a single document
					HashMap<String, Term> termMap = new HashMap<String, Term>();
					
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
						
						//System.out.println(poemContent);
						ArrayList<String> strArr = getArrayListFromStr(poemContent); 
						
						for(int m=0;m<strArr.size();m++){
							String word = strArr.get(m);
							Term term;
							if(termMap.containsKey(word)){
								term = termMap.get(word);
								term.count++;
							}else{
								term = new Term();
								term.termName = word;
								term.count = 1;
								termMap.put(word, term);
							}
							
							//Check for Term to Document map for IDF calculation
							if(termToDocMap.containsKey(word)){
								termToDocMap.get(word).add(fileName);
							}else{
								Set<String> tmpSet = new HashSet<String>();
								tmpSet.add(fileName);
								termToDocMap.put(word, tmpSet);
							}
						}
						
						//System.out.println(termMap);
						doc.terms = termMap;
						docList.add(doc);
						
						//System.out.println();
						
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
					//poemList.add(poem);
				}
			}
			//System.out.println(poemList.size());

			//printAllPoems();

			//writeToFile("output/poem_output" + (loop+1) + ".txt");
		}
		children = null;
		inpFolder = null;	
	}
	
	
	public String processLine(String currentLine){
		currentLine = currentLine.trim();
		currentLine = currentLine.toLowerCase();
		currentLine = handlePunctuation(currentLine);
		String intermediateStr = cleanPrefix(currentLine);
		String outLine = cleanSuffix(intermediateStr);
		return outLine;
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
	
	public ArrayList<String> getArrayListFromStr(String sCurrentLine){
		ArrayList<String> outList = new ArrayList<String>(); 
		String[] arr = sCurrentLine.split(" ");
		for(int i=0;i<arr.length;i++){
			if(arr[i].trim().equals("")){
				continue;
			}
			outList.add(arr[i]);
		}
		return outList;
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
		retStr = retStr.replaceAll("“", " ");
		retStr = retStr.replaceAll("’", " ");
		retStr = retStr.replaceAll("-", " ");
		retStr = retStr.replaceAll("  ", " ");
		return retStr;
	}
	
}

 class Term {
	 
	 public String termID = "";
	 
	 public String termName = "";
	 
	 public Integer count = 0;
	 
	 @Override
	 public String toString(){
		 return termName + ":" + count;
	 }
}

 
class Doc {
	 
	 public String docID = "";
	 
	 public String docName = "";
	 
	 HashMap<String, Term> terms = new HashMap<String, Term>();
	 
	 @Override
	 public String toString(){
		 return docName + ":" + terms.toString();
	 }
	 
}
