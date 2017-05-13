package diction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MetaphorAnnotationAccuracy {

	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";

	public static int correctCount = 0;
	public static int wrongCount = 0;
	public static int skipCount = 0;

	public static void main(String[] args) {
		MetaphorAnnotationAccuracy obj = new MetaphorAnnotationAccuracy();

		//obj.readAnnotatedFile();
		obj.readAnnotatedFile2();
	}

	// Just reads 2 input files with manual annotation & outputs it on screen.
	public void readAnnotatedFile(){
		File currentFile = new File(basePath + "type1_metaphor_anno_Vaibhav2.txt");
		File currentFile2 = new File(basePath + "type1_metaphor_anno_Chris2.txt");
		BufferedReader br = null;
		BufferedReader br2 = null;
		String sCurrentLine;
		String sCurrentLine2;
		try{
			br = new BufferedReader(new FileReader(currentFile));
			br2 = new BufferedReader(new FileReader(currentFile2));
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				sCurrentLine2 = br2.readLine();
				checkLine(sCurrentLine, sCurrentLine2);
				/*if(lineNumber == 300){
					break;
				}*/
			}

			br.close();
			br = null;
			currentFile = null;

			System.out.println("Correct = " + correctCount);
			System.out.println("Wrong = " + wrongCount);
			System.out.println("Skipped = " + skipCount);


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

	public void checkLine(String str1, String str2){
		//System.out.println(str2);
		String[] strArr = str1.split("@");
		String anno = strArr[2];

		String[] strArr2 = str2.split("@");
		String anno2 = strArr2[2];

		if(anno.equals("s") || anno2.equals("s")){
			System.out.println(str1 + "::" + str2);
			skipCount++;
			return;
		}

		if(anno2.contains("(")){
			int indexOfStart = anno2.indexOf("(");
			anno2 = anno2.substring(indexOfStart+1, indexOfStart+2);
			//System.out.println(str2 + "::" + anno2);
		}

		if(anno.equals(anno2)){
			correctCount++;

		}else{
			wrongCount++;
		}

	}
	
	
	// Reads 2 input files with manual annotation & outputs it in a file (for later tie-breaking).
	public void readAnnotatedFile2(){
		File currentFile = new File(basePath + "type1_metaphor_anno_Vaibhav2.txt");
		File currentFile2 = new File(basePath + "type1_metaphor_anno_Chris2.txt");
		File outFile = new File(basePath + "type1_disagreements_batch2.txt");
		BufferedReader br = null;
		BufferedReader br2 = null;
		String sCurrentLine;
		String sCurrentLine2;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			fw = new FileWriter(outFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			br = new BufferedReader(new FileReader(currentFile));
			br2 = new BufferedReader(new FileReader(currentFile2));
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				sCurrentLine2 = br2.readLine();
				String outLine = checkLine2(sCurrentLine, sCurrentLine2);
				if(!outLine.equals("SKIP")){
					bw.write("\n" + outLine);
					bw.flush();
				}
				/*if(lineNumber == 300){
					break;
				}*/
			}

			br.close();
			br = null;
			currentFile = null;

			System.out.println("Correct = " + correctCount);
			System.out.println("Wrong = " + wrongCount);
			System.out.println("Skipped = " + skipCount);


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

	public String checkLine2(String str1, String str2){
		//System.out.println(str2);
		String[] strArr = str1.split("@");
		String anno = strArr[2];

		String[] strArr2 = str2.split("@");
		String anno2 = strArr2[2];

		if(anno2.contains("(")){
			int indexOfStart = anno2.indexOf("(");
			anno2 = anno2.substring(indexOfStart+1, indexOfStart+2);
			//System.out.println(str2 + "::" + anno2);
		}

		if(anno.equals(anno2)){
			correctCount++;
			return "SKIP";
		}else{
			wrongCount++;
			System.out.println(strArr[0] + "@" + strArr[1] + "@");
			return strArr[0] + "@" + strArr[1] + "@"; 
		}

	}

}
