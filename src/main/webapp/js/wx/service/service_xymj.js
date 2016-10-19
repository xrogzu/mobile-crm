/**
 * Created by dell on 2016/3/1.
 */
$(document).on('ready',function(){
    $('#slides img').eq(0).on('click',function(){
        //var iframe = $('<iframe src="http://www.iqiyi.com/v_19rrkb3au4.html" width="100%" height="100%"></iframe>');
        //$('body').append(iframe);
        location.href = "http://www.iqiyi.com/v_19rrkb3au4.html";
    })
    $('#slides img').eq(1).on('click',function(){
        //var iframe = $('<iframe src="http://www.iqiyi.com/v_19rrkb3au4.html" width="100%" height="100%"></iframe>');
        //$('body').append(iframe);
        location.href = "http://www.iqiyi.com/v_19rrokfp8o.html";
    })
    $('#slides img').eq(2).on('click',function(){
        //var iframe = $('<iframe src="http://www.iqiyi.com/v_19rrkb3au4.html" width="100%" height="100%"></iframe>');
        //$('body').append(iframe);
        location.href = "http://www.iqiyi.com/v_19rrofksmk.html";
    })
    $('#zatan').on('click',function(){
        location.href = 'http://www.xiaoshouyi.com/xymj/index.php/2015-01-21-03-03-50'
        //var iframe = $('<iframe width="100%" height="100%" src="http://www.xiaoshouyi.com/xymj/index.php/2015-01-21-03-03-50"></iframe>');
        //$('body').append(iframe);
    });
    $('#yanlun').on('click',function(){
        location.href = 'http://www.xiaoshouyi.com/xymj/index.php/2015-01-21-03-02-49'
        //var url  = 'http://www.xiaoshouyi.com/xymj/index.php/2015-01-21-03-02-49?d='+new Date().getTime();
        //var iframe = $('<iframe  id="yanlun_ifream" src="" style="width: 100%;height: 100%;" width="100%" height="100%"></iframe>');
        //
        //$('body').append(iframe);
        //$("#yanlun_ifream",document.body).attr("src",url)
        //document.getElementById('yanlun_ifream').contentWindow.location.reload(true);
        //alert(999);

    });
    $('.list').on('click',function(){
        remove_rTop();
    })
})
$(document).ready(function(){
    remove_rTop();
    control_back(back);
})
function back(){
    location.href = apppath+'/wx/serveice/index.action?'+ddcommparams;
}
//function share(){
//    var urlStr = 'https://thirdparty.xiaoshouyi.com'+apppath+'/wx/statics/index.action?'+ddcommparams;
//    alert(urlStr)
//    dd.biz.util.share({
//        type: 1,//分享类型，0:全部组件 默认； 1:只能分享到钉钉；2:不能分享，只有刷新按钮
//        url: urlStr,
//        title: '销售易钉钉版',
//        content: '销售易（内容）',
//        onSuccess : function() {
//        },
//        onFail : function(err) {}
//    })
//}
$(function() {
    $('#slides').slidesjs({
        width: 940,
        height: 528,
        navigation: {
            effect: "fade"
        },
        pagination: {
            effect: "fade"
        },
        effect: {
            fade: {
                speed: 400
            }
        },
        play: {
            auto: true,
            interval: 4000,
            swap: true
        }
    });
});