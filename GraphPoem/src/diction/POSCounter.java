package diction;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class POSCounter {
	
	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";
	
	public LinkedHashMap<String, HashMap<String, Integer>> poemPosList = new LinkedHashMap<String, HashMap<String, Integer>>(); 
	
	public static void main(String[] args) {
		POSCounter obj = new POSCounter();
		obj.readInputFolderPoetryFoundation();
		obj.writeToFile("posOutput.txt");
	}
	
	public void readInputFolderPoetryFoundation(){
		String inputFolder = basePath + "pos/";
		File inpFolder = new File(inputFolder);
		File[] children = inpFolder.listFiles();
		int count = -1;
		for(int loop=0; loop<children.length; loop++){
			count++;
			HashMap<String, Integer> posCountMap = new HashMap<String, Integer>();
			File currentFile = children[count];
			if(currentFile.isFile() && currentFile.getName().contains(".pos")){
				String fileName = currentFile.getName();
				System.out.println("Processing file " + (count+1) + " : " + fileName);

				BufferedReader br = null;
				String sCurrentLine;
				try{
					br = new BufferedReader(new FileReader(currentFile));
					int lineNumber = 0;
					int tokenCount = 0;
					while ((sCurrentLine = br.readLine()) != null) {
						lineNumber++;
						if(sCurrentLine.trim().equals("")){
							continue;
						}
						if(sCurrentLine.trim().split(" ").length == 1){
							continue;
						}
						if(sCurrentLine.trim().replaceAll("SPACE", "").trim().split(" ").length == 1){
							continue;
						}
						
						String []strArr = sCurrentLine.split(" ");
						
						for(String word : strArr){
							String key = word.trim();
							if(!key.equals("SPACE")){
								tokenCount++;
							}
							if(posCountMap.containsKey(key)){
								int val = posCountMap.get(key);
								val++;
								posCountMap.remove(key);
								posCountMap.put(key, val);
							}else{
								posCountMap.put(key, 1);
							}
						}
					}
					posCountMap.put("TOKENS", tokenCount);
					poemPosList.put(fileName, posCountMap);
					
					

					
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

			//writeToFile("output2/poem_output" + (loop+1) + ".txt");
		}
		children = null;
		inpFolder = null;

	}
	
	public void writeToFile(String str){
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File file = new File(basePath + str);

			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);

			Iterator itList = poemPosList.entrySet().iterator();
			while(itList.hasNext()){
				Map.Entry elParent = (Map.Entry)itList.next();
				String fileName = (String) elParent.getKey();
				HashMap<String, Integer> posCountMap = (HashMap<String, Integer>) elParent.getValue();
				bw.write("Filename : " + fileName + "\n");
				
				Iterator it = posCountMap.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry el = (Map.Entry)it.next();
					if(el.getKey().equals("SPACE")){
						continue;
					}
					bw.write(el.getKey() + " : " + el.getValue() + "\n");
				}
				if(posCountMap.containsKey("VERB")){
					bw.write("Verbal density=" + (1.0*posCountMap.get("VERB")/posCountMap.get("TOKENS")) + "\n");
				}
				if(posCountMap.containsKey("NOUN")){
					bw.write("Noun density=" + (1.0*posCountMap.get("NOUN")/posCountMap.get("TOKENS")) + "\n");
				}
				if(posCountMap.containsKey("ADJ")){
					bw.write("Adjective density=" + (1.0*posCountMap.get("ADJ")/posCountMap.get("TOKENS")) + "\n");
				}
				if(posCountMap.containsKey("PRON") && posCountMap.containsKey("NOUN")){
					bw.write("Pronoun Vs Noun ratio=" + (1.0*posCountMap.get("PRON")/posCountMap.get("NOUN")) + "\n");
				}
				bw.write("\n");
			}

			bw.close();

			System.out.println(str + " file written");

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
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

}
