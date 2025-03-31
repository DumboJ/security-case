package cn.dumboj.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_user")
@Entity
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;
    @Column(name = "password", nullable = false, length = 80)
    @JsonIgnore
    private String password;
    @Column(name = "email", unique = true, length = 50, nullable = false)
    private String email;
//    默认未过期
    @Column(name = "account_non_expired", nullable = false)
    private boolean accountNonExpired=true;
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;
    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;
    @Column(name = "credentials_non_expired", nullable = false)
    private boolean credentialsNonExpired = true;
    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "t_user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
            , inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> authorities;

}
