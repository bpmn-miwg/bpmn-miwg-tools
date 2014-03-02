/*global window*/
/*
 * jQuery.imagefallback.js © 2013 MIT — The Rapportive Team <lee@rapportive.com>
 * https://github.com/rapportive-oss/jquery-imagefallback
 *
 * Call this function on an image selector to fallback to another image if the
 * main image fails to load.
 *
 * There are two ways to use it.
 *
 * 1. Specify a fallback image URL
 * $('img.photo').fallback('http://google.com/fallback.jpg')
 *
 * 2. Specify a callback function: 
 * $('.photo-container > img').fallback(function () {
 *     $('.photo-container').hide();
 * }).attr('src', 'http://my.photosite.com/image1.jpg');
 */
(function ($) {
    $.fn.fallback = function (fallback) {
        return this.each(function () {
            var $this = $(this);
            $this.bind('error.image-fallback', function () {
                $this.unbind('error.image-fallback, load.image-fallback');
                if ($.isFunction(fallback)) {
                    fallback.apply($this);
                } else {
                    $this.attr('src', fallback);
                }
            })
            .bind('load.image-fallback', function () {
                $this.unbind('.image-fallback');
            });
        });
    };
}(window.jQuery || window.Zepto));