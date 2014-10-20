package com.gmteam.spiritdata.login;

import javax.annotation.Resource;

import com.gmteam.framework.FConstants;
import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.core.web.AbstractSessionLoader;
import com.gmteam.framework.core.web.SessionLoader;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.service.MetadataService;
import com.gmteam.spiritdata.metadata.relation.service._OwnerMetadataService;

public class SessionLoaderImp extends AbstractSessionLoader implements SessionLoader {
    @Resource
    private _OwnerMetadataService _ownerMdService;
    @Resource
    private MetadataService metadataService;

    @Override
    public void loader() {
        //创建所有者元数据
        String ownerId = this.session.getId();
        int ownerType = 2;

        UgaUser user = (UgaUser)session.getAttribute(FConstants.SESSION_USER);
        if (user!=null) {
            ownerId = user.getUserId();
            ownerType = 1;
        }
        _ownerMdService.loadData2Session(ownerId, ownerType, this.session);
        //一下测试代码
        MetadataModel mm = new MetadataModel();
        mm.setId("1235434dsf");
        mm.setOwnerId(ownerId);
        mm.setOwnerType(ownerType);
        mm.setDescn("ddd");
        MetadataColumn mc = new MetadataColumn();
        mc.setColumnIndex(1);
        mc.setColumnName("col001");
        mc.setColumnType("String");
        mc.setTitleName("列111");
        mc.setId(null);
        try {
            mm.addColumn(mc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mc = new MetadataColumn();
        mc.setColumnIndex(2);
        mc.setColumnName("col002");
        mc.setColumnType("String");
        mc.setTitleName("列112");
        try {
            mm.addColumn(mc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            metadataService.setSession(session);
            metadataService.storeMdModel4Import(mm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}