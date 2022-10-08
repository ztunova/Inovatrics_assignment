package com.inovatrics.rest_api_proj;

import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {
    // TODO nejako doriesit responsy a odpovede

    @PostMapping("file")
    public void createNewFile(@RequestParam("path") String filePath, @RequestParam("content") String fileContent){
        System.out.println("Received file: " + filePath);

        Path myPath = Paths.get(filePath);
        if (Files.exists(myPath)) {
            System.out.println("File already exists");
        }
        else {
            try {
                Files.createFile(myPath);
                System.out.println("File created");
                Files.writeString(myPath, fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(fileContent);
    }

    @DeleteMapping("file")
    public void deleteExistingFile(@RequestParam("path") String filePath) {
        Path path = Paths.get(filePath);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("file:copy")
    public void copyFileToTargetDirectory(@RequestParam("srcPath") String srcPath, @RequestParam("dstPath") String dstPath){
        Path source = Paths.get(srcPath);
        Path destination = Paths.get(dstPath);

        try {
            Files.copy(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PatchMapping("file")
    public void moveFileToTargetDirectory(@RequestParam("srcPath") String srcPath, @RequestParam("dstPath") String dstPath){
        Path source = Paths.get(srcPath);
        Path destination = Paths.get(dstPath);

        try {
            Files.move(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("file")
    public void getContentOfFile(@RequestParam("path") String filePath){
        // TODO response body
        Path path = Paths.get(filePath);
        try {
            String content = Files.readString(path);
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serachForPatternInFileContent(){
        // TODO get the list of files where the given pattern occurs
        // TODO get the line number (per file) where the given pattern occurs
    }
}
