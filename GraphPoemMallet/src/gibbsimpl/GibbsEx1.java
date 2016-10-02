package gibbsimpl;

import jgibblda.Inferencer;
import jgibblda.LDACmdOption;
import jgibblda.Model;

public class GibbsEx1 {
	
	public static String basePath = System.getProperty("user.dir") + "/resources/gibbs/";
	
	  public static void main(String[] args) {
		  LDACmdOption ldaOption = new LDACmdOption();
		  ldaOption.est = true; 
		  ldaOption.dir = basePath; 
		  //ldaOption.modelName = "newdocs"; 
		  ldaOption.niters = 1000;
		  
		  Inferencer inferencer = new Inferencer(); 
		  inferencer.init(ldaOption);
		  

		  ldaOption.dfile = "csv2.dat"; 
		  Model newModel = inferencer.inference();
		  
	  }

}
