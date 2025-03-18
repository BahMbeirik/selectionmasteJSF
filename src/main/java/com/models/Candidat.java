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
    
    @Column(name = "filier", nullable = false)
    private String filier;
    
    @Column(name = "etat_validation1", nullable = false)
    private String etat_validation1;
    
    @Column(name = "etat_validation2", nullable = false)
    private String etat_validation2;
    
    @Column(name = "etat_validation3", nullable = false)
    private String etat_validation3;
    
    @Column(name = "etat_validation4", nullable = false)
    private String etat_validation4;
    
    @Column(name = "etat_validation5", nullable = false)
    private String etat_validation5;
    
    @Column(name = "etat_validation6", nullable = false)
    private String etat_validation6;
    
    @Column(name = "numero_I_ou_bac", nullable = false)
    private String numero_I_ou_bac;

    @Column(name = "annee_bac", nullable = false)
    private int anneeBac;

	@Column(name = "annee_licence", nullable = false)
    private int anneeLicence;

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

    @Column(name = "moyenne_releve1")
    private double moyenneReleve1;

    @Column(name = "releve2_path")
    private String releve2Path;

    @Column(name = "moyenne_releve2")
    private double moyenneReleve2;

    @Column(name = "releve3_path")
    private String releve3Path;

    @Column(name = "moyenne_releve3")
    private double moyenneReleve3;

    @Column(name = "releve4_path")
    private String releve4Path;

    @Column(name = "moyenne_releve4")
    private double moyenneReleve4;

    @Column(name = "releve5_path")
    private String releve5Path;

    @Column(name = "moyenne_releve5")
    private double moyenneReleve5;

    @Column(name = "releve6_path")
    private String releve6Path;

    @Column(name = "moyenne_releve6")
    private double moyenneReleve6;
    
	@Column(name = "bonnus", nullable = true)
    private double bonnus;
	
	@Column(name = "malus", nullable = true)
    private double malus;
	
	@Column(name = "penalite", nullable = true)
    private double penalite;
	
	@Column(name = "moyenne_classement", nullable = true)
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
    
    public String getEtat_validation1() {
		return etat_validation1;
	}

	public void setEtat_validation1(String etat_validation1) {
		this.etat_validation1 = etat_validation1;
	}

	public String getEtat_validation2() {
		return etat_validation2;
	}

	public void setEtat_validation2(String etat_validation2) {
		this.etat_validation2 = etat_validation2;
	}

	public String getEtat_validation3() {
		return etat_validation3;
	}

	public void setEtat_validation3(String etat_validation3) {
		this.etat_validation3 = etat_validation3;
	}

	public String getEtat_validation4() {
		return etat_validation4;
	}

	public void setEtat_validation4(String etat_validation4) {
		this.etat_validation4 = etat_validation4;
	}

	public String getEtat_validation5() {
		return etat_validation5;
	}

	public void setEtat_validation5(String etat_validation5) {
		this.etat_validation5 = etat_validation5;
	}

	public String getEtat_validation6() {
		return etat_validation6;
	}

	public void setEtat_validation6(String etat_validation6) {
		this.etat_validation6 = etat_validation6;
	}
	
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

    public int getAnneeBac() { return anneeBac; }
    public void setAnneeBac(int anneeBac) { this.anneeBac = anneeBac; }

    public int getAnneeLicence() { return anneeLicence; }
    public void setAnneeLicence(int anneeLicence) { this.anneeLicence = anneeLicence; }

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

	public String getFilier() {
		return filier;
	}

	public void setFilier(String filier) {
		this.filier = filier;
	}

	public String getNumero_I_ou_bac() {
		return numero_I_ou_bac;
	}

	public void setNumero_I_ou_bac(String numero_I_ou_bac) {
		this.numero_I_ou_bac = numero_I_ou_bac;
	}

	public double getPenalite() {
		return penalite;
	}

	public void setPenalite(double penalite) {
		this.penalite = penalite;
	}
    
    
	
}