package com.example.demo.rest.service;

import com.example.demo.AppConfig;
import com.example.demo.model.CryptoData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static com.example.demo.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileExtractorServiceTest {

    @InjectMocks
    private FileExtractorService fileExtractorService;

    @Mock
    private AppConfig mockAppConfig;
    private static final String mockCryptoDirectory = "/prices";

    @Test
    public void testExtractFileNames() throws IOException, URISyntaxException {

        Set<String> result = fileExtractorService.extractFileNames( Paths.get(getClass().getResource(mockCryptoDirectory).toURI()).toString());

        Set<String> expected = Set.of(DOGE_values_csv, BTC_values_csv, LTC_values_csv, ETH_values_csv, XRP_values_csv);
        assertEquals(expected, result);

    }

    @Test
    public void testGetFilePath() throws Exception {
        when(mockAppConfig.getCryptoDirectory()).thenReturn(mockCryptoDirectory);

        List<String[]> result = fileExtractorService.readCsvFile(BTC_values_csv);
        assertEquals(102, result.size());
        assertEquals("timestamp", result.get(0)[0]);
        assertEquals("symbol", result.get(0)[1]);
        assertEquals("price", result.get(0)[2]);
    }

    @Test
    public void testExtractCryptoValuesFromFiles() {
        when(mockAppConfig.getCryptoDirectory()).thenReturn(mockCryptoDirectory);
        List<CryptoData> result = fileExtractorService.extractCryptoValuesFromFiles();
        assertTrue(result.stream().anyMatch(cryptoData -> cryptoData.getSymbol().equals(BTC)));
    }
}