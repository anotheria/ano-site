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
//Asks for target group and mode first, then posts the transfer and shows what came back per target.
function lightboxTransfer(action, documentName, id){
	showTransferBox('<div class="transfer_box"><h3>Transfer ' + documentName + '</h3><p>Loading transfer targets ...</p></div>');

	//relative on purpose: the cms is not always mounted under /cms, and the transfer action path handed in
	//above is relative too, so both resolve against whatever the current cms page is.
	$.post('asgTransferTargets', {}, function (response) {
		var data = response.data || {};
		if (!data.enabled) {
			showTransferBox(transferMessage(documentName, 'Transfer is not enabled on this instance. Set <code>transferEnabled</code> in anositeconfig.'));
			bindTransferClose();
			return;
		}
		if (!data.groups || data.groups.length === 0) {
			showTransferBox(transferMessage(documentName, 'No transfer target groups are configured in anositeconfig.'));
			bindTransferClose();
			return;
		}
		showTransferBox(transferForm(documentName, id, data));
		bindTransferClose();
		$('#transfer_ok_button').click(function () {
			runTransfer(action, documentName, id);
			return false;
		});
	}).fail(function () {
		showTransferBox(transferMessage(documentName, 'Could not load the transfer targets.'));
		bindTransferClose();
	});

	return false;
}

//the dialog asking where and how much to transfer
function transferForm(documentName, id, data){
	var html = '<div class="transfer_box"><h3>Transfer ' + documentName + '</h3>';
	html += '<p>Document id: ' + id + '</p>';

	html += '<p><b>Target</b></p>';
	for (var i = 0; i < data.groups.length; i++) {
		var group = data.groups[i];
		var targetNames = [];
		for (var t = 0; t < group.targets.length; t++)
			targetNames.push(group.targets[t].name);

		html += '<label><input type="radio" name="transferTarget" value="' + group.name + '"' + (i === 0 ? ' checked="checked"' : '') + '> ' +
			group.name + ' <span class="transfer_hint">(' + targetNames.join(', ') + ')</span></label><br/>';
	}

	html += '<p><b>Scope</b></p>';
	for (var m = 0; m < data.modes.length; m++) {
		var mode = data.modes[m];
		html += '<label><input type="radio" name="transferMode" value="' + mode.value + '"' + (m === 0 ? ' checked="checked"' : '') + '> ' +
			mode.label + '</label><br/>';
	}

	html += '<div class="overlay_buttons"><a href="#" class="button" id="transfer_ok_button"><span>Transfer</span></a>' +
		'<a href="#" class="button" id="cancel_button"><span>Cancel</span></a></div>';
	html += '</div>';
	return html;
}

//posts the transfer and renders the report
function runTransfer(action, documentName, id){
	var target = $('input[name="transferTarget"]:checked').val();
	var mode = $('input[name="transferMode"]:checked').val();

	showTransferBox('<div class="transfer_box"><h3>Transfer ' + documentName + '</h3>' +
		'<p>Transferring to ' + target + ' ...</p><div class="loading-transfer"></div></div>');

	$.post(action, {pId: id, transferTarget: target, transferMode: mode}, function (response) {
		showTransferBox(transferResult(documentName, response));
		bindTransferClose();
	}).fail(function () {
		showTransferBox(transferMessage(documentName, 'The transfer request failed.'));
		bindTransferClose();
	});
}

//what came back, per target, plus whatever the server warned about
function transferResult(documentName, response){
	var data = response.data || {};
	var html = '<div class="transfer_box"><h3>Transfer ' + documentName + '</h3>';

	if (data.documentCount !== undefined)
		html += '<p>' + data.documentCount + ' document(s) to ' + data.group + ', mode ' + data.mode + '.</p>';

	var targets = data.targets || [];
	for (var i = 0; i < targets.length; i++) {
		var t = targets[i];
		html += '<p>' + (t.success ? 'OK' : 'FAILED') + ': ' + t.name + ' (' + t.url + ') - ' +
			t.succeeded + ' transferred, ' + t.failed + ' failed.</p>';
		for (var e = 0; e < t.errors.length; e++)
			html += '<p class="transfer_error">' + t.errors[e] + '</p>';
	}

	if (response.errors !== undefined && response.errors.error !== undefined)
		for (var g = 0; g < response.errors.error.length; g++)
			html += '<p class="transfer_error">' + response.errors.error[g] + '</p>';

	var warnings = data.warnings || [];
	for (var w = 0; w < warnings.length; w++)
		html += '<p class="transfer_warning">' + warnings[w] + '</p>';

	if (targets.length === 0 && (response.errors === undefined || response.errors.error === undefined))
		html += '<p>Nothing was transferred.</p>';

	html += '<div class="overlay_buttons"><a href="#" class="button" id="cancel_button"><span>Close</span></a></div>';
	html += '</div>';
	return html;
}

function transferMessage(documentName, message){
	return '<div class="transfer_box"><h3>Transfer ' + documentName + '</h3><p>' + message + '</p>' +
		'<div class="overlay_buttons"><a href="#" class="button" id="cancel_button"><span>Close</span></a></div></div>';
}

//puts html into the lightbox and centers it
function showTransferBox(html){
	var el = $('.lightbox');
	el.show();
	el.find('.box_in .text_here').html(html);
	$('.lightbox .box').css('width', 'auto');
	$('.lightbox .box').width($('.lightbox .box_in').width());
	var box = el.find('.box');
	box.css('left', '50%');
	box.css('margin-left', -box.width() / 2);
	box.css('top', '50%');
	box.css('margin-top', -box.height() / 2);
	box.css('position', 'fixed');
}

//the global close handler is bound with live() and does not catch buttons rendered afterwards
function bindTransferClose(){
	$('.lightbox #cancel_button').click(function () {
		$('.lightbox').hide();
		return false;
	});
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

function getFileInfo(fileName){
	$.post('/cms/showFileInfo', {fileName: fileName}, function (resp){
		if (resp.status == "ERROR") {
			console.log('Error.', resp.errors._global);
			return false;
		}

		$('#fileInfoSize').text(resp.data.size);
		$('#fileInfoPixels').text(resp.data.pixels)
	});
}

function sortTextData(doc, pid){
	$.post('/cms/sortTextData', {doc: doc, pId: pid}, function (resp){
		if (resp.status == "ERROR") {
			console.log('Error.', resp.errors._global);
			notification(resp.errors._global);
			return false;
		}

		if (resp.data.isUpdated){
			location.reload();
		} else {
			notification("No data for sort");
		}
	});
}

$(function() {
	initAllCmsDocs();
	initSelectedCmsDocsDeletion();
	//test
});
