package de.awtools.registration.user;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

@Entity(name = "Privilege")
@Table(name = "PRIVILEGE")
public class PrivilegeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NaturalId
    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Set<RoleEntity> roles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PrivilegeEntity)) {
            return false;
        }

        PrivilegeEntity other = (PrivilegeEntity) o;
        return Objects.equals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    public static class PrivilegeBuilder {
        private PrivilegeBuilder() {
        }

        public static PrivilegeEntity of(String name) {
            PrivilegeEntity privilege = new PrivilegeEntity();
            privilege.setName(name);
            return privilege;
        }
    }

}
