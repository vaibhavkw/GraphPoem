package diction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import concordance.WordNet;

public class AbstractConcrete {
	
	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";


	public static void main(String[] args) {
		AbstractConcrete obj = new AbstractConcrete();
		obj.readInputFolderPoetryFoundation();
		//obj.writeToFile("posOutput.txt");
	}

	public void readInputFolderPoetryFoundation(){
		String inputFolder = basePath + "lemma_subset/";
		File inpFolder = new File(inputFolder);
		File[] children = inpFolder.listFiles();
		
		WordNet obj = new WordNet();
		File outFile = new File(basePath + "concrete_abstract_sub.txt");
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
			
			LinkedHashSet nounList = new LinkedHashSet();
			ArrayList concreteList = new ArrayList();
			ArrayList abstractList = new ArrayList();
			
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
						
						getNounInPoem(processedLine, processedPOSLine, nounList);
					}
					
					
					checkNounClass(obj, nounList, concreteList, abstractList);
					
					/*System.out.println("Nouns : " + nounList.toString());
					System.out.println(abstractList.size() + " Abstract words : " + abstractList);
					System.out.println(concreteList.size() + " Concrete words: " + concreteList);
					System.out.println("Abstract/Concrete ratio: " + abstractList.size()*1.0/concreteList.size());*/
					
					bw.write("\nNouns : " + nounList.toString());
					bw.write("\n" + abstractList.size() + " Abstract words : " + abstractList);
					bw.write("\n" + concreteList.size() + " Concrete words: " + concreteList);
					bw.write("\nAbstract/Concrete ratio: " + abstractList.size()*1.0/concreteList.size());
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
	
	public void checkNounClass(WordNet obj, LinkedHashSet nounList, ArrayList concreteList, ArrayList abstractList){
		Iterator it = nounList.iterator();
		while(it.hasNext()){
			String word = (String) it.next();
			try{
				String wordClass = obj.getConcreteAbstract(word);
				//System.out.println(word + " : " + wordClass);
				if(wordClass.equals("abstract")){
					abstractList.add(word);
				}
				if(wordClass.equals("concrete")){
					concreteList.add(word);
				}
			}catch(Exception e){
				//System.out.println("Error : " + e.getMessage());
			}
			//System.out.println();
		}
	}
	
	public void getNounInPoem(String currentLine, String currentLinePOS, LinkedHashSet nounList){
		String []lineArr = currentLine.split(" +");
		String []lineArrPOS = currentLinePOS.split(" +");
		for(int i=0;i<lineArr.length;i++){
			//System.out.println(lineArr[i] + " : " + lineArrPOS[i]);
			if(lineArrPOS[i].equals("NOUN")){
				nounList.add(lineArr[i].replaceAll("-", ""));
			}
		}
	}
	
	
	public String processLine(String currentLine){
		//currentLine = currentLine.trim();
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
