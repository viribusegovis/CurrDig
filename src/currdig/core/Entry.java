package currdig.core;

import java.io.Serializable;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;
import currdig.utils.Utils;

/**
 * This class represents an entry in the digital curriculum system. It includes
 * a description, entity public key, target user's public key, and the date and
 * time when the entry was created.
 */
public class Entry implements Serializable {

    private String description;
    private PublicKey entityPublicKey;
    private PublicKey targetUserPublicKey; // The public key of the target user associated with the entry
    private LocalDateTime dateTime; // The time when the entry was created

    /**
     * Constructs a new Entry object with the provided details.
     *
     * @param description A description of the entry.
     * @param entityPublicKey The public key of the entity creating the entry.
     * @param targetUserPublicKey The public key of the target user associated
     * with the entry.
     */
    public Entry(String description, PublicKey entityPublicKey, PublicKey targetUserPublicKey) {
        this.description = description;
        this.entityPublicKey = entityPublicKey;
        this.targetUserPublicKey = targetUserPublicKey;
        this.dateTime = Utils.fetchNetworkTime(); // Fetch the current network time for this entry
    }

    /**
     * Gets the description of the entry.
     *
     * @return The description of the entry.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the public key of the entity that created the entry.
     *
     * @return The entity's public key.
     */
    public PublicKey getEntityPublicKey() {
        return entityPublicKey;
    }

    /**
     * Gets the public key of the target user associated with the entry.
     *
     * @return The target user's public key.
     */
    public PublicKey getTargetUserPublicKey() {
        return targetUserPublicKey;
    }

    /**
     * Gets the date and time when the entry was created.
     *
     * @return The LocalDateTime object representing the creation time.
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Returns a formatted string representation of the entry, including the
     * description, the target user's public key (partially encoded in Base64),
     * and the date and time.
     *
     * @return A formatted string describing the entry.
     */
    public String getFormattedString() {
        return String.format("%s - Issued To: %s - Date: %s",
                this.description,
                Base64.getEncoder().encodeToString(this.targetUserPublicKey.getEncoded()).substring(0, 10) + "...",
                this.dateTime.toString());
    }

    /**
     * Returns a string representation of the entry, including the description,
     * the Base64 encoded entity public key, the Base64 encoded target user
     * public key, and the date and time of creation.
     *
     * @return A string representation of the entry.
     */
    @Override
    public String toString() {
        return description + "|"
                + Base64.getEncoder().encodeToString(entityPublicKey.getEncoded()) + "|"
                + Base64.getEncoder().encodeToString(targetUserPublicKey.getEncoded()) + "|"
                + dateTime;
    }

    /**
     * Checks if two entries are equal by comparing their description, entity
     * public key, target user public key, and creation date and time.
     *
     * @param o The object to compare with.
     * @return true if the entries are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Entry entry = (Entry) o;
        return Objects.equals(description, entry.description)
                && Objects.equals(entityPublicKey, entry.entityPublicKey)
                && Objects.equals(targetUserPublicKey, entry.targetUserPublicKey)
                && Objects.equals(dateTime, entry.dateTime);
    }

    /**
     * Generates a hash code for the entry based on its description, entity
     * public key, target user public key, and creation date and time.
     *
     * @return A hash code value for this entry.
     */
    @Override
    public int hashCode() {
        return Objects.hash(description, entityPublicKey, targetUserPublicKey, dateTime);
    }
}
