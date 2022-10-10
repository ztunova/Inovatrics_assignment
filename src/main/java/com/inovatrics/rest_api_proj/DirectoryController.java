package com.inovatrics.rest_api_proj;

import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.*;

@RestController
public class DirectoryController {

    @PostMapping("directory")
    public void createNewDirectory(@RequestParam("path") String dirPath){
        File newDirectory = new File(dirPath);
        if (!newDirectory.exists()){

            if (newDirectory.mkdirs()){
                System.out.println("new directory created");;
            } else {
                System.out.println("new directory failed to create");
            }
        } else {
            System.out.println("directory already exists");
        }
    }

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

    @DeleteMapping("directory")
    public void deleteExistingDirectory(@RequestParam("path") String dirPath){
        File dir = new File(dirPath);
        deleteSubfiles(dir);
        dir.delete();
    }

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
        System.out.println("size of folder: " + size);
        return size;
    }

    @GetMapping("directory")
    public void listContentOfDirectory(@RequestParam("path") String dirPath){
        //TODO response
        File dir = new File(dirPath);
        File[] fileArr = dir.listFiles();
        if (fileArr != null) {
            TreeMap<Long, ArrayList<String>> sizeFileMap = new TreeMap<>();

            long size = 0;
            ArrayList<String> value;
            for (File f : fileArr){
                System.out.println(f.getName());
                if (f.isDirectory()){
                    System.out.println("directory");
                    size = lengthOfDirectory(f);
                } else {
                    size = f.length();
                }

                if (sizeFileMap.containsKey(size)){
                    value = sizeFileMap.get(size);
                } else {
                    value = new ArrayList<String>();
                }
                value.add(f.getName());
                sizeFileMap.put(size, value);
            }

            System.out.println(sizeFileMap);
        } else {
            System.out.println("empty directory");
        }
    }
}
