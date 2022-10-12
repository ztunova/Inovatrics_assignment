package com.inovatrics.rest_api_proj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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
    void createNewFileTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/c/newCreated.txt";
        Files.deleteIfExists(Paths.get(path));
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
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/toBeDeleted.txt";
        Files.createFile(Paths.get(path));

        assert Files.exists(Paths.get(path));
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/file")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("File deleted"));

        assertFalse(Files.exists(Paths.get(path)));
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
        Files.deleteIfExists(Paths.get(dstPath));

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
        String srcPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/piaty.txt";
        String dstPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/a/piaty.txt";
        Files.move(Paths.get(dstPath), Paths.get(srcPath));

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

    @Test
    void getContentOfFileTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/prvy.txt";
        String content = """
                100 Continue
                This interim response indicates that the client should continue the request or ignore the response if the request is already finished.

                101 Switching Protocols
                This code is sent in response to an Upgrade request header from the client and indicates the protocol the server is switching to.

                102 Processing
                This code indicates that the server has received and is processing the request, but no response is available yet.""";

        content = content.replace("\n", "").replace("\r", "");

        assert Files.exists(Paths.get(path));
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/file")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString().replace("\n", "").replace("\r", "");

        assert content.equals(responseContent);
    }

    @Test
    void getContentOfEmptyFileTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/empty.txt";

        assert Files.exists(Paths.get(path));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/file")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    void getContentOfNotExistingFileTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/abc.txt";

        assertFalse(Files.exists(Paths.get(path)));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/file")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("File not found"));
    }

    @Test
    void findPatternNotExistingDirectoryTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/xyz";
        String pattern = "anything";

        mockMvc.perform(
                MockMvcRequestBuilders.get("/file:pattern")
                        .param("path", path)
                        .param("pattern", pattern)
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("File not found"));
    }

    @Test
    void findPatternEmptyDirectoryTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/emptyDir";
        String pattern = "anything";

        mockMvc.perform(
                MockMvcRequestBuilders.get("/file:pattern")
                        .param("path", path)
                        .param("pattern", pattern)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{}"));
    }

    @Test
    void findPatternTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest";
        String pattern = "request";

        mockMvc.perform(
                MockMvcRequestBuilders.get("/file:pattern")
                        .param("path", path)
                        .param("pattern", pattern)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{prvy.txt=[2, 5, 8], druhy.txt=[2, 7, 10]}"));
    }

}