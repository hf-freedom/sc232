package com.emergency.materials.entity;

import java.time.LocalDate;
import java.util.UUID;

public class MaterialInventory {
    private String id;
    private String type;
    private String batchNumber;
    private String name;
    private Integer quantity;
    private Integer lockedQuantity;
    private LocalDate expiryDate;
    private String warehouseLocation;
    private String unit;
    private Long createdAt;

    public MaterialInventory() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.lockedQuantity = 0;
    }

    public int getAvailableQuantity() {
        return quantity - lockedQuantity;
    }

    public boolean isExpiringSoon() {
        return expiryDate != null && LocalDate.now().plusMonths(3).isAfter(expiryDate);
    }

    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getLockedQuantity() {
        return lockedQuantity;
    }

    public void setLockedQuantity(Integer lockedQuantity) {
        this.lockedQuantity = lockedQuantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
