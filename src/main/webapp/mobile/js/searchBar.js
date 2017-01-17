var historyLen = 5;
function setHistory(){
	var val = $("#search-top").val();
	if(val == null || val == "" || val == "null"){
		return;
	}
	var countArray = new Array();
	var history = localStorage.getItem("wmlSearchHistory");
	var flag = true;
	if (history != null && history != "[]" && history != "null" && history != ""){
		countArray = countArray.concat(JSON.parse(history));
	}
	for(var i=0;i<countArray.length;i++){
		if(val == countArray[i]){
			flag = false;
		}
	}
	if(flag){
		var arr = new Array();
		arr[0] = val;
		var len = Math.min(countArray.length,historyLen);
		for(var i=0;i<len;i++){
			arr[i+1] = countArray[i];
		}
		localStorage.setItem("wmlSearchHistory",JSON.stringify(arr));
	}
}
function showHistoryLi(){
	var countArray = new Array();
	var history = localStorage.getItem("wmlSearchHistory");
	if (history != null && history != "[]" && history != "null" && history != ""){
		countArray = countArray.concat(JSON.parse(history));
		divHistoryLi(countArray);
	}
}
function divHistoryLi(arr){
	var div = "";
	for(var i=0;i<arr.length;i++){
		if(i == arr.length-1){
			div += "<div class=\"search-list last\">";
		}else{
			div += "<div class=\"search-list\">";
		}
		div += "<span  onclick=\"outSearch('"+arr[i]+"')\" class=\"txt\">"+arr[i]+"</span>";
		div += "<em onclick=\"addInput('"+arr[i]+"')\" class=\"add\"></em>";
		div += "</div>";
	}
	$("#autoComplete").empty();
	$(".edit").show();
	$("#autoComplete").append(div);
	$(".search-msg").show();
}
String.prototype.replaceAll = function(reallyDo, replaceWith, ignoreCase) {
    if (!RegExp.prototype.isPrototypeOf(reallyDo)) {  
        return this.replace(new RegExp(reallyDo, (ignoreCase ? "gi": "g")), replaceWith);  
    } else {  
        return this.replace(reallyDo, replaceWith);  
    }  
}
function autoComplete(){
	var word = $("#search-top").val();
	$.ajax({
		url : path + "/autoComplete.htm?q=" +encodeURIComponent(word),
		type : "get",
		async : true,
		dataType : "json",
		success : function(data) {
			var size = data.size;
			var items = data.items;
			var div = "";
			if(size > 0){
				for(var i=0;i<size;i++){
					var kv = items[i];
					if(kv.length == 2){
						div += kv[1];
					}else{
						var value = kv[0].replaceAll(word,"<font>" + word + "</font>" ,true);
						if(i == size-1){
							div += "<div class=\"search-list last\">";
						}else{
							div += "<div class=\"search-list\">";
						}
						div += "<span  onclick=\"outSearch('"+kv[0]+"')\" class=\"txt\">"+value+"</span>";
						div += "<em onclick=\"addInput('"+kv[0]+"')\" class=\"add\"></em>";
						div += "</div>";
					}
				}
				$("#autoComplete").empty();
				$(".edit").hide();
				$("#autoComplete").append(div);
				$(".search-msg").show();
			}else{
				$(".search-msg").hide();
			}
		}
	});
}
function submitNot(id){
	var val = $("#"+id).val();
	if(val == null || val == ""){
		return false;
	}
}
function clearHistory(){
	localStorage.setItem("wmlSearchHistory",null);
	shutDown();
}
function clearbuttom(){
	var word = $("#search-top").val();
	if(word != "" ){
		$("#cross-top").show();
	}else{
		$("#cross-top").hide();
	}
}
function clearInput(){
	addInput("");
	shutDown();
	$("#cross-top").hide();
	showHistoryLi();
}
function shutDown(){
	$(".search-msg").hide();
}
function addInput(word){
	$("#search-top").val(word);
}
function outSearch(word){
	addInput(word);
	$("#searchForm").submit();
}
$(document).ready(function() {
	$("#search-top").click(function(e){
		e.stopPropagation();
		if( $.trim($(this).val()) == "" ){
			$(".search-msg").hide();
			showHistoryLi();
		}else{
			autoComplete();
		}
		clearbuttom();
	});
	$("#search-top").bind("keyup", function(e){
		var key = e.keyCode;  
	   if( key == 40 ){
	   }else if( key == 38){
	   }else if( key == 39){
	   }else if( key == 37){
	   }else{
		   var autocompleteVal= $.trim(e.target.value);
		   if( autocompleteVal != "" ){
			   autoComplete();
		   }else{
				$(".search-msg").hide();
				showHistoryLi();
		   }
		   clearbuttom();
	   }
	});
	clearbuttom();
	setHistory();
});