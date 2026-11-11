package com.nic.fldc.common.routing;

public class ZoneContextHolder {

    private static final ThreadLocal<String> CURRENT_ZONE_KEY = new ThreadLocal<>();
    private static final ThreadLocal<Integer> CURRENT_DISTRICT = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> READ_ONLY = new ThreadLocal<>();

    private ZoneContextHolder() {}

    public static void setZoneKey(String zoneKey) {
        CURRENT_ZONE_KEY.set(zoneKey);
    }

    public static String getZoneKey() {
        return CURRENT_ZONE_KEY.get();
    }

    public static void setDistrict(Integer districtCode) {
        CURRENT_DISTRICT.set(districtCode);
    }

    public static Integer getDistrict() {
        return CURRENT_DISTRICT.get();
    }

    public static void setReadOnly(boolean readOnly) {
        READ_ONLY.set(readOnly);
    }

    public static Boolean isReadOnly() {
        return Boolean.TRUE.equals(READ_ONLY.get());
    }

    public static void clear() {
        CURRENT_ZONE_KEY.remove();
        CURRENT_DISTRICT.remove();
        READ_ONLY.remove();
    }
}

