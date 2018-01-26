package diction;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import concordance.WordNet;

public class DictionCommon {

	public static String basePath = "D:/EclipseWorkSpace/GraphPoem" + "/resources/diction/";

	HashMap<String, Integer> posCountMap = new HashMap<String, Integer>(); // for POS Counter
	ArrayList<Word> wordList = new ArrayList<Word>(); // for Inflection Counter
	HashSet<String> nounList = new HashSet<String>(); // for Abstract/Concrete Counter

	WordNet objWN = new WordNet();

	Tokenizer tokenizer = null;
	POSTaggerME tagger = null;

	public static void main(String[] args) {
		DictionCommon obj = new DictionCommon();

		String inputText = "I .have. too? 'much' \"knee\" power!";
		String newline = System.getProperty("line.separator");
		String []newlineArr = inputText.split(newline);

		String outStr = obj.processLineArray(newlineArr);
		System.out.println(outStr);
	}


	public void initializePOSModel(){
		InputStream modelIn = null;
		InputStream tokenModelIn = null;

		try {
			tokenModelIn = new FileInputStream(basePath + "opennlp_models/en-token.bin");
			modelIn = new FileInputStream(basePath + "opennlp_models/en-pos-maxent.bin");

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

	public String processLineArray(String[] lineArr){
		initializePOSModel();

		for(int t=0;t<lineArr.length;t++){
			String line = lineArr[t];
			line = line.toLowerCase();
			//line = line.replaceAll("--", " -- ");
			String tokensOrig[] = tokenizer.tokenize(line);
			String []tokens = new String[tokensOrig.length];
			int counter = -1;
			for(String token : tokensOrig){
				counter++;
				if(token.length()>1){
					tokens[counter] = cleanPrefix(token);
				} else {
					tokens[counter] = token;
				}
			}

			String tags[] = tagger.tag(tokens);

			modulePOSCounter(tokens, tags);
			moduleInflection(tokens, tags);
			moduleAbstractConcrete(tokens, tags);

		}

		StringBuffer sb = new StringBuffer();

		sb.append(finalisePOSCounter());
		sb.append(finaliseInflection());
		sb.append(finaliseAbstractConcrete());

		return sb.toString();

	}

	public void modulePOSCounter(String tokens[], String tags[]){
		for(String tag : tags){
			String key = tag.trim();
			if(posCountMap.containsKey(key)){
				int val = posCountMap.get(key);
				val++;
				posCountMap.remove(key);
				posCountMap.put(key, val);
			}else{
				posCountMap.put(key, 1);
			}
		}

		if(posCountMap.containsKey("TOKENS")){
			int val = posCountMap.get("TOKENS");
			val = val + tags.length;
			posCountMap.remove("TOKENS");
			posCountMap.put("TOKENS", val);
		}else{
			posCountMap.put("TOKENS", tags.length);
		}
	}

	public void moduleInflection(String tokens[], String tags[]){
		for(int i=0;i<tokens.length;i++){
			String word = tokens[i];
			String tag = tags[i];
			if(isNoun(tag) || isVerb(tag) || isAdjective(tag)){
				Word objWord = new Word();
				objWord.setWordText(word.replaceAll("-", ""));
				objWord.setPostag(tag);
				wordList.add(objWord);
			}
		}
	}

	public void moduleAbstractConcrete(String tokens[], String tags[]){
		for(int i=0;i<tokens.length;i++){
			String word = tokens[i];
			String tag = tags[i];
			if(isNoun(tag)){
				nounList.add(word.replaceAll("-", ""));
			}
		}
	}

	public StringBuffer finalisePOSCounter(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n\n");

		HashMap<String, Integer> modPosCountMap = new HashMap<String, Integer>();

		Iterator it = posCountMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry el = (Map.Entry)it.next();
			String key = el.getKey().toString();
			int value = Integer.parseInt(el.getValue().toString());
			String tagName = "";
			if(isVerb(key)){
				tagName = "VERB";
			}
			if(isAdjective(key)){
				tagName = "ADJ";
			}
			if(isAdverb(key)){
				tagName = "ADV";
			}
			if(isPronoun(key)){
				tagName = "PRON";
			}
			if(isPunctuation(key)){
				tagName = "PUNCT";
			}
			if(isConjunction(key)){
				tagName = "CONJ";
			}
			if(isPreposition(key)){
				tagName = "ADP";
			}
			if(isDeterminer(key)){
				tagName = "DET";
			}
			if(isProper(key)){
				tagName = "PROPN";
			}
			if(isParticle(key)){
				tagName = "PART";
			}
			if(isInterjection(key)){
				tagName = "INTJ";
			}
			if(isNoun(key)){
				tagName = "NOUN";
			}
			if(key.equals("TOKENS")){
				modPosCountMap.put(key, value);
			}

			if(!tagName.equals("")){
				if(modPosCountMap.containsKey(tagName)){
					int val = modPosCountMap.get(tagName);
					val = val + value;
					modPosCountMap.remove(tagName);
					modPosCountMap.put(tagName, val);
				}else{
					modPosCountMap.put(tagName, value);
				}
			}
		}


		Iterator itr = modPosCountMap.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry el = (Map.Entry)itr.next();
			sb.append(el.getKey() + " : " + el.getValue() + "\n");
		}

		if(modPosCountMap.containsKey("VERB")){
			sb.append("Verbal density : " + formatNumbers((1.0*modPosCountMap.get("VERB")/modPosCountMap.get("TOKENS"))) + "\n");
		}
		if(modPosCountMap.containsKey("NOUN")){
			sb.append("Noun density : " + formatNumbers((1.0*modPosCountMap.get("NOUN")/modPosCountMap.get("TOKENS"))) + "\n");
		}
		if(modPosCountMap.containsKey("ADJ")){
			sb.append("Adjective density : " + formatNumbers((1.0*modPosCountMap.get("ADJ")/modPosCountMap.get("TOKENS"))) + "\n");
		}
		if(modPosCountMap.containsKey("PRON") && modPosCountMap.containsKey("NOUN")){
			sb.append("Pronoun Vs Noun ratio : " + formatNumbers((1.0*modPosCountMap.get("PRON")/modPosCountMap.get("NOUN"))) + "\n");
		}
		return sb;
	}

	public StringBuffer finaliseInflection(){
		StringBuffer sb = new StringBuffer();
		//sb.append("\n");

		ArrayList<String> inflectionList = new ArrayList<String>();
		ArrayList<String> noninflectionList = new ArrayList<String>();
		for(int i=0;i<wordList.size();i++){
			Word word = wordList.get(i);
			try{
				String wordClass = objWN.getInflectionOrNot(word.getWordText(), word.getPostag());
				//System.out.println(word + " : " + wordClass);
				if(wordClass.equals("inflectional")){
					inflectionList.add(word.getWordText());
				}
				if(wordClass.equals("noninflectional")){
					noninflectionList.add(word.getWordText());
				}
			}catch(Exception e){
				//System.out.println("Error : " + e.getMessage());
			}
			//System.out.println();
		}

		//sb.append("\n\n" + inflectionList.size() + " Inflectional words : " + inflectionList);
		//sb.append("\n" + noninflectionList.size() + " Non Inflectional words : " + noninflectionList);
		//sb.append("\n\n" + inflectionList.size() + " Inflectional words");
		//sb.append("\n" + noninflectionList.size() + " Non Inflectional words");
		sb.append("\nInflection ratio : " + formatNumbers(inflectionList.size()*1.0/noninflectionList.size()));
		return sb;
	}

	public StringBuffer finaliseAbstractConcrete(){
		StringBuffer sb = new StringBuffer();
		//sb.append("\n\n");

		ArrayList concreteList = new ArrayList();
		ArrayList abstractList = new ArrayList();

		Iterator it = nounList.iterator();
		while(it.hasNext()){
			String word = (String) it.next();
			try{
				String wordClass = objWN.getConcreteAbstract(word);
				//System.out.println(word + " : " + wordClass);
				if(wordClass.equals("abstract")){
					abstractList.add(word);
				}
				if(wordClass.equals("concrete")){
					concreteList.add(word);
				}
			}catch(Exception e){
				//System.out.println("Error : " + e.getMessage());
			}
		}

		//sb.append("\nNouns : " + nounList.toString());
		//sb.append("\n" + abstractList.size() + " Abstract words : " + abstractList);
		//sb.append("\n" + concreteList.size() + " Concrete words : " + concreteList);
		sb.append("\nAbstract/Concrete ratio : " + formatNumbers(abstractList.size()*1.0/concreteList.size()));
		return sb;
	}


	public void processLine(String line){ //not used
		line = line.toLowerCase();
		//line = line.replaceAll("--", " -- ");
		String tokens[] = tokenizer.tokenize(line);
		String tags[] = tagger.tag(tokens);
		System.out.println(tokens);
		System.out.println(tags.toString());
	}

	public String[] getTaggedLine(String currentLine){ //not used
		//StringBuffer sb = new StringBuffer();
		currentLine = currentLine.toLowerCase();
		//currentLine = currentLine.replaceAll("--", " -- ");
		String sent[] = tokenizer.tokenize(currentLine);
		String tags[] = tagger.tag(sent);
		return tags;
	}

	public boolean isNoun(String tag){
		if(tag.equalsIgnoreCase("nn") || tag.equalsIgnoreCase("nns") || tag.equalsIgnoreCase("nnp") || tag.equalsIgnoreCase("nnps")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isVerb(String tag){
		if(tag.equalsIgnoreCase("vb") || tag.equalsIgnoreCase("vbd") || tag.equalsIgnoreCase("vbg") || tag.equalsIgnoreCase("vbn") 
				|| tag.equalsIgnoreCase("vbp") || tag.equalsIgnoreCase("vbz") || tag.equalsIgnoreCase("md")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isAdjective(String tag){
		if(tag.equalsIgnoreCase("jj") || tag.equalsIgnoreCase("jjr") || tag.equalsIgnoreCase("jjs") || tag.equalsIgnoreCase("pdt")
				|| tag.equalsIgnoreCase("prp$")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isAdverb(String tag){
		if(tag.equalsIgnoreCase("ex") || tag.equalsIgnoreCase("rb") || tag.equalsIgnoreCase("rbr") || tag.equalsIgnoreCase("rbs")
				|| tag.equalsIgnoreCase("wrb")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isPronoun(String tag){
		if(tag.equalsIgnoreCase("prp")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isPunctuation(String tag){
		if(tag.equalsIgnoreCase(".")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isConjunction(String tag){
		if(tag.equalsIgnoreCase("cc")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isPreposition(String tag){
		if(tag.equalsIgnoreCase("in")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isDeterminer(String tag){
		if(tag.equalsIgnoreCase("dt")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isProper(String tag){
		if(tag.equalsIgnoreCase("nnp") || tag.equalsIgnoreCase("nnps")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isParticle(String tag){
		if(tag.equalsIgnoreCase("pos") || tag.equalsIgnoreCase("rp") || tag.equalsIgnoreCase("to")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isInterjection(String tag){
		if(tag.equalsIgnoreCase("uh")){
			return true;
		} else {
			return false;
		}
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
	
	public String formatNumbers(double input){
		return String.format( "%.3f", input );
	}


}
