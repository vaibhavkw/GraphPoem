import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class AnalysePoemOutput {
	public static String basePath = "D:/EclipseWorkSpace/GraphPoem/resources/";
	ArrayList<PoemOutput> poemList = new ArrayList<PoemOutput>();

	public static void main(String[] args) {
		AnalysePoemOutput obj = new AnalysePoemOutput();
		obj.readOutputFolder();
		System.out.println(obj.poemList.size());
		
		
	}
	
	public void readOutputFolder(){
		String outputFolder = basePath + "output/";
		File outFolder = new File(outputFolder);
		File[] children = outFolder.listFiles();
		for(int i=0;i<children.length;i++){
			File currentFile = children[i];
			if(currentFile.isFile() && currentFile.getName().contains(".txt")){
				String fileName = currentFile.getName();
				System.out.println("Processing file : " + fileName);
				BufferedReader br = null;
				String sCurrentLine;
				try{
					br = new BufferedReader(new FileReader(currentFile));
					int lineNumber = 0;
					PoemOutput poem = new PoemOutput();
					while ((sCurrentLine = br.readLine()) != null) {
						if(sCurrentLine.trim().equalsIgnoreCase("")){
							continue;
						}
						lineNumber++;
						
						String strArr[] = sCurrentLine.split(":");
						if(lineNumber == 1){
							poem = new PoemOutput();
							poem.setFileName(strArr[1]);
						}
						if(lineNumber == 2){
							poem.setTitle(strArr[1]);
						}
						if(lineNumber == 3){
							poem.setAuthor(strArr[1]);
						}
						if(lineNumber == 4){
							poem.setEndRhymeScore(Double.parseDouble((strArr[1])));
						}
						if(lineNumber == 5){
							poem.setInternalRhymeScore(Double.parseDouble((strArr[1])));
						}
						if(lineNumber == 6){
							poem.setEyeRhymeScore(Double.parseDouble((strArr[1])));
						}
						if(lineNumber == 7){
							poem.setFullRhymeScore(Double.parseDouble((strArr[1])));
						}
						if(lineNumber == 8){
							poem.setRichRhymeScore(Double.parseDouble((strArr[1])));
						}
						if(lineNumber == 9){
							poem.setIdenticalRhymeScore(Double.parseDouble((strArr[1])));
						}
						if(lineNumber == 10){
							poem.setSlantRhymeScore(Double.parseDouble((strArr[1])));
							poemList.add(poem);
							lineNumber = 0;
						}
					}
					br.close();
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
		outFolder = null;
	}
}
