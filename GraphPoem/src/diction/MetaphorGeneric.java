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

public class MetaphorGeneric {

	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";

	MaxentTagger taggerStanford = null;
	DependencyParser parserStanford = null;
	Tokenizer tokenizer = null;
	POSTaggerME tagger = null;

	WordNet objWN = null;

	public static void main(String[] args) {
		MetaphorGeneric obj = new MetaphorGeneric();
		//obj.readInputFolderPoetryFoundationNew();
		obj.readInputAnnotatedPoFo();

	}

	public void readInputAnnotatedPoFo(){
		initializePOSModel();
		objWN = new WordNet();
		//objCN = new ConceptNet();

		File outFile = new File(basePath + "gentype_metaphor_type1.txt");
		File outFile2 = new File(basePath + "gentype_metaphor_anno.txt");
		FileWriter fw = null;
		FileWriter fw2 = null;
		BufferedWriter bw = null;
		BufferedWriter bw2 = null;
		try{
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			if (!outFile2.exists()) {
				outFile2.createNewFile();
			}

			fw = new FileWriter(outFile.getAbsoluteFile());
			fw2 = new FileWriter(outFile2.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw2 = new BufferedWriter(fw2);
		}catch(Exception e){
			e.printStackTrace();
		}

		File currentFile = new File(basePath + "type1_metaphor_anno_final.txt");
		//String fileName = currentFile.getName();
		//fileName = fileName.substring(0, fileName.length()-4);
		//File currentPOSFile = new File(basePath + "pos/" + fileName + ".pos");
		System.out.println("\nProcessing file " + " : " + currentFile);

		BufferedReader br = null;
		//BufferedReader brPOS = null;
		String sCurrentLine;
		//String sCurrentLinePOS;
		try{
			bw.write("\n\nFile " + " : " + currentFile + "\n");
			br = new BufferedReader(new FileReader(currentFile));
			//brPOS = new BufferedReader(new FileReader(currentPOSFile));
			String compositeLine = "";
			while ((sCurrentLine = br.readLine()) != null) {
				//sCurrentLinePOS = brPOS.readLine();

				String []arr = sCurrentLine.split("@");

				String associations = runStanfordParser(arr[0]);
				bw.write(associations);
				bw.flush();
			}

			br.close();
			br = null;
			currentFile = null;

			//System.out.println("associations=" + associations);
			//String processedPOSLine = processPOSLine(sCurrentLinePOS);

			//bw2.flush();

		} catch (Exception e) {
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

		try{

			bw.flush();
			bw2.flush();

			bw.close();
			bw2.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public void readInputFolderPoetryFoundationNew(){
		initializePOSModel();
		objWN = new WordNet();
		//objCN = new ConceptNet();
		String inputFolder = basePath + "opennlp_pos/";
		//String inputFolder = basePath + "opennlp_pos/";
		File inpFolder = new File(inputFolder);
		File[] children = inpFolder.listFiles();

		File outFile = new File(basePath + "gentype_metaphor_no_pronoun.txt");
		File outFile2 = new File(basePath + "gentype_metaphor_anno.txt");
		FileWriter fw = null;
		FileWriter fw2 = null;
		BufferedWriter bw = null;
		BufferedWriter bw2 = null;
		try{
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			if (!outFile2.exists()) {
				outFile2.createNewFile();
			}

			fw = new FileWriter(outFile.getAbsoluteFile());
			fw2 = new FileWriter(outFile2.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw2 = new BufferedWriter(fw2);
		}catch(Exception e){
			e.printStackTrace();
		}


		int count = -1;
		for(int loop=0; loop<children.length; loop++){
			count++;
			File currentFile = children[count];
			if(currentFile.isFile() && currentFile.getName().contains(".pos")){
				String fileName = currentFile.getName();
				fileName = fileName.substring(0, fileName.length()-4);
				//File currentPOSFile = new File(basePath + "pos/" + fileName + ".pos");
				System.out.println("\nProcessing file " + (count+1) + " : " + fileName);

				ArrayList<String> poem = new ArrayList<String>(); 
				BufferedReader br = null;
				//BufferedReader brPOS = null;
				String sCurrentLine;
				//String sCurrentLinePOS;
				try{
					bw.write("\n\nFile " + (count+1) + " : " + fileName + "\n");
					br = new BufferedReader(new FileReader(currentFile));
					//brPOS = new BufferedReader(new FileReader(currentPOSFile));
					String compositeLine = "";
					while ((sCurrentLine = br.readLine()) != null) {
						//sCurrentLinePOS = brPOS.readLine();

						if(sCurrentLine.trim().equals("")){
							continue;
						}
						if(sCurrentLine.trim().split(" ").length == 1){
							continue;
						}

						String currentLineWithoutPOS = removePOSTags(sCurrentLine);
						if(currentLineWithoutPOS.contains(".")){
							compositeLine = compositeLine + currentLineWithoutPOS.substring(0, currentLineWithoutPOS.indexOf(".")+1);
							poem.add(compositeLine);
							System.out.println(compositeLine);
							//bw.write(compositeLine + "\n");
							compositeLine = currentLineWithoutPOS.substring(currentLineWithoutPOS.indexOf(".")+2, currentLineWithoutPOS.length());
							if(compositeLine.contains(".")){
								String tmpStr = compositeLine;
								compositeLine = tmpStr.substring(0, tmpStr.indexOf(".")+1);
								poem.add(compositeLine);
								System.out.println(compositeLine);
								//bw.write(compositeLine + "\n");
								compositeLine = tmpStr.substring(tmpStr.indexOf(".")+2, tmpStr.length());
							}
						}else if(currentLineWithoutPOS.contains(";")){
							compositeLine = compositeLine + currentLineWithoutPOS.substring(0, currentLineWithoutPOS.indexOf(";")+1);
							poem.add(compositeLine);
							System.out.println(compositeLine);
							//bw.write(compositeLine + "\n");
							compositeLine = currentLineWithoutPOS.substring(currentLineWithoutPOS.indexOf(";")+2, currentLineWithoutPOS.length());
							if(compositeLine.contains(";")){
								String tmpStr = compositeLine;
								compositeLine = tmpStr.substring(0, tmpStr.indexOf(";")+1);
								poem.add(compositeLine);
								System.out.println(compositeLine);
								//bw.write(compositeLine + "\n");
								compositeLine = tmpStr.substring(tmpStr.indexOf(";")+2, tmpStr.length());
							}
						}else{
							compositeLine = compositeLine + currentLineWithoutPOS;
						}
					}

					br.close();
					br = null;
					currentFile = null;

					for(String poemLine : poem){
						String associations = runStanfordParser(poemLine);
						bw.write(associations);
					}
					bw.flush();
					//System.out.println("associations=" + associations);
					//String processedPOSLine = processPOSLine(sCurrentLinePOS);

					//bw2.flush();

				} catch (Exception e) {
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

			//writeToFile("poem_output" + (loop+1) + ".txt");
		}

		try{

			bw.flush();
			bw2.flush();

			bw.close();
			bw2.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		children = null;
		inpFolder = null;

	}

	public String removePOSTags(String currentLine){
		StringBuffer sb = new StringBuffer();
		String []lineArr = currentLine.split(" +");
		for(int i=0;i<lineArr.length;i++){
			if(lineArr[i].trim().equals("")){
				continue;
			}
			int indexOfSlash = lineArr[i].indexOf("/");
			String word = lineArr[i].substring(0,indexOfSlash);
			sb.append(word + " ");
		}

		return sb.toString();
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

	public String runStanfordParser(String text) {
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

		for(TypedDependency dep : list){
			if(dep.reln().toString().equals("dobj") || dep.reln().toString().equals("nsubj") || dep.reln().toString().equals("nsubjpass")){
				//System.out.println(dep.dep().toString() + "/" + dep.dep().index() + " :: " + dep.gov().toString() + "/" + dep.gov().index());
				String deps = dep.dep().toString();
				String depsPOS = deps.substring(deps.indexOf("/")+1, deps.length());
				deps = deps.substring(0, deps.indexOf("/"));
				String govs = dep.gov().toString();
				String govsPOS = govs.substring(govs.indexOf("/")+1, govs.length());
				govs = govs.substring(0, govs.indexOf("/"));
				//if(!convertPennTag(govsPOS).equals("pron") && !convertPennTag(depsPOS).equals("pron")){
				retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "@@" + convertPennTag(govsPOS) + "::" + convertPennTag(depsPOS) + "\n";
				//}
				//System.out.println(dep.reln().toString() + "::" + govs + "#" + deps);

			}
			//System.out.println(dep);
			//System.out.println(dep.gov().index());
		}

		//sb.append("\nReturn str :: " + retVal.toLowerCase());
		return retStr.toLowerCase();
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

}
