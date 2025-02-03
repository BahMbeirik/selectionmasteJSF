package com.beans;

import org.primefaces.model.file.UploadedFile;

import com.dao.CandidatDAO;
import com.models.Candidat;
import com.models.Etablissement;
import com.models.Parcours;
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
                    String fileName = "diplome_bac_" + candidat.getNom().replaceAll("\\s+", "_")+ "_" + candidat.getPrenom().replaceAll("\\s+", "_") + ".pdf";
                    saveFile(diplomeBacFile, fileName);
                    candidat.setDiplomeBacPath(Paths.get(UPLOAD_DIR, fileName).toString());
                } else {
                    System.out.println("⚠️ Le fichier est vide !");
                }
            } else {
                System.out.println("❌ Aucun fichier reçu !");
            }
            
            if (diplomeLicenceFile != null && diplomeLicenceFile.getSize() > 0) {
                String fileName = "diplome_licence_"+ candidat.getNom().replaceAll("\\s+", "_")+ "_" + candidat.getPrenom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(diplomeLicenceFile, fileName);
                candidat.setDiplomeLicencePath(Paths.get(UPLOAD_DIR, fileName).toString());
            }
            if (releve1File != null) {
                String fileName = "releve1_" + candidat.getNom().replaceAll("\\s+", "_")+ "_" + candidat.getPrenom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(releve1File, fileName);
                candidat.setReleve1Path(Paths.get(UPLOAD_DIR, fileName).toString());
            }
            if (releve2File != null) {
                String fileName = "releve2_" + candidat.getNom().replaceAll("\\s+", "_")+ "_" + candidat.getPrenom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(releve2File, fileName);
                candidat.setReleve2Path(Paths.get(UPLOAD_DIR, fileName).toString());
            }
            if (releve3File != null) {
                String fileName = "releve3_" + candidat.getNom().replaceAll("\\s+", "_")+ "_" + candidat.getPrenom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(releve3File, fileName);
                candidat.setReleve3Path(Paths.get(UPLOAD_DIR, fileName).toString());
            }
            if (releve4File != null) {
                String fileName = "releve4_" + candidat.getNom().replaceAll("\\s+", "_")+ "_" + candidat.getPrenom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(releve4File, fileName);
                candidat.setReleve4Path(Paths.get(UPLOAD_DIR, fileName).toString());
            }
            if (releve5File != null) {
                String fileName = "releve5_" + candidat.getNom().replaceAll("\\s+", "_")+ "_" + candidat.getPrenom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(releve5File, fileName);
                candidat.setReleve5Path(Paths.get(UPLOAD_DIR, fileName).toString());
            }
            if (releve6File != null) {
                String fileName = "releve6_" + candidat.getNom().replaceAll("\\s+", "_")+ "_" + candidat.getPrenom().replaceAll("\\s+", "_") + ".pdf";
                saveFile(releve6File, fileName);
                candidat.setReleve6Path(Paths.get(UPLOAD_DIR, fileName).toString());
            }

            // Enregistrer le candidat dans la base de données
            candidatDAO.ajouterCandidat(candidat);

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
    
    public List<Candidat> candidatsTrier() {
        List<Candidat> candidats = candidatDAO.obtenirTousLesCandidats();

       
        candidats.sort(Comparator.comparingDouble(Candidat::getMoyenneClassement).reversed());
        
        return candidats;
    }

    
    public void annulerModification() {
        candidat = new Candidat(); // Réinitialisation du candidat
        parcoursChoisis = new ArrayList<>(); // Réinitialiser les parcours choisis
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Modification annulée."));
       
    }
    
    public void calculerEtTrierParMC() {
        for (Candidat c : obtenirTousLesCandidats()) {
            double MNS = (c.getMoyenneReleve1() + c.getMoyenneReleve2() + 
                          c.getMoyenneReleve3() + c.getMoyenneReleve4() + 
                          c.getMoyenneReleve5() + c.getMoyenneReleve6()) / 6.0;

            double BS = c.getBonnus();
            double MS = c.getMalus();

            double MC = MNS + BS + MS;
            c.setMoyenneClassement(MC);
            candidatDAO.modifierCandidat(c);
        }
        
        candidats = (List<Candidat>) candidatsParParcours();
        
    }
    
    public Map<String, List<Candidat>> candidatsParParcours() {
        Map<String, List<Candidat>> candidatsParParcours = new HashMap<>();
        
        List<Candidat> candidats = candidatDAO.obtenirTousLesCandidats();
        
        for (Candidat c : candidats) {
            String parcours = c.getParcours();
            String parcoursAvantVirgule = parcours.split(",")[0]; 
            if (!candidatsParParcours.containsKey(parcoursAvantVirgule)) {
                candidatsParParcours.put(parcoursAvantVirgule, new ArrayList<>());
            }
            candidatsParParcours.get(parcoursAvantVirgule).add(c);
        }
        
        for (Map.Entry<String, List<Candidat>> entry : candidatsParParcours.entrySet()) {
            List<Candidat> sortedList = entry.getValue().stream()
                .sorted(Comparator.comparingDouble(Candidat::getMoyenneClassement).reversed()) 
                .collect(Collectors.toList());
            
            candidatsParParcours.put(entry.getKey(), sortedList);
        }
        
        return candidatsParParcours;
    }



}