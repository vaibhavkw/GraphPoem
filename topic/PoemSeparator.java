package topic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

public class PoemSeparator {

	public static String basePath = System.getProperty("user.dir") + "/resources/";

	ArrayList<String> poemList = new ArrayList<String>();

	HashMap<String, String> topicsMap = new HashMap<String, String>();

	public static void main(String[] args) {
		PoemSeparator obj = new PoemSeparator();
		obj.initializeTopicsMap();
		obj.readInputFolder();

	}

	public void initializeTopicsMap(){
		//Love subcategories
		topicsMap.put("Desire", "Love");
		topicsMap.put("Heartache & Loss", "Love");
		topicsMap.put("Realistic & Complicated", "Love");
		topicsMap.put("Romantic Love", "Love");
		topicsMap.put("Classic Love", "Love");
		topicsMap.put("Infatuation & Crushes", "Love");
		topicsMap.put("Unrequited Love", "Love");
		topicsMap.put("Break-ups & Vexed Love", "Love");
		topicsMap.put("First Love", "Love");

		//Nature subcategories
		topicsMap.put("Winter", "Nature");
		topicsMap.put("Spring", "Nature");
		topicsMap.put("Summer", "Nature");
		topicsMap.put("Fall", "Nature");
		topicsMap.put("Landscapes & Pastorals", "Nature");
		topicsMap.put("Weather", "Nature");
		topicsMap.put("Trees & Flowers", "Nature");
		topicsMap.put("Stars, Planets, Heavens", "Nature");
		topicsMap.put("Seas, Rivers, & Streams", "Nature");
		topicsMap.put("Animals", "Nature");

		//Social Commentaries subcategories
		topicsMap.put("History & Politics", "Social Commentaries");
		topicsMap.put("War & Conflict", "Social Commentaries");
		topicsMap.put("Crime & Punishment", "Social Commentaries");
		topicsMap.put("Money & Economics", "Social Commentaries");
		topicsMap.put("Race & Ethnicity", "Social Commentaries");
		topicsMap.put("Gender & Sexuality", "Social Commentaries");
		topicsMap.put("Class", "Social Commentaries");
		topicsMap.put("Popular Culture", "Social Commentaries");
		topicsMap.put("Cities & Urban Life", "Social Commentaries");
		topicsMap.put("Town & Country Life", "Social Commentaries");

		//Religion subcategories
		topicsMap.put("Faith & Doubt", "Religion");
		topicsMap.put("God & the Divine", "Religion");
		topicsMap.put("Judaism", "Religion");
		topicsMap.put("Buddhism", "Religion");
		topicsMap.put("Islam", "Religion");
		topicsMap.put("Christianity", "Religion");
		topicsMap.put("Other Religions", "Religion");
		topicsMap.put("The Spiritual", "Religion");

		//Living subcategories
		topicsMap.put("Birth & Birthdays", "Living");
		topicsMap.put("Infancy", "Living");
		topicsMap.put("Youth", "Living");
		topicsMap.put("Coming of Age", "Living");
		topicsMap.put("Marriage & Companionship", "Living");
		topicsMap.put("Parenthood", "Living");
		topicsMap.put("Separation & Divorce", "Living");
		topicsMap.put("Midlife", "Living");
		topicsMap.put("Growing Old", "Living");
		topicsMap.put("Health & Illness", "Living");
		topicsMap.put("Disappointment & Failure", "Living");
		topicsMap.put("Death", "Living");
		topicsMap.put("Sorrow & Grieving", "Living");
		topicsMap.put("Life Choices", "Living");
		topicsMap.put("The Body", "Living");
		topicsMap.put("The Mind", "Living");
		topicsMap.put("Time & Brevity", "Living");		

		//Relationships subcategories
		topicsMap.put("Men & Women", "Relationships");
		topicsMap.put("Friends & Enemies", "Relationships");
		topicsMap.put("Gay, Lesbian, Queer", "Relationships");
		topicsMap.put("Home Life", "Relationships");
		topicsMap.put("Family & Ancestors", "Relationships");
		topicsMap.put("Pets", "Relationships");
		
		//Activities subcategories		
		topicsMap.put("School & Learning", "Activities");
		topicsMap.put("Jobs & Working", "Activities");
		topicsMap.put("Travels & Journeys", "Activities");
		topicsMap.put("Sports & Outdoor Activities", "Activities");
		topicsMap.put("Gardening", "Activities");
		topicsMap.put("Eating & Drinking", "Activities");
		topicsMap.put("Indoor Activities", "Activities");
		
		//Arts & Sciences subcategories
		topicsMap.put("Philosophy", "Arts & Sciences");
		topicsMap.put("Sciences", "Arts & Sciences");
		topicsMap.put("Reading & Books", "Arts & Sciences");
		topicsMap.put("Poetry & Poets", "Arts & Sciences");
		topicsMap.put("Humor & Satire", "Arts & Sciences");
		topicsMap.put("Music", "Arts & Sciences");
		topicsMap.put("Theater & Dance", "Arts & Sciences");
		topicsMap.put("Painting & Sculpture", "Arts & Sciences");
		topicsMap.put("Architecture & Design", "Arts & Sciences");
		topicsMap.put("Photography & Film", "Arts & Sciences");
		topicsMap.put("Language & Linguistics", "Arts & Sciences");
		
		//Mythology & Folklore subcategories
		topicsMap.put("Ghosts & the Supernatural", "Mythology & Folklore");
		topicsMap.put("Horror", "Mythology & Folklore");
		topicsMap.put("Heroes & Patriotism", "Mythology & Folklore");
		topicsMap.put("Greek & Roman Mythology", "Mythology & Folklore");
		topicsMap.put("Fairy-tales & Legends", "Mythology & Folklore");		
		
	}

	public void readInputFolder(){
		String inputFolder = basePath + "poetry_foundation_final/";
		//BufferedWriter bw = null;		
		//FileWriter fw = null;
		//File file = new File(basePath + "poemContent.txt");
		HashMap<String, String> checkDuplMap = new HashMap<String, String>();
		try{
			//fw = new FileWriter(file.getAbsoluteFile());
			//bw = new BufferedWriter(fw);

			File inpFolder = new File(inputFolder);
			File[] children = inpFolder.listFiles();
			File destFolder = null;
			//bw.write(children.length + "\n");

			int count = -1;
			for(int loop=0; loop<children.length; loop++){
				count++;
				File currentFile = children[count];
				if(currentFile.isFile() && currentFile.getName().contains(".txt")){
					String fileName = currentFile.getName();
					System.out.println("Processing file " + (count+1) + " : " + fileName);
					poemList.add(fileName);

					BufferedReader br = null;
					String sCurrentLine;
					String poemName = "";
					String poemAuthor = "";
					try{
						br = new BufferedReader(new FileReader(currentFile));
						int lineNumber = 0;
						while ((sCurrentLine = br.readLine()) != null) {
							lineNumber++;
							if(lineNumber == 1){
								poemName = sCurrentLine;
							}
							if(lineNumber == 2){
								poemAuthor = sCurrentLine;
							}
							String subjects = "";
							if(lineNumber == 6){
								subjects = sCurrentLine;
								String []subArr = subjects.split(","); 
								for(int s=0;s<subArr.length;s++){
									String currTopic = subArr[s].trim();
									if(topicsMap.containsKey(currTopic)){
										String parentTopic = topicsMap.get(currTopic);
										destFolder = new File(basePath + "topic/categoriespoems/" + parentTopic);
										if(!destFolder.exists()){
											destFolder.mkdir();
										}
										destFolder = new File(basePath + "topic/categoriespoems/" + parentTopic + "/" + currTopic);
										if(!destFolder.exists()){
											destFolder.mkdir();
										}
										destFolder = new File(basePath + "topic/categoriespoems/" + parentTopic + "/" + currTopic + "/" +currentFile.getName());
										Files.copy(currentFile.toPath(), destFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
										System.out.println("Copied " + destFolder.getName());
									}
								}
							}
						}

						br.close();
						br = null;
						currentFile = null;

						//bw.write(fileName + "\t" + "X" + "\t" + poemContent + "\n");

						/*if(!poemContent.trim().equalsIgnoreCase("")){
							if(!checkDuplMap.containsKey(poemContent.trim())){
								checkDuplMap.put(poemContent.trim(), "X");
								//bw.write(fileName + "," + poemContent.trim() + "\n");
								addToWordFreqMap(poemContent.trim());
							}
						}*/

					} catch (IOException e) {
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

			}
			children = null;
			inpFolder = null;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		//System.out.println("poemContent.txt file written");

	}

}
