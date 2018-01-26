<%@ page import ="diction.*,concordance.*,java.io.*,java.util.*,weka.classifiers.trees.*,weka.classifiers.*,weka.core.*" %>


<%!  
	public static String basePath = "/resources/diction/";

	DictionCommon obj;
	
	StringBuffer outStr = new StringBuffer();

%>
<% 
	String inputText = request.getParameter("hidText");
	outStr = new StringBuffer();
	
	if(inputText == null){
		//out.println("null");
	}else{
		System.out.println("Starting..");
		obj = new DictionCommon();
	
		String newline = System.getProperty("line.separator");
		String []newlineArr = inputText.split(newline);

		String output = obj.processLineArray(newlineArr);
		System.out.println(output);
		output.replaceAll("\\n","<br>");
		System.out.println(output);
		outStr.append(output);
	}

%>
<html>
  <head>
    <title>Diction in Poetry</title>
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
	document.getElementById('mspan').innerHTML = "";
	document.getElementById('mform').submit();
}
</script>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title" onClick="location.href='index.html'"><center>Diction Analysis in Poetry</center></h3>
	</div>
</div>
  <center>
    <br>
	Enter text:
	<form id="mform" method="post" action="diction.jsp">
		<textarea rows="12" cols="55" id="inputText"></textarea>
		<input type="hidden" name="hidText" id="hidText"></input>
		<br>
		<button type="button" class="btn btn-primary center-block" id="inputButton" value="Process" onclick="mclick();">Process</button>
	</form>
	<style>
		 p {
		  white-space: pre;
		 }
	</style>
	<div class="panel panel-default">
		<p>
			<span id="mspan"><%= outStr.toString() %>
			</span>
		</p>
	</div>
	</center>
  </body>
</html>
