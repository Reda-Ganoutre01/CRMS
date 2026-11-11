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
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Column(nullable = false, unique = true, length = 20)
    private String cin;

    @Column(length = 20)
    private String telephone;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String adresse;

    @Column(name = "numero_permis", length = 30)
    private String numeroPermis;

    @OneToMany(mappedBy = "client")
    private List<Contrat> contrats = new ArrayList<>();

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}