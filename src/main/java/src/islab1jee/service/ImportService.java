package src.islab1jee.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.json.JsonObject;
import src.islab1jee.enums.ImportStatus;

import src.islab1jee.model.importobjects.ImportOperation;
import src.islab1jee.repository.ImportRepository;
import src.islab1jee.utils.JsonUtils;

import java.io.InputStream;
import java.util.List;

@Stateless
public class ImportService {

    @Inject
    private ImportRepository importRepository;

    @Transactional
    public ImportOperation processImport(String username, InputStream inputStream) {
        ImportOperation op;
        try {
            List<JsonObject> objects = JsonUtils.parseJsonArray(inputStream);

            // TODO: здесь логика создания бизнес-объектов из JsonObject
            // Например: for (JsonObject obj : objects) { persistToDomainTables(obj); }

            int addedCount = objects.size();

            op = new ImportOperation();
            op.setUsername(username);
            op.setStatus(ImportStatus.SUCCESS);
            op.setAddedCount(addedCount);
            op.setErrorMessage(null);

            importRepository.save(op);

        } catch (Exception e) {
            op = new ImportOperation();
            op.setUsername(username);
            op.setStatus(ImportStatus.FAILED);
            op.setAddedCount(0);
            op.setErrorMessage(e.getMessage());

            importRepository.save(op);
        }

        return op;
    }
}
