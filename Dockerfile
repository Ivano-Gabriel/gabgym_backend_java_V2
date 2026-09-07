# Estágio 1: Build da aplicação usando Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copia o pom e baixa as dependências primeiro (cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e faz o build PULANDO os testes
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem leve para rodar a aplicação
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Pega o .jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# O Render injeta uma variável $PORT dinamicamente. 
# O Spring Boot vai ler isso e rodar na porta certa.
ENTRYPOINT ["java", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]