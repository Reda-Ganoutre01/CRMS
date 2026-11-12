package ma.location.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "clients")
public class Client extends Utilisateur {

    @Column(nullable = false, unique = true, length = 20)
    private String cin;

    @Column(length = 200)
    private String adresse;

    @Column(name = "numero_permis", length = 30)
    private String numeroPermis;

    @OneToMany(mappedBy = "client")
    private List<Contrat> contrats = new ArrayList<>();

    @OneToMany(mappedBy = "client")
    private List<Reservation> reservations = new ArrayList<>();

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}