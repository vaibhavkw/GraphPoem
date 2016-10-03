import java.io.File;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;


public class Test {


	public static void main(String[] args) {
		try{
			// construct the URL to the Wordnet dictionary directory
			String wnhome = "C:\\Program Files (x86)\\WordNet\\2.1";
			String path = wnhome + File.separator + "dict";
			File file = new File(path);

			// construct the dictionary object and open it
			IDictionary dict = new Dictionary(file);
			dict.open();

			// look up first sense of the word "dog"
			IIndexWord idxWord = dict.getIndexWord("car", POS.NOUN);
			IWordID wordID = idxWord.getWordIDs().get(0);
			IWord word = dict.getWord(wordID);

			System.out.println("ID = " + wordID);
			System.out.println("Lemma = " + word.getLemma());
			System.out.println("Gloss = " + word.getSynset().getGloss());

			ISynset synset = word.getSynset();
			for( IWord w : synset.getWords())
				System.out.println(w.getLemma());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
