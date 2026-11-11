package com.nic.fldc.workflow.statemachine;

public final class OpType {

    private OpType() {}

    public static final String SUBMITTED = "SUBMITTED";
    public static final String ENQUIRY_PROCESSED = "ENQUIRY_PROCESSED";
    public static final String ENQUIRY_REJECTED = "ENQUIRY_REJECTED";
    public static final String ENQUIRY_REVERTED = "ENQUIRY_REVERTED";
    public static final String ACCEPT_FAMILY = "ACCEPT_FAMILY";
    public static final String REJECT_FAMILY = "REJECT_FAMILY";
    public static final String REVERT_FAMILY = "REVERT_FAMILY";
    public static final String RECOMMEND_ENQUIRY = "RECOMMEND_ENQUIRY";
    public static final String HELDUP_V = "HELDUP_V";
    public static final String VERIFY = "VERIFY";
    public static final String REJECT = "REJECT";
    public static final String REVERT = "REVERT";
    public static final String RECOMMENDED_FOR_REJECTION = "RECOMMENDED_FOR_REJECTION";
    public static final String APPROVE = "APPROVE";
    public static final String HELDUP_A = "HELDUP_A";
    public static final String HELDUP_M_A = "HELDUP_M_A";
    public static final String REJECTED_HOLD_A = "REJECTED_HOLD_A";
    public static final String APPROVE_FROM_REJECT = "APPROVE_FROM_REJECT";
    public static final String REJECT_TO_ENQUIRY = "REJECT_TO_ENQUIRY";

    public static String holdAuditType(String typeName) {
        return "HOLD_" + (typeName != null ? typeName.trim() : "GENERAL");
    }
}

