package ma.location.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "administrateurs")
public class Administrateur extends Utilisateur {
}
