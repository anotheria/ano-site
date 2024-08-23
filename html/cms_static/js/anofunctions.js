$(function() {

	//left dropdown opener
	function left_open(el) {
		if (el.hasClass('opened')) {
			el.removeClass('opened');
			el.next().next().hide();
		} else {
			el.addClass('opened');
			el.next().next().show();
		}

	}

	;

	//left dropdown to open
	$('.adv_search, .lang_open').click(
			function() {
				left_open($(this));
				return false;
			}
			);

	//search text dis
	$('.search').click(function() {
		if ($(this).val() == 'Search...') {
			$(this).val('');
			$(this).css('color', 'black');
		}
	});

	$('.search').blur(function() {
		if ($(this).val() == '') {
			$(this).val('Search...');
			$(this).css('color', '#808080');
		}
	});

	//menu
	$('.main_navigation li a').click(function() {
		if ($(this).parent().parent().hasClass('main_navigation') && !$(this).parent().hasClass('opened')) {
			$('.main_navigation li').each(function() {
				$(this).removeClass('opened');
			});
			$(this).parent().addClass('opened');
		}
	});

	//scroll open down
	$('.open_pop').click(function() {
		if ($(this).parent().hasClass('opened')) {
			$(this).parent().removeClass('opened');
		} else {
			$('.left_p li').each(function() {
				$(this).removeClass('opened');
			});
			$(this).parent().addClass('opened');
		}
		return false;
	});

	$('.right_p a:first').click(function() {
		if ($(this).hasClass('opened')) {
			$(this).removeClass('opened');
			$(this).next().hide();
		} else {
			$(this).addClass('opened');
			$(this).next().show();
		}
		return false;
	});

	$('.filter_open').click(function() {
		if ($(this).hasClass('opened')) {
			$(this).removeClass('opened');
			$(this).next('.filters').hide();
		} else {
			$(this).addClass('opened');
			$(this).next('.filters').show();
		}
		return false;
	});

	//resize topnav
	function resizeTopNav() {
		$('.top_nav').width($('.main_area').width());
	}

	$(window).bind('resize', function() {
		resizeTopNav();
	});

	resizeTopNav();

	$('.left_p li a').click(function() {
		if (!$(this).hasClass('open_pop')) {
			setTimeout(function() {
				window.scrollBy(0, -$('.top_nav').height()-35);
			}, 60);
			$('.left_p li').removeClass('opened');
		}
		$('.main_area tr').removeClass('backlight');
		var ids = $(this).attr('href');
		$(ids).parents().filter('tr').addClass('backlight');
	});

	if ($('.top_nav').height() != null) {
		$('.r_w').css('padding-top', $('.top_nav').height() + 30);
	} else {
		$('.r_w').css('padding-top', $('.top_nav').height() + 10);
	}

	if ($('.top_nav').height() != null) {$('.r_w').css('padding-top', $('.top_nav').height()+30);} else {$('.r_w').css('padding-top', $('.top_nav').height()+10);}

	//disables all checkboxes
	function disableAll(el, dis) {
		if (el.is(':checked')) {
			dis.removeAttr('disabled');
			el.attr('checked', 'checked');

		} else {
			dis.attr('disabled', 'disabled');
			el.removeAttr('checked');
		}
	}

	//disable ckeckboxes in langeages
	$('.all_check').click(function() {
		checkAll($('.all_check'), $('.lang_s_open li input'));
	});
	
	$('.lang_s_open li input').click(function() {
		checkAllUncheck($('.all_check'), $('.lang_s_open li input'));
	});

	//close popup on click somewhere
	$('body').click(function(event) {
		if ($(event.target).parents('.pop_up').length == 0) {
			$('.left_p li').removeClass('opened');
		}
	});
	
	//check all inputs
	function checkAll(all, inputs) {
		if (all.is(':checked')) {
			inputs.attr('checked', 'checked');
		} else {
			inputs.removeAttr('checked');
		}
	}

	;


	//all checkboxes uncheck
	function checkAllUncheck(all, inputs) {
		var bool = true;
		inputs.each(function() {
			if (!$(this).is(':checked')) {
				bool = false;
			}
			if (!bool) {
				all.attr('checked', false);
			} else {
				all.attr('checked', true);
			}
		});
	};
    
    //rich text edit on/off
	function richSwitch(btn) {
		var on = btn.find('.rich_on_off').eq(0);
		var off = btn.find('.rich_on_off').eq(1);
		if (on.is(':visible')) {
			on.hide();
			off.show();
		} else {
			on.show();
			off.hide();
		}
	}

	
	$('.rich_on_off').click(function() {
		richSwitch($(this).parents('td'));
	});
    
});

//create cookies
function createCookie(name, value, days) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
		var expires = "; expires=" + date.toGMTString();
	}
	else var expires = "";
	document.cookie = name + "=" + '\"' + value + '\"' + expires + "; path=/";

}

//read cookies
function readCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') c = c.substring(1, c.length);
		if (c.indexOf(nameEQ) == 0) {
			c = c.substring(nameEQ.length, c.length);
			c = c.substring(1, c.length - 1);
			return c;
		}
	}
	return null;
}

//erase cookies
function eraseCookie(name) {
	createCookie(name, "", -1);
}

//save to cookies
function saveCookie() {
	var str = '';
	var i = 0;
	if ($('.all_check').is(':checked'))
	{
		for (i = 0; i <= $('.lang_s_open li input').length; i++) {
			if ($('.lang_s_open li input').eq(i).is(':checked')) {
				str = str + $('.lang_s_open li input').eq(i).attr('id') + ',';
			}
		}
		str = str.substring(0, str.length - 1);
		if (str == '') {
			createCookie('ids', 'none', 7);
		} else {
			createCookie('ids', str, 7);
		}
	} else {
	{
		for (i = 0; i <= $('.lang_s_open li input').length; i++) {
			if ($('.lang_s_open li input').eq(i).is(':checked')) {
				str = str + $('.lang_s_open li input').eq(i).attr('id') + ',';
			}
		}
		str = str.substring(0, str.length - 1);
		if (str == '') {
			createCookie('ids', 'none', 7);
		} else {
			createCookie('ids', str, 7);
		}
	}
	}
	loadCookie();
}

	//disable select when quick add
/*	$('.add_id ').change(function() {
		if ($(this).val() == '') {
			$('.select_row select').removeAttr('disabled');
		} else {
			$('.select_row select').attr('disabled', 'disabled');
		}
	});
*/


function loadCookie() {
	var str = '';
	var i = 0;
	var ar = [];
	str = readCookie('ids');
	if ((str == 'none') || (str == null)) {
		$('.all_check').removeAttr('checked');
		$('.lang_s_open li input').removeAttr('checked', 'checked');
		$('.main_area .lang_hide').hide();
		//$('.main_area .def').show();
		if (str == null) {
			$('.main_area .lang_hide').show();
			$('#all_check').attr('checked', 'checked');
			$('.lang_s_open li input').attr('checked', 'checked');
		}
	} else {
		ar = str.split(',');
		$('.main_area .lang_hide').hide();
		for (i = 0; i <= ar.length; i++) {
			//$('#all_check').attr('checked', 'checked');
			$('#' + ar[i]).attr('checked', 'checked');
			$('.' + ar[i]).show();
		}
	}
}

//open lightbox
function lightbox(text, href) {
	var buttons = '<div class="overlay_buttons"><a href="'+href+'" class="button" id="ok_button"><span>OK</span></a><a href="#" class="button" id="cancel_button"><span>Cancel</span></a></div>';
	var el = $('.lightbox');
	el.show();
	text = text + buttons;
	el.find('.box_in .text_here').html(text);
	$('.lightbox .box').css('width', 'auto');
	$('.lightbox .box').width($('.lightbox .box_in').width());
	var wid = el.find('.box').width();
	var box = el.find('.box');
	var hig = el.find('.box').height();
	box.css('left', '50%');
	box.css('margin-left', -wid / 2);
	//box.css('top', link.offset().top);
	box.css('top', '50%');
	box.css('margin-top', -hig / 2);
	box.css('position', 'fixed');
	return false;
}

//open lightbox for transfer function
function lightboxTransfer(action, document, id){
	var onclickFunc = '$.post(\'' + action +'\', {pId:\'' + id +'\' }, function (response) {' +
    ' if (response.errors != undefined && response.errors.length != 0) {' +
        'notification(response.errors.error);' +
    '} else {location.reload(true); }})';

	var text = 'Are you sure you want to transfer to prod ' + document + ' with id: ' + id +'?';
    var loadingText = 'Transferring ' + document + ' with id ' + id + ' ...';
    var buttons = '<div class="overlay_buttons"><a href="#" onclick="'+ onclickFunc+ '" class="button"  id="ok_button"><span>OK</span></a><a href="#" class="button" id="cancel_button"><span>Cancel</span></a></div>';
    var el = $('.lightbox');
    el.show();
    text = text + buttons;
    el.find('.box_in .text_here').html(text);
    $('.lightbox .box').css('width', 'auto');
    $('.lightbox .box').width($('.lightbox .box_in').width());
    var wid = el.find('.box').width();
    var box = el.find('.box');
    var hig = el.find('.box').height();
    box.css('left', '50%');
    box.css('margin-left', -wid / 2);
    //box.css('top', link.offset().top);
    box.css('top', '50%');
    box.css('margin-top', -hig / 2);
    box.css('position', 'fixed');

    $('#ok_button', el).on('click', function () {
        el.find('.overlay_buttons').hide();
        var loading = '<div class="loading-transfer"></div>';
        el.find('.box_in .text_here').html(loadingText + loading);
    });
    return false;
}

//close lightbox
$('.black_bg, .close_box, #cancel_button, #notification_cancel_button').live('click', function() {
	$('.lightbox').hide();
	return false;
});

//open notification Box
function notification(text, href) {
	var buttons = '<div class="overlay_buttons"><a href="#" class="button" id="notification_cancel_button"><span>Ok</span></a></div>';
	var el = $('.lightbox');
	el.show();
	text = text + buttons;
	el.find('.box_in .text_here').html(text);
	$('.lightbox .box').css('width', 'auto');
	$('.lightbox .box').width($('.lightbox .box_in').width());
	var wid = el.find('.box').width();
	var box = el.find('.box');
	var hig = el.find('.box').height();
	box.css('left', '50%');
	box.css('margin-left', -wid / 2);
	//box.css('top', link.offset().top);
	box.css('top', '50%');
	box.css('margin-top', -hig / 2);
	box.css('position', 'fixed');
	return false;
}

function notificationAutoClose(text) {
	var el = $('.lightbox');
	el.show();
	el.find('.box_in .text_here').html(text);
	$('.lightbox .box').css('width', 'auto');
	$('.lightbox .box').width($('.lightbox .box_in').width());
	var wid = el.find('.box').width();
	var box = el.find('.box');
	var hig = el.find('.box').height();
	box.css('left', '50%');
	box.css('margin-left', -wid / 2);
	//box.css('top', link.offset().top);
	box.css('top', '50%');
	box.css('margin-top', -hig / 2);
	box.css('position', 'fixed');
	setTimeout(function(){
		$('.lightbox').hide();
	}, 1000);
	return false;
}

$('.lang_s_open .button').live('click',
		function() {
			saveCookie();
			return false;
		});

$(function() {
	loadCookie();
});

function initAllCmsDocs() {
	var checkAllCmsDocsCheckbox = $('#checkAllCmsDocsCheckbox'),
		cmsDocumentCheckbox = $('tr.cmsDocument input[name="pId"]');

	checkAllCmsDocsCheckbox.click(function() {
		var checkedStatus = this.checked;
		
		cmsDocumentCheckbox.each(function() {
			this.checked = checkedStatus; 
		});
	});
};


function initSelectedCmsDocsDeletion() {
	var deleteSelectedId = $('#deleteSelectedId'),
		cmsDocumentCheckboxes = $('tr.cmsDocument input[name="pId"]');

	deleteSelectedId.click(function() {
		var selectedIds = [];
		cmsDocumentCheckboxes.each(function (i, checkbox) {
			if (checkbox.checked) {
			 	selectedIds.push(checkbox.value);
			 };
		});
		if (selectedIds.length){
			this.href = this.href + "&" + $.param({pId: selectedIds}, true);
			return true;
		} else {
			return false;
		}
	});
};

//tinyMCE saving Hack
function customSubmit() {
	 var editors = tinyMCE.editors;
	 for(i = 0; i<editors.length; i++){
		 if (tinyMCE.get(editors[i].id).isHidden()){
			 tinyMCE.get(editors[i].id).load();
		 }
		 else {
			 tinyMCE.get(editors[i].id).save();
		 }
	}
}

$(function() {
	initAllCmsDocs();
	initSelectedCmsDocsDeletion();
	//test
});
