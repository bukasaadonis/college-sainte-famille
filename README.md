# Collège Sainte Famille — Application Desktop portable v4.0

Cette version a été simplifiée pour fonctionner facilement sur plusieurs ordinateurs.

## Ce qui a été simplifié

- aucune procédure stockée MySQL ;
- aucun trigger ;
- aucune vue SQL ;
- aucune clause `DEFINER` ;
- génération des matricules directement en Java ;
- génération des numéros d'inscription directement en Java ;
- statistiques calculées directement par les DAO ;
- détection automatique du port MySQL **3306 ou 3307** ;
- un seul fichier de configuration facultatif : `config/database.properties`.

## Prérequis sur chaque ordinateur

1. JDK 17 ou supérieur ;
2. NetBeans avec Maven, ou Maven installé ;
3. XAMPP / MySQL / MariaDB démarré ;
4. phpMyAdmin pour importer la base.

## Installation la plus simple

### 1. Base de données

Dans phpMyAdmin :

1. cliquez sur **Importer** ;
2. choisissez `database/college_sainte_famille_portable.sql` ;
3. cliquez sur **Exécuter**.

Le script crée automatiquement la base `college_sainte_famille`.

> Attention : le script supprime puis recrée `college_sainte_famille`. Utilisez-le pour une installation neuve ou après sauvegarde.

### 2. Application

Ouvrez le dossier du projet dans NetBeans, puis faites **Clean and Build** et **Run**.

Ou en ligne de commande :

```bash
mvn clean javafx:run
```

## Connexion MySQL

Par défaut :

- serveur : `127.0.0.1`
- port : détection automatique `3306` puis `3307`
- base : `college_sainte_famille`
- utilisateur MySQL : `root`
- mot de passe MySQL : vide

Si votre MySQL utilise un autre mot de passe, éditez simplement :

`config/database.properties`

Exemple :

```properties
db.host=127.0.0.1
db.port=auto
db.name=college_sainte_famille
db.user=root
db.password=MON_MOT_DE_PASSE_MYSQL
```

## Comptes de l'application

| Rôle | Identifiant | Mot de passe |
|---|---|---|
| Administrateur | `admin` | `Admin@123` |
| Secrétaire | `secretaire` | `Secretaire@123` |
| Directeur | `directeur` | `Directeur@123` |
| Professeur | `professeur` | `Professeur@123` |

Les mots de passe ne sont pas stockés en clair dans MySQL. La base contient leurs hachages PBKDF2 compatibles avec l'application.

## Important pour changer d'ordinateur

Ne copiez **jamais** les dossiers `xampp/mysql/data/...` d'un ordinateur vers un autre.

Copiez seulement :

1. le dossier de ce projet Java ;
2. le fichier `database/college_sainte_famille_portable.sql`.

Sur le nouvel ordinateur, réimportez le fichier SQL avec phpMyAdmin. Cela évite les erreurs InnoDB `#1932` et `#1030 Tablespace is missing`.
