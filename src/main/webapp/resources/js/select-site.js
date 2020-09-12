/**
 * 
 */
var campName;
var imgs;
var campZone;
var maxPeople;
var category;
var campPrice;
var bbqPrice;
var peopleNum;





$(document).ready(function(){
	//avgStar Booking.jsp에 평점 띄우기 위해 localStorage로 넘김
	avgStar = $('#avgStar').val();
	console.log(" : "+avgStar+"type of : "+typeof avgStar);
	if(avgStar===""){
		avgStar="0.0";
	}
	localStorage.setItem("avgStar",avgStar);
})

function selectSite(index){
	//예약 버튼 활성화
	//document.getElementById('booking-event').style.pointerEvents = 'auto'; 
	$('#booking-event').empty();
	$('#booking-event').append("예약하기");
	
	
	
	//.input-number-decrement, .input-number-increment
	$("#peopleNum").bind('keyup mouseup',function(){
		peopleNum = Number(document.getElementById("peopleNum").value);
		//alert(peopleNum);
		if(peopleNum>=1){
			alert("1>>" +peopleNum);
			//예약 버튼 활성화
			document.getElementById('booking-event').style.pointerEvents = 'auto'; 
		}else{
			document.getElementById('booking-event').style.pointerEvents = 'none';
		}
	});

	// == $('#booking-event').text("예약하기");
	imgs = document.getElementById('img'+index).getAttribute('src');
	campName = document.getElementById('campName').value;
	campZone =  document.getElementById('campZone'+index).innerHTML;
	maxPeople = document.getElementById('maxPeople'+index).innerHTML;
	category = document.getElementById('category'+index).value;
	campPrice = document.getElementById('price'+index).innerHTML;
	bbqPrice = document.getElementById('bbqPrice').value;
	
	
	var imgs_html = "";
	imgs_html += "<img id='imgTest' src="+imgs+" alt='blog image'>";

	//document.getElementsByClassName("calender-customize")[0].value = "2/2/2020";
	//$('.calender-customize').val("2/2/2020");
	//var length = document.getElementsByClassName("calender-customize").length;
	//for(var i=0; i<length; i++){
	//	document.getElementsByClassName("calender-customize")[i].value = "2/2/2020";
	//}
	
	var imgs_html = "";
	imgs_html += "<img src="+imgs+" alt='blog image'>";
	$("#selectedImg").html(imgs_html);
	
	var selectedBody_html = "";
	selectedBody_html += "<h4 class='recent__link'>"
		+ campName+"("+category+") : "
    	+ "<br/><em>"+campZone+"</em></h4>"
    	+ "<p class='recent__meta'><span class='color-text font-weight-bold'>"+campPrice+"</span>"
    	+	"<br>최대인원 - "+maxPeople+"</p>";
	/*var checkBox_html = "";	
	checkBox_html += "<div class='custom-checkbox'>"
		+ "<input type='checkbox' id='chb'>"
		+ "<label for='chb'>바베큐 : "+bbqPrice+"원<span>+</span></label>"
		+ "</div>";*/
	var checkBox_html = "";
	checkBox_html += "<div class='custom-checkbox'>"
		+ "<input type='checkbox' id='chb' class='check-bbq' value='"+bbqPrice+"'>"
		+ "<label for='chb' style=''>바베큐 : "+bbqPrice+"<span>+</span></label>"
		+ "</div>";
		
		// peopleNum class 의 속성인 max 를 maxPeople 값으로 변경
		document.getElementById("peopleNum").setAttribute("max", maxPeople);
		//peopleNum class 의 value 값이 maxPeople보다 큰 경우 maxPoeple값으로 설정(ex 예약시 선택한 인원수가 6인데 선택한 캠핑장의 수용 가능 인원수가 4라면 6->4로 설정됨)
		peopleNum = Number(document.getElementById("peopleNum").value);
		if(peopleNum>Number(maxPeople)){
			alert("2>>"+peopleNum);
			document.getElementById("peopleNum").value = Number(maxPeople);
		}
		$("#selectedBody").html(selectedBody_html);
		$(".customize-checkbox").html(checkBox_html);
	
	//document.getElementsByClassName('peopleNum').setAttribute('max', maxPeople);
		
	window.location.href="#focusHere";
}

/*$('.card-body').on('click', function(event) {
    alert('You clicked the Bootstrap Card');
});*/