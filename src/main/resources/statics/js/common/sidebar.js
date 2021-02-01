(function($) {
  "use strict";

  // Add active state to sidbar nav links
  var path = window.location.href; // because the 'href' property of the DOM element is the absolute path
  $("#layoutSidenav_nav .sb-sidenav a.nav-link").each(function() {
    if (this.href === path) {
      $(this).addClass("active");
    }
  });

  // Toggle the side navigation
  $("#sidebarToggle").on("click", function(e) {
    e.preventDefault();
    $("body").toggleClass("sb-sidenav-toggled");
  });

  const currentPath = location.pathname;

  // 페이지 로딩시 선택 페이지
  $('.sidebar li > a').each(function(){

    if ( currentPath == $(this).attr("href") ) {
      $(this).parent().addClass("active");
      $(this).parent().parent().addClass('no-transition').collapse("show");

      return false;
    }
  });

})(jQuery);

$(document).ready(function() {

  // 다른 메뉴 접기
  $('.sidebar > ul > li > a').click(function () {
    const parentUl = $(this).parent();
    $('.sidebar > ul > li').each(function() {
      if (!$(this).is(parentUl)) {
        $(this).find("ul").collapse('hide');
      }
    });

  });

});