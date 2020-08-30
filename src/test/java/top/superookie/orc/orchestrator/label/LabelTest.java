package top.superookie.orc.orchestrator.label;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import top.superookie.toolcenter.database.DBInfo;
import top.superookie.toolcenter.database.DBOperate;
import top.superookie.toolcenter.http.CallResult;
import top.superookie.toolcenter.http.HttpOperate;
import top.superookie.toolcenter.http.HttpUtils;
import top.superookie.toolcenter.http.OrcHeader;

import java.sql.Connection;
import java.time.*;
import java.util.Map;
import java.util.UUID;

public class LabelTest {

    private static String api = "/api/v1/labels";

    private static final String payload = "{\"name\":\"%s\",\"createdAt\":\"%s\",\"createdBy\":\"%s\",\"resourceSn\":\"%s\"}";

    private static Connection orcConn = DBInfo.getConn("db.orchestrator");

    @DataProvider(name = "data")
    public Object[][] data() {
        return new Object[][] {
                { "tag5", LocalDateTime.now().atOffset(ZoneOffset.UTC).toString(), "abc", UUID.randomUUID().toString() }
        };
    }

    @Test(description = "添加标签", dataProvider = "data")
    public void test01(String name, String createdAt, String createdBy, String resourceSn) {
        clear(name);
        String reqBody = String.format(payload, name, createdAt, createdBy, resourceSn);
        HttpPost httpPost = HttpUtils.orcHttpPost(api, OrcHeader.CONTENT_TYPE_JSON);
        StringEntity entity = new StringEntity(reqBody, "UTF-8");
        httpPost.setEntity(entity);
        CallResult callResult = HttpOperate.execute(httpPost);
        System.out.println(callResult.getEntity());
        Assert.assertEquals(callResult.getStatus().intValue(), 201);
        String sql = "select * from label where name='%s'";
        Map<String, Object> map = DBOperate.selectOne(orcConn, String.format(sql, name));
        Assert.assertEquals(map.get("name").toString(), name);
        Assert.assertEquals(map.get("created_by").toString(), createdBy);
        Assert.assertNull(map.get("comments"));
    }

    private static void clear(String labelName) {
        String sql = "delete from label where name='%s'";
        int count = DBOperate.execute(orcConn, String.format(sql, labelName));
        System.out.println(String.format("一共清理了%d条重名数据\n", count));
    }
}
