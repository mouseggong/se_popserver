var url = "http://61.255.45.175:28080";
function get_news_list(keyword,callback){ //콜백함수 들고간다.
	$.get(url+"/search/news/"+keyword, function(data) { //서버로부터 데이터를 가져온다.
	//get 방식 ajax요청(파일의 위치, 함수)
   //첫번째 매개변수는 요청 주소이고
   //두번째 매개변수는 성공했을 때 호출되는 메서드이며
   //이 메서드의 매개변수가 가져온 데이터입니다.
   //여기서 data에는 기사가 들어가있다.(데이터는 배열)
		callback(data); // 이걸 모르겠다. 데이터를 처리하는 루틴인듯?
	});
}
function get_keyword_list(keyword,callback){ //콜백함수 들고간다.
	$.get(url+"/search/keyword/"+keyword, function(data) { //서버로부터 데이터를 가져온다.
	//get 방식 ajax요청(파일의 위치, 함수)
   //첫번째 매개변수는 요청 주소이고
   //두번째 매개변수는 성공했을 때 호출되는 메서드이며
   //이 메서드의 매개변수가 가져온 데이터입니다.
   //여기서 data에는 기사가 들어가있다.(데이터는 배열)
		callback(data); // 이걸 모르겠다. 데이터를 처리하는 루틴인듯?
	});
}
// function JSON2CSV(objArray) {


// 	var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
// 	var str = '';
// 	var line = '';
// 	for (var i = 0; i < array.length; i++) {
// 		var line = '';
// 		for (var index in array[i]) {
// 			line+=index+',';
// 			line += array[i][index] + '\n';
// 		}	
// 		line = line.slice(0, -1);
// 		str += line + '\r\n';
// 	}
// 	return str;
// }     
// $("#convert").click(function() {
// 	var json = $.parseJSON(url+"/tocsv");
// 	var csv = JSON2CSV(json);
// 	$("#csv").val(csv);
// });