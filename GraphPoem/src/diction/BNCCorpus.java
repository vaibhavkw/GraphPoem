package diction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class BNCCorpus {

	private static String corpusPath = "D:/study/graph_poem/Tools/Metaphor/BNC/2554/download/Texts/";

	private static String bncPath = "D:/EclipseWorkSpace/GraphPoem/resources/diction/bnc/";

	private StringBuilder recursiveStr = new StringBuilder(); 

	public static void main(String[] args) {
		BNCCorpus obj = new BNCCorpus();
		String query = "pigs fly";
		//obj.convertBNCFolder();
		//obj.indexDirectory();
		//obj.searchIndexFast(query);
		//obj.buildBigramsBNC();
		//obj.indexBigramsBNCDir();
		obj.searchCompositeQuery(query);
		//obj.normalIOSearch(query);

	}

	public void outputForPython(){
		try{
			int count = 1;
			File f = new File(bncPath + "extractedBNC/");
			for (File file : f.listFiles()) {
				System.out.println("Indexed " + count++ + " : " + file.getCanonicalPath());               
				BufferedReader reader = new BufferedReader(new FileReader(file));
				StringBuffer stringBuffer = new StringBuffer();
				String line = null;
				while((line = reader.readLine())!=null){
					stringBuffer.append(line + "\n");
				}
				reader.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String searchCompositeQuery(String searchQuery){
		long freqA = 0;
		long freqB = 0;
		long freqAB = 0;
		if(searchQuery.trim().contains(" ")){
			String[] strArr = searchQuery.split(" ");
			freqA = searchIndexFast(strArr[0]);
			freqB = searchIndexFast(strArr[1]);
			freqAB = searchBigramIndexFast(searchQuery);

		}else{
			System.out.println("Not a phrase query.");
			return "NA";
		}
		return String.valueOf(freqA) + "@" + String.valueOf(freqB) + "@" + String.valueOf(freqAB);
	}

	public long searchBigramIndexFast(String searchQuery) {
		long countTerm = 0;
		try {
			String modQuery = searchQuery.replace(" ", "zz");
			//modQuery = "newzz*";
			Path path = Paths.get(bncPath + "indexesBigram");
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

	public void normalIOSearch(String searchQuery) {
		System.out.println("Started..");
		double starttime = System.currentTimeMillis();
		try {
			int matches = 0;
			File f = new File(bncPath + "extractedBNC/");
			File[] fileArr = f.listFiles();
			for (File file : fileArr) {
				//System.out.println("Indexed " + count++ + " : " + file.getCanonicalPath());
				BufferedReader reader = new BufferedReader(new FileReader(file));
				//StringBuffer stringBuffer = new StringBuffer();
				String line = null;
				while((line = reader.readLine())!=null){
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
		double endtime = System.currentTimeMillis();
		System.out.println(endtime-starttime);
	}

	private void indexBigramsBNCDir() {
		try {  
			Path path = Paths.get(bncPath + "indexesBigram");
			Directory directory = FSDirectory.open(path);
			IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());        
			IndexWriter indexWriter = new IndexWriter(directory, config);
			indexWriter.deleteAll();
			int count = 1;

			File f = new File(bncPath + "BigramBNC/");     
			for (File file : f.listFiles()) {
				System.out.println("Indexing " + count++ + " : " + file.getCanonicalPath());               
				org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
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

	public void buildBigramsBNC(){
		System.out.println("Starting..");
		try {  
			int count = 1;
			File f = new File(bncPath + "extractedBNC/");
			File[] fileArr = f.listFiles();
			for (File file : fileArr) {
				//System.out.println("Indexed " + count++ + " : " + file.getCanonicalPath());
				File outFile = new File(bncPath + "BigramBNC/" + file.getName());
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
	
	public double getTotalCorpusSize() {
		double tot = 0;
		try {
		Path path = Paths.get(bncPath + "indexes");
		Directory directory = FSDirectory.open(path);       
		IndexReader indexReader =  DirectoryReader.open(directory);
		tot = indexReader.getSumTotalTermFreq("content");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tot;
	}

	public long searchIndexFast(String searchQuery) {
		long countTerm = 0;
		try {   
			Path path = Paths.get(bncPath + "indexes");
			Directory directory = FSDirectory.open(path);       
			IndexReader indexReader =  DirectoryReader.open(directory);

			Term term = new Term("content", searchQuery);
			countTerm = indexReader.totalTermFreq(term);
			System.out.println();

			System.out.println("Frequency of \"" + searchQuery + "\" : " + countTerm);

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return countTerm;
	}

	private void indexDirectory() {
		try {
			Path path = Paths.get(bncPath + "indexes");
			Directory directory = FSDirectory.open(path);
			IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());        
			IndexWriter indexWriter = new IndexWriter(directory, config);
			indexWriter.deleteAll();
			int count = 1;

			File f = new File(bncPath + "extractedBNC/");
			for (File file : f.listFiles()) {
				System.out.println("Indexed " + count++ + " : " + file.getCanonicalPath());               
				org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
				doc.add(new TextField("path", file.getName(), Store.YES));
				BufferedReader reader = new BufferedReader(new FileReader(file));
				StringBuffer stringBuffer = new StringBuffer();
				String line = null;
				while((line = reader.readLine())!=null){
					stringBuffer.append(line + "\n");
				}
				reader.close();
				Field text = new Field("content", stringBuffer.toString(), Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS);
				doc.add(text);
				//doc.add(new TextField("contents", stringBuffer.toString(), Store.YES));
				indexWriter.addDocument(doc);           
			}               
			indexWriter.close();           
			directory.close();
		} catch (Exception e) {
			e.printStackTrace();
		}                   
	}




	public void convertBNCFolder(){
		SAXReader reader = new SAXReader();
		double starttime = System.currentTimeMillis();
		File inpFolder = new File(corpusPath);
		Collection<File> children = listFileTree(inpFolder);

		int count = -1;

		for(File currentFile : children){	
			count++;
			if(currentFile.isFile() && currentFile.getName().contains(".xml")){
				String fileName = currentFile.getName();
				System.out.println("\nProcessing file " + (count+1) + " : " + fileName);

				try{
					Document document = reader.read(currentFile);
					Element root = document.getRootElement();

					recursiveStr = new StringBuilder();
					recursiveReadXML(root);
					//System.out.println(recursiveStr.toString());

					document = null;

					File outFile = new File(bncPath + "extractedBNC/" + fileName.replace(".xml", ".txt"));
					FileWriter fw = null;
					BufferedWriter bw = null;
					try{
						if (!outFile.exists()) {
							outFile.createNewFile();
						}

						fw = new FileWriter(outFile.getAbsoluteFile());
						bw = new BufferedWriter(fw);
					}catch(Exception e){
						e.printStackTrace();
					}

					bw.write(recursiveStr.toString());
					bw.flush();
					bw.close();
					bw = null;
					fw = null;
					currentFile = null;
					outFile = null;

					/*for ( Iterator i = root.elementIterator(); i.hasNext(); ) {
						Element element = (Element) i.next();
						if(element.getName().contains("text")){
							System.out.println(element);
							for ( Iterator j = element.elementIterator(); j.hasNext(); ) {
								Element elementj = (Element) j.next();
								System.out.println(elementj);
								for ( Iterator k = elementj.elementIterator(); k.hasNext(); ) {
									Element elementk = (Element) k.next();
									System.out.println(elementk);
									for ( Iterator l = elementk.elementIterator(); l.hasNext(); ) {
										Element elementl = (Element) l.next();
										if(elementl.getName().equals("w")){
											System.out.println(elementl.getText());
											System.out.println(elementl.attributeValue("pos"));
										}
										if(elementl.getName().equals("c")){
											System.out.println(elementl.getText());
											System.out.println(elementl.attributeValue("c5"));
										}
									}
								}
							}
						}
					}
					 */

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			//writeToFile("poem_output" + (loop+1) + ".txt");
		}

		//System.out.println("Occurence of " + query + " : " + countOccurence);
		double endtime = System.currentTimeMillis();
		System.out.println(endtime-starttime);

		children = null;
		inpFolder = null;

	}

	public static Collection<File> listFileTree(File dir) {
		Set<File> fileTree = new HashSet<File>();
		if(dir==null||dir.listFiles()==null){
			return fileTree;
		}
		for (File entry : dir.listFiles()) {
			if (entry.isFile()) fileTree.add(entry);
			else fileTree.addAll(listFileTree(entry));
		}
		return fileTree;
	}

	private void recursiveReadXML(Element elem){
		for (Iterator i = elem.elementIterator(); i.hasNext();) {
			Element element = (Element) i.next();
			if(element.getName().equals("teiHeader")){
				continue;
			}
			if(element.getName().equals("s")){
				recursiveStr.append("\n");
				recursiveReadXML(element);
			}
			/*if(element.getName().equals("div") || element.getName().equals("head") || element.getName().equals("u") || element.getName().equals("mw")){
				//System.out.println(element);
				recursiveReadXML(element);
			}
			if(element.getName().equals("wtext") || element.getName().equals("stext")){
				//System.out.println(element);
				recursiveReadXML(element);
			}*/
			if(element.getName().equals("w")){
				//System.out.println(element.getText());
				//System.out.println(element.attributeValue("pos"));
				//recursiveStr.append(element.getText() + "/" + element.attributeValue("pos"));
				String text = element.getText();
				//String pos = element.attributeValue("pos");
				if(text != null && !text.equals("")){
					text = text.trim();
				}
				/*if(pos != null && !pos.equals("")){
					pos = pos.trim();
				}*/				
				//recursiveStr.append(text + "/" + pos + " ");
				recursiveStr.append(text + " ");
			}
			if(element.getName().equals("c")){
				//System.out.println(element.getText());
				//System.out.println(element.attributeValue("c5"));
				//recursiveStr.append(element.getText() + "/" + element.attributeValue("c5"));
				String text = element.getText();
				//String pos = element.attributeValue("c5");
				if(text != null && !text.equals("")){
					text = text.trim();
				}
				/*if(pos != null && !pos.equals("")){
					pos = pos.trim();
				}*/
				//recursiveStr.append(text + "/" + pos + " ");
				recursiveStr.append(text + " ");
			} else {
				recursiveReadXML(element);
			}
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
		//retStr = retStr.replaceAll("!", " ! ");
		retStr = retStr.replaceAll("~", " ");
		retStr = retStr.replaceAll("`", " ");
		//retStr = retStr.replaceAll("\\$", " $ ");
		retStr = retStr.replaceAll("%", " ");
		retStr = retStr.replaceAll("\\^", " ");
		//retStr = retStr.replaceAll("&", " & ");
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
		//retStr = retStr.replaceAll("\\\\", " \\ ");
		retStr = retStr.replaceAll("\\[", " ");
		retStr = retStr.replaceAll("]", " ");
		retStr = retStr.replaceAll("\\{", " ");
		retStr = retStr.replaceAll("}", " ");
		//retStr = retStr.replaceAll(";", " ; ");
		retStr = retStr.replaceAll(":", " ");
		retStr = retStr.replaceAll("\"", " ");
		retStr = retStr.replaceAll("'", " ");
		//retStr = retStr.replaceAll(",", " , ");
		//retStr = retStr.replaceAll("\\.", " . ");
		retStr = retStr.replaceAll("//", " ");
		retStr = retStr.replaceAll("<", " ");
		retStr = retStr.replaceAll(">", " ");
		//retStr = retStr.replaceAll("\\?", " ? ");
		//retStr = retStr.replaceAll("“", " ");
		//retStr = retStr.replaceAll("’", " ");
		retStr = retStr.replaceAll("  ", " ");
		return retStr;
	}


}
