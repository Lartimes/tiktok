"use strict";

var _interopRequireWildcard = require("@babel/runtime-corejs2/helpers/interopRequireWildcard");

var _interopRequireDefault = require("@babel/runtime-corejs2/helpers/interopRequireDefault");

var _Object$defineProperty = require("@babel/runtime-corejs2/core-js/object/define-property");

_Object$defineProperty(exports, "__esModule", {
  value: true
});

exports["default"] = void 0;

var _stringify = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/json/stringify"));

var _iterator = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/symbol/iterator"));

var _symbol = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/symbol"));

var _promise = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/promise"));

var _assign = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/object/assign"));

var _create = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/object/create"));

var _setPrototypeOf = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/object/set-prototype-of"));

var _api = require("../api");

var _errors = require("../errors");

var utils = _interopRequireWildcard(require("../utils"));

var _base = _interopRequireDefault(require("./base"));

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

/** 是否为正整数 */
function isPositiveInteger(n) {
  var re = /^[1-9]\d*$/;
  return re.test(String(n));
}

var Resume =
/** @class */
function (_super) {
  __extends(Resume, _super);

  function Resume() {
    return _super !== null && _super.apply(this, arguments) || this;
  }
  /**
   * @returns  {Promise<ResponseSuccess<any>>}
   * @description 实现了 Base 的 run 接口，处理具体的分片上传事务，并抛出过程中的异常。
   */


  Resume.prototype.run = function () {
    return __awaiter(this, void 0, void 0, function () {
      var pool, mkFileResponse, localKey, uploadChunks, error_1;

      var _this = this;

      return __generator(this, function (_a) {
        switch (_a.label) {
          case 0:
            this.logger.info('start run Resume.');

            if (!this.config.chunkSize || !isPositiveInteger(this.config.chunkSize)) {
              throw new _errors.QiniuError(_errors.QiniuErrorName.InvalidChunkSize, 'chunkSize must be a positive integer');
            }

            if (this.config.chunkSize > 1024) {
              throw new _errors.QiniuError(_errors.QiniuErrorName.InvalidChunkSize, 'chunkSize maximum value is 1024');
            }

            return [4
            /*yield*/
            , this.initBeforeUploadChunks()];

          case 1:
            _a.sent();

            pool = new utils.Pool(function (chunkInfo) {
              return __awaiter(_this, void 0, void 0, function () {
                return __generator(this, function (_a) {
                  switch (_a.label) {
                    case 0:
                      if (this.aborted) {
                        pool.abort();
                        throw new Error('pool is aborted');
                      }

                      return [4
                      /*yield*/
                      , this.uploadChunk(chunkInfo)];

                    case 1:
                      _a.sent();

                      return [2
                      /*return*/
                      ];
                  }
                });
              });
            }, this.config.concurrentRequestLimit);
            mkFileResponse = null;
            localKey = this.getLocalKey();
            uploadChunks = this.chunks.map(function (chunk, index) {
              return pool.enqueue({
                chunk: chunk,
                index: index
              });
            });
            _a.label = 2;

          case 2:
            _a.trys.push([2, 5,, 6]);

            return [4
            /*yield*/
            , _promise["default"].all(uploadChunks)];

          case 3:
            _a.sent();

            return [4
            /*yield*/
            , this.mkFileReq()];

          case 4:
            mkFileResponse = _a.sent();
            return [3
            /*break*/
            , 6];

          case 5:
            error_1 = _a.sent(); // uploadId 无效，上传参数有误（多由于本地存储信息的 uploadId 失效）

            if (error_1 instanceof _errors.QiniuRequestError && (error_1.code === 612 || error_1.code === 400)) {
              utils.removeLocalFileInfo(localKey, this.logger);
            }

            throw error_1;

          case 6:
            // 上传成功，清理本地缓存数据
            utils.removeLocalFileInfo(localKey, this.logger);
            return [2
            /*return*/
            , mkFileResponse];
        }
      });
    });
  };

  Resume.prototype.uploadChunk = function (chunkInfo) {
    return __awaiter(this, void 0, void 0, function () {
      var index, chunk, cachedInfo, shouldCheckMD5, reuseSaved, md5, onProgress, requestOptions, response;

      var _this = this;

      return __generator(this, function (_a) {
        switch (_a.label) {
          case 0:
            index = chunkInfo.index, chunk = chunkInfo.chunk;
            cachedInfo = this.cachedUploadedList[index];
            this.logger.info("upload part " + index + ", cache:", cachedInfo);
            shouldCheckMD5 = this.config.checkByMD5;

            reuseSaved = function reuseSaved() {
              _this.usedCacheList[index] = true;

              _this.updateChunkProgress(chunk.size, index);

              _this.uploadedList[index] = cachedInfo;

              _this.updateLocalCache();
            }; // FIXME: 至少判断一下 size


            if (cachedInfo && !shouldCheckMD5) {
              reuseSaved();
              return [2
              /*return*/
              ];
            }

            return [4
            /*yield*/
            , utils.computeMd5(chunk)];

          case 1:
            md5 = _a.sent();
            this.logger.info('computed part md5.', md5);

            if (cachedInfo && md5 === cachedInfo.md5) {
              reuseSaved();
              return [2
              /*return*/
              ];
            } // 没有使用缓存设置标记为 false


            this.usedCacheList[index] = false;

            onProgress = function onProgress(data) {
              _this.updateChunkProgress(data.loaded, index);
            };

            requestOptions = {
              body: chunk,
              md5: this.config.checkByServer ? md5 : undefined,
              onProgress: onProgress,
              onCreate: function onCreate(xhr) {
                return _this.addXhr(xhr);
              }
            };
            this.logger.info("part " + index + " start uploading.");
            return [4
            /*yield*/
            , (0, _api.uploadChunk)(this.token, this.key, chunkInfo.index + 1, this.getUploadInfo(), requestOptions)];

          case 2:
            response = _a.sent();
            this.logger.info("part " + index + " upload completed."); // 在某些浏览器环境下，xhr 的 progress 事件无法被触发，progress 为 null，这里在每次分片上传完成后都手动更新下 progress

            onProgress({
              loaded: chunk.size,
              total: chunk.size
            });
            this.uploadedList[index] = {
              etag: response.data.etag,
              md5: response.data.md5,
              size: chunk.size
            };
            this.updateLocalCache();
            return [2
            /*return*/
            ];
        }
      });
    });
  };

  Resume.prototype.mkFileReq = function () {
    return __awaiter(this, void 0, void 0, function () {
      var data, result;

      var _this = this;

      return __generator(this, function (_a) {
        switch (_a.label) {
          case 0:
            data = __assign(__assign(__assign({
              parts: this.uploadedList.map(function (value, index) {
                return {
                  etag: value.etag,
                  // 接口要求 index 需要从 1 开始，所以需要整体 + 1
                  partNumber: index + 1
                };
              }),
              fname: this.putExtra.fname
            }, this.putExtra.mimeType && {
              mimeType: this.putExtra.mimeType
            }), this.putExtra.customVars && {
              customVars: this.putExtra.customVars
            }), this.putExtra.metadata && {
              metadata: this.putExtra.metadata
            });
            this.logger.info('parts upload completed, make file.', data);
            return [4
            /*yield*/
            , (0, _api.uploadComplete)(this.token, this.key, this.getUploadInfo(), {
              onCreate: function onCreate(xhr) {
                return _this.addXhr(xhr);
              },
              body: (0, _stringify["default"])(data)
            })];

          case 1:
            result = _a.sent();
            this.logger.info('finish Resume Progress.');
            this.updateMkFileProgress(1);
            return [2
            /*return*/
            , result];
        }
      });
    });
  };

  Resume.prototype.initBeforeUploadChunks = function () {
    return __awaiter(this, void 0, void 0, function () {
      var cachedInfo, res, infoMessage;
      return __generator(this, function (_a) {
        switch (_a.label) {
          case 0:
            this.uploadedList = [];
            this.usedCacheList = [];
            cachedInfo = utils.getLocalFileInfo(this.getLocalKey(), this.logger);
            if (!!cachedInfo) return [3
            /*break*/
            , 2];
            this.logger.info('init upload parts from api.');
            return [4
            /*yield*/
            , (0, _api.initUploadParts)(this.token, this.bucketName, this.key, this.uploadHost.getUrl())];

          case 1:
            res = _a.sent();
            this.logger.info("initd upload parts of id: " + res.data.uploadId + ".");
            this.uploadId = res.data.uploadId;
            this.cachedUploadedList = [];
            return [3
            /*break*/
            , 3];

          case 2:
            infoMessage = ['resume upload parts from local cache,', "total " + cachedInfo.data.length + " part,", "id is " + cachedInfo.id + "."];
            this.logger.info(infoMessage.join(' '));
            this.cachedUploadedList = cachedInfo.data;
            this.uploadId = cachedInfo.id;
            _a.label = 3;

          case 3:
            this.chunks = utils.getChunks(this.file, this.config.chunkSize);
            this.loaded = {
              mkFileProgress: 0,
              chunks: this.chunks.map(function (_) {
                return 0;
              })
            };
            this.notifyResumeProgress();
            return [2
            /*return*/
            ];
        }
      });
    });
  };

  Resume.prototype.getUploadInfo = function () {
    return {
      id: this.uploadId,
      url: this.uploadHost.getUrl()
    };
  };

  Resume.prototype.getLocalKey = function () {
    return utils.createLocalKey(this.file.name, this.key, this.file.size);
  };

  Resume.prototype.updateLocalCache = function () {
    utils.setLocalFileInfo(this.getLocalKey(), {
      id: this.uploadId,
      data: this.uploadedList
    }, this.logger);
  };

  Resume.prototype.updateChunkProgress = function (loaded, index) {
    this.loaded.chunks[index] = loaded;
    this.notifyResumeProgress();
  };

  Resume.prototype.updateMkFileProgress = function (progress) {
    this.loaded.mkFileProgress = progress;
    this.notifyResumeProgress();
  };

  Resume.prototype.notifyResumeProgress = function () {
    var _this = this;

    this.progress = {
      total: this.getProgressInfoItem(utils.sum(this.loaded.chunks) + this.loaded.mkFileProgress, // FIXME: 不准确的 fileSize
      this.file.size + 1 // 防止在 complete 未调用的时候进度显示 100%
      ),
      chunks: this.chunks.map(function (chunk, index) {
        var fromCache = _this.usedCacheList[index];
        return _this.getProgressInfoItem(_this.loaded.chunks[index], chunk.size, fromCache);
      }),
      uploadInfo: {
        id: this.uploadId,
        url: this.uploadHost.getUrl()
      }
    };
    this.onData(this.progress);
  };

  return Resume;
}(_base["default"]);

var _default = Resume;
exports["default"] = _default;