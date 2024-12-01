"use strict";

var _interopRequireDefault = require("@babel/runtime-corejs2/helpers/interopRequireDefault");

var _Object$defineProperty = require("@babel/runtime-corejs2/core-js/object/define-property");

_Object$defineProperty(exports, "__esModule", {
  value: true
});

_Object$defineProperty(exports, "QiniuErrorName", {
  enumerable: true,
  get: function get() {
    return _errors.QiniuErrorName;
  }
});

_Object$defineProperty(exports, "QiniuError", {
  enumerable: true,
  get: function get() {
    return _errors.QiniuError;
  }
});

_Object$defineProperty(exports, "QiniuRequestError", {
  enumerable: true,
  get: function get() {
    return _errors.QiniuRequestError;
  }
});

_Object$defineProperty(exports, "QiniuNetworkError", {
  enumerable: true,
  get: function get() {
    return _errors.QiniuNetworkError;
  }
});

_Object$defineProperty(exports, "imageMogr2", {
  enumerable: true,
  get: function get() {
    return _image.imageMogr2;
  }
});

_Object$defineProperty(exports, "watermark", {
  enumerable: true,
  get: function get() {
    return _image.watermark;
  }
});

_Object$defineProperty(exports, "imageInfo", {
  enumerable: true,
  get: function get() {
    return _image.imageInfo;
  }
});

_Object$defineProperty(exports, "exif", {
  enumerable: true,
  get: function get() {
    return _image.exif;
  }
});

_Object$defineProperty(exports, "pipeline", {
  enumerable: true,
  get: function get() {
    return _image.pipeline;
  }
});

_Object$defineProperty(exports, "deleteUploadedChunks", {
  enumerable: true,
  get: function get() {
    return _api.deleteUploadedChunks;
  }
});

_Object$defineProperty(exports, "getUploadUrl", {
  enumerable: true,
  get: function get() {
    return _api.getUploadUrl;
  }
});

_Object$defineProperty(exports, "upload", {
  enumerable: true,
  get: function get() {
    return _upload["default"];
  }
});

_Object$defineProperty(exports, "region", {
  enumerable: true,
  get: function get() {
    return _config.region;
  }
});

_Object$defineProperty(exports, "compressImage", {
  enumerable: true,
  get: function get() {
    return _utils.compressImage;
  }
});

_Object$defineProperty(exports, "urlSafeBase64Encode", {
  enumerable: true,
  get: function get() {
    return _utils.urlSafeBase64Encode;
  }
});

_Object$defineProperty(exports, "urlSafeBase64Decode", {
  enumerable: true,
  get: function get() {
    return _utils.urlSafeBase64Decode;
  }
});

_Object$defineProperty(exports, "getHeadersForMkFile", {
  enumerable: true,
  get: function get() {
    return _utils.getHeadersForMkFile;
  }
});

_Object$defineProperty(exports, "getHeadersForChunkUpload", {
  enumerable: true,
  get: function get() {
    return _utils.getHeadersForChunkUpload;
  }
});

var _errors = require("./errors");

var _image = require("./image");

var _api = require("./api");

var _upload = _interopRequireDefault(require("./upload"));

var _config = require("./config");

var _utils = require("./utils");