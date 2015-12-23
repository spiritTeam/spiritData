package com.spiritdata.dataanal.testWebAnal4AJ.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;

import com.spiritdata.framework.util.DateUtils;

public class GetDataService {
    @Resource
    protected BasicDataSource dataSource;

    /**
     * 获得历史小时访问数据
     * @return
     * @throws SQLException 
     */
    public Map<String, String> getHistoryHourVisit() throws SQLException {
        Map<String, String> m = new LinkedHashMap<String, String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String tempStr="";
        try {
            conn = dataSource.getConnection();
            String sql = "select substr(a, 12) s, avg(b) from ("
              +" select DATE_FORMAT(visitTime, '%Y-%m-%d %H') as a, count(*) as b from sa_visit_log"
              +" where objId='国家安全生产监督管理总局'"
              +" GROUP BY DATE_FORMAT( visitTime, '%Y-%m-%d %H')"
              +") as c group by substr(a, 12)";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs!=null) {//注意这里不考虑非varchar2以外的数据类型，应为所有表都是系统自动建立的
                while (rs.next()) {
                    tempStr = rs.getString(2);
                    m.put(rs.getString(1), tempStr.substring(0, tempStr.indexOf(".")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rs.close();
            ps.close();
            conn.close();
        }
        return m;
    }

    /**
     * 来源
     * @return
     * @throws SQLException 
     */
    public Map<String, String> getVisitFrom() throws SQLException {
        Map<String, String> m = new LinkedHashMap<String, String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String tempStr="";
        try {
            conn = dataSource.getConnection();
            String sql = "select a, count(*) from ("
              +"select ltrim(substr(fromUrl, 1, POSITION('/' in (subStr(fromUrl, 10)))+9)) as a,  fromUrl from sa_visit_log"
              +" where objId='国家安全生产监督管理总局'"
              +") as c group by a order by count(*) desc";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs!=null) {//注意这里不考虑非varchar2以外的数据类型，应为所有表都是系统自动建立的
                int c = 0;
                int s = 0;
                while (rs.next()) {
                    if (c<=8) {
                        tempStr = rs.getString(1);
                        if (tempStr==null||"".equals(tempStr)) m.put("直接访问", rs.getString(2));
                        else m.put(tempStr, rs.getString(2));
                    } else {
                        s+=rs.getInt(2);
                    }
                    c++;
                }
                if (c>8) m.put("其他", s+"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rs.close();
            ps.close();
            conn.close();
        }
        return m;
    }

    /**
     * 获得间隔时间历史数据，只后的最近的100条
     * @return 
     * @throws SQLException 
     */
    public Map<String, String> getVisitCount(int minites) throws SQLException {
        Map<String, String> m = new LinkedHashMap<String, String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            String sql = "select visitTime-visitTime% ("+minites+"*60) , count(*)"
                +" from sa_visit_log where objId='国家安全生产监督管理总局'"
                +" group by visitTime-visitTime% ("+minites+"*60) order by visitTime-visitTime% ("+minites+"*60) desc";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs!=null) {//注意这里不考虑非varchar2以外的数据类型，应为所有表都是系统自动建立的
                int c = 0;
                while (rs.next()) {
                    if (c<=40) m.put(rs.getString(1), rs.getString(2));
                    else break;
                    c++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rs.close();
            ps.close();
            conn.close();
        }
        return m;
    }

    /**
     * 从某一时间后，获取实时间隔数据
     * @return 
     * @throws SQLException 
     */
    public Map<String, String> getRealVisitCount(int minites, String beginTimeStr) throws SQLException {
        Map<String, String> m = new LinkedHashMap<String, String>();
        if (beginTimeStr.equals("")) return m;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean isNull=false;
        try {
            conn = dataSource.getConnection();
            String sql = "select DATE_FORMAT(max(visitTime), '%Y%m%d%H%i%s'), count(*) from sa_visit_log where objId='国家安全生产监督管理总局' and DATE_FORMAT(visitTime, '%Y%m%d%H%i%s')>'"+beginTimeStr+"'";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs!=null) {//注意这里不考虑非varchar2以外的数据类型，应为所有表都是系统自动建立的
                while (rs.next()) {
                    if (rs.getString(1)==null) {
                        isNull=true;
                        break;
                    }
                    m.put(rs.getString(1), rs.getString(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rs.close();
            ps.close();
            conn.close();
        }
        if (isNull) m.clear();
        return m;
    }

    /**
     * 获取计数数据
     * @return 
     * @throws SQLException 
     */
    public Map<String, String> getRealCount() throws SQLException {
        Map<String, String> m = new LinkedHashMap<String, String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            String sql = "select count(*) from sa_visit_log  where objId='国家安全生产监督管理总局'";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs!=null&&rs.next()) {//注意这里不考虑非varchar2以外的数据类型，应为所有表都是系统自动建立的
                m.put("all", rs.getString(1));
            }
            sql = "select DATE_FORMAT(visitTime, '%Y-%m-%d'), count(*) a from sa_visit_log where objId='国家安全生产监督管理总局' group by DATE_FORMAT(visitTime, '%Y-%m-%d') order by DATE_FORMAT(visitTime, '%Y-%m-%d') desc";
            rs.close();rs=null;
            ps.close();ps=null;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs!=null&&rs.next()) {//注意这里不考虑非varchar2以外的数据类型，应为所有表都是系统自动建立的
                m.put("curDate", rs.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rs.close();
            ps.close();
            conn.close();
        }
        return m;
    }

    /**
     * 获得每日访问
     * @return 前7日情况
     * @throws SQLException 
     */
    public Map<String, String> getPerDate() throws SQLException {
        Map<String, String> m = new LinkedHashMap<String, String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            String sql = "select DATE_FORMAT(visitTime, '%Y%m%d'), count(*) from sa_visit_log where objId='国家安全生产监督管理总局' group by DATE_FORMAT(visitTime, '%Y%m%d') desc";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs!=null) {//注意这里不考虑非varchar2以外的数据类型，应为所有表都是系统自动建立的
                int c = 0;
                while (rs.next()) {
                    Date d = DateUtils.getDateTime("yyyyMMdd", rs.getString(1));
                    if (c++<8) m.put(DateUtils.convert2LocalStr("yyyy-MM-dd", d), rs.getString(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rs.close();
            ps.close();
            conn.close();
        }
        return m;
    }
}
/*
select sum(a), avg(a) from (
select DATE_FORMAT(visitTime, '%Y-%m-%d'), count(*) a from sa_visit_log
group by DATE_FORMAT(visitTime, '%Y-%m-%d')
) as b
*/