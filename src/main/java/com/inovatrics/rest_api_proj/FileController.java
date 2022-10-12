package com.inovatrics.rest_api_proj;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    @Operation(summary = "Create new file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File successfully created"),
            @ApiResponse(responseCode = "400", description = "Given file already exists"),
            @ApiResponse(responseCode = "500", description = "Other error")})
    @PostMapping("file")
    public ResponseEntity<String> createNewFile(@RequestParam("path")
                                                    @Parameter(name = "path", description = "path to created file") String filePath,
                                                @RequestParam("content")
                                                @Parameter(name = "content", description = "content of new file")
                                                        String fileContent){
        Path myPath = Paths.get(filePath);
        try {
            Files.createFile(myPath);
            Files.writeString(myPath, fileContent);
            return new ResponseEntity<>("File Created", HttpStatus.CREATED);
        } catch (FileAlreadyExistsException fe) {
            return new ResponseEntity<>("File already exists", HttpStatus.BAD_REQUEST);
        } catch (IOException e){
            return new ResponseEntity<>(e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete existing file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Given file does not exist"),
            @ApiResponse(responseCode = "500", description = "Other error")})
    @DeleteMapping("file")
    public ResponseEntity<String> deleteExistingFile(@RequestParam("path")
                                                         @Parameter(name = "path", description = "Path to file to be deleted")
                                                                 String filePath) {
        Path path = Paths.get(filePath);
        try {
            Files.delete(path);
            return new ResponseEntity<>("File deleted", HttpStatus.OK);
        } catch (NoSuchFileException noFile) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        } catch (IOException e){
            return new ResponseEntity<>(e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Copy file to target directory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File successfully copied"),
            @ApiResponse(responseCode = "400", description = "Destination file already exists"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Other error")})
    @PostMapping("file:copy")
    public ResponseEntity<String> copyFileToTargetDirectory(@RequestParam("srcPath")
                                                                @Parameter(name = "srcPath", description = "Path to file to be copied")
                                                                        String srcPath,
                                                            @RequestParam("dstPath") @Parameter(name = "dstPath", description = "Path to copied file")
                                                                    String dstPath){
        Path source = Paths.get(srcPath);
        Path destination = Paths.get(dstPath);

        try {
            Files.copy(source, destination);
            return new ResponseEntity<>("File copied", HttpStatus.OK);
        } catch(FileAlreadyExistsException fe){
            return new ResponseEntity<>("Destination file already exists", HttpStatus.BAD_REQUEST);
        } catch (NoSuchFileException no){
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        } catch (IOException e){
            return new ResponseEntity<>(e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Move file to target directory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File successfully moved"),
            @ApiResponse(responseCode = "400", description = "Destination file already exists"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Other error")})
    @PatchMapping("file")
    public ResponseEntity<String> moveFileToTargetDirectory(@RequestParam("srcPath")
                                                                @Parameter(name = "srcPath", description = "Path to file to be moved")
                                                                        String srcPath,
                                                            @RequestParam("dstPath")
                                                            @Parameter(name = "dstPath", description = "Path to moved file")
                                                                    String dstPath){
        Path source = Paths.get(srcPath);
        Path destination = Paths.get(dstPath);

        try {
            Files.move(source, destination);
            return new ResponseEntity<>("File moved", HttpStatus.OK);
        } catch(FileAlreadyExistsException fe){
            return new ResponseEntity<>("Destination file already exists", HttpStatus.BAD_REQUEST);
        } catch (NoSuchFileException no){
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        } catch (IOException e){
            return new ResponseEntity<>(e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get content of file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Content of file successfully read"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Other error")})
    @GetMapping("file")
    public ResponseEntity<String> getContentOfFile(@RequestParam("path")
                                                       @Parameter(name = "path", description = "Path to file to be read")
                                                               String filePath){

        Path path = Paths.get(filePath);
        try {
            String content = Files.readString(path);
            return new ResponseEntity<>(content, HttpStatus.OK);
        } catch (NoSuchFileException no){
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        } catch (IOException e){
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
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
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
                return new ResponseEntity<>(e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        System.out.println(result);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);

    }
}
