/**
 * 
 */

/* スケジュール＞アイテム：文字がコンテンツ幅に収まるようフォントサイズを自動調整する */
window.addEventListener('DOMContentLoaded', () => {
	if(document.getElementsByClassName('schedule-outline').length) {
		const scheduleItems = document.getElementsByClassName('schedule-item');
		let texts;
		let range = new Range();
		for(let i = 0; i < scheduleItems.length; i++) {
			texts = scheduleItems[i].children;
			for(let j = 0; j < texts.length; j++) {
				range.selectNode(texts[j]);
				if(range.getClientRects().length > 2) {
//					console.log(texts[j].textContent);
//					fitty(texts[j], {
//						observeMutations: MutationObserverInit
//					});
					adjustTextForOneline(texts[j]);
				}
			}
		}
	}
})

function adjustTextForOneline (textNode) {
//	let size = window.getComputedStyle(textNode).fontSize.split('px')[0];
//	size = size - 12;
//	textNode.style.zoom = '0.5';
}

/* 画像フェードイン */
$(function(){
    $(window).scroll(function (){
        $('.fadein-scroll').each(function(){
            var targetElement = $(this).offset().top;
            var scroll = $(window).scrollTop();
            var windowHeight = $(window).height();
            if (scroll > targetElement - windowHeight + 200){
                $(this).css('opacity','1');
                $(this).css('transform','translateY(0)');
            }
        });
    });
});

$(function(){
    $(window).scroll(function (){
        $('.slidein-scroll').each(function(){
            var targetElement = $(this).offset().top;
            var scroll = $(window).scrollTop();
            var windowHeight = $(window).height();
            if (scroll > targetElement - windowHeight + 200){
                $(this).css('left','-5px');
            }
        });
    });
});

/* ファイルアップロードフォーム装飾 */
$(function(){
	$('#file_upload_top').on('change', function(){
		const file = $(this).prop('files')[0];
		$('#file_upload_top_text').text(file.name);
		$('#file_submit_top').removeClass('inactive');
	})
})

$(function(){
	$('#file_upload_sub').on('change', function(){
		const file = $(this).prop('files')[0];
		$('#file_upload_sub_text').text(file.name);
		$('#file_submit_sub').removeClass('inactive');
	})
})




