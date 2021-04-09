package com.iceicelee.nppaservice.pojo;

import net.sf.json.JSONObject;

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
        JSONObject jsonObject = JSONObject.fromObject(resp);
        this.setErrcode(jsonObject.getInt("errorcode"));
        this.setErrmsg(jsonObject.getString("errormsg"));
        //写的有的诡异呢 感觉是个递归就行了
        if (jsonObject.containsKey("data")) {
            CheckRespData data = new CheckRespData();
            this.setData(data);
            JSONObject dataJson = jsonObject.getJSONObject("data");
            if (dataJson.containsKey("result")) {
                JSONObject resultJson = dataJson.getJSONObject("result");
                CheckRespData.CheckRespResult result = new CheckRespData.CheckRespResult(
                        resultJson.getInt("status"), resultJson.getString("pi"));
                data.setResult(result);
            }
        }
    }

    /**
     * @author: Yao Shuai
     * @date: 2021/4/9 20:57
     */
    public static class CheckRespData {

        private CheckRespResult result;

        public CheckRespResult getResult() {
            return result;
        }

        public void setResult(CheckRespResult result) {
            this.result = result;
        }

        static class CheckRespResult {
            final int status;
            final String pi;

            CheckRespResult(int status, String pi) {
                this.status = status;
                this.pi = pi;
            }
        }

    }

    public CheckRespData getData() {
        return data;
    }

    public void setData(CheckRespData data) {
        this.data = data;
    }

    public int getStatus() {
        return this.getData().getResult().status;
    }

    public String getPi() {
        return this.getData().getResult().pi;
    }
}
