package com.inovatrics.rest_api_proj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
class DirectoryControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private final DirectoryController directoryController = new DirectoryController();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(directoryController).build();
    }

    @Test
    void createAndDeleteDirectoryTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/newDir";

        mockMvc.perform(
                MockMvcRequestBuilders.post("/directory")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Directory created"));

        assert Files.exists(Paths.get(path));

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/directory")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Directory deleted"));

        assertFalse(Files.exists(Paths.get(path)));
    }

    @Test
    void createAndDeleteNotEmptyDirectoryTest() throws Exception {
        String dirPath = "C:/Users/HP/Desktop/InovatricsUlohaTest/newDir1/newDir2/newDir3";

        mockMvc.perform(
                MockMvcRequestBuilders.post("/directory")
                        .param("path", dirPath)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Directory created"));

        assert Files.exists(Paths.get(dirPath));

        String[] nf = new String[]{"C:/Users/HP/Desktop/InovatricsUlohaTest/newDir1/nf1.txt",
                "C:/Users/HP/Desktop/InovatricsUlohaTest/newDir1/newDir2/nf2.txt",
                "C:/Users/HP/Desktop/InovatricsUlohaTest/newDir1/newDir2/nf3.txt",
                "C:/Users/HP/Desktop/InovatricsUlohaTest/newDir1/newDir2/newDir3/nf4.txt"};
        for (String p : nf) {
            Files.createFile(Paths.get(p));
        }

        String parentDirToDelete = "C:/Users/HP/Desktop/InovatricsUlohaTest/newDir1";
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/directory")
                        .param("path", parentDirToDelete)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Directory deleted"));

        assertFalse(Files.exists(Paths.get(dirPath)));
    }

    @Test
    void deleteNotExistingDirectoryTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/xyz";

        assertFalse(Files.exists(Paths.get(path)));
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/directory")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Directory not found"));
    }

    @Test
    void deleteNotDirectoryTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/prvy.txt";

        assert Files.exists(Paths.get(path));
        assertFalse(new File(path).isDirectory());
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/directory")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Not a directory"));
    }

    @Test
    void listContentOfDirecotryTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest";

        assert Files.exists(Paths.get(path));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/directory")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("empty.txt, emptyDir, newCreated.txt, prvy.txt, druhy.txt, a"));
    }

    @Test
    void listContentOfEmptyDirecotryTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/emptyDir";

        assert Files.exists(Paths.get(path));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/directory")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    void listContentOfNotExistingDirecotryTest() throws Exception {
        String path = "C:/Users/HP/Desktop/InovatricsUlohaTest/xyz";

        assertFalse(Files.exists(Paths.get(path)));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/directory")
                        .param("path", path)
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Directory not found"));
    }

}