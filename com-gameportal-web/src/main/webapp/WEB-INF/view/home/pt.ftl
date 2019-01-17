<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<script type="text/javascript">
// get the current hash
var loc = window.location.toString();
var hashValue = "";
var ind = loc.indexOf("#");
if (ind > 0) {
	hashValue = loc.substring(ind + 1);
}

var params = hashValue.split("&");
var queryStringList = [];
 
for(var i=0;i<params.length;i++) {
	var ind = params[i].indexOf("=");
	if (ind > 0) {		
		queryStringList[params[i].substring(0, ind)] = 
decodeURIComponent(params[i].substring(ind + 1).replace(/\+/g, '%20'));
	};
};

//call redirect callback
var tries = 0;
while (! tryCallback(queryStringList)) {
	tries++;
	if (tries > 5) {
		break;
	};
	var dom = document.domain;	
	var tmp = dom.split('.');	
	if (tmp.length > 2 && isNaN(tmp[tmp.length-1])) {		
		if (tmp.length > 3 && tmp[tmp.length-2].length < 3) {
			break;
		};
		document.domain = dom.substring(dom.indexOf('.') + 1);
	}
	else {
		break;
	};
};

function tryCallback(queryStringList) {
	var called = false;
	try {
		if (!called && top.iapiRedirectCallback) {
			top.iapiRedirectCallback(queryStringList);
			called = true;
		}
	}
	catch(err)
	{
	};
	
	try {
		if (!called && window.opener && window.opener.iapiRedirectCallback) {
			window.opener.iapiRedirectCallback(queryStringList);
			called = true;
		}
	}
	catch(err)
	{
	};
	
	try {
		if (!called && top.opener && top.opener.iapiRedirectCallback) {
			top.opener.iapiRedirectCallback(queryStringList);
			called = true;
		}
	}
	catch(err)
	{
	};
	return called;
};

</script>

</head>
<body>

</body>
</html>