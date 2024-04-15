package com.example.demo.rest.service;

import com.example.demo.model.CryptoData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static com.example.demo.TestUtils.BTC;
import static com.example.demo.rest.service.FileExtractorService.PRICES_DIRECTORY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class FileExtractorServiceTest {

    @InjectMocks
    private FileExtractorService fileExtractorService;

    @Test
    public void testExtractFileNames() throws IOException, URISyntaxException {

        Set<String> result = fileExtractorService.extractFileNames( Paths.get(getClass().getResource(PRICES_DIRECTORY).toURI()).toString());

        Set<String> expected = Set.of("DOGE_values.csv", "BTC_values.csv", "LTC_values.csv", "ETH_values.csv", "XRP_values.csv");
        assertEquals(expected, result);

    }

//    @Test
//    public void testGetDirectoryName() throws URISyntaxException {
//        String actualDirectoryName = fileExtractorService.getDirectoryName();
//        String expectedDirectoryName = Paths.get(getClass().getResource(PRICES_DIRECTORY).toURI()).toString();
//        assertEquals(expectedDirectoryName, actualDirectoryName);
//    }

    @Test
    public void testGetFilePath() throws Exception {
        List<String[]> result = fileExtractorService.readCsvFile( "BTC_values.csv");
        assertEquals(102, result.size());
        assertEquals("timestamp", result.get(0)[0]);
        assertEquals("symbol", result.get(0)[1]);
        assertEquals("price", result.get(0)[2]);
    }

    @Test
    public void testExtractCryptoValuesFromFiles() {
        List<CryptoData> result = fileExtractorService.extractCryptoValuesFromFiles();
        assertTrue(result.stream().anyMatch(cryptoData -> cryptoData.getSymbol().equals(BTC)));
    }
}