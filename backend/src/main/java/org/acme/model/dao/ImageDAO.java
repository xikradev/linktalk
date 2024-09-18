package org.acme.model.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.model.entity.Conversation;
import org.acme.model.entity.Image;
import org.acme.model.entity.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@ApplicationScoped
public class ImageDAO {

    @Inject
    EntityManager em;

    @Transactional
    public String saveImage(String base64Image, Long chatId, Long senderId, Message message, String typeChat) throws FileNotFoundException {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        String jpql = "SELECT COUNT(i) FROM Image i";
        Long count = em.createQuery(jpql, Long.class).getSingleResult();


        String fileName = chatId+"_"+typeChat + "_" + senderId + "_image_" + count + ".png";
        File file = new File("opt/app/images", fileName);
        String imageUrl = "http://localhost:8081/images/" + fileName;

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            // Escreve os dados binários da imagem no arquivo
            outputStream.write(imageBytes);
            outputStream.flush();

            Image image = new Image();
            image.setUrl(imageUrl);
            image.setMessage(message);
            em.persist(image);

            return imageUrl;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Image findByMessageId(Message message){
        TypedQuery<Image> query =em.createQuery("SELECT i FROM Image i WHERE i.message = :message",Image.class);
        query.setParameter("message", message);
        return query.getResultStream().findFirst().orElse(null);
    }

    public void delete(Image image) {

        String[] parts = image.getUrl().split("/");

        // Pegando a última parte que é o nome do arquivo
        String fileName = parts[parts.length - 1];

        Path filePath = Paths.get("opt/app/images/"+fileName);
        try{
            Files.delete(filePath);
        }catch (Exception ex){
            throw new NotFoundException("Não foi possível deletar a imagem da mensagem");
        }

        em.remove(em.contains(image) ? image : em.merge(image));
    }
}