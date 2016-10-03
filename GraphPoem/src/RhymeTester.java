import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class RhymeTester {
	public static String word1 = "sunly";
	public static String word2 = "moonly";
	
	public static String basePath = System.getProperty("user.dir") + "/resources/";
	HashMap<String,String> phonemeMap = new HashMap<String,String>();

	public static void main(String[] args) {
		RhymeTester obj = new RhymeTester();
		obj.populateCMUPhonemeList();
		
		String phoneme1 = obj.getPhoneme(word1);
		String phoneme2 = obj.getPhoneme(word2);
		
		System.out.println();
		System.out.println(phoneme1);
		System.out.println(phoneme2);
		
		String type =  obj.checkForEndRhymeSubType(phoneme1, phoneme2);
		System.out.println();
		System.out.println("Type : " + type);

	}
	
	public String checkForEndRhymeSubType(String word1, String word2){
		//word1 = reverseStringArr(("S IY1").replace(" ", "_"));
		//word2 = reverseStringArr(("W AH1 N").replace(" ", "_"));
		word1 = reverseStringArr(word1);
		word2 = reverseStringArr(word2);
		String[] lastUnitArr = word1.split("_");
		String[] currentUnitArr = word2.split("_");
		int min = Math.min(lastUnitArr.length, currentUnitArr.length);
		int max = Math.max(lastUnitArr.length, currentUnitArr.length);
		//int rhymeCount = 0; // to find overall same phoneme
		int consRhymeCount = 0; // to find contiguous same phonemes
		/*for(int n=0;n<min;n++){
			if(lastUnitArr[n].equals(currentUnitArr[n])){
				rhymeCount++;
			}
		}*/
		boolean stressFall = false;
		for(int n=0;n<min;n++){
			if(lastUnitArr[n].equals(currentUnitArr[n])){
				consRhymeCount++;
				if(lastUnitArr[n].contains("1") || lastUnitArr[n].contains("2")){
					stressFall = true;
				}
			}else{
				break;
			}
		}
		if(min == max){
			if(consRhymeCount == min){
				//System.out.println("Rich rhyme");
				return "rich";
			}
		}
		if(consRhymeCount == min){
			//System.out.println("Full rhyme");
			return "full";
		}			
		if(consRhymeCount >= 1 && stressFall == true){
			//System.out.println("Full rhyme");
			return "full";
		}
		if(consRhymeCount >= 1 && stressFall == false){
			//System.out.println("Slant rhyme");
			return "slant";
		}
		return "norhyme";
	}
	
	
	public String getPhoneme(String word){
		String phoneme = "";
		try{
			phoneme = phonemeMap.get(word.toUpperCase()).toString();
			phoneme = phoneme.replaceAll(" ", "_");
		}catch(NullPointerException npe){
			phoneme = "PhonemeNotFound"; 
		}
		return phoneme;
	}
	
	public String reverseStringArr(String inp){
		String[] strArr = inp.split("_");
		String outStr = "";
		for(int i=strArr.length-1;i>=0;i--){
			outStr = outStr + strArr[i] + "_";
		}
		outStr = outStr.substring(0, outStr.length()-1);
		return outStr;
	}
	
	public void populateCMUPhonemeList(){
		BufferedReader br = null;
		String sCurrentLine;
		try{
			br = new BufferedReader(new FileReader(basePath + "cmudict-0.7b.txt"));
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				int indexOfSpace = sCurrentLine.indexOf(" ");
				String key = sCurrentLine.substring(0, indexOfSpace);
				String value = sCurrentLine.substring(indexOfSpace+1, sCurrentLine.length()).trim();
				if(key.charAt(key.length()-1) == ')'){
					String baseWord = key.substring(0, key.indexOf("("));
					String origPhoneme = phonemeMap.get(baseWord).toString();
					phonemeMap.put(baseWord, origPhoneme + "#" + value);
				}else{
					phonemeMap.put(key, value);
				}
			}
			br.close();
			//System.out.println("Populated Phoneme List : " + phonemeMap.size());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
