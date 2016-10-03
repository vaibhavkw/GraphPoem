import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

//Find the accuracy percent of 2 annotated poems placed in baseline & poetry_foundation folders
public class AccuracyFinder {
	public static String basePath = "D:/EclipseWorkSpace/GraphPoem/resources/";
	public int totalAnnotationCount = 0;
	public int correctAnnotationCount = 0;
	public int incorrectAnnotationCount = 0;

	public static void main(String[] args) {
		AccuracyFinder obj = new AccuracyFinder();

		obj.readInputFolder();

	}

	public void readInputFolder(){
		String baselineFolderStr = basePath + "accuracychecker/baseline/";
		String checkFolderStr = basePath + "annotated_output/";
		File baselineFolder = new File(baselineFolderStr);
		File[] children = baselineFolder.listFiles();
		int count = -1;
		for(int loop=0; loop<children.length; loop++){
			count++;
			File currentFile = children[count];
			File checkFile = null;
			if(currentFile.isFile() && currentFile.getName().contains(".txt")){
				String fileName = currentFile.getName();
				System.out.println("\n\nProcessing file " + (count+1) + " : " + fileName);
				checkFile = new File(checkFolderStr + fileName);

				BufferedReader brBase = null;
				BufferedReader brCheck = null;
				String sCurrentLine;
				String sCheckLine;
				String poemName = "";
				String poemAuthor = "";
				try{
					brBase = new BufferedReader(new FileReader(currentFile));
					brCheck = new BufferedReader(new FileReader(checkFile));
					int lineNumber = 0;
					while ((sCurrentLine = brBase.readLine()) != null) {
						sCheckLine = brCheck.readLine();
						lineNumber++;
						if(lineNumber == 2){
							poemName = sCurrentLine;
						}
						if(lineNumber == 3){
							poemAuthor = sCurrentLine;
						}
						if(lineNumber <= 4){
							continue;
						}
						if(sCurrentLine.trim().equals("")){
							continue;
						}
						if(sCurrentLine.split(" ").length == 1){
							continue;
						}

						compareLines(sCurrentLine, sCheckLine);
					}
					computeAccuracy();

					brBase.close();
					brBase = null;
					brCheck.close();
					brCheck = null;
					currentFile = null;
					checkFile = null;

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (brBase != null) {
							brBase.close();
						}
						if (brCheck != null) {
							brCheck.close();
						}
						currentFile = null;
						checkFile = null;
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}

			}

			//writeToFile("output/poem_output" + (loop+1) + ".txt");
		}
		children = null;
		baselineFolder = null;

	}
	
	public void computeAccuracy(){
		System.out.println("Total annotations=" + totalAnnotationCount);
		System.out.println("Correct annotations=" + correctAnnotationCount);
		System.out.println("Incorrect annotations=" + (totalAnnotationCount-correctAnnotationCount));
		System.out.println("Skip annotations=" + incorrectAnnotationCount);
		double accuracy = (correctAnnotationCount*100.0/totalAnnotationCount);
		System.out.println("Accuracy Percent=" + accuracy);
		
	}
	
	public void compareLines(String sCurrentLine, String sCheckLine){
		HashMap<String, String> compareMap = new HashMap<String, String>();
		String[] arr1 = sCurrentLine.split("#");
		String[] arr2 = sCheckLine.split("#");
		if(arr1.length == 1 && arr2.length == 1){
			return;
		}
		for(int i=0;i<arr1.length;i++){
			String str = arr1[i];
			if(str.contains("=")){
				totalAnnotationCount++;
				str = str.trim();
				str = str.toLowerCase();
				compareMap.put(str, "1");
			}
		}
		for(int i=0;i<arr2.length;i++){
			String str = arr2[i];
			if(str.contains("=")){
				str = str.trim();
				str = str.toLowerCase();
				if(compareMap.containsKey(str)){
					correctAnnotationCount++;
					compareMap.remove(str);
					compareMap.put(str, "3");
				}else{
					incorrectAnnotationCount++;
					compareMap.put(str, "2");
				}				
			}
		}
		
		System.out.println(compareMap.toString());
		
	}
	
	
	
	
	
	
	
}
