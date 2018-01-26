package diction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import concordance.WordNet;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class MetaphorGeneric {

	public static String basePath = System.getProperty("user.dir") + "/resources/diction/";

	LexicalizedParser lp;
	TokenizerFactory<CoreLabel> tokenizerFactory;
	GrammaticalStructureFactory gsf;

	WordNet objWN = null;
	GloveGigaWord objGlove = null;
	BNCCorpus objBNC = null;
	CallPythonGlove objPy = null;
	ConceptNet objCN = null;

	double corpusSize;
	public static int dimensions = 100;

	public static void main(String[] args) {
		MetaphorGeneric obj = new MetaphorGeneric();
		//obj.readInputFolderPoetryFoundationNew();
		obj.readInputAnnotatedPoFo();

	}

	public void preInit(){
		initializePOSModelPCFG();
		//objWN = new WordNet();
		objGlove = new GloveGigaWord();
		System.out.println("Glove init...");
		objGlove.initProp();
		System.out.println("Glove done");
		objBNC = new BNCCorpus();
		objPy = new CallPythonGlove();
		objCN = new ConceptNet();
		corpusSize = objBNC.getTotalCorpusSize();
		//corpusSize = 196656562;
		//System.out.println("corpusSize=" + corpusSize);
	}

	public void readInputAnnotatedPoFo(){
		initializePOSModelPCFG();
		objWN = new WordNet();
		objGlove = new GloveGigaWord();
		objGlove.initProp();
		objBNC = new BNCCorpus();
		objPy = new CallPythonGlove();
		objCN = new ConceptNet();
		corpusSize = objBNC.getTotalCorpusSize();

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
		int lineCount = 0;
		try{
			bw.write("\n\nFile " + " : " + currentFile + "\n");
			br = new BufferedReader(new FileReader(currentFile));
			//brPOS = new BufferedReader(new FileReader(currentPOSFile));
			//String compositeLine = "";
			while ((sCurrentLine = br.readLine()) != null) {
				//sCurrentLinePOS = brPOS.readLine();
				lineCount++;
				if(lineCount==720){
					break;
				}
				String []arr = sCurrentLine.split("@");
				if(arr[2].equals("s")){
					continue;
				}
				String anchorWord = arr[0].split(" ")[Integer.parseInt(arr[1])-1];
				String anno;
				if(arr[2].equals("y")){
					anno = "1";
				}else{
					anno = "0";
				}

				String associations = runStanfordParserWithDiffNoNegSampling(arr[0], anchorWord, anno);
				bw.write(associations);
				//bw.write("\n");
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
		initializePOSModelPCFG();
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
						String associations = "";//runStanfordParser(poemLine);
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

	public void initializePOSModelPCFG(){
		String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		lp = LexicalizedParser.loadModel(parserModel);
		tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");

		TreebankLanguagePack tlp = lp.treebankLanguagePack();
		gsf = tlp.grammaticalStructureFactory();
	}

	public String runStanfordParserWithDiff(String text, String anchorWord, String anno) {
		//StringBuffer sb = new StringBuffer();
		//sb.append("\n" + anchor + " :: " + text);
		text = text + " . ";
		//text = "how can i tell you my mind is a blanket ?  ."; anchorWord = "mind"; anno = "1";
		System.out.println(text);
		String retStr = "";//noun1 + "#" + noun2;

		int countAnchorList = 0;
		ArrayList<TypedDependency> list = new ArrayList<TypedDependency>();
		ArrayList<TypedDependency> anchorList = new ArrayList<TypedDependency>();
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		for (List<HasWord> sentence : tokenizer) {			
			Tree parse = lp.apply(sentence);

			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();

			if(checkListForToken(sentence, anchorWord)){
				countAnchorList++;
				for(TypedDependency dep : tdl){
					anchorList.add(dep);
					//sb.append("\n" + dep);
				}
			} else {
				for(TypedDependency dep : tdl){
					list.add(dep);
					//sb.append("\n" + dep);
				}
			}
		}

		if(countAnchorList>1){
			//retStr = retStr + "Error size below : " + text + "::" + anno + "::" + anchorWord + "\n";
			System.out.println("Error size below : " + text + "::" + anno + "::" + anchorWord); // 4 cases not handled
		}

		int countDup = 0;
		for(TypedDependency dep : anchorList){
			if(dep.reln().toString().equals("dobj") || dep.reln().toString().equals("nsubj") || dep.reln().toString().equals("nsubjpass") || dep.reln().toString().equals("acl:relcl")){
				countDup++;
			}
		}
		if(countDup>1){
			//retStr = retStr + "Error dup anchor below : " + text + "::" + anno + "::" + anchorWord + "\n";
		}

		/*Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(text));
		List<CoreLabel> rawWords = tok.tokenize();

		Tree parse = lp.apply(rawWords);

		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> list = gs.typedDependenciesCCprocessed();*/

		int countAnno = 0;
		for(TypedDependency dep : anchorList){
			if(dep.reln().toString().equals("dobj") || dep.reln().toString().equals("nsubj") || dep.reln().toString().equals("nsubjpass") || dep.reln().toString().equals("acl:relcl")){
				//System.out.println(dep.dep().toString() + "/" + dep.dep().index() + " :: " + dep.gov().toString() + "/" + dep.gov().index());
				String deps = dep.dep().toString();
				//String depsPOS = deps.substring(deps.indexOf("/")+1, deps.length());
				deps = deps.substring(0, deps.indexOf("/"));
				String govs = dep.gov().toString();
				//String govsPOS = govs.substring(govs.indexOf("/")+1, govs.length());
				govs = govs.substring(0, govs.indexOf("/"));

				//if(!convertPennTag(govsPOS).equals("pron") && !convertPennTag(depsPOS).equals("pron")){
				//retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "@@" + convertPennTag(govsPOS) + "::" + convertPennTag(depsPOS) + "\n";
				//retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "::" + similarity + "\n";


				if(countDup>1){
					if(govs.equals(anchorWord) || deps.equals(anchorWord)){
						countAnno++;
						retStr = retStr + makeStringTypeGen(govs, deps) + anno + "\n";
					} else {
						retStr = retStr + makeStringTypeGen(govs, deps) + "0\n";
					}
				} else {
					retStr = retStr + makeStringTypeGen(govs, deps) + anno + "\n";
				}

				//}
				//System.out.println(dep.reln().toString() + "::" + govs + "#" + deps);

			}
			//System.out.println(dep);
			//System.out.println(dep.gov().index());
		}

		if(countAnno==0 && countDup>1 && anno.equals("1")){
			//retStr = retStr + "Error anno zero above : " + text + "::" + anno + "::" + anchorWord + "\n";
			System.out.println("Error anno zero above : " + text + "::" + anno + "::" + anchorWord); //11 cases are skipped due to this
		}

		for(TypedDependency dep : list){
			if(dep.reln().toString().equals("dobj") || dep.reln().toString().equals("nsubj") || dep.reln().toString().equals("nsubjpass")){
				//System.out.println(dep.dep().toString() + "/" + dep.dep().index() + " :: " + dep.gov().toString() + "/" + dep.gov().index());
				String deps = dep.dep().toString();
				//String depsPOS = deps.substring(deps.indexOf("/")+1, deps.length());
				deps = deps.substring(0, deps.indexOf("/"));
				String govs = dep.gov().toString();
				//String govsPOS = govs.substring(govs.indexOf("/")+1, govs.length());
				govs = govs.substring(0, govs.indexOf("/"));

				//if(!convertPennTag(govsPOS).equals("pron") && !convertPennTag(depsPOS).equals("pron")){
				//retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "@@" + convertPennTag(govsPOS) + "::" + convertPennTag(depsPOS) + "\n";
				//retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "::" + similarity + "\n";

				retStr = retStr + makeStringTypeGen(govs, deps) + "0" + "\n";

				//}
				//System.out.println(dep.reln().toString() + "::" + govs + "#" + deps);

			}
			//System.out.println(dep);
			//System.out.println(dep.gov().index());
		}

		//sb.append("\nReturn str :: " + retVal.toLowerCase());

		return retStr.toLowerCase();
	}


	public String runStanfordParserWithDiffNoNegSampling(String text, String anchorWord, String anno) {
		//StringBuffer sb = new StringBuffer();
		//sb.append("\n" + anchor + " :: " + text);
		text = text + " . ";
		//text = "how can i tell you my mind is a blanket ?  ."; anchorWord = "mind"; anno = "1";
		System.out.println(text);
		String retStr = "";//noun1 + "#" + noun2;

		int countAnchorList = 0;
		ArrayList<TypedDependency> list = new ArrayList<TypedDependency>();
		ArrayList<TypedDependency> anchorList = new ArrayList<TypedDependency>();
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		for (List<HasWord> sentence : tokenizer) {			
			Tree parse = lp.apply(sentence);

			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();

			if(checkListForToken(sentence, anchorWord)){
				countAnchorList++;
				for(TypedDependency dep : tdl){
					anchorList.add(dep);
					//sb.append("\n" + dep);
				}
			} else {
				for(TypedDependency dep : tdl){
					list.add(dep);
					//sb.append("\n" + dep);
				}
			}
		}

		if(countAnchorList>1){
			//retStr = retStr + "Error size below : " + text + "::" + anno + "::" + anchorWord + "\n";
			System.out.println("Error size below : " + text + "::" + anno + "::" + anchorWord); // 4 cases not handled
		}

		int countDup = 0;
		for(TypedDependency dep : anchorList){
			if(dep.reln().toString().equals("dobj") || dep.reln().toString().equals("nsubj") || dep.reln().toString().equals("nsubjpass") || dep.reln().toString().equals("acl:relcl")){
				countDup++;
			}
		}
		if(countDup>1){
			//retStr = retStr + "Error dup anchor below : " + text + "::" + anno + "::" + anchorWord + "\n";
		}

		/*Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(text));
		List<CoreLabel> rawWords = tok.tokenize();

		Tree parse = lp.apply(rawWords);

		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> list = gs.typedDependenciesCCprocessed();*/

		int countAnno = 0;
		for(TypedDependency dep : anchorList){
			if(dep.reln().toString().equals("dobj") || dep.reln().toString().equals("nsubj") || dep.reln().toString().equals("nsubjpass") || dep.reln().toString().equals("acl:relcl")){
				//System.out.println(dep.dep().toString() + "/" + dep.dep().index() + " :: " + dep.gov().toString() + "/" + dep.gov().index());
				String deps = dep.dep().toString();
				//String depsPOS = deps.substring(deps.indexOf("/")+1, deps.length());
				deps = deps.substring(0, deps.indexOf("/"));
				String govs = dep.gov().toString();
				//String govsPOS = govs.substring(govs.indexOf("/")+1, govs.length());
				govs = govs.substring(0, govs.indexOf("/"));

				//if(!convertPennTag(govsPOS).equals("pron") && !convertPennTag(depsPOS).equals("pron")){
				//retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "@@" + convertPennTag(govsPOS) + "::" + convertPennTag(depsPOS) + "\n";
				//retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "::" + similarity + "\n";


				if(countDup>1){
					if(govs.equals(anchorWord) || deps.equals(anchorWord)){
						countAnno++;
						retStr = retStr + makeStringTypeGen(govs, deps) + anno + "\n";
					} else {
						//retStr = retStr + makeStringTypeGen(govs, deps) + "0\n";
					}
				} else {
					retStr = retStr + makeStringTypeGen(govs, deps) + anno + "\n";
				}

				//}
				//System.out.println(dep.reln().toString() + "::" + govs + "#" + deps);

			}
			//System.out.println(dep);
			//System.out.println(dep.gov().index());
		}

		if(countAnno==0 && countDup>1 && anno.equals("1")){
			//retStr = retStr + "Error anno zero above : " + text + "::" + anno + "::" + anchorWord + "\n";
			System.out.println("Error anno zero above : " + text + "::" + anno + "::" + anchorWord); //11 cases are skipped due to this
		}

		/*		for(TypedDependency dep : list){
			if(dep.reln().toString().equals("dobj") || dep.reln().toString().equals("nsubj") || dep.reln().toString().equals("nsubjpass")){
				//System.out.println(dep.dep().toString() + "/" + dep.dep().index() + " :: " + dep.gov().toString() + "/" + dep.gov().index());
				String deps = dep.dep().toString();
				//String depsPOS = deps.substring(deps.indexOf("/")+1, deps.length());
				deps = deps.substring(0, deps.indexOf("/"));
				String govs = dep.gov().toString();
				//String govsPOS = govs.substring(govs.indexOf("/")+1, govs.length());
				govs = govs.substring(0, govs.indexOf("/"));

				//if(!convertPennTag(govsPOS).equals("pron") && !convertPennTag(depsPOS).equals("pron")){
				//retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "@@" + convertPennTag(govsPOS) + "::" + convertPennTag(depsPOS) + "\n";
				//retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "::" + similarity + "\n";

				retStr = retStr + makeStringTypeGen(govs, deps) + "0" + "\n";

				//}
				//System.out.println(dep.reln().toString() + "::" + govs + "#" + deps);

			}
			//System.out.println(dep);
			//System.out.println(dep.gov().index());
		}*/

		//sb.append("\nReturn str :: " + retVal.toLowerCase());

		return retStr.toLowerCase();
	}


	public boolean checkListForToken(List<HasWord> sentence, String anchorWord){
		for(HasWord elem : sentence){
			if(elem.word().equals(anchorWord)){
				return true;
			}
		}
		return false;
	}

	/*	public String runStanfordParser(String text) {
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
				//retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "@@" + convertPennTag(govsPOS) + "::" + convertPennTag(depsPOS) + "\n";
				retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "\n";
				//}
				//System.out.println(dep.reln().toString() + "::" + govs + "#" + deps);

			}
			//System.out.println(dep);
			//System.out.println(dep.gov().index());
		}

		//sb.append("\nReturn str :: " + retVal.toLowerCase());
		return retStr.toLowerCase();
	}*/

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

	public String makeStringTypeGen(String word1, String word2) {
		StringBuffer sb = new StringBuffer();
		System.out.println("\nProcessing " + word1 + " : " + word2);

		sb.append("'" + word1 + " " + word2 + "',"); //param 1 Phrase

		String concepts1 = objCN.getProp(word1);
		String concepts2 = objCN.getProp(word2);
		//System.out.println(concepts1 + ":" + concepts2);

		//System.out.println("ConceptNet " + noun1 + " : " + concepts1);
		//System.out.println("ConceptNet " + noun2 + " : " + concepts2);

		String conceptOverlap = String.valueOf(checkConceptOverlap(concepts1, concepts2));

		if(concepts1.equals(word1 + ",") || concepts2.equals(word2 + ",")){
			conceptOverlap = "?";
		}

		sb.append(conceptOverlap + ","); //param 2 Conceptnet Overlap

		String ret1 = objGlove.getProp(word1);
		String ret2 = objGlove.getProp(word2);

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

		String pmi = getPMI(word1, word2);
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
			//System.out.println("Overlap List= " + overlapList);
		}
		return overlap;
	}

	public String getPMI(String noun1, String noun2){
		String ret = "";
		String compositeStr = objBNC.searchCompositeQuery(noun1 + " " + noun2);
		//System.out.println("compositeStr=" + compositeStr);
		String []arr = compositeStr.split("@");
		double freqA = Double.parseDouble(arr[0]);
		double freqB = Double.parseDouble(arr[1]);
		double freqAB = Double.parseDouble(arr[2]);
		double pmi = Math.log(((freqAB*corpusSize)/(freqA*freqB*2))) / (Math.log(2));
		//System.out.println("pmi=" + pmi);
		ret = String.valueOf(pmi);
		return ret;
	}

	public String getBlankArray(){
		String retArr = "";
		for(int i=0;i<dimensions;i++){
			retArr = retArr + "?,";
		}
		return retArr;
	}

	public ArrayList<String> getWordPairs(String text) {
		ArrayList<String> retList = new ArrayList<String>();
		text = text + " . ";
		//text = "how can i tell you my mind is a blanket ?  .";
		System.out.println(text);

		ArrayList<TypedDependency> list = new ArrayList<TypedDependency>();
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		for (List<HasWord> sentence : tokenizer) {
			Tree parse = lp.apply(sentence);

			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
			for(TypedDependency dep : tdl){
				list.add(dep);
				//sb.append("\n" + dep);
			}
		}

		for(TypedDependency dep : list){
			if(dep.reln().toString().equals("dobj") || dep.reln().toString().equals("nsubj") || dep.reln().toString().equals("nsubjpass") || dep.reln().toString().equals("acl:relcl")){
				//System.out.println(dep.dep().toString() + "/" + dep.dep().index() + " :: " + dep.gov().toString() + "/" + dep.gov().index());
				String deps = dep.dep().toString();
				//String depsPOS = deps.substring(deps.indexOf("/")+1, deps.length());
				deps = deps.substring(0, deps.indexOf("/"));
				String govs = dep.gov().toString();
				//String govsPOS = govs.substring(govs.indexOf("/")+1, govs.length());
				govs = govs.substring(0, govs.indexOf("/"));

				//if(!convertPennTag(govsPOS).equals("pron") && !convertPennTag(depsPOS).equals("pron")){
				//retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "@@" + convertPennTag(govsPOS) + "::" + convertPennTag(depsPOS) + "\n";
				//retStr = retStr + dep.reln().toString() + "::" + govs + "#" + deps + "::" + similarity + "\n";

				retList.add(govs + "##" + deps);

				//}
				//System.out.println(dep.reln().toString() + "::" + govs + "#" + deps);

			}
			//System.out.println(dep);
			//System.out.println(dep.gov().index());
		}

		//sb.append("\nReturn str :: " + retVal.toLowerCase());

		return retList;
	}

}
