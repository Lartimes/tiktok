"use strict";

var _interopRequireDefault = require("@babel/runtime-corejs2/helpers/interopRequireDefault");

var _Object$defineProperty = require("@babel/runtime-corejs2/core-js/object/define-property");

_Object$defineProperty(exports, "__esModule", {
  value: true
});

exports.CRC32 = void 0;

var _iterator = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/symbol/iterator"));

var _symbol = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/symbol"));

var _promise = _interopRequireDefault(require("@babel/runtime-corejs2/core-js/promise"));

var _helper = require("./helper");

/* eslint-disable no-bitwise */
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

/**
 * 以下 class 实现参考
 * https://github.com/Stuk/jszip/blob/d4702a70834bd953d4c2d0bc155fad795076631a/lib/crc32.js
 * 该实现主要针对大文件优化、对计算的值进行了 `>>> 0` 运算（为与服务端保持一致）
 */
var CRC32 =
/** @class */
function () {
  function CRC32() {
    this.crc = -1;
    this.table = this.makeTable();
  }

  CRC32.prototype.makeTable = function () {
    var table = new Array();

    for (var i = 0; i < 256; i++) {
      var t = i;

      for (var j = 0; j < 8; j++) {
        if (t & 1) {
          // IEEE 标准
          t = t >>> 1 ^ 0xEDB88320;
        } else {
          t >>>= 1;
        }
      }

      table[i] = t;
    }

    return table;
  };

  CRC32.prototype.append = function (data) {
    var crc = this.crc;

    for (var offset = 0; offset < data.byteLength; offset++) {
      crc = crc >>> 8 ^ this.table[(crc ^ data[offset]) & 0xFF];
    }

    this.crc = crc;
  };

  CRC32.prototype.compute = function () {
    return (this.crc ^ -1) >>> 0;
  };

  CRC32.prototype.readAsUint8Array = function (file) {
    return __awaiter(this, void 0, void 0, function () {
      var _a;

      return __generator(this, function (_b) {
        switch (_b.label) {
          case 0:
            if (!(typeof file.arrayBuffer === 'function')) return [3
            /*break*/
            , 2];
            _a = Uint8Array.bind;
            return [4
            /*yield*/
            , file.arrayBuffer()];

          case 1:
            return [2
            /*return*/
            , new (_a.apply(Uint8Array, [void 0, _b.sent()]))()];

          case 2:
            return [2
            /*return*/
            , new _promise["default"](function (resolve, reject) {
              var reader = new FileReader();

              reader.onload = function () {
                if (reader.result == null) {
                  reject();
                  return;
                }

                if (typeof reader.result === 'string') {
                  reject();
                  return;
                }

                resolve(new Uint8Array(reader.result));
              };

              reader.readAsArrayBuffer(file);
            })];
        }
      });
    });
  };

  CRC32.prototype.file = function (file) {
    return __awaiter(this, void 0, void 0, function () {
      var _a, count, index, start, end, chuck;

      return __generator(this, function (_b) {
        switch (_b.label) {
          case 0:
            if (!(file.size <= _helper.MB)) return [3
            /*break*/
            , 2];
            _a = this.append;
            return [4
            /*yield*/
            , this.readAsUint8Array(file)];

          case 1:
            _a.apply(this, [_b.sent()]);

            return [2
            /*return*/
            , this.compute()];

          case 2:
            count = Math.ceil(file.size / _helper.MB);
            index = 0;
            _b.label = 3;

          case 3:
            if (!(index < count)) return [3
            /*break*/
            , 6];
            start = index * _helper.MB;
            end = index === count - 1 ? file.size : start + _helper.MB;
            return [4
            /*yield*/
            , this.readAsUint8Array(file.slice(start, end))];

          case 4:
            chuck = _b.sent();
            this.append(new Uint8Array(chuck));
            _b.label = 5;

          case 5:
            index++;
            return [3
            /*break*/
            , 3];

          case 6:
            return [2
            /*return*/
            , this.compute()];
        }
      });
    });
  };

  CRC32.file = function (file) {
    var crc = new CRC32();
    return crc.file(file);
  };

  return CRC32;
}();

exports.CRC32 = CRC32;