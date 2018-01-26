package diction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;


public class BuildPoetryEmbed {
	public static String basePath = System.getProperty("user.dir") + "/resources/";

	public static void main(String args[]){
		BuildPoetryEmbed obj = new BuildPoetryEmbed();
		obj.readInputFolder();

	}

	public void readInputFolder(){
		String inputFolder = basePath + "poetry_foundation_final/";
		BufferedWriter bw = null;		
		FileWriter fw = null;
		File file = new File(basePath + "pofo_all.txt");
		try{
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);

			File inpFolder = new File(inputFolder);
			File[] children = inpFolder.listFiles();
			int count = -1;
			for(int loop=0; loop<children.length; loop++){
				count++;
				File currentFile = children[count];
				if(currentFile.isFile() && currentFile.getName().contains(".txt")){
					String poemContent = "";
					String fileName = currentFile.getName();
					System.out.println("Processing file " + (count+1) + " : " + fileName);

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
							poemContent = poemContent + "" + processeLine;
						}

						br.close();
						br = null;
						currentFile = null;
						
						//bw.write(fileName + "\t" + "X" + "\t" + poemContent + "\n");
						bw.write(poemContent + "\n");
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
		StringBuffer sb = new StringBuffer();
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(currentLine));
		for (List<HasWord> sentence : tokenizer) {
			for(HasWord elem : sentence){
				sb.append(elem.word() + " ");
				/*if(elem.word().equals(anchorWord)){
					return true;
				}*/
			}			
		}
		//System.out.println(sb);
		/*currentLine = currentLine.trim();
		String intermediateStr = cleanPrefix(currentLine);
		String outLine = cleanSuffix(intermediateStr);
		outLine = outLine.replaceAll(",", "")*/;
		return sb.toString().toLowerCase();
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

}
