/**
 * MydemoPo.java
 */
package com.beyonds.phoenix.mountain.domain.dao.po;


import com.beyonds.phoenix.mountain.core.common.model.WdBaseModel;
import java.util.Date;

/**
 * @Author: DanielCao
 * @Date:   2019-04-04
 * @Time:   17:36:52
 * +MydemoPo
 */

public class MydemoPo extends WdBaseModel {
    private static final long serialVersionUID = -7064281178984892806L;
    
    private Long id;
    private String goodsCode;
    private String goodsSn;
    private Byte status;
    private Date createTime;
    private Date updateTime;
    private Integer version;


    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the goodsCode
     */
    public String getGoodsCode() {
        return this.goodsCode;
    }

    /**
     * @param goodsCode the goodsCode to set
     */
    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    /**
     * @return the goodsSn
     */
    public String getGoodsSn() {
        return this.goodsSn;
    }

    /**
     * @param goodsSn the goodsSn to set
     */
    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    /**
     * @return the status
     */
    public Byte getStatus() {
        return this.status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return this.createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the updateTime
     */
    public Date getUpdateTime() {
        return this.updateTime;
    }

    /**
     * @param updateTime the updateTime to set
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return the version
     */
    public Integer getVersion() {
        return this.version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

}
