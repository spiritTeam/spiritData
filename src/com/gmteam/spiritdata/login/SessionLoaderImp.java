package com.gmteam.spiritdata.login;

import javax.annotation.Resource;

import com.gmteam.framework.FConstants;
import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.core.web.AbstractSessionLoader;
import com.gmteam.framework.core.web.SessionLoader;
import com.gmteam.spiritdata.metadata.relation.service._OwnerMetadataService;

public class SessionLoaderImp extends AbstractSessionLoader implements SessionLoader {
    @Resource
    private _OwnerMetadataService _ownerMdService;
    public _OwnerMetadataService get_ownerMdService() {
        return _ownerMdService;
    }
    public void set_ownerMdService(_OwnerMetadataService _ownerMdService) {
        this._ownerMdService = _ownerMdService;
    }

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
    }
}