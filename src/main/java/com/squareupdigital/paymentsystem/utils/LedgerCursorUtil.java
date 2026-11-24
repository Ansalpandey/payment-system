package com.squareupdigital.paymentsystem.utils;

import com.squareupdigital.paymentsystem.model.LedgerEntry;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
public class LedgerCursorUtil {

    public String encode(LedgerEntry entry) {
        String value = entry.getCreatedAt()
                + "|" + entry.getId();

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public Cursor decode(String cursor) {
        try {
            String decoded = new String(
                    Base64.getUrlDecoder().decode(cursor),
                    StandardCharsets.UTF_8
            );

            String[] parts = decoded.split("\\|", 2);

            return new Cursor(
                    Instant.parse(parts[0]),
                    UUID.fromString(parts[1])
            );
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid ledger cursor");
        }
    }

    public record Cursor(Instant createdAt, UUID id) {}
}
