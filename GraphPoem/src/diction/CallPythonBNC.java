package diction;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class CallPythonBNC {
	public static int dimensions = 100;
	
	static String basePath = System.getProperty("user.dir") + "/resources/diction/python/";
	
	Properties prop = new Properties();
	Properties prop2 = new Properties();
	
	public static void main(String args[]) {
		CallPythonBNC obj = new CallPythonBNC();
		//System.out.println(obj.getWord2VecSimilarity("pigs@fly"));
		String ret1 = obj.getProp2("pigs");
		String ret2 = obj.getProp2("flyzzzz");
		System.out.println(ret1);
		System.out.println(ret2);
		String retStr = obj.getVectorDiff(ret1, ret2);
		System.out.println(retStr);
	}
	
	CallPythonBNC(){
		initProp();
	}
	
	private void initProp() {
		try {
			prop.load(new FileInputStream(basePath + "word2vec_BNC.properties"));
			prop2.load(new FileInputStream(basePath + "word2vec2_BNC.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveProp() {
		try {
			prop.store(new FileOutputStream(basePath + "word2vec_BNC.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveProp2() {
		try {
			prop2.store(new FileOutputStream(basePath + "word2vec2_BNC.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getProp(String key){
		String value = prop.getProperty(key);
		if(value == null){
			try{
				value = getWord2VecSimilarity(key);
				prop.setProperty(key, value);
				saveProp();
			}catch(Exception e){
				value = "";
				e.printStackTrace();
			}
		}
		return value;
	}
	
	public String getProp2(String key){
		String value = prop2.getProperty(key);
		if(value == null){
			try{
				value = getWord2VecVector(key);
				prop2.setProperty(key, value);
				saveProp2();
			}catch(Exception e){
				value = "";
				e.printStackTrace();
			}
		}
		if(value.equals("Loading saved model")){
			value = "";
			/*for(int i=1;i<=100;i++){
				value = value + ",0.0";
			}*/
		}
		return value;
	}

	public String getWord2VecSimilarity(String key){
		String word1 = key.substring(0, key.indexOf("@"));
		String word2 = key.substring(key.indexOf("@")+1, key.length());
		String retStr = "";
		//double starttime = System.currentTimeMillis();
		String line = "python D:/PythonWorkSpace/WordEmbeddings/WordEmbBNCCommand.py " + word1 + " " + word2;

		try {
			String[] cmd = new String[3];
			cmd[0] = "cmd" ;
			cmd[1] = "/C" ;
			cmd[2] = line;

			Runtime rt = Runtime.getRuntime();
			//System.out.println("Execing " + cmd[0] + " " + cmd[1] + " " + cmd[2]);
			Process proc = rt.exec(cmd);

			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");            
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");

			errorGobbler.start();
			outputGobbler.start();

			int exitVal = proc.waitFor();
			//System.out.println("ExitValue: " + exitVal);
			//double endtime = System.currentTimeMillis();
			//System.out.println(endtime - starttime);
			retStr = outputGobbler.getOutput();
			
		} catch (Throwable t){
			t.printStackTrace();
		}
		return retStr;
	}
	
	public String getWord2VecVector(String key){
		String retStr = "";
		//double starttime = System.currentTimeMillis();
		String line = "python D:/PythonWorkSpace/WordEmbeddings/WordEmbBNCVecCommand.py " + key;

		try {
			String[] cmd = new String[3];
			cmd[0] = "cmd" ;
			cmd[1] = "/C" ;
			cmd[2] = line;

			Runtime rt = Runtime.getRuntime();
			//System.out.println("Execing " + cmd[0] + " " + cmd[1] + " " + cmd[2]);
			Process proc = rt.exec(cmd);

			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");            
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");

			errorGobbler.start();
			outputGobbler.start();

			int exitVal = proc.waitFor();
			//System.out.println("ExitValue: " + exitVal);
			//double endtime = System.currentTimeMillis();
			//System.out.println(endtime - starttime);
			retStr = outputGobbler.getOutput();
			
		} catch (Throwable t){
			t.printStackTrace();
		}
		return retStr;
	}
	
	public String getVectorDiff(String str1, String str2){
		String retStr = "";
		String []arr1 = str1.split(",");
		String []arr2 = str2.split(",");
		if(arr1.length == 1){
			arr1 = getZeroArray();
		}
		if(arr2.length == 1){
			arr2 = getZeroArray();
		}
		for(int i=1;i<arr1.length;i++){
			double val1 = Double.parseDouble(arr1[i]);
			double val2 = Double.parseDouble(arr2[i]);
			double diff = val1 - val2;
			retStr = retStr + diff + ",";
			//System.out.println(val1 + "::" + val2 + "::" + diff);
		}
		
		return retStr;
	}
	
	public String[] getZeroArray(){
		String []retArr = new String[dimensions+1];
		for(int i=0;i<dimensions+1;i++){
			retArr[i] = "0";
		}
		return retArr;
	}
	
	public String[] getBlankArray(){
		String []retArr = new String[dimensions+1];
		for(int i=0;i<dimensions+1;i++){
			retArr[i] = "?";
		}
		return retArr;
	}

}

class StreamGobbler extends Thread
{
	InputStream is;
	String type;
	String line;

	StreamGobbler(InputStream is, String type)
	{
		this.is = is;
		this.type = type;
	}

	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line=null;
			while ( (line = br.readLine()) != null){
				//System.out.println(line);
				this.line = line;
			}
		} catch (IOException ioe)
		{
			ioe.printStackTrace();  
		}
	}
	
	public String getOutput(){
		return line;
	}
}

