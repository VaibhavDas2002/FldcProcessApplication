package com.nic.fldc.workflow.statemachine;

public final class ApplicationStatus {

    private ApplicationStatus() {}

    public static final String DRAFT = "DRAFT";
    public static final String SUBMITTED = "SUBMITTED";
    public static final String ENQUIRY_PROCESSED = "ENQUIRY_PROCESSED";
    public static final String ENQUIRY_REJECTED = "ENQUIRY_REJECTED";
    public static final String ENQUIRY_REVERTED = "ENQUIRY_REVERTED";
    public static final String REVERTED = "REVERTED";
    public static final String SENT_FOR_ENQUIRY = "SENT_FOR_ENQUIRY";
    public static final String HELDUP_V = "HELDUP_V";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String VERIFIED = "VERIFIED";
    public static final String APPROVED = "APPROVED";
    public static final String APPROVE = "APPROVE";
    public static final String REJECTED = "REJECTED";
    public static final String HELDUP_A = "HELDUP_A";
    public static final String HELDUP_M_A = "HELDUP_M_A";
    public static final String REJECTED_HOLD_A = "REJECTED_HOLD_A";
    public static final String RECOMMENDED_FOR_REJECTION = "RECOMMENDED_FOR_REJECTION";
    public static final String REJECT_TO_ENQUIRY = "REJECT TO ENQUIRY";

    public static String holdType(String typeName) {
        return "HOLD_" + (typeName != null ? typeName.trim() : "GENERAL");
    }
}

