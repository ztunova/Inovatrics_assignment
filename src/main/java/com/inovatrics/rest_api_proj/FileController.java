package com.inovatrics.rest_api_proj;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class FileController {

    @GetMapping("pomoc")
    public String pomocna(){
        return "Pomoc";
    }

    @PostMapping("file")
    public ResponseEntity<String> createNewFile(@RequestParam("path") String filePath, @RequestParam("content") String fileContent){
        System.out.println("Received file: " + filePath);

        Path myPath = Paths.get(filePath);
        /*if (Files.exists(myPath)) {
            System.out.println("File already exists");
            return new ResponseEntity<>("File already exists", HttpStatus.BAD_REQUEST);
        }
        else {*/
            try {
                Files.createFile(myPath);
                System.out.println("File created");
                Files.writeString(myPath, fileContent);
                return new ResponseEntity<>("File Created", HttpStatus.CREATED);
            } catch (FileAlreadyExistsException fe) {
                //TODO response
                //fe.printStackTrace();
                return new ResponseEntity<>("File already exists", HttpStatus.BAD_REQUEST);
            } catch (IOException e){
                e.printStackTrace();
                return new ResponseEntity<>(e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        //}
    }

    @DeleteMapping("file")
    public ResponseEntity<String> deleteExistingFile(@RequestParam("path") String filePath) {
        //nenajdeny vyhodit exception, 404, 500 io, 400 no directory
        Path path = Paths.get(filePath);
        try {
            Files.delete(path);
            return new ResponseEntity<>("File deleted", HttpStatus.OK);
        } catch (NoSuchFileException noFile) {
            //TODO response
            //noFile.printStackTrace();
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        } catch (IOException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("file:copy")
    public ResponseEntity<String> copyFileToTargetDirectory(@RequestParam("srcPath") String srcPath, @RequestParam("dstPath") String dstPath){
        Path source = Paths.get(srcPath);
        Path destination = Paths.get(dstPath);

        try {
            Files.copy(source, destination);
            return new ResponseEntity<>("File copied", HttpStatus.OK);
        } catch(FileAlreadyExistsException fe){
            //fe.printStackTrace();
            return new ResponseEntity<>("Destination file already exists", HttpStatus.BAD_REQUEST);
        } catch (NoSuchFileException no){
            //no.printStackTrace();
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        } catch (IOException e){
            //e.printStackTrace();
            return new ResponseEntity<>(e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("file")
    public ResponseEntity<String> moveFileToTargetDirectory(@RequestParam("srcPath") String srcPath, @RequestParam("dstPath") String dstPath){
        Path source = Paths.get(srcPath);
        Path destination = Paths.get(dstPath);

        try {
            Files.move(source, destination);
            return new ResponseEntity<>("File moved", HttpStatus.OK);
        } catch(FileAlreadyExistsException fe){
            //fe.printStackTrace();
            return new ResponseEntity<>("Destination file already exists", HttpStatus.BAD_REQUEST);
        } catch (NoSuchFileException no){
            //no.printStackTrace();
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        } catch (IOException e){
            //e.printStackTrace();
            return new ResponseEntity<>(e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("file")
    public ResponseEntity<String> getContentOfFile(@RequestParam("path") String filePath){

        Path path = Paths.get(filePath);
        try {
            String content = Files.readString(path);
            System.out.println(content);
            return new ResponseEntity<>(content, HttpStatus.OK);
        } catch(OutOfMemoryError o){
            o.printStackTrace();
            return new ResponseEntity<>("File too large", HttpStatus.INSUFFICIENT_STORAGE);
        } catch (NoSuchFileException no){
            no.printStackTrace();
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        } catch(AccessDeniedException ad){
            ad.printStackTrace();
            return new ResponseEntity<>("Method not allowed", HttpStatus.METHOD_NOT_ALLOWED);
        } catch (IOException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

        //TODO fix response for empty dir
        if (dirContent == null){
            return new ResponseEntity<>("Empty directory", HttpStatus.BAD_REQUEST);
        }
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

            } catch(FileNotFoundException fnf){
                fnf.printStackTrace();
                return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
            } catch (IOException e) {
                //TODO response
                e.printStackTrace();
                return new ResponseEntity<>(e.getClass().getSimpleName(), HttpStatus.BAD_REQUEST);
            }
        }

        System.out.println(result);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);

    }
}
