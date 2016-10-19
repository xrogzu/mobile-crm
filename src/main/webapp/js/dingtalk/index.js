/**
 * Created by dell on 2016/3/1.
 */
$(document).bind('ready',function(){
    //setLocalStorage();
    selectors();
    //业绩统计——行为统计切换
    sort();
    //行为统计页筛选
    actSumSelect();
    recordSelect();
    //==============业绩统计
    //销售漏斗
    get_funnel();
    change_funnel();
    //业绩排名
    get_rank();
    get_rank_data(rankDateType);
    //重点商机
    opp_options();
    build_opp_list(apppath+'/wx/statics/topopportunity.action',$('#list .main'),build_list_unit,listScrollFunc);

})
dd.ready(function(){
    control_back(back);
    remove_rTop();
    var buriedPointType = 'amountStatistics';
    buriedPoint(buriedPointType);
})
function setLocalStorage(){
    if(!localStorage.getItem(xsyUser.id)){
        localStorage.setItem(xsyUser.id,JSON.stringify({
            'index':{
                'funnelData':'',
                'rankData':'',
                'oppData':''
            },
            'opportunityHistory':'',
            'accountHistory':''
        }))
    }
}
function back(){
    if($('.ct').length>0){
        $('.ct').remove();
        set_title('首页');
        $('body').css({'height':'','overflow':''});
    }else{
        dd.biz.navigation.back();
    }
}
var pageNo = 0;
var pageSize = 10;
var oppDateType = 4;
var obj = {
    pageNo:pageNo,
    pagesize:pageSize,
    searchDateType:oppDateType
};
var actLoad = false;
function sort(){
    if(GetQueryString('page')=='behavior'){
        if(!actLoad){
            followSum();
            //跟进记录
            get_record($('#recordList > .main'),20);
            actLoad = true;
        }
        $('#sort .behavior').addClass('active');
        $('#sort .performance').removeClass('active');
        $('#behaviorPage').show();
        $('#performancePage').hide();
        //行为统计埋点
        var buriedPointType = 'behaviorStatistics';
        buriedPoint(buriedPointType);
    }
    $('#sort .behavior').bind('click',function(){
        if(!$(this).hasClass('active')){
            if(!actLoad){
                followSum();
                //跟进记录
                get_record($('#recordList > .main'),20);
                actLoad = true;
            }
            //行为统计埋点
            var buriedPointType = 'behaviorStatistics';
            buriedPoint(buriedPointType);
            $(this).addClass('active');
            $('#sort .performance').removeClass('active');
            $('#behaviorPage').show();
            $('#performancePage').hide();
        }
    })
    $('#sort .performance').bind('click',function(){
        if(!$(this).hasClass('active')){
            //业绩统计埋点
            var buriedPointType = 'amountStatistics';
            buriedPoint(buriedPointType);
            $(this).addClass('active');
            $('#sort .behavior').removeClass('active');
            $('#performancePage').show();
            $('#behaviorPage').hide();
        }
    })
}
function selectors(){
    $('.selector').bind('click',function(e){
        if($(this).find('.options').css('display')=='block'){
            $(this).find('.options').hide();
        }else{
            $('.options').hide();
            $(this).find('.options').show();
            if(($(this).offset().top-$(window).scrollTop()-60)>$(this).find('.options').height()){
                $(this).find('.options').addClass('upper');
                $(this).find('.options').removeClass('lower');
            }else{
                $(this).find('.options').addClass('lower');
                $(this).find('.options').removeClass('upper');
            }
        }
        e.stopPropagation();
    })
    $('.options li').each(function(){
        $(this).bind('click',function(e){
            $(this).parent().parent().find('span').text($(this).text());
            $(this).parent().hide();
            e.stopPropagation();
        })
    })
    $('body').bind('touchmove',function(){
        $('.options').hide();
    })
    $('body').bind('click',function(){
        $('.options').hide();
    })
}
//获取并设置销售漏斗数据

var funnelDateType = 4;
function get_funnel(){
    //先加载缓存数据
    if(funnelDateType == 4){
        if(localStorage.getItem(xsyUser.id)){
            var ls = JSON.parse(localStorage.getItem(xsyUser.id));
            if(ls.index){
                if(ls.index.funnelData){
                    if(ls.index.funnelData.success==true){
                        //alert('加载缓存数据')
                        funnelByData(ls.index.funnelData);
                    }
                }
            }
        }
    }
    $('.funnelTop .selector .options li').each(function(){
        $(this).bind('click',function(){
            funnelDateType = $(this).attr('value');
            set_funnel();
        })
    })
    set_funnel();
    function set_funnel(){
        $.ajax({
            url:apppath+'/wx/char/salesfunnel.action',
            data:{
                searchDateType:funnelDateType
            },
            success:function(data){
                console.log('漏斗：'+data);
                var oData = eval('('+data+')');
                if(oData.success == true){
                    if(funnelDateType == 4){
                        var ls;
                        if(localStorage.getItem(xsyUser.id)){
                            ls = JSON.parse(localStorage.getItem(xsyUser.id));
                            ls.index = {'funnelData':oData}
                        }else{
                            ls = {'index':{'funnelData':oData}}
                        }
                        localStorage.setItem(xsyUser.id,JSON.stringify(ls));
                    }
                    //alert('加载新数据')
                    funnelByData(oData);
                }else{
                    if(oData.entity&&oData.entity.status&&oData.entity.status == '0000003'){
                        location.href = apppath+'/wx/authorized/no.action?'+ddcommparams;
                    }else {
                        myAlert('暂时无法获取数据，请稍后再试');
                    }
                }
            },
            error:function(){
                myAlert('暂时无法获取数据，请检查您的网络');
            }
        })
    }
}
function funnelByData(oData){
    var total = oData.entity.total;
    var win = oData.entity.win;
    var estimateCompleted = oData.entity.estimateCompleted;

    var scalingValue = 999999;
    var flag_total = "元";
    var flag_win = "元";
    var flag_estimateCompleted = "元";
    if(total > scalingValue){
        total = total / 10000;
        total = (Math.round(total * 100) / 100);
        flag_total = "万元";
    }
    if(win > scalingValue){
        win = win / 10000;
        win = (Math.round(win * 100) / 100);
        flag_win = "万元";
    }
    if(estimateCompleted > scalingValue){
        estimateCompleted = estimateCompleted / 10000;
        estimateCompleted = (Math.round(estimateCompleted * 100) / 100);
        flag_estimateCompleted = "万元";
    }

    $('.whole .value').text(total);
    $('.predict .value').text(estimateCompleted);
    $('.actual .value').text(win);


    $('.whole .label').text("漏斗总值("+flag_total+")");
    $('.predict .label').text("预计完成("+flag_estimateCompleted+")");
    $('.actual .label').text("实际完成("+flag_win+")");


    var series = oData.entity.series;
    //无数据
    if(series[0].count==0&&series[1].count==0&&series[2].count==0&&series[3].count==0){
        $('.noDataFunnel').show();
        $('.funnelArea').hide();
        $('#aim').hide();
        $('#saleFunnel > .title').hide();
        $('#saleFunnel').addClass('noData');
    }else {
        $('.funnelArea').show();
        $('#aim').show();
        $('#saleFunnel > .title').show();
        $('.noDataFunnel').hide();
        $('#saleFunnel').removeClass('noData');
        if ($('#saleFunnel .btn').text() == '金额') {
            var money0 = series[0].amount;
            var money1 = series[1].amount;
            var money2 = series[2].amount;
            var money3 = series[3].amount;
            var size = '元';
            if (money0 > scalingValue || money1 > scalingValue || money2 > scalingValue || money3 > scalingValue ) {
                size = '(万元)';
                money0 = money0 / 10000;
                money0 = Math.round(money0 * 100) / 100;
                money1 = money1 / 10000;
                money1 = Math.round(money1 * 100) / 100;
                money2 = money2 / 10000;
                money2 = Math.round(money2 * 100) / 100;
                money3 = money3 / 10000;
                money3 = Math.round(money3 * 100) / 100;
            }
            $('#leftLine .line1 .info').text(series[0].name);
            $('#rightLine .line1 .info').text(money0+size);
            $('#leftLine .line2 .info').text(series[1].name);
            $('#rightLine .line2 .info').text(money1+size);
            $('#leftLine .line3 .info').text(series[2].name);
            $('#rightLine .line3 .info').text(money2+size);
            $('#leftLine .line4 .info').text(series[3].name);
            $('#rightLine .line4 .info').text(money3+size);
            funnel(series[0].amount, series[1].amount, series[2].amount, series[3].amount);
        } else if ($('#saleFunnel .btn').text() == '个数') {
            $('#leftLine .line1 .info').text(series[0].name);
            $('#rightLine .line1 .info').text(series[0].count);
            $('#leftLine .line2 .info').text(series[1].name);
            $('#rightLine .line2 .info').text(series[1].count);
            $('#leftLine .line3 .info').text(series[2].name);
            $('#rightLine .line3 .info').text(series[2].count);
            $('#leftLine .line4 .info').text(series[3].name);
            $('#rightLine .line4 .info').text(series[3].count);
            funnel(series[0].count, series[1].count, series[2].count, series[3].count);
        }
    }
}
//漏斗形式切换（金额/个数）
function change_funnel(){
    $('#saleFunnel .btn').bind('click',function(){
        if($(this).text()=='个数'){
            $(this).text('金额');
            $(this).parent().find('span').text('(元)');
        }else if($(this).text()=='金额'){
            $(this).text('个数');
            $(this).parent().find('span').text('(个)');
        }
        get_funnel();
    })
}
//生成漏斗
function funnel(n1,n2,n3,n4){
    var minHeight = 35;
    var height = $('#funnel').height();
    var height4 = height/(n1+n2+n3+n4)*n4;
    var height3 = height/(n1+n2+n3+n4)*n3;
    var height2 = height/(n1+n2+n3+n4)*n2;
    var height1 = height/(n1+n2+n3+n4)*n1;
    if(height4<minHeight){
        height4 = minHeight;
    };
    if(height3<minHeight){
        height3 = minHeight;
    };
    if(height2<minHeight){
        height2 = minHeight;
    };
    if(height1<minHeight){
        height1 = minHeight;
    }
    var w3 = height3/8*4;
    var w2 = height2/8*4;
    var w1 = height1/8*4;
    var width4 = $('#funnel .funnel4').width();
    var width3 = width4;
    var width2 = width3+w3*2;
    var width1 = width2+w2*2;
    $('#funnel .funnel4').css({'borderTopWidth':height4});
    $('#funnel .funnel3').css({'borderTopWidth':height3,'width':width3, 'borderLeftWidth':w3, 'borderRightWidth':w3});
    $('#funnel .funnel2').css({'borderTopWidth':height2,'width':width2, 'borderLeftWidth':w2, 'borderRightWidth':w2});
    $('#funnel .funnel1').css({'borderTopWidth':height1,'width':width1, 'borderLeftWidth':w1, 'borderRightWidth':w1});

    var lineHeight = $('.line').height();
    var top1 = $('#funnel .funnel1').position().top+parseInt($('#funnel .funnel1').css('borderTopWidth'))-lineHeight-2;
    var top2 = $('#funnel .funnel2').position().top+parseInt($('#funnel .funnel2').css('borderTopWidth'))*0.8-lineHeight-2;
    var top3 = $('#funnel .funnel3').position().top+parseInt($('#funnel .funnel3').css('borderTopWidth'))*0.8-lineHeight-2;
    var top4 = $('#funnel .funnel4').position().top+parseInt($('#funnel .funnel4').css('borderTopWidth'))-lineHeight-2;
    if(top4-top3<lineHeight){
        top3 = top4-lineHeight-2;
    };
    if(top3-top2<lineHeight){
        top2 = top3-lineHeight-2;
    };
    if(top2-top1<lineHeight){
        top1 = top2-lineHeight-2;
    };
    var lWidth1 = ($('#saleFunnel').width()-$('#funnel .funnel1').width())/2-80;
    var lWidth2 = ($('#saleFunnel').width()-$('#funnel .funnel2').width()-w2)/2-70;
    var lWidth3 = ($('#saleFunnel').width()-$('#funnel .funnel3').width()-w3)/2-70;
    var lWidth4 = ($('#saleFunnel').width()-$('#funnel .funnel4').width())/2-70;
    $('#leftLine .line1').css({'top':top1-15,'width':lWidth1});
    $('#leftLine .line2').css({'top':top2-5,'width':lWidth2});
    $('#leftLine .line3').css({'top':top3,'width':lWidth3});
    $('#leftLine .line4').css({'top':top4-2,'width':lWidth4});
    $('#rightLine .line1').css({'top':top1-15,'width':lWidth1});
    $('#rightLine .line2').css({'top':top2-5,'width':lWidth2});
    $('#rightLine .line3').css({'top':top3,'width':lWidth3});
    $('#rightLine .line4').css({'top':top4-2,'width':lWidth4});
}
//=======================================行为汇总=====================================
var actDateType = 1;
function actSumSelect(){
    $('.actSum .selector .options li').each(function(){
        $(this).bind('click',function(){
            actDateType = $(this).attr('value');
            followSum();
        })
    })
}
function followSum(){
    $('.actSum .list').children().remove();
    $.ajax({
        url:apppath+'/wx/statics/activerecords.action',
        data:{
            searchDateType:actDateType
        },
        success:function(data){
            console.log('跟进汇总：'+data);
            var oData = eval('('+data+')');
            if(oData.success == true){
                actSumByData(oData);
            }else{
                if(oData.entity&&oData.entity.status&&oData.entity.status == '0000003'){
                    location.href = apppath+'/wx/authorized/no.action?'+ddcommparams;
                }else {
                    console.log('暂时无法获取数据，请稍后再试');
                }
            }
        },
        error:function(){
            myAlert('暂时无法获取数据，请检查您的网络')
        }
    })
}
function actSumByData(oData){
    var obj = oData.entity;
    for(var i=0;i<obj.length;i++){
        var icon;
        var size;
        var name = obj[i].text;
        var num = obj[i].count;
        var compare = obj[i].compare;
        var count = obj[i].count;
        //if(compare!='N/A'){
        //    compare = compare+'%';
        //}
        if(name == '新建商机'){
            name = '新增销售机会'
            size = '个';
            icon = 'shangji';
            buildLi();
        }else if(name == '新建客户'){
            name = '新增公司'
            size = '个';
            icon = 'gongsi';
            buildLi();
        }else if(name=='电话'){
            size = '个';
            icon = 'dianhua';
            buildLi();
        }else if(name=='拜访签到'){
            name = '签到'
            size = '次';
            icon = 'qiandao';
            buildLi();
        }else if(name=='记录'){
            size = '个';
            icon = 'jilu';
            buildLi();
        }
        function buildLi(){
            var li = $('<li class="boxSizing"> <em class="'+icon+'"></em> ' +
            '<div> <span class="name">'+name+'</span> <span class="num">'+num+size+'</span> </div> ' +
            '<span class="count">'+count+'</span> </li>');
            if(count*1>0){
                li.find('.count').addClass('more');
            }else if(count*1<0){
                li.find('.count').addClass('less');
            }
            $('.actSum > ul').append(li);
        }
    }
}
//团队业绩排名
var rankDateType = 4;
function get_rank(){
    //先加载缓存数据
    if(localStorage.getItem(xsyUser.id)){
        var ls = JSON.parse(localStorage.getItem(xsyUser.id));
        if(ls.index){
            if(ls.index.rankData){
                if(ls.index.rankData.success==true){
                    //alert('加载rank缓存数据')
                    ranking(ls.index.rankData);
                }
            }
        }
    }
    $('.ranking .options li').each(function(){
        $(this).bind('click',function(){
            if($(this).index()==1){
                rankDateType = 4;
                $('.ranking .selector > span').text('本月');
            }
            if($(this).index()==2){
                rankDateType = 3;
                $('.ranking .selector > span').text('上月');
            }
            if($(this).index()==3){
                rankDateType = 7;
                $('.ranking .selector > span').text('本季');
            }
            if($(this).index()==4){
                rankDateType = 6;
                $('.ranking .selector > span').text('上季');
            }
            if($(this).index()==5){
                rankDateType = 10;
                $('.ranking .selector > span').text('本年');
            }
            if($(this).index()==6){
                rankDateType = 9;
                $('.ranking .selector > span').text('上年');
            }
            $('.ranking .list').children().remove();
            $('.ranking .searchMore').remove();
            get_rank_data(rankDateType);
        })
    })
}
function get_rank_data(rankDateType){
    console.log('rankDateType:'+rankDateType)
    $.ajax({
        url:apppath+'/wx/char/salesRank.action',
        data:{
            searchDateType:rankDateType
        },
        success:function(data){
            var oData = eval('('+data+')');
            if(oData.success == true){
                if(rankDateType == 4){
                    var ls;
                    if(localStorage.getItem(xsyUser.id)){
                        ls = JSON.parse(localStorage.getItem(xsyUser.id));
                        ls.index = {'rankData':oData}
                    }else{
                        ls = {'index':{'rankData':oData}}
                    }
                    localStorage.setItem(xsyUser.id,JSON.stringify(ls));
                }
                //alert('加载rank新数据')
                ranking(oData);
            }else{
                if(oData.entity&&oData.entity.status&&oData.entity.status == '0000003'){
                    location.href = apppath+'/wx/authorized/no.action?'+ddcommparams;
                }else {
                    myAlert('暂时无法获取数据，请稍后再试')
                }
            }
        },
        error:function(){
            myAlert('暂时无法获取数据，请检查您的网络')
        }
    })
}
var mydata = {
    "entity": {
        "series": [
            {
                "ord": 0,
                "userId": 75918,
                "userName": "天天他",
                "money": 10000000
            },
            {
                "ord": 1,
                "userId": 34234,
                "userName": "rete",
                "money": 4122220
            },
            {
                "ord": 2,
                "userId": 56565,
                "userName": "尔特让",
                "money": 3332320
            },
            {
                "ord": 3,
                "userId": 34345,
                "userName": "儿儿",
                "money": 2262220
            },
            {
                "ord": 4,
                "userId": 94524,
                "userName": "有人人",
                "money": 666660
            },
            {
                "ord": 5,
                "userId": 45336,
                "userName": "一天",
                "money": 658650
            },
            {
                "ord": 6,
                "userId": 56425,
                "userName": "人额",
                "money": 76780
            },
            {
                "ord": 7,
                "userId": 36354,
                "userName": "而人额",
                "money": 76770
            },
            {
                "ord": 8,
                "userId": 24455,
                "userName": "人员",
                "money": 8670
            },
            {
                "ord": 9,
                "userId": 22222,
                "userName": "河野谈",
                "money": 6660
            },
            {
                "ord": 10,
                "userId": 34341,
                "userName": "发送的",
                "money": 210
            },
            {
                "ord": 11,
                "userId": 12213,
                "userName": "发毒素",
                "money": 0
            },
            {
                "ord": 12,
                "userId": 22222,
                "userName": "河野谈",
                "money": 6660
            },
            {
                "ord": 13,
                "userId": 34341,
                "userName": "发送的",
                "money": 210
            },
            {
                "ord": 14,
                "userId": 12213,
                "userName": "发毒素",
                "money": 0
            },
            {
                "ord": 15,
                "userId": 22222,
                "userName": "河野谈",
                "money": 6660
            },
            {
                "ord": 16,
                "userId": 34341,
                "userName": "发送的",
                "money": 2112330
            },
            {
                "ord": 17,
                "userId": 12213,
                "userName": "发毒素",
                "money": 0
            }
        ]
    },
    "success": true
}

function ranking(data){
    console.log(data)
    if(data.entity.series.length==0||data.entity.series[0].money==0){
        $('#ranking .list').hide();
        $('#ranking .noDataRanking').show();
    }else{
        $('#ranking .list').show();
        $('#ranking .noDataRanking').hide();
    }
    var len = 10;
    if(data.entity.series.length<=10){
        len = data.entity.series.length;
    }
    build_rank($('#ranking .list'),len);
    function build_rank($container,length){
        $container.children().remove();
        for(var i=0;i<length;i++){
            var ord = (Number(data.entity.series[i].ord)+1);
            if(ord<10){
                ord = '0'+String(ord);
            };
            var name = data.entity.series[i].userName;
            var money = data.entity.series[i].money;
            var li = $('<li> <span class="label">'+ord+'</span> <span class="name">'+name+'</span> <p class="pillowBlock"><span class="pillow"></span></p> <span class="value">'+money+'</span> </li>')
            var id = data.entity.series[i].userId;
            if(xsyUser){
                if(xsyUser.id == id){
                    //if('34234' == id){
                    li.addClass('myself');
                }
            }else{
                myAlert('未获取到当前用户');
            }
            $container.append(li);
        }
        var width1 = $container.find('li').width()-$container.find('li').eq(0).find('.value').width()-190;
        $container.find('li').eq(0).find('.pillow').css('width',width1);
        $container.find('.pillowBlock').css('width',width1);
        for(var i=1;i<length;i++){
            var pillowWidth = width1/data.entity.series[0].money*data.entity.series[i].money;
            if(pillowWidth<=3){
                pillowWidth = 3;
            }
            $container.find('li').eq(i).find('.pillow').css('width',pillowWidth);
        }
    }
    if($('.ct').length>0){
        build_rank($('.ct .list'),data.entity.series.length);
    }
    if(data.entity.series.length>10){
        $('#ranking .searchMore').remove();
        $('#ranking').append('<div class="searchMore">查看全部</div>');
        $('.searchMore').bind('click',function(){
            checkTable('业绩排名');
            $('.ct').addClass('ranking');
            var top = $('<div class="title boxSizing"><p class="label">业绩总值</p><p class="value">'+data.entity.totalCount+'元</p></div>');
            var selector = $('<div class="selector"> <span>本月</span> <ul class="options lower boxSizing"> ' +
            '<div class="triangle"></div> ' +
            '<li class="boxSizing">本月</li> <li class="boxSizing">上月</li> ' +
            '<li class="boxSizing">本季</li> <li class="boxSizing">上季</li> ' +
            '<li class="boxSizing">本年</li> <li class="boxSizing">上年</li> </ul> </div>');
            selector.find('span').eq(0).text($('#ranking .selector >span').text());
            top.append(selector);
            selector.bind('click',function(){
                if($(this).find('.options').css('display')=='block'){
                    $(this).find('.options').hide();
                }else{
                    $(this).find('.options').show();
                }
                return false;
            })
            var bot = $('<ul class="list boxSizing"></ul>');
            $('.ct').append(top);
            $('.ct').append(bot);
            build_rank($('.ct .list'),data.entity.series.length);
            get_rank();
        })
    }
}

//跟进记录
var recordDateType = 1;
function recordSelect(){
    $('#recordList .selector .options li').each(function(){
        $(this).bind('click',function(){
            recordDateType = $(this).attr('value');
            recordPageNo = 0;
            $('#recordList > .main').children().remove();
            get_record($('#recordList > .main'),20);
        })
    })
}
var recordPageNo = 0;
function get_record($parent_obj){
    //先加载缓存数据
    //if(localStorage.getItem(xsyUser.id)){
    //    var ls = JSON.parse(localStorage.getItem(xsyUser.id));
    //    if(ls.index){
    //        if(ls.index.recordData){
    //            //alert('加载opp缓存数据')
    //            recordByData(ls.index.recordData,$parent_obj);
    //        }
    //    }
    //}
    $.ajax({
        url:apppath+'/wx/statics/activerecordlist.action',
        data:{
            pagesize:20,
            pageNo:recordPageNo,
            searchDateType:recordDateType
        },
        success:function(data){
            var oData = eval('('+data+')');
            //测试校验
            console.log('跟进记录：'+data)
            if(oData.success==true){
                //if(recordDateType == 4){
                //    var ls;
                //    if(localStorage.getItem(xsyUser.id)){
                //        ls = JSON.parse(localStorage.getItem(xsyUser.id));
                //        ls.index = {'recordData':oData}
                //    }else{
                //        ls = {'index':{'recordData':oData}}
                //    }
                //    localStorage.setItem(xsyUser.id,JSON.stringify(ls));
                //}
                //alert('加载record新数据')
                recordByData(oData,$parent_obj);
            }else{
                if(oData.entity&&oData.entity.status&&oData.entity.status == '0000003'){
                    location.href = apppath+'/wx/authorized/no.action?'+ddcommparams;
                }else {
                    myAlert('暂时无法获取数据，请稍后再试')
                    console.log('跟进记录：' + data)
                }
            }
        },
        error:function(){
            myAlert('暂时无法获取数据，请检查您的网络')
        }
    })
}
function recordByData(oData,$parent_obj){
    var totalSize = oData.entity.totalSize;
    $('#recordList > .top > .title > span').text('跟进记录('+totalSize+')');
    if(oData.entity.records.length==0){
        $parent_obj.append($('<div class="noInfo">该范围内没有跟进记录</div>'));
        console.log('该对象暂无跟进记录');
    }else{
        build_record_list($parent_obj,oData);
        if ($parent_obj.find('li').length < totalSize) {
            $('#loading').remove();
            build_end($parent_obj,'加载更多', 'load_more');
            recordPageNo ++;
            scroll_load();
        } else {
            $('#loading').remove();
        };
    }
}
function build_end($parent_obj,info,name){
    var end = $('<div id="'+name+'" class="end_info">'+info+'</div>');
    $parent_obj.append(end);
}
function scroll_load(){
    var windowHeight = document.body.clientHeight;
    $(window).bind('scroll',function(){
        if($('#load_more').length>0){
            if($('#load_more').offset().top-$(document).scrollTop()<=windowHeight-$('#load_more').height()-50){
                $('#load_more').remove();
                build_end($('#recordList > .main'),'加载中…','loading');
                get_record($('#recordList > .main'),20);
                $(this).unbind('scroll');
            }
        }
    })
}
function build_record_list($parent_obj,data){
    var item = data.entity.records;
    for(var i=0;i<item.length;i++){
        var userName = item[i].user.userName;
        var typeName = item[i].typeName;
        var objectName = item[i].objectName;
        var belongId = item[i].belongId;
        var objectId = item[i].objectId;
        var startTime = item[i].startTime.substr(-5);
        var startDate = item[i].startTime.substr(0,11);
        var type = item[i].recordType.type;
        if(i!=0&&item[i].startTime.substr(0,11)!=item[i-1].startTime.substr(0,11)){
            if(i==0&&$parent_obj.find('.day').last().text == item[i].startTime.substr(0,11)){
            }else{
                var day = $('<div class="day"><em></em><p class="startDate">'+startDate+'</p><div class="ban"></div></div>');
                $parent_obj.append(day);
            }
        }else if(i==0&&recordPageNo == 0){
            var day = $('<div class="day"><em></em><p class="startDate">'+startDate+'</p><div class="ban"></div></div>');
            $parent_obj.append(day);
        }
        var oList = $('<li class="boxSizing"><div class="info"><span class="userName">'+userName+'</span><span class="typeName">'+typeName+':</span><span class="objectName">'+objectName+'</span><span class="startTime">'+startTime+'</span></div></li>');
        oList.find('.objectName').attr({'belongId':belongId,'objectId':objectId});
        oList.find('.objectName').bind('click',function(){
            var url;
            if($(this).attr('belongId') == 1){
                url = apppath+'/wx/account/info.action?'+ddcommparams+'&accountId='+$(this).attr('objectId')+'&from=index&page=behavior';
            }else if($(this).attr('belongId') == 2){
                url = apppath+'/wx/contact/info.action?'+ddcommparams+'&contactId='+$(this).attr('objectId')+'&from=index&page=behavior';
            }else if($(this).attr('belongId') == 3){
                url = apppath+'/wx/opportunity/detail.action?'+ddcommparams+'&opportunityId='+$(this).attr('objectId')+'&from=index&page=behavior';
            }
            location.href = url;
        });
        if(type == "TEL"){
            oList.addClass('phone_bg');
        }else if(type == "SIGN_IN"){
            oList.addClass('sign_in_bg');
        }else if(type == "RECORD"){
            oList.addClass('record_bg');
        }
        if(item[i].content){
            var contentText = item[i].content;
            var content_span = $('<span class="content_span">'+contentText+'</span>');
            var content = $('<div class="content"><em class="triangle"></em></div>');
            content.append(content_span);
            if(contentText.length>32){
                var shortContentText = contentText.substr(0,30)+'...';
                var short_content_span = $('<span class="short_content_span">'+shortContentText+'</span>');
                content.append(short_content_span);
                content_span.css('display','none');
                var index = 0;
                var more_btn = $('<em class="more"></em>');
                more_btn.bind('click',function(){
                    index++;
                    $(this).parent().find('.short_content_span').slideToggle();
                    $(this).parent().find('.content_span').slideToggle();
                    $(this).css("transform", "rotate(" + (90+180*index) + "deg)");

                })
                content.append(more_btn);
            }
            oList.append(content);
        }
        if(item[i].location){
            var locat = $('<div class="location">'+item[i].location+'</div>');
            oList.append(locat);
        }
        var bottom_border = $('<div class="bottom_border"></div>');
        oList.append(bottom_border);
        $parent_obj.append(oList);
    }
    removeBorder();
}
function removeBorder(){
    $('#record_tab li').each(function(){
        if($(this).next().hasClass('day')){
            $(this).find('.bottom_border').remove();
        }
    })
}

//重点商机
//列表页单元创建
function build_list_unit(oData,$container,i){
    var item = oData.entity.records[i];
    var saleStageText = item.saleStageText;
    var opportunityName = item.opportunityName;
    var Money = item.money;
    var accountName = item.accountName;
    var id = item.id;
    var saleStageOrder = item.saleStageOrder;
    //阶段百分比
    var jieduan_p = item.saleStagePercentage;
    var li = $('<li class="boxSizing"><em class="icon"></em><div class="info"><div class="top">' +
    '<span class="opportunityName">'+opportunityName+'</span><span class="Money boxSizing">'+Money+'元</span>' +
    '</div><div class="bot"><p class="accountName">'+accountName+'</p><p class="jieduan">阶段：'+saleStageText+'</p>' +
    '<span class="jieduan_p">'+jieduan_p+'%</span></div></div></li>');
    li.attr('id',id);
    li.attr('name',opportunityName);
    li.find('.icon').addClass('jieduan'+saleStageOrder);
    li.bind('click',function(){
        location.href = apppath+'/wx/opportunity/detail.action?'+ddcommparams+'&opportunityId='+$(this).attr('id')+'&from=index';
    })
    $container.append(li);
}
//列表页下拉加载
function listScrollFunc(){
    $('#content').bind('scroll',function(){
        if($('#load_more').length>0){
            if($('#load_more').offset().top<=$(window).height()-$('#load_more').height()-53){
                $('#load_more.end_info').remove();
                $('#list .main').append($('<div id="loading" class="end_info">加载中…</div>'));
                build_opp_list(apppath+'/wx/statics/topopportunity.action',$('#list .main'),build_list_unit,listScrollFunc,'scroll')
            }
        }
    })
}

function opp_options(){

    $('#list .selector .options li').each(function(){
        $(this).bind('click',function(){
            oppDateType = $(this).attr('value');
            pageNo = 0;
            build_opp_list(apppath+'/wx/statics/topopportunity.action',$('#list .main'),build_list_unit,listScrollFunc);
        })
    })
}
function build_opp_list(url,$container,build_unit,scrollFunc,type){
    //先加载缓存数据
    //if(oppDateType == 4){
    //    if(localStorage.getItem(xsyUser.id)){
    //        var ls = JSON.parse(localStorage.getItem(xsyUser.id));
    //        if(ls.index){
    //            if(ls.index.oppData){
    //                if(ls.index.oppData.success==true){
    //                    alert('加载opp缓存数据')
    //                    oppListByData(ls.index.oppData,$container,build_unit,scrollFunc,type);
    //                }
    //            }
    //        }
    //    }
    //}
    obj = {
        pageNo:pageNo,
        pagesize:pageSize,
        searchDateType:oppDateType
    };
    //alert('oppDateType'+oppDateType)
    $.ajax({
        url:url,
        data:obj,
        async:false,
        success:function(data){
            console.log('商机:'+data)
            var oData = eval('('+data+')');
            if(oData.success == true){
                if(oppDateType == 4){
                    var ls;
                    if(localStorage.getItem(xsyUser.id)){
                        ls = JSON.parse(localStorage.getItem(xsyUser.id));
                        ls.index = {'oppData':oData}
                    }else{
                        ls = {'index':{'oppData':oData}}
                    }
                    localStorage.setItem(xsyUser.id,JSON.stringify(ls));
                }
                //alert('加载opp新数据')
                oppListByData(oData,$container,build_unit,scrollFunc,type);
            }else{
                if(oData.entity&&oData.entity.status&&oData.entity.status == '0000003'){
                    location.href = apppath+'/wx/authorized/no.action?'+ddcommparams;
                }else {
                    myAlert('暂时无法获取数据，请稍后再试');
                }
            }
        },
        error:function(){
            myAlert('暂时无法获取数据，请检查您的网络');
        }
    })
}
function oppListByData(oData,$container,build_unit,scrollFunc,type){
    if(type != 'scroll'){
        $container.children().remove();
    }
    //获取总条数
    var len = oData.entity.records.length;
    var sEnd;
    if(len == 0){
        //无结果情况
        $container.append($('<div class="noInfo">该范围内没有重点销售机会</div>'));
    }else{
        //有结果情况
        if(len-pageNo*pageSize>pageSize){
            //下拉有加载
            sEnd = pageSize;
        }else{
            //下拉无加载
            sEnd = len-pageNo*pageSize;
        }
        //生成列表
        for(var i=0;i<sEnd;i++){
            //创建单元
            build_unit(oData,$container,i);
        }
        //创建加载更多栏
        if ($container.children().length < len) {
            $container.append($('<div id="load_more" class="end_info">加载更多</div>'));
        }
        //pageNo+1
        obj.pageNo ++;
        pageNo++;
        //去除加载中栏（如果存在）
        $('#loading').remove();
        //判断是否滚动到底部
        scrollFunc();
    }
}