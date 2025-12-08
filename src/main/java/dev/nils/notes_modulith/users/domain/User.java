package dev.nils.notes_modulith.users.domain;

import dev.nils.notes_modulith.common.AbstractBaseEntity;
import jakarta.persistence.*;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "users",
        indexes = {@Index(name = "IDX_USER_EMAIL", columnList = "email")})
@SequenceGenerator(name = "sq_users", sequenceName = "sq_users", allocationSize = 10)
public class User extends AbstractBaseEntity {

    @Serial
    private static final long serialVersionUID = -8008792371855432846L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_users")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, length = 250)
    private String email;

    @Column(name = "full_name", length = 500)
    private String fullName;
}
