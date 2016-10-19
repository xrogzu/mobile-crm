// 'use strict';
// var getMethod = function(method, ns) {
//     var arr = method.split('.');
//     var namespace = ns || dd;
//     for (var i = 0, k = arr.length; i < k; i++) {
//         if (i === k - 1) {
//             return namespace[arr[i]];
//         }
//         if (typeof namespace[arr[i]] == 'undefined') {
//             namespace[arr[i]] = {};
//         }
//         namespace = namespace[arr[i]];
//     }
// };

// function st(elem) {
//     var $elem = $(elem);
//     var t = $elem.offset().top;
//     window.scrollTo(0, t)
// }
// //st('#J_2');


// //demo
// console.log('钉钉版本号：', dd.version);
// /*dd.config({
//     debug: false,
//     appId: 'f8b4f85f3a794e77',
//     timestamp: 1426559642,
//     nonceStr: 'f0MCnGU69VPQA2iC',
//     signature: '05256628b0c49a6ade532ea1116c715a8de29a60'
// });*/
// dd.error(function(res) {
//     console.log(res);
// });

// dd.ready(function(bridge) {
//     //console.log('是否支持bridge：', dd.support);
//     //通过配置触发

//     //企业测试配置
//     var config = window.CONFIG || {
//         corpId: _config.corpId,
//         //users: ['manager4999','02024763207326']
//         users: ['02024763207326']
//     };
//     $('.J_method_btn').on('click', function() {
//         var $this = $(this);
//         var method = $this.data('method');
//         var action = $this.data('action');
//         var param = $this.data('param') || {};
//         //alert(param);
//         if (typeof param === 'string') {
//             param = JSON.parse(param);
//         }
//         // 参数灵活变更
//         //alert(config.corpId);
//         if (param.corpId) {
//             param.corpId = config.corpId;
//             if (param.id) {
//                 param.id = config.users[0];
//             }
//             if (param.users) {
//                 param.users = config.users;
//             }
//         }
//         if (param.params && param.params.corpId) {
//             param.params.corpId = config.corpId;
//             if (param.params.id) {
//                 param.params.id = config.users[0];
//             }
//             if (param.params.users) {
//                 param.params.users = config.users;
//             }
//         }
//         // end

//         param.onSuccess = function(result) {
//             console.log(method, '调用成功，success', result)
//             if (action === 'alert') {
//                 dd.device.notification.alert({
//                     title: method,
//                     message: '传参：' + JSON.stringify(param, null, 4) + '\n' + '响应：' + JSON.stringify(result, null, 4)
//                 });
//             }
//         };
//         param.onFail = function(result) {
//             console.log(method, '调用失败，fail', result)
//         };
//         getMethod(method)(param);
//     });

//     //直接取值
//     $('.J_method_out_btn').on('click', function() {
//         var $this = $(this);
//         var method = $this.data('method');
//         var action = $this.data('action');
//         var param = $this.data('param') || {};
//         if (typeof param === 'string') {
//             param = JSON.parse(param);
//         }
//         var result = getMethod(method, window);
//         if (action === 'alert') {
//             dd.device.notification.alert({
//                 title: method,
//                 message: JSON.stringify(result, null, 4)
//             });
//         }
//     });
//     //单独处理
//     $('.J_shake').on('click', function() {
//         dd.device.accelerometer.watchShake({
//             sensitivity: 15, //振动幅度，加速度变化超过这个值后触发shake
//             frequency: 150, //采样间隔(毫秒)，指每隔多长时间对加速度进行一次采样， 然后对比前后变化，判断是否触发shake
//             callbackDelay: 1000,
//             onSuccess: function(result) {
//                 console.log('watchShake success', result);
//                 dd.device.notification.vibrate({
//                     duration: 300,
//                     onSuccess: function() {
//                         console.log('……………………震动……………………')
//                     },
//                     onFail: function() {
//                         console.log('木有震动')
//                     }
//                 })
//             },
//             onFail: function(result) {
//                 console.log('error', result)
//             }
//         });
//     });

//     $('.J_nav_left').on('click', function() {
//         dd.biz.navigation.setLeft({
//             control: true,
//             text: "取消",
//             onSuccess: function(result) {
//                 console.log('左侧导航回调', result);
//                 dd.device.notification.confirm({
//                     message: ' 点确定重置左侧按钮，点取消返回上一页',
//                     title: '操作',
//                     buttonLabels: ['确定', '取消'],
//                     onSuccess: function(result) {
//                         var index = result.buttonIndex;
//                         if (index === 0) {
//                             console.log('弹窗回调', result, '准备恢复左侧导航');
//                             dd.biz.navigation.setLeft({
//                                 control: false,
//                                 text: "",
//                                 onSuccess: function(result) {
//                                     console.log('左侧导航恢复完成', result)
//                                 },
//                                 onFail: function() {

//                                 }
//                             });
//                         } else {
//                             console.log('调用导航回退');
//                             dd.biz.navigation.back();
//                         }
//                     },
//                     onFail: function() {

//                     }
//                 });

//             },
//             onFail: function() {

//             }
//         })
//     });

//     $('.J_nav_right').on('click', function() {
//         dd.biz.navigation.setRight({
//             control: true,
//             text: "发送",
//             onSuccess: function(result) {
//                 console.log('右侧导航回调', result);
//                 dd.device.notification.confirm({
//                     message: '点击了发送按钮， 要还原按钮状态吗？',
//                     title: '操作',
//                     buttonLabels: ['好的', '取消'],
//                     onSuccess: function(result) {
//                         var index = result.buttonIndex;
//                         if (index === 0) {
//                             console.log('弹窗回调', result, '准备恢复右侧导航');
//                             dd.biz.navigation.setRight({
//                                 control: false,
//                                 text: "",
//                                 onSuccess: function(result) {
//                                     console.log('右侧导航恢复完成', result)
//                                 },
//                                 onFail: function() {

//                                 }
//                             });
//                         } else {
//                             console.log('弹窗回调', result, '什么也不干');
//                         }
//                     },
//                     onFail: function() {

//                     }
//                 });

//             },
//             onFail: function() {

//             }
//         })
//     });
//     $('.J_loading').on('click', function() {
//         var $this = $(this);
//         var method = $this.data('method');
//         var action = $this.data('action');
//         var param = $this.data('param') || {};
//         if (typeof param === 'string') {
//             param = JSON.parse(param);
//         }
//         param.onSuccess = function(result) {
//             console.log(method, '调用成功，success', result)
//             setTimeout(function() {
//                 dd.device.notification.hidePreloader();
//             }, 3000)
//         };
//         param.onFail = function(result) {
//             console.log(method, '调用失败，fail', result)
//         };
//         getMethod(method)(param);
//     });

//     $('.J_pull_to_refresh').on('click', function() {
//         dd.ui.pullToRefresh.enable({
//             onSuccess: function(result) {
//                 console.log('下拉刷新回调', result);
//                 dd.device.notification.confirm({
//                     message: '点击确定2s后收起loading',
//                     title: '',
//                     buttonLabels: ['确定', '取消'],
//                     onSuccess: function(result) {
//                         var index = result.buttonIndex;
//                         if (index === 0) {
//                             setTimeout(function() {
//                                 dd.ui.pullToRefresh.stop();
//                             }, 2000)
//                             console.log('收起下拉刷新');
//                         } else {
//                             console.log('取消');
//                         }
//                     },
//                     onFail: function() {

//                     }
//                 });

//             },
//             onFail: function() {

//             }
//         })
//     });





//     $('.J_upload').on('click', function() {
//         var $this = $(this);
//         var method = $this.data('method');
//         var action = $this.data('action');
//         var param = $this.data('param') || {};
//         if (typeof param === 'string') {
//             param = JSON.parse(param);
//         }
//         param.onSuccess = function(result) {
//             console.log(method, '调用成功，success', result)
//             if (result && result.length > 0) {
//                 var url = 'http://dingtalk.aliapp.com/ox';
//                 $.ajax({
//                     url: url,
//                     type: 'get',
//                     data: {
//                         k: 'jsapiupload',
//                         text: result.join(',')
//                     },
//                     dataType: "jsonp",
//                     jsonp: 'callback',
//                     success: function(data) {
//                         console.log(data);
//                     },
//                     error: function(err) {
//                         console.log('上传服务器失败', err)
//                     }
//                 })
//             }
//             if (action === 'alert') {
//                 dd.device.notification.alert({
//                     title: method,
//                     message: '传参：' + JSON.stringify(param, null, 4) + '\n' + '响应：' + JSON.stringify(result, null, 4)
//                 });
//             }
//         };
//         param.onFail = function(result) {
//             console.log(method, '调用失败，fail', result)
//         };
//         getMethod(method)(param);
//     });

//     $(document).on('click', '.J_set_account', function() {
//         bridge.callHandler('getAccount', '10007', function(res) {
//             alert(res)
//             location.href = 'http://oa.new-see.com/history.html'
//         });
//     })

//     //事件监听
//     //document.addEventListener('backbutton', function(e) {
//     //    e.preventDefault();
//     //    dd.device.notification.alert({
//     //        message: '哎呀，你不小心点到返回键啦!',
//     //        title: '...警告...'
//     //    });
//     //}, false);
//     document.addEventListener('pause', function(e) {
//         e.preventDefault();
//         console.log('事件：pause')
//     }, false);
//     document.addEventListener('resume', function(e) {
//         e.preventDefault();
//         console.log('事件：resume')
//     }, false);
//     document.addEventListener('online', function(e) {
//         e.preventDefault();
//         console.log('事件：online')
//     }, false);
//     document.addEventListener('offline', function(e) {
//         e.preventDefault();
//         console.log('事件：offline')
//     }, false);
//     document.addEventListener('swipeRefresh', function(e) {
//         e.preventDefault();
//         console.log('事件：swipeRefresh')
//     }, false);
// });
// $(document).on('change', '.J_file', function(e) {
//     var $this = $(this);
//     var type = $this.data('type');
//     var file = this.files[0];
//     console.log(this.value)
//     for (var i in file) {
//         console.log(i, ':', file[i])
//     }
//     var url;
//     switch (type) {
//         case 'create':
//             url = window.URL.createObjectURL(file);
//             console.log(url)
//             $('body').append('<img src="' + url + '"/>');
//             break;
//         case 'reader':
//             var reader = new FileReader();
//             reader.onload = function(e) {
//                 $('body').append('<img src="' + e.target.result + '"/>');
//             };
//             reader.readAsDataURL(file);
//             break;
//     }
// })
