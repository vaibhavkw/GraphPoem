import java.util.ArrayList;


public class AnnotatedPoemHtml extends AnnotatedPoem {
	
	private ArrayList<String> origContent;
	

	public ArrayList<String> getOrigContent() {
		return origContent;
	}

	public void setOrigContent(ArrayList<String> origContent) {
		this.origContent = origContent;
	}
	
	public String getOrigContent(int index) {
		return origContent.get(index);
	}

	public void setOrigContent(String line, int index) {
		this.origContent.set(index, line);
	}
	

}
