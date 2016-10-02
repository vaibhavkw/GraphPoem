package gibbsimpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

// Read the Gibbs LDA model file to get topic probabilities of each poem
public class GibbsInferencer {
	public static String basePath = System.getProperty("user.dir") + "/resources/";

	public static String basePath2 = System.getProperty("user.dir") + "/resources/gibbs/";

	ArrayList<String> poemList = new ArrayList<String>();

	public static void main(String args[]){
		GibbsInferencer obj = new GibbsInferencer();
		obj.readGibbsModel();
		//obj.writeToFile("PoemList.txt", obj.poemList);
	}

	public void readGibbsModel(){
		File filePoemList = new File(basePath2 + "PoemList.txt");
		File fileModel = new File(basePath2 + "model-final.theta");

		BufferedReader brList = null;
		BufferedReader brModel = null;
		String poemName = "";
		String probabilities = "";
		try{
			brList = new BufferedReader(new FileReader(filePoemList));
			brModel = new BufferedReader(new FileReader(fileModel));
			while ((poemName = brList.readLine()) != null) {
				probabilities = brModel.readLine();
				String []strArr = probabilities.split(" ");
				double highProb = 0.0;
				int highTopic = -1;
				for(int m=0;m<strArr.length;m++){
					double prob = Double.parseDouble(strArr[m]);
					if(prob > highProb){
						highProb = prob;
						highTopic = m + 1;
					}
				}
				System.out.println(poemName + ":" + (int)(highProb*100) + "%:" + highTopic);
				File poemToCopy = new File(basePath + "poetry_foundation_final/" + poemName);
				File destFolder = new File(basePath2 + "categories/" + highTopic + "/" + poemName);
				Files.copy(poemToCopy.toPath(), destFolder.toPath());
			}

			brList.close();
			brList = null;
			filePoemList = null;

			brModel.close();
			brModel = null;
			fileModel = null;

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (brList != null) {
					brList.close();
				}
				if (brModel != null) {
					brModel.close();
				}
				filePoemList = null;
				fileModel = null;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}


	public void writeToFile(String str, ArrayList<String> arr){
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File file = new File(basePath2 + str);

			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			for(int i=0;i<arr.size();i++){
				bw.write(arr.get(i) + "\n");
				bw.flush();
			}			
			bw.close();

			System.out.println(str + " file written");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

}
