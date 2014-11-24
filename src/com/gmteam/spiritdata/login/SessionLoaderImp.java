package com.gmteam.spiritdata.login;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.gmteam.framework.FConstants;
import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.core.web.AbstractSessionLoader;
import com.gmteam.framework.core.web.SessionLoader;
import com.gmteam.spiritdata.metadata.relation.service._OwnerMetadataService;

public class SessionLoaderImp implements SessionLoader {
    @Resource
    private _OwnerMetadataService _ownerMdService;

//    @Override
//    public void loader() {
//        //创建所有者元数据
//        String ownerId = this.session.getId();
//        int ownerType = 2;
//
//        UgaUser user = (UgaUser)this.session.getAttribute(FConstants.SESSION_USER);
//        if (user!=null) {
//            ownerId = user.getUserId();
//            ownerType = 1;
//        }
//        _ownerMdService.loadData2Session(ownerId, ownerType, this.session);
//    }
//
    @Override
    public void loader() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSession(HttpSession arg0) {
        // TODO Auto-generated method stub
        
    }
}