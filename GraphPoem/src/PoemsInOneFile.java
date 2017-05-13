import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class PoemsInOneFile {
	public static String basePath = "D:/EclipseWorkSpace/GraphPoem/resources/";

	public static void main(String[] args) {
		PoemsInOneFile obj = new PoemsInOneFile();
		try{
			obj.readInputFolderPoetryFoundation();
			//System.out.println(System.getProperty("java.io.tmpdir"));
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public void readInputFolderPoetryFoundation() throws Exception {
		String inputFolder = basePath + "poetry_foundation_final_subset/";
		File outFile = new File(basePath + "allpoems_love.txt");
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

				BufferedReader br = null;
				String sCurrentLine;
				String sOrigLine;
				String poemName = "";
				String poemAuthor = "";
				try{
					br = new BufferedReader(new FileReader(currentFile));
					int lineNumber = 0;
					while ((sCurrentLine = br.readLine()) != null) {
						lineNumber++;
						sOrigLine = sCurrentLine;
						if(lineNumber == 1){
							poemName = sCurrentLine;
						}
						if(lineNumber == 2){
							poemAuthor = sCurrentLine;
						}
						if(lineNumber <= 9){
							continue;
						}

						bw.write("\n" + sCurrentLine.trim());

					}
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
		}
		bw.close();
		bw = null;
		fw = null;
		outFile = null;
		
		children = null;
		inpFolder = null;
	}

}
