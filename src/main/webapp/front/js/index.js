$(document).ready(function(){
	$('.text-flash').bumpyText();
	 $('body').GalMenu({
         'menu': 'GalDropDown'
      });
	 
	
	 $("#sendEmailForm").on("submit", function(ev) { 
		 $("#sendEmailBtn").attr("disabled","disabled");
		 ZENG.msgbox.show("邮件发送中，请稍后...", 6);
		 var url = $("#base").attr("href")+"/email/send";
		 var formData = $("#sendEmailForm").serialize();
		 var tip = "发送失败"
		 var tipIcon = 5;//图标类型，1为普通提示图标，4为成功提示图标，5为失败图标，6为加载图标
		 $.ajax({
			    url:url,
			    type:'POST', 
			    data:formData,
			    dataType:'json',    
			    success:function(data,textStatus,jqXHR){
			    	$("#sendEmailBtn").attr("disabled",false);
			    	ZENG.msgbox._hide();
			        if(data.status == 1){
			        	tip = "发送成功"
				        tipIcon = 4
			        }
			        ZENG.msgbox.show(tip, tipIcon, 2000);
			        $("#sendEmailForm")[0].reset()
			    },
			    error:function(xhr,textStatus){
			        console.log(xhr)
			        $("#sendEmailBtn").attr("disabled",false);
			    	ZENG.msgbox._hide();
			        ZENG.msgbox.show("服务器繁忙，请稍后再试", tipIcon, 2000);
			    }
			});
         //阻止submit表单提交  
         ev.preventDefault();  
         //或者return false  
         //return false;  
     });  
	 
	 
});





var t = 0
var j = 0;
var k = 0;
var showString= "小编年方18,英俊潇酒。七岁学文，九岁习武，12岁会泡妞，上知天文地理，下知鸡毛蒜皮，每外出行走，常引美女回头，帅哥跳楼，心地善良，乐于助人。。。"
var showStrOne = "小学时语文课老师讲解“帅哥”含义，小编百思不得其解，同桌偷偷递过小镜子。小编一照。哦。刹那间明白了。。。"
var showStrTwo = "据说，小编出生时，天空的北方，出现祥云一片，渐渐由远至近，飘到小编家房顶后，幻化成一个字：帅。。。"
function marquee(){
var stringLength = showString.length
var str = $(".text_animate").html() + showString.charAt(t);
$(".text_animate").html(str);
t++
var timeID= setTimeout("marquee()",100);
if (t >= stringLength)
	{clearTimeout(timeID); t=0;marqueeOne();}
}

function marqueeOne(){
	var stringLength = showStrOne.length
	var str = $(".text_animate_one").html() + showStrOne.charAt(j);
	$(".text_animate_one").html(str);
	j++
	var timeID= setTimeout("marqueeOne()",100);
	if (j >= stringLength)
		{clearTimeout(timeID); j=0; marqueeTwo();}
	}

function marqueeTwo(){
	var stringLength = showStrTwo.length
	var str = $(".text_animate_two").html() + showStrTwo.charAt(k);
	$(".text_animate_two").html(str);
	k++
	var timeID= setTimeout("marqueeTwo()",100);
	if (k >= stringLength)
		{clearTimeout(timeID); k=0;}
	}
 
function adjustHeightOfPage(pageNo) {

	// Get the page height
	var totalPageHeight = 15
			+ $('.cd-slider-nav').height()
			+ $(
					".cd-hero-slider li:nth-of-type(" + pageNo
							+ ") .js-tm-page-content").height() + 160
			+ $('.tm-footer').height();

	// Adjust layout based on page height and window height
	if (totalPageHeight > $(window).height()) {
		$('.cd-hero-slider').addClass('small-screen');
		$('.cd-hero-slider li:nth-of-type(' + pageNo + ')').css("min-height",
				totalPageHeight + "px");
	} else {
		$('.cd-hero-slider').removeClass('small-screen');
		$('.cd-hero-slider li:nth-of-type(' + pageNo + ')').css("min-height",
				"100%");
	}

}

/*
 * Everything is loaded including images.
 */
$(window)
		.load(
				function() {

					adjustHeightOfPage(1); // Adjust page height

					/*
					 * Gallery pop up -----------------------------------------
					 */
					$('.tm-img-gallery').magnificPopup({
						delegate : 'a', // child items selector, by clicking on
						// it popup will open
						type : 'image',
						gallery : {
							enabled : true
						}
					});

					/*
					 * Collapse menu after click
					 * -----------------------------------------
					 */
					$('#tmNavbar a').click(function() {
						$('#tmNavbar').collapse('hide');

						adjustHeightOfPage($(this).data("no")); // Adjust page
						// height
					});

					/*
					 * Browser resized -----------------------------------------
					 */
					$(window)
							.resize(
									function() {
										var currentPageNo = $(
												".cd-hero-slider li.selected .js-tm-page-content")
												.data("page-no");
										adjustHeightOfPage(currentPageNo);
									});

					// Remove preloader
					// https://ihatetomatoes.net/create-custom-preloading-screen/
					$('body').addClass('loaded');

				});

