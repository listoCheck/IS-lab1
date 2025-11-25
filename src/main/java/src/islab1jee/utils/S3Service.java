package src.islab1jee.utils;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class S3Service {

    private MinioClient minioClient;
    private final String bucketName = "islabs-3";

    public S3Service() {
        // Конструктор пустой
    }

    private MinioClient getClient() {
        if (minioClient == null) {
            synchronized (this) {
                if (minioClient == null) {
                    try {
                        minioClient = MinioClient.builder()
                                .endpoint("https://s3.cloud.ru")
                                .credentials(
                                        "66436867-c426-4e83-a211-6a08c57cac01:64a43acc4952200834f6add811209354",
                                        "e08e8f4501066e2b67acf101cbbc3889"
                                )
                                .region("ru-central-1")
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException("Ошибка инициализации MinioClient", e);
                    }
                }
            }
        }
        System.out.println("made");
        return minioClient;
    }

    public String uploadFile(String fileName, InputStream inputStream) {
        try {
            getClient().putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, -1, 10485760)
                            .build()
            );

            return getClient().getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при работе с S3 Cloud.ru", e);
        }
    }
}
