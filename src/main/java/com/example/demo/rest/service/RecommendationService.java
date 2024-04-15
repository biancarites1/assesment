package com.example.demo.rest.service;

import com.example.demo.AppConfig;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.CryptoData;
import com.example.demo.model.CryptoStatistics;
import com.example.demo.model.NormalizedValueForCrypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;



@Service
public class RecommendationService {
    @Autowired
    private FileExtractorService fileExtractorService;
    @Autowired
    private AppConfig appConfig;

    public CryptoStatistics getCryptoStatisticsForSymbol(String symbol, Date requestedDay) throws Exception {
        return generateAllCryptoStatistics(validateCryptoDataList(), requestedDay).stream().filter((cryptoStatistics -> cryptoStatistics.getCrypto().equals(symbol))).findFirst().get();
    }

    public List<NormalizedValueForCrypto> getNormalizedValuesForAllCryptos(Date day) throws Exception {
        List<CryptoStatistics> cryptoStatisticsList = generateAllCryptoStatistics(validateCryptoDataList(), day);
        Collections.sort(cryptoStatisticsList, ((Comparator<CryptoStatistics>) (o1, o2) -> Float.compare(o1.getNormalizedRange(), o2.getNormalizedRange())).reversed());
        return cryptoStatisticsList.stream().map(statistics -> new NormalizedValueForCrypto(statistics.getCrypto(), statistics.getNormalizedRange())).collect(Collectors.toList());

    }

    public NormalizedValueForCrypto getHighestNormalizedValueForCryptoByDay(Date day) throws Exception {
        List<NormalizedValueForCrypto> normalizedValueForCryptos = getNormalizedValuesForAllCryptos(day);
        return normalizedValueForCryptos.get(0);
    }

    private List<CryptoData> validateCryptoDataList() {
        List<CryptoData> cryptoDataList = fileExtractorService.getCryptoDataList();
        if (cryptoDataList.isEmpty()) {
            throw new BadRequestException("Crypto data files are empty");
        }
        return cryptoDataList;
    }

    private List<Float> getCryptoPrices(List<CryptoData> cryptoDataList, String crypto) {
        return cryptoDataList.stream().filter(cryptoData -> cryptoData.getSymbol().equals(crypto)).map(CryptoData::getPrice).collect(Collectors.toList());
    }

    private List<String> getCryptoSymbols(List<CryptoData> cryptoDataList) {
        List<String> supportedCryptos = appConfig.getAllowedCryptos();
        List<String> cryptoSymbols = cryptoDataList.stream().map(CryptoData::getSymbol).distinct().collect(Collectors.toList());

        List<String> notFoundEntries = cryptoSymbols.stream()
                .filter(entry -> !supportedCryptos.contains(entry))
                .collect(Collectors.toList());

        if (!notFoundEntries.isEmpty()) {
            throw new IllegalArgumentException("Cryptos not supported yet: " + notFoundEntries);
        }
        return cryptoSymbols;
    }

    private List<Date> getCryptoTimestamps(List<CryptoData> cryptoDataList, String crypto) {
        return cryptoDataList.stream().filter(cryptoData -> cryptoData.getSymbol().equals(crypto)).map(CryptoData::getTimestamp).collect(Collectors.toList());
    }

    private static CryptoStatistics buildCryptoStatisticsForCrypto(String crypto, List<Date> timestamps, List<Float> prices) {
        Date oldestDate = null, newestDate = null;
        float minPrice = 0, maxPrice = 0, normalizedRange = 0;
        if (!timestamps.isEmpty()) {
            oldestDate = Collections.min(timestamps);
            newestDate = Collections.max(timestamps);
        }
        if (!prices.isEmpty()) {
            minPrice = Collections.min(prices);
            maxPrice = Collections.max(prices);
            normalizedRange = (maxPrice - minPrice) / minPrice;
        }
        return new CryptoStatistics(crypto, oldestDate, newestDate, minPrice, maxPrice, normalizedRange);
    }

    private List<CryptoStatistics> generateAllCryptoStatistics(List<CryptoData> cryptoDataList, Date day) throws Exception {
        List<String> symbols = getCryptoSymbols(cryptoDataList);
        List<CryptoStatistics> cryptoStatisticsList = new ArrayList<>();
        for (String symbol : symbols) {
            cryptoStatisticsList.add(generateStatisticsForCrypto(cryptoDataList, symbol, day));
        }
        return cryptoStatisticsList;
    }

    private CryptoStatistics generateStatisticsForCrypto(List<CryptoData> cryptoDataList, String crypto, Date day) throws Exception {
        List<Date> timestamps = getCryptoTimestamps(cryptoDataList, crypto);
        List<Float> prices = getCryptoPrices(cryptoDataList, crypto);
        if (day != null) {
            timestamps = timestamps.stream().filter(timestamp -> timestamp.equals(day)).collect(Collectors.toList());
        }
        return buildCryptoStatisticsForCrypto(crypto, timestamps, prices);
    }

}
