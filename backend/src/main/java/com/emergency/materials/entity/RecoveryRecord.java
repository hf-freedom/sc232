package com.emergency.materials.entity;

import java.util.UUID;

public class RecoveryRecord {
    private String id;
    private String requestId;
    private String applicantUnit;
    private String materialType;
    private String materialName;
    private Integer quantity;
    private String batchNumber;
    private String reason;
    private Long recoveredAt;
    private String operator;

    public RecoveryRecord() {
        this.id = UUID.randomUUID().toString();
        this.recoveredAt = System.currentTimeMillis();
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getRecoveredAt() {
        return recoveredAt;
    }

    public void setRecoveredAt(Long recoveredAt) {
        this.recoveredAt = recoveredAt;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
