package com.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "candidats", catalog = "selectionmaster")
public class Candidat implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "annee_bac", nullable = false)
    private String anneeBac;

    @Column(name = "annee_licence", nullable = false)
    private String anneeLicence;

    @Column(name = "moyenne_bac", nullable = false)
    private String moyenneBac;

    @Column(name = "moyenne_licence", nullable = false)
    private String moyenneLicence;

    private String parcours;

    @Column(name = "diplome_bac_path")
    private String diplomeBacPath;

    @Column(name = "diplome_licence_path")
    private String diplomeLicencePath;

    @Column(name = "releve1_path")
    private String releve1Path;

    @Column(name = "moyenneReleve1")
    private double moyenneReleve1;

    @Column(name = "releve2_path")
    private String releve2Path;

    @Column(name = "moyenneReleve2")
    private double moyenneReleve2;

    @Column(name = "releve3_path")
    private String releve3Path;

    @Column(name = "moyenneReleve3")
    private double moyenneReleve3;

    @Column(name = "releve4_path")
    private String releve4Path;

    @Column(name = "moyenneReleve4")
    private double moyenneReleve4;

    @Column(name = "releve5_path")
    private String releve5Path;

    @Column(name = "moyenneReleve5")
    private double moyenneReleve5;

    @Column(name = "releve6_path")
    private String releve6Path;

    @Column(name = "moyenneReleve6")
    private double moyenneReleve6;
    
	@Column(name = "bonnus", nullable = true)
    private double bonnus;
	
	@Column(name = "malus", nullable = true)
    private double malus;
	
	@Column(name = "moyenneClassement", nullable = true)
	private double moyenneClassement;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "candidat_parcours",
        joinColumns = @JoinColumn(name = "candidat_id"),
        inverseJoinColumns = @JoinColumn(name = "parcours_id")
    )
    private Set<Parcours> parcoursChoisis = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "etablissement_id", nullable = false)
    private Etablissement etablissement;
    
    @Column(name = "statut")
    private String statut;

    // Getters et Setters
    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getAnneeBac() { return anneeBac; }
    public void setAnneeBac(String anneeBac) { this.anneeBac = anneeBac; }

    public String getAnneeLicence() { return anneeLicence; }
    public void setAnneeLicence(String anneeLicence) { this.anneeLicence = anneeLicence; }

    public String getMoyenneBac() { return moyenneBac; }
    public void setMoyenneBac(String moyenneBac) { this.moyenneBac = moyenneBac; }

    public String getMoyenneLicence() { return moyenneLicence; }
    public void setMoyenneLicence(String moyenneLicence) { this.moyenneLicence = moyenneLicence; }

    public String getParcours() { return parcours; }
    public void setParcours(String parcours) { this.parcours = parcours; }

    public String getDiplomeBacPath() { return diplomeBacPath; }
    public void setDiplomeBacPath(String diplomeBacPath) { this.diplomeBacPath = diplomeBacPath; }

    public String getDiplomeLicencePath() { return diplomeLicencePath; }
    public void setDiplomeLicencePath(String diplomeLicencePath) { this.diplomeLicencePath = diplomeLicencePath; }

    public String getReleve1Path() { return releve1Path; }
    public void setReleve1Path(String releve1Path) { this.releve1Path = releve1Path; }

    public String getReleve2Path() { return releve2Path; }
    public void setReleve2Path(String releve2Path) { this.releve2Path = releve2Path; }

    public String getReleve3Path() { return releve3Path; }
    public void setReleve3Path(String releve3Path) { this.releve3Path = releve3Path; }

    public String getReleve4Path() { return releve4Path; }
    public void setReleve4Path(String releve4Path) { this.releve4Path = releve4Path; }

    public String getReleve5Path() { return releve5Path; }
    public void setReleve5Path(String releve5Path) { this.releve5Path = releve5Path; }

    public String getReleve6Path() { return releve6Path; }
    public void setReleve6Path(String releve6Path) { this.releve6Path = releve6Path; }

    public double getMoyenneReleve1() { return moyenneReleve1; }
    public void setMoyenneReleve1(double moyenneReleve1) { this.moyenneReleve1 = moyenneReleve1; }

    public double getMoyenneReleve2() { return moyenneReleve2; }
    public void setMoyenneReleve2(double moyenneReleve2) { this.moyenneReleve2 = moyenneReleve2; }

    public double getMoyenneReleve3() { return moyenneReleve3; }
    public void setMoyenneReleve3(double moyenneReleve3) { this.moyenneReleve3 = moyenneReleve3; }

    public double getMoyenneReleve4() { return moyenneReleve4; }
    public void setMoyenneReleve4(double moyenneReleve4) { this.moyenneReleve4 = moyenneReleve4; }

    public double getMoyenneReleve5() { return moyenneReleve5; }
    public void setMoyenneReleve5(double moyenneReleve5) { this.moyenneReleve5 = moyenneReleve5; }

    public double getMoyenneReleve6() { return moyenneReleve6; }
    public void setMoyenneReleve6(double moyenneReleve6) { this.moyenneReleve6 = moyenneReleve6; }

    public Set<Parcours> getParcoursChoisis() { return parcoursChoisis; }
    public void setParcoursChoisis(Set<Parcours> parcoursChoisis) { this.parcoursChoisis = parcoursChoisis; }
    
    public Etablissement getEtablissement() {
		return etablissement;
	}
	public void setEtablissement(Etablissement etablissement) {
		this.etablissement = etablissement;
	}
	public double getBonnus() {
		return bonnus;
	}
	public void setBonnus(double bonnus) {
		this.bonnus = bonnus;
	}
	public double getMalus() {
		return malus;
	}
	public void setMalus(double malus) {
		this.malus = malus;
	}

    public double getMoyenneClassement() {
        return moyenneClassement;
    }

    public void setMoyenneClassement(double moyenneClassement) {
        this.moyenneClassement = moyenneClassement;
    }
	
}