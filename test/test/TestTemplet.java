package test;

import com.spiritdata.dataanal.report.model.D_Tag;
import com.spiritdata.dataanal.report.model.DtagShowType;
import com.spiritdata.dataanal.report.model.OneJsond;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.model.ReportHead;

public class TestTemplet {

    public static void main(String[] args) {
        Report t = new Report();
        ReportHead th = new ReportHead();
        th.setId("abc");
        th.setCode("T.TEST::0002");
        th.setReportName("test");
        t.set_HEAD(th);
        t.set_REPORT("");
        OneJsond oj = new OneJsond();
        oj.setJsondId("001");
        oj.setFilePath("001FilePath");
        oj.setJsondCode("JD.EEE::001");
        oj.setUrl("001Url");
        t.addOneJsond(oj);
        oj = new OneJsond();
        oj.setJsondId("002");
        oj.setFilePath("002FilePath");
        oj.setJsondCode("JD.EEE::002");
        oj.setUrl("002Url");
        t.addOneJsond(oj);
        System.out.println(t.toJson());
        D_Tag dt = new D_Tag();
        dt.setDid("aabbcc");
        dt.setShowType(DtagShowType.VALUE);
        dt.setValue("abc");
        System.out.println(dt.toHtmlTag());
        dt = new D_Tag();
        dt.setDid("001");
        dt.setShowType(DtagShowType.FIRST);
        dt.setFuncStr("!first(3|num)");
        dt.setValue("abc");
        System.out.println(dt.toHtmlTag());
        dt = new D_Tag();
        dt.setDid("001");
        dt.setShowType(DtagShowType.PIE);
        dt.setValue("abc");
        dt.setLable("category");
        dt.setData("num");
        System.out.println(dt.toHtmlTag());
    }
}