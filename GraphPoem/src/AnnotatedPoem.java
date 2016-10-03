import java.util.ArrayList;
import java.util.HashMap;


public class AnnotatedPoem extends Poem {
	
	ArrayList<ArrayList<String>> annotation;
	
	HashMap<Integer, Integer> paraPause;

	public ArrayList<ArrayList<String>> getAnnotation() {
		return annotation;
	}
	
	public ArrayList<String> getAnnotationLine(int k) {
		return annotation.get(k);
	}

	public void setAnnotation(ArrayList<ArrayList<String>> annotation) {
		this.annotation = annotation;
	}

	public HashMap<Integer, Integer> getParaPause() {
		return paraPause;
	}

	public void setParaPause(HashMap<Integer, Integer> paraPause) {
		this.paraPause = paraPause;
	}
	
	

}
