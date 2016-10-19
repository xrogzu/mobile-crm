/**
 * Created by dell on 2016/2/29.
 */
$(document).on('ready', function() {
    //contactUs();
    $('#xymj').on('click', function() {
        location.href = apppath + '/wx/serveice/miji.action?' + ddcommparams;
    });
    $('.me-feedback-ico').on('click', function() {
        location.href = apppath + '/wx/serveice/yjfk.action?' + ddcommparams;
    });
    $('.me-about-ico').on('click', function() {
        location.href = apppath + '/wx/serveice/about.action?' + ddcommparams;
    });
    $('.me-schedule-ico').on('click', function() {
        location.href = apppath + '/wx/schedule/list.action?' + ddcommparams;
    });
    $('.me-feed-ico').on('click', function() {
        location.href = apppath + '/wx/activityrecord/activityrecord.action?' + ddcommparams;
    });
   
})
    //function aboutXSY(){
    //$('.list').eq(3).on('click',function(){
    //    $('#aboutShadow').fadeIn();
    //})
    //$('#aboutShadow .bot').on('click',function(){
    //    $('#aboutShadow').fadeOut();
    //})
    //}
    //function contactUs(){
    //    $('.list').eq(2).on('click',function(){
    //        $('#contactUs').show();
    //        set_title('联系我们');
    //    })
    //}
function back() {
    if ($('#contactUs').css('display') == 'block') {
        $('#contactUs').hide();
        set_title('服务');
    } else if ($('#aboutShadow').css('display') == 'block') {
        $('#aboutShadow').hide();
    } else {
        location.href = apppath + '/wx/statics/index.action?' + ddcommparams;
    }
}
