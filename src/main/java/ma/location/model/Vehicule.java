package ma.location.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "vehicule")
    private List<Contrat> contrats = new ArrayList<>();

    public Vehicule() {
    }

    public String getLibelle() {
        return marque + " " + modele + " (" + immatriculation + ")";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
    public String getCarburant() { return carburant; }
    public void setCarburant(String carburant) { this.carburant = carburant; }
    public int getKilometrage() { return kilometrage; }
    public void setKilometrage(int kilometrage) { this.kilometrage = kilometrage; }
    public double getPrixJour() { return prixJour; }
    public void setPrixJour(double prixJour) { this.prixJour = prixJour; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public List<Contrat> getContrats() { return contrats; }
    public void setContrats(List<Contrat> contrats) { this.contrats = contrats; }
}