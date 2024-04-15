package com.example.demo.rest.service;

import com.example.demo.AppConfig;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.CryptoData;
import com.example.demo.model.CryptoStatistics;
import com.example.demo.model.NormalizedValueForCrypto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.ArrayList;
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
    private static final String mockAllowedCryptos = "BTC,DOGE,LTC";
    @Test
    public void testGetStatisticsBySymbol() {
        Date date = Date.from(Instant.parse("2024-04-15T19:34:50.63Z"));
        List<CryptoData> mockCryptoDataList = generateCryptoData(date, BTC, 100);
        List<CryptoStatistics> expectedCryptoStatistics = generateCryptoStatistics(date, date,1);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        when(mockAppConfig.getAllowedCryptos()).thenReturn(mockAllowedCryptos);

        CryptoStatistics cryptoStatistics = recommendationService.getCryptoStatisticsForSymbol(BTC, null, null);
        assertEquals(expectedCryptoStatistics.get(0).getCrypto(), cryptoStatistics.getCrypto());
        assertEquals(expectedCryptoStatistics.get(0).getOldestDate(), cryptoStatistics.getOldestDate());
        assertEquals(expectedCryptoStatistics.get(0).getNewestDate(), cryptoStatistics.getNewestDate());
        assertEquals(expectedCryptoStatistics.get(0).getMaxPrice(), cryptoStatistics.getMaxPrice());
        assertEquals(expectedCryptoStatistics.get(0).getMinPrice(), cryptoStatistics.getMinPrice());
    }

    @Test(expected= BadRequestException.class)
    public void testGetStatisticsBySymbolEmptyFiles() {
        Date date = new Date();
        List<CryptoData> mockCryptoDataList = new ArrayList<>();
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        recommendationService.getCryptoStatisticsForSymbol(BTC, date, date);
    }

    @Test(expected= NotFoundException.class)
    public void testGetStatisticsBySymbolUnsupportedSymbol() {
        Date date = new Date();
        List<CryptoData> mockCryptoDataList = generateCryptoData(date, BTC, 100);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        when(mockAppConfig.getAllowedCryptos()).thenReturn(BNB);
        recommendationService.getCryptoStatisticsForSymbol(BTC, date, date);
   }

    @Test(expected= NotFoundException.class)
    public void testGetStatisticsNoSymbol() {
        Date date = new Date();
        List<CryptoData> mockCryptoDataList = generateCryptoData(date, BTC, 100);
        generateCryptoStatistics(date, date,1);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        when(mockAppConfig.getAllowedCryptos()).thenReturn(LTC);
        recommendationService.getCryptoStatisticsForSymbol(null, date, date);
    }

    @Test
    public void testGetStatisticsBySymbolAndDate() {
        Date date = Date.from(Instant.parse("2024-04-15T19:34:50.63Z"));
        Date startDate = Date.from(Instant.parse("2024-04-15T19:34:50.63Z"));
        Date endDate = Date.from(Instant.parse("2024-04-18T19:34:50.63Z"));
        List<CryptoData> mockCryptoDataList = generateCryptoData(date, BTC, 100);
        List<CryptoStatistics> expectedCryptoStatistics = generateCryptoStatistics(date, date,1);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        when(mockAppConfig.getAllowedCryptos()).thenReturn(mockAllowedCryptos);

        CryptoStatistics cryptoStatistics = recommendationService.getCryptoStatisticsForSymbol(BTC, startDate, endDate);
        assertEquals(expectedCryptoStatistics.get(0).getCrypto(), cryptoStatistics.getCrypto());
        assertEquals(expectedCryptoStatistics.get(0).getOldestDate(), cryptoStatistics.getOldestDate());
        assertEquals(expectedCryptoStatistics.get(0).getNewestDate(), cryptoStatistics.getNewestDate());
        assertEquals(expectedCryptoStatistics.get(0).getMaxPrice(), cryptoStatistics.getMaxPrice());
        assertEquals(expectedCryptoStatistics.get(0).getMinPrice(), cryptoStatistics.getMinPrice());
    }

    @Test
    public void testGetStatisticsBySymbolSameDate() {
        Date date = Date.from(Instant.parse("2024-04-15T19:34:50.63Z"));
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
    public void testGetStatisticsBySymbolInvalidDate() {
        Date date = Date.from(Instant.parse("2024-04-11T19:34:50.63Z"));
        Date startDate = Date.from(Instant.parse("2024-04-15T19:34:50.63Z"));
        Date endDate = Date.from(Instant.parse("2024-04-18T19:34:50.63Z"));
        List<CryptoData> mockCryptoDataList = generateCryptoData(date, BTC, 100);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        when(mockAppConfig.getAllowedCryptos()).thenReturn(mockAllowedCryptos);
        CryptoStatistics cryptoStatistics = recommendationService.getCryptoStatisticsForSymbol(BTC, startDate, endDate);
        assertEquals(null, cryptoStatistics);
    }
    @Test
    public void testGetNormalizedValuesForAllCryptos() {
        Date date = new Date();
        List<CryptoData> mockCryptoDataList = Arrays.asList(new CryptoData(date, BTC, 100f),new CryptoData(date, BTC, 10f),
                new CryptoData(date, LTC, 70f),new CryptoData(date, LTC, 10f));
        when(mockAppConfig.getAllowedCryptos()).thenReturn(mockAllowedCryptos);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        List<NormalizedValueForCrypto> expectedNormalizedValues = Arrays.asList(new NormalizedValueForCrypto("BTC", 9.0f), new NormalizedValueForCrypto("LTC", 6.0f));
        List<NormalizedValueForCrypto> result = recommendationService.getNormalizedValuesForAllCryptos(null, null);
        assertEquals(expectedNormalizedValues.size(), result.size());
        assertEquals(expectedNormalizedValues.get(0).getNormalizedValue(), result.get(0).getNormalizedValue(), 0);
        assertEquals(expectedNormalizedValues.get(0).getCurrency(), result.get(0).getCurrency());
    }

    @Test(expected = BadRequestException.class)
    public void testGetNormalizedValuesForAllCryptosEmptyFiles() {
        Date date = new Date();
        List<CryptoData> mockCryptoDataList = new ArrayList<>();
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        recommendationService.getNormalizedValuesForAllCryptos(date, date);
    }
    @Test(expected = NotFoundException.class)
    public void testGetNormalizedValuesForAllCryptosUnsupportedSymbol() {
        Date date = new Date();
        List<CryptoData> mockCryptoDataList = generateCryptoData(date, BTC, 100);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        when(mockAppConfig.getAllowedCryptos()).thenReturn(BNB);
        recommendationService.getNormalizedValuesForAllCryptos(date, date);
    }

    @Test
    public void testGetNormalizedValuesForAllCryptosDate() {
        Date date = Date.from(Instant.parse("2024-04-15T19:34:50.63Z"));
        Date startDate = Date.from(Instant.parse("2024-04-15T19:34:50.63Z"));
        Date endDate = Date.from(Instant.parse("2024-04-18T19:34:50.63Z"));
        List<CryptoData> mockCryptoDataList = Arrays.asList(new CryptoData(date, BTC, 100f),new CryptoData(date, BTC, 10f),
                new CryptoData(date, LTC, 70f),new CryptoData(date, LTC, 10f));
        when(mockAppConfig.getAllowedCryptos()).thenReturn(mockAllowedCryptos);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        List<NormalizedValueForCrypto> expectedNormalizedValues = Arrays.asList(new NormalizedValueForCrypto("BTC", 9.0f), new NormalizedValueForCrypto("LTC", 6.0f));
        List<NormalizedValueForCrypto> result = recommendationService.getNormalizedValuesForAllCryptos(startDate, endDate);
        assertEquals(expectedNormalizedValues.size(), result.size());
        assertEquals(expectedNormalizedValues.get(0).getNormalizedValue(), result.get(0).getNormalizedValue(), 0);
        assertEquals(expectedNormalizedValues.get(0).getCurrency(), result.get(0).getCurrency());
    }

    @Test
    public void testGetNormalizedValuesForAllCryptosInvalidDate() {
        Date date = Date.from(Instant.parse("2024-04-11T19:34:50.63Z"));
        Date startDate = Date.from(Instant.parse("2024-04-15T19:34:50.63Z"));
        Date endDate = Date.from(Instant.parse("2024-04-18T19:34:50.63Z"));

        List<CryptoData> mockCryptoDataList = Arrays.asList(new CryptoData(date, BTC, 100f),new CryptoData(date, BTC, 10f),
                new CryptoData(date, LTC, 70f),new CryptoData(date, LTC, 10f));
        when(mockAppConfig.getAllowedCryptos()).thenReturn(mockAllowedCryptos);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        List<NormalizedValueForCrypto> result = recommendationService.getNormalizedValuesForAllCryptos(startDate, endDate);
        assertEquals(0, result.size());

    }
    @Test
    public void testGetNormalizedValuesForAllCryptosSameDate() {
        Date date = Date.from(Instant.parse("2024-04-15T19:34:50.63Z"));
        List<CryptoData> mockCryptoDataList = Arrays.asList(new CryptoData(date, BTC, 100f),new CryptoData(date, BTC, 10f),
                new CryptoData(date, LTC, 70f),new CryptoData(date, LTC, 10f));
        when(mockAppConfig.getAllowedCryptos()).thenReturn(mockAllowedCryptos);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        List<NormalizedValueForCrypto> expectedNormalizedValues = Arrays.asList(new NormalizedValueForCrypto("BTC", 9.0f), new NormalizedValueForCrypto("LTC", 6.0f));
        List<NormalizedValueForCrypto> result = recommendationService.getNormalizedValuesForAllCryptos(date, date);
        assertEquals(expectedNormalizedValues.size(), result.size());
        assertEquals(expectedNormalizedValues.get(0).getNormalizedValue(), result.get(0).getNormalizedValue(), 0);
        assertEquals(expectedNormalizedValues.get(0).getCurrency(), result.get(0).getCurrency());
    }

    @Test
    public void testGetNormalizedValuesForAllCryptosSameInvalidDate() {
        Date date = Date.from(Instant.parse("2024-04-15T19:34:50.63Z"));
        Date startDate = Date.from(Instant.parse("2024-04-11T19:34:50.63Z"));
        List<CryptoData> mockCryptoDataList = Arrays.asList(new CryptoData(date, BTC, 100f),new CryptoData(date, BTC, 10f),
                new CryptoData(date, LTC, 70f),new CryptoData(date, LTC, 10f));
        when(mockAppConfig.getAllowedCryptos()).thenReturn(mockAllowedCryptos);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        List<NormalizedValueForCrypto> result = recommendationService.getNormalizedValuesForAllCryptos(startDate, startDate);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetHighestNormalizedValueForCryptoByDay() {
        Date date = new Date();
        List<CryptoData> mockCryptoDataList = Arrays.asList(new CryptoData(date, BTC, 100f),new CryptoData(date, BTC, 10f));
        when(mockAppConfig.getAllowedCryptos()).thenReturn(mockAllowedCryptos);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        NormalizedValueForCrypto expectedNormalizedValue = new NormalizedValueForCrypto("BTC", 9.0f);
        NormalizedValueForCrypto result = recommendationService.getHighestNormalizedValueForCryptoByDay(date);
        assertEquals(expectedNormalizedValue.getNormalizedValue(), result.getNormalizedValue(), 0);
        assertEquals(expectedNormalizedValue.getCurrency(), result.getCurrency());
    }

    @Test(expected = BadRequestException.class)
    public void testGetHighestNormalizedValueForCryptoByDayEmptyFiles() {
        Date date = new Date();
        List<CryptoData> mockCryptoDataList = new ArrayList<>();
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        recommendationService.getHighestNormalizedValueForCryptoByDay(date);
    }
    @Test(expected = NotFoundException.class)
    public void testGetHighestNormalizedValueForCryptoByDayUnsupportedSymbol() {
        Date date = new Date();
        List<CryptoData> mockCryptoDataList = generateCryptoData(date, BTC, 100);
        when(mockFileExtractorService.getCryptoDataList()).thenReturn(mockCryptoDataList);
        when(mockAppConfig.getAllowedCryptos()).thenReturn(BNB);
        recommendationService.getHighestNormalizedValueForCryptoByDay(date);
    }
}