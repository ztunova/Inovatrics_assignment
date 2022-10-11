package com.inovatrics.rest_api_proj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
class FileControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private final FileController fileController = new FileController();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    @Test
    void getPomocTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/pomoc")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Pomoc"));
    }

    @Test
    void createNewFileTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/siesty.txt";
        String content = "nejaky obsah suboru";

        mockMvc.perform(
                MockMvcRequestBuilders.post("/file")
                .param("path", path)
                .param("content", content)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("File Created"));

        assert Files.exists(Paths.get(path));
        assert Files.readString(Paths.get(path)).equals(content);
        Files.delete(Paths.get(path));
    }

    @Test
    void createNewFileAlreadyExistsTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/prvy.txt";
        String content = "pomoc prosim";

        assert Files.exists(Paths.get(path));
        mockMvc.perform(
                MockMvcRequestBuilders.post("/file")
                        .param("path", path)
                        .param("content", content)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("File already exists"));

    }

    @Test
    void deleteExistingFileTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/treti.txt";

        assert Files.exists(Paths.get(path));
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/file")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("File deleted"));

        assertFalse(Files.exists(Paths.get(path)));
        Files.createFile(Paths.get(path));
    }

    @Test
    void deleteNotExistingFile() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/xyz.txt";

        assertFalse(Files.exists(Paths.get(path)));
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/file")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("File not found"));
    }

    @Test
    void copyFileToTargetDirectoryTest() throws Exception {
        String srcPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/druhy.txt";
        String dstPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/a/druhy_copy.txt";

        assert Files.exists(Paths.get(srcPath));
        assertFalse(Files.exists(Paths.get(dstPath)));
        mockMvc.perform(
                MockMvcRequestBuilders.post("/file:copy")
                        .param("srcPath", srcPath)
                        .param("dstPath", dstPath)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("File copied"));

        assert Files.exists(Paths.get(srcPath));
        assert Files.exists(Paths.get(dstPath));
        Files.delete(Paths.get(dstPath));
    }

    @Test
    void copyFileToTargetDirectoryFileExistsTest() throws Exception {
        String srcPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/druhy.txt";
        String dstPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/a/stvrty.txt";

        assert Files.exists(Paths.get(srcPath));
        assert Files.exists(Paths.get(dstPath));
        mockMvc.perform(
                MockMvcRequestBuilders.post("/file:copy")
                        .param("srcPath", srcPath)
                        .param("dstPath", dstPath)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Destination file already exists"));
    }

    @Test
    void copyFileToTargetDirectoryNoFileTest() throws Exception {
        String srcPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/abc.txt";
        String dstPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/a/abc_copy.txt";

        assertFalse(Files.exists(Paths.get(srcPath)));
        mockMvc.perform(
                MockMvcRequestBuilders.post("/file:copy")
                        .param("srcPath", srcPath)
                        .param("dstPath", dstPath)
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("File not found"));
    }

    @Test
    void moveFileToTargetDirectoryTest() throws Exception {
        String srcPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/druhy.txt";
        String dstPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/a/druhy.txt";

        assert Files.exists(Paths.get(srcPath));
        assertFalse(Files.exists(Paths.get(dstPath)));
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/file")
                        .param("srcPath", srcPath)
                        .param("dstPath", dstPath)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("File moved"));

        assertFalse (Files.exists(Paths.get(srcPath)));
        assert Files.exists(Paths.get(dstPath));
        Files.move(Paths.get(dstPath), Paths.get(srcPath));
    }

    @Test
    void moveFileToTargetDirectoryFileExistsTest() throws Exception {
        String srcPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/druhy.txt";
        String dstPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/a/stvrty.txt";

        assert Files.exists(Paths.get(srcPath));
        assert Files.exists(Paths.get(dstPath));
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/file")
                        .param("srcPath", srcPath)
                        .param("dstPath", dstPath)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Destination file already exists"));
    }

    @Test
    void moveFileToTargetDirectoryNoFileTest() throws Exception {
        String srcPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/abc.txt";
        String dstPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/a/abc_copy.txt";

        assertFalse(Files.exists(Paths.get(srcPath)));
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/file")
                        .param("srcPath", srcPath)
                        .param("dstPath", dstPath)
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("File not found"));
    }

}