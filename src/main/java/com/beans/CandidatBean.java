package com.beans;

import org.primefaces.model.file.UploadedFile;

import com.dao.CandidatDAO;
import com.models.Candidat;
import com.models.Etablissement;
import com.models.Parcours;
import com.util.PdfExporterUtil;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.dao.EtablissementDAO;
import com.dao.ParcoursDAO;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


@ManagedBean(name = "candidatBean")
@SessionScoped
public class CandidatBean {
    private static final Logger logger = Logger.getLogger(CandidatBean.class.getName());
    private Candidat candidat = new Candidat();
    private List<Candidat> candidats; // Liste des candidats
    private List<Etablissement> etablissements; // Liste des établissements
    private List<Parcours> parcoursDisponibles; // Liste des parcours disponibles
    private List<Parcours> parcoursChoisis; // Parcours choisis par le candidat
    private UploadedFile diplomeBacFile;
    private UploadedFile diplomeLicenceFile;
    private UploadedFile releve1File;
    private UploadedFile releve2File;
    private UploadedFile releve3File;
    private UploadedFile releve4File;
    private UploadedFile releve5File;
    private UploadedFile releve6File;
    private static final String UPLOAD_DIR = "C:\\Users\\HP\\Desktop\\selectionMasterD\\uploads"; // Répertoire de stockage des fichiers

 // Liste des filières disponibles
    private List<String> filiers = Arrays.asList("IRM", "MIAGE", "DA2I", "MI","MA", "DI","IG","GLSI","RT","STATISTIQUE","FINANCE");
    private List<String> etat_validations = Arrays.asList("Premier Session" ,"Deuxieme Session");

    // DAOs
    private EtablissementDAO etablissementDAO = new EtablissementDAO();
    private ParcoursDAO parcoursDAO = new ParcoursDAO();
    private CandidatDAO candidatDAO = new CandidatDAO();

    // Getters et Setters
    public Candidat getCandidat() { return candidat; }
    public void setCandidat(Candidat candidat) { this.candidat = candidat; }

    public UploadedFile getDiplomeBacFile() { return diplomeBacFile; }
    public void setDiplomeBacFile(UploadedFile diplomeBacFile) { this.diplomeBacFile = diplomeBacFile; }

    public UploadedFile getDiplomeLicenceFile() { return diplomeLicenceFile; }
    public void setDiplomeLicenceFile(UploadedFile diplomeLicenceFile) { this.diplomeLicenceFile = diplomeLicenceFile; }

    public UploadedFile getReleve1File() { return releve1File; }
    public void setReleve1File(UploadedFile releve1File) { this.releve1File = releve1File; }

    public UploadedFile getReleve2File() { return releve2File; }
    public void setReleve2File(UploadedFile releve2File) { this.releve2File = releve2File; }

    public UploadedFile getReleve3File() { return releve3File; }
    public void setReleve3File(UploadedFile releve3File) { this.releve3File = releve3File; }

    public UploadedFile getReleve4File() { return releve4File; }
    public void setReleve4File(UploadedFile releve4File) { this.releve4File = releve4File; }

    public UploadedFile getReleve5File() { return releve5File; }
    public void setReleve5File(UploadedFile releve5File) { this.releve5File = releve5File; }

    public UploadedFile getReleve6File() { return releve6File; }
    public void setReleve6File(UploadedFile releve6File) { this.releve6File = releve6File; }

    public List<Etablissement> getEtablissements() {
        if (etablissements == null) {
            etablissements = etablissementDAO.obtenirTousLesEtablissements();
        }
        return etablissements;
    }
    
    public List<String> getFiliers() {
        return filiers;
    }
    
    public List<String> getEtat_validations() {
        return etat_validations;
    }

    public List<Parcours> getParcoursDisponibles() {
        if (parcoursDisponibles == null) {
            parcoursDisponibles = parcoursDAO.findAll();
        }
        return parcoursDisponibles;
    }

    public List<Parcours> getParcoursChoisis() { return parcoursChoisis; }
    public void setParcoursChoisis(List<Parcours> parcoursChoisis) { this.parcoursChoisis = parcoursChoisis; }

    // Initialisation des listes
    @PostConstruct
    public void init() {
        etablissements = etablissementDAO.obtenirTousLesEtablissements();
        parcoursDisponibles = parcoursDAO.findAll();
        
        this.calculBonnus();
        this.calculMalus();
    }

    // Méthode pour soumettre le formulaire
    public String soumettre() {
        try {
            // Associer les parcours choisis au candidat
            if (parcoursChoisis == null || parcoursChoisis.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Veuillez sélectionner au moins un parcours."));
                return null;
            }
            if (parcoursChoisis != null && !parcoursChoisis.isEmpty()) {
                StringBuilder parcoursStr = new StringBuilder();
                for (Parcours p : parcoursChoisis) {
                    parcoursStr.append(p.getNom()).append(", ");
                }
                candidat.setParcours(parcoursStr.toString().trim());
            }

            // Sauvegarder les fichiers
            if (diplomeBacFile != null) {
                System.out.println("🔹 Fichier reçu: " + diplomeBacFile.getFileName() + ", Taille: " + diplomeBacFile.getSize() + " bytes");
                
                if (diplomeBacFile.getSize() > 0) {
                    String fileName = "diplome_bac_" + candidat.getNumero_I_ou_bac().replaceAll("\\s+", "_")+ "_" + candidat.getNom().replaceAll("\\s+", "_") + ".pdf";
                    saveFile(diplomeBacFile, fileName);
                    candidat.setDiplomeBacPath(Paths.get(UPLOAD_DIR, fileName).toString());
                } else {
                    System.out.println("⚠️ Le fichier est vide !");
                }
            } else {
                System.out.println("❌ Aucun fichier reçu !");
            }
            
            if (diplomeLicenceFile != null && diplomeLicenceFile.getSize() > 0) {
                String fileName = "diplome_licence_"+ candidat.getNumero_I_ou_bac().replaceAll("\\s+", "_")+ "_" + candidat.getNom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(diplomeLicenceFile, fileName);
                candidat.setDiplomeLicencePath(Paths.get(UPLOAD_DIR, fileName).toString());
            }
            if (releve1File != null) {
                String fileName = "releve1_" + candidat.getNumero_I_ou_bac().replaceAll("\\s+", "_")+ "_" + candidat.getNom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(releve1File, fileName);
                candidat.setReleve1Path(Paths.get(UPLOAD_DIR, fileName).toString());
            }
            if (releve2File != null) {
                String fileName = "releve2_" + candidat.getNumero_I_ou_bac().replaceAll("\\s+", "_")+ "_" + candidat.getNom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(releve2File, fileName);
                candidat.setReleve2Path(Paths.get(UPLOAD_DIR, fileName).toString());
            }
            if (releve3File != null) {
                String fileName = "releve3_" + candidat.getNumero_I_ou_bac().replaceAll("\\s+", "_")+ "_" + candidat.getNom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(releve3File, fileName);
                candidat.setReleve3Path(Paths.get(UPLOAD_DIR, fileName).toString());
            }
            if (releve4File != null) {
                String fileName = "releve4_" + candidat.getNumero_I_ou_bac().replaceAll("\\s+", "_")+ "_" + candidat.getNom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(releve4File, fileName);
                candidat.setReleve4Path(Paths.get(UPLOAD_DIR, fileName).toString());
            }
            if (releve5File != null) {
                String fileName = "releve5_" + candidat.getNumero_I_ou_bac().replaceAll("\\s+", "_")+ "_" + candidat.getNom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(releve5File, fileName);
                candidat.setReleve5Path(Paths.get(UPLOAD_DIR, fileName).toString());
            }
            if (releve6File != null) {
                String fileName = "releve6_" + candidat.getNumero_I_ou_bac().replaceAll("\\s+", "_")+ "_" + candidat.getNom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(releve6File, fileName);
                candidat.setReleve6Path(Paths.get(UPLOAD_DIR, fileName).toString());
            }

            // Enregistrer le candidat dans la base de données
            candidatDAO.ajouterCandidat(candidat);
            
            candidat = new Candidat(); // Réinitialisation du candidat

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Candidature enregistrée."));
            return "success";
        } catch (IOException e) {
            logger.severe("Erreur lors de la sauvegarde des fichiers: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur lors de la sauvegarde des fichiers."));
            return null;
        } catch (Exception e) {
            logger.severe("Erreur inattendue: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur inattendue."));
            return null;
        }
    }

    // Méthode pour modifier un candidat
    public String modifierCandidat() {
        try {
            candidatDAO.modifierCandidat(candidat); 
            candidat = new Candidat(); // Réinitialisation du candidat
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Les informations du candidat ont été mises à jour avec succès."));
            return null; 
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur est survenue lors de la mise à jour."));
            return null;
        }
    }



    // Méthode pour supprimer un candidat
    public void supprimerCandidat(int id) {
        try {
            candidatDAO.supprimerCandidat(id);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Candidature supprimée."));
        } catch (Exception e) {
            logger.severe("Erreur lors de la suppression du candidat: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur lors de la suppression du candidat."));
        }
    }

    // Méthode pour charger un candidat par son ID
    public String chargerCandidat(int id) {
        candidat = candidatDAO.obtenirCandidatParId(id);
        if (candidat != null) {
            return "modifierCandidat"; // Rediriger vers la page de modification
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Candidat non trouvé."));
            return null;
        }
    }

    // Méthode pour sauvegarder un fichier
    private void saveFile(UploadedFile file, String fileName) throws IOException {
        System.out.println("🔹 Commence l'enregistrement du fichier: " + fileName);
        
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("✅ Dossier créé: " + UPLOAD_DIR);
        }

        Path filePath = uploadPath.resolve(fileName);
        
        try (InputStream input = file.getInputStream()) {
            Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Fichier enregistré avec succès : " + filePath.toString());
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de l'enregistrement du fichier: " + e.getMessage());
            throw e;
        }
    }


    
    public List<Candidat> getCandidats() {
        if (candidats == null) {
            candidats = candidatDAO.obtenirTousLesCandidats();
        }
        return candidats;
    }

    // Méthode pour obtenir tous les candidats
    public List<Candidat> obtenirTousLesCandidats() {
        return candidatDAO.obtenirTousLesCandidats();
    }
    

    
    public void annulerModification() {
        candidat = new Candidat(); // Réinitialisation du candidat
        parcoursChoisis = new ArrayList<>(); // Réinitialiser les parcours choisis
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Modification annulée."));
       
    }
    
    public void calculBonnus() {
    	 for (Candidat c : obtenirTousLesCandidats()) {
             double BONUS = 0;
             int annes = (int) (c.getAnneeLicence() - c.getAnneeBac()) ;
             if (annes == 3) {
            	 BONUS += 1;
             }
             
             if (c.getEtat_validation1().equals("Premier Session")) {
            	 BONUS += 0.4;
             }
             
             if (c.getEtat_validation2().equals("Premier Session")) {
            	 BONUS += 0.4;
             }
             
             if (c.getEtat_validation3().equals("Premier Session")) {
            	 BONUS += 0.4;
             }
             
             if (c.getEtat_validation4().equals("Premier Session")) {
            	 BONUS += 0.4;
             }
             
             if (c.getEtat_validation5().equals("Premier Session")) {
            	 BONUS += 0.4;
             }
             if (c.getEtat_validation6().equals("Premier Session")) {
            	 BONUS += 0.4;
             }
             
             c.setBonnus( BONUS);
             
             candidatDAO.modifierCandidat(c);
    	 }
    	
    }
    
    public void calculMalus() {
   	 for (Candidat c : obtenirTousLesCandidats()) {
            double MALUS = 0;
            
            int annes = (int) (c.getAnneeLicence()) ;
            while(annes < 2022) {
                MALUS -= 0.25;
                annes++; 
            }
            	
            
            c.setMalus( MALUS);
            
            candidatDAO.modifierCandidat(c);
   	 }
   	
   }
    
    public void calculerEtTrierParMC() {
        for (Candidat c : obtenirTousLesCandidats()) {
            double MNS = (c.getMoyenneReleve1() + c.getMoyenneReleve2() + 
                          c.getMoyenneReleve3() + c.getMoyenneReleve4() + 
                          c.getMoyenneReleve5() + c.getMoyenneReleve6()) / 6.0;

            double BS = c.getBonnus();
            double MS = c.getMalus();
            double P = c.getPenalite();

            double MC = MNS + BS + MS - P;
            c.setMoyenneClassement(MC);
            candidatDAO.modifierCandidat(c);
        }
        
        candidats = (List<Candidat>) candidatsParParcours();
        
    }
    
    

    public Map<String, Map<String, Map<String, List<Candidat>>>> candidatsParParcours() {
        if (candidatDAO == null || parcoursDAO == null) {
            throw new IllegalStateException("DAO non initialisé !");
        }

        Map<String, Map<String, Map<String, List<Candidat>>>> candidatsOrganises = new HashMap<>();
        Map<String, Integer> quotasGlobal = new HashMap<>();
        Map<String, Integer> candidatsCountParParcours = new HashMap<>();
        Map<String, Map<String, Integer>> quotasSpecialites = new HashMap<>();
        Map<String, Map<String, Integer>> candidatsCountParSpecialite = new HashMap<>();
        List<Parcours> parcoursList = parcoursDAO.findAll();
        if (parcoursList == null) {
            parcoursList = new ArrayList<>(); // Éviter NullPointerException
        }

        // Remplir la map des quotas globaux et initialiser les compteurs
        for (Parcours p : parcoursList) {
            if (p != null && p.getNom() != null) {
                quotasGlobal.put(p.getNom(), p.getQuota());
                candidatsCountParParcours.put(p.getNom(), 0); // Initialiser le compteur de candidats pour ce parcours

                // Définition des quotas par spécialité pour chaque parcours
                if (p.getNom().equals("SI")) {
                    Map<String, Integer> siQuotas = new HashMap<>();
                    siQuotas.put("DA2I", 7); // Quota pour DA2I
                    siQuotas.put("MIAGE", 5); // Quota pour MIAGE
                    siQuotas.put("MI", 3); // Quota pour MI
                    siQuotas.put("MA", 0); // Quota pour MA
                    quotasSpecialites.put(p.getNom(), siQuotas);
                } else if (p.getNom().equals("RSC")) {
                    Map<String, Integer> rscQuotas = new HashMap<>();
                    rscQuotas.put("DA2I", 4); // Quota pour DA2I
                    rscQuotas.put("MIAGE", 3); // Quota pour MIAGE
                    rscQuotas.put("MI", 2); // Quota pour MI
                    rscQuotas.put("MA", 0); // Quota pour MA
                    quotasSpecialites.put(p.getNom(), rscQuotas);
                } else if (p.getNom().equals("SDD")) {
                    Map<String, Integer> sddQuotas = new HashMap<>();
                    sddQuotas.put("DA2I", 0); // Quota pour DA2I
                    sddQuotas.put("MIAGE", 0); // Quota pour MIAGE
                    sddQuotas.put("MI", 12); // Quota pour MI
                    sddQuotas.put("MA", 5); // Quota pour MA
                    quotasSpecialites.put(p.getNom(), sddQuotas);
                } else {
                    // Par défaut, aucun quota pour les autres parcours
                    quotasSpecialites.put(p.getNom(), new HashMap<>());
                }
            }
        }

        // Récupération de tous les candidats
        List<Candidat> candidats = candidatDAO.obtenirTousLesCandidats();
        if (candidats == null) {
            candidats = new ArrayList<>();
        }

        // Trier les candidats par moyenne de classement (décroissant)
        candidats.sort(Comparator.comparingDouble(Candidat::getMoyenneClassement).reversed());

        for (Candidat c : candidats) {
            if (c == null || c.getParcours() == null || c.getParcours().trim().isEmpty()) {
                continue;
            }

            String[] parcoursPref = c.getParcours().split(",");
            boolean placed = false;

            // Essayer de placer le candidat dans ses préférences
            for (String parcours : parcoursPref) {
                parcours = parcours.trim();
                if (parcours.isEmpty() || !quotasGlobal.containsKey(parcours)) continue;

                String etablissement = c.getEtablissement().getNom();
                String specialite = c.getFilier();

                // Vérifier si le quota global du parcours est respecté
                int quotaGlobal = quotasGlobal.get(parcours);
                int totalCandidatsDansParcours = candidatsCountParParcours.get(parcours);

                if (totalCandidatsDansParcours >= quotaGlobal) {
                    continue; // Passer au parcours suivant si le quota global est atteint
                }

                // Initialisation des structures si nécessaire
                candidatsOrganises
                    .computeIfAbsent(parcours, k -> new HashMap<>())
                    .computeIfAbsent(etablissement, k -> new HashMap<>())
                    .computeIfAbsent(specialite, k -> new ArrayList<>());

                candidatsCountParSpecialite
                    .computeIfAbsent(parcours, k -> new HashMap<>())
                    .putIfAbsent(specialite, 0);

                int quotaSpecialite = quotasSpecialites.getOrDefault(parcours, new HashMap<>()).getOrDefault(specialite, Integer.MAX_VALUE);
                int totalCandidatsDansSpecialite = candidatsCountParSpecialite.get(parcours).get(specialite);

                // Vérifier s'il y a encore de la place dans cette spécialité
                if (totalCandidatsDansSpecialite < quotaSpecialite) {
                    candidatsOrganises.get(parcours).get(etablissement).get(specialite).add(c);
                    candidatsCountParSpecialite.get(parcours).put(specialite, totalCandidatsDansSpecialite + 1);
                    candidatsCountParParcours.put(parcours, totalCandidatsDansParcours + 1);
                    placed = true;

                    // Définition du statut
                    c.setStatut("retenu");
                    break; // Stopper dès que le candidat est placé
                } else if (totalCandidatsDansSpecialite == quotaSpecialite) {
                    // Si le quota est atteint, on met le candidat en attente
                    candidatsOrganises.get(parcours).get(etablissement).get(specialite).add(c);
                    candidatsCountParSpecialite.get(parcours).put(specialite, totalCandidatsDansSpecialite + 1);
                    candidatsCountParParcours.put(parcours, totalCandidatsDansParcours + 1);
                    placed = true;

                    // Définition du statut
                    c.setStatut("en attente");
                    break; // Stopper dès que le candidat est placé
                }
            }

            // Si le candidat n'a pas été placé dans ses préférences, chercher le parcours le plus proche
            if (!placed) {
                String closestParcours = findClosestParcours(candidatsOrganises, quotasSpecialites, parcoursPref);
                if (closestParcours != null) {
                    String etablissement = c.getEtablissement().getNom();
                    String specialite = c.getFilier();

                    // Vérifier si le quota global du parcours est respecté
                    int quotaGlobal = quotasGlobal.get(closestParcours);
                    int totalCandidatsDansParcours = candidatsCountParParcours.get(closestParcours);

                    if (totalCandidatsDansParcours < quotaGlobal) {
                        candidatsOrganises.computeIfAbsent(closestParcours, k -> new HashMap<>())
                            .computeIfAbsent(etablissement, k -> new HashMap<>())
                            .computeIfAbsent(specialite, k -> new ArrayList<>())
                            .add(c);

                        int totalCandidatsDansSpecialite = candidatsCountParSpecialite.get(closestParcours).getOrDefault(specialite, 0);
                        candidatsCountParSpecialite.get(closestParcours).put(specialite, totalCandidatsDansSpecialite + 1);
                        candidatsCountParParcours.put(closestParcours, totalCandidatsDansParcours + 1);

                        int quotaSpecialite = quotasSpecialites.getOrDefault(closestParcours, new HashMap<>()).getOrDefault(specialite, Integer.MAX_VALUE);
                        if (totalCandidatsDansSpecialite < quotaSpecialite) {
                            c.setStatut("retenu");
                        } else if (totalCandidatsDansSpecialite == quotaSpecialite) {
                            c.setStatut("en attente");
                        } else {
                            c.setStatut("non retenu");
                        }
                    } else {
                        c.setStatut("non retenu");
                    }
                } else {
                    // Si aucun parcours n'est disponible, on ajoute le candidat avec le statut "non retenu"
                    String etablissement = c.getEtablissement().getNom();
                    String specialite = c.getFilier();

                    candidatsOrganises.computeIfAbsent(parcoursPref[0], k -> new HashMap<>()) // Ajouter au premier parcours préféré
                        .computeIfAbsent(etablissement, k -> new HashMap<>())
                        .computeIfAbsent(specialite, k -> new ArrayList<>())
                        .add(c);

                    c.setStatut("non retenu");
                }
            }

            // Mise à jour du candidat en base
            candidatDAO.modifierCandidat(c);
        }

        return candidatsOrganises;
    }
    
    
    private String findClosestParcours(Map<String, Map<String, Map<String, List<Candidat>>>> candidatsOrganises,
            Map<String, Map<String, Integer>> quotasSpecialites,
            String[] parcoursPref) {
		String closestParcours = null;
		int maxEspaceRestant = -1;
		
		for (String parcours : parcoursPref) {
			parcours = parcours.trim();
			if (parcours.isEmpty() || !quotasSpecialites.containsKey(parcours)) continue;
			
			int totalQuota = quotasSpecialites.get(parcours).values().stream().mapToInt(Integer::intValue).sum();
			int currentCount = candidatsOrganises.getOrDefault(parcours, new HashMap<>())
			.values().stream()
			.flatMap(e -> e.values().stream())
			.mapToInt(List::size)
			.sum();
			
			int espaceRestant = totalQuota - currentCount;
			if (espaceRestant > maxEspaceRestant) {
				maxEspaceRestant = espaceRestant;
				closestParcours = parcours;
			}
		}
			
		return closestParcours;
	}

    
    

    public void exportAllToPDF() {
        Map<String, Map<String, Map<String, List<Candidat>>>> candidatsParParcoursEtEtablissement = candidatsParParcours();
        PdfExporterUtil.exportAllToPDF(candidatsParParcoursEtEtablissement, "selection_master.pdf");
    }
   
}




//public Map<String, Map<String, List<Candidat>>> candidatsParParcours() {
//// Vérification des dépendances
//if (candidatDAO == null) {
//  throw new IllegalStateException("candidatDAO n'est pas initialisé");
//}
//if (parcoursDAO == null) {
//  throw new IllegalStateException("parcoursDAO n'est pas initialisé");
//}
//
//// Initialisation des structures de données
//Map<String, Map<String, List<Candidat>>> candidatsParParcoursEtEtablissement = new HashMap<>();
//Map<String, Integer> quotas = new HashMap<>();
//Map<String, Integer> candidatsCountParParcours = new HashMap<>(); // Pour compter le nombre total de candidats par parcours
//
//// Récupération des parcours et de leurs quotas a partir de parcoursDAO.findAll()
//List<Parcours> parcoursList = parcoursDAO.findAll();
//if (parcoursList == null) {
//  parcoursList = new ArrayList<>(); // Éviter NullPointerException
//}
//
//// Remplir la map des quotas
//for (Parcours p : parcoursList) {
//  if (p != null && p.getNom() != null) {
//      quotas.put(p.getNom(), p.getQuota());
//      candidatsCountParParcours.put(p.getNom(), 0); // Initialiser le compteur de candidats pour ce parcours
//  }
//}
//
//// Récupération de tous les candidats
//List<Candidat> candidats = candidatDAO.obtenirTousLesCandidats();
//if (candidats == null) {
//  candidats = new ArrayList<>(); // Éviter NullPointerException
//}
//
//// Tri des candidats par moyenne de classement (décroissant)
//candidats.sort(Comparator.comparingDouble(Candidat::getMoyenneClassement).reversed());
//
//// Répartition des candidats dans les parcours et les établissements
//for (Candidat c : candidats) {
//  if (c == null || c.getParcours() == null || c.getParcours().trim().isEmpty()) {
//      continue; // Ignorer les candidats sans parcours valide
//  }
//
//  String[] parcoursPref = c.getParcours().split(",");
//  boolean placed = false;
//
//  // Essayer de placer le candidat dans son premier choix disponible
//  for (String pref : parcoursPref) {
//      if (pref == null || pref.trim().isEmpty()) {
//          continue; // Ignorer les préférences vides
//      }
//
//      pref = pref.trim(); // Nettoyer les espaces
//
//      // Initialiser la map des établissements pour ce parcours
//      if (!candidatsParParcoursEtEtablissement.containsKey(pref)) {
//          candidatsParParcoursEtEtablissement.put(pref, new HashMap<>());
//      }
//
//      // Initialiser la liste des candidats pour cet établissement dans ce parcours
//      String etablissement = c.getEtablissement().getNom();
//      if (!candidatsParParcoursEtEtablissement.get(pref).containsKey(etablissement)) {
//          candidatsParParcoursEtEtablissement.get(pref).put(etablissement, new ArrayList<>());
//      }
//
//      // Vérifier si le quota est respecté
//      List<Candidat> candidatsDansParcours = candidatsParParcoursEtEtablissement.get(pref).get(etablissement);
//      int quota = quotas.getOrDefault(pref, Integer.MAX_VALUE);
//
//      // Compter le nombre total de candidats dans ce parcours
//      int totalCandidatsDansParcours = candidatsCountParParcours.getOrDefault(pref, 0);
//
//      if (totalCandidatsDansParcours < quota) {
//          candidatsDansParcours.add(c);
//          placed = true;
//
//          // Mettre à jour le compteur total de candidats pour ce parcours
//          candidatsCountParParcours.put(pref, totalCandidatsDansParcours + 1);
//
//          // Définir le statut du candidat
//          if (totalCandidatsDansParcours + 1 <= quota) {
//              c.setStatut("retenu");
//          } else if (totalCandidatsDansParcours + 1 == quota + 1) {
//              c.setStatut("en attente");
//          } else {
//              c.setStatut("non retenu");
//          }
//
//          break; // Candidat placé, passer au suivant
//      }
//  }
//
//  // Si le candidat n'a pas été placé, le mettre dans le parcours le plus proche du quota
//  if (!placed && parcoursPref.length > 0) {
//      String closestParcours = findClosestParcours(candidatsParParcoursEtEtablissement, quotas, parcoursPref);
//      if (closestParcours != null) {
//          String etablissement = c.getEtablissement().getNom();
//          List<Candidat> candidatsDansParcours = candidatsParParcoursEtEtablissement.get(closestParcours).get(etablissement);
//          candidatsDansParcours.add(c);
//
//          // Mettre à jour le compteur total de candidats pour ce parcours
//          int totalCandidatsDansParcours = candidatsCountParParcours.getOrDefault(closestParcours, 0);
//          candidatsCountParParcours.put(closestParcours, totalCandidatsDansParcours + 1);
//
//          // Définir le statut du candidat
//          int quota = quotas.getOrDefault(closestParcours, Integer.MAX_VALUE);
//          if (totalCandidatsDansParcours + 1 <= quota) {
//              c.setStatut("retenu");
//          } else if (totalCandidatsDansParcours + 1 == quota + 1) {
//              c.setStatut("en attente");
//          } else {
//              c.setStatut("non retenu");
//          }
//      }
//  }
//  candidatDAO.modifierCandidat(c);
//}
//
//return candidatsParParcoursEtEtablissement;
//}
//
///**
//* Trouve le parcours le plus proche du quota pour un candidat.
//*/
//private String findClosestParcours(Map<String, Map<String, List<Candidat>>> candidatsParParcoursEtEtablissement, 
//  Map<String, Integer> quotas, 
//  String[] parcoursPref) {
//	String closestParcours = null;
//	int minDifference = Integer.MAX_VALUE;
//	
//	for (String pref : parcoursPref) {
//		if (pref == null || pref.trim().isEmpty()) {
//			continue; // تجاهل الخيارات الفارغة
//		}
//		
//		pref = pref.trim(); // تنظيف النص
//		
//		// التأكد من أن المسار موجود في الحصص
//		if (!quotas.containsKey(pref)) {
//			continue; // تجاهل المسارات التي ليس لها حصص محددة
//		}
//		
//		// حساب عدد المرشحين في هذا المسار
//		int size = 0;
//		if (candidatsParParcoursEtEtablissement.containsKey(pref)) {
//			for (List<Candidat> candidats : candidatsParParcoursEtEtablissement.get(pref).values()) {
//				size += candidats.size();
//			}
//		}
//			
//		// حساب الفرق بين عدد المرشحين والحصة
//		int quota = quotas.get(pref);
//		int difference = size - quota;
//		
//		// العثور على المسار الأقرب إلى الحصة
//		if (difference < minDifference) {
//			minDifference = difference;
//			closestParcours = pref;
//		}
//	}
//		
//		return closestParcours;
//	}



//public Map<String, Map<String, Map<String, List<Candidat>>>> candidatsParParcours() {
//if (candidatDAO == null || parcoursDAO == null) {
//  throw new IllegalStateException("DAO non initialisé !");
//}
//
//Map<String, Map<String, Map<String, List<Candidat>>>> candidatsOrganises = new HashMap<>();
//Map<String, Map<String, Integer>> quotasSpecialites = new HashMap<>();
//Map<String, Map<String, Integer>> candidatsCountParSpecialite = new HashMap<>();
//
//
//// Définition des quotas
//Map<String, Integer> siQuotas = new HashMap<>();
//siQuotas.put("DA2I", 7);
//siQuotas.put("MIAGE", 5);
//siQuotas.put("MI", 3);
//siQuotas.put("MA", 0);
//quotasSpecialites.put("SI", siQuotas);
//
//Map<String, Integer> rscQuotas = new HashMap<>();
//rscQuotas.put("DA2I", 4);
//rscQuotas.put("MIAGE", 3);
//rscQuotas.put("MI", 2);
//rscQuotas.put("MA", 0);
//quotasSpecialites.put("RSC", rscQuotas);
//
//Map<String, Integer> sddQuotas = new HashMap<>();
//sddQuotas.put("DA2I", 0);
//sddQuotas.put("MIAGE", 0);
//sddQuotas.put("MI", 12);
//sddQuotas.put("MA", 5);
//quotasSpecialites.put("SDD", sddQuotas);
//
//// Récupération de tous les candidats
//List<Candidat> candidats = candidatDAO.obtenirTousLesCandidats();
//if (candidats == null) {
//  candidats = new ArrayList<>();
//}
//
//// Trier les candidats par MC (décroissant)
//candidats.sort(Comparator.comparingDouble(Candidat::getMoyenneClassement).reversed());
//
//for (Candidat c : candidats) {
//  if (c == null || c.getParcours() == null || c.getParcours().trim().isEmpty()) {
//      continue;
//  }
//
//  String[] parcoursPref = c.getParcours().split(",");
//  boolean placed = false;
//
//  // Essayer de placer le candidat dans ses préférences
//  for (String parcours : parcoursPref) {
//      parcours = parcours.trim();
//      if (parcours.isEmpty()) continue;
//
//      String etablissement = c.getEtablissement().getNom();
//      String specialite = c.getFilier();
//
//      // Initialisation des structures si nécessaire
//      candidatsOrganises
//          .computeIfAbsent(parcours, k -> new HashMap<>())
//          .computeIfAbsent(etablissement, k -> new HashMap<>())
//          .computeIfAbsent(specialite, k -> new ArrayList<>());
//
//      candidatsCountParSpecialite
//          .computeIfAbsent(parcours, k -> new HashMap<>())
//          .putIfAbsent(specialite, 0);
//
//      int quotaSpecialite = quotasSpecialites.getOrDefault(parcours, new HashMap<>()).getOrDefault(specialite, Integer.MAX_VALUE);
//      int totalCandidatsDansSpecialite = candidatsCountParSpecialite.get(parcours).get(specialite);
//
//      // Vérifier s'il y a encore de la place dans ce parcours et spécialité
//      if (totalCandidatsDansSpecialite < quotaSpecialite) {
//          candidatsOrganises.get(parcours).get(etablissement).get(specialite).add(c);
//          candidatsCountParSpecialite.get(parcours).put(specialite, totalCandidatsDansSpecialite + 1);
//          placed = true;
//
//          // Définition du statut
//          c.setStatut("retenu");
//          break; // Stopper dès que le candidat est placé
//      } else if (totalCandidatsDansSpecialite == quotaSpecialite) {
//          // Si le quota est atteint, on met le candidat en attente
//          candidatsOrganises.get(parcours).get(etablissement).get(specialite).add(c);
//          candidatsCountParSpecialite.get(parcours).put(specialite, totalCandidatsDansSpecialite + 1);
//          placed = true;
//
//          // Définition du statut
//          c.setStatut("en attente");
//          break; // Stopper dès que le candidat est placé
//      }
//  }
//
//  // Si le candidat n'a pas été placé dans ses préférences, chercher le parcours le plus proche
//  if (!placed) {
//      String closestParcours = findClosestParcours(candidatsOrganises, quotasSpecialites, parcoursPref);
//      if (closestParcours != null) {
//          String etablissement = c.getEtablissement().getNom();
//          String specialite = c.getFilier();
//
//          candidatsOrganises.computeIfAbsent(closestParcours, k -> new HashMap<>())
//              .computeIfAbsent(etablissement, k -> new HashMap<>())
//              .computeIfAbsent(specialite, k -> new ArrayList<>())
//              .add(c);
//
//          int totalCandidatsDansSpecialite = candidatsCountParSpecialite.get(closestParcours).getOrDefault(specialite, 0);
//          candidatsCountParSpecialite.get(closestParcours).put(specialite, totalCandidatsDansSpecialite + 1);
//
//          int quotaSpecialite = quotasSpecialites.getOrDefault(closestParcours, new HashMap<>()).getOrDefault(specialite, Integer.MAX_VALUE);
//          if (totalCandidatsDansSpecialite < quotaSpecialite) {
//              c.setStatut("retenu");
//          } else if (totalCandidatsDansSpecialite == quotaSpecialite) {
//              c.setStatut("en attente");
//          } else {
//              c.setStatut("non retenu");
//          }
//      } else {
//          // Si aucun parcours n'est disponible, on ajoute le candidat avec le statut "non retenu"
//          String etablissement = c.getEtablissement().getNom();
//          String specialite = c.getFilier();
//
//          candidatsOrganises.computeIfAbsent(parcoursPref[0], k -> new HashMap<>()) // Ajouter au premier parcours préféré
//              .computeIfAbsent(etablissement, k -> new HashMap<>())
//              .computeIfAbsent(specialite, k -> new ArrayList<>())
//              .add(c);
//
//          c.setStatut("non retenu");
//      }
//  }
//
//  // Mise à jour du candidat en base
//  candidatDAO.modifierCandidat(c);
//}
//
//return candidatsOrganises;
//}
//
//private String findClosestParcours(Map<String, Map<String, Map<String, List<Candidat>>>> candidatsOrganises,
//  Map<String, Map<String, Integer>> quotasSpecialites,
//  String[] parcoursPref) {
//String closestParcours = null;
//int maxEspaceRestant = -1;
//
//for (String parcours : parcoursPref) {
//parcours = parcours.trim();
//if (parcours.isEmpty() || !quotasSpecialites.containsKey(parcours)) continue;
//
//int totalQuota = quotasSpecialites.get(parcours).values().stream().mapToInt(Integer::intValue).sum();
//int currentCount = candidatsOrganises.getOrDefault(parcours, new HashMap<>())
//.values().stream()
//.flatMap(e -> e.values().stream())
//.mapToInt(List::size)
//.sum();
//
//int espaceRestant = totalQuota - currentCount;
//if (espaceRestant > maxEspaceRestant) {
//maxEspaceRestant = espaceRestant;
//closestParcours = parcours;
//}
//}
//
//return closestParcours;
//}
//

