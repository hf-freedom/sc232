package com.emergency.materials.entity;

import java.util.Map;
import java.util.UUID;

public class DemandRequest {
    private String id;
    private String applicantUnit;
    private Integer personCount;
    private String purpose;
    private String urgencyLevel;
    private Map<String, Integer> materials;
    private String status;
    private Integer priority;
    private String approver;
    private String approvalRemark;
    private Long createdAt;
    private Long approvedAt;
    private Map<String, Integer> distributedQuantity;

    public DemandRequest() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.status = "PENDING";
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

    public Integer getPersonCount() {
        return personCount;
    }

    public void setPersonCount(Integer personCount) {
        this.personCount = personCount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public Map<String, Integer> getMaterials() {
        return materials;
    }

    public void setMaterials(Map<String, Integer> materials) {
        this.materials = materials;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getApprovalRemark() {
        return approvalRemark;
    }

    public void setApprovalRemark(String approvalRemark) {
        this.approvalRemark = approvalRemark;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Long approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Map<String, Integer> getDistributedQuantity() {
        return distributedQuantity;
    }

    public void setDistributedQuantity(Map<String, Integer> distributedQuantity) {
        this.distributedQuantity = distributedQuantity;
    }
}
