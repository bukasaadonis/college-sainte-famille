-- =====================================================================
-- COLLEGE SAINTE FAMILLE - BASE DE DONNEES PORTABLE
-- Version 4.0
-- Compatible : MySQL 8.x / MariaDB 10.4+
-- IMPORTANT : ce script recrée entièrement la base college_sainte_famille.
-- Il ne contient NI DEFINER, NI PROCEDURE, NI TRIGGER, NI VIEW.
-- =====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `college_sainte_famille`;
CREATE DATABASE `college_sainte_famille`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE `college_sainte_famille`;

-- =========================
-- 1. STRUCTURE ACADEMIQUE
-- =========================

CREATE TABLE `annees_scolaires` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `libelle` VARCHAR(50) NOT NULL,
  `date_debut` DATE NOT NULL,
  `date_fin` DATE NOT NULL,
  `est_active` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_annee_libelle` (`libelle`),
  KEY `idx_annees_active` (`est_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `classes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(50) NOT NULL,
  `niveau` VARCHAR(20) NOT NULL,
  `section` VARCHAR(20) NOT NULL,
  `option` VARCHAR(50) DEFAULT NULL,
  `capacite_max` INT NOT NULL DEFAULT 35,
  `statut` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `date_creation` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_classe_nom` (`nom`),
  KEY `idx_classes_niveau` (`niveau`),
  KEY `idx_classes_section` (`section`),
  KEY `idx_classes_statut` (`statut`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `eleves` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `matricule` VARCHAR(30) NOT NULL,
  `nom` VARCHAR(50) NOT NULL,
  `postnom` VARCHAR(50) DEFAULT NULL,
  `prenom` VARCHAR(50) NOT NULL,
  `sexe` VARCHAR(10) NOT NULL,
  `date_naissance` DATE NOT NULL,
  `lieu_naissance` VARCHAR(100) DEFAULT NULL,
  `nationalite` VARCHAR(50) DEFAULT NULL,
  `adresse` VARCHAR(200) DEFAULT NULL,
  `telephone` VARCHAR(20) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `photo` LONGBLOB DEFAULT NULL,
  `nom_pere` VARCHAR(100) DEFAULT NULL,
  `nom_mere` VARCHAR(100) DEFAULT NULL,
  `etat_civil_parents` VARCHAR(20) DEFAULT NULL,
  `etablissement_provenance` VARCHAR(200) DEFAULT NULL,
  `statut` VARCHAR(20) NOT NULL DEFAULT 'ACTIF',
  `date_creation` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modification` DATETIME DEFAULT NULL,
  `supprime` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_eleve_matricule` (`matricule`),
  KEY `idx_eleves_nom` (`nom`),
  KEY `idx_eleves_prenom` (`prenom`),
  KEY `idx_eleves_statut` (`statut`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `responsables` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(50) NOT NULL,
  `postnom` VARCHAR(50) DEFAULT NULL,
  `prenom` VARCHAR(50) NOT NULL,
  `lien_avec_eleve` VARCHAR(20) NOT NULL,
  `profession` VARCHAR(100) DEFAULT NULL,
  `adresse` VARCHAR(200) DEFAULT NULL,
  `telephone` VARCHAR(20) NOT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `date_creation` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_responsables_nom` (`nom`),
  KEY `idx_responsables_telephone` (`telephone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `eleve_responsable` (
  `eleve_id` BIGINT NOT NULL,
  `responsable_id` BIGINT NOT NULL,
  PRIMARY KEY (`eleve_id`, `responsable_id`),
  KEY `idx_er_responsable` (`responsable_id`),
  CONSTRAINT `fk_er_eleve`
    FOREIGN KEY (`eleve_id`) REFERENCES `eleves` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_er_responsable`
    FOREIGN KEY (`responsable_id`) REFERENCES `responsables` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `enseignants` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(50) NOT NULL,
  `postnom` VARCHAR(50) DEFAULT NULL,
  `prenom` VARCHAR(50) NOT NULL,
  `specialite` VARCHAR(100) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `telephone` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_enseignants_nom` (`nom`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `cours` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `enseignant_id` BIGINT DEFAULT NULL,
  `classe_id` BIGINT NOT NULL,
  `nom` VARCHAR(100) NOT NULL,
  `code` VARCHAR(20) DEFAULT NULL,
  `coefficient` DECIMAL(4,2) NOT NULL DEFAULT 1.00,
  `description` TEXT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cours_code` (`code`),
  KEY `idx_cours_classe` (`classe_id`),
  KEY `idx_cours_enseignant` (`enseignant_id`),
  CONSTRAINT `fk_cours_enseignant`
    FOREIGN KEY (`enseignant_id`) REFERENCES `enseignants` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_cours_classe`
    FOREIGN KEY (`classe_id`) REFERENCES `classes` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `periodes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `annee_scolaire_id` BIGINT NOT NULL,
  `nom` VARCHAR(50) NOT NULL,
  `date_debut` DATE NOT NULL,
  `date_fin` DATE NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_periode_annee_nom` (`annee_scolaire_id`, `nom`),
  KEY `idx_periodes_annee` (`annee_scolaire_id`),
  CONSTRAINT `fk_periode_annee`
    FOREIGN KEY (`annee_scolaire_id`) REFERENCES `annees_scolaires` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `inscriptions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `eleve_id` BIGINT NOT NULL,
  `classe_id` BIGINT NOT NULL,
  `annee_scolaire_id` BIGINT NOT NULL,
  `num_inscription` VARCHAR(30) NOT NULL,
  `date_inscription` DATE NOT NULL,
  `statut` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `observations` TEXT DEFAULT NULL,
  `date_creation` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inscription_numero` (`num_inscription`),
  KEY `idx_inscriptions_eleve` (`eleve_id`),
  KEY `idx_inscriptions_classe` (`classe_id`),
  KEY `idx_inscriptions_annee` (`annee_scolaire_id`),
  KEY `idx_inscriptions_statut` (`statut`),
  CONSTRAINT `fk_inscription_eleve`
    FOREIGN KEY (`eleve_id`) REFERENCES `eleves` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_inscription_classe`
    FOREIGN KEY (`classe_id`) REFERENCES `classes` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_inscription_annee`
    FOREIGN KEY (`annee_scolaire_id`) REFERENCES `annees_scolaires` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `notes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `eleve_id` BIGINT NOT NULL,
  `cours_id` BIGINT NOT NULL,
  `periode_id` BIGINT NOT NULL,
  `valeur` DECIMAL(5,2) NOT NULL,
  `maximum` DECIMAL(5,2) NOT NULL DEFAULT 20.00,
  `coefficient` DECIMAL(4,2) NOT NULL DEFAULT 1.00,
  `date_saisie` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_note_eleve_cours_periode` (`eleve_id`, `cours_id`, `periode_id`),
  KEY `idx_notes_cours` (`cours_id`),
  KEY `idx_notes_periode` (`periode_id`),
  CONSTRAINT `fk_note_eleve`
    FOREIGN KEY (`eleve_id`) REFERENCES `eleves` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_note_cours`
    FOREIGN KEY (`cours_id`) REFERENCES `cours` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_note_periode`
    FOREIGN KEY (`periode_id`) REFERENCES `periodes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `bulletins` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `eleve_id` BIGINT NOT NULL,
  `periode_id` BIGINT NOT NULL,
  `moyenne_generale` DECIMAL(5,2) DEFAULT NULL,
  `rang` INT DEFAULT NULL,
  `decision` VARCHAR(20) DEFAULT NULL,
  `date_emission` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bulletin_eleve_periode` (`eleve_id`, `periode_id`),
  KEY `idx_bulletins_periode` (`periode_id`),
  CONSTRAINT `fk_bulletin_eleve`
    FOREIGN KEY (`eleve_id`) REFERENCES `eleves` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_bulletin_periode`
    FOREIGN KEY (`periode_id`) REFERENCES `periodes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- 2. SECURITE / UTILISATEURS
-- =========================

CREATE TABLE `roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(50) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_nom` (`nom`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `permissions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nom` VARCHAR(80) NOT NULL,
  `code` VARCHAR(50) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `role_permissions` (
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`),
  KEY `idx_rp_permission` (`permission_id`),
  CONSTRAINT `fk_rp_role`
    FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rp_permission`
    FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `utilisateurs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT NOT NULL,
  `login` VARCHAR(50) NOT NULL,
  `mot_de_passe` VARCHAR(255) NOT NULL,
  `nom` VARCHAR(50) NOT NULL,
  `postnom` VARCHAR(50) DEFAULT NULL,
  `prenom` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `telephone` VARCHAR(20) DEFAULT NULL,
  `statut` VARCHAR(20) NOT NULL DEFAULT 'ACTIF',
  `date_creation` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modification` DATETIME DEFAULT NULL,
  `derniere_connexion` DATETIME DEFAULT NULL,
  `tentative_echec` INT NOT NULL DEFAULT 0,
  `bloque` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_utilisateur_login` (`login`),
  UNIQUE KEY `uk_utilisateur_email` (`email`),
  KEY `idx_utilisateurs_role` (`role_id`),
  KEY `idx_utilisateurs_statut` (`statut`),
  CONSTRAINT `fk_utilisateur_role`
    FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `sessions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `utilisateur_id` BIGINT NOT NULL,
  `token` VARCHAR(255) NOT NULL,
  `date_debut` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_fin` DATETIME DEFAULT NULL,
  `adresse_ip` VARCHAR(45) DEFAULT NULL,
  `actif` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_token` (`token`),
  KEY `idx_sessions_utilisateur` (`utilisateur_id`),
  KEY `idx_sessions_actif` (`actif`),
  CONSTRAINT `fk_session_utilisateur`
    FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `connexions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `utilisateur_id` BIGINT NOT NULL,
  `date_connexion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_deconnexion` DATETIME DEFAULT NULL,
  `adresse_ip` VARCHAR(45) DEFAULT NULL,
  `navigateur` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_connexions_utilisateur` (`utilisateur_id`),
  CONSTRAINT `fk_connexion_utilisateur`
    FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `historiques` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `utilisateur_id` BIGINT NOT NULL,
  `action` VARCHAR(100) NOT NULL,
  `entite` VARCHAR(50) DEFAULT NULL,
  `entite_id` BIGINT DEFAULT NULL,
  `details` TEXT DEFAULT NULL,
  `date_action` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `adresse_ip` VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_historiques_utilisateur` (`utilisateur_id`),
  KEY `idx_historiques_action` (`action`),
  KEY `idx_historiques_date` (`date_action`),
  CONSTRAINT `fk_historique_utilisateur`
    FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateurs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- 3. DONNEES DE BASE
-- =========================

INSERT INTO `annees_scolaires` (`id`,`libelle`,`date_debut`,`date_fin`,`est_active`) VALUES
(1,'2026-2027','2026-09-01','2027-07-03',1);

INSERT INTO `periodes` (`id`,`annee_scolaire_id`,`nom`,`date_debut`,`date_fin`,`type`) VALUES
(1,1,'1er Trimestre','2026-09-01','2026-12-04','TRIMESTRE'),
(2,1,'2ème Trimestre','2026-12-07','2027-03-19','TRIMESTRE'),
(3,1,'3ème Trimestre','2027-03-22','2027-07-03','TRIMESTRE');

INSERT INTO `classes` (`id`,`nom`,`niveau`,`section`,`option`,`capacite_max`,`statut`) VALUES
(1,'7ème A','SEPT','GENERALE','AUCUNE',35,'ACTIVE'),
(2,'7ème B','SEPT','GENERALE','AUCUNE',35,'ACTIVE'),
(3,'8ème A','HUIT','GENERALE','AUCUNE',35,'ACTIVE'),
(4,'8ème B','HUIT','GENERALE','AUCUNE',35,'ACTIVE'),
(5,'9ème A','NEUF','GENERALE','AUCUNE',35,'ACTIVE'),
(6,'10ème A','DIX','SCIENTIFIQUE','SCIENCES_EXACTES',30,'ACTIVE'),
(7,'10ème B','DIX','LITTERAIRE','LETTRES_MODERNES',30,'ACTIVE'),
(8,'11ème A','ONZE','SCIENTIFIQUE','MATHEMATIQUES_PHYSIQUE',30,'ACTIVE'),
(9,'11ème B','ONZE','SCIENTIFIQUE','BIOLOGIE_CHIMIE',30,'ACTIVE'),
(10,'12ème A','DOUZE','SCIENTIFIQUE','SCIENCES_EXACTES',25,'ACTIVE');

INSERT INTO `enseignants` (`id`,`nom`,`postnom`,`prenom`,`specialite`,`email`,`telephone`) VALUES
(1,'KALALA','M','Pierre','Mathématiques','pierre.kalala@college.local','+243000000101'),
(2,'TSHIBANGU','N','Françoise','Physique-Chimie','francoise.tshibangu@college.local','+243000000102'),
(3,'LUMUMBA','P','Patrice','Français','patrice.lumumba@college.local','+243000000103'),
(4,'MOBUTU','S','Sese','Anglais','sese.mobutu@college.local','+243000000104');

INSERT INTO `cours` (`id`,`enseignant_id`,`classe_id`,`nom`,`code`,`coefficient`,`description`) VALUES
(1,1,1,'Mathématiques 7A','MAT7A',2.00,'Mathématiques - 7ème A'),
(2,3,1,'Français 7A','FRA7A',2.00,'Français - 7ème A'),
(3,4,1,'Anglais 7A','ANG7A',1.50,'Anglais - 7ème A');

INSERT INTO `roles` (`id`,`nom`,`description`) VALUES
(1,'ADMIN','Administrateur du système - accès complet'),
(2,'SECRETAIRE','Gestion des élèves, classes, inscriptions et responsables'),
(3,'DIRECTEUR','Consultation, statistiques, historique et rapports'),
(4,'PROFESSEUR','Gestion pédagogique : cours, notes et bulletins');

INSERT INTO `permissions` (`id`,`nom`,`code`,`description`) VALUES
(1,'Gérer les utilisateurs','USER_MANAGE','Créer et modifier les utilisateurs'),
(2,'Gérer les élèves','ELEVE_MANAGE','Gestion des dossiers des élèves'),
(3,'Gérer les classes','CLASSE_MANAGE','Gestion des classes et années scolaires'),
(4,'Gérer les inscriptions','INSCRIPTION_MANAGE','Gestion des inscriptions'),
(5,'Gérer les responsables','RESPONSABLE_MANAGE','Gestion des responsables des élèves'),
(6,'Gérer les résultats','RESULTAT_MANAGE','Enseignants, cours, notes et bulletins'),
(7,'Consulter les statistiques','STATS_VIEW','Consulter les statistiques'),
(8,'Gérer les sauvegardes','SAUVEGARDE_MANAGE','Sauvegarde et restauration'),
(9,'Consulter l historique','HISTORIQUE_VIEW','Consulter l historique'),
(10,'Exporter les données','EXPORT','Exporter les données');

-- ADMIN : toutes les permissions
INSERT INTO `role_permissions` (`role_id`,`permission_id`)
SELECT 1, id FROM `permissions`;

-- SECRETAIRE
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES
(2,2),(2,3),(2,4),(2,5),(2,7),(2,10);

-- DIRECTEUR
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES
(3,7),(3,9),(3,10);

-- PROFESSEUR
INSERT INTO `role_permissions` (`role_id`,`permission_id`) VALUES
(4,6),(4,7),(4,10);

-- Les mots de passe sont hachés en PBKDF2 dans la base.
-- Mots de passe en clair pour les tests :
-- admin       / Admin@123
-- secretaire  / Secretaire@123
-- directeur   / Directeur@123
-- professeur  / Professeur@123
INSERT INTO `utilisateurs`
(`id`,`role_id`,`login`,`mot_de_passe`,`nom`,`postnom`,`prenom`,`email`,`telephone`,`statut`,`tentative_echec`,`bloque`) VALUES
(1,1,'admin','PBKDF2$210000$2mFCu/iW7nw7LTQioCWi2A==$9G+xA4sVd6f7bPuft/cp3K/ctKwVBnuGHlL2SOmDwa0=','SYSTEME',NULL,'Administrateur','admin@college.local',NULL,'ACTIF',0,0),
(2,2,'secretaire','PBKDF2$210000$q1jx3WuCJ7taNGXW063a5g==$7NUXgd9tK7okSCxcsmv6ePpeQn+neXoyLTjxcucpSx0=','KABEYA','M','Marie','secretaire@college.local',NULL,'ACTIF',0,0),
(3,3,'directeur','PBKDF2$210000$AvGEBesx+rSrKV9O680GiA==$FB+b8P1wSkrt9KHCvQgyNNijiLMwWOqWKgsjGvWGfeY=','MALOBA','N','Jean','directeur@college.local',NULL,'ACTIF',0,0),
(4,4,'professeur','PBKDF2$210000$N2IjKWHpTXBxbT7aUxxs+w==$Fjz+p/IghOvqWs23ZS3Uxn4HQPE/ZcF08qSrV1D5JsE=','ENSEIGNANT',NULL,'Principal','professeur@college.local',NULL,'ACTIF',0,0);

SET FOREIGN_KEY_CHECKS = 1;

-- Vérification rapide : doit retourner 18 tables.
SELECT COUNT(*) AS nombre_tables
FROM information_schema.tables
WHERE table_schema='college_sainte_famille' AND table_type='BASE TABLE';

SELECT 'Base college_sainte_famille créée avec succès.' AS message;
