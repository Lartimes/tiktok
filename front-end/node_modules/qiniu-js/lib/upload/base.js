"use strict";

var _interopRequireWildcard = require("@babel/runtime-corejs2/helpers/interopRequireWildcard");

var _interopRequireDefault = require("@babel/runtime-corejs2/helpers/interopRequireDefault");

var _Object$defineProperty = require("@babel/runtime-corejs2/core-js/object/define-property");

_Object$defineProperty(exports, "__esModule", {
  value: true
});

exports["default"] = exports.RETRY_CODE_LIST = exports.FREEZE_CODE_LIST = exports.DEFAULT_CHUNK_SIZE = void 0;

var _iterator = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/symbol/iterator"));

var _symbol = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/symbol"));

var _promise = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/promise"));

var _assign = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/object/assign"));

var _errors = require("../errors");

var utils = _interopRequireWildcard(require("../utils"));

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

var DEFAULT_CHUNK_SIZE = 4; // 单位 MB
// code 信息地址 https://developer.qiniu.com/kodo/3928/error-responses

exports.DEFAULT_CHUNK_SIZE = DEFAULT_CHUNK_SIZE;
var FREEZE_CODE_LIST = [0, 502, 503, 504, 599]; // 将会冻结当前 host 的 code

exports.FREEZE_CODE_LIST = FREEZE_CODE_LIST;

var RETRY_CODE_LIST = __spread(FREEZE_CODE_LIST, [612]); // 会进行重试的 code


exports.RETRY_CODE_LIST = RETRY_CODE_LIST;
var GB = Math.pow(1024, 3);

var Base =
/** @class */
function () {
  function Base(options, handlers, hostPool, logger) {
    this.hostPool = hostPool;
    this.logger = logger;
    this.aborted = false;
    this.retryCount = 0;
    this.xhrList = [];
    this.config = options.config;
    logger.info('config inited.', this.config);
    this.putExtra = __assign({
      fname: ''
    }, options.putExtra);
    logger.info('putExtra inited.', this.putExtra);
    this.key = options.key;
    this.file = options.file;
    this.token = options.token;
    this.onData = handlers.onData;
    this.onError = handlers.onError;
    this.onComplete = handlers.onComplete;

    try {
      var putPolicy = utils.getPutPolicy(this.token);
      this.bucketName = putPolicy.bucketName;
      this.assessKey = putPolicy.assessKey;
    } catch (error) {
      logger.error('get putPolicy from token failed.', error);
      this.onError(error);
    }
  } // 检查并更新 upload host


  Base.prototype.checkAndUpdateUploadHost = function () {
    return __awaiter(this, void 0, void 0, function () {
      var newHost;
      return __generator(this, function (_a) {
        switch (_a.label) {
          case 0:
            // 从 hostPool 中获取一个可用的 host 挂载在 this
            this.logger.info('get available upload host.');
            return [4
            /*yield*/
            , this.hostPool.getUp(this.assessKey, this.bucketName, this.config.upprotocol)];

          case 1:
            newHost = _a.sent();

            if (newHost == null) {
              throw new _errors.QiniuError(_errors.QiniuErrorName.NotAvailableUploadHost, 'no available upload host.');
            }

            if (this.uploadHost != null && this.uploadHost.host !== newHost.host) {
              this.logger.warn("host switches from " + this.uploadHost.host + " to " + newHost.host + ".");
            } else {
              this.logger.info("use host " + newHost.host + ".");
            }

            this.uploadHost = newHost;
            return [2
            /*return*/
            ];
        }
      });
    });
  }; // 检查并解冻当前的 host


  Base.prototype.checkAndUnfreezeHost = function () {
    this.logger.info('check unfreeze host.');

    if (this.uploadHost != null && this.uploadHost.isFrozen()) {
      this.logger.warn(this.uploadHost.host + " will be unfrozen.");
      this.uploadHost.unfreeze();
    }
  }; // 检查并更新冻结当前的 host


  Base.prototype.checkAndFreezeHost = function (error) {
    this.logger.info('check freeze host.');

    if (error instanceof _errors.QiniuRequestError && this.uploadHost != null) {
      if (FREEZE_CODE_LIST.includes(error.code)) {
        this.logger.warn(this.uploadHost.host + " will be temporarily frozen.");
        this.uploadHost.freeze();
      }
    }
  };

  Base.prototype.handleError = function (error) {
    this.logger.error(error.message);
    this.onError(error);
  };
  /**
   * @returns Promise 返回结果与上传最终状态无关，状态信息请通过 [Subscriber] 获取。
   * @description 上传文件，状态信息请通过 [Subscriber] 获取。
   */


  Base.prototype.putFile = function () {
    return __awaiter(this, void 0, void 0, function () {
      var result, err_1, notReachRetryCount, needRetry;
      return __generator(this, function (_a) {
        switch (_a.label) {
          case 0:
            this.aborted = false;

            if (!this.putExtra.fname) {
              this.logger.info('use file.name as fname.');
              this.putExtra.fname = this.file.name;
            }

            if (this.file.size > 10000 * GB) {
              this.handleError(new _errors.QiniuError(_errors.QiniuErrorName.InvalidFile, 'file size exceed maximum value 10000G'));
              return [2
              /*return*/
              ];
            }

            if (this.putExtra.customVars) {
              if (!utils.isCustomVarsValid(this.putExtra.customVars)) {
                this.handleError(new _errors.QiniuError(_errors.QiniuErrorName.InvalidCustomVars, // FIXME: width => with
                'customVars key should start width x:'));
                return [2
                /*return*/
                ];
              }
            }

            if (this.putExtra.metadata) {
              if (!utils.isMetaDataValid(this.putExtra.metadata)) {
                this.handleError(new _errors.QiniuError(_errors.QiniuErrorName.InvalidMetadata, 'metadata key should start with x-qn-meta-'));
                return [2
                /*return*/
                ];
              }
            }

            _a.label = 1;

          case 1:
            _a.trys.push([1, 4,, 5]);

            this.uploadAt = new Date().getTime();
            return [4
            /*yield*/
            , this.checkAndUpdateUploadHost()];

          case 2:
            _a.sent();

            return [4
            /*yield*/
            , this.run()];

          case 3:
            result = _a.sent();
            this.onComplete(result.data);
            this.checkAndUnfreezeHost();
            this.sendLog(result.reqId, 200);
            return [2
            /*return*/
            ];

          case 4:
            err_1 = _a.sent();

            if (this.aborted) {
              this.logger.warn('upload is aborted.');
              this.sendLog('', -2);
              return [2
              /*return*/
              ];
            }

            this.clear();
            this.logger.error(err_1);

            if (err_1 instanceof _errors.QiniuRequestError) {
              this.sendLog(err_1.reqId, err_1.code); // 检查并冻结当前的 host

              this.checkAndFreezeHost(err_1);
              notReachRetryCount = ++this.retryCount <= this.config.retryCount;
              needRetry = RETRY_CODE_LIST.includes(err_1.code); // 以下条件满足其中之一则会进行重新上传：
              // 1. 满足 needRetry 的条件且 retryCount 不为 0
              // 2. uploadId 无效时在 resume 里会清除本地数据，并且这里触发重新上传

              if (needRetry && notReachRetryCount) {
                this.logger.warn("error auto retry: " + this.retryCount + "/" + this.config.retryCount + ".");
                this.putFile();
                return [2
                /*return*/
                ];
              }
            }

            this.onError(err_1);
            return [3
            /*break*/
            , 5];

          case 5:
            return [2
            /*return*/
            ];
        }
      });
    });
  };

  Base.prototype.clear = function () {
    this.xhrList.forEach(function (xhr) {
      xhr.onreadystatechange = null;
      xhr.abort();
    });
    this.xhrList = [];
    this.logger.info('cleanup uploading xhr.');
  };

  Base.prototype.stop = function () {
    this.logger.info('aborted.');
    this.clear();
    this.aborted = true;
  };

  Base.prototype.addXhr = function (xhr) {
    this.xhrList.push(xhr);
  };

  Base.prototype.sendLog = function (reqId, code) {
    var _a, _b;

    this.logger.report({
      code: code,
      reqId: reqId,
      remoteIp: '',
      upType: 'jssdk-h5',
      size: this.file.size,
      time: Math.floor(this.uploadAt / 1000),
      port: utils.getPortFromUrl((_a = this.uploadHost) === null || _a === void 0 ? void 0 : _a.getUrl()),
      host: utils.getDomainFromUrl((_b = this.uploadHost) === null || _b === void 0 ? void 0 : _b.getUrl()),
      bytesSent: this.progress ? this.progress.total.loaded : 0,
      duration: Math.floor((new Date().getTime() - this.uploadAt) / 1000)
    });
  };

  Base.prototype.getProgressInfoItem = function (loaded, size, fromCache) {
    return __assign({
      size: size,
      loaded: loaded,
      percent: loaded / size * 100
    }, fromCache == null ? {} : {
      fromCache: fromCache
    });
  };

  return Base;
}();

var _default = Base;
exports["default"] = _default;