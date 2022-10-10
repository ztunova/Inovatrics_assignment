package com.inovatrics.rest_api_proj;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class FileController {
    // TODO nejako doriesit responsy a odpovede

    @PostMapping("file")
    public ResponseEntity<String> createNewFile(@RequestParam("path") String filePath, @RequestParam("content") String fileContent){
        System.out.println("Received file: " + filePath);

        Path myPath = Paths.get(filePath);
        if (Files.exists(myPath)) {
            System.out.println("File already exists");
            return new ResponseEntity<>("File already exists", HttpStatus.BAD_REQUEST);
        }
        else {
            try {
                Files.createFile(myPath);
                System.out.println("File created");
                Files.writeString(myPath, fileContent);
                return new ResponseEntity<>("File Created", HttpStatus.CREATED);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(fileContent);
        return null;
    }

    @DeleteMapping("file")
    public ResponseEntity<String> deleteExistingFile(@RequestParam("path") String filePath) {
        Path path = Paths.get(filePath);
        try {
            Files.deleteIfExists(path);
            return new ResponseEntity<>("File deleted", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("file:copy")
    public ResponseEntity<String> copyFileToTargetDirectory(@RequestParam("srcPath") String srcPath, @RequestParam("dstPath") String dstPath){
        Path source = Paths.get(srcPath);
        Path destination = Paths.get(dstPath);

        try {
            Files.copy(source, destination);
            return new ResponseEntity<>("File copied", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PatchMapping("file")
    public ResponseEntity<String> moveFileToTargetDirectory(@RequestParam("srcPath") String srcPath, @RequestParam("dstPath") String dstPath){
        Path source = Paths.get(srcPath);
        Path destination = Paths.get(dstPath);

        try {
            Files.move(source, destination);
            return new ResponseEntity<>("File moved", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("file")
    public ResponseEntity<String> getContentOfFile(@RequestParam("path") String filePath){
        // TODO response body
        Path path = Paths.get(filePath);
        try {
            String content = Files.readString(path);
            System.out.println(content);
            return new ResponseEntity<>(content, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("file:pattern")
    public ResponseEntity<String> serachForPatternInFileContent(@RequestParam("path") String dirPath, @RequestParam("pattern") String givenPattern){
        Map<String, ArrayList<Integer>> result = new HashMap<>();

        File dir = new File(dirPath);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        };
        File[] dirContent = dir.listFiles(fileFilter);
        System.out.println(Arrays.toString(dirContent));

        Pattern pattern = Pattern.compile(givenPattern);
        Matcher matcher;

        assert dirContent != null;
        for (File file : dirContent) {
            System.out.println(file.getName());
            ArrayList<Integer> linesWithPattern = new ArrayList<>();

            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                int lineNum = 0;

                while ((line = reader.readLine()) != null) {
                    lineNum++;
                    matcher = pattern.matcher(line);

                    if (matcher.find()) {
                        linesWithPattern.add(lineNum);
                        System.out.println(lineNum + line);
                    }
                    //System.out.println(matcher.find());
                }
                if (!linesWithPattern.isEmpty()){
                    result.put(file.getName(), linesWithPattern);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(result);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);

    }
}
