$(function() {
	var base = $("#base").attr("href") + "/front/img/";
	$("#audio_btn").click(function() {
		var music = document.getElementById("music");
		if (music.paused) {
			music.play();
			$("#music_btn").attr("src",base+"play.png");
		} else {
			music.pause();
			$("#music_btn").attr("src",base+"pause.png");
		}
	});
	[]

})