"use strict";

var _interopRequireDefault = require("@babel/runtime-corejs2/helpers/interopRequireDefault");

var _Object$defineProperty = require("@babel/runtime-corejs2/core-js/object/define-property");

_Object$defineProperty(exports, "__esModule", {
  value: true
});

exports.normalizeUploadConfig = normalizeUploadConfig;

var _isArray = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/array/is-array"));

var _iterator = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/symbol/iterator"));

var _symbol = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/symbol"));

var _getOwnPropertySymbols = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/object/get-own-property-symbols"));

var _assign = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/object/assign"));

var _config = require("../config");

var _upload = require("../upload");

var __assign = void 0 && (void 0).__assign || function () {
  __assign = _assign["default"] || function (t) {
    for (var s, i = 1, n = arguments.length; i < n; i++) {
      s = arguments[i];

      for (var p in s) {
        if (Object.prototype.hasOwnProperty.call(s, p)) t[p] = s[p];
      }
    }

    return t;
  };

  return __assign.apply(this, arguments);
};

var __rest = void 0 && (void 0).__rest || function (s, e) {
  var t = {};

  for (var p in s) {
    if (Object.prototype.hasOwnProperty.call(s, p) && e.indexOf(p) < 0) t[p] = s[p];
  }

  if (s != null && typeof _getOwnPropertySymbols["default"] === "function") for (var i = 0, p = (0, _getOwnPropertySymbols["default"])(s); i < p.length; i++) {
    if (e.indexOf(p[i]) < 0 && Object.prototype.propertyIsEnumerable.call(s, p[i])) t[p[i]] = s[p[i]];
  }
  return t;
};

var __read = void 0 && (void 0).__read || function (o, n) {
  var m = typeof _symbol["default"] === "function" && o[_iterator["default"]];
  if (!m) return o;
  var i = m.call(o),
      r,
      ar = [],
      e;

  try {
    while ((n === void 0 || n-- > 0) && !(r = i.next()).done) {
      ar.push(r.value);
    }
  } catch (error) {
    e = {
      error: error
    };
  } finally {
    try {
      if (r && !r.done && (m = i["return"])) m.call(i);
    } finally {
      if (e) throw e.error;
    }
  }

  return ar;
};

var __spread = void 0 && (void 0).__spread || function () {
  for (var ar = [], i = 0; i < arguments.length; i++) {
    ar = ar.concat(__read(arguments[i]));
  }

  return ar;
};

function normalizeUploadConfig(config, logger) {
  var _a = __assign({}, config),
      upprotocol = _a.upprotocol,
      uphost = _a.uphost,
      otherConfig = __rest(_a, ["upprotocol", "uphost"]);

  var normalizeConfig = __assign({
    uphost: [],
    retryCount: 3,
    checkByMD5: false,
    forceDirect: false,
    useCdnDomain: true,
    checkByServer: false,
    concurrentRequestLimit: 3,
    chunkSize: _upload.DEFAULT_CHUNK_SIZE,
    upprotocol: 'https',
    debugLogLevel: 'OFF',
    disableStatisticsReport: false
  }, otherConfig); // 兼容原来的 http: https: 的写法


  if (upprotocol) {
    normalizeConfig.upprotocol = upprotocol.replace(/:$/, '');
  }

  var hostList = [];

  if (logger && (config === null || config === void 0 ? void 0 : config.uphost) != null && (config === null || config === void 0 ? void 0 : config.region) != null) {
    logger.warn('do not use both the uphost and region config.');
  } // 如果同时指定了 uphost 参数，添加到可用 host 列表


  if (uphost) {
    if ((0, _isArray["default"])(uphost)) {
      hostList.push.apply(hostList, __spread(uphost));
    } else {
      hostList.push(uphost);
    } // 否则如果用户传了 region，添加指定 region 的 host 到可用 host 列表

  } else if (normalizeConfig === null || normalizeConfig === void 0 ? void 0 : normalizeConfig.region) {
    var hostMap = _config.regionUphostMap[normalizeConfig === null || normalizeConfig === void 0 ? void 0 : normalizeConfig.region];

    if (normalizeConfig.useCdnDomain) {
      hostList.push.apply(hostList, __spread(hostMap.cdnUphost));
    } else {
      hostList.push.apply(hostList, __spread(hostMap.srcUphost));
    }
  }

  return __assign(__assign({}, normalizeConfig), {
    uphost: hostList.filter(Boolean)
  });
}