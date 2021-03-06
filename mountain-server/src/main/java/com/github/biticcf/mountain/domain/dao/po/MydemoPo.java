/**
 * MydemoPo.java
 */
package com.github.biticcf.mountain.domain.dao.po;


import java.util.Date;

import org.apache.ibatis.type.JdbcType;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.github.biticcf.mountain.core.common.model.WdBaseModel;

/**
 * author: DanielCao
 * date:   2019-04-05
 * time:   14:24:15
 * 
 * +MydemoPo
 */
@TableName(value = "WD_MY_DEMO_INFO")
public class MydemoPo extends WdBaseModel {
    private static final long serialVersionUID = 2219364663353079254L;
    
    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id", jdbcType = JdbcType.BIGINT)
    private Long id;
    @TableField(value = "goods_code", jdbcType = JdbcType.VARCHAR)
    private String goodsCode;
    @TableField(value = "goods_sn", jdbcType = JdbcType.VARCHAR)
    private String goodsSn;
    @TableField(value = "status", jdbcType = JdbcType.TINYINT)
    private Byte status;
    @TableField(value = "create_time", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
    private Date createTime;
    @TableField(value = "update_time", jdbcType = JdbcType.TIMESTAMP_WITH_TIMEZONE)
    private Date updateTime;
    @TableField(value = "version", jdbcType = JdbcType.INTEGER)
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
