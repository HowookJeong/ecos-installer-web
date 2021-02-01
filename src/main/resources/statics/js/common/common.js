var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

$(function() {
  $(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(header, token);
  });
});

// UUID 생성
function uuidv4() {
  return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
      (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
  );
}

// 날짜 포멧 변경
function formatDate(date) {
  moment.locale('ko');
  return moment(date).format("YYYY-MM-DD HH:mm:ss");
}

// Object to JSON Pretty print
function jsonPrettyFromObject(object) {
  return JSON.stringify(object, undefined, 2);
}

// String To Json Object To Json Pretty ptiny
function jsonPrettyFromString(string) {
  if (string === undefined || string === "") {
    return "";
  }
  const json = JSON.parse(string);
  return JSON.stringify(json, undefined, 2);
}

function validText(text, min, max, regExp) {
  if (!validTextSize(text, min, max) || !regExp.test(text)) {
    return false;
  }

  return true;
}

function validTextSize(text, min, max) {
  if (text.length < min || text.length > max) {
    return false;
  }

  return true;
}

/* Spinner */
function spinner_show(){ // Show
  var target_parent = $('.sb-nav-fixed');
  if ($('.spinner').length == 0) {
    target_parent.append('<div class="spinner-background"><div class="spinner"><span></span></div></div>');
  }
  $('.spinner-background').fadeIn(250);
  $('.spinner').fadeIn(250);
}
function spinner_hide(){ // Hide
  $('.spinner').fadeOut(250);
  $('.spinner-background').fadeOut(250);
}

// MODAL
let sharedDeleteCallback = undefined;
let sharedDeleteValidationText = "삭제";
function showSharedDeleteConfirm(title, message, callback) {
  showSharedDeleteConfirmHtml(title, message, null, callback);
}

function showSharedDeleteConfirmHtml(title, message, html, callback) {
  sharedDeleteCallback = callback;
  $("#sharedDeleteConfirmInput").val("");
  $("#shard-user-html-message").html("");

  $("#shared-modal-title").html(title);
  $("#shared-modal-message").html(message);

  if (html !== null) {
    $("#shard-user-html-message").html(html);
  }

  $("#sharedInputMessage").text("");
  $("#sharedDeleteConfirmModal").modal('show');
}

$('#sharedDeleteConfirmModal').on('shown.bs.modal', function() {
  $('#sharedDeleteConfirmInput').focus();
})

$("#sharedDeleteConfirmModal").on('hidden.bs.modal', function () {
  var deleteValidationText = sharedDeleteValidationText+"합니다";
  const txt = $("#sharedDeleteConfirmInput").val();

  if (txt === deleteValidationText) {
    sharedDeleteCallback();
  }
});

$("#sharedDeleteConfirmOk").click(function (e) {

  var deleteValidationText = sharedDeleteValidationText+"합니다";
  const txt = $("#sharedDeleteConfirmInput").val();

  if (txt === deleteValidationText) {
    $("#sharedDeleteConfirmModal").modal('hide');
  } else {
    $("#sharedInputMessage").text("'"+sharedDeleteValidationText+"합니다'를 입력해야 "+sharedDeleteValidationText+"가 가능합니다.");
    $("#sharedInputMessage")
    .animate({ opacity: 0 }, 100)
    .animate({ opacity: 1.0 }, 100)
    .animate({ opacity: 0 }, 100)
    .animate({ opacity: 1.0 }, 100)
    .animate({ opacity: 0 }, 100)
    .animate({ opacity: 1.0 }, 100);
    $('#sharedDeleteConfirmInput').focus();
  }
});
// MODAL

// code editor 생성
function getCodeEditor(element, mode) {
  let editor = ace.edit(element);;
  editor.session.setMode(mode);
  editor.setTheme("ace/theme/solarized_dark");
  editor.setShowPrintMargin(false);
  editor.setAutoScrollEditorIntoView(true);
  editor.session.setUseWrapMode(true);
  editor.session.setTabSize(2);
  return editor;
}

 function hashCode(s){
  return s.split("").reduce(function(a,b){a=((a<<5)-a)+b.charCodeAt(0);return a&a},0);
}

function _clipboard(text) {
  var isSupported = document.queryCommandSupported('copy');
  var el = document.createElement('input');

  if ( isSupported ) {
    el.value = text;
    document.body.appendChild(el);
    el.select();
    var isCopied = document.execCommand('copy');
    document.body.removeChild(el);

    $('#_toast_header_strong').text("Copy to Clipboard");

    if ( isCopied ) {
      $('#_toast_header_small').removeAttr("class");
      $('#_toast_header_small').addClass("text-success");
      $('#_toast_header_small').text("성공");
    } else {
      $('#_toast_header_small').removeAttr("class");
      $('#_toast_header_small').addClass("text-danger");
      $('#_toast_header_small').text("실패");
    }

    $('#_toast_body').text(text);
    $('#_toast').toast('show');
  } else {
    alert("지원하지 않는 기능입니다.");
    return false;
  }
}