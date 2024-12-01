"use strict";

var _interopRequireDefault = require("@babel/runtime-corejs2/helpers/interopRequireDefault");

var _Object$defineProperty = require("@babel/runtime-corejs2/core-js/object/define-property");

_Object$defineProperty(exports, "__esModule", {
  value: true
});

exports.getChunks = getChunks;
exports.isMetaDataValid = isMetaDataValid;
exports.isCustomVarsValid = isCustomVarsValid;
exports.sum = sum;
exports.setLocalFileInfo = setLocalFileInfo;
exports.createLocalKey = createLocalKey;
exports.removeLocalFileInfo = removeLocalFileInfo;
exports.getLocalFileInfo = getLocalFileInfo;
exports.getAuthHeaders = getAuthHeaders;
exports.getHeadersForChunkUpload = getHeadersForChunkUpload;
exports.getHeadersForMkFile = getHeadersForMkFile;
exports.createXHR = createXHR;
exports.computeMd5 = computeMd5;
exports.readAsArrayBuffer = readAsArrayBuffer;
exports.request = request;
exports.getPortFromUrl = getPortFromUrl;
exports.getDomainFromUrl = getDomainFromUrl;
exports.getPutPolicy = getPutPolicy;
exports.createObjectURL = createObjectURL;
exports.MB = void 0;

var _stringify = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/json/stringify"));

var _keys = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/object/keys"));

var _iterator = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/symbol/iterator"));

var _symbol = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/symbol"));

var _promise = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/promise"));

var _assign = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/object/assign"));

var _sparkMd = _interopRequireDefault(require("spark-md5"));

var _errors = require("../errors");

var _base = require("./base64");

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

var __awaiter = void 0 && (void 0).__awaiter || function (thisArg, _arguments, P, generator) {
  function adopt(value) {
    return value instanceof P ? value : new P(function (resolve) {
      resolve(value);
    });
  }

  return new (P || (P = _promise["default"]))(function (resolve, reject) {
    function fulfilled(value) {
      try {
        step(generator.next(value));
      } catch (e) {
        reject(e);
      }
    }

    function rejected(value) {
      try {
        step(generator["throw"](value));
      } catch (e) {
        reject(e);
      }
    }

    function step(result) {
      result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected);
    }

    step((generator = generator.apply(thisArg, _arguments || [])).next());
  });
};

var __generator = void 0 && (void 0).__generator || function (thisArg, body) {
  var _ = {
    label: 0,
    sent: function sent() {
      if (t[0] & 1) throw t[1];
      return t[1];
    },
    trys: [],
    ops: []
  },
      f,
      y,
      t,
      g;
  return g = {
    next: verb(0),
    "throw": verb(1),
    "return": verb(2)
  }, typeof _symbol["default"] === "function" && (g[_iterator["default"]] = function () {
    return this;
  }), g;

  function verb(n) {
    return function (v) {
      return step([n, v]);
    };
  }

  function step(op) {
    if (f) throw new TypeError("Generator is already executing.");

    while (_) {
      try {
        if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
        if (y = 0, t) op = [op[0] & 2, t.value];

        switch (op[0]) {
          case 0:
          case 1:
            t = op;
            break;

          case 4:
            _.label++;
            return {
              value: op[1],
              done: false
            };

          case 5:
            _.label++;
            y = op[1];
            op = [0];
            continue;

          case 7:
            op = _.ops.pop();

            _.trys.pop();

            continue;

          default:
            if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) {
              _ = 0;
              continue;
            }

            if (op[0] === 3 && (!t || op[1] > t[0] && op[1] < t[3])) {
              _.label = op[1];
              break;
            }

            if (op[0] === 6 && _.label < t[1]) {
              _.label = t[1];
              t = op;
              break;
            }

            if (t && _.label < t[2]) {
              _.label = t[2];

              _.ops.push(op);

              break;
            }

            if (t[2]) _.ops.pop();

            _.trys.pop();

            continue;
        }

        op = body.call(thisArg, _);
      } catch (e) {
        op = [6, e];
        y = 0;
      } finally {
        f = t = 0;
      }
    }

    if (op[0] & 5) throw op[1];
    return {
      value: op[0] ? op[1] : void 0,
      done: true
    };
  }
};

var MB = Math.pow(1024, 2); // 文件分块

exports.MB = MB;

function getChunks(file, blockSize) {
  var chunkByteSize = blockSize * MB; // 转换为字节
  // 如果 chunkByteSize 比文件大，则直接取文件的大小

  if (chunkByteSize > file.size) {
    chunkByteSize = file.size;
  } else {
    // 因为最多 10000 chunk，所以如果 chunkSize 不符合则把每片 chunk 大小扩大两倍
    while (file.size > chunkByteSize * 10000) {
      chunkByteSize *= 2;
    }
  }

  var chunks = [];
  var count = Math.ceil(file.size / chunkByteSize);

  for (var i = 0; i < count; i++) {
    var chunk = file.slice(chunkByteSize * i, i === count - 1 ? file.size : chunkByteSize * (i + 1));
    chunks.push(chunk);
  }

  return chunks;
}

function isMetaDataValid(params) {
  return (0, _keys["default"])(params).every(function (key) {
    return key.indexOf('x-qn-meta-') === 0;
  });
}

function isCustomVarsValid(params) {
  return (0, _keys["default"])(params).every(function (key) {
    return key.indexOf('x:') === 0;
  });
}

function sum(list) {
  return list.reduce(function (data, loaded) {
    return data + loaded;
  }, 0);
}

function setLocalFileInfo(localKey, info, logger) {
  try {
    localStorage.setItem(localKey, (0, _stringify["default"])(info));
  } catch (err) {
    logger.warn(new _errors.QiniuError(_errors.QiniuErrorName.WriteCacheFailed, "setLocalFileInfo failed: " + localKey));
  }
}

function createLocalKey(name, key, size) {
  var localKey = key == null ? '_' : "_key_" + key + "_";
  return "qiniu_js_sdk_upload_file_name_" + name + localKey + "size_" + size;
}

function removeLocalFileInfo(localKey, logger) {
  try {
    localStorage.removeItem(localKey);
  } catch (err) {
    logger.warn(new _errors.QiniuError(_errors.QiniuErrorName.RemoveCacheFailed, "removeLocalFileInfo failed. key: " + localKey));
  }
}

function getLocalFileInfo(localKey, logger) {
  var localInfoString = null;

  try {
    localInfoString = localStorage.getItem(localKey);
  } catch (_a) {
    logger.warn(new _errors.QiniuError(_errors.QiniuErrorName.ReadCacheFailed, "getLocalFileInfo failed. key: " + localKey));
  }

  if (localInfoString == null) {
    return null;
  }

  var localInfo = null;

  try {
    localInfo = JSON.parse(localInfoString);
  } catch (_b) {
    // 本地信息已被破坏，直接删除
    removeLocalFileInfo(localKey, logger);
    logger.warn(new _errors.QiniuError(_errors.QiniuErrorName.InvalidCacheData, "getLocalFileInfo failed to parse. key: " + localKey));
  }

  return localInfo;
}

function getAuthHeaders(token) {
  var auth = 'UpToken ' + token;
  return {
    Authorization: auth
  };
}

function getHeadersForChunkUpload(token) {
  var header = getAuthHeaders(token);
  return __assign({
    'content-type': 'application/octet-stream'
  }, header);
}

function getHeadersForMkFile(token) {
  var header = getAuthHeaders(token);
  return __assign({
    'content-type': 'application/json'
  }, header);
}

function createXHR() {
  if (window.XMLHttpRequest) {
    return new XMLHttpRequest();
  }

  if (window.ActiveXObject) {
    return new window.ActiveXObject('Microsoft.XMLHTTP');
  }

  throw new _errors.QiniuError(_errors.QiniuErrorName.NotAvailableXMLHttpRequest, 'the current environment does not support.');
}

function computeMd5(data) {
  return __awaiter(this, void 0, void 0, function () {
    var buffer, spark;
    return __generator(this, function (_a) {
      switch (_a.label) {
        case 0:
          return [4
          /*yield*/
          , readAsArrayBuffer(data)];

        case 1:
          buffer = _a.sent();
          spark = new _sparkMd["default"].ArrayBuffer();
          spark.append(buffer);
          return [2
          /*return*/
          , spark.end()];
      }
    });
  });
}

function readAsArrayBuffer(data) {
  return new _promise["default"](function (resolve, reject) {
    var reader = new FileReader(); // evt 类型目前存在问题 https://github.com/Microsoft/TypeScript/issues/4163

    reader.onload = function (evt) {
      if (evt.target) {
        var body = evt.target.result;
        resolve(body);
      } else {
        reject(new _errors.QiniuError(_errors.QiniuErrorName.InvalidProgressEventTarget, 'progress event target is undefined'));
      }
    };

    reader.onerror = function () {
      reject(new _errors.QiniuError(_errors.QiniuErrorName.FileReaderReadFailed, 'fileReader read failed'));
    };

    reader.readAsArrayBuffer(data);
  });
}

function request(url, options) {
  return new _promise["default"](function (resolve, reject) {
    var xhr = createXHR();
    xhr.open(options.method, url);

    if (options.onCreate) {
      options.onCreate(xhr);
    }

    if (options.headers) {
      var headers_1 = options.headers;
      (0, _keys["default"])(headers_1).forEach(function (k) {
        xhr.setRequestHeader(k, headers_1[k]);
      });
    }

    xhr.upload.addEventListener('progress', function (evt) {
      if (evt.lengthComputable && options.onProgress) {
        options.onProgress({
          loaded: evt.loaded,
          total: evt.total
        });
      }
    });

    xhr.onreadystatechange = function () {
      var responseText = xhr.responseText;

      if (xhr.readyState !== 4) {
        return;
      }

      var reqId = xhr.getResponseHeader('x-reqId') || '';

      if (xhr.status === 0) {
        // 发生 0 基本都是网络错误，常见的比如跨域、断网、host 解析失败、系统拦截等等
        reject(new _errors.QiniuNetworkError('network error.', reqId));
        return;
      }

      if (xhr.status !== 200) {
        var message = "xhr request failed, code: " + xhr.status;

        if (responseText) {
          message += " response: " + responseText;
        }

        var data = void 0;

        try {
          data = JSON.parse(responseText);
        } catch (_a) {// 无需处理该错误、可能拿到非 json 格式的响应是预期的
        }

        reject(new _errors.QiniuRequestError(xhr.status, reqId, message, data));
        return;
      }

      try {
        resolve({
          data: JSON.parse(responseText),
          reqId: reqId
        });
      } catch (err) {
        reject(err);
      }
    };

    xhr.send(options.body);
  });
}

function getPortFromUrl(url) {
  if (url && url.match) {
    var groups = url.match(/(^https?)/);

    if (!groups) {
      return '';
    }

    var type = groups[1];
    groups = url.match(/^https?:\/\/([^:^/]*):(\d*)/);

    if (groups) {
      return groups[2];
    }

    if (type === 'http') {
      return '80';
    }

    return '443';
  }

  return '';
}

function getDomainFromUrl(url) {
  if (url && url.match) {
    var groups = url.match(/^https?:\/\/([^:^/]*)/);
    return groups ? groups[1] : '';
  }

  return '';
}

function getPutPolicy(token) {
  if (!token) throw new _errors.QiniuError(_errors.QiniuErrorName.InvalidToken, 'invalid token.');
  var segments = token.split(':');
  if (segments.length === 1) throw new _errors.QiniuError(_errors.QiniuErrorName.InvalidToken, 'invalid token segments.'); // token 构造的差异参考：https://github.com/qbox/product/blob/master/kodo/auths/UpToken.md#admin-uptoken-authorization

  var assessKey = segments.length > 3 ? segments[1] : segments[0];
  if (!assessKey) throw new _errors.QiniuError(_errors.QiniuErrorName.InvalidToken, 'missing assess key field.');
  var putPolicy = null;

  try {
    putPolicy = JSON.parse((0, _base.urlSafeBase64Decode)(segments[segments.length - 1]));
  } catch (error) {
    throw new _errors.QiniuError(_errors.QiniuErrorName.InvalidToken, 'token parse failed.');
  }

  if (putPolicy == null) {
    throw new _errors.QiniuError(_errors.QiniuErrorName.InvalidToken, 'putPolicy is null.');
  }

  if (putPolicy.scope == null) {
    throw new _errors.QiniuError(_errors.QiniuErrorName.InvalidToken, 'scope field is null.');
  }

  var bucketName = putPolicy.scope.split(':')[0];

  if (!bucketName) {
    throw new _errors.QiniuError(_errors.QiniuErrorName.InvalidToken, 'resolve bucketName failed.');
  }

  return {
    assessKey: assessKey,
    bucketName: bucketName,
    scope: putPolicy.scope
  };
}

function createObjectURL(file) {
  var URL = window.URL || window.webkitURL || window.mozURL; // FIXME:  需要 revokeObjectURL

  return URL.createObjectURL(file);
}