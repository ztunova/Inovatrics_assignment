package com.inovatrics.rest_api_proj;

import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class DirectoryController {

    @PostMapping("directory")
    public void createNewDirectory(@RequestParam("path") String dirPath){
       // Path path = Paths.get(dirPath);
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

    @DeleteMapping("/directory/{path}")
    public void deleteExistingDirectory(@PathVariable("path") String path){}

    @GetMapping("/directory/{path}")
    public void listContentOfDirectory(@PathVariable("path") String path){
        //ordered by size
    }



}
