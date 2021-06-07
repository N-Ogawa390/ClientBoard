/**
 * 3Dカルーセル
 */
window.onload = function(){
	
	var carousel = document.querySelector('.carousel3d'),
		figure = carousel.querySelector('figure'),
		nav = carousel.querySelector('nav'),
		numImages = figure.childElementCount,
		curImage = 0,
		theta = 360 / numImages,
		img = figure.children[0],
		imgWidth = 400,
//		imgWidth = parseFloat(getComputedStyle(img).width),
		disCenter = (imgWidth / 2) * Math.tan((90 - (180 / numImages)) * (Math.PI / 180));	//画像から中心に垂線を引いた距離
		
	figure.style.transformOrigin = `center center -${disCenter}px`;
	for (i=1; i < numImages; i++) {
		figure.children[i].style.transform = `rotateY(${i * theta}deg)`;
		figure.children[i].style.transformOrigin = `center center -${disCenter}px`;
	}
	
	//カルーセル回転
	nav.addEventListener('click', navCarousel);

	function navCarousel(e) {
		var t = e.target;
		if (t.tagName.toUpperCase() != 'BUTTON'){
			return;
		}
		if (t.classList.contains('next')) {
			curImage++;
		}
		else {
			curImage--;
		}
		figure.style.transform = `rotateY(-${theta * curImage}deg)`;
	}
	
	//imgクリック時のイベント(画像をポップアップ)を追加
	var images = figure.querySelectorAll('img');
	var b = document.querySelector('body');
	
	images.forEach(function(i){
		i.addEventListener('click', function(e){
			var dispImg = e.target.cloneNode();
			
			dispImgFnc(dispImg);
		});
	});
	
	//画像にクリックイベントを付与してから表示
	function dispImgFnc(dispImg){
		dispImg.style.transform = null;
		dispImg.classList.add('disp-img', 'show');
		dispImg.addEventListener('click', function(e){
			var t = e.target;
//			var bs = document.getElementsByClassName('block-screen').item(0);
			t.classList.remove('show');
			t.classList.add('display-none');
			t.addEventListener('animationend', function(){
				t.parentNode.removeChild(t);
//				bs.parentNode.removeChild(bs);
			});
		});
		b.prepend(dispImg);
	}
}