package concordance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class LuceneTest {
	public static String basePath = "D:/EclipseWorkSpace/GraphPoem/resources/concordance/";
	
	public static void main(String[] args) {
		LuceneTest obj = new LuceneTest();
		//obj.indexDirectory();
		//obj.search("\"maples field\"~9");
		String query = "seas";
		String enhancedQuery = obj.getEnhancedQuery(query);
		//obj.search(enhancedQuery);
		StringBuffer sb = obj.searchAndHighLight(enhancedQuery);
		writeToFile(sb);
		//search("game~");
	}

	public String getEnhancedQuery(String inpQuery){
		String retQuery = "";
		WordNet objWN = new WordNet();
		Set<String> list = new LinkedHashSet<String>();
		try{
			list = objWN.getSiblings(inpQuery, "");
		}catch(Exception e){
			System.out.println(e.getMessage());
			list.add(inpQuery);
		}
		System.out.println("Lemma for " + inpQuery + " : ");
		for (String s : list) {
			System.out.println(s.replaceAll("_", " "));
			retQuery = retQuery + s + " OR ";
		}
		retQuery = retQuery.substring(0, retQuery.length()-4);
		return retQuery;
	}

	private void indexDirectory() {      
		//Apache Lucene Indexing Directory .txt files
		try {  
			//indexing directory    
			Path path = Paths.get("D:/EclipseWorkSpace/GraphPoemConcordance/resources/indexes");
			Directory directory = FSDirectory.open(path);
			IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());        
			IndexWriter indexWriter = new IndexWriter(directory, config);
			indexWriter.deleteAll();
			int count = 1;
			//D:/EclipseWorkSpace/GraphPoem/resources/poetry_foundation_final
			//D:/EclipseWorkSpace/GraphPoemConcordance/resources/input
			File f = new File("D:/EclipseWorkSpace/GraphPoem/resources/poetry_foundation_final"); // current directory     
			for (File file : f.listFiles()) {
				System.out.println("Indexed " + count++ + " : " + file.getCanonicalPath());               
				Document doc = new Document();
				doc.add(new TextField("path", file.getName(), Store.YES));
				BufferedReader reader = new BufferedReader(new FileReader(file));
				StringBuffer stringBuffer = new StringBuffer();
				String line = null;
				while((line = reader.readLine())!=null){
					line = line.replaceAll("Â", " ");
					line = line.replaceAll("â€”", " ");
					line = line.replaceAll("€", " ");
					line = line.replaceAll("â", " ");

					stringBuffer.append(line + "\n");
				}
				reader.close();
				doc.add(new TextField("contents", stringBuffer.toString(), Store.YES));
				indexWriter.addDocument(doc);           
			}               
			indexWriter.close();           
			directory.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}                   
	}

	private void search(String searchQuery) {
		//Apache Lucene searching text inside .txt files
		try {   
			Path path = Paths.get("D:/EclipseWorkSpace/GraphPoemConcordance/resources/indexes");
			Directory directory = FSDirectory.open(path);       
			IndexReader indexReader =  DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			
			QueryParser queryParser = new QueryParser("contents",  new StandardAnalyzer());  
			Query query = queryParser.parse(searchQuery);
			TopDocs topDocs = indexSearcher.search(query,10);
			
			System.out.println("totalHits " + topDocs.totalHits);
			int count = 0;
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				count++;
				Document document = indexSearcher.doc(scoreDoc.doc);
				System.out.println("path : " + document.get("path") + " : " + scoreDoc.score);
				//System.out.println("content " + document.get("contents"));
			}
			System.out.println("totalHits : " + topDocs.totalHits);
			System.out.println("count : " + count);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}               
	}

	public StringBuffer searchAndHighLight(String searchQuery) {
		try{
			QueryParser queryParser = new QueryParser("contents", new StandardAnalyzer());
			Query query = queryParser.parse(searchQuery);
			QueryScorer queryScorer = new QueryScorer(query, "contents");
			Fragmenter fragmenter = new SimpleSpanFragmenter(queryScorer);

			Highlighter highlighter = new Highlighter(queryScorer); // Set the best scorer fragments
			highlighter.setTextFragmenter(fragmenter); // Set fragment to highlight

			File indexFile = new File("D:/EclipseWorkSpace/GraphPoemConcordance/resources/indexes");
			Directory directory = FSDirectory.open(indexFile.toPath());
			IndexReader indexReader = DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);

			
			ScoreDoc scoreDocs[] = indexSearcher.search(query, 100000).scoreDocs;
			int count = 0;
			StringBuffer sb = new StringBuffer();
			//sb.append("Query : " + searchQuery + "\n");
			sb.append("Total poems results : " + scoreDocs.length + "\n");
			for (ScoreDoc scoreDoc : scoreDocs) {
				count++;
				Document document = indexSearcher.doc(scoreDoc.doc);
				String title = document.get("contents");
				String path = document.get("path");
				System.out.println("path : " + path);
				sb.append("\n<b1>File : " + path + "</b1>\n");
				sb.append("<b1>Lucene Score : " + scoreDoc.score + "</b1>\n");
				TokenStream tokenStream = TokenSources.getAnyTokenStream(indexReader, scoreDoc.doc, "contents", document, new StandardAnalyzer());
				//String fragment = highlighter.getBestFragment(tokenStream, title);
				String []fragments = highlighter.getBestFragments(tokenStream, title, 1000000);
				//System.out.println("\nfragment " + count + " : " + fragments[0]);
				for(String strFragment : fragments){
					sb.append(strFragment + "\n");
				}
			}
			//writeToFile(sb);
			return sb;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		} 
	}
	
	public static void writeToFile(StringBuffer sb){
		try {
			File file = new File(basePath + "results.html");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("<style> B {background-color: cyan;color: black;} B1 {background-color: yellow;color: black;} </style><pre>" + sb.toString() + "</pre>");
			bw.close();

			System.out.println("results.html written");
			URI uri = new URI(basePath + "results.html");
			java.awt.Desktop.getDesktop().browse(uri);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}