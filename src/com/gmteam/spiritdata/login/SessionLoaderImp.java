package com.gmteam.spiritdata.login;

import com.gmteam.framework.FConstants;
import com.gmteam.framework.UGA.UgaUser;
import com.gmteam.framework.core.web.AbstractSessionLoader;
import com.gmteam.framework.core.web.SessionLoader;
import com.gmteam.spiritdata.SDConstants;
import com.gmteam.spiritdata.metadata.relation.OwnerRmdUnit;

public class SessionLoaderImp extends AbstractSessionLoader implements SessionLoader {

    @Override
    public void loader() {
        System.out.println("==="+this.session.getId());
        //创建所有者元数据
        String ownerId = this.session.getId();
        UgaUser user = (UgaUser)session.getAttribute(FConstants.SESSION_USER);
        int ownerType = 2;
        if (user!=null) {
            ownerId = user.getUserId();
            ownerType = 1;
        }
        OwnerRmdUnit oru = new OwnerRmdUnit(ownerId, ownerType);
        this.session.setAttribute(SDConstants.SESSION_OWNERRMDUNIT, oru);
    }
}