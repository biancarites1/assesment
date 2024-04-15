package com.example.demo.rest.service;

import com.example.demo.AppConfig;
import com.example.demo.model.CryptoData;
import com.example.demo.model.CryptoStatistics;
import com.example.demo.model.NormalizedValueForCrypto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.example.demo.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private FileExtractorService mockFileExtractorService;

    @Mock
    private AppConfig mockAppConfig;
    private static final String mockAllowedCryptos = "BTC,DOGE";
    @Test
    public void testGetStatisticsBySymbol() throws Exception {
        Date date = new Date();
        List<CryptoData> mockCryptoDataList = generateCryptoData(date, BTC, 100);
        List<CryptoStatistics> expectedCryptoStatistics = generateCryptoStatistics(date, date,1);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        when(mockAppConfig.getAllowedCryptos()).thenReturn(mockAllowedCryptos);

        CryptoStatistics cryptoStatistics = recommendationService.getCryptoStatisticsForSymbol(BTC, date, date);
        assertEquals(expectedCryptoStatistics.get(0).getCrypto(), cryptoStatistics.getCrypto());
        assertEquals(expectedCryptoStatistics.get(0).getOldestDate(), cryptoStatistics.getOldestDate());
        assertEquals(expectedCryptoStatistics.get(0).getNewestDate(), cryptoStatistics.getNewestDate());
        assertEquals(expectedCryptoStatistics.get(0).getMaxPrice(), cryptoStatistics.getMaxPrice());
        assertEquals(expectedCryptoStatistics.get(0).getMinPrice(), cryptoStatistics.getMinPrice());
    }

    @Test
    public void testGetHighestNormalizedValueForCryptoByDay() throws Exception {
        Date date = new Date();
        List<CryptoData> mockCryptoDataList = Arrays.asList(new CryptoData(date, BTC, 100f),new CryptoData(date, BTC, 10f));
        when(mockAppConfig.getAllowedCryptos()).thenReturn(mockAllowedCryptos);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        NormalizedValueForCrypto expectedNormalizedValue = new NormalizedValueForCrypto("BTC", 9.0f);
        NormalizedValueForCrypto result = recommendationService.getHighestNormalizedValueForCryptoByDay(date);
        assertEquals(expectedNormalizedValue.getNormalizedValue(), result.getNormalizedValue(), 0);
        assertEquals(expectedNormalizedValue.getCurrency(), result.getCurrency());
    }
}