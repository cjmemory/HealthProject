package com.myhealth.app.web.rest;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSFile;
import com.myhealth.app.jackrabbit.JcrSessionFactory;
import com.myhealth.app.web.rest.dto.FileSystemDTO;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Session;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * REST controller for managing File system.
 */
@RestController
@RequestMapping("/api")
public class GridFileSystemResource {

    private static final String FILENAME = "fileName";
    private static final String FILETYPE = "fileType";
    private static final String FILEID = "fileID";
    private static final String FILEDESC = "fileDesc";
    private static final String MODIFIEDON = "modifiedOn";
    private static final String MODIFIEDBY = "modifiedBy";
    private static final String DIR = "Dir";
    private static final String FIL = "Fil";
    private static final String EMPTYSTR = "";

    private final Logger log = LoggerFactory.getLogger(GridFileSystemResource.class);

    @Inject
    GridFsTemplate template;

    @Inject
    private JcrSessionFactory jcrSessionFactory;

    /**
     * Interface to create a new folder
     */
    @RequestMapping(value="/createFolder", method=RequestMethod.POST)
    public @ResponseBody String createFolder(@RequestBody FileSystemDTO fileSystemDTO){
//                                                 @RequestParam("currentFolderPath") String currentFolderPath,
//                                             @RequestParam("newFolderName") String newFolderName) {

        boolean succ = saveFolder(fileSystemDTO.getPath(), fileSystemDTO.getNewFolderName());
        if(succ) {
            return "Create new Folder successfully.";
        } else {
            return "Failed to create new Folder.";
        }
    }

    /**
     * Interface to delete file or folder
     */
    @RequestMapping(value="/delete", method=RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("fileOrFolderPath") String fileOrFolderPath) {
        boolean succ = deleteFileOrFolder(fileOrFolderPath);
        if(succ) {
            return "Delete successfully.";
        } else {
            return "Failed to delete.";
        }
    }

    /**
     * Interface to handle multiple file upload
     *
     * @param currentFolderPath - current folder path
     * @param files - uploaded files
     * @return
     */
    @RequestMapping(value="/multipleSave", method=RequestMethod.POST)
    public @ResponseBody String multipleSave(@RequestParam("currentFolderPath") String currentFolderPath,
                                                  @RequestParam("file") MultipartFile[] files){
        String fileName = null;
        String msg = "";
        if (files != null && files.length >0) {
            for(int i =0 ;i< files.length; i++){
                try {
                    fileName = files[i].getOriginalFilename();
                    saveFile(currentFolderPath, files[i]);
                    msg += "You have successfully uploaded " + fileName +"<br/>";
                } catch (Exception e) {
                    return "You failed to upload " + fileName + ": " + e.getMessage() +"<br/>";
                }
            }
            return msg;
        } else {
            return "Unable to upload. File is empty.";
        }
    }

    /**
     * Create a new folder - Update tree structure, no need to touch GridFS
     *
     * @param currentFolderPath - current folder path (should be absolute path)
     * @param newFolderName - new folder name
     */
    private boolean saveFolder(String currentFolderPath, String newFolderName) {
        boolean succ = true;
        Session session = null;
        try {
            session = jcrSessionFactory.getSession();

            Node currentNode = session.getNode(currentFolderPath);
            Node newNode = currentNode.addNode(newFolderName);
            setNewNodeProperty(newNode, newFolderName, DIR, EMPTYSTR, EMPTYSTR,
                                CvtDateToString(getLocalTime()), "Someone");
            session.save();
        } catch (Exception e) {
            succ = false;
        } finally {
            if(null != session) {
                session.logout();
            }
        }
        return succ;
    }

    /**
     * Save one uploaded file to GridFS and register it in JCR tree
     *
     * @param currentFolderPath - current foler path
     * @param file - uploaded file
     * @throws IOException
     */
    private void saveFile(String currentFolderPath, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        byte[] bytes = file.getBytes();
        InputStream input = new ByteArrayInputStream(bytes);
        BasicDBObject metaData = new BasicDBObject();
        GridFSFile gridFSFile = template.store(input, file.getOriginalFilename(), metaData);
        input.close();

        if(null != gridFSFile) {  // If file has been stored into GridFS successfully
            Session session = null;
            try {
                session = jcrSessionFactory.getSession();
                Node currentNode = session.getNode(currentFolderPath);
                Node newNode = currentNode.addNode(fileName);
                ObjectId id = (ObjectId)gridFSFile.getId();
                setNewNodeProperty(newNode, fileName, FIL, id.toString(), EMPTYSTR,
                    CvtDateToString(getLocalTime()), "Someone");
                session.save();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(null != session) {
                    session.logout();
                }
            }
        }
    }

    /**
     * Remove the file or folder node in JCR tree
     *
     * @param fileOrFolderPath - the absolute path of the file or folder
     * @return
     */
    private boolean deleteFileOrFolder(String fileOrFolderPath) {
        boolean succ = true;
        Session session = null;
        try {
            session = jcrSessionFactory.getSession();

            Node currentNode = session.getNode(fileOrFolderPath);
            currentNode.remove();
            session.save();
        } catch (Exception e) {
            succ = false;
        } finally {
            if(null != session) {
                session.logout();
            }
        }
        return succ;
    }

    /**
     * Rename file or folder
     *
     * @param fileOrFolderPath - the file or folder path
     * @param newName - new name
     * @return
     */
    private boolean renameFileOrFolder(String fileOrFolderPath, String newName) {
        boolean succ = true;
        Session session = null;
        try {
            session = jcrSessionFactory.getSession();

            Node currentNode = session.getNode(fileOrFolderPath);
            // Don't know the effect to version history, will reimplement with delete+create in the future.
            session.move(currentNode.getPath(), currentNode.getParent().getPath() + "/" + newName);
            session.save();
        } catch (Exception e) {
            succ = false;
        } finally {
            if(null != session) {
                session.logout();
            }
        }
        return succ;
    }

    /**
     * Edit description of file or folder
     *
     * @param fileOrFolderPath - file or folder path
     * @param newDesc - new Description
     * @return
     */
    private boolean editDescription(String fileOrFolderPath, String newDesc) {
        boolean succ = true;
        Session session = null;
        try {
            session = jcrSessionFactory.getSession();

            Node currentNode = session.getNode(fileOrFolderPath);
            currentNode.setProperty(FILEDESC, newDesc);
            currentNode.setProperty(MODIFIEDON, CvtDateToString(getLocalTime()));
            currentNode.setProperty(MODIFIEDBY, "Someone");

            session.save();
        } catch (Exception e) {
            succ = false;
        } finally {
            if(null != session) {
                session.logout();
            }
        }
        return succ;
    }

    /**
     * Move file or folder to new place
     *
     * @param fromPath - original path (absolute path)
     * @param toPath - dest path (absolute path)
     * @return
     */
    private boolean moveFileOrFolder(String fromPath, String toPath) {
        boolean succ = true;
        Session session = null;
        try {
            session = jcrSessionFactory.getSession();
            session.move(fromPath, toPath);
            session.save();
        } catch (Exception e) {
            succ = false;
        } finally {
            if(null != session) {
                session.logout();
            }
        }
        return succ;
    }

    /**
     * Set the new node properties
     *
     * @param newNode - new node
     * @param fileName - file name
     * @param fileType - file type
     * @param fileID - file ID
     * @param fileDesc - file Description
     * @param modifiedOn - modified time
     * @param modifiedBy - modified by whom
     */
    private void setNewNodeProperty(Node newNode,
                                    String fileName,
                                    String fileType,
                                    String fileID,
                                    String fileDesc,
                                    String modifiedOn,
                                    String modifiedBy) {
        try {
            newNode.setProperty(FILENAME, fileName);
            newNode.setProperty(FILETYPE, fileType);
            newNode.setProperty(FILEID, fileID);
            newNode.setProperty(FILEDESC, fileDesc);
            newNode.setProperty(MODIFIEDBY, modifiedBy);
            newNode.setProperty(MODIFIEDON, modifiedOn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert Date to String
     *
     * @param date
     * @return
     */
    private String CvtDateToString(Date date) {
        SimpleDateFormat tf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        return tf.format(date);
    }

    /**
     * Convert String to Date
     *
     * @param str
     * @return
     */
    private Date CvtStringToDate(String str) {
        Date date = null;
        try {
            date = new SimpleDateFormat("MM-dd-yyyy HH:mm").parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Get local time
     *
     * @return
     */
    private Date getLocalTime() {
        TimeZone time = TimeZone.getTimeZone("CST");
        time = TimeZone.getDefault();
        TimeZone.setDefault(time);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        return date;
    }
}
