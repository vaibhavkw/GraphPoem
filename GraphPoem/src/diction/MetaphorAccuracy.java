package diction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

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

public class MetaphorAccuracy {

	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";
	Tokenizer tokenizer = null;
	POSTaggerME tagger = null;
	MaxentTagger taggerStanford = null;
	DependencyParser parserStanford = null;

	int tp = 0;
	int fp = 0;
	int fn = 0;
	int tn = 0;
	int errorCount = 0;
	
	ConceptNet objCN = null;
	WordNet objWN = null;
	CallPythonGlove objPy = null;

	public static void main(String[] args) {
		MetaphorAccuracy obj = new MetaphorAccuracy();

		obj.initializePOSModel();
		obj.readAnnotatedFile();
	}

	public void readAnnotatedFile(){
		objCN = new ConceptNet();
		objWN = new WordNet();
		objPy = new CallPythonGlove();
		File currentFile = new File(basePath + "type1_metaphor_anno_final.txt");
		BufferedReader br = null;
		String sCurrentLine;
		try{
			br = new BufferedReader(new FileReader(currentFile));
			int lineNumber = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				processLine(sCurrentLine);
				if(lineNumber == 719){
					break;
				}
			}

			br.close();
			br = null;
			currentFile = null;

			System.out.println("Error Count=" + (errorCount));
			System.out.println("Correct Count=" + (tp + tn));
			System.out.println("Incorrect Count=" + (fp + fn));
			System.out.println("Accuracy=" + (1.0*(tp+tn)/(tp+tn+fp+fn)));
			double precision = (1.0*tp)/(tp+fp);
			System.out.println("Precision=" + precision);
			double recall = (1.0*tp)/(tp+fn);
			System.out.println("Recall=" + recall);
			System.out.println("F-measure=" + (2.0*precision*recall)/(precision+recall));

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

	public void processLine(String str){
		String[] strArr = str.split("@");
		String line = strArr[0];
		int position = Integer.parseInt(strArr[1]);
		String anno = strArr[2];
		if(anno.equals("s")){
			return;
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
		boolean metaphor = false;
		try{
			System.out.println("\n" + str);
			String newNouns = runStanfordParser(line, processWord(noun1), processWord(noun2));
			String newNoun1 = newNouns.substring(0, newNouns.indexOf("#"));
			String newNoun2 = newNouns.substring(newNouns.indexOf("#")+1, newNouns.length());
			System.out.println("Processing Type1: " + noun1 + " : " + verb + " : " + noun2);
			//metaphor = checkForType1MetaphorCCO(newNoun1, newNoun2, verb);
			metaphor = checkForType1Metaphor(noun1, noun2, verb);
		}catch(Exception e){
			e.printStackTrace();
			errorCount++;
			return;
		}
		if(anno.equals("y") && metaphor == true){
			//tp++;
			tn++;
		}
		if(anno.equals("n") && metaphor == false){
			//tn++;
			tp++;
		}
		if(anno.equals("y") && metaphor == false){
			System.out.println("Wrong Line:" + str);
			//fn++;
			fp++;
		}
		if(anno.equals("n") && metaphor == true){
			System.out.println("Wrong Line:" + str);
			//fp++;
			fn++;
		}
	}

	public boolean checkForType1MetaphorSim(String noun1, String noun2, String verb) throws Exception {
		System.out.println("Processing Type1: " + noun1 + " : " + verb + " : " + noun2);
		String similarity = objPy.getProp(noun1+"@"+noun2);
		System.out.println("Similarity " + similarity);

		if(Double.parseDouble(similarity) < 0.3455){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean checkForType1MetaphorCCO(String noun1, String noun2, String verb) throws Exception {
		boolean overlap = false;
		//noun1 = "dddfffff";
		
		System.out.println("Processing Type1: " + noun1 + " : " + verb + " : " + noun2);
		
		LinkedHashMap<String, String> hypernymList1 = objWN.getHypernyms(noun1, "noun");
		LinkedHashMap<String, String> hypernymList2 = objWN.getHypernyms(noun2, "noun");
		Iterator it = hypernymList1.keySet().iterator();
		while(it.hasNext()){
			String hypernym = (String) it.next();
			//System.out.println(hypernym);
			if(hypernymList2.containsKey(hypernym)){
				overlap = true;
			}
		}
		System.out.println("Class Overlap:" + overlap);
		return !overlap;
	}

	public boolean checkForType1Metaphor(String noun1, String noun2, String verb) throws Exception {
		//noun1 = "hackles";
		//noun2 = "fear";
		System.out.println("Processing Type1: " + noun1 + " : " + verb + " : " + noun2);

		String classOfNoun1 = objWN.getConcreteAbstract(noun1);
		String classOfNoun2 = objWN.getConcreteAbstract(noun2);

		System.out.println(noun1 + ":" + classOfNoun1 + "    " + noun2 + ":" + classOfNoun2);

		/*if(!similarity.equals("Loading saved model")){
			double sim = Double.parseDouble(similarity);
			if(sim<0.25 && sim>0){
				System.out.println("Metaphor by Sim");
				return true;
			}
		}*/

		/*if(!classOfNoun1.equals(classOfNoun2)){
			System.out.println("Classes do not overlap");
			return true;
		}*/

		if(classOfNoun1.equals("abstract") && classOfNoun2.equals("abstract")){
			return false;
		}

		if(classOfNoun1.equals("abstract") && classOfNoun2.equals("concrete")){
			//sb.append("\n\nLine : " + origStr);
			//System.out.println("\nProcessing Type1: " + noun1 + " : " + verb + " : " + noun2);
			//System.out.println(noun1 + ":" + classOfNoun1 + "    " + noun2 + ":" + classOfNoun2);
			System.out.println("Metaphor by Abstract-Concrete relation");
			return true;
		}

		/*if(classOfNoun1.equals("concrete") && classOfNoun2.equals("abstract")){
			return false;
		}*/
		
		boolean overlap = false;
		LinkedHashMap<String, String> hypernymList1 = objWN.getHypernyms(noun1, "noun");
		LinkedHashMap<String, String> hypernymList2 = objWN.getHypernyms(noun2, "noun");
		Iterator it = hypernymList1.keySet().iterator();
		while(it.hasNext()){
			String hypernym = (String) it.next();
			//System.out.println(hypernym);
			if(hypernymList2.containsKey(hypernym)){
				overlap = true;
			}
		}
		System.out.println("Class Overlap:" + overlap);

		if(!overlap){
			//System.out.println("Metaphor by CCO relation");
			//sb.append("\n\nLine : " + origStr);
			//System.out.println("\nProcessing Type1: " + noun1 + " : " + verb + " : " + noun2);
			//System.out.println(noun1 + ":" + classOfNoun1 + "    " + noun2 + ":" + classOfNoun2);
			//System.out.println("Hypernym Class Overlap:" + overlap);
			System.out.println("Metaphor by CCO relation");
			return true;
		}

		//String similarity = objPy.getProp(noun1+"@"+noun2);
		//System.out.println("Similarity " + similarity);

		String concepts1 = objCN.getProp(noun1);
		String concepts2 = objCN.getProp(noun2);

		System.out.println("ConceptNet " + noun1 + " : " + concepts1);
		System.out.println("ConceptNet " + noun2 + " : " + concepts2);

		boolean conceptOverlap = checkConceptOverlap(concepts1, concepts2);
		System.out.println("Concept Overlap : " + conceptOverlap);

		if(!conceptOverlap && !concepts1.equals("") && !concepts2.equals("")){
			System.out.println("Metaphor by Concept Overlap");
			return true;
		}

		//if(classOfNoun1.equals("abstract") && classOfNoun2.equals("abstract")){
		//}

		return false;
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
