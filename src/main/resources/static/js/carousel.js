/**
 * 3Dカルーセル
 */
window.addEventListener('load', () => {
	
	var carousel3d = document.querySelector('.carousel3d');
	
	if(carousel3d != null) {
		
		carousel(carousel3d);
	}
});

function carousel(root) {
	
	const ua = window.navigator.userAgent.toLowerCase();
	
	var
	figure = root.querySelector('figure'),
	images = figure.children,
	numImages = images.length,
	curImage = 0;
	theta = 360 / numImages,
	img = figure.children[0],
	computedStyle = window.getComputedStyle(img),
	imgWidth = parseFloat(computedStyle.width),
	disCenter = (imgWidth / 2) * Math.tan((90 - (180 / numImages)) * (Math.PI / 180)),	//画像から中心に垂線を引いた距離
	nav = root.querySelector('nav');	
	
	figure.style.transformOrigin = `center center -${disCenter}px`;
	
	for (i=1; i < numImages; i++) {
		
		figure.children[i].style.transformOrigin = `center center ${-disCenter}px`;
		
		//osの差異を吸収する　※iphoneだけz軸が設定されない
		if(ua.indexOf('iphone') != -1) {
			
			//iosの場合はZ軸の移動を追加
			figure.children[i].style.transform = `translateZ(${-disCenter}px) rotateY(${i * theta}deg)`;
		} else {
			
			figure.children[i].style.transform = `rotateY(${i * theta}deg)`;
		}
	}
	
	//ナビゲーションにカルーセル回転イベントを追加
	nav.addEventListener('click', navCarousel);
	
	//imgクリック時のイベント(画像をポップアップ)を追加
	var images = figure.querySelectorAll('img');
	var body = document.querySelector('body');
	
	images.forEach(function(i){
		i.addEventListener('click', function(e){
			var dispImg = e.target.cloneNode();
			
			dispImgFnc(dispImg);
		});
	});
	
	//カルーセル回転
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
		figure.style.transform = `rotateY(${theta * curImage * (-1)}deg)`;
	}
	
	//画像にポップアップイベントを付与
	function dispImgFnc(dispImg) {
		dispImg.style.transform = null;
		dispImg.classList.add('disp-img', 'show');
		dispImg.addEventListener('click', function(e){
			var t = e.target;
			t.classList.remove('show');
			t.classList.add('display-none');
			t.addEventListener('animationend', function(){
				t.parentNode.removeChild(t);
			});
		});
		body.prepend(dispImg);
	};
};