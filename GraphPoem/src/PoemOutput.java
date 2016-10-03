

public class PoemOutput {
	
	private String title;
	
	private String author;
	
	private String fileName;
	
	double eyeRhymeScore;
	
	double endRhymeScore;
	
	double slantRhymeScore;
	
	double fullRhymeScore;
	
	double identicalRhymeScore;
	
	double internalRhymeScore;
	
	double richRhymeScore;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public double getEyeRhymeScore() {
		return eyeRhymeScore;
	}

	public void setEyeRhymeScore(double eyeRhymeScore) {
		this.eyeRhymeScore = eyeRhymeScore;
	}

	public double getEndRhymeScore() {
		return endRhymeScore;
	}

	public void setEndRhymeScore(double endRhymeScore) {
		this.endRhymeScore = endRhymeScore;
	}

	public double getSlantRhymeScore() {
		return slantRhymeScore;
	}

	public void setSlantRhymeScore(double slantRhymeScore) {
		this.slantRhymeScore = slantRhymeScore;
	}

	public double getFullRhymeScore() {
		return fullRhymeScore;
	}

	public void setFullRhymeScore(double fullRhymeScore) {
		this.fullRhymeScore = fullRhymeScore;
	}

	public double getIdenticalRhymeScore() {
		return identicalRhymeScore;
	}

	public void setIdenticalRhymeScore(double identicalRhymeScore) {
		this.identicalRhymeScore = identicalRhymeScore;
	}

	public double getInternalRhymeScore() {
		return internalRhymeScore;
	}

	public void setInternalRhymeScore(double internalRhymeScore) {
		this.internalRhymeScore = internalRhymeScore;
	}

	public double getRichRhymeScore() {
		return richRhymeScore;
	}

	public void setRichRhymeScore(double richRhymeScore) {
		this.richRhymeScore = richRhymeScore;
	}
	
	public String printResult(){
		StringBuffer outStr = new StringBuffer();
		outStr.append("End Rhyme:" + endRhymeScore + "\n");
		outStr.append("Internal Rhyme:" + internalRhymeScore + "\n");
		outStr.append("Eye Rhyme:" + eyeRhymeScore + "\n");
		
		outStr.append("Full Rhyme:" + fullRhymeScore + "\n");
		outStr.append("Rich Rhyme:" + richRhymeScore + "\n");
		outStr.append("Identical Rhyme:" + identicalRhymeScore + "\n");
		outStr.append("Slant Rhyme:" + slantRhymeScore + "\n");
		
		return outStr.toString();
	}
}
