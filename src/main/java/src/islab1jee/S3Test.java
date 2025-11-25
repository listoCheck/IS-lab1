package src.islab1jee;

import src.islab1jee.utils.S3Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class S3Test {

    public static void main(String[] args) {
        try {
            S3Service s3Service = new S3Service();

            File file = new File("testfile.txt");
            if (!file.exists()) {
                System.out.println("Файл не найден: " + file.getAbsolutePath());
                return;
            }

            try (InputStream inputStream = new FileInputStream(file)) {
                System.out.println("sadadad");
                String url = s3Service.uploadFile(file.getName(), inputStream);
                System.out.println("Файл успешно загружен! Ссылка: " + url);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке файла в S3" + " " + e);
        }
    }
}
