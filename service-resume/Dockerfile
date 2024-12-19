# syntax=docker/dockerfile-upstream:master-labs

# Используем официальный образ Maven для сборки приложения
FROM maven:3.8.5-openjdk-17 AS build

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файлы проекта в контейнер
COPY --parents pom.xml ./pom.xml
COPY service-resume/pom.xml ./service-resume/
COPY service-resume/src ./service-resume/src

# Собираем проект с помощью Maven
RUN mvn -f ./service-resume/pom.xml clean package -DskipTests

# Используем более легковесный образ для запуска Java-приложения
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный .jar файл из предыдущего этапа
COPY --from=build /app/service-resume/target/*.jar ./myapp.jar

# Указываем команду для запуска приложения
CMD ["java", "-jar", "myapp.jar"]