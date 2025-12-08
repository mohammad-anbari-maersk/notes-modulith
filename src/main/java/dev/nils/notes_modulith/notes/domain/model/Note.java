package dev.nils.notes_modulith.notes.domain.model;

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
@Table(name = "notes")
@SequenceGenerator(name = "sq_notes", sequenceName = "sq_notes", allocationSize = 25)
public class Note extends AbstractBaseEntity {

    @Serial
    private static final long serialVersionUID = 268411417620116096L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_notes")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", length = 1000)
    private String title;

    @Column(name = "content", length = 3000)
    private String content;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
