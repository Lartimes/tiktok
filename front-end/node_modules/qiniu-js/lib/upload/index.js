"use strict";

var _interopRequireDefault = require("@babel/runtime-corejs2/helpers/interopRequireDefault");

var _interopRequireWildcard = require("@babel/runtime-corejs2/helpers/interopRequireWildcard");

var _Object$keys = require("@babel/runtime-corejs2/core-js/object/keys");

var _Object$defineProperty = require("@babel/runtime-corejs2/core-js/object/define-property");

_Object$defineProperty(exports, "__esModule", {
  value: true
});

var _exportNames = {
  createUploadManager: true
};
exports.createUploadManager = createUploadManager;
exports["default"] = upload;

var _resume = _interopRequireWildcard(require("./resume"));

_Object$keys(_resume).forEach(function (key) {
  if (key === "default" || key === "__esModule") return;
  if (Object.prototype.hasOwnProperty.call(_exportNames, key)) return;

  _Object$defineProperty(exports, key, {
    enumerable: true,
    get: function get() {
      return _resume[key];
    }
  });
});

var _direct = _interopRequireDefault(require("./direct"));

var _logger = _interopRequireDefault(require("../logger"));

var _utils = require("../utils");

var _hosts = require("./hosts");

var _base = require("./base");

_Object$keys(_base).forEach(function (key) {
  if (key === "default" || key === "__esModule") return;
  if (Object.prototype.hasOwnProperty.call(_exportNames, key)) return;

  _Object$defineProperty(exports, key, {
    enumerable: true,
    get: function get() {
      return _base[key];
    }
  });
});

function createUploadManager(options, handlers, hostPool, logger) {
  if (options.config && options.config.forceDirect) {
    logger.info('ues forceDirect mode.');
    return new _direct["default"](options, handlers, hostPool, logger);
  }

  if (options.file.size > 4 * _utils.MB) {
    logger.info('file size over 4M, use Resume.');
    return new _resume["default"](options, handlers, hostPool, logger);
  }

  logger.info('file size less or equal than 4M, use Direct.');
  return new _direct["default"](options, handlers, hostPool, logger);
}
/**
 * @param file 上传文件
 * @param key 目标文件名
 * @param token 上传凭证
 * @param putExtra 上传文件的相关资源信息配置
 * @param config 上传任务的配置
 * @returns 返回用于上传任务的可观察对象
 */


function upload(file, key, token, putExtra, config) {
  // 为每个任务创建单独的 Logger
  var logger = new _logger["default"](token, config === null || config === void 0 ? void 0 : config.disableStatisticsReport, config === null || config === void 0 ? void 0 : config.debugLogLevel, file.name);
  var options = {
    file: file,
    key: key,
    token: token,
    putExtra: putExtra,
    config: (0, _utils.normalizeUploadConfig)(config, logger)
  }; // 创建 host 池

  var hostPool = new _hosts.HostPool(options.config.uphost);
  return new _utils.Observable(function (observer) {
    var manager = createUploadManager(options, {
      onData: function onData(data) {
        return observer.next(data);
      },
      onError: function onError(err) {
        return observer.error(err);
      },
      onComplete: function onComplete(res) {
        return observer.complete(res);
      }
    }, hostPool, logger);
    manager.putFile();
    return manager.stop.bind(manager);
  });
}