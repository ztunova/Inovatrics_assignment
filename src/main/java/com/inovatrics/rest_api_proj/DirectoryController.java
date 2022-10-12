package com.inovatrics.rest_api_proj;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;

@RestController
public class DirectoryController {

    @Operation(summary = "Create new directory", description = "Creates new directory and every parent directory which does not exist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Directory successfully created"),
            @ApiResponse(responseCode = "400", description = "Directory already exists"),
            @ApiResponse(responseCode = "500", description = "Other error")})
    @PostMapping("directory")
    public ResponseEntity<String> createNewDirectory(@RequestParam("path")
                                                         @Parameter(name = "path", description = "Path to directory to be created")
                                                                 String dirPath){
        File newDirectory = new File(dirPath);
        if (!newDirectory.exists()){
            if (newDirectory.mkdirs()){
                return new ResponseEntity<>("Directory created", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Directory failed to create", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Directory already exists", HttpStatus.BAD_REQUEST);
        }
    }

    // recursive function to delete files and directories in the given folder
    private void deleteSubfiles(File folder){
        File[] childFolders = folder.listFiles();

        assert childFolders != null;
        for (File file : childFolders){
            if (file.isDirectory()){
                deleteSubfiles(file);
            }
            file.delete();
        }
    }

    @Operation(summary = "Delete existing directory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Directory successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Trying to delete sth that is not a directory"),
            @ApiResponse(responseCode = "404", description = "Directory not found")})
    @DeleteMapping("directory")
    public ResponseEntity<String> deleteExistingDirectory(@RequestParam("path")
                                                              @Parameter(name = "path", description = "Path to directory to be deleted")
                                                                      String dirPath){
        File dir = new File(dirPath);
        if(!dir.exists()){
            return new ResponseEntity<>("Directory not found", HttpStatus.NOT_FOUND);
        }
        if (dir.isDirectory()) {
            deleteSubfiles(dir);
            dir.delete();
            return new ResponseEntity<>("Directory deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Not a directory", HttpStatus.BAD_REQUEST);
    }

    // get size of folder by summing all files in the given directory and child directories (recursive function)
    private long lengthOfDirectory(File folder){
        File[] filesInFolder = folder.listFiles();
        long size = 0;
        assert filesInFolder != null;
        for (File f : filesInFolder){
            if (f.isFile()){
                size = size + f.length();
            } else {
                size = size + lengthOfDirectory(f);
            }
        }
        return size;
    }

    @Operation(summary = "List content of directory", description = "Get list of content of directory ordered by size in ascending order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "404", description = "Directory not found")})
    @GetMapping("directory")
    public ResponseEntity<String> listContentOfDirectory(@RequestParam("path")
                                                             @Parameter(name = "path", description = "Path to folder")
                                                                     String dirPath){
        File dir = new File(dirPath);
        File[] fileArr = dir.listFiles();
        if (fileArr != null) {
            TreeMap<Long, ArrayList<String>> sizeFileMap = new TreeMap<>();

            long size = 0;
            ArrayList<String> value;
            for (File f : fileArr){
                if (f.isDirectory()){
                    size = lengthOfDirectory(f);
                } else {
                    size = f.length();
                }

                if (sizeFileMap.containsKey(size)){
                    value = sizeFileMap.get(size);
                } else {
                    value = new ArrayList<>();
                }
                value.add(f.getName());
                sizeFileMap.put(size, value);
            }
            return new ResponseEntity<>(sizeFileMap.values().toString().replace("[", "").replace("]", ""), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Directory not found", HttpStatus.NOT_FOUND);
        }
    }
}
