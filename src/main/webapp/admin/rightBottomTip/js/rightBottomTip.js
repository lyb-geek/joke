var base = $("#base").attr("href");
var stompClient = null;
$(document).ready(function(){
	connect();
	setInterval("close()",12000);
	$(".popClose").click(function(){
		$("#PcPoPmarket").fadeOut(1000);
	});
});

function connect() {
	var socket = new SockJS(base+'/notify');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/notify', function(data) {
			showMessage(JSON.parse(data.body));
		});
	});
}

function showMessage(message) {
	$("#PcPoPmarket").fadeIn(1000);
	$("#content").text(message.content);
	
}

function close(){
	if(!$(".PcPoPmarket").is(':hidden')){
		 $("#PcPoPmarket").fadeOut(1000);
	}
	
}