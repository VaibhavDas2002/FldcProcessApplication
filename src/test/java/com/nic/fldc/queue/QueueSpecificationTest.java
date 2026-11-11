package com.nic.fldc.queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import com.nic.fldc.workflow.dto.request.QueueFilterRequest;
import com.nic.fldc.workflow.entity.Family;
import com.nic.fldc.workflow.entity.FamilyMember;
import com.nic.fldc.workflow.repository.specification.ApproverQueueSpecification;
import com.nic.fldc.workflow.repository.specification.EnquiryQueueSpecification;
import com.nic.fldc.workflow.repository.specification.RejectedMemberQueueSpecification;
import com.nic.fldc.workflow.repository.specification.VerifierQueueSpecification;

public class QueueSpecificationTest {

    @Test
    @DisplayName("EnquiryQueueSpecification creates non-null Specification for all 5 tabs")
    void testEnquiryQueueSpecificationCreation() {
        for (int tab = 1; tab <= 5; tab++) {
            QueueFilterRequest request = QueueFilterRequest.builder()
                    .tabIndex(tab)
                    .districtCode(304)
                    .blockCode(1201)
                    .gpCode(4501)
                    .search("APP123")
                    .build();

            Specification<Family> spec = EnquiryQueueSpecification.filter(request, 304);
            assertNotNull(spec);
        }
    }

    @Test
    @DisplayName("VerifierQueueSpecification creates non-null Specification for tabs and create_mode")
    void testVerifierQueueSpecificationCreation() {
        for (int tab : new int[]{1, 2, 3, 5, 6, 8}) {
            QueueFilterRequest request = QueueFilterRequest.builder()
                    .tabIndex(tab)
                    .createMode("O")
                    .districtCode(304)
                    .search("JKS987")
                    .build();

            Specification<Family> spec = VerifierQueueSpecification.filter(request, 304);
            assertNotNull(spec);
        }
    }

    @Test
    @DisplayName("ApproverQueueSpecification creates non-null Specification for member queues")
    void testApproverQueueSpecificationCreation() {
        for (int tab = 0; tab <= 9; tab++) {
            QueueFilterRequest request = QueueFilterRequest.builder()
                    .tabIndex(tab)
                    .districtCode(304)
                    .build();

            Specification<FamilyMember> spec = ApproverQueueSpecification.filter(request, 304);
            assertNotNull(spec);
        }
    }

    @Test
    @DisplayName("RejectedMemberQueueSpecification creates non-null Specification for reprocessing")
    void testRejectedMemberQueueSpecificationCreation() {
        QueueFilterRequest request = QueueFilterRequest.builder()
                .districtCode(304)
                .search("REJECT123")
                .build();

        Specification<FamilyMember> spec = RejectedMemberQueueSpecification.filter(request, 304);
        assertNotNull(spec);
    }

    @Test
    @DisplayName("QueueFilterRequest pagination and defaults check")
    void testQueueFilterRequestDefaults() {
        QueueFilterRequest request = QueueFilterRequest.builder().build();
        assertEquals(0, request.getPage());
        assertEquals(10, request.getSize());
        assertEquals("createdAt", request.getSortBy());
        assertEquals("DESC", request.getSortDirection());
    }
}
