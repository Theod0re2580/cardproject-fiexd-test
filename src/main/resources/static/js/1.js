$(function () {

    const $dropdownToggle = $('.dropdown-toggle');

    // 드롭다운 열기/닫기
    $dropdownToggle.on('click', function (e) {
        e.stopPropagation();

        const $parent = $(this).closest('.dropdown');
        const $targetMenu = $parent.find('.dropdown-menu');

        // 다른 메뉴 닫기
        $('.dropdown-menu').not($targetMenu).fadeOut(150);

        // 현재 메뉴 토글
        $targetMenu.stop(true, true).fadeToggle(150);
    });

    // 바깥 클릭 시 닫기
    $(document).on('click', function () {
        $('.dropdown-menu').fadeOut(150);
    });
});
