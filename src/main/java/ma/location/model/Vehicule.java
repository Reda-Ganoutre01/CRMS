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
@Table(name = "vehicules")
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String immatriculation;

    @Column(nullable = false, length = 50)
    private String marque;

    @Column(nullable = false, length = 50)
    private String modele;

    private int annee;

    @Column(length = 20)
    private String carburant;

    private int kilometrage;

    @Column(name = "prix_jour", nullable = false)
    private double prixJour;

    private boolean disponible = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutVehicule statut = StatutVehicule.DISPONIBLE;

    @OneToMany(mappedBy = "vehicule")
    private List<Contrat> contrats = new ArrayList<>();

    @OneToMany(mappedBy = "vehicule")
    private List<Reservation> reservations = new ArrayList<>();

    public String getLibelle() {
        return marque + " " + modele + " (" + immatriculation + ")";
    }
}