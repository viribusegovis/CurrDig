/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package currdig.core;

import java.io.Serializable;
import java.security.PublicKey;
import java.time.LocalDateTime;

/**
 *
 * @author bmsff
 */
public class Entry implements Serializable {

    private static final long serialVersionUID = 1L;

    private String description;
    private PublicKey entityPublicKey;
    private LocalDateTime dateTime;

    public Entry(String description, PublicKey entityPublicKey) {
        this.description = description;
        this.entityPublicKey = entityPublicKey;
        this.dateTime = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PublicKey getEntityPublicKey() {
        return entityPublicKey;
    }

    public void setEntityPublicKey(PublicKey entityPublicKey) {
        this.entityPublicKey = entityPublicKey;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return description + "|" + entityPublicKey.toString() + "|" + dateTime;
    }
}
