# Sử dụng image JDK 21 nhỏ gọn
FROM openjdk:21-jdk-slim

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy file JAR từ local vào container
COPY target/lms-0.0.1-SNAPSHOT.jar app.jar

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
