package com.gmteam.spiritdata.metadata.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/**
 * 所有者“关系型元数据”单元。
 * 所有者对“关系型元数据”的存储结构以及对这个存储结构的操作。
 * 主要服务于缓存(或Session)、数据导入、数据质量分析。
 * 
 * 这个类的实例：存储基本元数据信息，并与数据库实现同步。
 * 
 * 这个类提供如下功能：加载存储的信息，与数据库同步，元数据比较等功能。
 * @author wh
 */
public class OwnerRmdUnit {
    private boolean loadSuccess=false; //加载数据是否完成

    private String onwerId; //所有者Id，有可能是用户Id也有可能是SessionId
    private int onwerType; //所有者类型：1=用户；2=Session
    protected String test="aabbcc";
    private List<String> l = new ArrayList<String>();
    private Boolean b = Boolean.FALSE;;

    private Map<String, MetadataModel> mdModelMap; //所有者元数据集合

    /**
     * 构造所有者处理单元
     * @param ownerId 所有者类型
     * @param onwerType 所有者Id
     */
    public OwnerRmdUnit(String onwerId, int onwerType) {
        this.onwerId = onwerId;
        this.onwerType = onwerType;
        this.mdModelMap = new ConcurrentHashMap();
        loadDataThread lm = new loadDataThread(test, l, b);
        Thread t = new Thread(lm);
        System.out.println("Bgin::="+l.size()+">>"+test+">>"+b);
        t.start();

        int i=0;
        while (i<1000) {
            i++;
            System.out.println("::="+l.size()+">>"+test+">>"+b);
        }
        
        //读取数据库内容到mdModelList；采用线程的方式
        
    }

    public String getOnwerId() {
        return onwerId;
    }

    /**
     * 加入新的元数据模型，注意这个方法不判断元数据信息是否意义重复。
     * 这个方法做两个事情：
     * 1-向mdModelMap中加入元数据模型
     * 2-向数据库中插入相应的记录——注意只是基本元数据的信息(除了语义表)，不涉及关联表和数据表
     * @param mm 新元数据模型
     */
    public void addMetedataModel(MetadataModel mm) {
        //
    }

    /**
     * 删除元数据模型。
     * 这个方法做两个事情：
     * 1-从mdModelMap中删除元数据模型
     * 2-从数据库中删除相应的记录
     * @param mdMId 元数据模型Id
     */
    public void delMetedataModel(String mdMId) {
        //先删除数据表
        //再删除metadata表
        //最后删除mdModelMap中信息
    }

    /**
     * 更改元数据模型，个方法做两个事情：
     * 1-更改mdModelMap中的元数据模型
     * 2-更改数据库中相应的记录
     * 具体怎样修改，还需要不断完善，应该和数据库的修改类似，目前规定：
     * 1-元数据ownerId/mdMId不能修改
     * 2-元数据列数量不能修改，数据类型不能修改，id不能修改，只能修改titleName
     * @param mm 需修改的元数据模式
     */
    public void updateMetedataModel(MetadataModel mm) {
        
    }

    public static void main(String args[]) throws InterruptedException {
        OwnerRmdUnit oru = new OwnerRmdUnit("", 1);
    }
}

class loadDataThread implements Runnable{
    private String t="";
    private List<String> l2;
    private Boolean _b;
    public loadDataThread(String t, List<String> l, Boolean b) {
        this.t=t;
        this.l2 = l;
        this._b=b;
    }
    
    @Override
    public void run() {
        this.t="change";
        _b = Boolean.TRUE;
        l2.add("SSSSS");
    }
    
}
