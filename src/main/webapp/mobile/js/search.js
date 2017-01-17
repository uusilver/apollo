var page=2,totalPage=0,haveNext=true;
var menuFlage = true;
function showMenu(obj){
	if(menuFlage){
		$('#menuMore').removeClass("head-more").addClass("head-more-unfold");
		$("#menu").show();
		menuFlage = false;
	}else{
		$('#menuMore').removeClass("head-more-unfold").addClass("head-more");
		$("#menu").hide();
		menuFlage = true;
	}
}
function more(word) {
	$(".more").html("<img src='"+path+"/images/wml/loading.gif'/>");
	$.ajax({
		url : path + "/wml/searchJson.do?p=" + page + "&q=" +encodeURIComponent(word),
		type : "get",
		async : true,
		dataType : "json",
		success : function(data) {
			$("#nextPage").append(_.template($("#nextWML").html(), {
				items : data.items,
				num : page
			}));
			totalPage = data.totalPage;
			haveNext = data.haveNext;
			if(!haveNext){
				$("#nextPageBut").hide();
			}
			page++;
			$(".more").html("<span>下一页</span>");
		},
		error : function() {
			alert("链接超时,请检查您的网络！");
		}
	});
}
function clearbuttomEnd(){
	var word = $("#search-end").val();
	if(word != "" ){
		$("#cross-end").show();
	}else{
		$("#cross-end").hide();
	}
}
function clearInputEnd(){
	$("#search-end").val("");
	$("#cross-end").hide();
}

$(document).ready(function() {
	$("#search-end").click(function(e){
		clearbuttomEnd();
	});
	$("#search-end").bind("keyup", function(e){
		var key = e.keyCode;  
	   if( key == 40 ){
	   }else if( key == 38){
	   }else if( key == 39){
	   }else if( key == 37){
	   }else{
		   clearbuttomEnd();
	   }
	});
	clearbuttomEnd();
});