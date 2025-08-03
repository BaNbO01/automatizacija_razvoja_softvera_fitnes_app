# Prva faza: build-ovanje aplikacije
# Koristimo Maven sliku baziranu na Javi 21 i Ubuntu Jammy
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder

# Postavljanje radnog direktorijuma
WORKDIR /app

# Kopiranje Maven pom.xml fajla da bi se keširale zavisnosti
COPY pom.xml .

# Preuzimanje zavisnosti
RUN mvn dependency:go-offline

# Kopiranje izvornog koda aplikacije
COPY src ./src

# Kompajliranje koda i pakovanje u JAR fajl
RUN mvn clean install -DskipTests

# Druga faza: kreiranje finalnog image-a
# Koristimo JRE sliku baziranu na Javi 21 i Ubuntu Jammy
FROM eclipse-temurin:21-jre-jammy

# Kopiranje JAR fajla iz prve faze
COPY --from=builder /app/target/*.jar app.jar

# Pokretanje aplikacije
ENTRYPOINT ["java","-jar","/app.jar"]
