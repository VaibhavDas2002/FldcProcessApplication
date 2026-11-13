package workflow;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import responseHandler.ApiResponse;
import security.UserPrincipal;
import entity.ApyLotDetails;
import entity.ApyLotMaster;
import service.LotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/workflow/lots")
@Tag(name = "Batch Payment / Lot Workflow", description = "Lot generation and approval operations (cross_check schema)")
@PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
public class LotController {

    private final LotService lotService;

    public LotController(LotService lotService) {
        this.lotService = lotService;
    }

    @GetMapping("/pending")
    @Operation(summary = "View Pending Lots", description = "Fetches lots with lot_status = 0 (Pending)")
    public ResponseEntity<ApiResponse<List<ApyLotMaster>>> viewPendingLots(
            @RequestParam(required = false) Integer districtCode) {
        List<ApyLotMaster> lots = lotService.viewPendingLots(districtCode);
        return ResponseEntity.ok(ApiResponse.success("Pending lots retrieved", lots));
    }

    @PostMapping("/{lotId}/approve")
    @Operation(summary = "Approve Lot", description = "Approves lot master and detail records (lot_status = 2)")
    public ResponseEntity<ApiResponse<ApyLotMaster>> approveLot(
            @PathVariable Long lotId,
            @AuthenticationPrincipal UserPrincipal user) {
        Long userId = user != null ? user.getUserId() : null;
        ApyLotMaster approvedLot = lotService.approveLot(lotId, userId);
        return ResponseEntity.ok(ApiResponse.success("Lot approved successfully", approvedLot));
    }

    @PostMapping("/{lotId}/reject")
    @Operation(summary = "Reject Lot", description = "Rejects lot master record (lot_status = -3, min 3 chars remarks)")
    public ResponseEntity<ApiResponse<ApyLotMaster>> rejectLot(
            @PathVariable Long lotId,
            @RequestParam String remarks,
            @AuthenticationPrincipal UserPrincipal user) {
        Long userId = user != null ? user.getUserId() : null;
        ApyLotMaster rejectedLot = lotService.rejectLot(lotId, remarks, userId);
        return ResponseEntity.ok(ApiResponse.success("Lot rejected", rejectedLot));
    }

    @PostMapping("/details/{detailId}/toggle-reject")
    @Operation(summary = "Toggle Reject Member in Lot")
    public ResponseEntity<ApiResponse<ApyLotDetails>> toggleRejectMember(@PathVariable Long detailId) {
        ApyLotDetails detail = lotService.toggleRejectMember(detailId);
        return ResponseEntity.ok(ApiResponse.success("Member lot status toggled", detail));
    }
}

