package com.emergency.materials.entity;

import java.util.UUID;

public class QuotaBlockRecord {
    private String id;
    private String applicantUnit;
    private Integer requestedQuantity;
    private Integer usedQuota;
    private Integer totalQuota;
    private Integer remainingQuota;
    private String materialType;
    private String reason;
    private Long blockedAt;

    public QuotaBlockRecord() {
        this.id = UUID.randomUUID().toString();
        this.blockedAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicantUnit() {
        return applicantUnit;
    }

    public void setApplicantUnit(String applicantUnit) {
        this.applicantUnit = applicantUnit;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public Integer getUsedQuota() {
        return usedQuota;
    }

    public void setUsedQuota(Integer usedQuota) {
        this.usedQuota = usedQuota;
    }

    public Integer getTotalQuota() {
        return totalQuota;
    }

    public void setTotalQuota(Integer totalQuota) {
        this.totalQuota = totalQuota;
    }

    public Integer getRemainingQuota() {
        return remainingQuota;
    }

    public void setRemainingQuota(Integer remainingQuota) {
        this.remainingQuota = remainingQuota;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(Long blockedAt) {
        this.blockedAt = blockedAt;
    }
}
