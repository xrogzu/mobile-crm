/**
 * Created by liqiao on 8/10/15.
 */

//logger.i('Here we go...');

/**
 * _config comes from server-side template. see views/index.jade
 */
dd.config({
    debug: true,
    agentId:_config.agentId,
    corpId: _config.corpId,
    timeStamp: _config.timeStamp,
    nonceStr: _config.nonceStr,
    signature: _config.signature,
    jsApiList: ['runtime.info',
        'biz.contact.choose',
        'device.notification.confirm',
        'device.notification.alert',
        'device.notification.prompt',
        'biz.ding.post',
        'biz.telephone.call',
        'biz.util.open',
        'biz.util.qrcode',
        'biz.navigation.setRight',
        'biz.navigation.setIcon',
        'biz.navigation.setTitle',
        'biz.navigation.close',
        'device.geolocation.get',
        'biz.util.uploadImage',
        'biz.util.uploadImageFromCamera',
        'biz.util.share',
        'biz.util.ut',
        'biz.navigation.back']
});
//alert("_config:" + JSON.stringify(_config));
//logger.i("_config:" + JSON.stringify(_config));
