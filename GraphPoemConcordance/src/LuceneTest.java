import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class LuceneTest {
    public static void main(String[] args) {
        indexDirectory();
        search("maples");
    }   
         
    private static void indexDirectory() {      
         //Apache Lucene Indexing Directory .txt files     
         try {  
         //indexing directory    
         Path path = Paths.get("D:/EclipseWorkSpace/GraphPoemConcordance/resources/indexes");
         Directory directory = FSDirectory.open(path);
         IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());        
         IndexWriter indexWriter = new IndexWriter(directory, config);
         indexWriter.deleteAll();
         File f = new File("D:/EclipseWorkSpace/GraphPoemConcordance/resources/input"); // current directory     
             for (File file : f.listFiles()) {
                    System.out.println("indexed " + file.getCanonicalPath());               
                    Document doc = new Document();
                    doc.add(new TextField("path", file.getName(), Store.YES));
                    FileInputStream is = new FileInputStream(file);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
            // TODO: handle exception
            e.printStackTrace();
        }                   
    }
     
    private static void search(String text) {   
        //Apache Lucene searching text inside .txt files
        try {   
            Path path = Paths.get("D:/EclipseWorkSpace/GraphPoemConcordance/resources/indexes");
            Directory directory = FSDirectory.open(path);       
            IndexReader indexReader =  DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            QueryParser queryParser = new QueryParser("contents",  new StandardAnalyzer());  
            Query query = queryParser.parse(text);
            TopDocs topDocs = indexSearcher.search(query,10);
            System.out.println("totalHits " + topDocs.totalHits);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {           
                Document document = indexSearcher.doc(scoreDoc.doc);
                System.out.println("path " + document.get("path"));
                System.out.println("content " + document.get("contents"));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }               
    }
  }