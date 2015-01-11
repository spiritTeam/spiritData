package com.spiritdata.dataanal.dictdata.pojo;

import com.spiritdata.framework.core.model.tree.TreeNode;

/**
 * 字典数据（字典模型），此模型包括所有者信息。由字典组和字典树组合而成
 * @author wh
 */
public class DictModel extends DictMaster{
    private static final long serialVersionUID = 5186525088340017035L;

    public DictModel() {
        super();
    }

    /**
     * 根据持久化数据（字典组），构造无字典树的字典模型
     * @param dMaster 字典组信息
     */
    public DictModel(DictMaster dMaster) {
        super();
        this.setDictModelByMaster(dMaster);
    }

    /**
     * 根据持久化数据（字典组），构造无字典树的字典模型
     * @param dMaster 字典组信息
     */
    public void setDictModelByMaster (DictMaster dMaster) {
        this.setId(dMaster.getId());
        this.setDmName(dMaster.getDmName());
        this.setNPy(dMaster.getNPy());
        this.setSort(dMaster.getSort());
        this.setIsValidate(dMaster.getIsValidate());
        this.setMType(dMaster.getMType());
        this.setMRef(dMaster.getMRef());
        this.setDescn(dMaster.getDescn());
        this.setOwnerId(dMaster.getOwnerId());
        this.setOwnerType(dMaster.getOwnerType());
        this.setCTime(dMaster.getCTime());
        this.setLmTime(dMaster.getLmTime());
    }

    /**
     * 获得字典组信息，为持久化处理
     */
    public DictMaster getDictMaster() {
        DictMaster dd = new DictMaster();
        dd.setId(this.getId());
        dd.setDmName(this.getDmName());
        dd.setNPy(this.getNPy());
        dd.setSort(this.getSort());
        dd.setIsValidate(this.getIsValidate());
        dd.setMType(this.getMType());
        dd.setMRef(this.getMRef());
        dd.setDescn(this.getDescn());
        dd.setOwnerId(this.getOwnerId());
        dd.setOwnerType(this.getOwnerType());
        dd.setCTime(this.getCTime());
        dd.setLmTime(this.getLmTime());
        return dd;
    }

    /**
     * 字典的根，此根结点是以本字典模型对应的字典组信息为基础的
     */
    public TreeNode<DictDetail> dictTree;
}