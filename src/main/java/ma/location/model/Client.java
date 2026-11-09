package ma.location.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public Client() {
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getNumeroPermis() { return numeroPermis; }
    public void setNumeroPermis(String numeroPermis) { this.numeroPermis = numeroPermis; }
    public List<Contrat> getContrats() { return contrats; }
    public void setContrats(List<Contrat> contrats) { this.contrats = contrats; }
}