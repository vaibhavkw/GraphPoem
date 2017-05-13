package diction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MetaphorDLPrepareData {

	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";

	public static void main(String[] args) {
		MetaphorDLPrepareData obj = new MetaphorDLPrepareData();		
		//obj.prepareDL4JInput();
		//obj.prepareTensorFlowInput();
		obj.preparePoFoInput();
	}
	
	public void preparePoFoInput(){
		File currentFile = new File(basePath + "DL/tensorflow_input_pofo/pofo.txt");
		File outFilePos = new File(basePath + "DL/tensorflow_input_pofo/pos.txt");
		File outFileNeg = new File(basePath + "DL/tensorflow_input_pofo/neg.txt");
		BufferedReader br = null;
		FileWriter fwPos = null;
		BufferedWriter bwPos = null;
		FileWriter fwNeg = null;
		BufferedWriter bwNeg = null;
		String sCurrentLine;
		try{
			br = new BufferedReader(new FileReader(currentFile));
			fwPos = new FileWriter(outFilePos.getAbsoluteFile());
			fwNeg = new FileWriter(outFileNeg.getAbsoluteFile());
			bwPos = new BufferedWriter(fwPos);
			bwNeg = new BufferedWriter(fwNeg);
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				System.out.println(lineNumber);
				String[] arr = sCurrentLine.split("@");
				String sentence = arr[0];
				//sentence = sentence.substring(1, sentence.length()-1);
				String anno = arr[2];
				if(anno.equals("s")){
					continue;
				}
				if(anno.equals("y")){ //metaphor
					bwPos.write("\n" + sentence);
					bwPos.flush();
				}else{ //non-metaphor
					bwNeg.write("\n" + sentence);
					bwNeg.flush();
				}
			}
			bwPos.close();
			bwPos = null;
			bwNeg.close();
			bwNeg = null;
			fwPos = null;
			fwNeg = null;
			outFilePos = null;
			outFileNeg = null;
			
			br.close();
			br = null;
			currentFile = null;			

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void prepareTensorFlowInput(){
		File currentFile = new File(basePath + "DL/tensorflow_input_trofi/trofi.txt");
		File outFilePos = new File(basePath + "DL/tensorflow_input_trofi/pos.txt");
		File outFileNeg = new File(basePath + "DL/tensorflow_input_trofi/neg.txt");
		BufferedReader br = null;
		FileWriter fwPos = null;
		BufferedWriter bwPos = null;
		FileWriter fwNeg = null;
		BufferedWriter bwNeg = null;
		String sCurrentLine;
		try{
			br = new BufferedReader(new FileReader(currentFile));
			fwPos = new FileWriter(outFilePos.getAbsoluteFile());
			fwNeg = new FileWriter(outFileNeg.getAbsoluteFile());
			bwPos = new BufferedWriter(fwPos);
			bwNeg = new BufferedWriter(fwNeg);
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				System.out.println(lineNumber);
				String[] arr = sCurrentLine.split(",");
				String sentence = arr[0];
				sentence = sentence.substring(1, sentence.length()-1);
				String anno = arr[104];
				if(anno.equals("1")){ //metaphor
					bwPos.write("\n" + sentence);
					bwPos.flush();
				}else{ //non-metaphor
					bwNeg.write("\n" + sentence);
					bwNeg.flush();
				}
			}
			bwPos.close();
			bwPos = null;
			bwNeg.close();
			bwNeg = null;
			fwPos = null;
			fwNeg = null;
			outFilePos = null;
			outFileNeg = null;
			
			br.close();
			br = null;
			currentFile = null;			

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

	public void prepareDL4JInput(){
		
		File currentFile = new File(basePath + "pofo_680_plus_shutova_plus_trofi_glove_test.txt");
		BufferedReader br = null;
		String sCurrentLine;
		try{
			br = new BufferedReader(new FileReader(currentFile));
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				String outFile = basePath + "DL/test/";
				FileWriter fw = null;
				BufferedWriter bw = null;
				lineNumber++;
				System.out.println(lineNumber);
				String[] arr = sCurrentLine.split(",");
				String sentence = arr[0];
				sentence = sentence.substring(1, sentence.length()-1);
				String anno = arr[104];
				if(anno.equals("1")){ //metaphor
					outFile = outFile + "pos/" + String.format("%04d", lineNumber) + ".txt";
				}else{ //non-metaphor
					outFile = outFile + "neg/" + String.format("%04d", lineNumber) + ".txt";
				}
				File currentOutFile = new File(outFile);
				if (!currentOutFile.exists()) {
					currentOutFile.createNewFile();
				}
				fw = new FileWriter(currentOutFile.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				
				bw.write(sentence);
				bw.flush();
				bw.close();
				bw = null;
				fw = null;
				currentOutFile = null;
			}
			
			br.close();
			br = null;
			currentFile = null;			

		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}
