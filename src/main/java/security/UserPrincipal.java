package security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

    private Long userId;
    private String username;
    private List<? extends GrantedAuthority> authorities;
    private Integer districtCode;
    private Integer blockCode;
    private Integer gpCode;
    private Long localBodyCode;
    private String office;
    private String clientIp;

    public UserPrincipal() {
    }

    public UserPrincipal(Long userId, String username, List<? extends GrantedAuthority> authorities,
                         Integer districtCode, Integer blockCode, Integer gpCode,
                         Long localBodyCode, String office, String clientIp) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
        this.districtCode = districtCode;
        this.blockCode = blockCode;
        this.gpCode = gpCode;
        this.localBodyCode = localBodyCode;
        this.office = office;
        this.clientIp = clientIp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
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

    public Long getLocalBodyCode() {
        return localBodyCode;
    }

    public void setLocalBodyCode(Long localBodyCode) {
        this.localBodyCode = localBodyCode;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    @Override
    public String getPassword() {
        return ""; // Resource server does not store credentials
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean hasRole(WorkflowRole role) {
        if (authorities == null || role == null) return false;
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase(role.name()));
    }

    public static UserPrincipalBuilder builder() {
        return new UserPrincipalBuilder();
    }

    public static class UserPrincipalBuilder {
        private Long userId;
        private String username;
        private List<? extends GrantedAuthority> authorities;
        private Integer districtCode;
        private Integer blockCode;
        private Integer gpCode;
        private Long localBodyCode;
        private String office;
        private String clientIp;

        public UserPrincipalBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserPrincipalBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserPrincipalBuilder authorities(List<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public UserPrincipalBuilder districtCode(Integer districtCode) {
            this.districtCode = districtCode;
            return this;
        }

        public UserPrincipalBuilder blockCode(Integer blockCode) {
            this.blockCode = blockCode;
            return this;
        }

        public UserPrincipalBuilder gpCode(Integer gpCode) {
            this.gpCode = gpCode;
            return this;
        }

        public UserPrincipalBuilder localBodyCode(Long localBodyCode) {
            this.localBodyCode = localBodyCode;
            return this;
        }

        public UserPrincipalBuilder office(String office) {
            this.office = office;
            return this;
        }

        public UserPrincipalBuilder clientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public UserPrincipal build() {
            return new UserPrincipal(userId, username, authorities, districtCode, blockCode, gpCode, localBodyCode, office, clientIp);
        }
    }
}
