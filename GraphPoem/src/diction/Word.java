package diction;

public class Word {
	
	private String wordText = "";
	
	private String postag = "";

	public String getWordText() {
		return wordText;
	}

	public void setWordText(String wordText) {
		this.wordText = wordText;
	}

	public String getPostag() {
		return postag;
	}

	public void setPostag(String postag) {
		this.postag = postag;
	}
	
	@Override
	public String toString(){
		return wordText + "::" + postag;
	}

}
