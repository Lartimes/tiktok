"use strict";

var _Object$defineProperty = require("@babel/runtime-corejs2/core-js/object/define-property");

_Object$defineProperty(exports, "__esModule", {
  value: true
});

exports.reportV3 = reportV3;

var _utils = require("../utils");

/**
 * @param  {string} token 上传使用的 token
 * @param  {V3LogInfo} data 上报的统计数据
 * @param  {number} retry 重试的次数，默认值 3
 * @description v3 版本的日志上传接口，参考文档 https://github.com/qbox/product/blob/master/kodo/uplog.md#%E7%89%88%E6%9C%AC-3。
 */
function reportV3(token, data, retry) {
  if (retry === void 0) {
    retry = 3;
  }

  var xhr = (0, _utils.createXHR)();
  xhr.open('POST', 'https://uplog.qbox.me/log/3');
  xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
  xhr.setRequestHeader('Authorization', (0, _utils.getAuthHeaders)(token).Authorization);

  xhr.onreadystatechange = function () {
    if (xhr.readyState === 4 && xhr.status !== 200 && retry > 0) {
      reportV3(token, data, retry - 1);
    }
  }; // 顺序参考：https://github.com/qbox/product/blob/master/kodo/uplog.md#%E7%89%88%E6%9C%AC-3


  var stringifyData = [data.code || '', data.reqId || '', data.host || '', data.remoteIp || '', data.port || '', data.duration || '', data.time || '', data.bytesSent || '', data.upType || '', data.size || ''].join(',');
  xhr.send(stringifyData);
}