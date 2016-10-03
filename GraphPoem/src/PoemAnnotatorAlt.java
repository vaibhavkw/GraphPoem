import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class PoemAnnotatorAlt {
	public static String basePath = System.getProperty("user.dir") + "/resources/";
	HashMap<String,String> phonemeMap = new HashMap<String,String>();
	ArrayList<AnnotatedPoem> poemList = new ArrayList<AnnotatedPoem>(); 
	public static int batches  = 10;

	public static void main(String[] args) {
		PoemAnnotatorAlt obj = new PoemAnnotatorAlt();
		obj.populateCMUPhonemeList();

		//obj.readInputFolder();
		obj.readInputFolderPoetryFoundation();
		//obj.readInputFolderSubset();

	}

	public void checkForEyeRhymes(){
		for(int i=0;i<poemList.size();i++){
			AnnotatedPoem poem = poemList.get(i);
			ArrayList<String> lastWordList = new ArrayList<String>(); 
			System.out.println("\nTitle : " + poem.getTitle());
			for(int j=0;j<poem.getContent().size();j++){
				ArrayList<String> currentLine = poem.getContent().get(j);
				lastWordList.add(currentLine.get(currentLine.size()-1));
			}
			double consectiveRhymeScore = consectiveLineEyeRhyme(lastWordList, poem);
			System.out.println("Consective Eye Rhyme Score : " + consectiveRhymeScore);

			double alternateRhymeScore = alternateLineEyeRhyme(lastWordList, poem);
			System.out.println("Alternate Eye Rhyme Score : " + alternateRhymeScore);

			poem.setIdenticalRhymeScore((consectiveRhymeScore + alternateRhymeScore)/2.0);
		}

	}

	public double consectiveLineEyeRhyme(ArrayList<String> lastWordList, AnnotatedPoem poem){
		double totalRhymeScore = 0;
		//Check for consective rhyming
		for(int k=1;k<lastWordList.size();k++){
			String currentLineEndWord = lastWordList.get(k);
			String lastLineEndWord = lastWordList.get(k-1);

			double rhymeScore = 0.0;
			String revCurrentLineEndWord = reverseStringArr(currentLineEndWord);
			String revLastLineEndWord = reverseStringArr(lastLineEndWord);

			int min = Math.min(revCurrentLineEndWord.length(), revLastLineEndWord.length());
			int max = Math.max(revCurrentLineEndWord.length(), revLastLineEndWord.length());
			int rhymeCount = 0;
			for(int n=0;n<min;n++){
				if(revCurrentLineEndWord.charAt(n) ==  revLastLineEndWord.charAt(n)){
					rhymeCount++;
				}else{
					break;
				}
			}
			if(rhymeCount>2){
				rhymeScore = (double)rhymeCount/max;
				ArrayList<String> annoList = poem.getAnnotationLine(k);
				boolean otherRhymePresent = checkForOtherRhymes(annoList, "cons");
				if(!otherRhymePresent){
					annoList.add("#cons=eye#");
				}else{
					rhymeScore = 0.0;
				}
			}
			totalRhymeScore = totalRhymeScore + rhymeScore;
		}
		totalRhymeScore = (double)totalRhymeScore/(lastWordList.size()-1);
		return totalRhymeScore;
	}


	public double alternateLineEyeRhyme(ArrayList<String> lastWordList, AnnotatedPoem poem){
		double totalRhymeScore = 0;
		//Check for alternate rhyming
		for(int k=2;k<lastWordList.size();k++){
			String currentLineEndWord = lastWordList.get(k);
			for(int h=k-2;h>=0;h--){
				String level = "";
				if((k-h) != 2){
					level = String.valueOf(k-h-1);
				}
				String lastLineEndWord = lastWordList.get(h);

				double rhymeScore = 0.0;
				String revCurrentLineEndWord = reverseStringArr(currentLineEndWord);
				String revLastLineEndWord = reverseStringArr(lastLineEndWord);

				int min = Math.min(revCurrentLineEndWord.length(), revLastLineEndWord.length());
				int max = Math.max(revCurrentLineEndWord.length(), revLastLineEndWord.length());
				int rhymeCount = 0;
				for(int n=0;n<min;n++){
					if(revCurrentLineEndWord.charAt(n) ==  revLastLineEndWord.charAt(n)){
						rhymeCount++;
					}else{
						break;
					}
				}
				if(rhymeCount>2){
					rhymeScore = (double)rhymeCount/max;
					ArrayList<String> annoList = poem.getAnnotationLine(k);
					boolean otherRhymePresent = checkForOtherRhymes(annoList, "alt" + level);
					if(!otherRhymePresent){
						annoList.add("#alt" + level + "=eye#");
					}else{
						rhymeScore = 0.0;
					}
				}
				totalRhymeScore = totalRhymeScore + rhymeScore;
			}
		}
		double possibleComparisons = (double)(lastWordList.size()-1)*(lastWordList.size()-2)/2.0;
		totalRhymeScore = (double)totalRhymeScore/(possibleComparisons);
		return totalRhymeScore;
	}

	public boolean checkForOtherRhymes(ArrayList<String> annoList, String str){
		boolean ret = false;
		for(int i=0;i<annoList.size();i++){
			if(annoList.get(i).contains(str)){
				ret = true;
			}
		}
		return ret;
	}



	public void checkForIdenticalRhymes(){
		for(int i=0;i<poemList.size();i++){
			AnnotatedPoem poem = poemList.get(i);
			ArrayList<String> lastWordList = new ArrayList<String>(); 
			System.out.println("\nTitle : " + poem.getTitle());
			for(int j=0;j<poem.getContent().size();j++){
				ArrayList<String> currentLine = poem.getContent().get(j);
				lastWordList.add(currentLine.get(currentLine.size()-1));
			}
			double consectiveRhymeScore = consectiveLineIdenticalRhyme(lastWordList, poem);
			System.out.println("Consective Identical Rhyme Score : " + consectiveRhymeScore);

			double alternateRhymeScore = alternateLineIdenticalRhyme(lastWordList, poem);
			System.out.println("Alternate Identical Rhyme Score : " + alternateRhymeScore);

			poem.setIdenticalRhymeScore((consectiveRhymeScore + alternateRhymeScore)/2.0);
		}

	}

	public double alternateLineIdenticalRhyme(ArrayList<String> lastWordList, AnnotatedPoem poem){
		double totalRhymeScore = 0;
		//Check for alternate rhyming
		for(int k=2;k<lastWordList.size();k++){
			String currentLineEndWord = lastWordList.get(k);
			for(int h=k-2;h>=0;h--){
				String level = "";
				if((k-h) != 2){
					level = String.valueOf(k-h-1);
				}
				String lastLineEndWord = lastWordList.get(h);
				if(currentLineEndWord.equalsIgnoreCase(lastLineEndWord)){
					totalRhymeScore++;
					ArrayList<String> annoList = poem.getAnnotationLine(k);
					annoList.add("#alt" + level + "=identical#");
				}
			}
		}
		double possibleComparisons = (double)(lastWordList.size()-1)*(lastWordList.size()-2)/2.0;
		totalRhymeScore = (double)totalRhymeScore/(possibleComparisons);
		return totalRhymeScore;
	}

	public double consectiveLineIdenticalRhyme(ArrayList<String> lastWordList, AnnotatedPoem poem){
		double totalRhymeScore = 0;
		//Check for consecutive rhyming
		for(int k=1;k<lastWordList.size();k++){
			String currentLineEndWord = lastWordList.get(k);
			String lastLineEndWord = lastWordList.get(k-1);
			if(currentLineEndWord.equalsIgnoreCase(lastLineEndWord)){
				totalRhymeScore++;
				ArrayList<String> annoList = poem.getAnnotationLine(k);
				annoList.add("#cons=identical#");
			}
		}
		totalRhymeScore = (double)totalRhymeScore/(lastWordList.size()-1);
		return totalRhymeScore;
	}

	public void checkForEndRhymes(){
		for(int i=0;i<poemList.size();i++){
			AnnotatedPoem poem = poemList.get(i);
			ArrayList<String> lastWordList = new ArrayList<String>(); 
			System.out.println("\nTitle : " + poem.getTitle());
			for(int j=0;j<poem.getPhonemes().size();j++){
				ArrayList<String> currentLine = poem.getPhonemes().get(j);
				lastWordList.add(currentLine.get(currentLine.size()-1));
			}
			double consectiveRhymeScore = consectiveLineEndRhyme(lastWordList, poem);
			System.out.println("Consective End Rhyme Score : " + consectiveRhymeScore);

			double alternateRhymeScore = alternateLineEndRhyme(lastWordList, poem);
			System.out.println("Alternate End Rhyme Score : " + alternateRhymeScore);

			poem.setEndRhymeScore((consectiveRhymeScore + alternateRhymeScore)/2.0);
		}
	}

	public double consectiveLineEndRhyme(ArrayList<String> lastWordList, AnnotatedPoem poem){
		double totalRhymeScore = 0;
		//Check for consecutive rhyming
		for(int k=1;k<lastWordList.size();k++){
			String currentPhoneme = lastWordList.get(k);
			String lastPhoneme = lastWordList.get(k-1);
			if(currentPhoneme.equals("PhonemeNotFound") || lastPhoneme.equals("PhonemeNotFound")){
				continue;
			}
			//Check if it is already Identical rhyme case, if yes then skip to next line
			ArrayList<String> annoList1 = poem.getAnnotationLine(k);
			boolean identicalRhymePresent = checkForOtherRhymes(annoList1, "cons=identical");
			if(identicalRhymePresent){
				continue;
			}

			String[] lastPhonemeArr = lastPhoneme.split("#");
			String[] currentPhonemeArr = currentPhoneme.split("#");

			double rhymeScore = 0;
			String chosenWord1 = "";
			String chosenWord2 = "";
			for(int l=0;l<currentPhonemeArr.length;l++){
				String currentPhonemeSingle = reverseStringArr(currentPhonemeArr[l]);
				for(int m=0;m<lastPhonemeArr.length;m++){
					String lastPhonemeSingle = reverseStringArr(lastPhonemeArr[m]);
					String[] lastUnitArr = lastPhonemeSingle.split("_");
					String[] currentUnitArr = currentPhonemeSingle.split("_");
					int min = Math.min(lastUnitArr.length, currentUnitArr.length);
					int max = Math.max(lastUnitArr.length, currentUnitArr.length);
					double rhymeCount = 0.0;
					for(int n=0;n<min;n++){
						rhymeCount = rhymeCount + calculateRhymeScore(lastUnitArr[n], currentUnitArr[n]);
					}
					if((double)rhymeCount/max >= rhymeScore){
						rhymeScore = (double)rhymeCount/max;
						chosenWord1 = lastPhonemeSingle;
						chosenWord2 = currentPhonemeSingle;
					}
					//rhymeScore = Math.max((double)rhymeCount/max , rhymeScore);
				}
			}
			String endRhymeType = checkForEndRhymeSubType(chosenWord1, chosenWord2);
			updateRhymeType(endRhymeType, poem, rhymeScore, 1.0);
			if(endRhymeType.equals("norhyme")){
				rhymeScore = 0.0;
			}else{
				ArrayList<String> annoList = poem.getAnnotationLine(k);
				annoList.add("#cons=" + endRhymeType + "#");
			}
			totalRhymeScore = (double)totalRhymeScore + rhymeScore;
		}
		totalRhymeScore = (double)totalRhymeScore/(lastWordList.size()-1);
		return totalRhymeScore;
	}	

	public double alternateLineEndRhyme(ArrayList<String> lastWordList, AnnotatedPoem poem){
		double totalRhymeScore = 0;
		//Check for alternate N rhyming
		for(int k=2;k<lastWordList.size();k++){
			String currentPhoneme = lastWordList.get(k);
			for(int h=k-2;h>=0;h--){
				String level = "";
				if((k-h) != 2){
					level = String.valueOf(k-h-1);
				}
				String lastPhoneme = lastWordList.get(h);
				if(currentPhoneme.equals("PhonemeNotFound") || lastPhoneme.equals("PhonemeNotFound")){
					continue;
				}
				//Check if it is already Identical rhyme case, if yes then skip to next line
				ArrayList<String> annoList1 = poem.getAnnotationLine(k);
				boolean identicalRhymePresent = checkForOtherRhymes(annoList1, "alt" + level + "=identical");
				if(identicalRhymePresent){
					continue;
				}

				String[] lastPhonemeArr = lastPhoneme.split("#");
				String[] currentPhonemeArr = currentPhoneme.split("#");

				double rhymeScore = 0;
				String chosenWord1 = "";
				String chosenWord2 = "";
				for(int l=0;l<currentPhonemeArr.length;l++){
					String currentPhonemeSingle = reverseStringArr(currentPhonemeArr[l]);
					for(int m=0;m<lastPhonemeArr.length;m++){
						String lastPhonemeSingle = reverseStringArr(lastPhonemeArr[m]);
						String[] lastUnitArr = lastPhonemeSingle.split("_");
						String[] currentUnitArr = currentPhonemeSingle.split("_");
						int min = Math.min(lastUnitArr.length, currentUnitArr.length);
						int max = Math.max(lastUnitArr.length, currentUnitArr.length);
						double rhymeCount = 0.0;
						for(int n=0;n<min;n++){
							rhymeCount = rhymeCount + calculateRhymeScore(lastUnitArr[n], currentUnitArr[n]);
						}
						if((double)rhymeCount/max >= rhymeScore){
							rhymeScore = (double)rhymeCount/max;
							chosenWord1 = lastPhonemeSingle;
							chosenWord2 = currentPhonemeSingle;
						}
						//rhymeScore = Math.max((double)rhymeCount/max , rhymeScore);
					}
				}
				String endRhymeType = checkForEndRhymeSubType(chosenWord1, chosenWord2);
				updateRhymeType(endRhymeType, poem, rhymeScore, 1.0);
				if(endRhymeType.equals("norhyme")){
					rhymeScore = 0.0;
				}else{
					ArrayList<String> annoList = poem.getAnnotationLine(k);
					annoList.add("#alt" + level + "=" + endRhymeType + "#");
				}
				totalRhymeScore = (double)totalRhymeScore + rhymeScore;
			}

		}
		double possibleComparisons = (double)(lastWordList.size()-1)*(lastWordList.size()-2)/2.0;
		totalRhymeScore = (double)totalRhymeScore/(possibleComparisons);
		return totalRhymeScore;
	}
	

	public void checkForInternalRhymes(){
		for(int i=0;i<poemList.size();i++){
			AnnotatedPoem poem = poemList.get(i);
			System.out.println("\nTitle : " + poem.getTitle());

			ArrayList<String> pausePhonemeList = new ArrayList<String>();
			ArrayList<String> lastPhonemeList = new ArrayList<String>();
			ArrayList<Integer> linePauseList = poem.getLinePause();
			ArrayList<String> pauseWordList = new ArrayList<String>();
			ArrayList<String> lastWordList = new ArrayList<String>();			

			for(int j=0;j<poem.getContent().size();j++){
				ArrayList<String> currentLine = poem.getPhonemes().get(j);
				lastPhonemeList.add(currentLine.get(currentLine.size()-1));

				ArrayList<String> currentLineW = poem.getContent().get(j);
				lastWordList.add(currentLineW.get(currentLineW.size()-1));

				int linePause = linePauseList.get(j);
				if(linePause == -1){
					pausePhonemeList.add("NoPauseFoundInCurrentLine");
					pauseWordList.add("NoPauseFoundInCurrentLine");
				}else{
					pausePhonemeList.add(currentLine.get(linePause));
					pauseWordList.add(currentLineW.get(linePause).replaceAll(",", "").replaceAll("\\.", "").replaceAll(";", ""));
				}
			}
			double internalRhymeScore = internalRhyme(pauseWordList, lastWordList, pausePhonemeList, lastPhonemeList, poem);
			System.out.println("Internal Rhyme Score : " + internalRhymeScore);

			poem.setInternalRhymeScore(internalRhymeScore);
		}
	}

	public double internalRhyme(ArrayList<String> pauseWordList, ArrayList<String> lastWordList, ArrayList<String> pausePhonemeList, ArrayList<String> lastPhonemeList, AnnotatedPoem poem){
		double totalRhymeScore = 0;
		//Check for internal rhyming
		for(int k=0;k<lastPhonemeList.size();k++){
			String currentPhoneme = pausePhonemeList.get(k);
			String lastPhoneme = lastPhonemeList.get(k);
			if(currentPhoneme.equals("NoPauseFoundInCurrentLine")){
				continue;
			}
			//Handling for Identical rhyme in Internal rhyme
			if((pauseWordList.get(k)).equalsIgnoreCase(lastWordList.get(k))){
				totalRhymeScore = (double)totalRhymeScore + 1.0;
				ArrayList<String> annoList = poem.getAnnotationLine(k);
				annoList.add("#int=identical#");
				continue;
			}

			if(currentPhoneme.equals("PhonemeNotFound") || lastPhoneme.equals("PhonemeNotFound")){
				continue;
			}
			String[] lastPhonemeArr = lastPhoneme.split("#");
			String[] currentPhonemeArr = currentPhoneme.split("#");

			double rhymeScore = 0;
			String chosenWord1 = "";
			String chosenWord2 = "";
			for(int l=0;l<currentPhonemeArr.length;l++){
				String currentPhonemeSingle = reverseStringArr(currentPhonemeArr[l]);
				for(int m=0;m<lastPhonemeArr.length;m++){
					String lastPhonemeSingle = reverseStringArr(lastPhonemeArr[m]);
					String[] lastUnitArr = lastPhonemeSingle.split("_");
					String[] currentUnitArr = currentPhonemeSingle.split("_");
					int min = Math.min(lastUnitArr.length, currentUnitArr.length);
					int max = Math.max(lastUnitArr.length, currentUnitArr.length);
					double rhymeCount = 0.0;
					for(int n=0;n<min;n++){
						rhymeCount = rhymeCount + calculateRhymeScore(lastUnitArr[n], currentUnitArr[n]);
					}
					if((double)rhymeCount/max >= rhymeScore){
						rhymeScore = (double)rhymeCount/max;
						chosenWord1 = lastPhonemeSingle;
						chosenWord2 = currentPhonemeSingle;
					}
					//rhymeScore = Math.max((double)rhymeCount/max , rhymeScore);
				}
			}
			String endRhymeType = checkForEndRhymeSubType(chosenWord1, chosenWord2);
			updateRhymeType(endRhymeType, poem, rhymeScore, 1.0);
			if(endRhymeType.equals("norhyme")){
				rhymeScore = 0.0;
			}else{
				ArrayList<String> annoList = poem.getAnnotationLine(k);
				annoList.add("#int=" + endRhymeType + "#");
			}
			totalRhymeScore = (double)totalRhymeScore + rhymeScore;
		}
		totalRhymeScore = (double)totalRhymeScore/(lastPhonemeList.size());
		return totalRhymeScore;
	}

	public double calculateRhymeScore(String phoneme1, String phoneme2){
		if(phoneme1.equals(phoneme2)){
			if(phoneme1.contains("1") || phoneme1.contains("2")){
				return 1.0;
			}
			if(phoneme1.contains("0")){
				return 0.6;
			}else{
				return 0.8;
			}
		}
		boolean isVowel1 = phoneme1.contains("0") || phoneme1.contains("1") || phoneme1.contains("2");
		boolean isVowel2 = phoneme2.contains("0") || phoneme2.contains("1") || phoneme2.contains("2");
		if(isVowel1 && isVowel2){
			return 0.2;
		}
		if(!isVowel1 && !isVowel2){
			return 0.4;
		}
		return 0.0;
	}

	public String checkForEndRhymeSubType(String word1, String word2){
		//word1 = reverseStringArr(("S IY1").replace(" ", "_"));
		//word2 = reverseStringArr(("W AH1 N").replace(" ", "_"));
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

	public void updateRhymeType(String rhymeType, AnnotatedPoem poem, double rhymeScore, double normfactor){
		if(rhymeType.equalsIgnoreCase("slant")){
			double slantScore = poem.getSlantRhymeScore();
			slantScore = (double)(slantScore + rhymeScore)/normfactor;

			poem.setSlantRhymeScore(slantScore);
		}
		if(rhymeType.equalsIgnoreCase("full")){
			double fullScore = poem.getFullRhymeScore();
			fullScore = (double)(fullScore + rhymeScore)/normfactor;
			poem.setFullRhymeScore(fullScore);
		}
		if(rhymeType.equalsIgnoreCase("rich")){
			double richScore = poem.getRichRhymeScore();
			richScore = (double)(richScore + rhymeScore)/normfactor;
			poem.setRichRhymeScore(richScore);
		}
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

	public void readInputFolderPoetryFoundation(){
		String inputFolder = basePath + "poetry_foundation_final/";
		File inpFolder = new File(inputFolder);
		File[] children = inpFolder.listFiles();
		int batchSize = children.length / batches;
		int count = -1;
		for(int loop=0; loop<batches; loop++){
			poemList = new ArrayList<AnnotatedPoem>();
			for(int i=0;i<batchSize;i++){
				count++;
				File currentFile = children[count];
				if(currentFile.isFile() && currentFile.getName().contains(".txt")){
					String fileName = currentFile.getName();
					System.out.println("Processing file " + (count+1) + " : " + fileName);
					AnnotatedPoem poem = new AnnotatedPoem();
					ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();
					ArrayList<ArrayList<String>> phonemes = new ArrayList<ArrayList<String>>();
					ArrayList<ArrayList<String>> contentAnno = new ArrayList<ArrayList<String>>();
					ArrayList<Integer> linePauseList = new ArrayList<Integer>();
					HashMap<Integer, Integer> paraPause = new HashMap<Integer, Integer>();

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
								paraPause.put(lineNumber-4, lineNumber-4);
								continue;
							}
							if(sCurrentLine.trim().split(" ").length == 1){
								paraPause.put(lineNumber-4, lineNumber-4);
								continue;
							}

							String processeLine = processLine(sCurrentLine);
							ArrayList<String> currentLine = getArrayListFromStr(processeLine);
							content.add(currentLine);

							//Add blank strings to Annnotation List
							ArrayList<String> currentLineAnno = getArrayListFromBlankStr();
							contentAnno.add(currentLineAnno);

							ArrayList<String> currentPhoneme = getPhonemeListFromStr(currentLine);
							phonemes.add(currentPhoneme);
							int linePause = findLinePause(currentLine);
							linePauseList.add(linePause);
						}
						poem.setTitle(poemName);
						poem.setAuthor(poemAuthor);
						poem.setFileName(fileName);
						poem.setContent(content);
						poem.setAnnotation(contentAnno);
						poem.setPhonemes(phonemes);
						poem.setLinePause(linePauseList);
						poem.setParaPause(paraPause);
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
					poemList.add(poem);
				}
			}
			System.out.println(poemList.size());

			checkForIdenticalRhymes(); // Identical Rhyme : Same word repeated

			checkForEndRhymes();  // End Rhyme

			checkForEyeRhymes();  // Eye Rhyme : Rhyme on spelling rather than phoneme

			checkForInternalRhymes();  // Internal Rhyme 

			//printAllPoems();

			writeAnnotatedPoems("annotated_output2/");

			writeToFile("output2/poem_output" + (loop+1) + ".txt");
		}
		children = null;
		inpFolder = null;

	}


	public void readInputFolderSubset(){
		String inputFolder = basePath + "poetry_foundation_final_subset/";
		File inpFolder = new File(inputFolder);
		File[] children = inpFolder.listFiles();
		int count = -1;
		for(int loop=0; loop<children.length; loop++){
			poemList = new ArrayList<AnnotatedPoem>();
			count++;
			File currentFile = children[count];
			if(currentFile.isFile() && currentFile.getName().contains(".txt")){
				String fileName = currentFile.getName();
				System.out.println("Processing file " + (count+1) + " : " + fileName);
				AnnotatedPoem poem = new AnnotatedPoem();
				ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();
				ArrayList<ArrayList<String>> phonemes = new ArrayList<ArrayList<String>>();
				ArrayList<ArrayList<String>> contentAnno = new ArrayList<ArrayList<String>>();
				ArrayList<Integer> linePauseList = new ArrayList<Integer>();
				HashMap<Integer, Integer> paraPause = new HashMap<Integer, Integer>();

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
							paraPause.put(lineNumber-4, lineNumber-4);
							continue;
						}
						if(sCurrentLine.trim().split(" ").length == 1){
							paraPause.put(lineNumber-4, lineNumber-4);
							continue;
						}

						String processeLine = processLine(sCurrentLine);
						ArrayList<String> currentLine = getArrayListFromStr(processeLine);
						content.add(currentLine);

						//Add blank strings to Annnotation List
						ArrayList<String> currentLineAnno = getArrayListFromBlankStr();
						contentAnno.add(currentLineAnno);

						ArrayList<String> currentPhoneme = getPhonemeListFromStr(currentLine);
						phonemes.add(currentPhoneme);
						int linePause = findLinePause(currentLine);
						linePauseList.add(linePause);
					}
					poem.setTitle(poemName);
					poem.setAuthor(poemAuthor);
					poem.setFileName(fileName);
					poem.setContent(content);
					poem.setAnnotation(contentAnno);
					poem.setPhonemes(phonemes);
					poem.setLinePause(linePauseList);
					poem.setParaPause(paraPause);
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
				poemList.add(poem);
			}
			System.out.println(poemList.size());

			checkForIdenticalRhymes(); // Identical Rhyme : Same word repeated

			checkForEndRhymes();  // End Rhyme

			checkForEyeRhymes();  // Eye Rhyme : Rhyme on spelling rather than phoneme

			checkForInternalRhymes();  // Internal Rhyme 

			//printAllPoems();

			writeAnnotatedPoems("subset_output/");

			//writeToFile("output/poem_output" + (loop+1) + ".txt");
		}
		children = null;
		inpFolder = null;

	}

	public void readInputFolder(){
		String inputFolder = basePath + "input/";
		File inpFolder = new File(inputFolder);
		File[] children = inpFolder.listFiles();
		for(int i=0;i<children.length;i++){
			File currentFile = children[i];
			if(currentFile.isFile() && currentFile.getName().contains(".txt")){
				AnnotatedPoem poem = new AnnotatedPoem();
				ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();
				ArrayList<ArrayList<String>> phonemes = new ArrayList<ArrayList<String>>();
				ArrayList<Integer> linePauseList = new ArrayList<Integer>();
				String fileName = currentFile.getName();
				System.out.println("Processing file : " + fileName);
				BufferedReader br = null;
				String sCurrentLine;
				try{
					br = new BufferedReader(new FileReader(currentFile));
					int lineNumber = -1;
					while ((sCurrentLine = br.readLine()) != null) {
						lineNumber++;
						String processeLine = processLine(sCurrentLine);
						ArrayList<String> currentLine = getArrayListFromStr(processeLine);
						content.add(currentLine);
						ArrayList<String> currentPhoneme = getPhonemeListFromStr(currentLine);
						phonemes.add(currentPhoneme);
						int linePause = findLinePause(currentLine);
						linePauseList.add(linePause);						
					}
					poem.setTitle(fileName.substring(0, fileName.indexOf("-")));
					poem.setAuthor(fileName.substring(fileName.indexOf("-")+1, fileName.lastIndexOf(".")));
					poem.setContent(content);
					poem.setPhonemes(phonemes);
					poem.setLinePause(linePauseList);
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
				poemList.add(poem);
			}
		}
		children = null;
		inpFolder = null;
	}

	public ArrayList<String> getPhonemeListFromStr(ArrayList<String> currentLine){
		ArrayList<String> phonemeList = new ArrayList<String>();
		for(int i=0;i<currentLine.size();i++){
			String word = currentLine.get(i);
			word = word.trim();
			word = word.replaceAll(",", "");
			word = word.replaceAll("\\?", "");
			word = word.replaceAll("\\.", "");
			word = word.replaceAll(":", "");
			word = word.replaceAll(";", "");
			word = word.replaceAll("!", "");
			word = word.replaceAll("\"", "");
			//Hyphen handling
			String phoneme = getPhoneme(word);
			if(phoneme.equals("PhonemeNotFound") && word.contains("-")){
				String temp = word.replaceAll("-", "");
				phoneme = getPhoneme(temp);
				if(phoneme.equals("PhonemeNotFound")){
					String[] arr = word.split("-");
					if(arr.length == 0 || arr.length == 1){
						phoneme = "PhonemeNotFound";
						phonemeList.add(phoneme);
						continue;
					}
					String phoneme1 = getPhoneme(arr[0]);
					String phoneme2 = getPhoneme(arr[1]);
					if(phoneme2.equals("PhonemeNotFound")){
						phoneme = "PhonemeNotFound";
						phonemeList.add(phoneme);
						continue;
					}
					phoneme = "";
					String[] splitPhoneme1 = phoneme1.split("#");
					String[] splitPhoneme2 = phoneme2.split("#");
					for(int j=0;j<splitPhoneme1.length;j++){
						for(int k=0;k<splitPhoneme2.length;k++){
							phoneme = phoneme + splitPhoneme1[j] + "_" + splitPhoneme2[k] + "#";
						}
					}
					phoneme = phoneme.substring(0, phoneme.length()-1);

				}
			}			

			if(phoneme.equals("PhonemeNotFound") && word.contains("'")){
				String[] arr = word.split("'");
				//System.out.println(arr.length);
				if(arr.length == 0){
					phoneme = "PhonemeNotFound";
					phonemeList.add(phoneme);
					continue;
				}
				String rootPhoneme = getPhoneme(arr[0]);
				if(rootPhoneme.equals("PhonemeNotFound")){
					phoneme = "PhonemeNotFound";
					phonemeList.add(phoneme);
					continue;
				}
				phoneme = "";
				String[] splitRootPhoneme = rootPhoneme.split("#");
				for(int j=0;j<splitRootPhoneme.length;j++){
					phoneme = phoneme + splitRootPhoneme[j] + "_Z#";
				}
				phoneme = phoneme.substring(0, phoneme.length()-1);
			}
			phonemeList.add(phoneme);
		}
		return phonemeList;
	}

	public int findLinePause(ArrayList<String> currentLine){
		int linePause = -1;
		for(int i=0;i<currentLine.size();i++){
			if(currentLine.get(i).contains(",") || currentLine.get(i).contains(".") || currentLine.get(i).contains(";")){
				linePause = i;
			}
		}
		if(linePause == currentLine.size()-1){
			linePause = -1;
		}
		return linePause;
	}

	public ArrayList<String> getArrayListFromStr(String sCurrentLine){
		ArrayList<String> outList = new ArrayList<String>(); 
		String[] arr = sCurrentLine.split(" ");
		for(int i=0;i<arr.length;i++){
			outList.add(arr[i]);
		}
		return outList;
	}

	public ArrayList<String> getArrayListFromBlankStr(){
		ArrayList<String> outList = new ArrayList<String>();
		outList.add(" ");
		return outList;
	}

	public String processLine(String currentLine){
		currentLine = currentLine.trim();
		String intermediateStr = cleanPrefix(currentLine);
		String outLine = cleanSuffix(intermediateStr);
		return outLine;
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
			System.out.println("Populated Phoneme List : " + phonemeMap.size());
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

	public void printAllPoems(){
		for(int i=0;i<poemList.size();i++){
			AnnotatedPoem poem = poemList.get(i);
			System.out.println("\n\nFile Name : " + poem.getFileName());
			System.out.println("Title : " + poem.getTitle());
			System.out.println("Author : " + poem.getAuthor());
			System.out.println(poem.printResult());
			//System.out.println("Poem : ");
			//poem.printContent();
			//System.out.println("Phonemes : ");
			//poem.printPhonemes();
		}
	}

	public void writeAnnotatedPoems(String str){
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			for(int i=0;i<poemList.size();i++){
				AnnotatedPoem poem = poemList.get(i);
				HashMap<Integer, Integer> paraPause = poem.getParaPause();
				File file = new File(basePath + str + poem.getFileName());

				if (!file.exists()) {
					file.createNewFile();
				}

				fw = new FileWriter(file.getAbsoluteFile());
				bw = new BufferedWriter(fw);

				//bw.write("\nFile Name : " + poem.getFileName());
				bw.write("\nTitle : " + poem.getTitle());
				bw.write("\nAuthor : " + poem.getAuthor() + "\n\n");
				//bw.write(poem.printResult() + "\n");
				int incrementor = 1;
				for(int j=0;j<poem.getContent().size();j++){

					if(paraPause.containsKey(j+5+incrementor)){
						incrementor++;
						bw.write("\n");
					}
					ArrayList<String> currentLine = poem.getContent().get(j);
					ArrayList<String> currentAnno = poem.getAnnotation().get(j);

					for(int r=0;r<currentLine.size();r++){
						bw.write(currentLine.get(r) + " ");
					}
					bw.write("\t\t");
					for(int s=0;s<currentAnno.size();s++){
						bw.write(currentAnno.get(s) + " ");
					}
					bw.write("\n");
				}
				bw.close();
				System.out.println(poem.getFileName() + " file written");
			}

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
			for(int i=0;i<poemList.size();i++){
				AnnotatedPoem poem = poemList.get(i);			
				bw.write(poem.getFileName() + ",");
				//bw.write(poem.getTitle() + ",");
				//bw.write(poem.getAuthor() + ",");
				bw.write(padOutput(poem.endRhymeScore) + ",");
				bw.write(padOutput(poem.internalRhymeScore) + ",");
				bw.write(padOutput(poem.eyeRhymeScore) + ",");
				bw.write(padOutput(poem.fullRhymeScore) + ",");
				bw.write(padOutput(poem.richRhymeScore) + ",");
				bw.write(padOutput(poem.identicalRhymeScore) + ",");
				bw.write(padOutput(poem.slantRhymeScore) + "\n");
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
	
	public String padOutput(double str){
		double newEl = (double)Math.round(str * 100000d) / 100000d;
		return(String.format("%.5f", newEl));
	}
	
}
