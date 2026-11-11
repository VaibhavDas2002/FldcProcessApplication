package com.nic.fldc.workflow.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Pagination and filtering parameters for queue queries")
public class QueueFilterRequest {

    @Schema(description = "Active tab index / filter selector", example = "1")
    private Integer tabIndex;

    @Schema(description = "Application entry mode filter: 'C' for Citizen, 'O' for Office", example = "O")
    private String createMode;

    @Schema(description = "LGD District code filter", example = "304")
    private Integer districtCode;

    @Schema(description = "LGD Block / Municipality code filter", example = "2145")
    private Integer blockCode;

    @Schema(description = "LGD Gram Panchayat / Ward code filter", example = "10982")
    private Integer gpCode;

    @Schema(description = "Free-text search query (matches family serial, application id, jks id)", example = "APY2024")
    private String search;

    @Schema(description = "Page number (0-based)", example = "0")
    private Integer page = 0;

    @Schema(description = "Page size", example = "10")
    private Integer size = 10;

    @Schema(description = "Sort property", example = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "Sort direction (ASC or DESC)", example = "DESC")
    private String sortDirection = "DESC";

    public QueueFilterRequest() {
    }

    public QueueFilterRequest(Integer tabIndex, String createMode, Integer districtCode, Integer blockCode,
                              Integer gpCode, String search, Integer page, Integer size, String sortBy, String sortDirection) {
        this.tabIndex = tabIndex;
        this.createMode = createMode;
        this.districtCode = districtCode;
        this.blockCode = blockCode;
        this.gpCode = gpCode;
        this.search = search;
        this.page = page != null ? page : 0;
        this.size = size != null ? size : 10;
        this.sortBy = sortBy != null ? sortBy : "createdAt";
        this.sortDirection = sortDirection != null ? sortDirection : "DESC";
    }

    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public String getCreateMode() {
        return createMode;
    }

    public void setCreateMode(String createMode) {
        this.createMode = createMode;
    }

    public Integer getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(Integer districtCode) {
        this.districtCode = districtCode;
    }

    public Integer getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(Integer blockCode) {
        this.blockCode = blockCode;
    }

    public Integer getGpCode() {
        return gpCode;
    }

    public void setGpCode(Integer gpCode) {
        this.gpCode = gpCode;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public static QueueFilterRequestBuilder builder() {
        return new QueueFilterRequestBuilder();
    }

    public static class QueueFilterRequestBuilder {
        private Integer tabIndex;
        private String createMode;
        private Integer districtCode;
        private Integer blockCode;
        private Integer gpCode;
        private String search;
        private Integer page = 0;
        private Integer size = 10;
        private String sortBy = "createdAt";
        private String sortDirection = "DESC";

        public QueueFilterRequestBuilder tabIndex(Integer tabIndex) {
            this.tabIndex = tabIndex;
            return this;
        }

        public QueueFilterRequestBuilder createMode(String createMode) {
            this.createMode = createMode;
            return this;
        }

        public QueueFilterRequestBuilder districtCode(Integer districtCode) {
            this.districtCode = districtCode;
            return this;
        }

        public QueueFilterRequestBuilder blockCode(Integer blockCode) {
            this.blockCode = blockCode;
            return this;
        }

        public QueueFilterRequestBuilder gpCode(Integer gpCode) {
            this.gpCode = gpCode;
            return this;
        }

        public QueueFilterRequestBuilder search(String search) {
            this.search = search;
            return this;
        }

        public QueueFilterRequestBuilder page(Integer page) {
            this.page = page;
            return this;
        }

        public QueueFilterRequestBuilder size(Integer size) {
            this.size = size;
            return this;
        }

        public QueueFilterRequestBuilder sortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public QueueFilterRequestBuilder sortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
            return this;
        }

        public QueueFilterRequest build() {
            return new QueueFilterRequest(tabIndex, createMode, districtCode, blockCode, gpCode, search, page, size, sortBy, sortDirection);
        }
    }
}
