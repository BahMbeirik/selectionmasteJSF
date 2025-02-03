package com.models;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "parcours", catalog = "selectionmaster")
public class Parcours implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "quota", nullable = false)
    private int quota;

    @ManyToMany(mappedBy = "parcoursChoisis")
    private Set<Candidat> candidats;

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getQuota() { return quota; }
    public void setQuota(int quota) { this.quota = quota; }

    public Set<Candidat> getCandidats() { return candidats; }
    public void setCandidats(Set<Candidat> candidats) { this.candidats = candidats; }
}