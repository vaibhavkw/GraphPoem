import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


public class MergeRhymeDictionCSV {

	public static String basePath = "D:/EclipseWorkSpace/GraphPoem" + "/resources/";

	public static void main(String[] args) {
		MergeRhymeDictionCSV obj = new MergeRhymeDictionCSV();
		obj.readCSVFiles();
	}

	public void readCSVFiles(){
		File outFile = new File(basePath + "rhyme_diction.csv");
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

		HashMap<String, String> rhymeMap = new HashMap<String, String>(); 
		
		File rhymeFile = new File(basePath + "rhymefull.csv");

		BufferedReader br = null;
		String sCurrentLine;
		try{
			br = new BufferedReader(new FileReader(rhymeFile));
			while ((sCurrentLine = br.readLine()) != null) {
				int index = sCurrentLine.indexOf(",");
				String key = sCurrentLine.substring(0, index);
				rhymeMap.put(key, sCurrentLine);
			}
			br.close();
			br = null;
			rhymeFile = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				rhymeFile = null;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		File dictionFile = new File(basePath + "diction_quantified.txt");

		br = null;
		String sCurrentLine2;
		try{
			br = new BufferedReader(new FileReader(dictionFile));
			while ((sCurrentLine2 = br.readLine()) != null) {
				int index = sCurrentLine2.indexOf(",");
				String key = sCurrentLine2.substring(0, index);
				String value = sCurrentLine2.substring(index+1, sCurrentLine2.length());
				String rhymeStr;
				if(rhymeMap.containsKey(key)){
					rhymeStr = rhymeMap.get(key);
				}else{
					rhymeStr = key + ",0.00000,0.00000,0.00000,0.00000,0.00000,0.00000,0.00000";
				}
				bw.write(rhymeStr + "," + value + "\n");
				bw.flush();
			}
			br.close();
			br = null;
			dictionFile = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				dictionFile = null;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

}
