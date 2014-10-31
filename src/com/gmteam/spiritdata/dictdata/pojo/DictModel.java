package com.gmteam.spiritdata.dictdata.pojo;

import com.gmteam.framework.core.model.tree.TreeNode;

/**
 * 字典数据（字典模型），此模型包括素有者信息。
 * 由字典组和字典树组合而成
 * @author wh
 */
public class DictModel extends DictMaster{
    private static final long serialVersionUID = 5186525088340017035L;

    public DictModel() {
        super();
    }

    public DictModel(DictMaster dMaster) {
        super();
        this.setDictModelByMaster(dMaster);
    }

    public void setDictModelByMaster (DictMaster dMaster) {
        this.setId(dMaster.getId());
        this.setDmName(dMaster.getDmName());
        this.setnPy(dMaster.getnPy());
        this.setSort(dMaster.getSort());
        this.setIsValidate(dMaster.getIsValidate());
        this.setmType(dMaster.getmType());
        this.setmRef(dMaster.getmRef());
        this.setDescn(dMaster.getDescn());
        this.setOwnerId(dMaster.getOwnerId());
        this.setOwnerType(dMaster.getOwnerType());
        this.setcTime(dMaster.getcTime());
    }

    public DictMaster getDictMaster() {
        DictMaster dd = new DictMaster();
        dd.setId(this.getId());
        dd.setDmName(this.getDmName());
        dd.setnPy(this.getnPy());
        dd.setSort(this.getSort());
        dd.setIsValidate(this.getIsValidate());
        dd.setmType(this.getmType());
        dd.setmRef(this.getmRef());
        dd.setDescn(this.getDescn());
        dd.setOwnerId(this.getOwnerId());
        dd.setOwnerType(this.getOwnerType());
        dd.setcTime(this.getcTime());
        return dd;
    }

    public TreeNode<DictDetail> dictTree;//字典的根
}