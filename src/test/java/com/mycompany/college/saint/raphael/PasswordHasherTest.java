package com.mycompany.college.saint.raphael;

import com.mycompany.college.saint.raphael.security.PasswordHasher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {
    @Test void hashAndVerify() {
        String hash = PasswordHasher.hash("MotDePasse@123");
        assertTrue(PasswordHasher.verify("MotDePasse@123", hash));
        assertFalse(PasswordHasher.verify("Incorrect", hash));
    }
}
