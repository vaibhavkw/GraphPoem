package diction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class WikiPedia {

	public static String basePath = System.getProperty("user.dir") + "/resources/diction/wikipedia/";

	public static String wikiPath = "D:/study/graph_poem/Tools/Metaphor/Wikipedia/raw.en/raw/";

	public static void main(String[] args) {
		String query = "E-Trade Financial";
		WikiPedia obj = new WikiPedia();
		//obj.readWikiFolder(query);
		//obj.indexDirectory();
		//obj.searchIndex(query);
		//obj.searchIndexFast(query);
		obj.normalIOSearch(query);
		//obj.buildBigramsWiki();
		//obj.indexBigramsWikiDir();
		//obj.searchBigramIndexFast(query);
		//obj.searchCompositeQuery(query);
	}
	
	public void searchCompositeQuery(String searchQuery){
		if(searchQuery.trim().contains(" ")){
			String[] strArr = searchQuery.split(" ");
			long freqA = searchIndexFast(strArr[0]);
			long freqB = searchIndexFast(strArr[1]);
			long freqAB = searchBigramIndexFast(searchQuery);
			
		}else{
			System.out.println("Not a phrase query.");
		}
		
	}
	
	public long searchIndexFast(String searchQuery) {
		long countTerm = 0;
		try {   
			Path path = Paths.get("D:/EclipseWorkSpace/GraphPoem/resources/diction/lucene_index_wiki/indexes");
			Directory directory = FSDirectory.open(path);       
			IndexReader indexReader =  DirectoryReader.open(directory);

			Term term = new Term("contents", searchQuery);
			countTerm = indexReader.totalTermFreq(term);

			System.out.println("Frequency of \"" + searchQuery + "\" : " + countTerm);

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return countTerm;
	}
	
	public long searchBigramIndexFast(String searchQuery) {
		long countTerm = 0;
		try {
			String modQuery = searchQuery.replace(" ", "zz");
			//modQuery = "newzz*";
			Path path = Paths.get(basePath + "indexesBigram");
			Directory directory = FSDirectory.open(path);       
			IndexReader indexReader =  DirectoryReader.open(directory);
			//IndexSearcher indexSearcher = new IndexSearcher(indexReader);

			Term term = new Term("contents", modQuery);
			//int countTerm = indexReader.docFreq(term);
			countTerm = indexReader.totalTermFreq(term);
			//long countTerm2 = indexReader.getSumTotalTermFreq("contents");

			System.out.println("Frequency of \"" + searchQuery + "\" : " + countTerm);

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return countTerm;
	}
	
	private void indexBigramsWikiDir() {
		try {  
			Path path = Paths.get(basePath + "indexesBigram");
			Directory directory = FSDirectory.open(path);
			IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());        
			IndexWriter indexWriter = new IndexWriter(directory, config);
			indexWriter.deleteAll();
			int count = 1;

			File f = new File(basePath + "BigramWikipedia/");     
			for (File file : f.listFiles()) {
				System.out.println("Indexing " + count++ + " : " + file.getCanonicalPath());               
				Document doc = new Document();
				doc.add(new TextField("path", file.getName(), Store.YES));
				BufferedReader reader = new BufferedReader(new FileReader(file));
				StringBuilder stringBuffer = new StringBuilder();
				//String appendedLine = "";
				String line = null;
				while((line = reader.readLine())!=null){
					stringBuffer.append(line + "\n");
				}
				reader.close();
				doc.add(new TextField("contents", stringBuffer.toString(), Store.YES));
				indexWriter.addDocument(doc);
				stringBuffer = null;
			}
			indexWriter.close();
			directory.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void buildBigramsWiki(){
		System.out.println("Starting..");
		try {  
			int count = 1;
			File f = new File(wikiPath);
			File[] fileArr = f.listFiles();
			for (File file : fileArr) {
				//System.out.println("Indexed " + count++ + " : " + file.getCanonicalPath());
				File outFile = new File(basePath + "BigramWikipedia/" + file.getName());
				FileWriter fw = null;
				BufferedWriter bw = null;

				if (!outFile.exists()) {
					outFile.createNewFile();
				}

				fw = new FileWriter(outFile.getAbsoluteFile());
				bw = new BufferedWriter(fw);

				BufferedReader reader = new BufferedReader(new FileReader(file));
				//StringBuffer stringBuffer = new StringBuffer();
				String line = null;
				while((line = reader.readLine())!=null){
					//stringBuffer.append(line + "\n");
					String[] arrStr = null;
					
					if(line == null){
						continue;
					}
					if(line.equals("")){
						continue;
					}
					if(line.trim().equals("")){
						continue;
					}
					if(line.contains("<doc id") || line.equals("</doc>") || line.equals("ENDOFARTICLE.")){
						continue;
					}
					
					String processedLine = processLine(line);
					try{
						arrStr = processedLine.toLowerCase().trim().split(" +");
					}catch(Exception e){
						System.out.println("String skipped due to exception");
						continue;
					}
					for(int i=1;i<arrStr.length;i++){
						String bigram = arrStr[i-1] + "zz" + arrStr[i];
						bw.write(bigram + " ");
					}
					bw.write("\n");
				}
				bw.flush();
				bw.close();
				reader.close();
				bw = null;
				fw = null;
				reader = null;
				
				System.out.println("Indexed " + count++ + " : " + file.getCanonicalPath());
			}
			//System.out.println("bigramMap size : " + bigramMap.size());
		} catch (Exception e) {
			e.printStackTrace();
		}                   
	
	}
	
	public String processLine(String input){
		String ret = "";
		ret = handlePunctuation(input);
		return ret;
	}
	
	public String handlePunctuation(String inpStr){
		//inpStr = "!!~~``%%^^&&**(())__--++==||\\[[]]{{}};;::\\\\''\"\",,..//<<>>";
		String retStr = inpStr;
		retStr = retStr.replaceAll("!", " ! ");
		retStr = retStr.replaceAll("~", " ");
		retStr = retStr.replaceAll("`", " ");
		//retStr = retStr.replaceAll("\\$", " $ ");
		retStr = retStr.replaceAll("%", " ");
		retStr = retStr.replaceAll("\\^", " ");
		retStr = retStr.replaceAll("&", " & ");
		retStr = retStr.replaceAll("\\*", " ");
		retStr = retStr.replaceAll("\\(", " ");
		retStr = retStr.replaceAll("\\)", " ");
		retStr = retStr.replaceAll("_", " ");
		retStr = retStr.replaceAll("-", " ");
		retStr = retStr.replaceAll("__", " ");
		retStr = retStr.replaceAll("--", " ");
		retStr = retStr.replaceAll("\\+", " ");
		retStr = retStr.replaceAll("=", " ");
		retStr = retStr.replaceAll("\\|", " ");
		retStr = retStr.replaceAll("\\\\", " \\ ");
		retStr = retStr.replaceAll("\\[", " ");
		retStr = retStr.replaceAll("]", " ");
		retStr = retStr.replaceAll("\\{", " ");
		retStr = retStr.replaceAll("}", " ");
		retStr = retStr.replaceAll(";", " ; ");
		retStr = retStr.replaceAll(":", " ");
		retStr = retStr.replaceAll("\"", " ");
		retStr = retStr.replaceAll("'", " ");
		retStr = retStr.replaceAll(",", " , ");
		retStr = retStr.replaceAll("\\.", " . ");
		retStr = retStr.replaceAll("//", " ");
		retStr = retStr.replaceAll("<", " ");
		retStr = retStr.replaceAll(">", " ");
		retStr = retStr.replaceAll("\\?", " ? ");
		retStr = retStr.replaceAll("â€œ", " ");
		retStr = retStr.replaceAll("â€™", " ");
		retStr = retStr.replaceAll("  ", " ");
		return retStr;
	}
	
	public void normalIOSearch(String searchQuery) {
		try {  
			int count = 1;
			int matches = 0;
			File f = new File(wikiPath);
			File[] fileArr = f.listFiles();
			for (File file : fileArr) {
				//System.out.println("Indexed " + count++ + " : " + file.getCanonicalPath());
				BufferedReader reader = new BufferedReader(new FileReader(file));
				//StringBuffer stringBuffer = new StringBuffer();
				String line = null;
				while((line = reader.readLine())!=null){
					/*line = line.replaceAll("Â", " ");
					line = line.replaceAll("â€”", " ");
					line = line.replaceAll("€", " ");
					line = line.replaceAll("â", " ");*/

					//stringBuffer.append(line + "\n");
					if(line.toLowerCase().contains(searchQuery)){
						matches++;
					}
				}
				reader.close();         
			}
			System.out.println("Occurences : " + matches);
		} catch (Exception e) {
			e.printStackTrace();
		}                   
	}



	private void searchIndex(String searchQuery) {
		//Apache Lucene searching text inside .txt files
		try {   
			Path path = Paths.get("D:/EclipseWorkSpace/GraphPoem/resources/diction/lucene_index_wiki/indexes");
			Directory directory = FSDirectory.open(path);       
			IndexReader indexReader =  DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);

			Term term = new Term("contents", searchQuery);
			//int countTerm = indexReader.docFreq(term);
			long countTerm = indexReader.totalTermFreq(term);
			//long countTerm2 = indexReader.getSumTotalTermFreq("contents");

			System.out.println("Index Count of " + searchQuery + " : " + countTerm);

			/*SpanQuery spanQuery = null;// define your span query here
			Spans spans = spanQuery.getSpans(indexReader);
			int occurrenceCount = 0;
			while (spans.next()) {
				occurrenceCount++;
			}*/

			QueryParser queryParser = new QueryParser("contents",  new StandardAnalyzer());  
			Query query = queryParser.parse(searchQuery);
			TopDocs topDocs = indexSearcher.search(query,100000);
			System.out.println("totalHits " + topDocs.totalHits);
			ScoreDoc scoreDocs[] = topDocs.scoreDocs;

			//System.out.println("totalHits " + topDocs.totalHits);
			int count = 0;
			System.out.println("Total : " + scoreDocs.length);
			for (ScoreDoc scoreDoc : scoreDocs) {
				count++;
				Document document = indexSearcher.doc(scoreDoc.doc);
				System.out.println("path : " + document.get("path") + " : " + scoreDoc.score);
				//Field content = (Field) document.getField("contents");
				//System.out.println("content " + document.get("contents"));
			}
			//System.out.println("totalHits : " + topDocs.totalHits);
			System.out.println("count : " + count);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}               
	}

	private void indexDirectory() {
		try {
			Path path = Paths.get("D:/EclipseWorkSpace/GraphPoem/resources/diction/lucene_index_wiki/indexes");
			Directory directory = FSDirectory.open(path);
			IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());        
			IndexWriter indexWriter = new IndexWriter(directory, config);
			indexWriter.deleteAll();
			int count = 1;

			File f = new File(wikiPath);
			for (File file : f.listFiles()) {
				System.out.println("Indexed " + count++ + " : " + file.getCanonicalPath());               
				Document doc = new Document();
				doc.add(new TextField("path", file.getName(), Store.YES));
				BufferedReader reader = new BufferedReader(new FileReader(file));
				StringBuffer stringBuffer = new StringBuffer();
				String line = null;
				while((line = reader.readLine())!=null){
					stringBuffer.append(line + "\n");
				}
				reader.close();
				doc.add(new TextField("contents", stringBuffer.toString(), Store.YES));
				indexWriter.addDocument(doc);           
			}               
			indexWriter.close();           
			directory.close();
		} catch (Exception e) {
			e.printStackTrace();
		}                   
	}

	public void readWikiFolder(String query){
		String sb = "";
		double starttime = System.currentTimeMillis();
		int countOccurence = 0;
		File inpFolder = new File(wikiPath);
		File[] children = inpFolder.listFiles();

		//WordNet obj = new WordNet();
		//File outFile = new File(wikiPath + "wiki.txt");
		FileWriter fw = null;
		//BufferedWriter bw = null;
		/*try{
			if (!outFile.exists()) {
				outFile.createNewFile();
			}

			fw = new FileWriter(outFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
		}catch(Exception e){
			e.printStackTrace();
		}*/


		int count = -1;

		for(int loop=0; loop<children.length; loop++){
			count++;
			File currentFile = children[count];
			if(currentFile.isFile()){
				String fileName = currentFile.getName();
				System.out.println("\nProcessing file " + (count+1) + " : " + fileName);

				BufferedReader br = null;
				//BufferedReader brPOS = null;
				String sCurrentLine;
				//String sCurrentLinePOS;
				try{
					//bw.write("\n\nFile " + (count+1) + " : " + fileName);
					br = new BufferedReader(new FileReader(currentFile));
					//brPOS = new BufferedReader(new FileReader(currentPOSFile));
					int lineNumber = 0;
					while ((sCurrentLine = br.readLine()) != null) {
						ArrayList<Word> wordList = new ArrayList<Word>();
						lineNumber++;
						//sCurrentLinePOS = brPOS.readLine();

						if(sCurrentLine.trim().equals("")){
							continue;
						}
						if(sCurrentLine.trim().split(" ").length == 1){
							continue;
						}

						sb = sb + sCurrentLine + " ";
						//sb.append(sCurrentLine + " ");

						/*if(sCurrentLine.contains(query)){
							countOccurence++;
							//System.out.println(sCurrentLine);

						}*/

						//String processedLine = processLine(sCurrentLine);
						//String currentLineWithoutPOS = removePOSTags(sCurrentLine);
						//String processedPOSLine = processPOSLine(sCurrentLinePOS);

						//bw.flush();
					}

					//checkInflection(obj, wordList, inflectionList, noninflectionList);

					br.close();
					br = null;
					currentFile = null;
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

		//System.out.println("Occurence of " + query + " : " + countOccurence);
		System.out.println(sb.toString());
		double endtime = System.currentTimeMillis();
		System.out.println(endtime-starttime);



		try{
			/*bw.write("\n\nTotal POS Sequence Matches : " + totalPOSSequenceMatch);
			bw.write("\n\nTotal Copular Sequence Matches : " + totalCopularSequenceMatch);
			bw.write("\n\nTotal Abstract-Concrete Metaphors : " + totalAbsConMeta);
			bw.write("\n\nTotal CCO Metaphors : " + totalConClassOverlap);
			bw.flush();*/

			//bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		children = null;
		inpFolder = null;

	}

}

