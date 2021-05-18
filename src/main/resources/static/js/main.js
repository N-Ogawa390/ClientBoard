/**
 * 
 */

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
	$('.file-upload-top').on('change', function(){
		$('#file-submit').removeClass('inactive');
	})
})




