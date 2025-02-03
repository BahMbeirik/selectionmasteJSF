package com.converters;

import com.models.Etablissement;
import com.dao.EtablissementDAO;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import java.util.logging.Logger;

@FacesConverter("etablissementConverter")
public class EtablissementConverter implements Converter {

    private static final Logger logger = Logger.getLogger(EtablissementConverter.class.getName());

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            // تحقق من أن القيمة ليست فارغة
            if (value == null || value.isEmpty()) {
                return null;
            }

            // قم بتحويل القيمة إلى كائن Etablissement
            EtablissementDAO dao = new EtablissementDAO();
            return dao.obtenirEtablissementParId(Long.valueOf(value));
        } catch (Exception e) {
            logger.severe("Erreur lors de la conversion de l'établissement: " + e.getMessage());
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur de conversion", "Établissement non valide."));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Etablissement) {
            return String.valueOf(((Etablissement) value).getId());
        } else {
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur de conversion", "Établissement non valide."));
        }
    }
}