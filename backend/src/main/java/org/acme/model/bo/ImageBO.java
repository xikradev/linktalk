package org.acme.model.bo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.dao.ImageDAO;
import org.acme.model.entity.Message;

import java.io.FileNotFoundException;

@ApplicationScoped
public class ImageBO {

    @Inject
    ImageDAO imageDAO;

    public String saveImage(String base64Image, Long chatId, Long senderId, Message message, String typeChat) throws FileNotFoundException {
        return imageDAO.saveImage(base64Image, chatId,senderId, message, typeChat);
    }
}
