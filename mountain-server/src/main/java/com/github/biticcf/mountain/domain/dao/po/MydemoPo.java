/**
 * MydemoPo.java
 */
package com.github.biticcf.mountain.domain.dao.po;


import java.util.Date;

import com.github.biticcf.mountain.core.common.model.WdBaseModel;
import com.github.biticcf.mountain.generator.annotation.ColumnConfig;
import com.github.biticcf.mountain.generator.annotation.EnuFieldType;
import com.github.biticcf.mountain.generator.annotation.TableConfig;

/**
 * author: DanielCao
 * date:   2019-04-05
 * time:   14:24:15
 * 
 * +MydemoPo
 */
@TableConfig(poName = "MydemoPo", tableName = "WD_MY_DEMO_INFO")
public class MydemoPo extends WdBaseModel {
    private static final long serialVersionUID = 2219364663353079254L;
    
    @ColumnConfig(propertyName = "id", columnName = "id", primaryKeyFlag = true, columnType = EnuFieldType.BIGINT)
    private Long id;
    @ColumnConfig(propertyName = "goodsCode", columnName = "goods_code", columnType = EnuFieldType.VARCHAR)
    private String goodsCode;
    @ColumnConfig(propertyName = "goodsSn", columnName = "goods_sn", columnType = EnuFieldType.VARCHAR)
    private String goodsSn;
    @ColumnConfig(propertyName = "status", columnName = "status", columnType = EnuFieldType.TINYINT)
    private Byte status;
    @ColumnConfig(propertyName = "createTime", columnName = "create_time", columnType = EnuFieldType.DATETIME)
    private Date createTime;
    @ColumnConfig(propertyName = "updateTime", columnName = "update_time", columnType = EnuFieldType.TIMESTAMP)
    private Date updateTime;
    @ColumnConfig(propertyName = "version", columnName = "version", columnType = EnuFieldType.INTEGER)
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
