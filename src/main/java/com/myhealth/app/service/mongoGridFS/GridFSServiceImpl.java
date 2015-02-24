package com.myhealth.app.service.mongoGridFS;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by JIECHEN on 1/30/15.
 */
@Service
public class GridFSServiceImpl {

    @Inject
    GridFsTemplate template;

    public void storeFileIntoGridFS(String fileName, byte[] fileContent) {
        BasicDBObject metaData = new BasicDBObject();
        metaData.append("username", "someone");
        metaData.append("filepath", "somepath");
        InputStream input = new ByteArrayInputStream(fileContent);

        template.store(input, fileName, metaData);
    }

    public GridFSDBFile findOneByFileName(String fileName) {
        return null;
    }


    /*
    ****************************************************************************
    * Following are some codes from Google search
    * Please be careful with the query on id for GridFS
    ****************************************************************************
    @Autowired
    GridFsTemplate gridFsTemplate;

    public String store(InputStream inputStream, String fileName,
                        String contentType, DBObject metaData) {
        return this.gridFsTemplate
            .store(inputStream, fileName, contentType, metaData).getId()
            .toString();
    }

    public GridFSDBFile getById(String id) {
        return this.gridFsTemplate.findOne(new Query(Criteria.where("_id").is(
            id)));
    }

    public GridFSDBFile getByFilename(String fileName) {
        return gridFsTemplate.findOne(new Query(Criteria.where("filename").is(
            fileName)));
    }

    public GridFSDBFile retrive(String fileName) {
        return gridFsTemplate.findOne(
            new Query(Criteria.where("filename").is(fileName)));
    }

    public List findAll() {
        return gridFsTemplate.find(null);
    }
    */
}
