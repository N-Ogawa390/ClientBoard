/* 郵便番号から住所を検索 */
$(function(){
	$('#zipSearch').on('click', function() {
		const zipCode = document.getElementById('zipCode');
		const prefecture = document.getElementById('prefecture');
		const city = document.getElementById('city');
		const address = document.getElementById('address');
		const zipError = document.getElementById('zipError');
		
		var req = new XMLHttpRequest();
		var res;
		req.onreadystatechange = function() {
			if(req.readyState == 4) {
				if(req.status == 200) {
					
					res = JSON.parse(req.response);
					if(res.results == null) {
						
						//レスポンスがnullの場合の処理
						zipError.textContent = '※郵便番号が見つかりませんでした';
					} else {
						
						//レスポンスがnullでなければ更新処理
						prefecture.value = res.results[0].address1;
						city.value = res.results[0].address2;
						address.value = res.results[0].address3;
						zipError.textContent = '';
					}
				}
			} else {
			}
		}
		req.open('GET', 'https://zipcloud.ibsnet.co.jp/api/search?zipcode=' + zipCode.value, true);
		req.send();
	})
})