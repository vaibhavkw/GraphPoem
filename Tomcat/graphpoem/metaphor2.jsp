<%@ page import ="diction.*,java.io.*,java.util.*,weka.classifiers.trees.*,weka.classifiers.*,weka.core.*" %>


<%!  
	public static String basePath = "/resources/diction/";

	protected Classifier m_Classifier = null;

	protected String m_TrainingFile = null;

	protected Instances m_Training = null;

	Instance inst = null;

	MetaphorGeneric objMG;

	static int percent = 50;
	
	StringBuffer outStr = new StringBuffer();

%>
<% 
	String inputText = request.getParameter("hidText");
	outStr = new StringBuffer();
	
	if(inputText == null){
		//out.println("null");
	}else{
		System.out.println("Starting..");
		long starttime = System.currentTimeMillis();

		WekaModel obj = new WekaModel();
		obj.loadModel();
		
		objMG = new MetaphorGeneric();
		System.out.println("Starting init..");
		objMG.preInit();
		System.out.println("Init done");
		
		obj.objMG = objMG;

		//String inputText = "he is the light of my life";
		
		String newline = System.getProperty("line.separator");
		String []newlineArr = inputText.split(newline);	
		System.out.println("Lines : " + newlineArr.length);
		
		for(int t=0;t<newlineArr.length;t++){
			if(t!=0){
				outStr.append("<br><br>");
			}
			outStr.append("Line : " + newlineArr[t]);
			ArrayList<String> list = objMG.getWordPairs(newlineArr[t].toLowerCase());

			for(int i=0;i<list.size();i++){
				String val = list.get(i);
				//out.println("Processing " + val);
				outStr.append("<br><br>Processing " + val.replace("##", " : "));
				String []arr = val.split("##");
				//out.println("\nPrediction : " + obj.predict(arr[0], arr[1]));
				outStr.append("<br>Prediction : " + obj.predict(arr[0], arr[1]));
			}
		}
		

		long endtime = System.currentTimeMillis();
		//out.println("\nTime taken : " + (endtime-starttime)/1000.0 + " secs");
		outStr.append("<br><br>Time taken : " + (endtime-starttime)/1000.0 + " secs");
	
	}

%>
<html>
  <head>
    <title>Metaphor Detection</title>
	    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta http-equiv='cache-control' content='no-cache'>
	<meta http-equiv='expires' content='0'>
	<meta http-equiv='pragma' content='no-cache'>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
	
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

  </head>
  <body>
  <script>
function mclick(){
	//alert('new');
	if(document.getElementById('inputText').value == ''){
		alert('Nothing entered.');
		return;
	}
	//alert(document.getElementById('inputText').value);
	document.getElementById('hidText').value = document.getElementById('inputText').value;
	//alert(document.getElementById('hidText').value);
	document.getElementById('inputButton').value = "Processing";
	document.getElementById('inputButton').disabled = true;
	document.getElementById('mspan').value = "";
	document.getElementById('mform').submit();
}
</script>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title" onClick="location.href='index.html'"><center>Metaphor Detection in Natural Language</center></h3>
	</div>
</div>
  <center>
    <br>
	Enter text:
	<form id="mform" method="post" action="metaphor2.jsp">
		<!--<input type="textarea" id="inputText" size="100" rows="4" cols="50" ></input> -->
		<textarea rows="12" cols="55" id="inputText"></textarea>	
		<input type="hidden" name="hidText" id="hidText"></input>
		<br><br>
		<button type="button" class="btn btn-primary center-block" id="inputButton" value="Process" onclick="mclick();">Process</button>
	</form>
	<div class="panel panel-default">
	<span id="mspan"><%= outStr.toString() %>
	</span>
	</div>
	</center>
  </body>
</html>
