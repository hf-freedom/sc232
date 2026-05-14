package com.emergency.materials.controller;

import com.emergency.materials.entity.*;
import com.emergency.materials.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @PostMapping("/inventory")
    public ResponseEntity<?> addInventory(@RequestBody MaterialInventory inventory) {
        try {
            return ResponseEntity.ok(materialService.addInventory(inventory));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<MaterialInventory>> getAllInventory() {
        return ResponseEntity.ok(materialService.getAllInventory());
    }

    @PostMapping("/requests")
    public ResponseEntity<?> createRequest(@RequestBody DemandRequest request) {
        try {
            return ResponseEntity.ok(materialService.createRequest(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/requests")
    public ResponseEntity<List<DemandRequest>> getAllRequests() {
        return ResponseEntity.ok(materialService.getAllRequests());
    }

    @PostMapping("/requests/{id}/lock")
    public ResponseEntity<?> lockInventory(@PathVariable String id) {
        try {
            materialService.lockInventory(id);
            return ResponseEntity.ok("库存锁定成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<?> approveRequest(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        try {
            String approver = (String) body.get("approver");
            String remark = (String) body.getOrDefault("remark", "");
            boolean approved = (Boolean) body.get("approved");
            DemandRequest result = materialService.approveRequest(id, approver, remark, approved);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);
            response.put("message", approved ? "审批通过，物资已发放" : "审批拒绝，库存已释放");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/recovery")
    public ResponseEntity<?> recoverMaterial(@RequestBody RecoveryRecord record) {
        try {
            return ResponseEntity.ok(materialService.recoverMaterial(record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/recovery")
    public ResponseEntity<List<RecoveryRecord>> getAllRecovery() {
        return ResponseEntity.ok(materialService.getAllRecovery());
    }

    @GetMapping("/distribution")
    public ResponseEntity<List<DistributionRecord>> getAllDistribution() {
        return ResponseEntity.ok(materialService.getAllDistribution());
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = materialService.getStatistics();
        stats.put("totalQuotaBlocks", materialService.getAllQuotaBlocks().size());
        stats.put("unitQuotaUsage", materialService.getUnitQuotaUsage());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/quotas")
    public ResponseEntity<Map<String, Integer>> getUnitQuotas() {
        return ResponseEntity.ok(materialService.getUnitQuotas());
    }

    @GetMapping("/quotas/usage")
    public ResponseEntity<Map<String, Map<String, Integer>>> getUnitQuotaUsage() {
        return ResponseEntity.ok(materialService.getUnitQuotaUsage());
    }

    @GetMapping("/quota-blocks")
    public ResponseEntity<List<QuotaBlockRecord>> getAllQuotaBlocks() {
        return ResponseEntity.ok(materialService.getAllQuotaBlocks());
    }

    @GetMapping("/inventory/batches/{type}")
    public ResponseEntity<List<Map<String, Object>>> getAvailableBatches(@PathVariable String type) {
        List<MaterialInventory> inventories = materialService.getInventoryByType(type);
        List<Map<String, Object>> batches = new ArrayList<>();
        for (MaterialInventory inv : inventories) {
            if (inv.getAvailableQuantity() > 0) {
                Map<String, Object> batch = new HashMap<>();
                batch.put("id", inv.getId());
                batch.put("batchNumber", inv.getBatchNumber());
                batch.put("expiryDate", inv.getExpiryDate() != null ? inv.getExpiryDate().toString() : "无");
                batch.put("availableQuantity", inv.getAvailableQuantity());
                batch.put("isExpiringSoon", inv.isExpiringSoon());
                batches.add(batch);
            }
        }
        return ResponseEntity.ok(batches);
    }
}
