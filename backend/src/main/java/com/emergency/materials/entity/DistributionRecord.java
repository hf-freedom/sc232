package com.emergency.materials.entity;

import java.util.UUID;

public class DistributionRecord {
    private String id;
    private String requestId;
    private String applicantUnit;
    private String materialType;
    private String materialName;
    private Integer quantity;
    private String batchNumber;
    private String expiryDate;
    private Boolean isExpiringSoon;
    private Long distributedAt;
    private String operator;

    public DistributionRecord() {
        this.id = UUID.randomUUID().toString();
        this.distributedAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getApplicantUnit() {
        return applicantUnit;
    }

    public void setApplicantUnit(String applicantUnit) {
        this.applicantUnit = applicantUnit;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getIsExpiringSoon() {
        return isExpiringSoon;
    }

    public void setIsExpiringSoon(Boolean isExpiringSoon) {
        this.isExpiringSoon = isExpiringSoon;
    }

    public Long getDistributedAt() {
        return distributedAt;
    }

    public void setDistributedAt(Long distributedAt) {
        this.distributedAt = distributedAt;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
