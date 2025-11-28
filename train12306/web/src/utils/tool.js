/**
 * 工具类
 */
class Tool {
  /**
   * 判断是否为空
   * @param obj
   * @returns {boolean}
   */
  static isEmpty(obj) {
    if (obj === null || obj === undefined || obj === '') {
      return true;
    }
    if (Array.isArray(obj) && obj.length === 0) {
      return true;
    }
    if (typeof obj === 'object' && Object.keys(obj).length === 0) {
      return true;
    }
    return false;
  }

  /**
   * 判断是否不为空
   * @param obj
   * @returns {boolean}
   */
  static isNotEmpty(obj) {
    return !this.isEmpty(obj);
  }
}

// 挂载到全局window对象
if (typeof window !== 'undefined') {
  window.Tool = Tool;
}

export default Tool;