package top.superookie.orc.actuator.server;

import com.alibaba.fastjson.JSON;
import org.apache.http.client.methods.HttpGet;
import org.testng.Assert;
import org.testng.annotations.Test;
import top.superookie.toolcenter.http.CallResult;
import top.superookie.toolcenter.http.HttpOperate;
import top.superookie.toolcenter.http.HttpUtils;

public class VersionTest {

    private static String api = "/version";

    @Test(description = "获取执行器的版本信息")
    public void test01() {
        HttpGet httpGet = HttpUtils.actHttpGet(api);
        CallResult callResult = HttpOperate.execute(httpGet);
        System.out.println(callResult);
        Assert.assertEquals(callResult.getStatus().intValue(), 200);
        String actual = JSON.parseObject(callResult.getEntity()).getString("version");
        String expect = "1.5.15";
        Assert.assertEquals(actual, expect);
    }


}
