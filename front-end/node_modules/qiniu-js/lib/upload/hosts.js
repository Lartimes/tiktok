"use strict";

var _interopRequireDefault = require("@babel/runtime-corejs2/helpers/interopRequireDefault");

var _Object$defineProperty = require("@babel/runtime-corejs2/core-js/object/define-property");

_Object$defineProperty(exports, "__esModule", {
  value: true
});

exports.HostPool = exports.Host = void 0;

var _map = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/map"));

var _iterator = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/symbol/iterator"));

var _symbol = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/symbol"));

var _promise = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/promise"));

var _api = require("../api");

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

/**
  * @description 解冻时间，key 是 host，value 为解冻时间
  */
var unfreezeTimeMap = new _map["default"]();

var Host =
/** @class */
function () {
  function Host(host, protocol) {
    this.host = host;
    this.protocol = protocol;
  }
  /**
   * @description 当前 host 是否为冻结状态
   */


  Host.prototype.isFrozen = function () {
    var currentTime = new Date().getTime();
    var unfreezeTime = unfreezeTimeMap.get(this.host);
    return unfreezeTime != null && unfreezeTime >= currentTime;
  };
  /**
   * @param  {number} time 单位秒，默认 20s
   * @description 冻结该 host 对象，该 host 将在指定时间内不可用
   */


  Host.prototype.freeze = function (time) {
    if (time === void 0) {
      time = 20;
    }

    var unfreezeTime = new Date().getTime() + time * 1000;
    unfreezeTimeMap.set(this.host, unfreezeTime);
  };
  /**
   * @description 解冻该 host
   */


  Host.prototype.unfreeze = function () {
    unfreezeTimeMap["delete"](this.host);
  };
  /**
   * @description 获取当前 host 的完整 url
   */


  Host.prototype.getUrl = function () {
    return this.protocol + "://" + this.host;
  };
  /**
   * @description 获取解冻时间
   */


  Host.prototype.getUnfreezeTime = function () {
    return unfreezeTimeMap.get(this.host);
  };

  return Host;
}();

exports.Host = Host;

var HostPool =
/** @class */
function () {
  /**
   * @param  {string[]} initHosts
   * @description 如果在构造时传入 initHosts，则该 host 池始终使用传入的 initHosts 做为可用的数据
   */
  function HostPool(initHosts) {
    if (initHosts === void 0) {
      initHosts = [];
    }

    this.initHosts = initHosts;
    /**
     * @description 缓存的 host 表，以 bucket 和 accessKey 作为 key
     */

    this.cachedHostsMap = new _map["default"]();
  }
  /**
   * @param  {string} accessKey
   * @param  {string} bucketName
   * @param  {string[]} hosts
   * @param  {InternalConfig['upprotocol']} protocol
   * @returns  {void}
   * @description 注册可用 host
   */


  HostPool.prototype.register = function (accessKey, bucketName, hosts, protocol) {
    this.cachedHostsMap.set(accessKey + "@" + bucketName, hosts.map(function (host) {
      return new Host(host, protocol);
    }));
  };
  /**
   * @param  {string} accessKey
   * @param  {string} bucketName
   * @param  {InternalConfig['upprotocol']} protocol
   * @returns  {Promise<void>}
   * @description 刷新最新的 host 数据，如果用户在构造时该类时传入了 host 或者已经存在缓存则不会发起请求
   */


  HostPool.prototype.refresh = function (accessKey, bucketName, protocol) {
    var _a, _b, _c, _d;

    return __awaiter(this, void 0, void 0, function () {
      var cachedHostList, response, stashHosts;
      return __generator(this, function (_e) {
        switch (_e.label) {
          case 0:
            cachedHostList = this.cachedHostsMap.get(accessKey + "@" + bucketName) || [];
            if (cachedHostList.length > 0) return [2
            /*return*/
            ];

            if (this.initHosts.length > 0) {
              this.register(accessKey, bucketName, this.initHosts, protocol);
              return [2
              /*return*/
              ];
            }

            return [4
            /*yield*/
            , (0, _api.getUpHosts)(accessKey, bucketName, protocol)];

          case 1:
            response = _e.sent();

            if ((response === null || response === void 0 ? void 0 : response.data) != null) {
              stashHosts = __spread(((_b = (_a = response.data.up) === null || _a === void 0 ? void 0 : _a.acc) === null || _b === void 0 ? void 0 : _b.main) || [], ((_d = (_c = response.data.up) === null || _c === void 0 ? void 0 : _c.acc) === null || _d === void 0 ? void 0 : _d.backup) || []);
              this.register(accessKey, bucketName, stashHosts, protocol);
            }

            return [2
            /*return*/
            ];
        }
      });
    });
  };
  /**
   * @param  {string} accessKey
   * @param  {string} bucketName
   * @param  {InternalConfig['upprotocol']} protocol
   * @returns  {Promise<Host | null>}
   * @description 获取一个可用的上传 Host，排除已冻结的
   */


  HostPool.prototype.getUp = function (accessKey, bucketName, protocol) {
    return __awaiter(this, void 0, void 0, function () {
      var cachedHostList, availableHostList, priorityQueue;
      return __generator(this, function (_a) {
        switch (_a.label) {
          case 0:
            return [4
            /*yield*/
            , this.refresh(accessKey, bucketName, protocol)];

          case 1:
            _a.sent();

            cachedHostList = this.cachedHostsMap.get(accessKey + "@" + bucketName) || [];
            if (cachedHostList.length === 0) return [2
            /*return*/
            , null];
            availableHostList = cachedHostList.filter(function (host) {
              return !host.isFrozen();
            });
            if (availableHostList.length > 0) return [2
            /*return*/
            , availableHostList[0] // 无可用的，去取离解冻最近的 host
            ];
            priorityQueue = cachedHostList.slice().sort(function (hostA, hostB) {
              return (hostA.getUnfreezeTime() || 0) - (hostB.getUnfreezeTime() || 0);
            });
            return [2
            /*return*/
            , priorityQueue[0]];
        }
      });
    });
  };

  return HostPool;
}();

exports.HostPool = HostPool;