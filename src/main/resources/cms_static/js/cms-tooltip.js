$(function() {
	$('.showTooltip').live({
	mouseover: function() {
		var text = '<div class="tipText">' + $(this).text() + '</div>';
		$(this).after(text);
		if ($(this).position().left - $(this).next('.tipText').width() < 0) {
			$(this).next('.tipText').css('top', $(this).position().top +'px').css('left', $(this).position().left+17+'px').css('margin-top', -$(this).next('.tipText').height()-15+'px').hide().fadeIn('fast');
		} else {
			$(this).next('.tipText').css('top', $(this).position().top +'px').css('left', $(this).position().left-$(this).next('.tipText').width()-14+'px').css('margin-top', -$(this).next('.tipText').height()-15+'px').hide().fadeIn('fast');
		}
	},
	mouseout: function() {
		$(this).next('.tipText').hide().remove();
	}})
	
	$('.showTooltip').click(function() {return false;});
});