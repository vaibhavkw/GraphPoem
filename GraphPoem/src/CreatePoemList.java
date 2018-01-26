import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class CreatePoemList {
	
	public static String basePath = "D:/EclipseWorkSpace/GraphPoem/resources/";

	public static void main(String[] args) {
		CreatePoemList obj = new CreatePoemList();
		try{
			obj.readInputFolderPoetryFoundation();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public void readInputFolderPoetryFoundation() throws Exception {
		String inputFolder = basePath + "poetry_foundation_final/";
		File outFile = new File(basePath + "poemlist.txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		if (!outFile.exists()) {
			outFile.createNewFile();
		}
		fw = new FileWriter(outFile.getAbsoluteFile());
		bw = new BufferedWriter(fw);

		File inpFolder = new File(inputFolder);
		File[] children = inpFolder.listFiles();
		int count = -1;
		for(int loop=0; loop<children.length; loop++){			
			count++;
			File currentFile = children[count];
			if(currentFile.isFile() && currentFile.getName().contains(".txt")){
				String fileName = currentFile.getName();
				System.out.println("Processing file " + (count+1) + " : " + fileName);

				bw.write(fileName + "\n");
			}
			
		}
		bw.flush();
		bw.close();
		bw = null;
		fw = null;
		outFile = null;
		
		children = null;
		inpFolder = null;
	}

}
