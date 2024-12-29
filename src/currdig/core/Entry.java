package currdig.core;

import java.io.Serializable;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;
import currdig.utils.Utils;

public class Entry implements Serializable {

    private String description;
    private PublicKey entityPublicKey;
    private PublicKey targetUserPublicKey; // Add this field
    private LocalDateTime dateTime;

    public Entry(String description, PublicKey entityPublicKey, PublicKey targetUserPublicKey) {
        this.description = description;
        this.entityPublicKey = entityPublicKey;
        this.targetUserPublicKey = targetUserPublicKey; // Initialize the field
        this.dateTime = Utils.fetchNetworkTime();
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

    @Override
    public int hashCode() {
        return Objects.hash(description, entityPublicKey, targetUserPublicKey, dateTime);
    }
}
