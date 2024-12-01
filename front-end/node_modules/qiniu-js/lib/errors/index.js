"use strict";

var _interopRequireDefault = require("@babel/runtime-corejs2/helpers/interopRequireDefault");

var _Object$defineProperty = require("@babel/runtime-corejs2/core-js/object/define-property");

_Object$defineProperty(exports, "__esModule", {
  value: true
});

exports.QiniuNetworkError = exports.QiniuRequestError = exports.QiniuError = exports.QiniuErrorName = void 0;

var _create = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/object/create"));

var _setPrototypeOf = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/object/set-prototype-of"));

var __extends = void 0 && (void 0).__extends || function () {
  var _extendStatics = function extendStatics(d, b) {
    _extendStatics = _setPrototypeOf["default"] || {
      __proto__: []
    } instanceof Array && function (d, b) {
      d.__proto__ = b;
    } || function (d, b) {
      for (var p in b) {
        if (b.hasOwnProperty(p)) d[p] = b[p];
      }
    };

    return _extendStatics(d, b);
  };

  return function (d, b) {
    _extendStatics(d, b);

    function __() {
      this.constructor = d;
    }

    d.prototype = b === null ? (0, _create["default"])(b) : (__.prototype = b.prototype, new __());
  };
}();

var QiniuErrorName;
exports.QiniuErrorName = QiniuErrorName;

(function (QiniuErrorName) {
  // 输入错误
  QiniuErrorName["InvalidFile"] = "InvalidFile";
  QiniuErrorName["InvalidToken"] = "InvalidToken";
  QiniuErrorName["InvalidMetadata"] = "InvalidMetadata";
  QiniuErrorName["InvalidChunkSize"] = "InvalidChunkSize";
  QiniuErrorName["InvalidCustomVars"] = "InvalidCustomVars";
  QiniuErrorName["NotAvailableUploadHost"] = "NotAvailableUploadHost"; // 缓存相关

  QiniuErrorName["ReadCacheFailed"] = "ReadCacheFailed";
  QiniuErrorName["InvalidCacheData"] = "InvalidCacheData";
  QiniuErrorName["WriteCacheFailed"] = "WriteCacheFailed";
  QiniuErrorName["RemoveCacheFailed"] = "RemoveCacheFailed"; // 图片压缩模块相关

  QiniuErrorName["GetCanvasContextFailed"] = "GetCanvasContextFailed";
  QiniuErrorName["UnsupportedFileType"] = "UnsupportedFileType"; // 运行环境相关

  QiniuErrorName["FileReaderReadFailed"] = "FileReaderReadFailed";
  QiniuErrorName["NotAvailableXMLHttpRequest"] = "NotAvailableXMLHttpRequest";
  QiniuErrorName["InvalidProgressEventTarget"] = "InvalidProgressEventTarget"; // 请求错误

  QiniuErrorName["RequestError"] = "RequestError";
})(QiniuErrorName || (exports.QiniuErrorName = QiniuErrorName = {}));

var QiniuError =
/** @class */
function () {
  function QiniuError(name, message) {
    this.name = name;
    this.message = message;
    this.stack = new Error().stack;
  }

  return QiniuError;
}();

exports.QiniuError = QiniuError;

var QiniuRequestError =
/** @class */
function (_super) {
  __extends(QiniuRequestError, _super);

  function QiniuRequestError(code, reqId, message, data) {
    var _this = _super.call(this, QiniuErrorName.RequestError, message) || this;

    _this.code = code;
    _this.reqId = reqId;
    /**
     * @description 标记当前的 error 类型是一个 QiniuRequestError
     * @deprecated 下一个大版本将会移除，不推荐使用，推荐直接使用 instanceof 进行判断
     */

    _this.isRequestError = true;
    _this.data = data;
    return _this;
  }

  return QiniuRequestError;
}(QiniuError);

exports.QiniuRequestError = QiniuRequestError;

/**
 * @description 由于跨域、证书错误、断网、host 解析失败、系统拦截等原因导致的错误
 */
var QiniuNetworkError =
/** @class */
function (_super) {
  __extends(QiniuNetworkError, _super);

  function QiniuNetworkError(message, reqId) {
    if (reqId === void 0) {
      reqId = '';
    }

    return _super.call(this, 0, reqId, message) || this;
  }

  return QiniuNetworkError;
}(QiniuRequestError);

exports.QiniuNetworkError = QiniuNetworkError;