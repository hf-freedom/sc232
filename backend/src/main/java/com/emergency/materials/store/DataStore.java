package com.emergency.materials.store;

import com.emergency.materials.entity.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    public static final Map<String, MaterialInventory> INVENTORY = new ConcurrentHashMap<>();
    public static final Map<String, DemandRequest> REQUESTS = new ConcurrentHashMap<>();
    public static final Map<String, DistributionRecord> DISTRIBUTION = new ConcurrentHashMap<>();
    public static final Map<String, RecoveryRecord> RECOVERY = new ConcurrentHashMap<>();
    public static final Map<String, QuotaBlockRecord> QUOTA_BLOCKS = new ConcurrentHashMap<>();
    public static final Map<String, Integer> UNIT_QUOTA = new ConcurrentHashMap<>();

    static {
        UNIT_QUOTA.put("A社区", 1000);
        UNIT_QUOTA.put("B社区", 800);
        UNIT_QUOTA.put("C社区", 1200);
        UNIT_QUOTA.put("D社区", 600);
        UNIT_QUOTA.put("E社区", 900);
    }
}
