package com.myhealth.app.web.rest.dto;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class FileSystemDTO {

    private String newFolderName;

    private String path;



    public FileSystemDTO() {
    }

    public FileSystemDTO(String newFolderName, String path) {
        this.newFolderName = newFolderName;
        this.path = path;
    }

    public String getNewFolderName() {
        return newFolderName;
    }

    public void setNewFolderName(String newFolderName) {
        this.newFolderName = newFolderName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
