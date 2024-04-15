package com.example.demo;

import com.example.demo.model.CryptoData;
import com.example.demo.model.CryptoStatistics;
import com.example.demo.model.NormalizedValueForCrypto;

import java.util.*;

public class TestUtils {
    public final static String BTC = "BTC";

    public static List<CryptoData> generateCryptoData(Date date, String currency, int length) {
        List<CryptoData> cryptoDataList = new ArrayList<>();
        for(int i=0;i<length;i++){
            CryptoData cryptoData = new CryptoData(date, currency, i);
            cryptoDataList.add(cryptoData);
        }
        return cryptoDataList;
    }

    public static List<CryptoStatistics> generateCryptoStatistics(Date oldestDate, Date newestDate, int length) {
        List<CryptoStatistics> cryptoStatisticsList = new ArrayList<>();
        for(int i=0;i<length;i++){
            CryptoStatistics statistics = generateNewCryptoStatistics(oldestDate, newestDate);
            cryptoStatisticsList.add(statistics);
        }
        return cryptoStatisticsList;
    }

    public static CryptoStatistics generateNewCryptoStatistics(Date oldestDate, Date newestDate) {
        return new CryptoStatistics(BTC, oldestDate, newestDate, 0.0f, 99f, 0.5f);
    }

    public static List<NormalizedValueForCrypto> generateNormalizedValues() {
        List<NormalizedValueForCrypto> normalizedValueForCryptos = new ArrayList<>();
        normalizedValueForCryptos.add(new NormalizedValueForCrypto("BTC", 1.23f));
        normalizedValueForCryptos.add(new NormalizedValueForCrypto("DOGE", 1.03f));
        normalizedValueForCryptos.add(new NormalizedValueForCrypto("ETH", 0.89f));
        normalizedValueForCryptos.add(new NormalizedValueForCrypto("LTC", 0.51f));
        normalizedValueForCryptos.add(new NormalizedValueForCrypto("XRP", 0.20f));
        return normalizedValueForCryptos;
    }
}
