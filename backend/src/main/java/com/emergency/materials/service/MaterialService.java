package com.emergency.materials.service;

import com.emergency.materials.entity.*;
import com.emergency.materials.store.DataStore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    public MaterialInventory addInventory(MaterialInventory inventory) {
        inventory.setId(UUID.randomUUID().toString());
        inventory.setLockedQuantity(0);
        DataStore.INVENTORY.put(inventory.getId(), inventory);
        return inventory;
    }

    public List<MaterialInventory> getAllInventory() {
        return new ArrayList<>(DataStore.INVENTORY.values());
    }

    public List<MaterialInventory> getInventoryByType(String type) {
        return DataStore.INVENTORY.values().stream()
                .filter(inv -> type.equals(inv.getType()))
                .sorted(Comparator.comparing(MaterialInventory::getExpiryDate, 
                    Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public int getTotalAvailableByType(String type) {
        return getInventoryByType(type).stream()
                .mapToInt(MaterialInventory::getAvailableQuantity)
                .sum();
    }

    public DemandRequest createRequest(DemandRequest request) {
        for (Map.Entry<String, Integer> entry : request.getMaterials().entrySet()) {
            int available = getTotalAvailableByType(entry.getKey());
            if (entry.getValue() > available) {
                throw new RuntimeException("物资 " + entry.getKey() + " 库存不足，可用: " + available);
            }
        }

        Integer quota = DataStore.UNIT_QUOTA.get(request.getApplicantUnit());
        if (quota == null) {
            quota = 500;
        }

        int totalRequested = request.getMaterials().values().stream().mapToInt(Integer::intValue).sum();
        int alreadyUsed = getUsedQuota(request.getApplicantUnit());
        if (totalRequested + alreadyUsed > quota) {
            QuotaBlockRecord blockRecord = new QuotaBlockRecord();
            blockRecord.setApplicantUnit(request.getApplicantUnit());
            blockRecord.setRequestedQuantity(totalRequested);
            blockRecord.setUsedQuota(alreadyUsed);
            blockRecord.setTotalQuota(quota);
            blockRecord.setRemainingQuota(quota - alreadyUsed);
            blockRecord.setMaterialType(request.getMaterials().keySet().stream().findFirst().orElse(""));
            blockRecord.setReason("超过可领上限");
            DataStore.QUOTA_BLOCKS.put(blockRecord.getId(), blockRecord);
            throw new RuntimeException("超过可领上限，剩余额度: " + (quota - alreadyUsed));
        }

        request.setPriority(calculatePriority(request));
        DataStore.REQUESTS.put(request.getId(), request);
        return request;
    }

    private int calculatePriority(DemandRequest request) {
        int priority = 0;
        switch (request.getUrgencyLevel()) {
            case "CRITICAL":
                priority += 100;
                break;
            case "HIGH":
                priority += 70;
                break;
            case "MEDIUM":
                priority += 40;
                break;
            case "LOW":
                priority += 10;
                break;
        }
        priority += Math.min(request.getPersonCount() / 10, 30);
        return priority;
    }

    private int getUsedQuota(String unit) {
        return DataStore.REQUESTS.values().stream()
                .filter(req -> unit.equals(req.getApplicantUnit()) && "APPROVED".equals(req.getStatus()))
                .mapToInt(req -> req.getMaterials().values().stream().mapToInt(Integer::intValue).sum())
                .sum();
    }

    public List<DemandRequest> getAllRequests() {
        return DataStore.REQUESTS.values().stream()
                .sorted(Comparator.comparing(DemandRequest::getPriority, Comparator.reverseOrder())
                        .thenComparing(DemandRequest::getCreatedAt))
                .collect(Collectors.toList());
    }

    public void lockInventory(String requestId) {
        DemandRequest request = DataStore.REQUESTS.get(requestId);
        if (request == null || !"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("申请状态不正确");
        }

        for (Map.Entry<String, Integer> entry : request.getMaterials().entrySet()) {
            int need = entry.getValue();
            List<MaterialInventory> inventories = getInventoryByType(entry.getKey());
            
            inventories.sort((a, b) -> {
                if (a.getExpiryDate() == null && b.getExpiryDate() == null) return 0;
                if (a.getExpiryDate() == null) return 1;
                if (b.getExpiryDate() == null) return -1;
                return a.getExpiryDate().compareTo(b.getExpiryDate());
            });
            
            for (MaterialInventory inv : inventories) {
                if (need <= 0) break;
                int canLock = Math.min(need, inv.getAvailableQuantity());
                if (canLock > 0) {
                    inv.setLockedQuantity(inv.getLockedQuantity() + canLock);
                    need -= canLock;
                }
            }
            if (need > 0) {
                releaseInventory(requestId);
                throw new RuntimeException("物资 " + entry.getKey() + " 库存不足");
            }
        }
        request.setStatus("LOCKED");
    }

    public void releaseInventory(String requestId) {
        DemandRequest request = DataStore.REQUESTS.get(requestId);
        if (request == null) return;

        for (Map.Entry<String, Integer> entry : request.getMaterials().entrySet()) {
            int need = entry.getValue();
            List<MaterialInventory> inventories = getInventoryByType(entry.getKey());
            for (MaterialInventory inv : inventories) {
                if (need <= 0) break;
                int canRelease = Math.min(need, inv.getLockedQuantity());
                if (canRelease > 0) {
                    inv.setLockedQuantity(inv.getLockedQuantity() - canRelease);
                    need -= canRelease;
                }
            }
        }
    }

    public DemandRequest approveRequest(String requestId, String approver, String remark, boolean approved) {
        DemandRequest request = DataStore.REQUESTS.get(requestId);
        if (request == null) {
            throw new RuntimeException("申请不存在");
        }

        if (!"LOCKED".equals(request.getStatus())) {
            throw new RuntimeException("请先锁定库存");
        }

        if (approved) {
            request.setStatus("APPROVED");
            request.setDistributedQuantity(new HashMap<>());
            for (Map.Entry<String, Integer> entry : request.getMaterials().entrySet()) {
                int need = entry.getValue();
                List<MaterialInventory> inventories = getInventoryByType(entry.getKey());
                
                inventories.sort((a, b) -> {
                    if (a.getExpiryDate() == null && b.getExpiryDate() == null) return 0;
                    if (a.getExpiryDate() == null) return 1;
                    if (b.getExpiryDate() == null) return -1;
                    return a.getExpiryDate().compareTo(b.getExpiryDate());
                });
                
                int distributed = 0;
                for (MaterialInventory inv : inventories) {
                    if (need <= 0) break;
                    int canTake = Math.min(need, inv.getLockedQuantity());
                    if (canTake > 0) {
                        inv.setQuantity(inv.getQuantity() - canTake);
                        inv.setLockedQuantity(inv.getLockedQuantity() - canTake);
                        need -= canTake;
                        distributed += canTake;

                        DistributionRecord record = new DistributionRecord();
                        record.setRequestId(requestId);
                        record.setApplicantUnit(request.getApplicantUnit());
                        record.setMaterialType(inv.getType());
                        record.setMaterialName(inv.getName());
                        record.setQuantity(canTake);
                        record.setBatchNumber(inv.getBatchNumber());
                        record.setExpiryDate(inv.getExpiryDate() != null ? inv.getExpiryDate().toString() : "无");
                        record.setIsExpiringSoon(inv.isExpiringSoon());
                        record.setOperator(approver);
                        DataStore.DISTRIBUTION.put(record.getId(), record);
                    }
                }
                request.getDistributedQuantity().put(entry.getKey(), distributed);
            }
        } else {
            releaseInventory(requestId);
            request.setStatus("REJECTED");
        }

        request.setApprover(approver);
        request.setApprovalRemark(remark);
        request.setApprovedAt(System.currentTimeMillis());
        return request;
    }

    public RecoveryRecord recoverMaterial(RecoveryRecord record) {
        record.setId(UUID.randomUUID().toString());
        record.setRecoveredAt(System.currentTimeMillis());
        DataStore.RECOVERY.put(record.getId(), record);

        Optional<MaterialInventory> existing = DataStore.INVENTORY.values().stream()
                .filter(inv -> inv.getType().equals(record.getMaterialType()) 
                    && inv.getBatchNumber().equals(record.getBatchNumber()))
                .findFirst();

        if (existing.isPresent()) {
            MaterialInventory inv = existing.get();
            inv.setQuantity(inv.getQuantity() + record.getQuantity());
        } else {
            MaterialInventory inv = new MaterialInventory();
            inv.setType(record.getMaterialType());
            inv.setName(record.getMaterialName());
            inv.setBatchNumber(record.getBatchNumber());
            inv.setQuantity(record.getQuantity());
            inv.setLockedQuantity(0);
            inv.setWarehouseLocation("回收入库");
            inv.setExpiryDate(LocalDate.now().plusYears(1));
            DataStore.INVENTORY.put(inv.getId(), inv);
        }

        return record;
    }

    public List<RecoveryRecord> getAllRecovery() {
        return new ArrayList<>(DataStore.RECOVERY.values());
    }

    public List<DistributionRecord> getAllDistribution() {
        return new ArrayList<>(DataStore.DISTRIBUTION.values());
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        Map<String, Integer> remaining = new HashMap<>();
        for (MaterialInventory inv : DataStore.INVENTORY.values()) {
            remaining.merge(inv.getType(), inv.getQuantity(), Integer::sum);
        }
        stats.put("remaining", remaining);

        Map<String, Integer> distributed = new HashMap<>();
        for (DistributionRecord rec : DataStore.DISTRIBUTION.values()) {
            distributed.merge(rec.getMaterialType(), rec.getQuantity(), Integer::sum);
        }
        stats.put("distributed", distributed);

        Map<String, Integer> recovered = new HashMap<>();
        for (RecoveryRecord rec : DataStore.RECOVERY.values()) {
            recovered.merge(rec.getMaterialType(), rec.getQuantity(), Integer::sum);
        }
        stats.put("recovered", recovered);

        long urgentCount = DataStore.REQUESTS.values().stream()
                .filter(req -> "CRITICAL".equals(req.getUrgencyLevel()) || "HIGH".equals(req.getUrgencyLevel()))
                .count();
        stats.put("urgentRequestCount", urgentCount);

        stats.put("totalRequests", DataStore.REQUESTS.size());
        stats.put("totalDistribution", DataStore.DISTRIBUTION.size());
        stats.put("totalRecovery", DataStore.RECOVERY.size());

        return stats;
    }

    public Map<String, Integer> getUnitQuotas() {
        return new HashMap<>(DataStore.UNIT_QUOTA);
    }

    public List<QuotaBlockRecord> getAllQuotaBlocks() {
        return new ArrayList<>(DataStore.QUOTA_BLOCKS.values());
    }

    public Map<String, Map<String, Integer>> getUnitQuotaUsage() {
        Map<String, Map<String, Integer>> usage = new HashMap<>();
        for (Map.Entry<String, Integer> entry : DataStore.UNIT_QUOTA.entrySet()) {
            String unit = entry.getKey();
            int totalQuota = entry.getValue();
            int usedQuota = getUsedQuota(unit);
            Map<String, Integer> unitData = new HashMap<>();
            unitData.put("totalQuota", totalQuota);
            unitData.put("usedQuota", usedQuota);
            unitData.put("remainingQuota", totalQuota - usedQuota);
            usage.put(unit, unitData);
        }
        return usage;
    }
}
