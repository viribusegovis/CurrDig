/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package currdig.core;

import java.io.Serializable;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 *
 * @author bmsff
 */
public class Entry implements Serializable {

    private static final long serialVersionUID = 1L;

    private String description;
    private PublicKey entityPublicKey;
    private PublicKey targetUserPublicKey; // Add this field
    private LocalDateTime dateTime;

    public Entry(String description, PublicKey entityPublicKey, PublicKey targetUserPublicKey) {
        this.description = description;
        this.entityPublicKey = entityPublicKey;
        this.targetUserPublicKey = targetUserPublicKey; // Initialize the field
        this.dateTime = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public PublicKey getEntityPublicKey() {
        return entityPublicKey;
    }

    public PublicKey getTargetUserPublicKey() {
        return targetUserPublicKey; // Add getter
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getFormattedString() {
        return String.format("%s - Issued To: %s - Date: %s",
                this.description,
                Base64.getEncoder().encodeToString(this.targetUserPublicKey.getEncoded()).substring(0, 10) + "...",
                this.dateTime.toString());
    }

    @Override
    public String toString() {
        return description + "|"
                + Base64.getEncoder().encodeToString(entityPublicKey.getEncoded()) + "|"
                + Base64.getEncoder().encodeToString(targetUserPublicKey.getEncoded()) + "|"
                + dateTime;
    }
}
