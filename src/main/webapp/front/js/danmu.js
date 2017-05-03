var dataNo = 0;
$(document).ready(function(){
	 $('#text_send').click(function() {
		 showContent();
	  });
	 
	 $("body").keydown(function(e) {
		 dataNo = $(".nav-item.selected").find(".nav-link").attr("data-no");
         if (e.keyCode == "13") {
            if(dataNo == 4){
            	 showContent();
            }else if(dataNo == 5){
            	//$("#sendEmailForm").submit();
            }
         }
     });
	 
	 initList();
	 setInterval("initList()",12000);
	 
	 
	
	 
});
 
 
 function showContent(){
	    // 获取内容，创建新元素，并设置位置追加到目标元素中
		var val = $('#text_content').val();
		var tip = "小编还没屌到可以猜出您输入空的意思！"
		var tipIcon = 1;//图标类型，1为普通提示图标，4为成功提示图标，5为失败图标，5为加载图标
		if(val == ""){
			ZENG.msgbox.show(tip, tipIcon, 2000);
			return;
		}
		
	    if(val.length > 25){
	    	tip = "您话太多了，小编最多只能听进25个字！"
	    	ZENG.msgbox.show(tip, tipIcon, 2000);
			return;
	    }
		
		var $content = $('<div class="text">' + val + '</div>');
		var top = Math.random() * $(document.body).height() - 50;
		$content.css({'top':top,"color":getRandomColor()});
		$('#mask').append($content); // 移动到最右侧，直接删除该元素
		ajaxComments(val);
		$("#text_content").val("");
		$content.animate({
			right : $(document.body).width() + 100
		}, 5000, function() {
			$(this).remove();
		}); 
 }
 
 
 var count = 0;
 function initList(){
	 dataNo = $(".nav-item.selected").find(".nav-link").attr("data-no");
	 if(dataNo == 4){
		 var url = $("#base").attr("href")+"/comments/list";
		 $.ajax({
			    url:url,
			    type:'POST', 
			    dataType:'json',  
			    success:function(data,textStatus,jqXHR){
			        if(data.status == 1){
			        	$.each(data.result,function(index, item){ 
			        		count++;
			        		 setTimeout(function () { 
			        			 showComments(item.comment);
			        		   }, 500);
			        	}); 
			        }
			    },
			    error:function(xhr,textStatus){
			        console.log(xhr);
			    }
			});
	 }
	
 }
 
 
 function showComments(val){
	 var $content = $('<div class="text">' + val + '</div>');
		var top = Math.random() * $(document.body).height() - 50;
		$content.css({'top':top,"color":getRandomColor()});
		$('#mask').append($content); // 移动到最右侧，直接删除该元素
//		var right = $(document.body).width() + 100;
//		if(count % 2 == 0){
//		   right = $(document.body).width() + 400*Math.random();
//		}
//		if(count % 3 == 0){
//			   right = $(document.body).width() + 600*Math.random();
//		  }
//		if(count % 4 == 0){
//			   right = $(document.body).width() + 800*Math.random();
//		  }
//		if(count % 5 == 0){
//			   right = $(document.body).width() + 1000*Math.random();
//		  }
		
		var right = 100 * Math.random()+"%";
		
		$content.animate({
			right : right
		}, 2200, function() {
			$(this).remove();
		});  
 }
 
 
 function ajaxComments(val){
	 var url = $("#base").attr("href")+"/comments/save";
	 $.ajax({
		    url:url,
		    type:'POST', 
		    data:{'comment':val},
		    dataType:'json',  
		    success:function(data,textStatus,jqXHR){
		        if(data.status == 1){
		        	console.log("success")
		        }
		    },
		    error:function(xhr,textStatus){
		        console.log(xhr)
		    }
		});
 }
 
 function getRandomColor(){
     return '#' + (function(h){
         return new Array(7 - h.length).join("0") + h
     })((Math.random() * 0x1000000 << 0).toString(16));
 }
 


