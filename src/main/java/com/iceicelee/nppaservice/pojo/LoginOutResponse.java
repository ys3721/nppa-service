package com.iceicelee.nppaservice.pojo;

import com.iceicelee.nppaservice.pojo.LoginOutResponse.LoginOutRespData.ReportResult;
import com.iceicelee.nppaservice.pojo.NppaCheckResp.CheckRespData;
import com.iceicelee.nppaservice.pojo.NppaCheckResp.CheckRespData.CheckRespResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author: Yao Shuai
 * @date: 2021/4/19 18:14
 */
public class LoginOutResponse extends AbstractNppaResponse {

    private LoginOutRespData data;

    public LoginOutRespData getData() {
        return data;
    }

    public void setData(LoginOutRespData data) {
        this.data = data;
    }

    public static class LoginOutRespData {

        List<ReportResult> results;

        public List<ReportResult> getResults() {
            return results;
        }

        public void setResults(List<ReportResult> results) {
            this.results = results;
        }

        static class ReportResult {
            final int no;
            final int errcode;
            final String errmsg;

            ReportResult(int no, int errcode, String errmsg) {
                this.no = no;
                this.errcode = errcode;
                this.errmsg = errmsg;
            }
        }
    }

    /**
     * 这个结果是这样的，自己手解一下吧，
     */
    public void parserFromJson(String resp) {
        JSONObject jsonObject = JSONObject.fromObject(resp);
        this.setErrcode(jsonObject.getInt("errcode"));
        this.setErrmsg(jsonObject.getString("errmsg"));
        //写的有的诡异呢 感觉是个递归就行了
        if (jsonObject.containsKey("data")) {
            LoginOutRespData data = new LoginOutRespData();
            this.setData(data);
            JSONObject dataJson = jsonObject.getJSONObject("data");
            List<ReportResult> errResults = new ArrayList<>(dataJson.size());
            data.setResults(errResults);
            if (dataJson.containsKey("results")) {
                JSONArray jsonArray =  JSONArray.fromObject(dataJson.get("results"));
                for (JSONObject resultJo : (Iterable<JSONObject>) jsonArray) {
                    ReportResult errInfo = new ReportResult(
                            resultJo.getInt("no"),
                            resultJo.getInt("errcode"),
                            resultJo.getString("errmsg"));
                    errResults.add(errInfo);
                }
            }
        }
    }
}
