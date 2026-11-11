Check the code and features and update the workflow documentation below .

FLDC Workflow

Referneces
f => dbt_apy.families
fm => dbt_apy.family_members
ari => dbt_apy.accept_reject_info (Audit & Activity Log)


================================================================================
ROLE-WISE WORKFLOW SPECIFICATION & ACTIONS
================================================================================

Citizen (Online Application)
1. Show Form on Citizen End for Online Application:
    a. Fresh Draft Application:
        (f.create_mode = 'C' and fm.create_mode = 'C' and f.next_level_role_id = 0 and fm.next_level_role_id = 0 and f.application_status = 'DRAFT')
    b. Reverted Application for Re-edit & Resubmission:
        - Reverted from Enquiry: (f.create_mode = 'C' and fm.create_mode = 'C' and f.next_level_role_id = 5 and fm.next_level_role_id = 5 and f.application_status in ('ENQUIRY_REVERTED', 'REVERTED'))
        - Reverted from Verifier: (f.create_mode = 'C' and fm.create_mode = 'C' and f.next_level_role_id = 6 and fm.next_level_role_id = 6 and f.application_status = 'REVERTED')
        - Reverted from Approver: (f.create_mode = 'C' and fm.create_mode = 'C' and f.next_level_role_id = 4 and fm.next_level_role_id = 4 and f.application_status = 'REVERTED')

2. On Final Submit of Application:-
    f.next_level_role_id = 10
    fm.next_level_role_id = 10
    f.application_status = 'SUBMITTED'
    fm.application_status = 'SUBMITTED'
    f.is_agreed = true
    f.updated_at = now()
    fm.updated_at = now()
    Audit Log: ari.op_type = 'SUBMITTED'



Enquiry Officer
1. Show Datatables on:
    a. Under Enquiry Queue (Tab: Under Enquiry):
        (f.under_enquiry = true and fm.under_enquiry = true and f.next_level_role_id = 10 and fm.next_level_role_id = 10)
    b. Office Entry Queue (Tab: Office Application):
        ((f.create_mode = 'O' or f.create_mode = 'o') and (fm.create_mode = 'O' or fm.create_mode = 'o') and f.under_enquiry = false and f.next_level_role_id = 10 and fm.next_level_role_id = 10)
    c. Citizen / Online Entry Queue (Tab: Online Application):
        ((f.create_mode = 'C' or f.create_mode = 'c') and (fm.create_mode = 'C' or fm.create_mode = 'c') and f.under_enquiry = false and f.next_level_role_id = 10 and fm.next_level_role_id = 10)
    d. Cross-Check / Priority Queue (Tab: APY Paid):
        (EXISTS (SELECT 1 FROM cross_check.lb_data_paid_apy cc WHERE cc.family_id = f.id and cc.lgd_district_code = f.lgd_district_code) and f.under_enquiry = false and f.next_level_role_id = 10 and fm.next_level_role_id = 10)
    e. Recommended for Enquiry from Verifier:
        (f.next_level_role_id = 11 and fm.next_level_role_id = 11) for both create_mode in ('C', 'c') and create_mode in ('O', 'o')

2. On Action on Application:-
    a. On Recommend for Accept on Verify (Process Action -> Send to Verifier):-
        f.next_level_role_id = 15
        fm.next_level_role_id = 15
        f.application_status = 'ENQUIRY_PROCESSED'
        fm.application_status = 'ENQUIRY_PROCESSED'
        f.enquiry_remarks = remarks
        f.enquiry_datetime = now()
        f.enquiry_conducted_by = Auth::id()
        Audit Log: ari.op_type = 'ENQUIRY_PROCESSED'

    b. On Recommend for Reject on Verify (Process Action -> Recommend for Rejection):-
        f.next_level_role_id = 13
        fm.next_level_role_id = 13
        f.application_status = 'ENQUIRY_REJECTED'
        fm.application_status = 'ENQUIRY_REJECTED'
        f.enquiry_remarks = remarks
        f.enquiry_datetime = now()
        f.enquiry_conducted_by = Auth::id()
        Audit Log: ari.op_type = 'ENQUIRY_REJECTED'

    c. Revert / Send Back to Operator (only for create_mode = 'O' or 'o'):-
        f.next_level_role_id = 7
        fm.next_level_role_id = 7
        f.application_status = 'ENQUIRY_REVERTED'
        fm.application_status = 'ENQUIRY_REVERTED'
        f.enquiry_remarks = remarks
        f.enquiry_datetime = now()
        Audit Log: ari.op_type = 'ENQUIRY_REVERTED', reject_revert_cause_id, remarks

    d. Revert / Send Back to Citizen (only for create_mode = 'C' or 'c'):-
        f.next_level_role_id = 5
        fm.next_level_role_id = 5
        f.application_status = 'ENQUIRY_REVERTED'
        fm.application_status = 'ENQUIRY_REVERTED'
        f.enquiry_remarks = remarks
        f.enquiry_datetime = now()
        Audit Log: ari.op_type = 'ENQUIRY_REVERTED', reject_revert_cause_id, remarks

    e. On Edit & Save by Enquiry Officer (enquiry-form-edit):-
        f.next_level_role_id = 15
        fm.next_level_role_id = 15
        f.application_status = 'ENQUIRY_PROCESSED'
        fm.application_status = 'ENQUIRY_PROCESSED'
        f.updated_by = Auth::id()
        f.updated_at = now()
        Audit Log: ari.op_type = 'ENQUIRY_PROCESSED'



Operator 
1. Show Form & Queues on Operator End:
    a. Fresh Draft Office Entry:
        (f.create_mode in ('O', 'o') and fm.create_mode in ('O', 'o') and f.next_level_role_id = 0 and fm.next_level_role_id = 0 and f.application_status = 'DRAFT')
    b. Reverted Applications Queue (Pending for Correction):
        - Reverted from Enquiry: (f.next_level_role_id = 7 and fm.next_level_role_id = 7 and f.application_status = 'ENQUIRY_REVERTED')
        - Reverted from Verifier: (f.next_level_role_id = 8 and fm.next_level_role_id = 8 and f.application_status = 'REVERTED')
        - Reverted from Approver: (f.next_level_role_id = 9 and fm.next_level_role_id = 9 and f.application_status = 'REVERTED')

2. On Final Submit of Application:-
    f.next_level_role_id = 10
    fm.next_level_role_id = 10
    f.application_status = 'SUBMITTED'
    fm.application_status = 'SUBMITTED'
    f.is_agreed = true
    f.updated_by = Auth::id()
    f.updated_at = now()
    Audit Log: ari.op_type = 'SUBMITTED'



Verifier

1. Show Datatables on
[
    1. ALL (Default Queue):
       ((f.next_level_role_id = 15 and fm.next_level_role_id = 15) or (f.next_level_role_id = 13 and fm.next_level_role_id = 13))

    2. Under Enquiry Processed (verifier_filter = 1):
       ((f.next_level_role_id = 15 and fm.next_level_role_id = 15) or (f.next_level_role_id = 13 and fm.next_level_role_id = 13)) and (f.under_enquiry = true or fm.under_enquiry = true)

    3. Enquiry Processed / Pending after Enquiry (verifier_filter = 2):
       ((f.next_level_role_id = 15 and fm.next_level_role_id = 15) or (f.next_level_role_id = 13 and fm.next_level_role_id = 13)) and (f.under_enquiry = false and fm.under_enquiry = false)

    4. Accepted Applications (verifier_filter = 3):
       (f.next_level_role_id = 18 and fm.next_level_role_id = 18)

    5. Paid on June (verifier_filter = 4):
       ((f.next_level_role_id = 15 and fm.next_level_role_id = 15) or (f.next_level_role_id = 13 and fm.next_level_role_id = 13)) and fm.lb_data_paid_apy = true

    6. JKS Entry (verifier_filter = 5):
       ((f.next_level_role_id = 15 and fm.next_level_role_id = 15) or (f.next_level_role_id = 13 and fm.next_level_role_id = 13)) and (f.jks_id is not null and f.jks_id <> '0000000000000')

    7. Physical Application - Earlier (verifier_filter = 6):
       ((f.next_level_role_id = 15 and fm.next_level_role_id = 15) or (f.next_level_role_id = 13 and fm.next_level_role_id = 13)) and (f.jks_id is not null and f.jks_id = '0000000000000')

    8. July Lot / Paid on July (verifier_filter = 7):
       ((f.next_level_role_id = 15 and fm.next_level_role_id = 15) or (f.next_level_role_id = 13 and fm.next_level_role_id = 13)) and fm.july_payment = true

    9. Claim Applications (verifier_filter = 8):
       ((f.next_level_role_id = 15 and fm.next_level_role_id = 15) or (f.next_level_role_id = 13 and fm.next_level_role_id = 13)) and (f.is_claim = true or fm.is_claim = true)

    10. ASAP Applications (verifier_filter = 9):
       ((f.next_level_role_id = 15 and fm.next_level_role_id = 15) or (f.next_level_role_id = 13 and fm.next_level_role_id = 13)) and fm.is_asap = true
]

Member filter applied on Verifier queue:
fm.gender = 'F' and fm.applying_for_annapurna_bhandar = true and (fm.next_level_role_id = 13 or fm.next_level_role_id = 15 or fm.next_level_role_id = 18)

Filter by Application Entry Mode:
a. Online Application: (f.create_mode in ('C', 'c') and fm.create_mode in ('C', 'c'))
b. Offline / Office Application: (f.create_mode in ('O', 'o') and fm.create_mode in ('O', 'o'))

2. On Action on Application (Family-Level Actions - Before Accepting):-
    a. Accept Application (acceptFamily):-
        f.next_level_role_id = 18
        fm.next_level_role_id = 18 (for all non-approved members where next_level_role_id <> 30)
        f.application_status = 'ACCEPTED'
        fm.application_status = 'ACCEPTED'
        Audit Log: ari.op_type = 'ACCEPT_FAMILY'

    b. Reject Application (rejectFamily):-
        f.next_level_role_id = -100
        fm.next_level_role_id = -100 (for all non-approved members where next_level_role_id <> 30)
        f.application_status = 'REJECTED'
        fm.application_status = 'REJECTED'
        f.verification_datetime = now()
        fm.verification_datetime = now()
        Audit Log: ari.op_type = 'REJECT_FAMILY', reject_revert_cause_id, remarks

    c. Revert Application (revertFamily):-
        - Revert to Operator (only for create_mode = 'O' or 'o'):-
            f.next_level_role_id = 8
            fm.next_level_role_id = 8 (for all non-approved members where next_level_role_id <> 30)
            f.application_status = 'REVERTED'
            fm.application_status = 'REVERTED'
            f.detail_status = 99
            fm.detail_status = 99
            f.verification_datetime = now()
            fm.verification_datetime = now()
            Audit Log: ari.op_type = 'REVERT_FAMILY', reject_revert_cause_id, remarks
        - Revert to Citizen (only for create_mode = 'C' or 'c'):-
            f.next_level_role_id = 6
            fm.next_level_role_id = 6 (for all non-approved members where next_level_role_id <> 30)
            f.application_status = 'REVERTED'
            fm.application_status = 'REVERTED'
            f.detail_status = 99
            fm.detail_status = 99
            f.verification_datetime = now()
            fm.verification_datetime = now()
            Audit Log: ari.op_type = 'REVERT_FAMILY', reject_revert_cause_id, remarks

    d. Recommend for Enquiry (recommendForEnquiry):-
        f.next_level_role_id = 11
        fm.next_level_role_id = 11 (for all non-approved members where next_level_role_id <> 30)
        f.application_status = 'SENT_FOR_ENQUIRY'
        fm.application_status = 'SENT_FOR_ENQUIRY'
        f.verification_datetime = now()
        fm.verification_datetime = now()
        Audit Log: ari.op_type = 'RECOMMEND_ENQUIRY', remarks (mandatory, min 5 chars)

    e. Further Verification Required / Held-Up Family (heldupFamily):-
        f.next_level_role_id = 17
        fm.next_level_role_id = 17 (for all non-approved members where next_level_role_id <> 30)
        f.application_status = 'HELDUP_V'
        fm.application_status = 'HELDUP_V'
        f.verification_remarks = remarks (mandatory, min 15 chars)
        fm.verification_remarks = remarks
        Audit Log: ari.op_type = 'HELDUP_V'

3. On Action on Eligible Members (Family Accepted: f.next_level_role_id = 18 and fm.next_level_role_id = 18 and fm.gender = 'F' and fm.applying_for_annapurna_bhandar = true and age >= 25 and age <= 60):-

    a. Verify Eligible Member (Single Action `verify` OR Batch `confirmVerification` with 'Verify'):-
        f.next_level_role_id = 18 (family state maintained)
        fm.next_level_role_id = 20
        fm.application_status = 'VERIFIED'
        fm.verified_by = Auth::id()
        fm.verification_datetime = now()
        fm.not_in_asdd = notInAsdd (boolean)
        fm.appeal_tribunal = appealTribunal (text)
        Audit Log: ari.op_type = 'VERIFY'

    b. Reject Eligible Member (Single Action `reject` OR Batch `confirmVerification` with 'Reject'):-
        f.next_level_role_id = 18 (family state maintained)
        fm.next_level_role_id = -101
        fm.application_status = 'REJECTED'
        fm.verification_datetime = now()
        Audit Log: ari.op_type = 'REJECT', reject_revert_cause_id, remarks

    c. Revert Eligible Member (Single Action `revert` OR Batch `confirmVerification` with 'Revert'):-
        *CRITICAL ENFORCEMENT*: Cannot revert a single member! Reverting any member reverts the whole application (family + all members) back to Operator or Citizen.
        - Revert to Operator (only for create_mode = 'O' or 'o'):-
            f.next_level_role_id = 8
            fm.next_level_role_id = 8 (all family members)
            f.application_status = 'REVERTED'
            fm.application_status = 'REVERTED'
            f.detail_status = 99
            fm.detail_status = 99
            f.verification_datetime = now()
            fm.verification_datetime = now()
            Audit Log: ari.op_type = 'REVERT' (per member) and 'REVERT_FAMILY' (family)
        - Revert to Citizen (only for create_mode = 'C' or 'c'):-
            f.next_level_role_id = 6
            fm.next_level_role_id = 6 (all family members)
            f.application_status = 'REVERTED'
            fm.application_status = 'REVERTED'
            f.detail_status = 99
            fm.detail_status = 99
            f.verification_datetime = now()
            fm.verification_datetime = now()
            Audit Log: ari.op_type = 'REVERT' (per member) and 'REVERT_FAMILY' (family)

4. Post-Approval Action by Verifier (AYRejectApprove Workflow):-
    a. Recommend Approved Member for Rejection (RecomendRejectApprovedBeneficiary):-
        Triggered when an approved member (fm.next_level_role_id = 30) is flagged for exclusion / grievance:
        fm.next_level_role_id = 27
        fm.application_status = 'RECOMMENDED_FOR_REJECTION'
        Audit Log: ari.op_type = 'RECOMMENDED_FOR_REJECTION'



RevertRejectToApprove (Post-Rejection Re-processing Workflow)

NOTE: This is a special administrative workflow for previously rejected members. Access controlled by WorkFlowPermissionHelper::canAyRevertRejectToApprove().

1. Show Datatables on:
    Members where fm.next_level_role_id IN (-100, -101, -102, -103) — previously rejected at any stage.

2. On Action on Rejected Member (Two Actions Available):-

    a. Re-Approve (Approve from Rejected) (Action: approveMember):-
        Triggered when a rejected member needs to be approved retroactively:
        - If selected member is HOF OR if the entire family is rejected (all fm in [-100,-101,-102,-103]):
            Affects all members with applying_for_annapurna_bhandar = true in the family.
            fm.next_level_role_id = 30
            fm.application_status = 'APPROVE'
            f.next_level_role_id = 18
            f.application_status = 'ACCEPTED'
        - If a single non-HOF member only:
            fm.next_level_role_id = 30 (only that member, if applying_for_annapurna_bhandar = true)
            f.next_level_role_id NOT changed
        Audit Log: ari.op_type = 'APPROVE_FROM_REJECT'

    b. Send Back to Enquiry (Action: sendToEnquiry):-
        Triggered when a rejected member's case needs to be re-enquired:
        - Always updates ALL non-deleted family members and the family record:
        f.next_level_role_id = 10
        fm.next_level_role_id = 10 (all non-deleted members)
        f.application_status = 'REJECT TO ENQUIRY'
        fm.application_status = 'REJECT TO ENQUIRY'
        Audit Log: ari.op_type = 'REJECT_TO_ENQUIRY' (one log row per family member)
        Note: Mandatory remarks (min 5 chars) required for action validation.



Approver

1. Show Datatables on 
[
    1. ALL (Default Queue):
       (f.next_level_role_id = 18 and fm.next_level_role_id = 20 and fm.applying_for_annapurna_bhandar = true)

    2. Under Enquiry Verified (approver_filter = 1):
       (f.next_level_role_id = 18 and fm.next_level_role_id = 20 and fm.applying_for_annapurna_bhandar = true) and (f.under_enquiry = true or fm.under_enquiry = true)

    3. Verified / After Verification (approver_filter = 2):
       (f.next_level_role_id = 18 and fm.next_level_role_id = 20 and fm.applying_for_annapurna_bhandar = true) and (f.under_enquiry = false and fm.under_enquiry = false)

    4. Paid on June (approver_filter = 3):
       (f.next_level_role_id = 18 and fm.next_level_role_id = 20 and fm.applying_for_annapurna_bhandar = true) and fm.lb_data_paid_apy = true

    5. JKS Entry (approver_filter = 5):
       (f.next_level_role_id = 18 and fm.next_level_role_id = 20 and fm.applying_for_annapurna_bhandar = true) and (f.jks_id is not null and f.jks_id <> '0000000000000')

    6. Physical Application - Earlier (approver_filter = 6):
       (f.next_level_role_id = 18 and fm.next_level_role_id = 20 and fm.applying_for_annapurna_bhandar = true) and (f.jks_id is not null and f.jks_id = '0000000000000')

    7. July Lot / Paid on July (approver_filter = 7):
       (f.next_level_role_id = 18 and fm.next_level_role_id = 20 and fm.applying_for_annapurna_bhandar = true) and fm.july_payment = true

    8. Claim Applications (approver_filter = 8):
       (f.next_level_role_id = 18 and fm.next_level_role_id = 20 and fm.applying_for_annapurna_bhandar = true) and (f.is_claim = true or fm.is_claim = true)

    9. ASAP Applications (approver_filter = 9):
       (f.next_level_role_id = 18 and fm.next_level_role_id = 20 and fm.applying_for_annapurna_bhandar = true) and fm.is_asap = true
]

2. On Action on Application / Member:-
    a. Approve Member (Single Action `approve`):-
        f.next_level_role_id = 18 (family remains at accepted level)
        fm.next_level_role_id = 30
        fm.application_status = 'APPROVED'
        fm.approved_by = Auth::id()
        fm.approval_datetime = now()
        Audit Log: ari.op_type = 'APPROVE'
        Note: Standard approver action via AnnapurnaYojanaApproverController.
              (Contrast: RevertRejectToApprove.approveMember uses 'APPROVE' — legacy inconsistency)

    b. Bulk Approve Members (List View Action `bulkApproveList`):-
        Executes stored procedure: dbt_apy.ay_approve_member(...) for selected verified members (fm.next_level_role_id = 20)
        fm.next_level_role_id = 30
        fm.application_status = 'APPROVED'
        fm.approved_by = Auth::id()
        fm.approval_datetime = now()
        Audit Log: ari.op_type = 'APPROVE'

    c. Reject Member (Action `reject`):-
        fm.next_level_role_id = -102
        fm.application_status = 'REJECTED'
        fm.approval_datetime = now()
        Audit Log: ari.op_type = 'REJECT', reject_revert_cause_id, remarks
        *(Note: If whole application rejected at Approver level: f.next_level_role_id = -102, fm.next_level_role_id = -102, f.application_status = 'REJECTED', fm.application_status = 'REJECTED')*

    d. Revert Member & Family (Action `revert`):-
        *CRITICAL ENFORCEMENT*: Reverting a verified member at Approver level reverts the WHOLE FAMILY and ALL MEMBERS (excluding already approved role 30).
        - Revert to Operator (only for create_mode = 'O' or 'o'):-
            f.next_level_role_id = 9
            fm.next_level_role_id = 9 (for all non-approved members where next_level_role_id <> 30)
            f.application_status = 'REVERTED'
            fm.application_status = 'REVERTED'
            f.detail_status = 99
            fm.detail_status = 99
            f.approval_datetime = now()
            fm.approval_datetime = now()
            Audit Log: ari.op_type = 'REVERT' (per member) and 'REVERT_FAMILY' (family)
        - Revert to Citizen (only for create_mode = 'C' or 'c'):-
            f.next_level_role_id = 4
            fm.next_level_role_id = 4 (for all non-approved members where next_level_role_id <> 30)
            f.application_status = 'REVERTED'
            fm.application_status = 'REVERTED'
            f.detail_status = 99
            fm.detail_status = 99
            f.approval_datetime = now()
            fm.approval_datetime = now()
            Audit Log: ari.op_type = 'REVERT' (per member) and 'REVERT_FAMILY' (family)

    e. Further Verification Required / Held-Up Family (Action `heldup`):-
        Approver marks whole family and members on hold:
        f.next_level_role_id = 26
        fm.next_level_role_id = 26 (where next_level_role_id <> 30)
        f.application_status = 'HELDUP_A'
        fm.application_status = 'HELDUP_A'
        f.approval_remarks = remarks (mandatory, min 15 chars)
        fm.approval_remarks = remarks
        f.approval_datetime = now()
        fm.approval_datetime = now()
        Audit Log: ari.op_type = 'HELDUP_A'

    g. Further Verification Required / Held-Up Single Member (Action `heldupSingle`):-
        Approver marks single member on hold without altering family status:
        fm.next_level_role_id = 26
        fm.application_status = 'HELDUP_M_A'
        fm.approval_remarks = remarks (mandatory, min 15 chars)
        fm.approval_datetime = now()
        Audit Log: ari.op_type = 'HELDUP_M_A'

    h. Temporary Hold-Up & Rejection for Approved Members (Exclusive Cases - `AYTempHoldApprover` / `AYTempHoldApproverBulk`):-
        Applied to previously approved members (fm.next_level_role_id = 30):
        - Temporary Hold-Up:
            fm.next_level_role_id = 25
            fm.application_status = 'HOLD_' . type_name
            Audit Log: ari.op_type = 'HOLD_' . type_name
        - Reject Hold Member:
            fm.next_level_role_id = -104
            fm.application_status = 'REJECTED_HOLD_A'
            Audit Log: ari.op_type = 'REJECTED_HOLD_A'

    i. Reject Member Recommended for Rejection (`RejectRecomendedBeneficiary` in AYRejectApprove):-
        Applied to members recommended for rejection by verifier (fm.next_level_role_id = 27):
        fm.next_level_role_id = -103
        fm.application_status = 'REJECTED'
        Audit Log: ari.op_type = 'REJECT'



================================================================================
LOT GENERATION & LOT APPROVAL (Batch Payment Workflow)
================================================================================

NOTE: This workflow operates on the cross_check schema (separate from dbt_apy). The
member-level dbt_apy.family_members update is currently DISABLED (commented out in code).
Only the lot master/detail records are updated. This feature is PARTIALLY IMPLEMENTED.

LotGeneration (Livewire: AnnapurnaYojana\LotGeneration)
1. Generate Lot (generateLot):-
    - Calls stored procedure: cross_check.ay_generate_lot(?, ?, ?, ?, ?, ?, ?) or
      cross_check.ay_generate_lot_without_gp_restriction(?, ?, ?, ?, ?, ?) depending on GP restriction flag.
    - Lot size options: 100, 500, 1000 members
    - Lot created in: cross_check.ay_lot_master (lot_status = 0 = Pending)
    - Members in lot stored in: cross_check.ay_lot_details

2. View Pending Lots (viewPendingLots):-
    - Fetches: cross_check.ay_lot_master WHERE lot_status = 0 (Pending, not yet approved)

LotApproval (Livewire: AnnapurnaYojana\LotApproval)
1. Approve Lot (approveLot) [CURRENTLY DISABLED - returns warning toast]:-
    - Would update: cross_check.ay_lot_master SET lot_status = 2, approved_by, approved_at
    - Would update: cross_check.ay_lot_details SET approved_by, approved_at
    - Member dbt_apy.family_members update: DISABLED (commented out)
      (Intended: fm.next_level_role_id = 30, fm.application_status = 'APPROVED')

2. Reject Lot (rejectLot):-
    - Updates: cross_check.ay_lot_master SET lot_status = -3, approved_by, approved_at, approver_remarks
    - Member dbt_apy.family_members update: DISABLED (commented out)
      (Intended: fm.next_level_role_id = 10, fm.application_status = 'REJECTED_APPROVER')
    - Mandatory remarks (min 3 chars)

3. Toggle Reject Member within Lot (toggleRejectMember):-
    - Marks individual members in a lot as rejected/included for payment.

================================================================================
MASTER REFERENCE: ANNAPURNA YOJANA NEXT LEVEL ROLE IDS
================================================================================

Role ID | Description / Status Name                                   | Entity  | Stage / Triggered By
--------+-------------------------------------------------------------+---------+---------------------------------------------------------
0       | Draft Entry                                                 | Both    | Entry Stage (Operator / Citizen draft)
4       | Revert Application / Family (Citizen Entry) - Approver      | Both    | Approver Stage (Reverts whole family to Citizen)
5       | Revert Application / Family (Citizen Entry) - Enquiry       | Both    | Enquiry Stage (Reverts whole family to Citizen)
6       | Revert Application / Family (Citizen Entry) - Verifier      | Both    | Verifier Stage (Reverts whole family to Citizen)
7       | Revert Application / Family (Office Entry) - Enquiry        | Both    | Enquiry Stage (Reverts whole family to Operator)
8       | Revert Application / Family (Office Entry) - Verifier       | Both    | Verifier Stage (Reverts whole family to Operator)
9       | Revert Application / Family (Office Entry) - Approver       | Both    | Approver Stage (Reverts whole family to Operator)
10      | Final Submission                                            | Both    | Entry Stage (Initial final submission by Citizen/Operator)
11      | Recommend for Enquiry - Verifier                            | Both    | Verifier Stage (Sends back to Enquiry Officer)
13      | Recommend for Rejection - Enquiry Officer                   | Both    | Enquiry Officer (Forwarded to Verifier as rejected)
15      | Recommend for Accept - Enquiry Officer                      | Both    | Enquiry Officer (Forwarded to Verifier as accepted)
17      | Verifier Hold / Further Verification Required - Verifier    | Both    | Verifier Stage (Verifier puts family on hold)
18      | Accept Application / Family Details - Verifier              | Both    | Verifier Stage (Family accepted; enables member action)
20      | Verify Member (Annapurna) Accepted Application - Verifier   | Member  | Verifier Stage (Member verified; forwarded to Approver)
25      | Hold Member / Family Details - Approver (Exclusive Cases)   | Member  | Approver Stage (Temp hold on approved member)
26      | Approver Hold / Further Verification Required - Approver    | Member  | Approver Stage (Approver puts family/member on hold)
27      | Recommend Approved Member (Annapurna) For Rejection         | Member  | Verifier Stage (Post-approval verifier re-check)
30      | Approve Member (Annapurna) - Approver                       | Member  | Approver Stage (Final approval)
-100    | Reject Application / Family                                 | Both    | Verifier Stage (Whole family rejection)
-101    | Reject Accepted Member (Annapurna)                          | Member  | Verifier Stage (Individual member rejected)
-102    | Reject Member (Annapurna)                                   | Member  | Approver Stage (Approver rejects verified member)
-103    | Reject Approved / Recommended Member (Annapurna)            | Member  | Approver Stage (Rejects member from role 27)
-104    | Reject Hold Member (Exclusive Cases)                        | Member  | Approver Stage (Rejects member from role 25)
-106    | Reject Priority Re-verification                             | Member  | Priority Process Stage (Exclusion Re-verification)



================================================================================
BUSINESS RULES & IMPLEMENTATION GUIDELINES (ON WORDS)
================================================================================

On Words;

1. Fix the Workflow on my project 
Update the column as defined in the workflow and update the status of the application as defined in the workflow:
Update f.next_level_role_id, fm.next_level_role_id, f.application_status, fm.application_status, f.detail_status, fm.detail_status, f.verification_datetime, fm.verification_datetime, f.approval_datetime, fm.approval_datetime, verified_by, approved_by, and write an immutable audit trail entry in dbt_apy.accept_reject_info for every operation.

2. Show the datatables as defined in the workflow
Show the datatables as defined in the workflow for each role based on the next_level_role_id, under_enquiry, create_mode, and the user's assigned LGD jurisdiction (District, Block/Municipality, GP/Ward).

3. Keep the pre existing code same.
Maintain backwards compatibility with existing features including LGD jurisdiction validation (GlobalUpdateCheckHelper), multi-tenant database routing via ZoneDB, document view endpoints, export services, and exception rollback handling.

4. On Action on Application there is already 3 button "Accept application", "Reject application" and "Revert to Operator"  now  add one more button "Recommend for Enquiry" and on click of that button update the next_level_role_id and application_status as defined in the workflow.
The family action bar provides:
- "Accept the Application": f.next_level_role_id = 18, fm.next_level_role_id = 18, f.application_status = 'ACCEPTED', fm.application_status = 'ACCEPTED'
- "Reject the Application": f.next_level_role_id = -100, fm.next_level_role_id = -100, f.application_status = 'REJECTED', fm.application_status = 'REJECTED'
- "Revert the Application": f.next_level_role_id = 8 (Office) or 6 (Citizen), fm.next_level_role_id = 8 or 6, f.application_status = 'REVERTED', fm.application_status = 'REVERTED'
- "Recommend for Enquiry": f.next_level_role_id = 11, fm.next_level_role_id = 11, f.application_status = 'SENT_FOR_ENQUIRY', fm.application_status = 'SENT_FOR_ENQUIRY'
- "Further verification required" (Held-Up): f.next_level_role_id = 17, fm.next_level_role_id = 17, f.application_status = 'HELDUP_V', fm.application_status = 'HELDUP_V'

5. Reset  is same as alreadt developed in the previous code, 
Reset clears search input, resets active filter tab, clears create_mode filter, clears the persisted session LGD filters ('apy_verifier_lgd_filter' or 'apy_approver_lgd_filter'), restores default login LGD credentials from lgd_session, and resets pagination to page 1.

6. On accept the application the the eligible members will show "Process Action" Button , After choosing the action "Verify"/"Reject"/"Revert" on the eligible member . the verifier will click on the "Confrim Verification" button (Already Delevloped on the code).
Eligible members qualify when:
- fm.applying_for_annapurna_bhandar = true
- fm.gender = 'F'
- Age between 25 and 60 years
- fm.next_level_role_id = 18
Each eligible member row provides action options ("Verify", "Reject", "Revert") with reasons and ASDD/Tribunal fields. Clicking "Confirm Verification" opens the batch confirmation modal.

7. On click of "Confirm Verification" button update the next_level_role_id and application_status as defined in the workflow for each action "Verify"/"Reject"/"Revert".
- "Verify": fm.next_level_role_id = 20, fm.application_status = 'VERIFIED', f.next_level_role_id remains 18
- "Reject": fm.next_level_role_id = -101, fm.application_status = 'REJECTED', f.next_level_role_id remains 18
- "Revert": f.next_level_role_id = 8 (Office) or 6 (Citizen), fm.next_level_role_id = 8 or 6, application_status = 'REVERTED'

8. in a scinario where there is more tan one eligile member and the verifier has taken "Revert" action on one member and "Verify" action on other member then , on Confrimation show message "You Cannot Revert a single application, on Confrimation whole application (with) will be reverted" and update the next_level_role_id and application_status  for both families and family members as defined in the workflow.
The Alpine.js verification component detects mixed revert selections (hasMixedRevert()) and presents the warning banner:
"You Cannot Revert a single application, on Confrimation whole application (with) will be reverted"
On submission, the backend confirmVerification method cascades revert status to the family and all family members:
- For Office entry (create_mode = 'O'/'o'): f.next_level_role_id = 8, fm.next_level_role_id = 8, application_status = 'REVERTED', detail_status = 99
- For Citizen entry (create_mode = 'C'/'c'): f.next_level_role_id = 6, fm.next_level_role_id = 6, application_status = 'REVERTED', detail_status = 99

9. On approver End show only the  members whose next_level_role_id is fm.next_level_role_id = 20 on click of "Approve" or "Reject" or "Revert" update the next_level_role_id and application_status as defined in the workflow.
Approver datatable and details view display verified members with fm.next_level_role_id = 20:
- "Approve": fm.next_level_role_id = 30, fm.application_status = 'APPROVED' (single or bulk approve calling dbt_apy.ay_approve_member)
- "Reject": fm.next_level_role_id = -102, fm.application_status = 'REJECTED'
- "Revert": f.next_level_role_id = 9 (Office) or 4 (Citizen), fm.next_level_role_id = 9 or 4, application_status = 'REVERTED' (whole family + all non-approved members reverted)
- "Held-Up": fm.next_level_role_id = 26, fm.application_status = 'HELDUP_M_A' (single member) or f & fm role 26 'HELDUP_A' (whole family)

10. On Verifier End give 3 tab and on active tab show the datatables as defined in the workflow , Dont load the datatable at once 
The Verifier Queue supports lazy-loaded, tab-based queries without fetching all records simultaneously:
- Tab 1: Under Enquiry Processed ((f.next_level_role_id = 15 and fm.next_level_role_id = 15) or (f.next_level_role_id = 13 and fm.next_level_role_id = 13)) and (f.under_enquiry = true or fm.under_enquiry = true)
- Tab 2: Office/Citizen Pending for Verification after Enquiry ((f.next_level_role_id = 15 and fm.next_level_role_id = 15) or (f.next_level_role_id = 13 and fm.next_level_role_id = 13)) and (f.under_enquiry = false and fm.under_enquiry = false)
- Tab 3: Office Pending for Verification after Accepting the Application (f.next_level_role_id = 18 and fm.next_level_role_id = 18)
Reverted applications tab is excluded from standard Verifier and Approver queues and routed to dedicated reverted workflow views.

11. "Recommend for Enquiry" button is already there on the Verifier End and on click of the button update the next_level_role_id and application_status as defined in the workflow for Both (Create_mode = 'C') or (Create_mode = 'O' or Create_mode = 'o').
The button initiates recommendForEnquiry in AnnapurnaYojanaVerificationController:
- f.next_level_role_id = 11
- fm.next_level_role_id = 11 (where next_level_role_id <> 30)
- f.application_status = 'SENT_FOR_ENQUIRY'
- fm.application_status = 'SENT_FOR_ENQUIRY'
- f.verification_datetime = now()
- fm.verification_datetime = now()
- Mandatory remarks (min 5 chars) logged into dbt_apy.accept_reject_info with op_type = 'RECOMMEND_ENQUIRY'
Handles both online (create_mode in ('C', 'c')) and offline (create_mode in ('O', 'o')) entries identically.
