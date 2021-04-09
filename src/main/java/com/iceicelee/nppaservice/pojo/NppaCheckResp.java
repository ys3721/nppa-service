package com.iceicelee.nppaservice.pojo;

/**
 * 实名认证返回的结果
 *
 * @author: Yao Shuai
 * @date: 2021/4/9 20:54
 */
public class NppaCheckResp extends AbstractNppaResponse {

    private CheckRespData data;

    /**
     * 这个结果是这样的，自己手解一下吧，
     * 是不是可以换个jackson试试？
     * @param resp
     * Response:
     *  {
     *      "errorcode":0,
     *      "errormsg":"ok",
     *      "data":{
     *          "result":{
     *              "status":0,
     *              "pi":"test-pi"
     *          }
     *      }
     *
     *  }
     * 注意，也是可能没有data的返回
     * {"errcode":4002,"errmsg":"TEST TASK NOT EXIST"}
     */
    public void parserFromJson(String resp) {

    }

    /**
     * @author: Yao Shuai
     * @date: 2021/4/9 20:57
     */
    class CheckRespData {

        private CheckRespResult result;

        public CheckRespResult getResult() {
            return result;
        }

        public void setResult(CheckRespResult result) {
            this.result = result;
        }

        class CheckRespResult {
            final int status;
            final String pi;

            CheckRespResult(int status, String pi) {
                this.status = status;
                this.pi = pi;
            }
        }
    }

}
