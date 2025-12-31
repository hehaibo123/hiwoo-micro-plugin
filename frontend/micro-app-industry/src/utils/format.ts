/* eslint no-extend-native: ["error", { "exceptions": ["Date"] }] */

Date.prototype.format = function (fmt) {
  var o = {
    'M+': this.getMonth() + 1,
    'd+': this.getDate(),
    'h+': this.getHours(),
    'm+': this.getMinutes(),
    's+': this.getSeconds(),
    'q+': Math.floor((this.getMonth() + 3) / 3),
    'S': this.getMilliseconds()
  }
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substring(4 - RegExp.$1.length))
  }
  for (var k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      if (RegExp.$1.length === 1) {
        // @ts-ignore
        fmt = fmt.replace(RegExp.$1, o[k])
      } else {
        fmt = fmt.replace(RegExp.$1, ('00' + o[k]).substring(('' + o[k]).length))
      }
    }
  }
  return fmt
}
