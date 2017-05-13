package concordance;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.morph.IStemmer;
import edu.mit.jwi.morph.WordnetStemmer;


public class WordNet {
	public static String wordNetPath = "C:\\Program Files (x86)\\WordNet\\2.1\\dict";
	public IDictionary dict = null;

	public static void main(String[] args) {
		String query = "cars";
		WordNet obj = new WordNet();
		Set<String> list = new LinkedHashSet<String>();
		try{
			list = obj.getSiblings(query, "");
			//obj.getRelatedWords(query, "");
			//obj.getHypernyms("animals", "noun");
			//list.add(obj.getConcreteAbstract(query));
			//list.add(obj.getInflectionOrNot(query, "noun"));
		}catch(Exception e){
			e.printStackTrace();
			list.add(query);
		}
		//System.out.println("  Lemma for " + query + " : ");
		for (String s : list) {
			System.out.println("Output : " + s.replaceAll("_", " "));
		}
	}
	
	public WordNet(){
		File file = new File(wordNetPath);
		dict = new Dictionary(file);
		try{
			dict.open();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public POS getPOSObject(String pos){
		POS inputPOS = null;
		if(pos.equals("")){
			inputPOS = POS.NOUN;
		} else if(pos.equalsIgnoreCase("verb")){
			inputPOS = POS.VERB;
		} else if(pos.equalsIgnoreCase("noun")){
			inputPOS = POS.NOUN;
		} else if(pos.equalsIgnoreCase("adjective") || pos.equalsIgnoreCase("adj")){
			inputPOS = POS.ADJECTIVE;
		} else if(pos.equalsIgnoreCase("adverb") || pos.equalsIgnoreCase("adv")){
			inputPOS = POS.ADVERB;
		}
		return inputPOS;
	}

	// DO NOT CHANGE ACCESSED BY Lucene API
	public final Set<String> getSiblings(String query, String pos) throws Exception {
		Set<String> synList = new LinkedHashSet<String>();
		POS inputPOS = getPOSObject(pos);
		try{
			IIndexWord idxWord = dict.getIndexWord(query, inputPOS);          
			List<IWordID> wordIDList = idxWord.getWordIDs();

			for(IWordID wordID : idxWord.getWordIDs()){
				IWord word = dict.getWord(wordID);

				//System.out.println("ID = " + wordID);
				System.out.println("Lemma = " + word.getLemma());
				System.out.println("Meaning = " + word.getSynset().getGloss());

				ISynset synset = word.getSynset();
				for( IWord w : synset.getWords()){
					synList.add(w.getLemma());
					System.out.println(w.getLemma());
				}

			}
		}catch(Exception e){
			//System.out.println(e);
			System.out.println(query + " : no lemma found. Trying to search for stem word.");
			synList.add(query);
			IStemmer stemmer = new WordnetStemmer(dict);
			List stemList = stemmer.findStems(query, inputPOS);
			//System.out.println("Stems:");
			for(int i=0;i<stemList.size();i++){
				//System.out.println(stemList.get(i));
				IIndexWord idxWord = dict.getIndexWord(stemList.get(i).toString(), inputPOS);
				for(IWordID wordID : idxWord.getWordIDs()){
					IWord word = dict.getWord(wordID);

					//System.out.println("ID = " + wordID);
					System.out.println("Lemma = " + word.getLemma());
					System.out.println("Meaning = " + word.getSynset().getGloss());

					ISynset synset = word.getSynset();
					for( IWord w : synset.getWords())
						synList.add(w.getLemma());
				}
			}
			if(synList.size() == 1){
				throw new Exception("No lemma found even after stemming.");
			}
			//throw new Exception("No lemma found for " + query);
		}
		return synList;
	}
	
	public IIndexWord getIndexWordStem(IDictionary dict, String query, POS pos) throws Exception {
		IIndexWord idxWord = dict.getIndexWord(query, pos);
		if(idxWord == null){
			//System.out.println(query + " : no lemma found. Trying to search for stem word.");
			IStemmer stemmer = new WordnetStemmer(dict);
			List<String> stemList = stemmer.findStems(query, pos);
			if(stemList.size() == 0){
				throw new Exception("No lemma found even after stemming for : " + query);
			}else{
				//System.out.println("Lemma found after stemming : " + stemList.get(0));
				idxWord = dict.getIndexWord(stemList.get(0), pos);
			}
		}
		return idxWord;
	}
	
	public String getStemWord(String query, String pos) {
		//query = "absorbed";
		POS posObj = getPOSObject(pos);
		IIndexWord idxWord = dict.getIndexWord(query, posObj);
		if(idxWord == null){
			//System.out.println(query + " : no lemma found. Trying to search for stem word.");
			IStemmer stemmer = new WordnetStemmer(dict);
			List<String> stemList = stemmer.findStems(query, posObj);
			if(stemList.size() == 0){
				return query;
			}else{
				//System.out.println("Lemma found after stemming : " + stemList.get(0));
				//System.out.println(query);
				//System.out.println(stemList);
				idxWord = dict.getIndexWord(stemList.get(0), posObj);
			}
		}
		if(idxWord == null){
			return query;
		}
		return idxWord.getLemma();
	}

	public LinkedHashMap getHypernyms(String query, String pos) throws Exception {
		LinkedHashMap<String, String> hypernymList = new LinkedHashMap<String, String>(); 
		if(query.equalsIgnoreCase("entity")){
			return hypernymList;
		}
		POS inputPOS = getPOSObject(pos);

		//IIndexWord idxWord = dict.getIndexWord(query, inputPOS);
		IIndexWord idxWord = getIndexWordStem(dict, query, inputPOS);
		List<IWordID> wordIDList = idxWord.getWordIDs();

		//System.out.println("Senses = " + idxWord.getWordIDs().size());

		IWordID wordID = idxWord.getWordIDs().get(0);
		IWord word = dict.getWord(wordID);
		String currentHypernym = "start";

		while(!currentHypernym.equals("entity")){
			ISynset synset = word.getSynset();

			List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);
			if(hypernyms.size()==0){
				hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE);
			}

			ISynsetID synid = hypernyms.get(0);
			IWord synWord = dict.getSynset(synid).getWords().get(0);
			currentHypernym = synWord.getLemma();
			//System.out.println(currentHypernym);
			if(currentHypernym.equalsIgnoreCase("whole") || currentHypernym.equalsIgnoreCase("abstraction") 
					|| currentHypernym.equalsIgnoreCase("object") || currentHypernym.equalsIgnoreCase("physical_entity") 
					|| currentHypernym.equalsIgnoreCase("abstract_entity")){
				break;
			}
			hypernymList.put(currentHypernym, currentHypernym);
			word = synWord;
		}
		return hypernymList;
	}
	
	public String getConcreteAbstract(String query) throws Exception {
		//query = "love";
		String ret = "none";
		if(query.equalsIgnoreCase("entity")){
			return ret;
		}

		//IIndexWord idxWord = dict.getIndexWord(query, POS.NOUN);
		IIndexWord idxWord = getIndexWordStem(dict, query, POS.NOUN);
		List<IWordID> wordIDList = idxWord.getWordIDs();

		//System.out.println("Senses = " + idxWord.getWordIDs().size());

		IWordID wordID = idxWord.getWordIDs().get(0);
		IWord word = dict.getWord(wordID);
		String currentHypernym = "start";

		while(!currentHypernym.equals("entity")){
			ISynset synset = word.getSynset();

			List<ISynsetID> hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM);
			if(hypernyms.size()==0){
				hypernyms = synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE);
			}

			ISynsetID synid = hypernyms.get(0);
			IWord synWord = dict.getSynset(synid).getWords().get(0);
			currentHypernym = synWord.getLemma();
			//System.out.println(currentHypernym);
			if(currentHypernym.equalsIgnoreCase("physical_entity")){
				ret = "concrete";
			}
			if(currentHypernym.equalsIgnoreCase("abstract_entity")){
				ret = "abstract";
			}
			word = synWord;
		}
		return ret;
	}

	public void getRelatedWords(String query, String pos) throws Exception {
		POS inputPOS = null;
		if(pos.equals("")){
			inputPOS = POS.NOUN;
		} else if(pos.equalsIgnoreCase("verb")){
			inputPOS = POS.VERB;
		} else if(pos.equalsIgnoreCase("adjective")){
			inputPOS = POS.ADJECTIVE;
		} else if(pos.equalsIgnoreCase("adjective")){
			inputPOS = POS.ADJECTIVE;
		} else if(pos.equalsIgnoreCase("adverb")){
			inputPOS = POS.ADVERB;
		}

		IIndexWord idxWord = dict.getIndexWord(query, inputPOS);          
		IWordID wordID = idxWord.getWordIDs().get(0);
		IWord word = dict.getWord(wordID);

		//System.out.println("ID = " + wordID);
		System.out.println("Lemma = " + word.getLemma());
		System.out.println("Meaning = " + word.getSynset().getGloss());
		System.out.println("Related words=");
		for( IWordID w : word.getRelatedWords())
			System.out.println(dict.getWord(w).getLemma());
	}
	
	public String getInflectionOrNot(String query, String pos) throws Exception {
		//query = "killing";
		//pos = "verb";
		String retStr = "";
		POS inputPOS = getPOSObject(pos);
		IIndexWord idxWord = null;//dict.getIndexWord(query, inputPOS);
		if(idxWord == null){
			//System.out.println(query + " : no lemma found. Trying to search for stem word.");
			IStemmer stemmer = new WordnetStemmer(dict);
			List<String> stemList = stemmer.findStems(query, inputPOS);
			if(stemList.size() == 0){
				//System.out.println(query + "::" + stemList);
				return "none";
			}
			int actualStemCount = 0;
			for(int i=0;i<stemList.size();i++){
				if(!query.equalsIgnoreCase(stemList.get(i))){
					actualStemCount++;
				}
			}
			if(actualStemCount == 0){
				retStr = "noninflectional";
			}else{
				retStr = "inflectional";
			}
		}else{
			//retStr = "noninflectional";
		}
		return retStr;
	}

}
