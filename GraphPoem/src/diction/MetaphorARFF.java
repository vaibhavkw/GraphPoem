package diction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.sun.glass.ui.Size;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import concordance.WordNet;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class MetaphorARFF {

	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";
	Tokenizer tokenizer = null;
	POSTaggerME tagger = null;
	int correctCount = 0;
	int incorrectCount = 0;
	ConceptNet objCN = null;
	WordNet objWN = null;
	MaxentTagger taggerStanford = null;
	DependencyParser parserStanford = null;
	CallPythonGlove objPy = null;
	BNCCorpus objBNC = null;
	GloveGigaWord objGlove = null;

	double corpusSize;
	public static int dimensions = 100;

	public static void main(String[] args) {
		MetaphorARFF obj = new MetaphorARFF();

		//obj.printRepeatedOutput();
		obj.readAnnotatedFile();
		//obj.readShutovaAnnotatedFile();
		//obj.readAnnotatedFileTrofi();
		//obj.readParsedFileTrofi();

	}

	public void printRepeatedOutput(){
		for(int i=1;i<=dimensions;i++){
			System.out.println("@attribute dim" + i + " REAL");
		}
	}

	public void readAnnotatedFile(){
		initializePOSModel();
		objCN = new ConceptNet();
		objWN = new WordNet();
		objPy = new CallPythonGlove();
		objBNC = new BNCCorpus();
		objGlove = new GloveGigaWord();
		corpusSize = objBNC.getTotalCorpusSize();
		
		objGlove.initProp();
		
		File currentFile = new File(basePath + "type1_metaphor_anno_final.txt");
		File outFile = new File(basePath + "type1_metaphor.arff");
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		String sCurrentLine;
		try{
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			fw = new FileWriter(outFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			br = new BufferedReader(new FileReader(currentFile));
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				String outLine = processLine(sCurrentLine);
				if(!outLine.equals("SKIP")){
					bw.write("\n" + outLine);
				}
				if(lineNumber == 719){
					break;
				}
				bw.flush();
			}
			bw.close();
			br.close();
			br = null;
			bw = null;
			fw = null;
			currentFile = null;
			outFile = null;

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String processLine(String str){
		String retStr = "";
		String[] strArr = str.split("@");
		String line = strArr[0];
		int position = Integer.parseInt(strArr[1]);
		String anno = strArr[2];
		if(anno.equals("s")){
			return "SKIP";
		}
		if(anno.contains("(")){
			int indexOfStart = anno.indexOf("(");
			anno = anno.substring(indexOfStart+1, indexOfStart+2);
		}
		String[] lineArr = line.split(" ");
		String[] linePOSArr = getTaggedLine(line);		
		String noun1 = "";
		String noun2 = "";
		String verb = "";
		for(int i=0;i<=3;i++){
			if(i==0){
				noun1 = lineArr[position+i-1];
			}
			if(i==1){
				verb = lineArr[position+i-1];
			}
			if(i==2 && convertPennTag(linePOSArr[position+i-1]).equals("noun")){
				noun2 = lineArr[position+i-1];
				break;
			}
			if(i==3 && convertPennTag(linePOSArr[position+i-1]).equals("noun")){
				noun2 = lineArr[position+i-1];
			}
		}
		try{
			System.out.println("\n" + str);
			//changes for parser inclusion
			String newNouns = runStanfordParser(line, processWord(noun1), processWord(noun2));
			System.out.println("newNouns=" + newNouns);
			String newNoun1 = newNouns.substring(0, newNouns.indexOf("#"));
			String newNoun2 = newNouns.substring(newNouns.indexOf("#")+1, newNouns.length());
			System.out.println("Processing Type1: " + noun1 + " : " + verb + " : " + noun2);
			//
			//retStr = makeStringType1(newNoun1, newNoun2, verb);
			retStr = makeStringType1Trofi(newNoun1, newNoun2);
			if(anno.equals("y")){
				retStr = retStr + "1";
			}else{
				retStr = retStr + "0";
			}
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
			return "ERROR";
		}
		return retStr;
	}

	public String makeStringType1(String noun1, String noun2, String verb) {
		StringBuffer sb = new StringBuffer();
		System.out.println("Processing Type1: " + noun1 + " : " + verb + " : " + noun2);

		sb.append("'" + noun1 + " " + verb + " " + noun2 + "',"); //param 1 Phrase

		/*String classOfNoun1 = "";
		String classOfNoun2 = "";

		try{
			classOfNoun1 = objWN.getConcreteAbstract(noun1);			
		}catch(Exception e){
			e.printStackTrace();
			classOfNoun1 = "?";
		}

		try{
			classOfNoun2 = objWN.getConcreteAbstract(noun2);
		}catch(Exception e){
			e.printStackTrace();
			classOfNoun2 = "?";
		}

		sb.append(classOfNoun1 + ","); //param 2 Class for Noun 1 abstract/concrete
		sb.append(classOfNoun2 + ","); //param 3 Class for Noun 2 abstract/concrete

		String classOverlap;
		if(classOfNoun1.equals("?") || classOfNoun2.equals("?")){
			classOverlap = "?";
		}else{
			if(classOfNoun1.equals(classOfNoun2)){
				classOverlap = "true";
			}else{
				classOverlap = "false";
			}
		}
		sb.append(classOverlap + ","); //param 4 Class overlap abstract/concrete

		String overlap = "false";
		try{
			LinkedHashMap<String, String> hypernymList1 = objWN.getHypernyms(noun1, "noun");
			LinkedHashMap<String, String> hypernymList2 = objWN.getHypernyms(noun2, "noun");
			Iterator it = hypernymList1.keySet().iterator();
			while(it.hasNext()){
				String hypernym = (String) it.next();
				//System.out.println(hypernym);
				if(hypernymList2.containsKey(hypernym)){
					overlap = "true";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			overlap = "?";
		}

		sb.append(overlap + ","); //param 5 Concrete Class Overlap
		 */
		String concepts1 = objCN.getProp(noun1);
		String concepts2 = objCN.getProp(noun2);
		System.out.println(concepts1 + ":" + concepts2);

		//System.out.println("ConceptNet " + noun1 + " : " + concepts1);
		//System.out.println("ConceptNet " + noun2 + " : " + concepts2);

		String conceptOverlap = String.valueOf(checkConceptOverlap(concepts1, concepts2));

		if(concepts1.equals(noun1 + ",") || concepts2.equals(noun2 + ",")){
			conceptOverlap = "?";
		}

		sb.append(conceptOverlap + ","); //param 6 Conceptnet Overlap

		String similarity = objPy.getProp(noun1+"@"+noun2);
		if(similarity.equals("Loading saved model")){
			similarity = "?";
		}
		sb.append(similarity + ","); //param 7 Similarity index

		String pmi = getPMI(noun1, noun2);
		if(pmi.equals("-Infinity") || pmi.equals("NaN")){
			pmi = "?";
		}
		sb.append(pmi + ","); //param 8 PMI

		//String ret1 = objPy.getProp2(noun1);
		//String ret2 = objPy.getProp2(noun2);

		String retStr = "";

		String ret1 = objPy.getProp2(noun1);
		String ret2 = objPy.getProp2(noun2);

		if(ret1.equals("") || ret2.equals("")){
			retStr = getBlankArray();
		}else{
			//ret1 = ret1.substring(0, ret1.length()-1);
			//ret1 = "," + ret1;

			//ret2 = ret2.substring(0, ret2.length()-1);
			//ret2 = "," + ret2;

			retStr = objPy.getVectorDiff(ret1, ret2);
		}
		sb.append(retStr); //param 9-108 Vector difference

		/*ret1 = ret1.substring(1, ret1.length());
		sb.append(ret1); // param 109-208 Vector for Noun 1

		sb.append(ret2 + ","); // param 209-308 Vector for Noun 2
		 */		
		return sb.toString();
	}

	public void readShutovaAnnotatedFile(){

		objCN = new ConceptNet();
		objWN = new WordNet();
		objPy = new CallPythonGlove();
		objBNC = new BNCCorpus();
		objGlove = new GloveGigaWord();
		corpusSize = objBNC.getTotalCorpusSize();
		File currentFile = new File(basePath + "shutova_verb_labels.txt");
		File outFile = new File(basePath + "type1_metaphor.arff");
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		String sCurrentLine;		
		try{
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			fw = new FileWriter(outFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			br = new BufferedReader(new FileReader(currentFile));
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				String outLine = processLineSh(sCurrentLine);
				if(!outLine.equals("SKIP")){
					bw.write("\n" + outLine);
				}
				if(lineNumber == 647){
					break;
				}
				bw.flush();
			}
			bw.close();
			br.close();
			br = null;
			bw = null;
			fw = null;
			currentFile = null;
			outFile = null;

		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public String processLineSh(String str){
		String retStr = "";
		String[] strArr = str.split(" ");			
		String noun = strArr[0];
		String verb = strArr[1];
		String anno = strArr[2];

		try{
			System.out.println("\n" + str);
			retStr = makeStringType1Sh(noun, verb);
			retStr = retStr + anno;
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
			return "ERROR";
		}
		return retStr;
	}

	public String makeStringType1Sh(String noun, String verb) {
		StringBuffer sb = new StringBuffer();
		System.out.println("Processing Type1: " + noun + " : " + verb);

		sb.append("'" + noun + " " + verb + "',"); //param 1 Phrase

		String concepts1 = objCN.getProp(noun);
		String concepts2 = objCN.getProp(verb);
		System.out.println(concepts1 + ":" + concepts2);

		//System.out.println("ConceptNet " + noun1 + " : " + concepts1);
		//System.out.println("ConceptNet " + noun2 + " : " + concepts2);

		String conceptOverlap = String.valueOf(checkConceptOverlap(concepts1, concepts2));

		if(concepts1.equals(noun + ",") || concepts2.equals(verb + ",")){
			conceptOverlap = "?";
		}

		sb.append(conceptOverlap + ","); //param 2 Conceptnet Overlap

		String similarity = objPy.getProp(noun+"@"+verb);
		if(similarity.equals("Loading saved model")){
			similarity = "?";
		}
		sb.append(similarity + ","); //param 3 Similarity index

		String pmi = getPMI(noun, verb);
		if(pmi.equals("-Infinity") || pmi.equals("NaN")){
			pmi = "?";
		}
		sb.append(pmi + ","); //param 4 PMI

		String retStr = "";

		String ret1 = objPy.getProp2(noun);
		String ret2 = objPy.getProp2(verb);

		if(ret1.equals("") || ret2.equals("")){
			retStr = getBlankArray();
		}else{
			//ret1 = ret1.substring(0, ret1.length()-1);
			//ret1 = "," + ret1;

			//ret2 = ret2.substring(0, ret2.length()-1);
			//ret2 = "," + ret2;

			retStr = objPy.getVectorDiff(ret1, ret2);
		}
		sb.append(retStr); //param 5-104 Vector difference
		/*
		if(ret1.equals("")){
			ret1 = getBlankArray();
			ret1 = "," + ret1;
			ret1 = ret1.substring(0, ret1.length()-1);
		}
		if(ret2.equals("")){
			ret2 = getBlankArray();
		}

		ret1 = ret1.substring(1, ret1.length());
		sb.append(ret1); // param 105-204 Vector for Noun 1

		sb.append(ret2 + ","); // param 205-304 Vector for Noun 2
		 */
		return sb.toString();
	}

	public void readAnnotatedFileTrofi(){
		initializePOSModel();
		objCN = new ConceptNet();
		objWN = new WordNet();
		objPy = new CallPythonGlove();
		objBNC = new BNCCorpus();
		objGlove = new GloveGigaWord();
		corpusSize = objBNC.getTotalCorpusSize();
		File currentFile = new File(basePath + "trofi/TroFiExampleBase.txt");
		File outFile = new File(basePath + "type1_metaphor.arff");
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		String sCurrentLine;		
		try{
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			fw = new FileWriter(outFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			br = new BufferedReader(new FileReader(currentFile));
			int lineNumber = 0;
			String anchor = "";
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.equals("") || sCurrentLine.equals("********************") || sCurrentLine.equals("*literal cluster*") || sCurrentLine.equals("*nonliteral cluster*")){
					continue;
				}
				if(sCurrentLine.substring(0, 3).equals("***")){
					anchor = sCurrentLine.substring(3, sCurrentLine.length()-3);
					System.out.println(anchor);
					continue;
				}
				String outLine = prepareLineTrofi(sCurrentLine, anchor);
				if(!outLine.equals("SKIP")){
					lineNumber++;
					System.out.println("Processing line " + lineNumber);
					bw.write("\n" + outLine);
				}
				/*if(lineNumber == 243){
					break;
				}*/
				bw.flush();
			}
			bw.close();
			br.close();
			br = null;
			bw = null;
			fw = null;
			currentFile = null;
			outFile = null;

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String prepareLineTrofi(String str, String anchor){
		String retStr = "";
		String[] strArr = str.split("\t");
		String line = strArr[2].trim();
		String anno = strArr[1].trim();
		if(anno.equals("U")){
			return "SKIP";
		}

		String subjects = runStanfordParserTrofi(line, anchor);
		if(subjects.equals("Not Found")){
			return "SKIP";
		}
		//String verb = subjects.substring(0, subjects.indexOf("#"));
		//String noun = subjects.substring(subjects.indexOf("#")+1, subjects.length());

		try{
			retStr = subjects;//makeStringType1Sh(noun, verb);
			if(anno.equals("N")){
				retStr = retStr + "#1";
			}else{
				retStr = retStr + "#0";
			}
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
			return "ERROR";
		}
		return retStr;
	}

	public String runStanfordParserTrofi(String text, String anchor) {
		//StringBuffer sb = new StringBuffer();
		//sb.append("\n" + anchor + " :: " + text);
		ArrayList<TypedDependency> list = new ArrayList<TypedDependency>(); 
		String retStr = "";//noun1 + "#" + noun2;
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		for (List<HasWord> sentence : tokenizer) {
			List<TaggedWord> tagged = taggerStanford.tagSentence(sentence);
			GrammaticalStructure gs = parserStanford.predict(tagged);

			List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();

			for(TypedDependency dep : tdl){
				list.add(dep);
				//sb.append("\n" + dep);
			}
		}
		
		String retVal = checkParserRelation(list, "dobj", anchor);		
		if(retVal.equals("Not Found")){
			retVal = checkParserRelation(list, "nsubj", anchor);
			if(retVal.equals("Not Found")){
				retVal = checkParserRelation(list, "nsubjpass", anchor);
				if(retVal.equals("Not Found")){
					retVal = checkParserRelation(list, "amod", anchor);
					if(retVal.equals("Not Found")){
						retVal = checkParserRelation(list, "nsubj:xsubj", anchor);
						if(retVal.equals("Not Found")){
							retVal = checkParserRelation(list, "auxpass", anchor);
							if(retVal.equals("Not Found")){
								retVal = checkParserRelation(list, "nmod", anchor);
								if(retVal.equals("Not Found")){
									//sb.append("\nNo match found");
									return "Not Found";
								}
							}
						}
					}
				}
			}
		}else{
			String word = retVal.substring(retVal.indexOf("#")+1, retVal.length());
			String retVal2 = checkParserRelation(list, "nmod:of", word);
			if(!retVal2.equals("Not Found")){
				String word2 = retVal2.substring(retVal2.indexOf("#")+1, retVal2.length());
				//System.out.println("New:" + retVal + "-->" + word2);
				retVal = anchor + "#" + word2;				
			}
		}
		//sb.append("\nReturn str :: " + retVal.toLowerCase());
		return retVal.toLowerCase();
	}
	
	public String checkParserRelation(ArrayList<TypedDependency> list, String relation, String anchor) {
		String retStr = "";
		int match = 0;
		for(TypedDependency dep : list){
			match = 0;
			boolean relEq = false;
			if(relation.equals("nmod")){
				relEq = dep.reln().toString().contains(relation);
			}else{
				relEq = dep.reln().toString().equals(relation);
			}
			if(relEq){
				//System.out.println(dep.dep().toString() + "/" + dep.dep().index() + " :: " + dep.gov().toString() + "/" + dep.gov().index());
				//System.out.println(dep.gov().toString() + "/" + dep.gov().index() + " :: " + dep.dep().toString() + "/" + dep.dep().index());
				String deps = dep.dep().toString();
				deps = deps.substring(0, deps.indexOf("/"));
				String govs = dep.gov().toString();
				String govsPOS = govs.substring(govs.indexOf("/")+1, govs.length());
				govs = govs.substring(0, govs.indexOf("/"));
				if(relation.equals("amod")){
					String newDeps = objWN.getStemWord(deps, "verb");
					if(newDeps.equalsIgnoreCase(anchor)){
						match++;
					}
				}else{
					String newGovs = objWN.getStemWord(govs, "verb");
					if(newGovs.equalsIgnoreCase(anchor)){
						match++;
					}
				}
				/*if(govs.equalsIgnoreCase(anchor)){
					match++;
				}*/
				//System.out.println(match);
				if(match>0){
					if(relation.equals("amod")){
						retStr = anchor + "#" + govs;
					}else{
						retStr = anchor + "#" + deps;
					}
					//System.out.println(text);
					//System.out.println("Return str::" + retStr + "\n");
					//sb.append("\nReturn str :: " + retStr);
					/*if(!convertPennTag(govsPOS).equals("noun")){
						retStr = anchor + "#" + anchor;
					}*/
					return retStr;
				}
			}
			//System.out.println(dep);
			//System.out.println(dep.gov().index());
		}
		return "Not Found";
	}
	
	public void readParsedFileTrofi(){
		//initializePOSModel();
		objCN = new ConceptNet();
		objWN = new WordNet();
		objPy = new CallPythonGlove();
		objBNC = new BNCCorpus();
		objGlove = new GloveGigaWord();
		corpusSize = objBNC.getTotalCorpusSize();
		
		objGlove.initProp();
		
		File currentFile = new File(basePath + "trofi/trofi_python_final.txt");
		File outFile = new File(basePath + "type1_metaphor.arff");
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		String sCurrentLine;		
		try{
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			fw = new FileWriter(outFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			br = new BufferedReader(new FileReader(currentFile));
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				String outLine = processLineTrofi(sCurrentLine);
				if(!outLine.equals("SKIP")){
					lineNumber++;
					System.out.println("Processing line " + lineNumber);
					bw.write("\n" + outLine);
				}
				/*if(lineNumber == 243){
					break;
				}*/
				bw.flush();
			}
			bw.close();
			br.close();
			br = null;
			bw = null;
			fw = null;
			currentFile = null;
			outFile = null;

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public String processLineTrofi(String str){
		String retStr = "";
		
		String[] strArr = str.split("#");
		String verb = strArr[0].trim();
		String noun = strArr[1].trim();
		String anno = strArr[2].trim();
		
		try{
			retStr = makeStringType1Trofi(noun, verb);
			retStr = retStr + anno;
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
			return "ERROR";
		}
		return retStr;
	}
	
	public String makeStringType1Trofi(String noun, String verb) {
		StringBuffer sb = new StringBuffer();
		System.out.println("Processing Type1: " + noun + " : " + verb);

		sb.append("'" + noun + " " + verb + "',"); //param 1 Phrase

		String concepts1 = objCN.getProp(noun);
		String concepts2 = objCN.getProp(verb);
		System.out.println(concepts1 + ":" + concepts2);

		//System.out.println("ConceptNet " + noun1 + " : " + concepts1);
		//System.out.println("ConceptNet " + noun2 + " : " + concepts2);

		String conceptOverlap = String.valueOf(checkConceptOverlap(concepts1, concepts2));

		if(concepts1.equals(noun + ",") || concepts2.equals(verb + ",")){
			conceptOverlap = "?";
		}

		sb.append(conceptOverlap + ","); //param 2 Conceptnet Overlap

		String ret1 = objGlove.getProp(noun);
		String ret2 = objGlove.getProp(verb);
		
		String similarity = "";
		if(ret1.equals("") || ret2.equals("")){
			similarity = "?";
		}else{
			similarity = objPy.cosineSimilarity(ret1, ret2);
		}
		
		//String similarity = objPy.getProp(noun+"@"+verb);
		
		if(similarity.equals("Loading saved model")){
			similarity = "?";
		}
		sb.append(similarity + ","); //param 3 Similarity index

		String pmi = getPMI(noun, verb);
		if(pmi.equals("-Infinity") || pmi.equals("NaN")){
			pmi = "?";
		}
		sb.append(pmi + ","); //param 4 PMI

		String retStr = "";

		//String ret1 = objPy.getProp2(noun);
		//String ret2 = objPy.getProp2(verb);

		if(ret1.equals("") || ret2.equals("")){
			retStr = getBlankArray();
		}else{
			//ret1 = ret1.substring(0, ret1.length()-1);
			//ret1 = "," + ret1;

			//ret2 = ret2.substring(0, ret2.length()-1);
			//ret2 = "," + ret2;

			retStr = objPy.getVectorDiff(ret1, ret2);
		}
		sb.append(retStr); //param 5-104 Vector difference
		/*
		if(ret1.equals("")){
			ret1 = getBlankArray();
			ret1 = "," + ret1;
			ret1 = ret1.substring(0, ret1.length()-1);
		}
		if(ret2.equals("")){
			ret2 = getBlankArray();
		}

		ret1 = ret1.substring(1, ret1.length());
		sb.append(ret1); // param 105-204 Vector for Noun 1

		sb.append(ret2 + ","); // param 205-304 Vector for Noun 2
		 */
		return sb.toString();
	}

	public String getBlankArray(){
		String retArr = "";
		for(int i=0;i<dimensions;i++){
			retArr = retArr + "?,";
		}
		return retArr;
	}

	public String getPMI(String noun1, String noun2){
		String ret = "";
		String compositeStr = objBNC.searchCompositeQuery(noun1 + " " + noun2);
		System.out.println("compositeStr=" + compositeStr);
		String []arr = compositeStr.split("@");
		double freqA = Double.parseDouble(arr[0]);
		double freqB = Double.parseDouble(arr[1]);
		double freqAB = Double.parseDouble(arr[2]);
		double pmi = Math.log(((freqAB*corpusSize)/(freqA*freqB*2))) / (Math.log(2));
		System.out.println("pmi=" + pmi);
		ret = String.valueOf(pmi);
		return ret;
	}

	public boolean checkConceptOverlap(String str1, String str2){
		boolean overlap = false;
		HashMap<String, String> map = new HashMap<String, String>(); 
		String[] strArr1 = str1.split(",");
		String[] strArr2 = str2.split(",");
		for(String word : strArr1){
			map.put(word, "");
		}
		String overlapList = "";
		for(String word : strArr2){
			if(map.containsKey(word)){
				overlap = true;
				overlapList = overlapList + word + ", ";
			}
		}
		if(overlap){
			System.out.println("Overlap List= " + overlapList);
		}
		return overlap;
	}

	public String[] getTaggedLine(String currentLine){
		StringBuffer sb = new StringBuffer();
		currentLine = currentLine.toLowerCase();
		//currentLine = currentLine.replaceAll("--", " -- ");
		String sent[] = tokenizer.tokenize(currentLine);
		String tags[] = tagger.tag(sent);
		return tags;
	}

	public void initializePOSModel(){
		InputStream modelIn = null;
		InputStream tokenModelIn = null;

		try {
			tokenModelIn = new FileInputStream(basePath + "opennlp_models/en-token.bin");
			modelIn = new FileInputStream(basePath + "opennlp_models/en-pos-maxent.bin");
			String modelPath = DependencyParser.DEFAULT_MODEL;
			String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";

			taggerStanford = new MaxentTagger(taggerPath);
			parserStanford = DependencyParser.loadFromModelFile(modelPath);

			POSModel model = new POSModel(modelIn);
			TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);

			tokenizer = new TokenizerME(tokenModel);
			tagger = new POSTaggerME(model);

		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String convertPennTag(String tag){
		tag = tag.toUpperCase();
		if(tag.equals("NN") || tag.equals("NNS") || tag.equals("NNP") || tag.equals("NNPS")){
			return "noun";
		}
		if(tag.equals("VB") || tag.equals("VBD") || tag.equals("VBG") || tag.equals("VBN") || tag.equals("VBP") || tag.equals("VBZ")){
			return "verb";
		}
		if(tag.equals("JJ") || tag.equals("JJR") || tag.equals("JJS")){
			return "adj";
		}
		if(tag.equals("RB") || tag.equals("RBR") || tag.equals("RBS") || tag.equals("WRB")){
			return "adv";
		}
		if(tag.equals("DT") || tag.equals("PDT") || tag.equals("WDT")){
			return "det";
		}
		if(tag.equals("PRP") || tag.equals("PRP$") || tag.equals("WP") || tag.equals("WP$")){
			return "pron";
		}
		return tag.toLowerCase();
	}

	public String processWord(String word){
		String ret = word;
		ret = ret.replaceAll("\"", "");
		ret = ret.replaceAll("'", "");
		return ret;
	}

	public String runStanfordParser(String text, String noun1, String noun2) {
		ArrayList<TypedDependency> list = new ArrayList<TypedDependency>(); 
		String retStr = noun1 + "#" + noun2;
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		for (List<HasWord> sentence : tokenizer) {
			List<TaggedWord> tagged = taggerStanford.tagSentence(sentence);
			GrammaticalStructure gs = parserStanford.predict(tagged);

			List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();

			for(TypedDependency dep : tdl){
				list.add(dep);
			}
		}
		for(TypedDependency dep : list){
			int match = 0;
			if(dep.reln().toString().equals("nsubj")/* || dep.reln().toString().equals("nsubjpass")*/){
				System.out.println(dep.dep().toString() + "/" + dep.dep().index() + " :: " + dep.gov().toString() + "/" + dep.gov().index());
				String deps = dep.dep().toString();
				deps = deps.substring(0, deps.indexOf("/"));
				String govs = dep.gov().toString();
				String govsPOS = govs.substring(govs.indexOf("/")+1, govs.length());
				govs = govs.substring(0, govs.indexOf("/"));
				if(deps.equalsIgnoreCase(noun1) || deps.equalsIgnoreCase(noun2)){
					match++;
				}
				if(govs.equalsIgnoreCase(noun1) || govs.equalsIgnoreCase(noun2)){
					match++;
				}
				//System.out.println(match);
				if(match>0){
					retStr = deps + "#" + govs;
					if(!convertPennTag(govsPOS).equals("noun")){
						retStr = noun1 + "#" + noun2;
					}
					break;
				}
			}
			//System.out.println(dep);
			//System.out.println(dep.gov().index());
		}
		return retStr;
	}

}
