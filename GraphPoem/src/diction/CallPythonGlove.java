package diction;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class CallPythonGlove {
	public static int dimensions = 100;
	
	static String basePath = System.getProperty("user.dir") + "/resources/diction/python/";
	
	Properties prop = new Properties();
	Properties prop2 = new Properties();
	
	public static void main(String args[]) {
		CallPythonGlove obj = new CallPythonGlove();
		//System.out.println(obj.getWord2VecSimilarity("pigs@fly"));
		
		String out = obj.cosineSimilarity("0.014314,0.68838,1.0354,-0.22272,0.7122,-1.8591,-0.54074,0.3788,-0.4473,0.39239,0.015253,-0.91126,-0.75051,-0.20166,-0.38735,-0.76363,-0.30962,0.46411,0.086319,-0.48519,-0.23435,-0.47433,-0.0079533,-0.11603,-0.51613,0.903,-0.089598,0.57729,-0.22603,0.72144,-0.25002,0.1062,-0.94918,-0.084096,-0.62692,-0.17111,0.1704,0.38448,0.039208,-0.12554,0.17141,-1.1132,-0.5683,-0.093456,-0.028492,-0.01072,-0.58381,0.46759,-0.15049,-0.59781,0.18358,-0.11986,0.14094,1.0061,0.35668,-2.1176,-0.11615,0.14937,1.6014,0.3073,0.68025,-0.69607,-0.69121,-0.23822,1.0161,0.46592,0.5549,-0.12957,1.3661,-0.39927,0.70148,-0.023808,0.42136,-0.4654,0.11011,-0.57907,0.19514,-1.0146,-1.5497,0.051181,0.96762,0.62337,-0.70582,0.20581,-1.0544,0.28568,0.61272,0.14516,-0.060257,-1.352,-0.05806,-0.26566,-0.51645,-0.085122,-1.0167,0.25942,-0.23039,-0.52353,0.79593,-0.30368,","-0.56658,0.47168,0.051911,-0.19937,-0.51219,-0.18559,-0.85573,0.06785,0.59259,0.0024437,0.31345,-0.26052,-0.26158,-0.026186,0.31173,-1.0164,-0.58087,0.33592,0.16951,-0.091623,-0.39424,-0.47751,-0.34073,0.31372,-0.77782,-0.1394,-0.30158,-0.081483,0.57756,-0.35911,-0.070971,0.30034,-1.0576,-0.2258,-0.56295,-0.46942,0.586,-0.0016982,-0.38696,-0.24488,0.062917,-0.67739,-1.1454,-0.17618,-0.025537,0.81873,0.42091,0.40993,-0.40172,-0.33557,-0.44685,0.026867,0.3702,0.43807,0.30045,-0.6108,0.34876,-0.51135,0.87745,-0.2865,0.60656,0.66837,-0.66813,0.31779,0.53989,-0.074489,0.65069,-0.18312,0.39519,-1.2348,0.56249,-0.73669,-0.010709,0.1747,0.30228,-0.11739,-0.49814,-0.80352,-0.096242,0.59162,0.64676,-0.037521,-1.0331,0.29191,-0.13073,-0.36302,0.50729,0.40869,-0.26244,0.51416,0.4713,-0.17076,-0.19382,-1.1265,-0.48035,-0.15289,0.14812,-0.38841,0.10561,0.15264,");
		
		String ret1 = obj.getProp2("pigs");
		String ret2 = obj.getProp2("flyzzzz");
		System.out.println(ret1);
		System.out.println(ret2);
		String retStr = obj.getVectorDiff(ret1, ret2);
		System.out.println(retStr);
	}
	
	CallPythonGlove(){
		initProp();
	}
	
	private void initProp() {
		try {
			prop.load(new FileInputStream(basePath + "word2vec_glove.properties"));
			prop2.load(new FileInputStream(basePath + "word2vec2_glove.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveProp() {
		try {
			prop.store(new FileOutputStream(basePath + "word2vec_glove.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveProp2() {
		try {
			prop2.store(new FileOutputStream(basePath + "word2vec2_glove.properties"), null);
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
		String line = "python D:/PythonWorkSpace/WordEmbeddings/WordEmbGloveCommand.py " + word1 + " " + word2;

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
		String line = "python D:/PythonWorkSpace/WordEmbeddings/WordEmbGloveVecCommand.py " + key;

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
		for(int i=0;i<arr1.length;i++){
			double val1 = Double.parseDouble(arr1[i]);
			double val2 = Double.parseDouble(arr2[i]);
			double diff = val1 - val2;
			retStr = retStr + diff + ",";
			//System.out.println(val1 + "::" + val2 + "::" + diff);
		}
		
		return retStr;
	}
	
	public String cosineSimilarity(String str1, String str2) {
		double out = 0.0;
		String []arr1 = str1.split(",");
		String []arr2 = str2.split(",");
		if(arr1.length == 1){
			arr1 = getZeroArray();
		}
		if(arr2.length == 1){
			arr2 = getZeroArray();
		}
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
		for(int i=0;i<arr1.length;i++){
			double val1 = Double.parseDouble(arr1[i]);
			double val2 = Double.parseDouble(arr2[i]);
	        dotProduct += val1 * val2;
	        normA += Math.pow(val1, 2);
	        normB += Math.pow(val2, 2);
			//retStr = retStr + diff + ",";
			//System.out.println(val1 + "::" + val2 + "::" + diff);
		}

	    out = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	    return String.valueOf(out);
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

