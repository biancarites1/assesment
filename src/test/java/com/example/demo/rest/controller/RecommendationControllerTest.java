package com.example.demo.rest.controller;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.CryptoStatistics;
import com.example.demo.model.NormalizedValueForCrypto;
import com.example.demo.rest.service.RecommendationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static com.example.demo.TestUtils.BTC;
import static com.example.demo.TestUtils.generateNormalizedValues;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RecommendationControllerTest {

    @InjectMocks
    private RecommendationController recommendationController;

    @Mock
    private RecommendationService mockRecommendationService;

    @Test
    public void testGetStatisticsByCrypto() {
        Date oldestDate = new Date();
        Date newestDate = new Date();
        CryptoStatistics mockCryptoStatistics = new CryptoStatistics(BTC, oldestDate, newestDate, 1.0f, 100.0f, 0.54f);
        when(mockRecommendationService.getCryptoStatisticsForSymbol( BTC, null, null)).thenReturn(mockCryptoStatistics);
        CryptoStatistics result = recommendationController.getStatisticsByCrypto(BTC, null, null);
        verify(mockRecommendationService).getCryptoStatisticsForSymbol(BTC, null, null);
        assertEquals(BTC, result.getCrypto());
        assertEquals(oldestDate, result.getOldestDate());
        assertEquals(newestDate, result.getNewestDate());
        assertEquals(100.0f, result.getMaxPrice(), 0);
        assertEquals(1.0f, result.getMinPrice(),0);
    }

    @Test(expected = Exception.class)
    public void testGetStatisticsByCryptoNoData() {
        when(mockRecommendationService.getCryptoStatisticsForSymbol(anyString(), any(), any())).thenThrow(new NotFoundException("No currency found!"));
        recommendationController.getStatisticsByCrypto(BTC, null, null);
        verify(mockRecommendationService).getCryptoStatisticsForSymbol(BTC, null, null);
    }

    @Test
    public void testGetAllNormalizedValues() {
        List<NormalizedValueForCrypto> mockNormalizedValues = generateNormalizedValues();
        when(mockRecommendationService.getNormalizedValuesForAllCryptos(any(), any())).thenReturn(mockNormalizedValues);
        List<NormalizedValueForCrypto> result = recommendationController.getAllNormalizedValues(null, null);
        verify(mockRecommendationService).getNormalizedValuesForAllCryptos(any(), any());
        assertEquals(5, result.size());
        assertEquals(1.23f, result.get(0).getNormalizedValue(), 0);
        assertEquals(BTC, result.get(0).getCurrency());
    }

    @Test(expected = Exception.class)
    public void testGetAllNormalizedValuesNoData(){
        when(mockRecommendationService.getNormalizedValuesForAllCryptos(any(), any())).thenThrow(new NotFoundException("No currency found!"));
        recommendationController.getAllNormalizedValues(null, null);
        verify(mockRecommendationService).getNormalizedValuesForAllCryptos(any(), any());
    }

    @Test
    public void testGetHighestlNormalizedValues() {
        Date date = new Date();

        NormalizedValueForCrypto mockNormalizedValue = new NormalizedValueForCrypto(BTC, 1.2f);
        when(mockRecommendationService.getHighestNormalizedValueForCryptoByDay(any())).thenReturn(mockNormalizedValue);
        NormalizedValueForCrypto result = recommendationController.getHighestNormalizedValue(date);
        verify(mockRecommendationService).getHighestNormalizedValueForCryptoByDay(any());
        assertEquals(1.2f, result.getNormalizedValue(), 0);
        assertEquals(BTC, result.getCurrency());
    }
    @Test(expected = Exception.class)
    public void testGetHighestlNormalizedValuesNoData() {
        when(mockRecommendationService.getHighestNormalizedValueForCryptoByDay(any())).thenThrow(new NotFoundException("No currency found!"));
        recommendationController.getHighestNormalizedValue(new Date());
        verify(mockRecommendationService).getHighestNormalizedValueForCryptoByDay(any());

    }
}