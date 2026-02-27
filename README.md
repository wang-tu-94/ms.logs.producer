# 📡 MS Logs Producer (gRPC & Kafka)

<div align="center">
  <img src="https://img.shields.io/badge/Java_21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/gRPC-244C5A?style=for-the-badge&logo=gRPC&logoColor=white" alt="gRPC" />
  <img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white" alt="Apache Kafka" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
</div>

<br />

Ce dépôt contient le système de centralisation et d'ingestion des logs de l'écosystème. Il est conçu pour offrir de très hautes performances grâce à la communication **gRPC** et un couplage asynchrone via **Apache Kafka**.

Il s'agit d'un projet **Gradle multi-modules** composé d'un contrat partagé, d'un SDK (Starter Spring Boot) intégrable dans n'importe quel microservice, et d'un serveur d'ingestion autonome.

## 📋 Table des matières
- [Architecture du projet](#-architecture-du-projet)
- [Prérequis](#-prérequis)
- [Installation et Build](#-installation-et-build)
- [Lancement de l'infrastructure (Docker & Kafka)](#-lancement-de-linfrastructure-docker--kafka)
- [Comment utiliser le SDK (Client)](#-comment-utiliser-le-sdk-client)
- [Tests](#-tests)

---

## 🏗 Architecture du projet

Le projet est divisé en 3 modules distincts :

1. **`proto-schema`** : Contient le contrat d'interface (`logger.proto`) et génère les classes Java/gRPC associées.
2. **`log-sdk-starter`** : Une librairie (Spring Boot Starter) à importer dans les autres microservices. Elle fournit l'annotation AOP `@Loggable` et un client gRPC pré-configuré pour émettre les logs de manière non bloquante.
3. **`log-ingestor`** : Le microservice serveur qui écoute sur le port gRPC (`50051`), reçoit les flux de logs envoyés par les SDKs, et les pousse dans un topic **Kafka** (`app-logs`).

---

## 🛠 Prérequis

Pour compiler et exécuter ce projet localement :
- **Java 21** (JDK 21)
- **Docker & Docker Compose** (pour exécuter Kafka et le serveur d'ingestion)

---

## 🚀 Installation et Build

### 1. Cloner le projet
```bash
git clone [https://github.com/wang-tu-94/ms.logs.producer.git](https://github.com/wang-tu-94/ms.logs.producer.git)
cd ms.logs.producer
```

### 2. Compiler le contrat gRPC (Protobuf)
Avant de lancer quoi que ce soit, il faut générer les classes issues du `.proto` :
```bash
./gradlew :proto-schema:build
```

### 3. Publier le SDK localement (Optionnel)
Si vous souhaitez tester le `log-sdk-starter` dans un autre projet local (ex: *product-trial-back*), publiez-le dans votre Maven local :
```bash
./gradlew publishToMavenLocal
```

---

## 🐳 Lancement de l'infrastructure (Docker & Kafka)

Le projet inclut un fichier `docker-compose-dev.yml` qui déploie **Kafka (en mode KRaft)**, **Kafka UI** pour la visualisation, et le conteneur du **log-ingestor**.

```bash
# Lancer toute la stack en arrière-plan
docker-compose -f docker-compose-dev.yml up -d --build
```

- **Kafka UI** sera accessible sur : `http://localhost:9000`
- **Serveur gRPC (log-ingestor)** écoutera sur : `localhost:50051`
- **Serveur HTTP (Actuator du log-ingestor)** écoutera sur : `localhost:8
